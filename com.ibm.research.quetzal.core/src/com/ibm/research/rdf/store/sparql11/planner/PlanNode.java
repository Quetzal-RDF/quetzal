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
 package com.ibm.research.rdf.store.sparql11.planner;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ibm.research.rdf.store.runtime.service.types.TypeMap;
import com.ibm.research.rdf.store.schema.Pair;
import com.ibm.research.rdf.store.sparql11.model.BinaryUnion;
import com.ibm.research.rdf.store.sparql11.model.BindPattern;
import com.ibm.research.rdf.store.sparql11.model.ConstantExpression;
import com.ibm.research.rdf.store.sparql11.model.Expression;
import com.ibm.research.rdf.store.sparql11.model.Expression.ERelationalOp;
import com.ibm.research.rdf.store.sparql11.model.IRI;
import com.ibm.research.rdf.store.sparql11.model.Pattern;
import com.ibm.research.rdf.store.sparql11.model.ProjectedVariable;
import com.ibm.research.rdf.store.sparql11.model.QueryTriple;
import com.ibm.research.rdf.store.sparql11.model.RelationalExpression;
import com.ibm.research.rdf.store.sparql11.model.SubSelectPattern;
import com.ibm.research.rdf.store.sparql11.model.Values;
import com.ibm.research.rdf.store.sparql11.model.Variable;
import com.ibm.research.rdf.store.sparql11.model.VariableExpression;
import com.ibm.research.rdf.store.sparql11.planner.Planner.ServiceNode;
import com.ibm.wala.util.collections.HashMapFactory;
import com.ibm.wala.util.collections.HashSetFactory;

