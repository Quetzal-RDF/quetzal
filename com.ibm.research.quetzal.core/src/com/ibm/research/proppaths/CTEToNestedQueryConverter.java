/******************************************************************************
 * Copyright (c) 2015 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *****************************************************************************/
 package com.ibm.research.proppaths;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.AllComparisonExpression;
import net.sf.jsqlparser.expression.AnalyticExpression;
import net.sf.jsqlparser.expression.AnyComparisonExpression;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.CaseExpression;
import net.sf.jsqlparser.expression.CastExpression;
import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.ExtractExpression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.IntervalExpression;
import net.sf.jsqlparser.expression.JdbcNamedParameter;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.OracleHierarchicalExpression;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.SignedExpression;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.TimeValue;
import net.sf.jsqlparser.expression.TimestampValue;
import net.sf.jsqlparser.expression.WhenClause;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseAnd;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseOr;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseXor;
import net.sf.jsqlparser.expression.operators.arithmetic.Concat;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.expression.operators.arithmetic.Modulo;
import net.sf.jsqlparser.expression.operators.arithmetic.Multiplication;
import net.sf.jsqlparser.expression.operators.arithmetic.Subtraction;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.Between;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExistsExpression;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.IsNullExpression;
import net.sf.jsqlparser.expression.operators.relational.LikeExpression;
import net.sf.jsqlparser.expression.operators.relational.Matches;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;
import net.sf.jsqlparser.expression.operators.relational.RegExpMatchOperator;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.FromItemVisitor;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.LateralSubSelect;
import net.sf.jsqlparser.statement.select.Limit;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.select.SetOperation;
import net.sf.jsqlparser.statement.select.SetOperationList;
import net.sf.jsqlparser.statement.select.SubJoin;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.select.ValuesList;
import net.sf.jsqlparser.statement.select.WithItem;

import com.ibm.research.rdf.store.Store;
import com.ibm.wala.util.collections.HashMapFactory;
import com.ibm.wala.util.collections.HashSetFactory;
import com.ibm.wala.util.collections.Pair;

/**
 * transforms a SQL CTE query into a nested query without CTEs.
 * @author fokoue
 *
 */
public class CTEToNestedQueryConverter {

	private boolean useASInTableAlias = true;
	private boolean useExplicitJoinSyntax = false;
	private boolean transformEXCEPT = false;
	private Store.Backend backend;
	private static Store.Backend getBackend(String backend) {
		Store.Backend ret = backend.equalsIgnoreCase(Store.Backend.db2.name())? Store.Backend.db2:
			backend.equalsIgnoreCase(Store.Backend.postgresql.name())? Store.Backend.postgresql:
			backend.equalsIgnoreCase(Store.Backend.shark.name())? Store.Backend.shark: null;
		if (ret==null) {
			throw new RuntimeException("Unsupported backend: "+backend);
		}
		return ret;
	}
	public CTEToNestedQueryConverter(String backend) {
		this(getBackend(backend));
	}
	public CTEToNestedQueryConverter(Store.Backend backend) {
		this.backend = backend;
		this.useASInTableAlias = !backend.equals(Store.Backend.shark);
		this.useExplicitJoinSyntax = backend.equals(Store.Backend.shark);
		this.transformEXCEPT = backend.equals(Store.Backend.shark);
		
	}
	
	public static class SplitOnAndWhereExpression implements ExpressionVisitor {
		private Table leftTable;
		private Table rightTable;
		private Expression onExp = null;
		private Expression whereExp = null;
		private boolean leftTableFound = false;
		private boolean rightTableFound = false;
		private boolean whereOnlyExpFound = false;
		//private boolean tmpLeftTableFound = false;
		//private boolean tmpRightTableFound = false;
		//private boolean tmpWhereOnlyExpFound = false;
		private boolean isTopLevel = true;
		public SplitOnAndWhereExpression(Table leftTable, Table rightTable) {
			super();
			this.leftTable = leftTable;
			this.rightTable = rightTable;
			
		}
		
		private void clear() {
			leftTableFound = false;
			rightTableFound = false;
			whereOnlyExpFound = false;
		}
		/*private void clearTmp() {
			tmpLeftTableFound = false;
			tmpRightTableFound = false;
			tmpWhereOnlyExpFound = false;
		}
		private void updateTmp(boolean left, boolean right, boolean where) {
			if (left) {
				tmpLeftTableFound |= leftTableFound;
			}
			if (right) {
				tmpRightTableFound |= rightTableFound;
			}
			if (where) {
				tmpWhereOnlyExpFound |= whereOnlyExpFound;
			}
		}
		
		private void updateTmp() {
			updateTmp(true, true, true);
		}
		private void update() {
			leftTableFound = tmpLeftTableFound;
			rightTableFound = tmpRightTableFound;
			whereOnlyExpFound = tmpWhereOnlyExpFound;
		}*/
		

		public Expression getWhereExp() {
			return whereExp;
		}

	

		public Expression getOnExp() {
			return onExp;
		}


		private void defaultTopLevelProcessing(Expression exp) {
			if (isTopLevel) {
				isTopLevel = false;
				if (whereOnlyExpFound) {
					whereExp = exp;
					onExp = null;
				} else {
					whereExp = null;
					onExp = exp;
				}
			}
		}


