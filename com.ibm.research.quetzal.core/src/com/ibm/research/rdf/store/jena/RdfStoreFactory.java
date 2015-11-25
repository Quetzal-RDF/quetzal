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
 package com.ibm.research.rdf.store.jena;

import java.sql.Connection;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.ibm.research.rdf.store.Store;
import com.ibm.research.rdf.store.Store.Backend;
import com.ibm.research.rdf.store.config.Constants;
import com.ibm.research.rdf.store.jena.impl.DB2Dataset;
import com.ibm.research.rdf.store.jena.impl.DB2Graph;
import com.ibm.research.rdf.store.runtime.service.sql.StoreHelper;

public class RdfStoreFactory extends DatasetFactory
   {

   // Do we need the connectMethod that returns GraphStore

   /**
    * Connects to a already created tripleStore/Dataset in the RDFStore.
    * 
    * @connection identifies the database and schema in which the dataset exists
    * @datasetName a unique identifier for the dataset in the DB/Schema
    * @return the default Model/Graph of the created dataset
    */

   public static Dataset connectDataset(Store store, Connection connection, Backend backend)
      {
      StoreHelper.setSchema(connection, backend, store.getSchemaName());
      if (backend == Backend.db2)
         {
         StoreHelper.setPath(connection, store.getSchemaName());
         }
      DB2Graph g = new DB2Graph(store, connection, Constants.DEFAULT_GRAPH_MONIKER);
      Model model = ModelFactory.createModelForGraph(g);
      return new DB2Dataset(model);

      }

//   public static Model connectDefaultModel(Store store, Connection connection, String backend)
//      {
//      return connectDataset(store, connection, backend).getDefaultModel();
//      }

//   public static Model connectNamedModel(Store store, Connection connection, String backend, String iri)
//      {
//
//      if (iri.equalsIgnoreCase(Constants.DEFAULT_GRAPH_MONIKER))
//         {
//         }
//
//      Dataset dSource = (Dataset) connectDataset(store, connection, backend);
//
//      return dSource.getNamedModel(iri);
//      }

//   public static Graph connectDefaultGraph(Store store, Connection connection, String backend)
//      {
//      return connectDataset(store, connection, backend).asDatasetGraph().getDefaultGraph();
//      }

//   public static Graph connectNamedGraph(Store store, Connection connection, String backend, String iri)
//      {
//      return connectNamedModel(store, connection, backend, iri).getGraph();
//      }

   /*
    * // Not sure of this one public static GraphStore connectGraphStore(Store store, Connection connection) { StoreHelper.setSchema(connection,
    * store.getSchemaName()); return null; }
    */
   }