/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ibm.research.quetzal.loader;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.PipelineResult.State;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.io.TextIO.Read.Bound;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.options.Validation.Required;
import org.apache.beam.sdk.transforms.Combine;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.GroupByKey;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.SerializableFunction;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PCollectionTuple;
import org.apache.beam.sdk.values.PCollectionView;
import org.apache.beam.sdk.values.TupleTag;
import org.apache.beam.sdk.values.TupleTagList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.StatementCollector;

import com.google.api.services.bigquery.model.TableFieldSchema;
import com.google.api.services.bigquery.model.TableRow;
import com.google.api.services.bigquery.model.TableSchema;
import com.ibm.research.rdf.store.Store.Db2Type;
import com.ibm.research.rdf.store.config.Constants;
import com.ibm.research.rdf.store.runtime.service.types.TypeMap;
import com.ibm.research.rdf.store.runtime.service.types.TypeMap.TypeCategory;
import com.ibm.research.rdf.store.runtime.service.types.TypeMapForLoader;
import com.ibm.wala.util.collections.HashMapFactory;
import com.ibm.wala.util.collections.HashSetFactory;

/**
 * An example that reads RDF files from a directory broken into smaller chunks
 * and loads them. Adapted from WordCount.java in beam
 * 
 * <pre>
 *   1. Executing a Pipeline both locally and using the selected runner
 *   2. Using ParDo with static DoFns defined out-of-line
 *   3. Building a composite transform
 *   4. Defining your own pipeline options
 * </pre>
 *
 * <p>
 * Concept #1: you can execute this pipeline either locally or using by
 * selecting another runner. These are now command-line options and not
 * hard-coded as they were in the MinimalWordCount example.
 *
 * <p>
 * To change the runner, specify:
 * 
 * <pre>
 * {@code
 *   --runner=YOUR_SELECTED_RUNNER
 * }
 * </pre>
 *
 * <p>
 * To execute this pipeline, specify a local output file (if using the
 * {@code DirectRunner}) or output prefix on a supported distributed file
 * system.
 * 
 * <pre>
 * {@code
 *   --output=[YOUR_LOCAL_FILE | YOUR_OUTPUT_PREFIX]
 * }
 * </pre>
 *
 * <p>
 * Choose your own input with {@code --inputFile}.
 */
public class BigQueryLoader {

	private static final TupleTag<String> languages = new TupleTag<String>() {
		private static final long serialVersionUID = -3923823265301357082L;
	};

	private static final TupleTag<String> datatypes = new TupleTag<String>() {
		private static final long serialVersionUID = -7393487068354805574L;			
	};

	private static final TupleTag<KV<String,Db2Type>> predicateType = new TupleTag<KV<String,Db2Type>>() {
		private static final long serialVersionUID = -3923823265301357082L;		
	};
	
	private static final TupleTag<KV<String, KV<String, KV<String, Short>>>> entities = new TupleTag<KV<String, KV<String, KV<String, Short>>>>() {
		private static final long serialVersionUID = 8665879349850405191L;
	};

	private static final class MergeJSON<T> implements SerializableFunction<Iterable<JSONObject>, JSONObject> {
		private static final long serialVersionUID = 6627022411624872181L;

		static <T> MergeJSON<T> make(MergeFn<T> merge) {
			return new MergeJSON<T>(merge);
		}
		
		private MergeJSON(MergeFn<T> merge) {
			this.merge = merge;
		}

		@FunctionalInterface
		interface MergeFn<T> extends Serializable {
			T merge(T a, T b);
		}
		
		private final MergeFn<T> merge;
		
		@Override
		@SuppressWarnings("unchecked")
		public JSONObject apply(Iterable<JSONObject> input) {
			JSONObject x = new JSONObject();

			for(JSONObject y : input) {
				y.forEach((Object t, Object u) -> {
					String pred = (String) t;
					T v = (T) u;
					if (! x.containsKey(pred)) {
						x.put(pred, v);
					} else {
						x.put(pred, merge.merge((T)x.get(pred), v));
					}
				});
			}

			return x;
		}
	}

	/**
	 * Concept #2: You can make your pipeline assembly code less verbose by
	 * defining your DoFns statically out-of-line. This DoFn tokenizes RDF
	 * triples into subject -> (predicate, value) KVs we pass it to a ParDo in
	 * the pipeline.
	 */
	static class ExtractSubjectTriplesFn extends DoFn<String, KV<String, KV<String, KV<String, Short>>>> {

		/**
		 * 
		 */
		private static final long serialVersionUID = -12119381151250242L;

