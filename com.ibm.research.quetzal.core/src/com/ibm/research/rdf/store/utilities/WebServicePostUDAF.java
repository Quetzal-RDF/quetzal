package com.ibm.research.rdf.store.utilities;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.parse.SemanticException;
import org.apache.hadoop.hive.ql.udf.generic.AbstractGenericUDAFResolver;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDAFEvaluator;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDAFParameterInfo;
import org.apache.hadoop.hive.serde2.io.ShortWritable;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.WritableConstantStringObjectInspector;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.ibm.research.rdf.store.runtime.service.types.TypeMap;

public class WebServiceUDAF extends AbstractGenericUDAFResolver {

	@Override
	public GenericUDAFEvaluator getEvaluator(GenericUDAFParameterInfo info) throws SemanticException {
		// Type-checking goes here
		return new TestUDFEvaluator();
	}

	public static class TestUDFEvaluator extends GenericUDAFEvaluator {

		private String urlForService = null;
		private String functionBody = null;

		private Map<String, Integer> outputColumnNames = new HashMap<String, Integer>();
		private List<String> inputColumnNames = new ArrayList<String>();
		private List<String> outputColumnTypes = new ArrayList<String>();

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

		@Override
		/**
		 * Assumption: parameters are passed in the following order:
		 * urlForService, functionName, functionBody, columnNames separated by
		 * '|', and all output parameters. All of these parameters will be
		 * passed in as WritableConstantStringObjectInspectors. All input
		 * parameters are passed in after WritableConstantStringParameters, so
		 * the separation is based on types.
		 */
		public ObjectInspector init(Mode m, ObjectInspector[] parameters) throws HiveException {
			// TODO Auto-generated method stub
			super.init(m, parameters);
			ArrayList<ObjectInspector> foi = new ArrayList<ObjectInspector>();

			int i = 0;
			for (ObjectInspector p : parameters) {
				PrimitiveObjectInspector param = (PrimitiveObjectInspector) p;

				// parameters are either input parameters, or constants. the
				// first constant is the url, function name, function body & followed by the output parameter
				// names
				if (param instanceof WritableConstantStringObjectInspector) {
					WritableConstantStringObjectInspector wc = (WritableConstantStringObjectInspector) param;
					if (i == 0) {
						urlForService = wc.getWritableConstantValue().toString();
					} else if (i == 1) {
						functionBody = wc.getWritableConstantValue().toString();
					} else if (i == 2) {
						StringTokenizer tokenizer = new StringTokenizer(wc.getWritableConstantValue().toString(), "|");
						while (tokenizer.hasMoreTokens()) {
							inputColumnNames.add(tokenizer.nextToken());
						}
					} else {
						String str = wc.getWritableConstantValue().toString();
						if (str.equals("index")) {
							continue;				// KAVITHA: The other systems need an index column passed in to the function.  Ignore it.
						}
						handleOutputTypeSpecification(foi, i, str);
					}
					i++;
				} 
			}
			
			// add all input columns to output because that will be returned by this function along with output columns.  
			// all input parameters will be treated as string unless they are type columns, indicated by _TYP
			for (String inputCol : inputColumnNames) {
				if (!inputCol.endsWith("_TYP")) {
					foi.add(PrimitiveObjectInspectorFactory.writableStringObjectInspector);
				} else {
					foi.add(PrimitiveObjectInspectorFactory.writableShortObjectInspector);
				}
			}
			
			LinkedList<String> l =  new LinkedList<String>(outputColumnNames.keySet());
			l.addAll(inputColumnNames);

			return ObjectInspectorFactory.getStandardListObjectInspector(ObjectInspectorFactory.getStandardStructObjectInspector(l, foi));
		}

