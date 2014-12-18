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

import com.ibm.research.rdf.store.schema.Pair;
import com.ibm.research.rdf.store.sparql11.planner.statistics.StatisticComparator;

//
// Similar to EAccessMethod but copied here to make the ST module self-contained;
//
public class AccessMethod implements Comparable<AccessMethod>
   {
   
   AccessMethodType type;
   Pair<Double> cost;
   
   //
   // Constructor for the class. Depending on the type, the provided cost
   // is either the actual or the partial cost
   //
   public AccessMethod(AccessMethodType type, Pair<Double> cost)
      {
      this.type = type;
      this.cost = cost;
      }
   
   public AccessMethodType getType()
      {
      return type;
      }

   public boolean isPartial()
      {
      return ((this.type == AccessMethodType.DPH_POLL_SUBJECT) || (this.type == AccessMethodType.RPH_POLL_OBJECT) ? true : false);
      }
   
   public Pair<Double> getCost()
      {
      return cost;
      }

   public void setType(AccessMethodType type)
      {
      this.type = type;
      }

   public void setCost(Pair<Double> cost)
      {
      this.cost = cost;
      }
   
   public int compareTo(AccessMethod arg0)
      {
      if (StatisticComparator.lessThan(this.getCost(), arg0.getCost()))
         {
         return -1;
         }
      else if (StatisticComparator.equals(this.getCost(), arg0.getCost()))
         {
         return 0;
         }
      else 
         {
         return 1;
         }       
      }

   //
   // Output the contents of the object as a string
   //
   public String toString()
      {
      StringBuffer returnString = new StringBuffer();

      returnString.append("Method: " + this.type + " Cost: " + this.cost);
      return returnString.toString();
      }

   }
