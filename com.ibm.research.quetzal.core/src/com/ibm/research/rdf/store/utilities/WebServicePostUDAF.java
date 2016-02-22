package com.ibm.research.rdf.store.utilities;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.parse.SemanticException;
import org.apache.hadoop.hive.ql.udf.generic.AbstractGenericUDAFResolver;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDAFEvaluator;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDAFEvaluator.AggregationBuffer;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDAFParameterInfo;
import org.apache.hadoop.hive.serde2.io.ShortWritable;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector.PrimitiveCategory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.WritableConstantStringObjectInspector;
import org.apache.hadoop.hive.serde2.typeinfo.PrimitiveTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;
import org.apache.hadoop.io.Text;

import com.ibm.research.rdf.store.runtime.service.types.TypeMap;
import com.ibm.wala.util.collections.Pair;

public class WebServicePostUDAF extends AbstractGenericUDAFResolver {

	@Override
	public GenericUDAFEvaluator getEvaluator(GenericUDAFParameterInfo info) throws SemanticException {
		// Type-checking goes here
		return new TestUDFEvaluator();
	}

	public static class TestUDFEvaluator extends GenericUDAFEvaluator implements WebServiceInterface {

		private String urlForService = null;
		private String xpathForRows = null;
		private NamespaceResolver resolver;
		private List<Pair<String, Pair<String, String>>> xPathForColumns = new LinkedList<Pair<String, Pair<String, String>>>();

		private Map<String, Integer> outputColumnNames = new HashMap<String, Integer>();
		private Map<String, Integer> inputColumns = new HashMap<String, Integer>();
		private Map<String, Integer> postedColumns = new HashMap<String, Integer>();
		private Set<String> joinColumns = new HashSet<String>();

		@SuppressWarnings("deprecation")
		public static class TableAggregator implements AggregationBuffer {
			public List<Object[]> data;

			public TableAggregator() {
				data = new LinkedList<Object[]>();
			}

			public void add(Object[] datum) {
				data.add(datum);
			}
		}
		
		public List<Pair<String, Pair<String, String>>> getXpathForColumns() {
			return xPathForColumns;
		}

		@Override
		/**
		 * Assumption: parameters are passed in the following order:  
		 * all input parameters which are NOT constants and hence not writableConstantStringParameters
		 * followed by a set of constants to denote:
		 * urlForService, input columnNames separated by
		 * ',', posted columns separated by ',', output parameters separated by ',', namespace parameters as defined by "key1=val1, key2=val2..",
		 * xpath for rows, xpath for columns as specified by col1name, xpathForCol1, xpathForCol1Type etc. All of these parameters will be
		 * passed in as WritableConstantStringObjectInspectors. 
		 */
		public ObjectInspector init(Mode m, ObjectInspector[] parameters) throws HiveException {
			// TODO Auto-generated method stub
			super.init(m, parameters);
			ArrayList<ObjectInspector> foi = new ArrayList<ObjectInspector>();

			int i = 0;
			int k = 0;
			List<String> inputCols = new LinkedList<String>();
			LinkedList<String> l =  new LinkedList<String>();

			while (i < parameters.length) {
				PrimitiveObjectInspector param = (PrimitiveObjectInspector) parameters[i];
				if (param instanceof WritableConstantStringObjectInspector) {
					WritableConstantStringObjectInspector wc = (WritableConstantStringObjectInspector) param;
					switch(k) {
						case 0:
							urlForService = wc.getWritableConstantValue().toString();
							break;
						case 1:
							StringTokenizer tokenizer = new StringTokenizer(wc.getWritableConstantValue().toString(), ", ");
							int z = 0;
							while (tokenizer.hasMoreTokens()) {
								String col = tokenizer.nextToken();
								inputColumns.put(col, z);
								inputCols.add(col);
								z++;
							}
							break;
						case 2:
							tokenizer = new StringTokenizer(wc.getWritableConstantValue().toString(), ", ");
							while (tokenizer.hasMoreTokens()) {
								String col = tokenizer.nextToken();
								postedColumns.put(col, inputColumns.get(col));
							}
							break;
						case 3:
							String str = wc.getWritableConstantValue().toString();
							handleOutputTypeSpecification(foi, str, l);
							break;
						case 4:
							resolver = createNamespaces(wc.getWritableConstantValue().toString());
							break;
						case 5:
							xpathForRows = wc.getWritableConstantValue().toString();
							break;			
						default: 
							addColXpathTuple(parameters, i);
							i+=2;
					}
					k++;
				} 
				i++;
			}
			
			// add all input columns to output because that will be returned by this function along with output columns on order of what was passed in.  
			// all input parameters will be treated as string unless they are type columns, indicated by _TYP
			for (String inputCol : inputCols) {
				if (outputColumnNames.containsKey(inputCol)) {
					continue;
				}
				if (!inputCol.endsWith("_TYP")) {
					foi.add(PrimitiveObjectInspectorFactory.writableStringObjectInspector);
				} else {
					foi.add(PrimitiveObjectInspectorFactory.writableShortObjectInspector);
				}
				l.add(inputCol);
			}
			
			for (String s : outputColumnNames.keySet()) {
				if (inputColumns.containsKey(s)) {
					joinColumns.add(s);
				}
			}

			return ObjectInspectorFactory.getStandardListObjectInspector(ObjectInspectorFactory.getStandardStructObjectInspector(l, foi));
		}