		private void handleOutputTypeSpecification(ArrayList<ObjectInspector> foi, int indexOfCol, String outputColSpec) {
			int sep = outputColSpec.indexOf(":");
			String outName = null;
			if (sep != -1) {
				outName = outputColSpec.substring(0, sep);

				String type = outputColSpec.substring(sep + 1);
				if (type.equals("Decimal")) {
					outputColumnTypes.add("decimal");
				} else if (type.equals("Date")) {
					outputColumnTypes.add("date");
				} else if (type.equals("Timestamp")) {
					outputColumnTypes.add("timestamp");
				} else if (type.equals("int")) {
					outputColumnTypes.add("int");
				} else if (type.equals("float")) {
					outputColumnTypes.add("float");
				} else if (type.equals("double")) {
					outputColumnTypes.add("double");
				} else if (type.equals("boolean")) {
					outputColumnTypes.add("boolean");
				} else if (type.equals("short")) {
					outputColumnTypes.add("short");
				} else {
					outputColumnTypes.add("string");
				}
			}  else {
				outName = outputColSpec;
			}
			outputColumnNames.put(outName, indexOfCol - 3); 	
			outputColumnNames.put(outName + "_TYP", indexOfCol - 2);
			foi.add(PrimitiveObjectInspectorFactory.writableStringObjectInspector);			// output column name
			foi.add(PrimitiveObjectInspectorFactory.writableShortObjectInspector);			// output column type as a type map
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

			Object[] s = new Object[inputColumnNames.size()];
			for (int i = 3; i < inputColumnNames.size() + 3; i++) {
				if (arg1[i] instanceof String) {
					s[i - 3] = new Text((String) arg1[i]);	
				} else {
					s[i - 3] = arg1[i];
				}
			}
			agg.add(s);
		}
		