		@Override
		public void visit(NullValue nullValue) {
			clear();
			defaultTopLevelProcessing(nullValue);
		}
		@Override
		public void visit(Function function) {
			clear();
			boolean tmpLeftTableFound = false;
			boolean tmpRightTableFound = false;
			boolean tmpWhereOnlyExpFound = false;
			boolean prevIsTopVal = isTopLevel;
			isTopLevel = false;
			if (function.getParameters()!=null) {
				for (Expression e: function.getParameters().getExpressions()) {
					e.accept(this);
					tmpLeftTableFound |= leftTableFound;
					tmpRightTableFound |= rightTableFound;
					tmpWhereOnlyExpFound |= whereOnlyExpFound;
				}
			}
			leftTableFound = tmpLeftTableFound;
			rightTableFound = tmpRightTableFound;
			whereOnlyExpFound = tmpWhereOnlyExpFound;
			isTopLevel = prevIsTopVal;
			defaultTopLevelProcessing(function);
		}
		@Override
		public void visit(SignedExpression signedExpression) {
			clear();
			boolean prevIsTopVal = isTopLevel;
			isTopLevel = false;
			signedExpression.getExpression().accept(this);
			isTopLevel = prevIsTopVal;
			defaultTopLevelProcessing(signedExpression);
		}
		@Override
		public void visit(JdbcParameter jdbcParameter) {
			clear();
			defaultTopLevelProcessing(jdbcParameter);
		}
		@Override
		public void visit(JdbcNamedParameter jdbcNamedParameter) {
			clear();
			defaultTopLevelProcessing(jdbcNamedParameter);
		}
		@Override
		public void visit(DoubleValue doubleValue) {
			clear();
			defaultTopLevelProcessing(doubleValue);
		}
		@Override
		public void visit(LongValue longValue) {
			clear();
			defaultTopLevelProcessing(longValue);
		}
		@Override
		public void visit(DateValue dateValue) {
			clear();
			defaultTopLevelProcessing(dateValue);
		}
		@Override
		public void visit(TimeValue timeValue) {
			clear();
			defaultTopLevelProcessing(timeValue);
		}
		@Override
		public void visit(TimestampValue timestampValue) {
			clear();
			defaultTopLevelProcessing(timestampValue);
		}
		@Override
		public void visit(Parenthesis parenthesis) {
			/*clear();
			boolean prevIsTopVal = isTopLevel;
			isTopLevel = false;*/
			if (!parenthesis.isNot()) {
				parenthesis.getExpression().accept(this);
			} else {
				clear();
				boolean prevIsTopVal = isTopLevel;
				boolean tmpLeftTableFound = false;
				boolean tmpRightTableFound = false;
				isTopLevel = false;
				parenthesis.getExpression().accept(this);
				// update tmp
				tmpLeftTableFound |= leftTableFound;
				tmpRightTableFound |= rightTableFound;
				//
				
				
				// update 
				leftTableFound = tmpLeftTableFound;
				rightTableFound = tmpRightTableFound;
				whereOnlyExpFound = true;
				//
				
				isTopLevel = prevIsTopVal;
				defaultTopLevelProcessing(parenthesis);
			}
			/*isTopLevel = prevIsTopVal;
			defaultTopLevelProcessing(parenthesis);*/
		}
		@Override
		public void visit(StringValue stringValue) {
			clear();
			defaultTopLevelProcessing(stringValue);
		}
		public void visitBinary(BinaryExpression exp, boolean allowLeftAndRightTable) {
			clear();
			boolean prevIsTopVal = isTopLevel;
			boolean tmpLeftTableFound = false;
			boolean tmpRightTableFound = false;
			boolean tmpWhereOnlyExpFound = false;
			isTopLevel = false;
			exp.getLeftExpression().accept(this);
			// update tmp
			tmpLeftTableFound |= leftTableFound;
			tmpRightTableFound |= rightTableFound;
			tmpWhereOnlyExpFound |= whereOnlyExpFound;
			//
			exp.getRightExpression().accept(this);
			//update tmp
			tmpLeftTableFound |= leftTableFound;
			tmpRightTableFound |= rightTableFound;
			tmpWhereOnlyExpFound |= whereOnlyExpFound;
			//
			
			// update 
			leftTableFound = tmpLeftTableFound;
			rightTableFound = tmpRightTableFound;
			whereOnlyExpFound = tmpWhereOnlyExpFound;
			//
			if (!allowLeftAndRightTable && leftTableFound && rightTableFound) {
				whereOnlyExpFound = true;
			}
			isTopLevel = prevIsTopVal;
			defaultTopLevelProcessing(exp);
		}
		@Override
		public void visit(Addition addition) {
			visitBinary(addition, false);
		}
		@Override
		public void visit(Division division) {
			visitBinary(division, false);
		}
		@Override
		public void visit(Multiplication multiplication) {
			visitBinary(multiplication, false);
		}
		@Override
		public void visit(Subtraction subtraction) {
			visitBinary(subtraction, false);
			
		}
		
