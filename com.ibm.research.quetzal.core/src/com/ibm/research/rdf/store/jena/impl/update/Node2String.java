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

import com.hp.hpl.jena.graph.Node;
import com.ibm.research.rdf.store.config.Constants;
import com.ibm.research.rdf.store.jena.RdfStoreException;
import com.ibm.research.rdf.store.runtime.service.types.TypeMap;

public class Node2String
   {
   public static String getString(Node n)
      {
      if (n.isURI())
         {
         return n.toString(false);
         }
      else if (n.isBlank())
         {
         return Constants.PREFIX_BLANK_NODE + n.toString();
         }
      else if (n.isLiteral())
         {
         String object;
         if (n.getLiteral().getDatatype() != null)
            {
            // mdb types handling //uri = n.getLiteralDatatypeURI();
            // object = n.getLiteral().toString(false);
            object = n.getLiteralLexicalForm();
            // object = n.getLiteralValue().toString();
            }
         else
            {
            String lang = n.getLiteralLanguage();
            if (lang != null && lang.length() > 0)
               {
               // mdb types
               // object = n.getLiteralValue() + Constants.LITERAL_LANGUAGE
               // + lang;
               object = n.getLiteralValue().toString();
               }
            else
               {
               // mdb datatype change
               // object = "\"" + n.getLiteralValue() + "\"";
               object = n.getLiteralValue().toString();
               }
            }
         return object;
         }
      else
         {
         return "\"" + n.toString(false) + "\"";
         }
      }

   public static short getType(Node n)
      {
      if (n.isURI())
         {
         return TypeMap.IRI_ID;
         }
      if (n.isBlank())
         {
         return TypeMap.BLANK_NODE_ID;
         }

      if (n.isLiteral())
         {

         if (n.getLiteral().getDatatype() != null)
            {
            return TypeMap.getDatatypeType(n.getLiteralDatatypeURI());
            }
         else
            {
            String lang = n.getLiteralLanguage();
            if (lang != null && lang.length() > 0)
               {
               return TypeMap.getLanguageType(lang);
               }
            else
               {
               return TypeMap.SIMPLE_LITERAL_ID;
               }
            }
         }

      throw new RdfStoreException("Unknown RDFterm");
      }
   }