		private String inferInputType(Object[] arg, int colIndex, int rowIndex) {
			String colName = inputColumnNames.get(colIndex);
			if (inputColumnNames.contains(colName + "_TYP")) {
				int index = inputColumnNames.indexOf(colName + "_TYP");
				short obj = (short) arg[index];
				if (obj == TypeMap.DECIMAL_ID) {
					return "decimal";
				} else if (obj == TypeMap.DATE_ID) {
					return "date";
				} else if (obj == TypeMap.DATETIME_ID) {
					return "timestamp";
				} else if (obj == TypeMap.INT_ID || obj == TypeMap.INTEGER_ID || obj == TypeMap.SHORT_ID) {
					return "int";
				} else if (obj == TypeMap.FLOAT_ID || obj == TypeMap.LONG_ID) {
					return "float";
				} else if (obj == TypeMap.DOUBLE_ID) {
					return "double";
				} else if (obj == TypeMap.BOOLEAN_ID) {
					return "boolean";
				} else {
					return "string";
				}
			} 
			return "string";
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

		private List<Object[]> addRowNumbers(List<Object[]> r) {
			List<Object[]> result = new LinkedList<Object[]>();
			for (int i = 0; i < r.size(); i++) {
				Object[] s = r.get(i);
				Object[] ss = new Object[s.length + 1];
				System.arraycopy(s, 0, ss, 0, s.length);
				ss[ss.length - 1] = i;
				result.add(ss);
			}
			return result;
		}
		
		/** 
		 * Input rows are in terms of non writable types.  Need a mechanism to copy
		 * input files out but in the right format expected by hive
		 */
		private void copyToWritable(Object[] src, Object[] dest, int startIndex, int endIndex) {
			assert dest.length == src.length + startIndex;
			for (int k = 0; k < endIndex; k++) {
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

		/**
		 * Assumption is that the right hand side (input temp table) and left
		 * hand side (output temp table) share NO columns in common other than
		 * the index column. Also, assumption is that the index column is the
		 * very last column in each temp table
		 * 
		 * @param r
		 * @param l
		 * @return
		 */

		private List<Object[]> join(List<Object[]> r, List<Object[]> l) {
			List<Object[]> ret = new LinkedList<Object[]>();

			// iterate over the two lists and merge them
			int i = 0;
			int j = 0;

			while (i < r.size() && j < l.size()) {
				Object[] rRow = r.get(i);
				Object[] lRow = l.get(j);
				int rlast = rRow.length - 1;
				int llast = lRow.length - 1;
				int totalInRow = rlast + llast;
				int rIndex = (int) rRow[rlast];
				int lIndex = ((IntWritable) lRow[llast]).get();

				if (rIndex == lIndex) {
					Object[] newRow = new Object[totalInRow];
					System.arraycopy(lRow, 0, newRow, 0, llast);
					copyToWritable(rRow, newRow, llast, rlast);
					//System.arraycopy(rRow, 0, newRow, llast, rlast);
					
					ret.add(newRow);
					j++;
				} else if (rIndex < lIndex) {
					i++;
				} else {
					j++;
				}

			}
			System.out.println("Returned table:");
			prettyPrint(ret);
			return ret;
		}

		@SuppressWarnings("deprecation")
		@Override
		// KAVITHA: we need to join the rows returned by the service call with
		// the input dataset based on their row numbers.
		// In DB2 and Postgresql, this is accomplished
		// as a join between the input dataset with an extra column for row
		// numbers (created by row_sequence). In Hive this would be
		// yet another table aggregation function whose sole job would be to add
		// row numbers. Better to do this join here in Java (UGH)
		// and return the joined result

		public Object terminate(AggregationBuffer arg0) throws HiveException {
			// TODO Auto-generated method stub
			List<Object[]> r = ((TableAggregator) arg0).data;
			
			System.out.println("printing input");
			prettyPrint(r);

			// add row numbers to table. This step is used to join the table
			// that is returned to the input table.
			// Note this step cannot be performed until the reduce step here
			// (e.g., it CANNOT be done in iterate)
			List<Object[]> rs = addRowNumbers(r);

			// serialize the data as XML
			HttpClient client = new HttpClient();

			PostMethod method = new PostMethod(urlForService);
			method.setParameter("funcBody", functionBody);
			method.setParameter("funcData", serialize(rs));
			BufferedReader br = null;
			List<Object[]> result = null;

			try {
				int returnCode = client.executeMethod(method);

				if (returnCode == HttpStatus.SC_NOT_IMPLEMENTED) {
					System.err.println("The Post method is not implemented by this URI");
					// still consume the response body
					method.getResponseBodyAsString();
				} else {
					result = parseResponse(method.getResponseBodyAsStream());
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
			result = join(rs, result);
			return result;
		}

		private void printDocument(Document doc) throws Exception {
			OutputFormat format = new OutputFormat(doc);
			format.setIndenting(true);
			XMLSerializer serializer = new XMLSerializer(System.out, format);
			serializer.serialize(doc);
		}

		private List<Object[]> parseResponse(InputStream is) throws Exception {
			BufferedReader br;
			br = new BufferedReader(new InputStreamReader(is));
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new InputSource(br));
			printDocument(doc);

			XPath xPath = XPathFactory.newInstance().newXPath();

			System.out.println("In parse response");
			
			NodeList rows = (NodeList) xPath.compile("//*[local-name()='result']").evaluate(doc,
					XPathConstants.NODESET);

			List<Object[]> result = new LinkedList<Object[]>();
			for (int i = 0; i < rows.getLength(); i++) {
				Node row = rows.item(i);
				NodeList cells = (NodeList) xPath.compile("./*[local-name()='binding']").evaluate(row,
						XPathConstants.NODESET);

				Object[] k = new Object[outputColumnNames.size() + 1];		// return from function will always return an index column.  So the extra element in the k array reflects that		

				IntWritable indexValue = null;
				for (int j = 0; j < cells.getLength(); j++) {						
					String value = cells.item(j).getTextContent();
					assert cells.item(j).getAttributes() != null
							&& cells.item(j).getAttributes().getNamedItem("name") != null;

					String colName = cells.item(j).getAttributes().getNamedItem("name").getTextContent();
					if (colName.equals("index")) {
						indexValue = new IntWritable();
						indexValue.set(Integer.parseInt(value));
						continue;
					}
					k[j * 2] = new Text(value);
					ShortWritable st = new ShortWritable();
					st.set(getTypedValue(value, j));
					k[(j * 2) +1] =  st;
				}
				// add index at very end
				k[k.length - 1] = indexValue;
				result.add(k);
			}
			System.out.println("Parsed response");
			prettyPrint(result);
			return result;
		}

		private short getTypedValue(String value, int index) {
			if (outputColumnTypes.isEmpty()) {
				return TypeMap.SIMPLE_LITERAL_ID;
			}
			String colType = outputColumnTypes.get(index);
			return getTypedValue(value, colType);
		}

		private short getTypedValue(String value, String colType) {
			if (colType.equals("decimal")) {
				return TypeMap.DECIMAL_ID;
			} else if (colType.equals("Date")) {
				return TypeMap.DATE_ID;
			} else if (colType.equals("Timestamp")) {
				return TypeMap.DATETIME_ID;
			} else if (colType.equals("int")) {
				return TypeMap.INT_ID;
			} else if (colType.equals("float")) {
				return TypeMap.FLOAT_ID;
			} else if (colType.equals("double")) {
				return TypeMap.DOUBLE_ID;
			} else if (colType.equals("boolean")) {
				return TypeMap.BOOLEAN_ID;
			} else {
				return TypeMap.SIMPLE_LITERAL_ID;
			}
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
				for (int i = 0; i < s.length - 1; i++) {
					buf.append("<").append(inputColumnNames.get(i)).append(" type=\"xs:")
							.append(inferInputType(s, i, j)).append("\"> ").append(s[i]).append("</")
							.append(inputColumnNames.get(i)).append("> ");
				}

				buf.append("<index type=\"xs:int\">").append(s[s.length - 1]).append("</index>");
				buf.append("</row>");
			}
			buf.append("</data>");
			return buf.toString();
		}
	}

	public static void main(String[] args) throws Exception {
		TestUDFEvaluator eval = new TestUDFEvaluator();
		// testSerialize(eval);
		// testDeserialize(eval);
		testJoin(eval);
	}
	
	private static void prettyPrint(List<Object[]> table) {
		for (Object[] row : table) {
			for (Object r : row) {
				if (r == null) {
					System.out.print(r + " ");
					continue;
				}
				System.out.print(r + "-" + r.getClass() + " ");
			}
			System.out.println("");
		}
		System.out.println("*******************");
	}

	private static void testJoin(TestUDFEvaluator eval) {
		List<Object[]> input = new LinkedList<Object[]>();
		List<Object[]> output = new LinkedList<Object[]>();

		for (int i = 0; i < 5; i++) {
			Object[] row = new Object[2];
			row[0] = "foo" + i;
			row[1] = i;
			input.add(row);
		}

		for (int i = 0; i < 10; i++) {
			Object[] row = new Object[2];
			row[0] = "foo" + i;
			row[1] = i / 2;
			output.add(row);
		}
		
		prettyPrint(eval.join(input, output));
		output = new LinkedList<Object[]>();
		for (int i = 3; i < 4; i++) {
			Object[] row = new Object[2];
			row[0] = "foo" + i;
			row[1] = i;
			output.add(row);
		}
		prettyPrint(eval.join(input, output));

		output = new LinkedList<Object[]>();
		for (int i = 0; i < 5; i += 2) {
			Object[] row = new Object[2];
			row[0] = "foo" + i;
			row[1] = i;
			output.add(row);
		}
		prettyPrint(eval.join(input, output));

	}

	private static void testDeserialize(TestUDFEvaluator eval) throws Exception {
		FileInputStream fi = new FileInputStream(
				"/Users/ksrinivs/git/DawgTests/dawg/sparql11-test-suite/exists/exists01.srx");
		eval.outputColumnNames.put("s", 0);
		eval.outputColumnNames.put("p", 1);
		eval.outputColumnNames.put("o", 2);

		eval.outputColumnTypes.add("string");
		eval.outputColumnTypes.add("string");
		eval.outputColumnTypes.add("string");

		System.out.println(eval.parseResponse(fi));
	}

	private static void testSerialize(TestUDFEvaluator eval) {
		List<Object[]> data = new LinkedList<Object[]>();
		eval.inputColumnNames.add("foo");
		eval.inputColumnNames.add("bar");
		eval.inputColumnNames.add("gar");

		for (int i = 0; i < 5; i++) {
			Object[] row = new Object[3];
			for (int j = 0; j < 3; j++) {
				row[j] = "j" + j;
			}
			data.add(row);
		}
		System.out.println(eval.serialize(data));
	}
}