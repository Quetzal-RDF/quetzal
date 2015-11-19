package com.ibm.research.rdf.store.utilities;

import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDTF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector.PrimitiveCategory;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.WritableConstantStringObjectInspector;
import org.apache.hadoop.io.Text;

import com.ibm.wala.util.collections.Pair;

public class WebServiceGetUDTF extends GenericUDTF implements WebServiceInterface {

	private String xpathForRows = null;
	private NamespaceResolver resolver;
	private String queryText = null;
	private enum httpMethod {GET, POST};
	private httpMethod method;
	List<String> inputColumns =  new LinkedList<String>();
	private List<Pair<String, Pair<String, String>>> xPathForColumns = new LinkedList<Pair<String, Pair<String, String>>>();

	private Map<String, Integer> outputColumnNames = new HashMap<String, Integer>();

	@Override
	public void close() throws HiveException {
		// TODO Auto-generated method stub

	}

	@Override
	public StructObjectInspector initialize(ObjectInspector[] parameters) throws UDFArgumentException {
		List<ObjectInspector> foi = new ArrayList<ObjectInspector>();

		int i = 0;
		int k = 0;
		while (i < parameters.length) {
			PrimitiveObjectInspector param = (PrimitiveObjectInspector) parameters[i];

			if (param instanceof WritableConstantStringObjectInspector) {
				WritableConstantStringObjectInspector wc = (WritableConstantStringObjectInspector) param;
				switch (k) {
				case 0:
					String str = wc.getWritableConstantValue().toString();
					handleOutputTypeSpecification(foi, str);
					break;
				case 1:
					str = wc.getWritableConstantValue().toString();
					StringTokenizer tokenizer = new StringTokenizer(str, ",");
					while (tokenizer.hasMoreTokens()) {
						inputColumns.add(tokenizer.nextToken());
					}
					break;

				case 2:
					queryText = wc.getWritableConstantValue().toString();
					break;
				case 3:
					str = wc.getWritableConstantValue().toString();
					if (str.equals("GET")) {
						method = httpMethod.GET;
					} else {
						method = httpMethod.POST;
					}
					break;
				case 4:
					resolver = createNamespaces(wc.getWritableConstantValue().toString());
					break;
				case 5:
					xpathForRows = wc.getWritableConstantValue().toString();
					break;
				default:
					addColXpathTuple(parameters, i);
					i += 2;
				}
				k++;
			}
			i++;
		}

		return ObjectInspectorFactory.getStandardStructObjectInspector(new LinkedList<String>(outputColumnNames.keySet()), foi);

	}

