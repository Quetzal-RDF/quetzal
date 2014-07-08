package com.ibm.rdf.store.sparql11.stopt;

public enum STEPlanNodeType
   {
   MATERIALIZED_TABLE, EMPTY, TRIPLE, AND, UNION, LEFT, SUBSELECT, MINUS, EXISTS, NOT_EXISTS, STAR, REUSE, VALUES, PARTITION, GRAPH, JOIN;
   }
