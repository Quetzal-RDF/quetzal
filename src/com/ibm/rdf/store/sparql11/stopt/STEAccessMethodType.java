package com.ibm.rdf.store.sparql11.stopt;

public enum STEAccessMethodType
   {
   DPH_INDEX_SUBJECT,      // used for triples of the form (const, p, const') or (const, p, ?v) where const is selective
   DPH_SCAN,               // used for triples of the form (?v, p, const') or (?v, p, ?v')
   DPH_INDEX_GRAPH,        //
   DPH_POLL_SUBJECT,       // used for triples of the form (const, p, const') or (const, p, ?v) where const is not selective
   RPH_INDEX_OBJECT,       // same as before where we switch the roles of subject and object
   RPH_SCAN,
   RPH_INDEX_GRAPH,
   DPH_POLL_GRAPH,
   RPH_POLL_GRAPH,
   RPH_POLL_OBJECT;
   
   public static boolean isDirectAccess(STEAccessMethodType method) {
	   switch(method) {
	   case DPH_INDEX_GRAPH : case DPH_INDEX_SUBJECT : case DPH_POLL_GRAPH : case DPH_POLL_SUBJECT : case DPH_SCAN : 
		   return true;
	   }
	   return false;
   }

   public static boolean isReverseAccess(STEAccessMethodType method) {
	   switch(method) {
	   case RPH_INDEX_GRAPH : case RPH_INDEX_OBJECT : case RPH_POLL_GRAPH : case RPH_POLL_OBJECT : case RPH_SCAN :
		   return true;
	   }
	   return false;
   }

   public static boolean isGraphAccess(STEAccessMethodType method) {
	   switch(method) {
	   case DPH_INDEX_GRAPH : case RPH_INDEX_GRAPH : case DPH_POLL_GRAPH : case RPH_POLL_GRAPH : 
		   return true;
	   }
	   return false;
   }
   
   public static boolean isIndexAccess(STEAccessMethodType method) {
	   switch(method) {
	   case DPH_INDEX_GRAPH: case DPH_INDEX_SUBJECT: case RPH_INDEX_GRAPH: case RPH_INDEX_OBJECT: return true;
	   }
	   return false;
   }

   public static boolean isPollAccess(STEAccessMethodType method) {
	   switch(method) {
	   case DPH_POLL_GRAPH: case DPH_POLL_SUBJECT: case RPH_POLL_GRAPH: case RPH_POLL_OBJECT: return true;
	   }
	   return false;
   }
   public static boolean isScanAccess(STEAccessMethodType method) {
	   switch(method) {
	   case DPH_SCAN: case RPH_SCAN: return true;
	   }
	   return false;
   }
   }
   