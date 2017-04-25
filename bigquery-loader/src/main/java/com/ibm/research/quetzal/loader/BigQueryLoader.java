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
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.options.Validation.Required;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.GroupByKey;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.KV;
import org.openrdf.model.Statement;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.StatementCollector;

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

	/**
	 * Concept #2: You can make your pipeline assembly code less verbose by
	 * defining your DoFns statically out-of-line. This DoFn tokenizes RDF
	 * triples into subject -> (predicate, value) pairs we pass it to a ParDo in
	 * the pipeline.
	 */
	static class ExtractSubjectTriplesFn extends DoFn<String, KV<String, KV<String, String>>> {

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
				// handle IO problems (e.g. the file could not be read)
			} catch (RDFParseException e) {
				// handle unrecoverable parse error
			} catch (RDFHandlerException e) {
				// handle a problem encountered by the RDFHandler
			}
			// Output each subject, with a set of pairs of predicate values per
			// subject encountered into the output PCollection.
			for (Statement s : myList) {
				c.output(
						KV.of(s.getSubject().toString(), KV.of(s.getPredicate().toString(), s.getObject().toString())));
			}
		}
	}

	/**
	 * Create JSON representation per subject based on keys
	 */

	public static class CreateJSONPerSubject extends DoFn<KV<String, Iterable<KV<String, String>>>, String> {

		@ProcessElement
		public void processElement(ProcessContext c) {
			KV<String, Iterable<KV<String, String>>> input = c.element();
			String subject = input.getKey();
			StringBuilder all = new StringBuilder();
			all.append("{");
			HashMap<String, HashSet<String>> map = new HashMap<String, HashSet<String>>();
			
			for (KV<String, String> item: input.getValue()) {
				if (!map.containsKey(item.getKey())) {
					map.put(item.getKey(), new HashSet<String>());
				}
				Set<String> l = map.get(item.getKey());
				l.add(item.getValue().replaceAll("\"", "\\\\\""));
			}
			
			for (Map.Entry<String, HashSet<String>> e: map.entrySet()) {
				all.append("\"" + e.getKey() + "\"");
				all.append(":");
				Set<String> l = e.getValue(); 
				assert !l.isEmpty();
				if (l.size() == 1) {
					all.append("\"" + l.iterator().next() + "\"");
				} else {
					all.append("[");
					all.append(String.join(",", l));
					all.append("]");
				}
				all.append(",");
			}
			all.append("\"subject\":" + "\"" + subject + "\"");
			all.append("}");
			c.output(all.toString());
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
		 * By default, this example reads from a public dataset containing the
		 * text of King Lear. Set this option to choose a different input file
		 * or glob.
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
	}

	public static void main(String[] args) {
		LoaderOptions options = PipelineOptionsFactory.fromArgs(args).withValidation().as(LoaderOptions.class);
		Pipeline p = Pipeline.create(options);

		// Concepts #2 and #3: Our pipeline applies the composite CountWords
		// transform, and passes the
		// static FormatAsTextFn() to the ParDo transform.
		p.apply("ReadLines", TextIO.Read.from(options.getInputFile())).apply(ParDo.of(new ExtractSubjectTriplesFn()))
				.apply("Group by Key", GroupByKey.<String, KV<String, String>>create())
				.apply("Create JSON per subject", ParDo.of(new CreateJSONPerSubject()))
				.apply("WriteJSON", TextIO.Write.to(options.getOutput()));

		p.run().waitUntilFinish();
	}
}