		@ProcessElement
		public void processElement(ProcessContext c) {

			// Split the line into words.
			String words = c.element();
			RDFParser p = Rio.createParser(RDFFormat.NTRIPLES);
			ArrayList<Statement> myList = new ArrayList<Statement>();
			StatementCollector collector = new StatementCollector(myList);
			p.setRDFHandler(collector);
			try {
				p.parse(new StringReader(words), "http://test");
			} catch (IOException e) {
				System.err.println("skipping " + words + ":" + e);
			} catch (RDFParseException e) {
				System.err.println("skipping " + words + ":" + e);
			} catch (RDFHandlerException e) {
				System.err.println("skipping " + words + ":" + e);
			}
			// Output each subject, with a set of KVs of predicate values per
			// subject encountered into the output PCollection.
			for (Statement s : myList) {
				short code = TypeMap.IRI_ID;
				
				if (s.getObject() instanceof Literal) {
					Literal l = (Literal) s.getObject();
		
					if (l.getLanguage() != null) {
						String langStr = l.getLanguage().toString();
						TypeMapForLoader.ensureLanguage(langStr);
						code = TypeMap.getLanguageType(langStr);
						
						c.sideOutput(languages, langStr);
						c.sideOutput(predicateType, KV.of(s.getPredicate().toString(), Db2Type.VARCHAR));
					}
					
					if (l.getDatatype() != null) {
						String typeStr = l.getDatatype().toString();
						c.sideOutput(datatypes, typeStr);
						
						TypeMapForLoader.ensureType(typeStr);
						code = TypeMap.idForIRI(typeStr);
						TypeCategory category = TypeMap.getTypeCategory(code);
		
						switch (category) {
						case DATE:
						case DATETIME: 
							c.sideOutput(predicateType, KV.of(s.getPredicate().toString(), Db2Type.TIMESTAMP));
							break;
						case NUMERIC:
							c.sideOutput(predicateType, KV.of(s.getPredicate().toString(), Db2Type.DECFLOAT));
							break;
						default:
							c.sideOutput(predicateType, KV.of(s.getPredicate().toString(), Db2Type.VARCHAR));
							break;
						}
					}
				
					c.output(
						KV.of(s.getSubject().toString(), KV.of(s.getPredicate().toString(), KV.of(l.stringValue(), code))));
				
				} else {
					c.output(
						KV.of(s.getSubject().toString(), KV.of(s.getPredicate().toString(), KV.of(s.getObject().toString(), code))));					
				}
			}
		}
	}

	/**
	 * Create JSON representation per subject based on keys
	 */

	public static class CreateJSONPerSubject extends DoFn<KV<String, Iterable<KV<String, KV<String, Short>>>>, JSONObject> {

		private final PCollectionView<JSONObject> predicateMapping;

		/**
		 * 
		 */
		private static final long serialVersionUID = 7809651082495837853L;

		public CreateJSONPerSubject(PCollectionView<JSONObject> predTable) {
			predicateMapping = predTable;
		}

		@SuppressWarnings("unchecked")
		private int index(String pred, JSONObject map) {
			int i = 0;
			for(Object o : new TreeSet<Object>(map.keySet())) {
				i++;
				if (pred.equals(o)) {
					return i;
				}
			}
			return -1;
		}

		@SuppressWarnings("unchecked")
		@ProcessElement
		public void processElement(ProcessContext c) {
			KV<String, Iterable<KV<String, KV<String, Short>>>> input = c.element();
			String subject = input.getKey();

			JSONObject predTable = c.sideInput(predicateMapping);

			JSONObject all = new JSONObject();
			HashMap<String, Set<KV<String,Short>>> map = HashMapFactory.make();

			for (KV<String, KV<String, Short>> item: input.getValue()) {
				if (!map.containsKey(item.getKey())) {
					map.put(item.getKey(), HashSetFactory.make());
				}
				Set<KV<String, Short>> l = map.get(item.getKey());
				l.add(KV.of(item.getValue().getKey().replaceAll("\"", "\\\\\""), item.getValue().getValue()));
			}

			for (Map.Entry<String, Set<KV<String,Short>>> e: map.entrySet()) {
				boolean multiple = (boolean) predTable.get(e.getKey());
				String colName = Constants.NAME_COLUMN_PREFIX_VALUE + index(e.getKey(), predTable);
				String typeName = Constants.NAME_COLUMN_PREFIX_TYPE + index(e.getKey(), predTable);
				Set<KV<String, Short>> l = e.getValue(); 
				assert !l.isEmpty();
				if (! multiple) {
					all.put(colName, l.iterator().next().getKey());
					all.put(typeName, l.iterator().next().getValue());
				} else {
					JSONArray a = new JSONArray();
					l.forEach((KV<String,Short> p) -> { a.add(p.getKey()); });
					all.put(colName, a);

					JSONArray b = new JSONArray();
					l.forEach((KV<String,Short> p) -> { b.add(p.getValue()); });
					all.put(typeName, b);	
				}
			}
			all.put(Constants.NAME_COLUMN_ENTRY, subject);
			c.output(all);
		}
	}