		protected List<Expression> flatten(AndExpression and) {
			List<Expression> ret = new LinkedList<Expression>();
			if (and.getLeftExpression() instanceof AndExpression)  {
				ret.addAll(flatten((AndExpression) and.getLeftExpression()));
			} else {
				ret.add(and.getLeftExpression());
			}
			if (and.getRightExpression() instanceof AndExpression) {
				ret.addAll(flatten((AndExpression) and.getRightExpression())); 
			} else {
				ret.add(and.getRightExpression());
			}
			return ret;
		}
		@Override
		public void visit(AndExpression andExpression) {
			clear();
			boolean prevIsTopVal = isTopLevel;
			boolean tmpLeftTableFound = false;
			boolean tmpRightTableFound = false;
			boolean tmpWhereOnlyExpFound = false;
			isTopLevel = false;
			for (Expression exp: flatten(andExpression)) {
				exp.accept(this);
				if (prevIsTopVal) {
					if (whereOnlyExpFound) {
						whereExp = whereExp == null? exp: new AndExpression(whereExp, exp);
						
					} else {
						onExp = onExp==null? exp: new AndExpression(onExp, exp);
					}
				}
				// update tmp
				tmpLeftTableFound |= leftTableFound;
				tmpRightTableFound |= rightTableFound;
				tmpWhereOnlyExpFound |= whereOnlyExpFound;
				//
			}
			
			// update 
			leftTableFound = tmpLeftTableFound;
			rightTableFound = tmpRightTableFound;
			whereOnlyExpFound = tmpWhereOnlyExpFound;
			//
			
		}
		@Override
		public void visit(OrExpression exp) {
			//visitBinary(orExpression, true);
			clear();
			boolean prevIsTopVal = isTopLevel;
			boolean tmpLeftTableFound = false;
			boolean tmpRightTableFound = false;
			isTopLevel = false;
			exp.getLeftExpression().accept(this);
			// update tmp
			tmpLeftTableFound |= leftTableFound;
			tmpRightTableFound |= rightTableFound;
			//
			exp.getRightExpression().accept(this);
			//update tmp
			tmpLeftTableFound |= leftTableFound;
			tmpRightTableFound |= rightTableFound;
			//
			
			// update 
			leftTableFound = tmpLeftTableFound;
			rightTableFound = tmpRightTableFound;
			whereOnlyExpFound = true;
			//
			
			isTopLevel = prevIsTopVal;
			defaultTopLevelProcessing(exp);
			
			
		}
		@Override
		public void visit(Between between) {
			clear();
			boolean prevIsTopVal = isTopLevel;
			boolean tmpLeftTableFound = false;
			boolean tmpRightTableFound = false;
			boolean tmpWhereOnlyExpFound = false;
			isTopLevel = false;
			between.getLeftExpression().accept(this);
			// update tmp
			tmpLeftTableFound |= leftTableFound;
			tmpRightTableFound |= rightTableFound;
			tmpWhereOnlyExpFound |= whereOnlyExpFound;
			//
			between.getBetweenExpressionStart().accept(this);
			// update tmp
			tmpLeftTableFound |= leftTableFound;
			tmpRightTableFound |= rightTableFound;
			tmpWhereOnlyExpFound |= whereOnlyExpFound;
			//
			clear();
			between.getBetweenExpressionStart().accept(this);
			// update tmp
			tmpLeftTableFound |= leftTableFound;
			tmpRightTableFound |= rightTableFound;
			tmpWhereOnlyExpFound |= whereOnlyExpFound;
			//			
			// update 
			leftTableFound = tmpLeftTableFound;
			rightTableFound = tmpRightTableFound;
			whereOnlyExpFound = tmpWhereOnlyExpFound;
			//
			isTopLevel = prevIsTopVal;
			if (leftTableFound && rightTableFound) {
				whereOnlyExpFound = true;
			}
			defaultTopLevelProcessing(between);
			
		}
		@Override
		public void visit(EqualsTo equalsTo) {
			visitBinary(equalsTo, true);
			
		}
		@Override
		public void visit(GreaterThan greaterThan) {
			visitBinary(greaterThan, false);
			
			
		}
		@Override
		public void visit(GreaterThanEquals greaterThanEquals) {
			visitBinary(greaterThanEquals, false);
			
			
		}
		@Override
		public void visit(InExpression inExpression) {
			defaultVisit(inExpression);
			
		}
		@Override
		public void visit(IsNullExpression isNullExpression) {
			clear();
			boolean prevIsTopLevel = isTopLevel;
			isTopLevel = false;
			isNullExpression.getLeftExpression().accept(this);
			if (leftTableFound && rightTableFound) {
				whereOnlyExpFound = true;
			}
			isTopLevel = prevIsTopLevel;
			defaultTopLevelProcessing(isNullExpression);
			
		}
		@Override
		public void visit(LikeExpression likeExpression) {
			visitBinary(likeExpression, false);
		}
		@Override
		public void visit(MinorThan minorThan) {
			
			visitBinary(minorThan, false);
		}
		@Override
		public void visit(MinorThanEquals minorThanEquals) {
			visitBinary(minorThanEquals, false);
		}
		@Override
		public void visit(NotEqualsTo notEqualsTo) {
			visitBinary(notEqualsTo, false);
		}
		@Override
		public void visit(Column tableColumn) {
			clear();
			boolean prevIsTopLevel = isTopLevel;
			isTopLevel = false;
			if (tableColumn.getTable()!=null && tableColumn.getTable().getName()!=null) {
				if (tableColumn.getTable().getName().equalsIgnoreCase(leftTable.getAlias()!=null? leftTable.getAlias().getName(): leftTable.getName())) {
					leftTableFound = true;
				}
				if (tableColumn.getTable().getName().equalsIgnoreCase(rightTable.getAlias()!=null? rightTable.getAlias().getName(): rightTable.getName())) {
					rightTableFound = true;
				}
			}
			isTopLevel = prevIsTopLevel;
			defaultTopLevelProcessing(tableColumn);
		}
		@Override
		public void visit(SubSelect subSelect) {
			defaultVisit(subSelect);
		}
		@Override
		public void visit(CaseExpression caseExpression) {
			defaultVisit(caseExpression);
		}
		@Override
		public void visit(WhenClause whenClause) {
			defaultVisit(whenClause);
		}
		@Override
		public void visit(ExistsExpression existsExpression) {
			defaultVisit(existsExpression);
			
		}
		@Override
		public void visit(AllComparisonExpression allComparisonExpression) {
			defaultVisit(allComparisonExpression);
		}
		@Override
		public void visit(AnyComparisonExpression anyComparisonExpression) {
			defaultVisit(anyComparisonExpression);
		}
		@Override
		public void visit(Concat concat) {
			defaultVisit(concat);
		}
		@Override
		public void visit(Matches matches) {
			defaultVisit(matches);
		}
		@Override
		public void visit(BitwiseAnd bitwiseAnd) {
			defaultVisit(bitwiseAnd);
		}
		@Override
		public void visit(BitwiseOr bitwiseOr) {
			defaultVisit(bitwiseOr);
		}
		@Override
		public void visit(BitwiseXor bitwiseXor) {
			defaultVisit(bitwiseXor);
			
		}
		