		@SuppressWarnings("deprecation")
		@Override
		public AggregationBuffer getNewAggregationBuffer() throws HiveException {
			// TODO Auto-generated method stub
			return new TableAggregator();
		}

		@SuppressWarnings("deprecation")
		@Override
		/*
		 * Need to optimize this.  It will send all input types over to R as well right now which is less than ideal
		 * (non-Javadoc)
		 * @see org.apache.hadoop.hive.ql.udf.generic.GenericUDAFEvaluator#iterate(org.apache.hadoop.hive.ql.udf.generic.GenericUDAFEvaluator.AggregationBuffer, java.lang.Object[])
		 */
		public void iterate(AggregationBuffer arg0, Object[] arg1) throws HiveException {
			// TODO Auto-generated method stub

			TableAggregator agg = (TableAggregator) arg0;

			Object[] s = new Object[inputColumns.size()];
			for (int i = 0; i < inputColumns.size(); i++) {
				if (arg1[i] instanceof String) {
					s[i] = new Text((String) arg1[i]);	
				} else {
					s[i] = arg1[i];
				}
			}
			agg.add(s);
		}
		
		private String inferInputType(Object[] arg, String colName) {
			if (inputColumns.containsKey(colName + "_TYP")) {
				int index = inputColumns.get(colName + "_TYP");
				short obj = (short) arg[index];
				if (obj == TypeMap.DECIMAL_ID) {
					return TypeMap.DECIMAL_IRI;
				} else if (obj == TypeMap.DATE_ID) {
					return TypeMap.DATE_IRI;
				} else if (obj == TypeMap.DATETIME_ID) {
					return TypeMap.DATETIME_IRI;
				} else if (obj == TypeMap.INT_ID || obj == TypeMap.INTEGER_ID || obj == TypeMap.SHORT_ID) {
					return TypeMap.INTEGER_IRI;
				} else if (obj == TypeMap.FLOAT_ID || obj == TypeMap.LONG_ID) {
					return TypeMap.FLOAT_IRI;
				} else if (obj == TypeMap.DOUBLE_ID) {
					return TypeMap.DOUBLE_IRI;
				} else if (obj == TypeMap.BOOLEAN_ID) {
					return TypeMap.BOOLEAN_IRI;
				} else {
					return TypeMap.STRING_IRI;
				}
			} 
			return TypeMap.STRING_IRI;
		}

		@SuppressWarnings("deprecation")
		@Override
		public void merge(AggregationBuffer arg0, Object arg1) throws HiveException {
			// TODO Auto-generated method stub
			// KAVITHA: Not sure a merge operation should even be supported --
			// it doesnt make sense to send some subset of the table
			// to an outside service because we know nothing about the semantics
			// of the service
		}

