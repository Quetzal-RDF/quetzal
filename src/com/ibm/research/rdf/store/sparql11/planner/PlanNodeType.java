package com.ibm.research.rdf.store.sparql11.planner;

public enum PlanNodeType
   {
   MATERIALIZED_TABLE, EMPTY, TRIPLE, AND, UNION, LEFT, SUBSELECT, MINUS, EXISTS, NOT_EXISTS, STAR, REUSE, VALUES, PARTITION, GRAPH, JOIN, PRODUCT;
   }