		public void defaultVisit(Expression exp) {
			clear();
			whereOnlyExpFound = true;
			if (isTopLevel) {
				whereExp = exp;
				onExp = null;
			}
			
		}
		@Override
		public void visit(CastExpression cast) {
			defaultVisit(cast);
			
		}
		@Override
		public void visit(Modulo modulo) {
			defaultVisit(modulo);
			
		}
		@Override
		public void visit(AnalyticExpression aexpr) {
			defaultVisit(aexpr);
		}
		@Override
		public void visit(ExtractExpression eexpr) {
			defaultVisit(eexpr);
			
		}
		@Override
		public void visit(IntervalExpression iexpr) {
			defaultTopLevelProcessing(iexpr);
			
		}
		@Override
		public void visit(OracleHierarchicalExpression oexpr) {
			defaultVisit(oexpr);
			
		}
		@Override
		public void visit(RegExpMatchOperator rexpr) {
			defaultVisit(rexpr);
		}
		
	}
	/**
	 * Change toString method so that operand are not placed in parenthesis (something that hive ql parser cannot handle)
	 * @author fokoue
	 *
	 */
	public static class SetOperationListNoParenthesisWrapper extends SetOperationList {
		protected SetOperationList setOp;

		
		public SetOperationListNoParenthesisWrapper(SetOperationList setOp) {
			super();
			this.setOp = setOp;
		}

		public void accept(SelectVisitor selectVisitor) {
			setOp.accept(selectVisitor);
		}

		public List<OrderByElement> getOrderByElements() {
			return setOp.getOrderByElements();
		}

		public List<PlainSelect> getPlainSelects() {
			return setOp.getPlainSelects();
		}

		public List<SetOperation> getOperations() {
			return setOp.getOperations();
		}

		public void setOrderByElements(List<OrderByElement> orderByElements) {
			setOp.setOrderByElements(orderByElements);
			
		}

		public void setOpsAndSelects(List<PlainSelect> select,
				List<SetOperation> ops) {
			setOp.setOpsAndSelects(select, ops);
		}

		public Limit getLimit() {
			return setOp.getLimit();
		}

		public void setLimit(Limit limit) {
			setOp.setLimit(limit);
		}

		@Override
		public String toString() {
			StringBuilder buf = new StringBuilder();

			for (int i = 0; i < getPlainSelects().size(); i++) {
				if (i != 0) {
					buf.append(" ")
						.append(getOperations().get(i - 1).toString())
						.append(" ");
				}
				buf.append(getPlainSelects().get(i).toString());
			}

			if (getOrderByElements() != null) {
				buf.append(PlainSelect.orderByToString(getOrderByElements()));
			}
			if (getLimit() != null) {
				buf.append(getLimit().toString());
			}
			return buf.toString();
		}
		
		
	}
	public  class  ReplacementFromItemVisitor implements FromItemVisitor {
	
		
		protected Map<String, SelectBody> cteName2Def;
		private FromItem result;
		protected Set<String> placeHolderTables;
		
		
		public ReplacementFromItemVisitor(Map<String, SelectBody> cteName2Def,Set<String> placeHolderTables ) {
			super();
			this.cteName2Def = cteName2Def;
			this.placeHolderTables = placeHolderTables;
		}
		public FromItem getResult() {
			return result;
		}
		@Override
		public void visit(ValuesList valuesList) {
			result = valuesList;
			
		}
		
