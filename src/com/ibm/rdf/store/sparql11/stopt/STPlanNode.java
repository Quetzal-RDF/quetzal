package com.ibm.rdf.store.sparql11.stopt;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ibm.rdf.store.runtime.service.types.TypeMap;
import com.ibm.rdf.store.schema.Pair;
import com.ibm.rdf.store.sparql11.model.BinaryUnion;
import com.ibm.rdf.store.sparql11.model.BindPattern;
import com.ibm.rdf.store.sparql11.model.ConstantExpression;
import com.ibm.rdf.store.sparql11.model.Expression;
import com.ibm.rdf.store.sparql11.model.Expression.ERelationalOp;
import com.ibm.rdf.store.sparql11.model.IRI;
import com.ibm.rdf.store.sparql11.model.Pattern;
import com.ibm.rdf.store.sparql11.model.ProjectedVariable;
import com.ibm.rdf.store.sparql11.model.QueryTriple;
import com.ibm.rdf.store.sparql11.model.RelationalExpression;
import com.ibm.rdf.store.sparql11.model.SubSelectPattern;
import com.ibm.rdf.store.sparql11.model.Values;
import com.ibm.rdf.store.sparql11.model.Variable;
import com.ibm.rdf.store.sparql11.model.VariableExpression;
import com.ibm.wala.util.collections.HashMapFactory;
import com.ibm.wala.util.collections.HashSetFactory;

public class STPlanNode
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
   public STEPlanNodeType                                    type;
   
   //
   // If this is a TRIPLE plan node, then the following fields are relevant:
   // - The triple associated with the node
   // - The associated access method and cost
   //
   private QueryTriple                                       triple;
   private STAccessMethod                                    method;

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
   private STPlanNode reUseNode;
   
   private Map<Variable, Variable> reuseVarMappings;
   
   private Set<Variable> undefVariables;
   
   
   //
   // The cost of the STPlanNode
   //
   public com.ibm.wala.util.collections.Pair<Double, Double> cost;
   
   
   
   // Members for STEPlanNodeType.MATERIALIZED_TABLE 
   
   private STEPlanNodeType originalType;
   private String materialzedTable;
   
   // Members for VALUES statements
   private Values values;
   
   
   // Member for graph access
   private BinaryUnion<Variable, IRI> graphRestriction;
   
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

public void setType(STEPlanNodeType type) {
	this.type = type;
}

public void setTriple(QueryTriple triple) {
	this.triple = triple;
}

public void setMethod(STAccessMethod method) {
	this.method = method;
}

public void setPattern(Pattern pattern) {
	this.pattern = pattern;
}

public void setBindPatterns(List<BindPattern> bindPatterns) {
	this.bindPatterns = bindPatterns;
}

public void setReUseNode(STPlanNode reUseNode) {
	this.reUseNode = reUseNode;
}

public void setReuseVarMappings(Map<Variable, Variable> reuseVarMappings) {
	this.reuseVarMappings = reuseVarMappings;
}

public void setCost(com.ibm.wala.util.collections.Pair<Double, Double> cost) {
	this.cost = cost;
}

public void setOriginalType(STEPlanNodeType originalType) {
	this.originalType = originalType;
}

public void setMaterialzedTable(String materialzedTable) {
	this.materialzedTable = materialzedTable;
}