	/**
	 * Options supported by {@link BigQueryLoader}.
	 *
	 * <p>
	 * Concept #4: Defining your own configuration options. Here, you can add
	 * your own arguments to be processed by the command-line parser, and
	 * specify default values for them. You can then access the options values
	 * in your pipeline code.
	 *
	 * <p>
	 * Inherits standard configuration options.
	 */
	public interface LoaderOptions extends PipelineOptions {

		/**
		 * Set this option to choose a different input file or glob.
		 */
		@Description("Path of the file to read from")
		@Required
		String getInputFile();

		void setInputFile(String value);

		/**
		 * Set this required option to specify where to write the output.
		 */
		@Description("Path of the file to write to")
		@Required
		String getOutput();

		void setOutput(String value);

		/**
		 * read gzip'ed files
		 */
		@Description("Whether to read gzip'ed files")
		boolean getUseGzip();

		void setUseGzip(boolean v);

		/**
		 * Google Cloud project for loading to BigQuery
		 */
		@Description("Google Cloud project")
		String getCloudProject();

		void setCloudProject(String v);

		/**
		 * Google Cloud service account email, for JDBC connection
		 */
		@Description("Google Cloud service account email")
		String getEmail();

		void setEmail(String v);

		/**
		 * Google Cloud access key, either p12 or JSON
		 */
		@Description("Google Cloud access key")
		String getKey();

		void setKey(String v);
	}

	private static PCollection<JSONObject> reduceStrings(PCollection<String> table) {
		return table.apply("get pred JSON", ParDo.of(new DoFn<String, JSONObject>() {
			private static final long serialVersionUID = -6915154158686774192L;
			@SuppressWarnings("unchecked")
			@ProcessElement
			public void processElement(ProcessContext c) {
				String input = c.element();
				JSONObject x = new JSONObject();
				x.put(input, false);
				c.output(x);
			}}));
	}

	public static void write(String fileName, PCollection<JSONObject> data) {
		data.apply("convert to JSON string", ParDo.of(new DoFn<JSONObject,String>() {
			private static final long serialVersionUID = -4204128594221801617L;
			@ProcessElement
			public void processElement(ProcessContext c) {
				c.output(c.element().toJSONString());
			}
		})).apply("Write predicate table", TextIO.Write.to(fileName));

	}

	public static String dphTempLocation(LoaderOptions options) {
		return options.getTempLocation() + "/DPH";
	}

	public static void dph(LoaderOptions options, PCollection<JSONObject> data) {
		if (isLocalDir(options.getOutput())) {
			write(options.getOutput() + "_DPH", data);
		} else {
			write(dphTempLocation(options), data);
		}
	}

	private static boolean isLocalDir(String outName) {
		return outName.startsWith("/");
	}

	public static void preds(String outName, PCollection<JSONObject> data) {
		if (isLocalDir(outName)) {
			write(outName, data);
		} else {
			write(outName, predicateTable(), data);
		}
	}

	public static void types(String outName, PCollection<JSONObject> data) {
		if (isLocalDir(outName)) {
			write(outName, data);
		} else {
			write(outName, datatypeTable(), data);
		}
	}

	public static void write(String table, TableSchema schema, PCollection<JSONObject> data) {
		data.apply("convert to TableRow", ParDo.of(new DoFn<JSONObject,TableRow>() {
			private static final long serialVersionUID = -4204128594221801617L;
			@SuppressWarnings("unchecked")
			@ProcessElement
			public void processElement(ProcessContext c) {
				JSONObject obj = c.element();
				TableRow x = new TableRow();
				obj.keySet().forEach((Object key) -> {
					x.set((String) key, obj.get(key));
				});
				c.output(x);
			}
		})).apply(BigQueryIO.Write
				.withTableDescription(table)
				.withWriteDisposition(BigQueryIO.Write.WriteDisposition.WRITE_TRUNCATE)
				.withCreateDisposition(BigQueryIO.Write.CreateDisposition.CREATE_IF_NEEDED)
				.withSchema(schema)
				.to(table));
	}