		@Override
		public void visit(LateralSubSelect lateralSubSelect) {
			if (lateralSubSelect.getSubSelect()!=null) {
				lateralSubSelect.getSubSelect().accept(this);
				lateralSubSelect.setSubSelect((SubSelect) result);
			}
			result = lateralSubSelect;
		}
		
		@Override
		public void visit(SubJoin subjoin) {
			if (subjoin.getLeft()!=null) {
				subjoin.getLeft().accept(this);
				subjoin.setLeft(result);
			}
			if (subjoin.getJoin().getRightItem()!=null) {
				FromItem right = subjoin.getJoin().getRightItem();
				right.accept(this);
				subjoin.getJoin().setRightItem(result);
			}
			result = subjoin;
			
		}
		
		@Override
		public void visit(SubSelect subSelect) {
			if (subSelect.getSelectBody()!=null) {
				replace(subSelect.getSelectBody(), cteName2Def, placeHolderTables);
			}
			result = subSelect;
		}
		
		@Override
		public void visit(Table tableName) {
			SelectBody select = cteName2Def.get(tableName.getFullyQualifiedName());
			if (select != null) {
				SubSelect subSelect = new SubSelect();
				subSelect.setSelectBody(select);
				Alias alias;
				if (tableName.getAlias()==null) {
					alias= new Alias(tableName.getFullyQualifiedName(), useASInTableAlias);
				} else {
					alias = tableName.getAlias();
				}
				subSelect.setAlias(alias);
				result = subSelect;
			} else {
				result = tableName;
			}
			
		}
		
	}
	public  class ReplacementSelectVisitor implements SelectVisitor {

		protected Map<String, SelectBody> cteName2Def;
		protected Set<String> placeHolderTables;
		public ReplacementSelectVisitor(Map<String, SelectBody> cteName2Def, Set<String> placeHolderTables) {
			super();
			this.cteName2Def = cteName2Def;
			this.placeHolderTables= placeHolderTables;
		}
		protected int getNumberNonPlaceHolderFromItem(PlainSelect plainSelect) {
			int ret = 0;
			if (useExplicitJoinSyntax) {
				for (Join join: plainSelect.getJoins()) {
					if (join.getRightItem()!=null) {
						if ( (join.getRightItem() instanceof Table)) {
							String tableName = ((Table) join.getRightItem()).getFullyQualifiedName();
							if (!placeHolderTables.contains(tableName)) {
								ret++;
							}
						} else {
							ret++;
						}
					}
				}
			}
			return ret;
		}
		@Override
		public void visit(PlainSelect plainSelect) {
			FromItem fromItem =plainSelect.getFromItem();
			ReplacementFromItemVisitor visitor = new ReplacementFromItemVisitor(cteName2Def,placeHolderTables);
			if (fromItem!=null) {
				fromItem.accept(visitor);
				//if (visitor.getResult()!=fromItem) 
				{
					FromItem res = visitor.getResult();
					if (res instanceof SubSelect) {
						SubSelect sub = (SubSelect) res;
						if (sub.getSelectBody() instanceof SetOperationList) {
							SetOperationListNoParenthesisWrapper setOp = new SetOperationListNoParenthesisWrapper((SetOperationList) sub.getSelectBody());
							sub.setSelectBody(setOp);
						}
						
					}
					if (res.getAlias()!=null) {
						res.getAlias().setUseAs(useASInTableAlias);
					}
					plainSelect.setFromItem(res);
				}
			}
			//order by 
			if (plainSelect.getOrderByElements()!=null) {
				List<OrderByElement> newOrderBy = new LinkedList<OrderByElement>();
				for (OrderByElement oe: plainSelect.getOrderByElements()) {
					if (oe.getExpression() instanceof Parenthesis) {
						OrderByElement noe =new OrderByElement();
						noe.setExpression( ((Parenthesis)oe.getExpression()).getExpression());
						noe.setAsc(oe.isAsc());
						newOrderBy.add(noe);
					} else {
						newOrderBy.add(oe);
					}
				}
				plainSelect.setOrderByElements(newOrderBy);
			}
			if (plainSelect.getJoins()!=null) {
				int nonPlaceHolderTables =getNumberNonPlaceHolderFromItem(plainSelect);
				for (Join join: plainSelect.getJoins()) {
					if (join.getRightItem()!=null) {
						join.getRightItem().accept(visitor);
						//if (visitor.getResult()!=join.getRightItem()) 
						{
							FromItem res = visitor.getResult();
							if (res instanceof SubSelect) {
								SubSelect sub = (SubSelect) res;
								if (sub.getSelectBody() instanceof SetOperationList) {
									SetOperationListNoParenthesisWrapper setOp = new SetOperationListNoParenthesisWrapper((SetOperationList) sub.getSelectBody());
									sub.setSelectBody(setOp);
								}
								
							}
							if (res.getAlias()!=null) {
								res.getAlias().setUseAs(useASInTableAlias);
							}
							join.setRightItem(res);
						}
						if (join.isSimple() && useExplicitJoinSyntax) {
							join.setSimple(false);
							if (nonPlaceHolderTables>2 && plainSelect.getWhere()!=null) {
								throw new RuntimeException("3 way join or more not implemented yet!\n"+plainSelect);
							} else if (plainSelect.getWhere()!=null ) {
								String tableName = null;
								if (join.getRightItem() instanceof Table) {
									tableName = ((Table) join.getRightItem()).getFullyQualifiedName();
								}
								if (tableName == null || !placeHolderTables.contains(tableName)) {
									Table leftTable;
									if (plainSelect.getFromItem() instanceof Table) {
										leftTable = (Table) plainSelect.getFromItem();
									} else {
										assert plainSelect.getFromItem().getAlias()!=null;
										leftTable = new Table(plainSelect.getFromItem().getAlias().getName());
									}
									Table rightTable;
									if (join.getRightItem() instanceof Table) {
										rightTable = (Table) join.getRightItem();
									} else {
										assert join.getRightItem().getAlias()!=null;
										rightTable = new Table(join.getRightItem().getAlias().getName());
									}
									
									SplitOnAndWhereExpression split = new SplitOnAndWhereExpression(leftTable, rightTable);
									plainSelect.getWhere().accept(split);
									join.setOnExpression(split.getOnExp());
									plainSelect.setWhere(split.getWhereExp());
								}
							}
						}
					}
				}
			}
			
		}

