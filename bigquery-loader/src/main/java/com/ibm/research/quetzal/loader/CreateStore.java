package com.ibm.research.quetzal.loader;

import java.util.Collections;
import java.util.Map;

import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.Field;
import com.google.cloud.bigquery.InsertAllRequest.RowToInsert;
import com.google.cloud.bigquery.Schema;
import com.google.cloud.bigquery.StandardTableDefinition;
import com.google.cloud.bigquery.Table;
import com.google.cloud.bigquery.TableDefinition;
import com.google.cloud.bigquery.TableId;
import com.google.cloud.bigquery.TableInfo;
import com.ibm.research.quetzal.loader.BigQueryLoader.LoaderOptions;
import com.ibm.wala.util.collections.HashMapFactory;

public class CreateStore {
	private final String datasetName;
	private final BigQuery bigquery;

	private static String dataset(LoaderOptions options) {
		String table = options.getOutput();
		return table.substring(table.indexOf(':')+1, table.indexOf('.'));	
	}
	
	public CreateStore(LoaderOptions options) {
		this(dataset(options), 
			 BigQueryOptions.newBuilder().setProjectId(options.getCloudProject()).build().getService());
	}

	public CreateStore(String datasetName, BigQuery bigquery) {
		this.datasetName = datasetName;
		this.bigquery = bigquery;
	}

	public void dropStore(String storeName) {
		for (Table t : bigquery.listTables(datasetName).iterateAll()) {
			if (t.getTableId().getTable().startsWith(storeName)) {
				System.err.println("deleting " + t.getTableId());
				t.delete();
			}
		}
	}
	
	public Table createTable(String tableName, Field[] fields) {	
		TableId tableId = TableId.of(datasetName, tableName);
		Schema schema = Schema.of(fields);
		TableDefinition tableDefinition = StandardTableDefinition.of(schema);
		TableInfo tableInfo = TableInfo.newBuilder(tableId, tableDefinition).build();
		Table t = bigquery.create(tableInfo);
		System.err.println("created " + t.getTableId());
		return t;
	}

	public static void main(String[] args) {
		CreateStore cs = 
			new CreateStore(args[1], BigQueryOptions.newBuilder().setProjectId(args[0]).build().getService());

		String storeName = args[2];
		
		cs.dropStore(storeName);
		cs.createStore(storeName, 0);
	}
	
	public void createStore(String storeName, int dphSize) {
		Table storeTable = createTable(storeName, new Field[]{
				Field.of("entry_ID", Field.Type.integer()),
				Field.of("storeName", Field.Type.string()),
				Field.of("version", Field.Type.integer()),
				Field.of("directPrimary", Field.Type.string()),
				Field.of("dPrimarySize", Field.Type.integer()),
				Field.of("directSecondary", Field.Type.string()),
				Field.of("reversePrimary", Field.Type.string()),
				Field.of("rPrimarySize", Field.Type.integer()),
				Field.of("reverseSecondary", Field.Type.string()),
				Field.of("longStrings", Field.Type.string()),
				Field.of("maxStringLen", Field.Type.integer()),
				Field.of("gidMaxStringLen", Field.Type.integer()),
				Field.of("hasLongStrings", Field.Type.integer()),
				Field.of("basicStatsTable", Field.Type.string()),
				Field.of("topKStatsTable", Field.Type.string()),
				Field.of("lastStatsUpdated", Field.Type.timestamp()),
				Field.of("predicateMappings", Field.Type.string()),
				Field.of("systemPredicateTable", Field.Type.string()),
				Field.of("status", Field.Type.integer()),
				Field.of("dataTypeTable", Field.Type.string()),
				Field.of("hasSpills", Field.Type.integer())
		});

		Map<String,Object> storeData = HashMapFactory.make(); 
		storeData.put("entry_ID", 0);
		storeData.put("storeName", storeName);
		storeData.put("version", 1);
		storeData.put("directPrimary", storeName + "_DPH");
		storeData.put("hasLongStrings", 0);
		storeData.put("basicStatsTable", storeName + "_basic_stats");
		storeData.put("topKStatsTable", storeName + "_topk_stats");
		storeData.put("lastStatsUpdated", 0);
		storeData.put("status", 0);
		storeData.put("dataTypeTable", storeName + "_TYPES");
		storeData.put("hasSpills", 0);
		storeData.put("dPrimarySize", dphSize);
		storeTable.insert(Collections.singleton(RowToInsert.of(storeData)));
		
		createTable(storeName + "_basic_stats", new Field[]{
				Field.of("TYPE", Field.Type.string()),
				Field.of("AVG", Field.Type.floatingPoint()),
				Field.of("STDDEV", Field.Type.floatingPoint()),
				Field.of("MIN", Field.Type.integer()),
				Field.of("MAX", Field.Type.integer())
		});
		
		createTable(storeName + "_topk_stats", new Field[]{
				Field.of("TYPE", Field.Type.string()),
				Field.of("GRAPH", Field.Type.string()),
				Field.of("SUBJECT", Field.Type.string()),
				Field.of("PREDICATE", Field.Type.string()),
				Field.of("OBJECT", Field.Type.string()),
				Field.of("CNT", Field.Type.integer()) 
		});
	}

}
