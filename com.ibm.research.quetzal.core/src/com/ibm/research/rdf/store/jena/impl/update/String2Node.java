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
 package com.ibm.research.rdf.store.jena.impl.update;

import com.hp.hpl.jena.datatypes.TypeMapper;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.ibm.research.rdf.store.config.Constants;
import com.ibm.research.rdf.store.jena.RdfStoreException;
import com.ibm.research.rdf.store.runtime.service.types.TypeMap;

public class String2Node
   {

   private RDFNode node;

   public String2Node(String type, String value, short datatype)
      {
	  if(value.equals("NULL")){
		  node = null;
	  }
	  else if (value.startsWith(Constants.PREFIX_BLANK_NODE))
         {
         AnonId id = new AnonId(value.substring(Constants.PREFIX_BLANK_NODE.length()));
         node = ModelFactory.createDefaultModel().createResource(id);
         }
      else if (type.equals(Constants.NAME_COLUMN_SUBJECT))
         {
         node = ResourceFactory.createResource(value);
         }
      else if (type.equals(Constants.NAME_COLUMN_PREDICATE))
         {
         node = ResourceFactory.createProperty(value);
         }
      else if (type.equals(Constants.NAME_COLUMN_OBJECT))
         {
         assignLiteral(value, datatype);
         }
      }

   public String2Node(String type, String value)
      {
	   if(value.equals("NULL")){
		   node = null;
	   }
	   else if (value.startsWith(Constants.PREFIX_BLANK_NODE))
         {
         AnonId id = new AnonId(value.substring(Constants.PREFIX_BLANK_NODE.length()));
         node = ModelFactory.createDefaultModel().createResource(id);
         }
      else if (type.equals(Constants.NAME_COLUMN_SUBJECT))
         {
         node = ResourceFactory.createResource(value);
         }
      else if (type.equals(Constants.NAME_COLUMN_PREDICATE))
         {
         node = ResourceFactory.createProperty(value);
         }
      else if (type.equals(Constants.NAME_COLUMN_OBJECT))
         {
         assert (false); // mdb change code call other ctor
         }
      }

   private void assignLiteral(String value, short datatype)
      {

      if (datatype == TypeMap.IRI_ID)
         {
         node = ResourceFactory.createResource(value);
         return;
         }

      if (datatype == TypeMap.SIMPLE_LITERAL_ID || datatype == 0)
         {
         node = ResourceFactory.createPlainLiteral(value);
         return;
         }

      if (datatype > TypeMap.DATATYPE_IDS_START && datatype < TypeMap.DATATYPE_IDS_END)
         {
         node = ResourceFactory.createTypedLiteral(value, TypeMapper.getInstance()
               .getTypeByName(TypeMap.getTypedString(datatype)));
         return;
         }

      // TODO language literals
      if (datatype >= TypeMap.LANG_IDS_START)
         {
         node = ModelFactory.createDefaultModel().createLiteral(value, TypeMap.getLanguageString(datatype));
         return;
         }

      throw new RdfStoreException("Unhandled literal type:" + datatype);
      /*
       * if (value.contains(Constants.TYPED_LITERAL_DELIMITER) && value.contains(Constants.LITERAL_LANGUAGE)) { // TODO
       * 
       * } else if (value.contains(Constants.LITERAL_LANGUAGE)) { String[] temp = value.split(Constants.LITERAL_LANGUAGE); if
       * (!Pattern.matches("[a-zA-Z]+(-[a-zA-Z0-9]+)*$", value.substring(value .lastIndexOf(Constants.LITERAL_LANGUAGE) + 1))) { if
       * (value.startsWith("mailto:")) { node = ResourceFactory.createResource(value); } else { node =
       * ModelFactory.createDefaultModel().createLiteral( value); } } else if (temp == null || temp.length != 2) { node =
       * ModelFactory.createDefaultModel().createLiteral(value); } else { node = ModelFactory.createDefaultModel().createLiteral(temp[0], temp[1]); }
       * } else { node = ResourceFactory.createResource(value); }
       */
      }

   public RDFNode getNode()
      {
      return node;
      }
   }
