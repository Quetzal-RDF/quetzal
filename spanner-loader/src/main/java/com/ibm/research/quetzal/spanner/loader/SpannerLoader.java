/*
 /*
 * Copyright 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ibm.research.quetzal.spanner.loader;

import java.util.ArrayList;
import org.apache.avro.reflect.Nullable;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.coders.DefaultCoder;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.io.gcp.spanner.SpannerIO;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.options.Validation;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.cloud.spanner.Mutation;
import com.google.cloud.spanner.Mutation.WriteBuilder;

/*
This sample demonstrates how to write to a Spanner table.
## Prerequisites
* Maven installed
* Set up GCP default credentials, one of the following:
    - export GOOGLE_APPLICATION_CREDENTIALS=path/to/credentials.json
    - gcloud auth application-default login
  [https://developers.google.com/identity/protocols/application-default-credentials]
* Create the Spanner table to write to, you'll need:
    - Instance ID
    - Database ID
    - Singers Table with schema:
       *singerId: INT64 NOT NULL
        firstName: STRING NOT NULL
        lastName: STRING NOT NULL
    - Albums Table with schema:
        singerId: INT64 NOT NULL
       *albumId: INT64 NOT NULL
        albumTitle: STRING NOT NULL
  [https://cloud.google.com/spanner/docs/quickstart-console]
## How to run
cd java-docs-samples/dataflow/spanner-io
mvn clean
mvn compile
mvn exec:java \
    -Dexec.mainClass=com.example.dataflow.SpannerWrite \
    -Dexec.args="--instanceId=my-instance-id \
                 --databaseId=my-database-id
*/

public class SpannerLoader {

  static final String DELIMITER = "\t";

  public interface Options extends PipelineOptions {

    @Description("DPH filename in the format in JSON")
    String getDphFilename();

    void setDphFilename(String value);

    @Description("Spanner instance ID to write to")
    @Validation.Required
    String getInstanceId();

    void setInstanceId(String value);

    @Description("Spanner database name to write to")
    @Validation.Required
    String getDatabaseId();

    void setDatabaseId(String value);
  }

  @DefaultCoder(AvroCoder.class)
  static class DPH {
    String subject;
    @Nullable String col_8;
    @Nullable String col_6;
    @Nullable String col_5;
    @Nullable String col_12;
    @Nullable String col_3;
    @Nullable String col_15;
    @Nullable String col_9;
    @Nullable String col_16;
    @Nullable ArrayList<String> col_7;
    @Nullable String col_2;
    @Nullable String col_11;
    @Nullable String col_4;
    @Nullable ArrayList<String> col_0;
    @Nullable ArrayList<String> col_14;
    @Nullable String col_10;
    @Nullable String col_13;
    @Nullable ArrayList<String> col_1;

    public DPH() {
      super();
    }
    
    private ArrayList<String> handleArrays(JSONArray arr) {
      ArrayList l = new ArrayList<String>(arr.size());
      for (Object o : arr) {
        l.add((String) o);
      }
      return l;
    }
    
    public void setField(String col, Object value) {
      if (col.equals("subject")) {
        subject = (String) value;
      } else if (col.equals("col_0")) {
        col_0 = handleArrays((JSONArray) value);
      } else if (col.equals("col_1")) {
        col_1 = handleArrays((JSONArray) value);
      } else if (col.equals("col_2")) {
        col_2 = (String) value;
      } else if (col.equals("col_3")) {
        col_3 = (String) value;
      } else if (col.equals("col_4")) {
        col_4 = (String) value;
      } else if (col.equals("col_5")) {
        col_5 = (String) value;
      } else if (col.equals("col_6")) {
        col_6 = (String) value;
      } else if (col.equals("col_7")) {
        col_7 = handleArrays((JSONArray) value);
      } else if (col.equals("col_8")) {
        col_8 = (String) value;
      } else if (col.equals("col_9")) {
        col_9 = (String) value;
      } else if (col.equals("col_10")) {
        col_10 = (String) value;
      } else if (col.equals("col_11")) {
        col_11 = (String) value;
      } else if (col.equals("col_12")) {
        col_12 = (String) value;
      } else if (col.equals("col_13")) {
        col_13 = (String) value;
      } else if (col.equals("col_14")) {
        col_14 = handleArrays((JSONArray) value);
      } else if (col.equals("col_15")) {
        col_15 = (String) value;
      } else if (col.equals("col_16")) {
        col_16 = (String) value;
      } 
    }
  }