		@SuppressWarnings("deprecation")
		@Override
		public void reset(AggregationBuffer arg0) throws HiveException {
			// TODO Auto-generated method stub

		}

		
		/** 
		 * Input rows are in terms of non writable types.  Need a mechanism to copy
		 * input files out but in the right format expected by hive, making sure not to copy shared variables
		 */
		private void copyToWritable(Object[] src, Object[] dest, int startIndex) {
			Set<Integer> sharedIndexes = new HashSet<Integer>();
			for (String s : joinColumns) {
				sharedIndexes.add(inputColumns.get(s));
			}
			
			for (int k = 0; k < src.length; k++) {
				if (sharedIndexes.contains(k)) {
					continue;
				}
				Object o = src[k];
				if (o instanceof Short) {
					ShortWritable s = new ShortWritable();
					s.set((short) o);
					dest[k + startIndex] = s;
				} else {
					dest[k + startIndex] = o;
				}

			}
		}
		
		private boolean isJoinable(Object[] inputRow, Object[] outputRow) {
			for (String s : joinColumns) {
				int inputIndex = inputColumns.get(s);
				int outputIndex = outputColumnNames.get(s);
				// KAVITHA: Of course the input and output object types won't match 
				// So revert to string equals :(
				if (!inputRow[inputIndex].toString().trim().equals(outputRow[outputIndex].toString().trim())) {
					return false;
				}
			}
			return true;
		}

		/**
		 * Assumption is that there is a set of shared variables that 
		 * are common across input and outpul cols.  We need to implement a join
		 * based on those shared variables
		 * 
		 * @param input
		 * @param output
		 * @return
		 */

		private List<Object[]> join(List<Object[]> input, List<Object[]> output) {
			List<Object[]> ret = new LinkedList<Object[]>();

			// nested loop join  
			for (int i = 0; i < input.size(); i++) {
				for (int j = 0; j < output.size(); j++) {
					Object[] iRow = input.get(i);
					Object[] oRow = output.get(j);
					int totalInRow = iRow.length + oRow.length - joinColumns.size();
					
					if (isJoinable(iRow, oRow)) {
						Object[] newRow = new Object[totalInRow];
						System.arraycopy(oRow, 0, newRow, 0, oRow.length);
						copyToWritable(iRow, newRow, oRow.length);
						ret.add(newRow);
						j++;
					} 
				}
			}
			System.out.println("Returned table:");
			WebServiceInterface.prettyPrint(ret);
			return ret;
		}

		@SuppressWarnings("deprecation")
		@Override
		// KAVITHA: we need to join the rows returned by the service call with
		// the input dataset based on shared variables between the input and output columns

		public Object terminate(AggregationBuffer arg0) throws HiveException {
			// TODO Auto-generated method stub
			List<Object[]> input = ((TableAggregator) arg0).data;
			
			System.out.println("printing input");
			WebServiceInterface.prettyPrint(input);

			// serialize the data as XML
			HttpClient client = new HttpClient();

			PostMethod method = new PostMethod(urlForService);
			method.setParameter("funcData", serialize(input));
			BufferedReader br = null;
			List<Object[]> result = null;

			try {
				int returnCode = client.executeMethod(method);
				
				if (returnCode == HttpStatus.SC_NOT_IMPLEMENTED) {
					System.err.println("The Post method is not implemented by this URI");
					// still consume the response body
					method.getResponseBodyAsString();
				} else {
					result = parseResponse(method.getResponseBodyAsStream(), resolver, xpathForRows, xPathForColumns);
				}
			} catch (Exception e) {
				e.printStackTrace();
				;
			} finally {
				method.releaseConnection();
				if (br != null)
					try {
						br.close();
					} catch (Exception fe) {
					}
			}
			result = join(input, result);
			return result;
		}

		@SuppressWarnings("deprecation")
		@Override
		public Object terminatePartial(AggregationBuffer arg0) throws HiveException {
			// TODO Auto-generated method stub
			return null;
		}

		private String serialize(List<Object[]> data) {
			StringBuffer buf = new StringBuffer();
	//		prettyPrint(data);
			buf.append("<data>");
			for (int j = 0; j < data.size(); j++) {
				Object[] s = data.get(j);
				buf.append("<row>");
				for (Map.Entry<String, Integer> entry : postedColumns.entrySet()) {
					buf.append("<").append(entry.getKey()).append(" type=\"")
							.append(inferInputType(s, entry.getKey())).append("\"> ").append(s[entry.getValue()]).append("</")
							.append(entry.getKey()).append("> ");
				}

				buf.append("</row>");
			}
			buf.append("</data>");
			return buf.toString();
		}