		private Expression getExpression(SelectItem item, Table table) {
			SelectExpressionItem rightSelectItem = (SelectExpressionItem) item;
			Expression rightExp = rightSelectItem.getExpression();
			if (rightExp instanceof Column) {
				Column c = (Column) rightExp;
				if (c.getTable() == null) {
					c.setTable(table);
				}
			}
			return rightExp;
		}
		@Override
		public void visit(SetOperationList setOpList) {
			if (transformEXCEPT && setOpList.getOperations().size()>0 && setOpList.getOperations().get(0).toString().equals(SetOperationList.SetOperationType.EXCEPT.name())) {
				assert setOpList.getPlainSelects().size() == 2 : setOpList;
				PlainSelect left = setOpList.getPlainSelects().get(0);
				PlainSelect right = setOpList.getPlainSelects().get(1);
				Join join = new Join();
				join.setLeft(true);
				join.setOuter(true);
				FromItem rightFrom = right.getFromItem();
				assert rightFrom instanceof Table;
				Table  rightTable = (Table) rightFrom;
				join.setRightItem(rightTable);
				FromItem leftFrom = left.getFromItem();
				assert leftFrom instanceof Table;
				Table leftTable = (Table) leftFrom;
				Expression onExp = null;
				Expression whereExp = null;
				for (int i=0;i<Math.min(left.getSelectItems().size(), right.getSelectItems().size());i++) {
					Expression rightExp = getExpression(right.getSelectItems().get(i), rightTable);
					Expression leftExp = getExpression(left.getSelectItems().get(i), leftTable);
					if (!leftExp.toString().equals(rightExp.toString())) {
						EqualsTo leftEqRight = new EqualsTo();
						leftEqRight.setLeftExpression(leftExp);
						leftEqRight.setRightExpression(rightExp);
						if (onExp ==  null) {
							onExp = leftEqRight;
						} else {
							onExp = new AndExpression(onExp, leftEqRight);
						}
					}
					if (!rightExp.toString().equals(new NullValue().toString())) {
						IsNullExpression rightEqNull = new IsNullExpression();
						rightEqNull.setLeftExpression(rightExp);
						rightEqNull.setNot(false);
						if (whereExp == null) {
							whereExp = rightEqNull;
						} else {
							whereExp = new AndExpression(whereExp,rightEqNull);
						}
					}
				}
				join.setOnExpression(onExp);
				if (whereExp!=null) {
					if (left.getWhere()!=null) {
						whereExp = new AndExpression(left.getWhere(), whereExp);
					}
					left.setWhere(whereExp);
				}
				if (left.getJoins() ==null) {
					List<Join> js = new LinkedList<Join>();
					js.add(join);
					left.setJoins(js);
				} else {
					left.getJoins().add(join);
				}
				setOpList.getPlainSelects().remove(1);
				setOpList.getOperations().clear();
				setOpList.accept(this);
				
			} else {
				if (setOpList.getPlainSelects()!=null) {
					for (PlainSelect plainSelect : setOpList.getPlainSelects()) {
						plainSelect.accept(this);
					}
				}
			}
		}