	public static TableFieldSchema field(String name, String type) {
		TableFieldSchema x = new TableFieldSchema();
		x.setName(name);
		x.setType(type);
		return x;
	}

	public static TableSchema datatypeTable() {
		TableSchema x = new TableSchema();
		x.setFields(
				Arrays.asList(
						field("datatype_name", "STRING"),
						field("datatype_id", "INTEGER")));
		return x;
	}

	public static TableSchema predicateTable() {
		TableSchema x = new TableSchema();
		x.setFields(
				Arrays.asList(
						field("pred", "STRING"),
						field("onetoone", "BOOLEAN"),
						field("num_hashes", "INTEGER"),
						field("db2type", "STRING"),
						field("spills", "INTEGER"),
						field("hash0", "INTEGER"), 
						field("hash1", "INTEGER"), 
						field("hash2", "INTEGER")));
		return x;
	}

	public static String getPredicateTableName(LoaderOptions options) {
		return options.getOutput() + "_PREDICATES";
	}

	public static void main(String[] args) throws SQLException{
		LoaderOptions options = PipelineOptionsFactory.fromArgs(args).withValidation().as(LoaderOptions.class);

		String table = options.getOutput();

		CreateStore cs = null;
		if (! isLocalDir(table)) {
			cs = new CreateStore(options);		
			table = table.substring(table.lastIndexOf('.')+1);

			cs.dropStore(table);
		}
		
		Pipeline p = Pipeline.create(options);

		// 1. parse triples, collecting entities, datatypes and languages
		Bound reader = TextIO.Read
				.from(options.getInputFile())
				.withCompressionType(TextIO.CompressionType.AUTO);
		PCollectionTuple triples = p.apply("ReadLines", reader)
				.apply(ParDo.of(new ExtractSubjectTriplesFn())
						.withOutputTags(entities, TupleTagList.of(Arrays.asList(datatypes, languages, predicateType))));
		PCollection<KV<String, Iterable<KV<String, KV<String,Short>>>>> subjectGroups = triples
				.get(entities)
				.apply("Group by Key", GroupByKey.<String, KV<String, KV<String,Short>>>create());

		// build mapping of types for predicates
		PCollectionView<JSONObject> predicateTypes = triples.get(predicateType)
		  .apply("Group by predicate", GroupByKey.<String, Db2Type>create())
		  .apply("build predicate types", ParDo.of(new DoFn<KV<String,Iterable<Db2Type>>, JSONObject>() {
			private static final long serialVersionUID = -1149518644032820939L;
			    @SuppressWarnings("unchecked")
				@ProcessElement
				public void processElement(ProcessContext c) {
					String predicate = c.element().getKey();
					Db2Type type = c.element().getValue().iterator().next();
					for(Db2Type t : c.element().getValue()) {
						if (t != type) {
							type = Db2Type.MIXED;
							break;
						}
					}
					
					JSONObject result = new JSONObject();
					result.put(predicate, type);

					c.output(result);
				}
		  })).apply("combine pred type JSON", Combine.globally(MergeJSON.make((Db2Type a, Db2Type b) -> { return a==b? a: Db2Type.MIXED; })).asSingletonView());
		
		// 2. collect set of predicates as JSON object, mapping predicates to column numbers
		@SuppressWarnings("unchecked")
		PCollection<JSONObject> predCollection = subjectGroups.apply("get pred JSON", ParDo.of(new DoFn<KV<String, Iterable<KV<String, KV<String, Short>>>>, JSONObject>() {
			private static final long serialVersionUID = -6915154158686774192L;
			@ProcessElement
			public void processElement(ProcessContext c) {
				KV<String, Iterable<KV<String, KV<String, Short>>>> input = c.element();

				Set<String> once = HashSetFactory.make();
				Set<String> twice = HashSetFactory.make();
				for(KV<String, KV<String, Short>> y : input.getValue()) {
					if (once.contains(y.getKey())) {
						twice.add(y.getKey());
					} else {
						once.add(y.getKey());
					}
				}

				JSONObject x = new JSONObject();
				for(String pred : once) {
					x.put(pred, twice.contains(pred)? true: false);
				}

				c.output(x);
			}})).apply("combine pred JSON", Combine.globally(MergeJSON.make((Boolean a, Boolean b) -> { return a || b; })));
		PCollectionView<JSONObject> predTable = predCollection.apply("combine pred JSON", Combine.globally(MergeJSON.make((Boolean a, Boolean b) -> { return a || b; })).asSingletonView());

		// 3. create table mapping datatypes and languages to numbers
		PCollection<JSONObject> langTable = 
				reduceStrings(triples.get(languages)).apply("combine JSON", Combine.globally(MergeJSON.make((Boolean a, Boolean b) -> { return a || b; })));
		PCollectionView<JSONObject> datatypeTable = reduceStrings(triples.get(datatypes))
				.apply("combine JSON", Combine.globally(MergeJSON.make((Boolean a, Boolean b) -> { return a || b; })).asSingletonView());
		types(options.getOutput() + "_TYPES", langTable.apply("Build type table", ParDo.of(new DoFn<JSONObject, JSONObject>() {
			private static final long serialVersionUID = -1149518644032820939L;
			@ProcessElement
			public void processElement(ProcessContext c) {
				JSONObject langs = c.element();
				for(Object lang : langs.keySet()) {
					TypeMapForLoader.ensureLanguage((String) lang);
				}

				JSONObject datatypes = c.sideInput(datatypeTable);
				for(Object typeIRI : datatypes.keySet()) {
					TypeMapForLoader.ensureType((String) typeIRI);
				}

				TypeMapForLoader.dump(new TypeMapForLoader.Writer() {
					@SuppressWarnings("unchecked")
					@Override
					public void write(String type, short value) {
						JSONObject o = new JSONObject();
						o.put("datatype_name", type);
						o.put("datatype_id", value);
						c.output(o);		
					}
				});
			}
		}).withSideInputs(datatypeTable)));

		// 4. create table of entities, with predicates mapped to column numbers
		//    (collect predicates with multiple objects for some subject)
		PCollection<JSONObject> stuff = subjectGroups
				.apply("Create JSON per subject", 
						ParDo.of(new CreateJSONPerSubject(predTable))
						.withSideInputs(predTable));

		// 5. write entities as DPH
		dph(options, stuff);

		// 6. write table of predicates information
		preds(getPredicateTableName(options), 
				predCollection.apply("Build predicate table", ParDo.of(new DoFn<JSONObject, JSONObject>() {
					private static final long serialVersionUID = 7325592745849805380L;
					@SuppressWarnings("unchecked")
					@ProcessElement
					public void processElement(ProcessContext c) {
						int i = 0;
						JSONObject preds = c.element();
						for(Object pred : new TreeSet<Object>(preds.keySet())) {
							i++;
							boolean multiple = (boolean)preds.get(pred);
							Db2Type type = (Db2Type) c.sideInput(predicateTypes).get(pred);
							JSONObject row = new JSONObject();
							row.put("pred", pred);
							row.put("onetoone", !multiple);
							row.put("db2type", type==null? Db2Type.VARCHAR: type.toString());
							row.put("num_hashes", 1);
							row.put("spills", 0);
							row.put("hash0", i);
							row.put("hash1", -1);
							row.put("hash2", -1);
							c.output(row);						
						}
					}	
				}).withSideInputs(predicateTypes)));

		// do it all
		State s = p.run().waitUntilFinish();

		if (s == State.DONE && !isLocalDir(options.getOutput())) {
			int dphSize = Stage2.doit(options);
			
			cs.createStore(table, dphSize);
		}
		
	}
	