		@Override
		public Map<String, Integer> getOutputColumnNames() {
			// TODO Auto-generated method stub
			return outputColumnNames;
		}
	}

	public static void main(String[] args) throws Exception {
		test();
	}
	
	public static void test() throws HiveException {
		TestUDFEvaluator eval = new TestUDFEvaluator();
		List<ObjectInspector> inputParameters = new LinkedList<ObjectInspector>();
		
		// add url
		ObjectInspector sc =  (ObjectInspector) PrimitiveObjectInspectorFactory.getPrimitiveWritableConstantObjectInspector(TypeInfoFactory.stringTypeInfo, new Text("http://localhost:8083/postData"));
		inputParameters.add(sc);
		// add input columns
		sc = (ObjectInspector) PrimitiveObjectInspectorFactory.getPrimitiveWritableConstantObjectInspector(TypeInfoFactory.stringTypeInfo, new Text("name,age"));
		inputParameters.add(sc);
		// add postedColumns
		sc = (ObjectInspector) PrimitiveObjectInspectorFactory.getPrimitiveWritableConstantObjectInspector(TypeInfoFactory.stringTypeInfo, new Text("age"));
		inputParameters.add(sc);
		// add output columns
		sc = (ObjectInspector) PrimitiveObjectInspectorFactory.getPrimitiveWritableConstantObjectInspector(TypeInfoFactory.stringTypeInfo, new Text("age,sum"));
		inputParameters.add(sc);
		// add namespaces
		sc = (ObjectInspector) PrimitiveObjectInspectorFactory.getPrimitiveWritableConstantObjectInspector(TypeInfoFactory.stringTypeInfo, new Text(""));
		inputParameters.add(sc);
		// add xpath to get rows back
		sc = (ObjectInspector) PrimitiveObjectInspectorFactory.getPrimitiveWritableConstantObjectInspector(TypeInfoFactory.stringTypeInfo, new Text("//row"));
		inputParameters.add(sc);

		// add xpath specs for age
		sc = (ObjectInspector) PrimitiveObjectInspectorFactory.getPrimitiveWritableConstantObjectInspector(TypeInfoFactory.stringTypeInfo, new Text("age"));
		inputParameters.add(sc);
		sc = (ObjectInspector) PrimitiveObjectInspectorFactory.getPrimitiveWritableConstantObjectInspector(TypeInfoFactory.stringTypeInfo, new Text("./age"));
		inputParameters.add(sc);
		sc = (ObjectInspector) PrimitiveObjectInspectorFactory.getPrimitiveWritableConstantObjectInspector(TypeInfoFactory.stringTypeInfo, new Text(TypeMap.INTEGER_IRI));
		inputParameters.add(sc);
		// add xpath specs for sum
		sc = (ObjectInspector) PrimitiveObjectInspectorFactory.getPrimitiveWritableConstantObjectInspector(TypeInfoFactory.stringTypeInfo, new Text("sum"));
		inputParameters.add(sc);
		sc = (ObjectInspector) PrimitiveObjectInspectorFactory.getPrimitiveWritableConstantObjectInspector(TypeInfoFactory.stringTypeInfo, new Text("./sum"));
		inputParameters.add(sc);
		sc = (ObjectInspector) PrimitiveObjectInspectorFactory.getPrimitiveWritableConstantObjectInspector(TypeInfoFactory.stringTypeInfo, new Text(TypeMap.INTEGER_IRI));
		inputParameters.add(sc);
		
		ObjectInspector[] inputParams = new ObjectInspector[inputParameters.size()];
		inputParams = inputParameters.toArray(inputParams);
		

		eval.init(org.apache.hadoop.hive.ql.udf.generic.GenericUDAFEvaluator.Mode.COMPLETE, inputParams);
		
		Object[][] data = new Object[3][2];
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 2; j++) {
				if (j == 0) {
					data[i][j] = "name_" + i;
				} else {
					data[i][j] = i + 40;
				}
			}
		}
		AggregationBuffer buf = eval.getNewAggregationBuffer();
		for (int i = 0; i < 3; i++) {
			Object[] row = data[i];
			eval.iterate(buf, row);
		}
		eval.terminate(buf); 	
	}

}