		@Override
		public void visit(WithItem withItem) {
			if (withItem!=null) {
				replace(withItem.getSelectBody(), cteName2Def, placeHolderTables);
			}
		}	
		
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		String sql = "   WITH  QS0 AS (SELECT X AS Z FROM  session.t)"
			+","
			+" QS1 AS (SELECT entity::text AS Z,elem AS Y"
			+" FROM db2inst2.uobm30_RS AS T,QS0"
			+"  WHERE  entity = QS0.Z "
			+" AND   (prop = 'http://semantics.crl.ibm.com/univ-bench-dl.owl#isStudentOf')"
			+" ), "
			+" QS2 AS (SELECT entity::text AS Z,elem AS Y, 10::smallint AS sin "
			+" FROM db2inst2.uobm30_RS AS T,QS0"
			+" WHERE  entity = QS0.Z "
			+" AND   (prop = 'http://semantics.crl.ibm.com/univ-bench-dl.owl#enrollIn')"
			+" )\n"
			+" SELECT * FROM QS2  ";
		if (args.length!=0) {
			sql = args[0];
		}
		CTEToNestedQueryConverter converter = new CTEToNestedQueryConverter(Store.Backend.shark);
		
       /* CCJSqlParserManager pm = new CCJSqlParserManager();
        net.sf.jsqlparser.statement.Statement statement = pm.parse(new StringReader(sql));
        if (statement instanceof Select) {
        	Select select = (Select) statement;
        	List<WithItem> withItems= select.getWithItemsList();
        	SelectBody body = select.getSelectBody();
        	for (WithItem item: withItems) {
        		System.out.println(item.getName()+" = "+item.getSelectBody());
        	}
        	System.out.println("main select = "+body);*/
        	
        	System.out.println("Transformation to nested query:\n"+converter.transform(sql));
       // }
        
	}
	private String addSuffix(String st, String suffix, int numOfTimes) {
		for (int i=0; i<numOfTimes;i++) {
			st = st+suffix;
		}
		return st;
	}
	private static String computeAbsentPrefix(String input, String prefix, String replacementSuffix) {
		if (input.contains(prefix+replacementSuffix)) {
			prefix = prefix+"_";
		} else {
			return prefix;
		}
		int i=0;
		while (input.contains(prefix+i+replacementSuffix)) {
			i++;
		}
		return prefix+i+"_";
		
	}
	private static Pair<String, Map<String, String>> replace(String regex, String input, String replacementPrefix, String replacementSuffix) {
		return replace(Pattern.compile(regex), input, replacementPrefix,replacementSuffix);
	}
	private static Pair<String, Map<String, String>> replace(Pattern pattern, String input, String replacementPrefix, String replacementSuffix) {
		Map<String, String> ret = HashMapFactory.make();
		replacementPrefix = computeAbsentPrefix(input, replacementPrefix,replacementSuffix);
		Matcher m = pattern.matcher(input);
		StringBuffer sb = new StringBuffer();
		int i=0;
		while (m.find()) {
			String repl = Matcher.quoteReplacement(replacementPrefix+i+replacementSuffix);
			String matchedText = input.substring(m.start(), m.end());
			m.appendReplacement(sb, repl);
			ret.put(replacementPrefix+i+replacementSuffix, matchedText);
			i++;
			
		}
		m.appendTail(sb);
		return Pair.make(sb.toString(), ret);
		
	}
	