//
   // This constructor creates a new STPlanNode for an input triple and a
   // specified access method (+plus its cost)
   //
   public STPlanNode(QueryTriple triple, STAccessMethod method, Pair<Set<Variable>> variablePair, Pattern pattern)
      {
      this.type = STEPlanNodeType.TRIPLE;
      this.triple = triple;
      this.method = method;
      this.pattern = pattern;
      this.producedVariables = new HashSet<Variable>(variablePair.getFirst());
      this.requiredVariables = new HashSet<Variable>(variablePair.getSecond());
      this.operatorVariables = null;
      }
   
   public STPlanNode(STEPlanNodeType type, Set<Variable> operatorVariables, Pattern pattern)
      {
      this.type = type;
      this.requiredVariables = new HashSet<Variable>();
      this.producedVariables = new HashSet<Variable>();
      this.operatorVariables = new HashSet<Variable>(operatorVariables);
      this.pattern = pattern;
      }
   
   public STPlanNode(STPlanNode reUseNode, Map<Variable, Variable> reuseVarMappings) {
	   this.type = STEPlanNodeType.REUSE;
	   this.reUseNode = reUseNode;
	   this.reuseVarMappings = reuseVarMappings;
   }
   
   public STPlanNode(String  materialzedTable, Set<Variable> producedVariables) {
	   this.materialzedTable = materialzedTable;
	   this.type = STEPlanNodeType.MATERIALIZED_TABLE;
	   this.requiredVariables = new HashSet<Variable>();
	   this.producedVariables = new HashSet<Variable>(producedVariables);
   }
   
   public STPlanNode(Values values) {
	   this.values = values;
	   this.producedVariables = new HashSet<Variable>();
	   this.producedVariables.addAll(values.getVariables());
	   this.requiredVariables = Collections.emptySet();
	   this.type = STEPlanNodeType.VALUES;
	   this.undefVariables = values.determineUNDEFVariables();
   }
   
   
   
   public STPlanNode(BinaryUnion<Variable, IRI> graphRestriction) {
	super();
	this.graphRestriction = graphRestriction;
	type= STEPlanNodeType.GRAPH;
   }
   
   
   
   public STPlanNode clone() {
	   STPlanNode ret;
	   STEPlanNodeType origType;
	   if (type.equals(STEPlanNodeType.MATERIALIZED_TABLE)) {
		   origType = originalType;
	   } else {
		   origType = type;
	   }
	   if (origType!=null) {
		   switch (origType) {
				case TRIPLE: 
					ret = new STPlanNode(triple, method, new Pair(producedVariables, requiredVariables), pattern);
					break;
				case REUSE:
					ret = new STPlanNode(reUseNode, reuseVarMappings);
					break;
				case GRAPH:
					ret = new STPlanNode(graphRestriction);
					break;
				default:
					ret = new STPlanNode(origType, operatorVariables, pattern);
					
					break;
		   }
	   } else {
		   ret = new STPlanNode(materialzedTable, producedVariables);
	   }
	   ret.requiredVariables = new HashSet<Variable>(requiredVariables);
	   ret.producedVariables = new HashSet<Variable>(producedVariables);
	   if (availableVariables!=null) {
		   ret.availableVariables =  new HashSet<Variable>(availableVariables);
	   } 
	   if (operatorVariables!=null) {
		   ret.operatorVariables = new HashSet<Variable>(operatorVariables);
	   }
	   if  (type.equals(STEPlanNodeType.MATERIALIZED_TABLE)
			 && origType!=null) {
		   ret.materialize(getMaterialzedTable());
	   }
	   if (bindPatterns!=null) {
		   ret.bindPatterns = new LinkedList<BindPattern>(bindPatterns);
	   }
	   return ret;
   }
   
   
   public STPlanNode getReUseNode() {
	   return reUseNode;
   }

   public Map<Variable, Variable> getReuseVarMappings() {
	   return reuseVarMappings;
   }
   
   //
   // Return the access method for the node
   //
   public STAccessMethod getMethod()
      {
      switch (this.type)
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
      switch (this.type)
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
      if (getType().equals(STEPlanNodeType.TRIPLE)) {
    	  returnString.append("<STPlanNode>triple: " + triple + " with " + method + "\n");
      } else if (getType().equals(STEPlanNodeType.STAR)) {
    	  for(QueryTriple t : starTriples) {
        	  returnString.append("<STPlanNode>triple: " + t + "\n");    		  
    	  }
    	  if (starOptionalTriples != null) {
    	   	  for(QueryTriple t : starOptionalTriples) {
            	  returnString.append("<STPlanNode>triple: " + t + "\n");    		  
        	  }   		  
    	  }
      } else if (getType().equals(STEPlanNodeType.MATERIALIZED_TABLE)) {
    	  returnString.append("<STPlanNode> Materialized table original type: " +originalType+"\n");
    	  returnString.append("<STPlanNode> Materialized table name: " +materialzedTable+"\n");
    	  returnString.append("[");
    	  if (originalType != null && originalType.equals(STEPlanNodeType.TRIPLE)) {
        	  returnString.append("<STPlanNode>triple: " + triple + " with " + method + "\n");
          } else if (originalType != null && originalType.equals(STEPlanNodeType.STAR)) {
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
     	 
      } else if (getType().equals(STEPlanNodeType.VALUES)) {
    	  System.out.println("Undef variables:" + undefVariables);
      } 
      
      if (!typeCheckVariables.isEmpty()) {
    	  System.out.println("Type check variables:" + typeCheckVariables);
      }
      
      
      return returnString.toString();
      }
   
   public STEPlanNodeType getType()
      {
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
	   return type == STEPlanNodeType.EXISTS || type == STEPlanNodeType.NOT_EXISTS;
   }
   
   public List<Expression> getApplicableFilters(STPlan p) {
	   return getApplicableFilters(p, getAvailableVariables());
   }

   public List<Expression> getApplicableFilters(STPlan p, Set<Variable> availableVariables)
   {
	   if (getFilters() == null || getFilters().isEmpty()) {
		   return Collections.emptyList();
	   }
	   List<Expression> processedFilters = new LinkedList<Expression>(); 
	   List<Expression> allFilters= getFilters();
	   Set<Expression> builtInExists = Pattern.getFiltersWithBuiltInExistsExpressions(allFilters);
	   
	   STPlanNode predecessor = getPredecessor(p);
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

   public STPlanNode getTempTableNode(STPlan p) {
	   switch (getType()) {
	   case AND: {
		   STPlanNode c = null;
		   Iterator<STPlanNode> ss = p.getPlanTree().getSuccNodes(this);
		   while (ss.hasNext()) {
			   c = ss.next();
		   }
		   return c.getTempTableNode(p);
	   }
	   
	   default:
		   return this;
	   }
   }
   
   public STPlanNode getPredecessor(STPlan p) {
	   STPlanNode x = getPredecessorUp(p);
	   return x==null? null: x.getPredecessorDown(p);
   }
   
   public STPlanNode getPredecessorDown(STPlan p) {
	   switch (getType()) {
	   case AND: {
		   STPlanNode c = null;
		   Iterator<STPlanNode> ss = p.getPlanTree().getSuccNodes(this);
		   while (ss.hasNext()) {
			   c = ss.next();
		   }
		   return c.getPredecessorDown(p);
	   }

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
	   case VALUES:{
		   return this;
	   }
	  

	   default: {
		   assert false : "unexpected child " + this;
	   	   return null;
	   }

	   }
   }

   public STPlanNode getPredecessorUp(STPlan p) {
	   Iterator<STPlanNode> parents = p.getPlanTree().getPredNodes(this);
	   if (! parents.hasNext()) {
		   // root of tree
		   return null;
	   } else {
		   STPlanNode parent = parents.next();
		   assert ! parents.hasNext(); // it ought to be a tree
		   switch (parent.getType()) {
			case MINUS:
			case AND:
			case NOT_EXISTS:
			case EXISTS:
			case LEFT: {
				int i = 0;
				Iterator<STPlanNode> ss= p.getPlanTree().getSuccNodes(parent);
				while (ss.next() != this) { i++; }
				if (i == 0) {
					return parent.getPredecessorUp(p);
				} else {
					STPlanNode pred = null;
					int j = 0;
					ss = p.getPlanTree().getSuccNodes(parent);
					while (j++ < i) { pred = ss.next(); }
					return pred;
				}
			}
			
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

   public void setAccessMethod(STAccessMethod stAccessMethod) {
	   this.method = stAccessMethod;
   }
   
   public void setFilters(List<Expression> filters) {
	   this.filters = filters;
   }

    public void materialize(String tableName) {
    	if (type.equals(STEPlanNodeType.MATERIALIZED_TABLE)) {
    		throw new RuntimeException("STPLanNode has already been materialized:\n"+this);
    	}
    	originalType = type;
    	type=STEPlanNodeType.MATERIALIZED_TABLE;
    	materialzedTable = tableName;
    }
	public STEPlanNodeType getOriginalType() {
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
		if (type == STEPlanNodeType.VALUES) {
			for (List<Expression> l : values.getExpressions()) {
				exps.addAll((Collection<? extends Expression>) l);
			}
		} else if (type == STEPlanNodeType.SUBSELECT) {
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
			if (type != STEPlanNodeType.TRIPLE) {
				putIntoTypeCheck(v, e);
				return;
			}
			if (v == getTriple().getObject().getVariable()) {
					// Optimization possible: We may not need a type check if all the predicates have no mixed types
					// but not sure this handles special types anyway
					// if object is not being constrained by the expression due to some filter, we are good to ignore a case test
					checkNeedsCase(v, e);
			}
		} 
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