	public static class Stage2 {
		public static int doit(LoaderOptions options) throws SQLException {
			String table = options.getOutput();
			table = table.substring(table.lastIndexOf('.')+1);

			TableSchema dphSchema = DphSchemaJDBC.readSchema(options.getCloudProject(), options.getEmail(), options.getKey(), table);

			Pipeline p = Pipeline.create(options);

			p.apply("read temp dph", TextIO.Read.from(dphTempLocation(options) + "*"))
		 	 .apply("convert to TableRow", ParDo.of(new DoFn<String,TableRow>() {
				private static final long serialVersionUID = -4204128594221801617L;
				@SuppressWarnings("unchecked")
				@ProcessElement
				public void processElement(ProcessContext c) {
					try {
						JSONParser parser = new JSONParser();
						JSONObject obj = (JSONObject) parser.parse(c.element());
						TableRow x = new TableRow();
						obj.keySet().forEach((Object key) -> {
							x.set((String) key, obj.get(key));
						});
						c.output(x);
					} catch (ParseException e) {
						assert false : e;
					}
				}
			}))
			.apply(BigQueryIO.Write.to(options.getOutput() + "_DPH").withSchema(dphSchema));

			// load dph
			p.run().waitUntilFinish();
			
			// predicate columns excludes subject
			return dphSchema.size()-1;
		}

		public static void main(String[] args) throws SQLException{
			LoaderOptions options = PipelineOptionsFactory.fromArgs(args).withValidation().as(LoaderOptions.class);
			doit(options);
		}
	}
}