  /**
   * Parses each tab-delimited line into a Singer object. The line format is the following:
   *   singer_id\tfirstName\tlastName
   */
  static class ParseDPH extends DoFn<String, DPH> {
    private static final Logger LOG = LoggerFactory.getLogger(ParseDPH.class);

    @ProcessElement
    public void processElement(ProcessContext c) {
      try {
        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject) parser.parse(c.element());

        DPH x = new DPH();
        obj.keySet().forEach((Object key) -> {
            x.setField((String) key, obj.get(key));
        });
        c.output(x);
      } catch (ParseException e) {
        LOG.info("ParseDPH: parse error on '" + c.element() + "': " + e.getMessage());
      }
    }
  }

  public static void main(String[] args) {
    Options options = PipelineOptionsFactory.fromArgs(args).withValidation().as(Options.class);
    Pipeline p = Pipeline.create(options);
    
    String instanceId = options.getInstanceId();
    String databaseId = options.getDatabaseId();
 
    // Read singers from a tab-delimited file
    p.apply("ReadDPH", TextIO.read().from(options.getDphFilename()))
        // Parse the tab-delimited lines into Singer objects
        .apply("ParseDPH", ParDo.of(new ParseDPH()))
        // Spanner expects a Mutation object, so create it using the Singer's data
        .apply("CreateDPHMutation", ParDo.of(new DoFn<DPH, Mutation>() {
          @ProcessElement
          public void processElement(ProcessContext c) {
            DPH dph = c.element();
            WriteBuilder b = Mutation.newInsertOrUpdateBuilder("DPH");
            b.set("subject").to(dph.subject);
            
            if (dph.col_0 != null) {
              b.set("col_0").toStringArray(dph.col_0);
            }
            
            if (dph.col_1 != null) {
              b.set("col_1").toStringArray(dph.col_1);
            }
            
            if (dph.col_2 != null) {
              b.set("col_2").to(dph.col_2);
            }
            
            if (dph.col_3 != null) {
              b.set("col_3").to(dph.col_3);
            }
            
            if (dph.col_4 != null) {
              b.set("col_4").to(dph.col_4);
            }
 
            if (dph.col_5 != null) {
              b.set("col_5").to(dph.col_5);
            }
            
            if (dph.col_6 != null) {
              b.set("col_6").to(dph.col_6);
            }
            
            if (dph.col_7 != null) {
              b.set("col_7").toStringArray(dph.col_7);
            }
            
            if (dph.col_8 != null) {
              b.set("col_8").to(dph.col_8);
            }
            
            if (dph.col_9 != null) {
              b.set("col_9").to(dph.col_9);
            }
            
            if (dph.col_10 != null) {
              b.set("col_10").to(dph.col_10);
            }
            
            if (dph.col_11 != null) {
              b.set("col_11").to(dph.col_11);
            }
            
            if (dph.col_12 != null) {
              b.set("col_12").to(dph.col_12);
            }
            
            if (dph.col_13 != null) {
              b.set("col_13").to(dph.col_13);
            }
            
            if (dph.col_14 != null) {
              b.set("col_14").toStringArray(dph.col_14);
            } 

            if (dph.col_15 != null) {
              b.set("col_15").to(dph.col_15);
            }
            
            if (dph.col_16 != null) {
              b.set("col_16").to(dph.col_16);
            }
            
            c.output(b.build());
          }
        }))
        // Finally write the Mutations to Spanner
        .apply("WriteDPH", SpannerIO.write()
            //.withBatchSizeBytes(1024) // 1KB
            .withInstanceId(instanceId)
            .withDatabaseId(databaseId));

 

    p.run().waitUntilFinish();
  }
}
