package com.ibm.research.quetzal.spanner.loader;

import java.util.Arrays;
import com.google.cloud.spanner.Database;
import com.google.cloud.spanner.DatabaseAdminClient;
import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.DatabaseId;
import com.google.cloud.spanner.Operation;
import com.google.cloud.spanner.Spanner;
import com.google.cloud.spanner.SpannerOptions;
import com.google.spanner.admin.database.v1.CreateDatabaseMetadata;

public class CreateDatabase {

  static void createDatabase(DatabaseAdminClient dbAdminClient, DatabaseId id) {
    Operation<Database, CreateDatabaseMetadata> op = dbAdminClient.createDatabase(
        id.getInstanceId().getInstance(), id.getDatabase(),
        Arrays.asList("CREATE TABLE DPH (\n" + "  subject STRING(MAX) NOT NULL,\n"
            + "  col_0  ARRAY<STRING(MAX)>, \n" + "  col_1  ARRAY<STRING(MAX)>, \n"
            + "  col_2  STRING(MAX), \n" + "  col_3  STRING(MAX), \n" + "  col_4  STRING(MAX), \n"
            + "  col_5  STRING(MAX), \n" + "  col_6  STRING(MAX), \n"
            + "  col_7  ARRAY<STRING(MAX)>, \n" + "  col_8  STRING(MAX), \n"
            + "  col_9  STRING(MAX), \n" + "  col_10  STRING(MAX), \n" + "  col_11  STRING(MAX), \n"
            + "  col_12  STRING(MAX), \n" + "  col_13  STRING(MAX), \n"
            + "  col_14  ARRAY<STRING(MAX)>, \n" + "  col_15  STRING(MAX), \n"
            + "  col_16  STRING(MAX)) \n" + " PRIMARY KEY (subject)"));
    Database db = op.waitFor().getResult();
    System.out.println("Created database [" + db.getId() + "]");
  }

  public static void main(String[] args) {
    SpannerOptions options = SpannerOptions.newBuilder().build();
    Spanner spanner = options.getService();

    try {
  
      DatabaseId db = DatabaseId.of(options.getProjectId(), args[0], args[1]);
      // [END init_client]
      // This will return the default project id based on the environment.
      String clientProject = spanner.getOptions().getProjectId();
      if (!db.getInstanceId().getProject().equals(clientProject)) {
        System.err.println("Invalid project specified. Project in the database id should match"
            + "the project name set in the environment variable GCLOUD_PROJECT. Expected: "
            + clientProject);
      }
      // [START init_client]
      DatabaseClient dbClient = spanner.getDatabaseClient(db);
      DatabaseAdminClient dbAdminClient = spanner.getDatabaseAdminClient();
      createDatabase(dbAdminClient, db);
    } finally {
      spanner.close();
    }
    
  }
}
