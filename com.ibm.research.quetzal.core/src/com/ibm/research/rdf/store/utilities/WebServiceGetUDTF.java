package com.ibm.research.rdf.store.utilities;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
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
					resolver = createNamespaces(wc.getWritableConstantValue().toString());
					break;
				case 2:
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

		return ObjectInspectorFactory.getStandardStructObjectInspector(new LinkedList(outputColumnNames.keySet()), foi);

	}

	@Override
	public void process(Object[] arg0) throws HiveException {
		String url = PrimitiveObjectInspectorFactory.javaStringObjectInspector.getPrimitiveJavaObject(arg0[0]);
		List<Object[]> result = null;
		InputStream stream = getResponseAsStream(url);
		try {
			result = parseResponse(stream, resolver, xpathForRows, xPathForColumns, false);
			stream.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public InputStream getResponseAsStream(String url) {
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
		GetMethod method = new GetMethod(url);
		

		try {
			int returnCode = client.executeMethod(method);

			if (returnCode == HttpStatus.SC_NOT_IMPLEMENTED) {
				System.err.println("The Post method is not implemented by this URI");
				// still consume the response body
				throw new RuntimeException("Failed to contact service:" + method.getResponseBodyAsString());
				
			} else {
				stream = method.getResponseBodyAsStream();
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
		argsToProcess[0] = PrimitiveObjectInspectorFactory.javaStringObjectInspector.create("http://localhost:8082/getDrugBank");
		// argsToProcess[0] = PrimitiveObjectInspectorFactory.javaStringObjectInspector.create("file:///tmp/foo");

		ObjectInspector[] inputParams = createInput(); 
		
		WebServiceGetUDTF udtf = new WebServiceGetUDTF();
		udtf.initialize(inputParams);
		udtf.process(argsToProcess);
		
	}

/*	public static ObjectInspector[] createInput() {
		ObjectInspector[] inputParams = new ObjectInspector[9];
		ObjectInspector sc =  (ObjectInspector) PrimitiveObjectInspectorFactory.getPrimitiveWritableConstantObjectInspector(PrimitiveCategory.STRING, new Text("request,status"));
		inputParams[0] = sc;
		sc =  (ObjectInspector) PrimitiveObjectInspectorFactory.getPrimitiveWritableConstantObjectInspector(PrimitiveCategory.STRING, new Text("urn=http://camera-org/"));
		inputParams[1] = sc;
		sc =  (ObjectInspector) PrimitiveObjectInspectorFactory.getPrimitiveWritableConstantObjectInspector(PrimitiveCategory.STRING, new Text("//urn:ResponseStatus"));
		inputParams[2] = sc;
		sc =  (ObjectInspector) PrimitiveObjectInspectorFactory.getPrimitiveWritableConstantObjectInspector(PrimitiveCategory.STRING, new Text("request"));
		inputParams[3] = sc;
		sc =  (ObjectInspector) PrimitiveObjectInspectorFactory.getPrimitiveWritableConstantObjectInspector(PrimitiveCategory.STRING, new Text("./urn:requestURL"));
		inputParams[4] = sc;
		sc =  (ObjectInspector) PrimitiveObjectInspectorFactory.getPrimitiveWritableConstantObjectInspector(PrimitiveCategory.STRING, new Text("<string>"));
		inputParams[5] = sc;
		sc =  (ObjectInspector) PrimitiveObjectInspectorFactory.getPrimitiveWritableConstantObjectInspector(PrimitiveCategory.STRING, new Text("status"));
		inputParams[6] = sc;
		sc =  (ObjectInspector) PrimitiveObjectInspectorFactory.getPrimitiveWritableConstantObjectInspector(PrimitiveCategory.STRING, new Text("./urn:statusCode"));
		inputParams[7] = sc;
		sc =  (ObjectInspector) PrimitiveObjectInspectorFactory.getPrimitiveWritableConstantObjectInspector(PrimitiveCategory.STRING, new Text("<string>"));
		inputParams[8] = sc;
		return inputParams;
	}  */
	
	public static ObjectInspector[] createInput() {
		ObjectInspector[] inputParams = new ObjectInspector[9];
		ObjectInspector sc =  (ObjectInspector) PrimitiveObjectInspectorFactory.getPrimitiveWritableConstantObjectInspector(PrimitiveCategory.STRING, new Text("drug,id"));
		inputParams[0] = sc;
		sc =  (ObjectInspector) PrimitiveObjectInspectorFactory.getPrimitiveWritableConstantObjectInspector(PrimitiveCategory.STRING, new Text("x=http://www.drugbank.ca"));
		inputParams[1] = sc;
		sc =  (ObjectInspector) PrimitiveObjectInspectorFactory.getPrimitiveWritableConstantObjectInspector(PrimitiveCategory.STRING, new Text("/x:drugbank/x:drug[./x:transporters/x:transporter/x:polypeptide/x:external-identifiers/x:external-identifier/x:resource/text()=\"UniProtKB\"]"));
		inputParams[2] = sc;
		sc =  (ObjectInspector) PrimitiveObjectInspectorFactory.getPrimitiveWritableConstantObjectInspector(PrimitiveCategory.STRING, new Text("drug"));
		inputParams[3] = sc;
		sc =  (ObjectInspector) PrimitiveObjectInspectorFactory.getPrimitiveWritableConstantObjectInspector(PrimitiveCategory.STRING, new Text("./x:name/text()"));
		inputParams[4] = sc;
		sc =  (ObjectInspector) PrimitiveObjectInspectorFactory.getPrimitiveWritableConstantObjectInspector(PrimitiveCategory.STRING, new Text("<string>"));
		inputParams[5] = sc;
		sc =  (ObjectInspector) PrimitiveObjectInspectorFactory.getPrimitiveWritableConstantObjectInspector(PrimitiveCategory.STRING, new Text("transporter"));
		inputParams[6] = sc;
		sc =  (ObjectInspector) PrimitiveObjectInspectorFactory.getPrimitiveWritableConstantObjectInspector(PrimitiveCategory.STRING, new Text("./x:transporters/x:transporter"));
		inputParams[7] = sc;
		sc =  (ObjectInspector) PrimitiveObjectInspectorFactory.getPrimitiveWritableConstantObjectInspector(PrimitiveCategory.STRING, new Text("<string>"));
		inputParams[8] = sc;
		return inputParams;
	} 

}