	public  String transform(String sqlWithCTEs)  {
		Set<String> placeHolderTables = HashSetFactory.make();
		System.err.println("before transformation sql: "+sqlWithCTEs); 
		
		// hack!!! to fix type issue in lubm q9 
		if (backend.equals(Store.Backend.postgresql)) {
			sqlWithCTEs = sqlWithCTEs.replace(",20 AS", ",20::smallint AS");
			sqlWithCTEs = sqlWithCTEs.replace(" 20 AS", " 20::smallint AS");
			sqlWithCTEs = sqlWithCTEs.replace("\n20 AS", "\n20::smallint AS");
		}
		//
		String insideMap= "\\s*\\w+\\s*\\,\\s*\\w+\\s*(\\,\\s*\\w+\\s*\\,\\s*\\w+\\s*)*";
		String insideArray="\\s*\\w+\\s*(\\,\\s*\\w+\\s*)*";
		String insideArrayOfStructCore ="\\s*named_struct\\s*\\(\\s*'\\w+'\\s*,\\s*\\w+\\s*\\,\\s*'\\w+'\\s*,\\s*\\w+\\s*(,\\s*'\\w+'\\s*,\\s*\\w+\\s*)?\\)";
		String insideArrayOfStruct=insideArrayOfStructCore+"(\\,"+insideArrayOfStructCore+")*";
		Pattern  lateralViewPattern= Pattern.compile("lateral\\s+view\\s+explode"
				//+"\\s*\\(\\s*map\\s*\\("+insideMap+"\\s*\\)\\s*\\)\\s*\\w+\\s+AS\\s+\\w+\\s*\\,\\s*\\w+");
				  +"((\\s*\\(\\s*map\\s*\\("+insideMap+"\\s*\\)\\s*\\)\\s*\\w+\\s+(a|A)(s|S)\\s+\\w+\\s*\\,\\s*\\w+)"
				  + "|"
				  + "(\\s*\\(\\s*array\\s*\\("+insideArray+"\\s*\\)\\s*\\)\\s*\\w+\\s+(a|A)(s|S)\\s+\\w+)"
				  + "|"
				  + "(\\s*\\(\\s*array\\s*\\("+insideArrayOfStruct +"\\s*\\)\\s*\\)\\s*\\w+\\s+(a|A)(s|S)\\s+\\w+)"
				  + ")");
		String lateralViewTableReplacementPrefix =", lateralViewTable";
		String replacementPrefix = lateralViewTableReplacementPrefix;
		Pair<String, Map<String, String>> p = replace(lateralViewPattern, sqlWithCTEs, replacementPrefix, "");
		sqlWithCTEs = p.fst;
		System.err.println("sql: "+sqlWithCTEs); 
		Map<String, String> replacement2LateralViewText = p.snd;
		String coreInsidePattern = "\\s*struct\\s*\\(.*\\)\\s*";
		insideArray=coreInsidePattern+"(\\,"+coreInsidePattern +")*";
		lateralViewPattern =  Pattern.compile("lateral\\s+view\\s+explode\\s*\\(\\s*array\\s*\\("+insideArray+"\\s*\\)\\s*\\)\\s*LATTEMP\\s+AS\\s+struct_col");
		p = replace(lateralViewPattern, sqlWithCTEs, replacementPrefix,"");
		sqlWithCTEs = p.fst;
		replacement2LateralViewText.putAll(p.snd);
		System.err.println("sql: "+sqlWithCTEs); 
		for (String repl: replacement2LateralViewText.keySet()) {
			placeHolderTables.add(repl.substring(2));
		}
		Pattern equal = Pattern.compile("\\w+\\.\\w+\\s*\\<\\=\\>\\s*\\s*\\w+\\.\\w+");
		replacementPrefix = "equalTest";
		 p = replace(equal, sqlWithCTEs, replacementPrefix, "()");
		sqlWithCTEs = p.fst;
		replacement2LateralViewText.putAll(p.snd);
		System.err.println("sql: "+sqlWithCTEs); 
		//String dateTimeRegex = Pattern.quote("'^([0-9]{4})-([0-1][0-9])-([0-3][0-9]T[0-2][0-9]:[0-6][0-9]:[0-6][0-9](([0-2][0-9]:[0-6][0-9])|Z)?)$'");
		Pattern dateTimePattern = Pattern.compile("\\s+RLIKE\\s+");//+dateTimeRegex);
		replacementPrefix = "= RLIKE";// = 0 OR 1 = dateTimeRegex";
		p = replace(dateTimePattern, sqlWithCTEs, replacementPrefix,"() OR '10' = ");
		sqlWithCTEs = p.fst;
		replacement2LateralViewText.putAll(p.snd);
		System.err.println("sql: "+sqlWithCTEs); 
		//System.err.println(p.snd);
		/*if (true) {
			return sqlWithCTEs;
		}*/
		try {
			String ret;
			CCJSqlParserManager pm = new CCJSqlParserManager();
			net.sf.jsqlparser.statement.Statement statement = pm.parse(new StringReader(sqlWithCTEs));
			if (statement instanceof Select) {
				Select select = (Select) statement;
				if (select.getWithItemsList()!=null) {
					List<WithItem> withItems= new ArrayList<WithItem>(select.getWithItemsList());
					Map<String, SelectBody> cteName2Def = HashMapFactory.make();
					SelectBody body = select.getSelectBody();
					for (WithItem item: withItems) {
						cteName2Def.put(item.getName(), item.getSelectBody());
					}
					for (int i = 0; i< withItems.size();i++) {
						// Replace all references to ctej in ctei (j<i) 
						SelectBody ctedefi = withItems.get(i).getSelectBody();
				   		replace(ctedefi, cteName2Def, placeHolderTables);
					}
					replace(body, cteName2Def,placeHolderTables);
					ret= body.toString();
				} else {
					Map<String, SelectBody> cteName2Def = HashMapFactory.make();
					replace(select.getSelectBody(), cteName2Def, placeHolderTables);
					ret= select.getSelectBody().toString();//sqlWithCTEs;
				}
				if (select.getSelectBody() instanceof PlainSelect) {
					PlainSelect ps = (PlainSelect) select.getSelectBody();
					if (ps.getLimit()!=null && ps.getLimit().getRowCount() == 0 && ps.getLimit().toString().equals("")) {
						ret += " LIMIT 0";
					}
				}
			} else {
				ret = sqlWithCTEs;
			}
			for (Map.Entry<String, String> e: replacement2LateralViewText.entrySet()) {
				String replacement;
				if (backend.equals(Store.Backend.shark) && e.getKey().startsWith(lateralViewTableReplacementPrefix)) {
					replacement = "JOIN"+e.getKey().substring(1);
				} else {
					replacement = e.getKey();
				}
				//int index = ret.indexOf(replacement);
				//System.out.println("Index : "+ index+" replacement = "+replacement);
				ret = ret.replace(replacement," "+ e.getValue());
			}
			System.err.println("Final transformed sql: "+ret); 
			
			return ret;
		} catch (JSQLParserException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	
	public void replace(SelectBody r, Map<String, SelectBody> cteName2Def,Set<String> placeHolderTables) {
		r.accept(new ReplacementSelectVisitor(cteName2Def, placeHolderTables));
	}
	

}