public class PlanNode
   {
   //
   // Each plan node can be one of four types:
   // - TRIPLE: a node representing a triple with an associated access method
   // - AND: a node representing a conjunction of one of more nodes sharing a
   // variable
   // - UNION: a node representing a union of other plan nodes (again sharing a
   // variable)
   // - OPTIONAL: a node representing a graph
   //
   private PlanNodeType                                    type;
   
   //
   // If this is a TRIPLE plan node, then the following fields are relevant:
   // - The triple associated with the node
   // - The associated access method and cost
   //
   private QueryTriple                                       triple;
   private AccessMethod                                    method;

   public Set<QueryTriple>									 starTriples;
   public Set<QueryTriple>									 starOptionalTriples;

   //
   // For convenience, we also provide the pattern for which the node is built
   //
   private Pattern                                           pattern;
   
   //
   // Each plan node has a set of interfaces, namely:
   // - A set of required variables, necessary to evaluate the plan
   // - A set of variables produced, after the plan is evaluated
   private Set<Variable>                                 producedVariables;
   private Set<Variable>                                 requiredVariables;
   private Set<Variable>                                 availableVariables;
   
   //
   // The set of variables on which an AND or UNION node operates
   //
   private Set<Variable>                                 operatorVariables;
   
   private List<Expression> filters;
   
   private List<BindPattern> bindPatterns = new LinkedList<BindPattern>();
   
   // If this node re-uses work from another node already in the plan, the following 2 variables are used to track its state
   private PlanNode reUseNode;
   
   private Map<Variable, Variable> reuseVarMappings;
   
   private Set<Variable> undefVariables;
   
   private boolean post = false;			// flag to indicate if the service is a post or a get
      
   //
   // The cost of the STPlanNode
   //
   public com.ibm.wala.util.collections.Pair<Double, Double> cost;
   
   
   
   // Members for STEPlanNodeType.MATERIALIZED_TABLE 
   
   private PlanNodeType originalType;
   private String materialzedTable;
   
   // Members for VALUES statements
   private Values values;
   
   
   // Member for graph access
   private BinaryUnion<Variable, IRI> graphRestriction;
   
   private boolean isProduct = false;
   
   Map<Variable, TypeCheckInformation> typeCheckVariables = HashMapFactory.make();
   
   public Set<QueryTriple> getStarTriples() {
	  return starTriples;
}

public void setStarTriples(Set<QueryTriple> starTriples) {
	this.starTriples = starTriples;
}

public Set<QueryTriple> getStarOptionalTriples() {
	return starOptionalTriples;
}

public void setStarOptionalTriples(Set<QueryTriple> starOptionalTriples) {
	this.starOptionalTriples = starOptionalTriples;
}

public Set<Variable> getOperatorVariables() {
	return operatorVariables;
}

public void setOperatorVariables(Set<Variable> operatorVariables) {
	this.operatorVariables = operatorVariables;
}

public Values getValues() {
	return values;
}

public void setType(PlanNodeType type) {
	this.type = type;
}

public void setTriple(QueryTriple triple) {
	this.triple = triple;
}

public void setMethod(AccessMethod method) {
	this.method = method;
}

public void setPattern(Pattern pattern) {
	this.pattern = pattern;
}

public void setBindPatterns(List<BindPattern> bindPatterns) {
	this.bindPatterns = bindPatterns;
}

public void setReUseNode(PlanNode reUseNode) {
	this.reUseNode = reUseNode;
}

public void setReuseVarMappings(Map<Variable, Variable> reuseVarMappings) {
	this.reuseVarMappings = reuseVarMappings;
}

public void setCost(com.ibm.wala.util.collections.Pair<Double, Double> cost) {
	this.cost = cost;
}

public void setOriginalType(PlanNodeType originalType) {
	this.originalType = originalType;
}

public void setMaterialzedTable(String materialzedTable) {
	this.materialzedTable = materialzedTable;
}

//
   // This constructor creates a new STPlanNode for an input triple and a
   // specified access method (+plus its cost)
   //
   public PlanNode(QueryTriple triple, AccessMethod method, Pair<Set<Variable>> variablePair, Pattern pattern)
      {
	   if (triple.getPredicate().isComplexPath()) {
		   System.err.println("got here");
	   }
      this.type = PlanNodeType.TRIPLE;
      this.triple = triple;
      this.method = method;
      this.pattern = pattern;
      this.producedVariables = new HashSet<Variable>(variablePair.getFirst());
      this.requiredVariables = new HashSet<Variable>(variablePair.getSecond());
      this.operatorVariables = null;
      }
   
   public PlanNode(PlanNodeType type, Set<Variable> operatorVariables, Pattern pattern)
      {
      this.type = type;
      this.requiredVariables = new HashSet<Variable>();
      this.producedVariables = new HashSet<Variable>();
      this.operatorVariables = new HashSet<Variable>(operatorVariables);
      this.pattern = pattern;
      }
   
   public PlanNode(PlanNode reUseNode, Map<Variable, Variable> reuseVarMappings) {
	   this.type = PlanNodeType.REUSE;
	   this.reUseNode = reUseNode;
	   this.reuseVarMappings = reuseVarMappings;
   }
   
   public PlanNode(String  materialzedTable, Set<Variable> producedVariables) {
	   this.materialzedTable = materialzedTable;
	   this.type = PlanNodeType.MATERIALIZED_TABLE;
	   this.requiredVariables = new HashSet<Variable>();
	   this.producedVariables = new HashSet<Variable>(producedVariables);
   }
   
   public PlanNode(Values values) {
	   this.values = values;
	   this.producedVariables = new HashSet<Variable>();
	   this.producedVariables.addAll(values.getVariables());
	   this.requiredVariables = Collections.emptySet();
	   this.type = PlanNodeType.VALUES;
	   this.undefVariables = values.determineUNDEFVariables();
   }
   
   public PlanNode(ServiceNode service) {
	   this.producedVariables = new HashSet<Variable>();
	   this.producedVariables.addAll(service.getProducedVariables());
	   this.requiredVariables = service.getRequiredVariables();
	   this.pattern = service.p;
	   this.type = PlanNodeType.SERVICE;
   }
   
   public PlanNode(BinaryUnion<Variable, IRI> graphRestriction) {
	super();
	this.graphRestriction = graphRestriction;
	type= PlanNodeType.GRAPH;
   }
   
   
   
   public PlanNode clone() {
	   PlanNode ret;
	   PlanNodeType origType;
	   if (type.equals(PlanNodeType.MATERIALIZED_TABLE)) {
		   origType = originalType;
	   } else {
		   origType = type;
	   }
	   if (origType!=null) {
		   switch (origType) {
				case TRIPLE: 
					ret = new PlanNode(triple, method, new Pair(producedVariables, requiredVariables), pattern);
					break;
				case REUSE:
					ret = new PlanNode(reUseNode, reuseVarMappings);
					break;
				case GRAPH:
					ret = new PlanNode(graphRestriction);
					break;
				default:
					ret = new PlanNode(origType, operatorVariables, pattern);
					
					break;
		   }
	   } else {
		   ret = new PlanNode(materialzedTable, producedVariables);
	   }
	   ret.requiredVariables = new HashSet<Variable>(requiredVariables);
	   ret.producedVariables = new HashSet<Variable>(producedVariables);
	   if (availableVariables!=null) {
		   ret.availableVariables =  new HashSet<Variable>(availableVariables);
	   } 
	   if (operatorVariables!=null) {
		   ret.operatorVariables = new HashSet<Variable>(operatorVariables);
	   }
	   if  (type.equals(PlanNodeType.MATERIALIZED_TABLE)
			 && origType!=null) {
		   ret.materialize(getMaterialzedTable());
	   }
	   if (bindPatterns!=null) {
		   ret.bindPatterns = new LinkedList<BindPattern>(bindPatterns);
	   }
	   return ret;
   }
   
   
   public PlanNode getReUseNode() {
	   return reUseNode;
   }
   
   public void setProductFlag(Plan plan) {
		if (type != PlanNodeType.AND) {
			return;
		}
		List<Expression> allFilters = new LinkedList<Expression>();
		Iterator<PlanNode> succs = plan.getPlanTree().getSuccNodes(this);
		
		PlanNode lhs = null;
		PlanNode rhs = null;
		
		assert succs.hasNext();
		lhs = succs.next();
		assert succs.hasNext();
		rhs = succs.next();
		
		if (lhs.getType() == PlanNodeType.REUSE || rhs.getType() == PlanNodeType.REUSE) {
			return; 
		}
		
		if (rhs.getFilters() != null) {
			allFilters.addAll(rhs.getFilters());
		}
		if (lhs.getFilters() != null) {
			allFilters.addAll(lhs.getFilters());
		}
		
		boolean filtersServeAsAJoin = false;
		for (Expression e : allFilters) {
			// check if ANY of the filters act as a join, meaning its variable intersects with the left's available variables AND the right's produced
			Set<Variable> filterVars = e.gatherVariables();
			boolean left = false;
			boolean right = false;
			for (Variable v : filterVars) {
				if (lhs.getAvailableVariables().contains(v)) {
					left = true;
				}
				if (rhs.getProducedVariables().contains(v)) {
					right = true;
				}
				if (left && right) {
					filtersServeAsAJoin = true;
					break;
				}
			}
		}
		if (operatorVariables.isEmpty() && !filtersServeAsAJoin) {
			isProduct = true;
		} 
   }
   
   public boolean getIsProduct() {
	   return isProduct;
   }

   public Map<Variable, Variable> getReuseVarMappings() {
	   return reuseVarMappings;
   }
   
   //
   // Return the access method for the node
   //
   public AccessMethod getMethod()
      {
      switch (getType())
         {
         case TRIPLE:
         case STAR:
            return method;
         default:
            return null;
         }
      }
   
   //
   // Return the set of required variables
   //
   public Set<Variable> getRequiredVariables()
      {
      return requiredVariables;
      }
   
   //
   // Return the set of produced variables
   //
   public Set<Variable> getProducedVariables()
      {
      return producedVariables;
      }

   //
   // Return the set of available variables
   //
   public Set<Variable> getAvailableVariables()
      {
      return availableVariables;
      }
   

   //
   // add produced variables to the current node
   //
   public void addProducedVariables(Set<Variable> moreProducedVariables)
      {
      this.producedVariables.addAll(moreProducedVariables);
      }
   
   //
   // add produced variables to the current node
   //
   public void addAvailableVariables(Set<Variable> moreAvailableVariables)
      {
      this.availableVariables.addAll(moreAvailableVariables);
      }
   
   //
   // Set the available variables
   //
   public void setAvailableVariables(Set<Variable> rv)
      {
      availableVariables = HashSetFactory.make(rv);
      }
   
   //
   // Set the required variables
   //
   public void setRequiredVariables(Set<Variable> rv)
      {
      requiredVariables = HashSetFactory.make(rv);
      }
 
   //
   // Set the produced variables
   //
   public void setProducedVariables(Set<Variable> pv)
      {
      producedVariables = HashSetFactory.make(pv);
      }
   
   //
   // Set the produced variables
   //
   public void setOperatorsVariables(Set<Variable> ov)
      {
      operatorVariables = HashSetFactory.make(ov);
      }
   
   //
   // get the produced variables
   //
   public Set<Variable> getOperatorsVariables()
      {
      return operatorVariables;
      }
   
   //
   // each plan node has an associated cost, depending on the type of the node:
   // - TRIPLE: the cost of the node is the cost of the triple access method
   // - AND:
   // - UNION:
   // - GRAPH_VAR:
   //
   public Pair<Double> getCost()
      {
      switch (getType())
         {
         case TRIPLE:
            return this.method.getCost();
         case AND:
         case UNION:
         case LEFT:
         }
      return new Pair<Double>((double) 0, (double) 0);
      }
   
   //
   // Output the contents of the object as a string
   //
   public String toString()
      {
      StringBuffer returnString = new StringBuffer();
      returnString.append("<STPlanNode>Required vars: " + ((this.requiredVariables.isEmpty()) ? "None" : this.requiredVariables) + "\n");
      returnString.append("<STPlanNode>Produced vars: " + ((this.producedVariables.isEmpty()) ? "None" : this.producedVariables) + "\n");
      returnString.append("<STPlanNode>" + type.name() + " Node\n");
      returnString.append("<STPlanNode>Access cost: " + this.cost + "\n");
      returnString.append("<STPlanNode>Operator vars: " + ((this.operatorVariables == null || this.operatorVariables.isEmpty()) ? "None" : this.operatorVariables) + "\n");
      returnString.append("<STPlanNode>Required vars: " + ((this.requiredVariables == null || this.requiredVariables.isEmpty()) ? "None" : this.requiredVariables) + "\n");
      returnString.append("<STPlanNode>Produced vars: " + ((this.producedVariables == null || this.producedVariables.isEmpty()) ? "None" : this.producedVariables) + "\n");
      returnString.append("<STPlanNode>Available vars: " + ((availableVariables == null || this.availableVariables.isEmpty()) ? "None" : this.availableVariables) + "\n");
      if (filters != null && !filters.isEmpty()) {
    	  returnString.append("<STPlanNode>applicable filters: " + filters + "\n");
      }
      if (getType().equals(PlanNodeType.TRIPLE)) {
    	  returnString.append("<STPlanNode>triple: " + triple + " with " + method + "\n");
      } else if (getType().equals(PlanNodeType.STAR)) {
    	  for(QueryTriple t : starTriples) {
        	  returnString.append("<STPlanNode>triple: " + t + "\n");    		  
    	  }
    	  if (starOptionalTriples != null) {
    	   	  for(QueryTriple t : starOptionalTriples) {
            	  returnString.append("<STPlanNode>triple: " + t + "\n");    		  
        	  }   		  
    	  }
      } else if (getType().equals(PlanNodeType.MATERIALIZED_TABLE)) {
    	  returnString.append("<STPlanNode> Materialized table original type: " +originalType+"\n");
    	  returnString.append("<STPlanNode> Materialized table name: " +materialzedTable+"\n");
    	  returnString.append("[");
    	  if (originalType != null && originalType.equals(PlanNodeType.TRIPLE)) {
        	  returnString.append("<STPlanNode>triple: " + triple + " with " + method + "\n");
          } else if (originalType != null && originalType.equals(PlanNodeType.STAR)) {
        	  for(QueryTriple t : starTriples) {
            	  returnString.append("<STPlanNode>triple: " + t + "\n");    		  
        	  }
        	  if (starOptionalTriples != null) {
        	   	  for(QueryTriple t : starOptionalTriples) {
                	  returnString.append("<STPlanNode>triple: " + t + "\n");    		  
            	  }   		  
        	  }
          }
    	  returnString.append("]");
     	 
      } else if (getType().equals(PlanNodeType.VALUES)) {
    	  System.out.println("Undef variables:" + undefVariables);
      } 
      
      if (!typeCheckVariables.isEmpty()) {
    	  System.out.println("Type check variables:" + typeCheckVariables);
      }
      
      
      return returnString.toString();
      }
   
   public PlanNodeType getType()
      {
	   if (isProduct) {
		   return PlanNodeType.PRODUCT;
	   }
      return type;
      }
   
   public Pattern getPattern()
      {
      return pattern;
      }
   
   public QueryTriple getTriple()
      {
      return triple;
      }
   
   public BinaryUnion<Variable, IRI> getGraphRestriction()
      {
	   		if (graphRestriction!=null) {
	   			return graphRestriction;
	   		}
	   		return pattern.getGraphRestriction();
      }
   
   public List<Expression> getFilters()
      {
      return filters;
      }
   
   public boolean isExists() {
	   return type == PlanNodeType.EXISTS || type == PlanNodeType.NOT_EXISTS;
   }
   
   public List<Expression> getApplicableFilters(Plan p) {
	   return getApplicableFilters(p, getAvailableVariables());
   }

   public List<Expression> getApplicableFilters(Plan p, Set<Variable> availableVariables)
   {
	   if (getFilters() == null || getFilters().isEmpty()) {
		   return Collections.emptyList();
	   }
	   List<Expression> processedFilters = new LinkedList<Expression>(); 
	   List<Expression> allFilters= getFilters();
	   Set<Expression> builtInExists = Pattern.getFiltersWithBuiltInExistsExpressions(allFilters);
	   
	   PlanNode predecessor = getPredecessor(p);
	   for(Expression f : allFilters){
		   if(! availableVariables.containsAll(f.gatherVariables()))
			   continue;
		   if( predecessor!=null ){
			   Set<Variable> predAvailableVariables = predecessor.getAvailableVariables();
			   List<Expression> predecessorFilters = predecessor.getFilters();
			   if(predecessorFilters !=null && predAvailableVariables != null){
				   if(predecessorFilters.contains(f) && predAvailableVariables.containsAll(f.gatherVariables()))
					   continue;
			   }
		   }
		   if (!isExists() && builtInExists.contains(f)) {
			   continue;
		   }
		   
		   processedFilters.add(f);
	   }
	   return processedFilters;
   }

   public PlanNode getTempTableNode(Plan p) {
	   switch (getType()) {
	   case AND: {
		   PlanNode c = null;
		   Iterator<PlanNode> ss = p.getPlanTree().getSuccNodes(this);
		   while (ss.hasNext()) {
			   c = ss.next();
		   }
		   return c.getTempTableNode(p);
	   }
	   
	   default:
		   return this;
	   }
   }
   
   public PlanNode getPredecessor(Plan p) {
	   PlanNode x = getPredecessorUp(p);
	   System.err.println("*** node: " + x);
	   System.err.println("*** for: " + this);
	   return x==null? null: x.getPredecessorDown(p);
   }
   
   public PlanNode getPredecessorDown(Plan p) {
	   switch (getType()) {
	   case AND: {
		   PlanNode c = null;
		   Iterator<PlanNode> ss = p.getPlanTree().getSuccNodes(this);
		   while (ss.hasNext()) {
			   c = ss.next();
		   }
		   return c.getPredecessorDown(p);
	   }
	   case PRODUCT:
	   case JOIN:
	   case REUSE:
	   case LEFT:
	   case STAR:
	   case MINUS:
	   case NOT_EXISTS:
	   case SUBSELECT:
	   case TRIPLE:
	   case UNION:
	   case MATERIALIZED_TABLE:
	   case GRAPH:
	   case SERVICE:
	   case VALUES:{
		   return this;
	   }
	  

	   default: {
		   assert false : "unexpected child " + this;
	   	   return null;
	   }

	   }
   }

   public PlanNode getPredecessorUp(Plan p) {
	   assert p.getPlanTree().containsNode(this);
	   Iterator<PlanNode> parents = p.getPlanTree().getPredNodes(this);
	   if (! parents.hasNext()) {
		   // root of tree
		   System.err.println("got here");
		   return null;
	   } else {
		   PlanNode parent = parents.next();
		   assert ! parents.hasNext(); // it ought to be a tree
		   switch (parent.getType()) {
			case MINUS:
			case AND:
			case NOT_EXISTS:
			case EXISTS:
			case LEFT: {
				int i = 0;
				Iterator<PlanNode> ss= p.getPlanTree().getSuccNodes(parent);
				while (ss.next() != this) { i++; }
				if (i == 0) {
					return parent.getPredecessorUp(p);
				} else {
					PlanNode pred = null;
					int j = 0;
					ss = p.getPlanTree().getSuccNodes(parent);
					while (j++ < i) { pred = ss.next(); }
					return pred;
				}
			}
			case SERVICE:
			case PRODUCT:
			case JOIN:
			case SUBSELECT:
			case UNION:
			case GRAPH:
			case VALUES:{
				return parent.getPredecessorUp(p);
			}
			case MATERIALIZED_TABLE: {
				return null;
			}

			default: {
				assert false : "unexpected parent " + parent;
				return null;
			}
		   }
	   }
   }

   public List<BindPattern> getBindPatterns() {
	return bindPatterns;
   }

   public void addBindPattern(BindPattern bindPattern) {
	   bindPatterns.add(bindPattern);
   }

   public void setAccessMethod(AccessMethod stAccessMethod) {
	   this.method = stAccessMethod;
   }
   
   public void setFilters(List<Expression> filters) {
	   this.filters = filters;
   }

    public void materialize(String tableName) {
    	if (type.equals(PlanNodeType.MATERIALIZED_TABLE)) {
    		throw new RuntimeException("STPLanNode has already been materialized:\n"+this);
    	}
    	originalType = type;
    	type=PlanNodeType.MATERIALIZED_TABLE;
    	materialzedTable = tableName;
    }
	public PlanNodeType getOriginalType() {
		return originalType;
	}
	
	public String getMaterialzedTable() {
		return materialzedTable;
	}

	public Set<Variable> getUndefVariables() {
		return undefVariables;
	}    
	
	public Set<Expression> gatherAllExpressions() {
		Set<Expression> exps = HashSetFactory.make();
		if (getType() == PlanNodeType.VALUES) {
			for (List<Expression> l : values.getExpressions()) {
				exps.addAll((Collection<? extends Expression>) l);
			}
		} else if (getType() == PlanNodeType.SUBSELECT) {
			for (ProjectedVariable pv : ((SubSelectPattern) pattern).getSelectClause().getProjectedVariables()) {
				if (pv.getExpression() != null) {
					exps.add(pv.getExpression());
					// KAVITHA: Any projected variable actually refers to the same exact expression, so add
					// an equality relational expression for correct type inference
					exps.add(new RelationalExpression(new VariableExpression(pv.getVariable().getName()), pv.getExpression(), ERelationalOp.EQUAL));
				}
			}
		}
		if (bindPatterns != null && !bindPatterns.isEmpty()) {
			for (BindPattern bp : bindPatterns) {
				exps.add(bp.getExpression());
			}
		}
		if (filters != null && !filters.isEmpty()) {
			exps.addAll((Collection<? extends Expression>) filters);
		}
		
		return exps;
	}
	
	public com.ibm.wala.util.collections.Pair<Boolean, Boolean> isTypeCheckVariables(Variable v) {
		if (typeCheckVariables.containsKey(v)) {
			return com.ibm.wala.util.collections.Pair.make(typeCheckVariables.get(v).needsTypeCheck, typeCheckVariables.get(v).needsCase);
		}
		return com.ibm.wala.util.collections.Pair.make(false, false);
	}
	
	public TypeMap.TypeCategory getTypeOfTypeCheckVariable(Variable v) {
		if (typeCheckVariables.containsKey(v)) {
			return typeCheckVariables.get(v).type;
		}
		return null;
	}

	// We need a type check if this node produces the variable, and the variable
	// is in a certain position
	public void needsTypeCheck(Variable v, Expression e) {
		if (producedVariables.contains(v))  {
			if (type != PlanNodeType.TRIPLE && type != PlanNodeType.STAR) {
				putIntoTypeCheck(v, e);
				return;
			}
			if (isVariableAnObject(v)) {
					// Optimization possible: We may not need a type check if all the predicates have no mixed types
					// but not sure this handles special types anyway
					// if object is not being constrained by the expression due to some filter, we are good to ignore a case test
					checkNeedsCase(v, e);
			}
		} 
	}
	
	// Helper method, determines if a variable appears in the object position
	private boolean isVariableAnObject(Variable v) {
		switch(getType()) {
			case TRIPLE :
				return isVariableAnObjectInTriple(v, getTriple());
			case STAR:
				Set<QueryTriple> qt = new HashSet<QueryTriple>();
				qt.addAll(getStarTriples());
				if (starOptionalTriples != null) {
					qt.addAll(getStarOptionalTriples());
				}
				for (QueryTriple t : qt) {
					if (isVariableAnObjectInTriple(v, t)) {
						return true;
					}
				}
		}
		return false;
	}

	private boolean isVariableAnObjectInTriple(Variable v, QueryTriple qt) {
		return qt.getObject().getVariable() == v;
		
	}

	private void checkNeedsCase(Variable v, Expression e) {
		if (e instanceof RelationalExpression) {
			RelationalExpression re = (RelationalExpression) e;
			VariableExpression left = null;
			ConstantExpression right = null;
			if (re.getLeft() instanceof VariableExpression && re.getRight() instanceof ConstantExpression) {
				left = (VariableExpression) re.getLeft();
				right = (ConstantExpression) re.getRight();
			} else if (re.getRight() instanceof VariableExpression && re.getLeft() instanceof ConstantExpression) {
				left = (VariableExpression) re.getRight();
				right = (ConstantExpression) re.getLeft();
			}
			if (left != null && right != null && left.getVariables().contains(v)) {
				typeCheckVariables.put(v, new TypeCheckInformation(v, e.getTypeRestriction(v), true, true));						// overwrite whatever we might have here -- this variable needs the ugly check
			}
		} else {
			putIntoTypeCheck(v, e);
		}

	}

	private void putIntoTypeCheck(Variable v, Expression e) {
		if (!typeCheckVariables.containsKey(v)) {
			typeCheckVariables.put(v, new TypeCheckInformation(v, e.getTypeRestriction(v), true, false));
		}
	}
	
	public boolean isPost() {
		return post;
	}

	public void setPost(boolean post) {
		this.post = post;
	}


	static class TypeCheckInformation {
		private Variable var;
		private TypeMap.TypeCategory type;
		private boolean needsTypeCheck;
		private boolean needsCase;				// if we have a situation where a static check for type is not sufficient,
												// and we need to check dynamically for when we can actually cast
		public Variable getVar() {
			return var;
		}
		public TypeMap.TypeCategory getType() {
			return type;
		}
		public boolean isNeedsTypeCheck() {
			return needsTypeCheck;
		}
		public boolean isNeedsCase() {
			return needsCase;
		}
		public TypeCheckInformation(Variable var, TypeMap.TypeCategory type,
				boolean needsTypeCheck, boolean needsCase) {
			super();
			this.var = var;
			this.type = type;
			this.needsTypeCheck = needsTypeCheck;
			this.needsCase = needsCase;
		}
	}
}