	@Override
	public void process(Object[] arg0) throws HiveException {
		String url = PrimitiveObjectInspectorFactory.javaStringObjectInspector.getPrimitiveJavaObject(arg0[0]);
		try {

			List<Object[]> result = null;
			InputStream stream = getResponseAsStream(url, arg0);
			result = parseResponse(stream, resolver, xpathForRows, xPathForColumns, false);
			stream.close();
			for (Object[] record : result) {
				forward(record);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public InputStream getResponseAsStream(String url, Object[] arg0) {
		InputStream stream = null;

		try {
			if (url.startsWith("file:")) {
				URL urlConn = new URL(url);
				stream = urlConn.openStream();
				return stream;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		HttpClient client = new HttpClient();	

		try {

			if (method == httpMethod.GET) {
				if (!queryText.isEmpty()) {
					url = url + "?" + URLEncoder.encode(queryText, "UTF-8");
				} else {
					url = url + "?";
				}
				
				for (int i = 1; i < arg0.length; i++) {
					url = url + "&" + URLEncoder.encode(PrimitiveObjectInspectorFactory.javaStringObjectInspector.getPrimitiveJavaObject(arg0[i]), "UTF-8");
				}
				
				GetMethod getMethod = new GetMethod(url);
				client.executeMethod(getMethod);
				stream = getMethod.getResponseBodyAsStream();
			} else {
				PostMethod postMethod = new PostMethod(url);	
				for (int i = 0; i < inputColumns.size(); i++) {
					postMethod.setParameter(inputColumns.get(i), PrimitiveObjectInspectorFactory.writableStringObjectInspector.getPrimitiveJavaObject(arg0[i]));
				}
				client.executeMethod(postMethod);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return stream;
	}

	@Override
	public Map<String, Integer> getOutputColumnNames() {
		// TODO Auto-generated method stub
		return outputColumnNames;
	}

	@Override
	public List<Pair<String, Pair<String, String>>> getXpathForColumns() {
		// TODO Auto-generated method stub
		return xPathForColumns;
	}
	
	public static void main(String[] args) throws Exception {
		Object[] argsToProcess = new Object[1];
		argsToProcess[0] = PrimitiveObjectInspectorFactory.javaStringObjectInspector.create("http://localhost:8081/getDrugBank");
		// argsToProcess[0] = PrimitiveObjectInspectorFactory.javaStringObjectInspector.create("file:///tmp/foo");

		ObjectInspector[] inputParams = createInput(); 
		
		WebServiceGetUDTF udtf = new WebServiceGetUDTF();
		udtf.initialize(inputParams);
		udtf.process(argsToProcess);
		
	}

	
	public static ObjectInspector[] createInput() {
		ObjectInspector[] inputParams = new ObjectInspector[15];
		ObjectInspector sc =  (ObjectInspector) PrimitiveObjectInspectorFactory.getPrimitiveWritableConstantObjectInspector(PrimitiveCategory.STRING, new Text("drug,id,action"));
		inputParams[0] = sc;
		sc =  (ObjectInspector) PrimitiveObjectInspectorFactory.getPrimitiveWritableConstantObjectInspector(PrimitiveCategory.STRING, new Text("url"));
		inputParams[1] = sc;
		sc =  (ObjectInspector) PrimitiveObjectInspectorFactory.getPrimitiveWritableConstantObjectInspector(PrimitiveCategory.STRING, new Text(""));
		inputParams[2] = sc;
		sc =  (ObjectInspector) PrimitiveObjectInspectorFactory.getPrimitiveWritableConstantObjectInspector(PrimitiveCategory.STRING, new Text("GET"));
		inputParams[3] = sc;
		sc =  (ObjectInspector) PrimitiveObjectInspectorFactory.getPrimitiveWritableConstantObjectInspector(PrimitiveCategory.STRING, new Text("xs=http://www.w3.org/2001/XMLSchema"));
		inputParams[4] = sc;
		sc =  (ObjectInspector) PrimitiveObjectInspectorFactory.getPrimitiveWritableConstantObjectInspector(PrimitiveCategory.STRING, new Text("//row"));
		inputParams[5] = sc;
		sc =  (ObjectInspector) PrimitiveObjectInspectorFactory.getPrimitiveWritableConstantObjectInspector(PrimitiveCategory.STRING, new Text("drug"));
		inputParams[6] = sc;
		sc =  (ObjectInspector) PrimitiveObjectInspectorFactory.getPrimitiveWritableConstantObjectInspector(PrimitiveCategory.STRING, new Text("./drug"));
		inputParams[7] = sc;
		sc =  (ObjectInspector) PrimitiveObjectInspectorFactory.getPrimitiveWritableConstantObjectInspector(PrimitiveCategory.STRING, new Text("<string>"));
		inputParams[8] = sc;
		sc =  (ObjectInspector) PrimitiveObjectInspectorFactory.getPrimitiveWritableConstantObjectInspector(PrimitiveCategory.STRING, new Text("id"));
		inputParams[9] = sc;
		sc =  (ObjectInspector) PrimitiveObjectInspectorFactory.getPrimitiveWritableConstantObjectInspector(PrimitiveCategory.STRING, new Text("./id"));
		inputParams[10] = sc;
		sc =  (ObjectInspector) PrimitiveObjectInspectorFactory.getPrimitiveWritableConstantObjectInspector(PrimitiveCategory.STRING, new Text("<string>"));
		inputParams[11] = sc;
		sc =  (ObjectInspector) PrimitiveObjectInspectorFactory.getPrimitiveWritableConstantObjectInspector(PrimitiveCategory.STRING, new Text("action"));
		inputParams[12] = sc;
		sc =  (ObjectInspector) PrimitiveObjectInspectorFactory.getPrimitiveWritableConstantObjectInspector(PrimitiveCategory.STRING, new Text("./action"));
		inputParams[13] = sc;
		sc =  (ObjectInspector) PrimitiveObjectInspectorFactory.getPrimitiveWritableConstantObjectInspector(PrimitiveCategory.STRING, new Text("<string>"));
		inputParams[14] = sc;
		return inputParams;
	} 

}
