package com.ibm.research.rdf.store.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDTF;
import org.apache.hadoop.hive.serde2.io.ShortWritable;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.WritableConstantStringObjectInspector;
import org.apache.hadoop.io.Text;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.ibm.wala.util.collections.HashMapFactory;
import com.ibm.wala.util.collections.Pair;

public class WebServiceGetUDTF extends GenericUDTF implements WebServiceInterface {

	private String xpathForRows = null;
	private NamespaceResolver resolver;
	private String queryText = null;

	private enum httpMethod {
		GET, POST
	};

	private httpMethod method;
	List<String> inputColumns;
	private List<List<String>> xPathForColumns;

	private Map<String, Integer> outputColumnNames;
	private int indexOfInput = -1;

	@Override
	public void close() throws HiveException {
		// TODO Auto-generated method stub

	}

	@Override
	public StructObjectInspector initialize(ObjectInspector[] parameters) throws UDFArgumentException {
		List<ObjectInspector> foi = new ArrayList<ObjectInspector>();
		List<String> l = new LinkedList<String>();
		
		inputColumns = new LinkedList<String>();
		xPathForColumns = new LinkedList<List<String>>();
		outputColumnNames = new HashMap<String, Integer>();


		int i = 0;
		while (i < parameters.length && parameters[i] instanceof WritableConstantStringObjectInspector) {
			WritableConstantStringObjectInspector wc = (WritableConstantStringObjectInspector) parameters[i];
			switch (i) {
			case 0:
				String str = wc.getWritableConstantValue().toString();
				System.out.println("handling output:" + str);
				handleOutputTypeSpecification(foi, str, l);
				i++;
				break;
			case 1:
				str = wc.getWritableConstantValue().toString();
				System.out.println("handling input:" + str);

				StringTokenizer tokenizer = new StringTokenizer(str, ",");
				while (tokenizer.hasMoreTokens()) {
					inputColumns.add(tokenizer.nextToken());
				}
				i++;
				break;
			case 2:
				queryText = wc.getWritableConstantValue().toString();
				System.out.println("handling queryText:" + queryText);
				i++;
				break;
			case 3:
				str = wc.getWritableConstantValue().toString();
				System.out.println("handling method:" + str);

				if (str.equals("GET")) {
					method = httpMethod.GET;
				} else {
					method = httpMethod.POST;
				}
				i++;
				break;
			case 4:
				System.out.println("handling namespaces:" + wc.getWritableConstantValue().toString());
				resolver = createNamespaces(wc.getWritableConstantValue().toString());
				i++;
				break;
			case 5:
				System.out.println("xpathforRows:" + wc.getWritableConstantValue().toString());
				xpathForRows = wc.getWritableConstantValue().toString();
				i++;
				break;
			default:
				addColXpathTuple(parameters, i);
				i += 3;
			}
		}
		indexOfInput = i; // Input starts where writable constants end
		System.out.println("xpathForColumns:" + xPathForColumns);
		System.out.println("Finished processing init");
		return ObjectInspectorFactory.getStandardStructObjectInspector(l, foi);

	}

	@Override
	public void process(Object[] arg0) throws HiveException {
		System.out.println("Processing records");
		for (int i = 0; i < arg0.length; i++) {
			System.out.println(PrimitiveObjectInspectorFactory.javaStringObjectInspector
			.getPrimitiveJavaObject(arg0[i]));
		}
		String url = PrimitiveObjectInspectorFactory.javaStringObjectInspector
				.getPrimitiveJavaObject(arg0[indexOfInput]);
		System.out.println("url:" + url);
		try {

			List<Object[]> result = null;
			InputStream stream = getResponseAsStream(url, arg0);
			result = parseResponse(stream, resolver, xpathForRows, xPathForColumns);
			for (Object[] record : result) {
				// KAVITHA: This is most annoying but we have a situation where
				// sometimes, data from input needs
				// to be passed along, but we don't really get it back from the
				// web service. So search parsed
				// response table to see if we have nulls (i.e., things we didnt
				// get back from the web service but
				// we are still expected to output).
				int k = 0;
				for (k = 0; k < record.length; k++) {
					if (record[k] == null)
						break;
					k++;
				}
				if (k == record.length) {
					forward(record);
				} else {

					int actualInputLength = arg0.length - indexOfInput - 1;
					System.out.println("actualinputlength" + actualInputLength);
					System.out.println("indexof input:" + indexOfInput);
					System.out.println("arg0.length:" + arg0.length);
					assert k + actualInputLength < record.length;

					int l = indexOfInput + 1;
					for (int j = k; j < record.length; j++) {
						Object obj = arg0[l];
						if (obj instanceof String) {
							record[j] = new Text((String) obj);
						} else if (obj instanceof Short) {
							record[j] = new ShortWritable((Short) obj);
						}
						l++;
					}

					for (int i = 0; i < record.length; i++) {
						System.out.println("publishing row:" + record[i].toString());
						System.out.println("record cell class:" + record[i].getClass());
					}
					forward(record);
				}
			}
			stream.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String addURLParams(String url, Object[] arg0) {

		String replacedUrl = url.replaceAll("\"", "");
		// KAVITHA: an assumption here is that all the input column names are
		// passed in the same exact order
		// as they are called in the service URL. Its unfortunate but we have no
		// real sense of what input columns are in what order
		// so this assumption has to hold for now.

		for (int i = 1; i < inputColumns.size(); i++) {
			String regex = "\\|\\|[^|]*\\|\\|";
			replacedUrl = replacedUrl.replaceFirst(regex, arg0[indexOfInput + i].toString());
		}
		System.out.println("replaced url:" + replacedUrl);
		return replacedUrl;
	}

	public InputStream getResponseAsStream(String url, Object[] arg0) {
		InputStream stream = null;

		try {
			if (url.startsWith("file:")) {
				URL urlConn = new URL(url);
				stream = urlConn.openStream();
				return stream;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		HttpClient client = new HttpClient();

		try {

			if (method == httpMethod.GET) {
				if (!queryText.isEmpty()) {
					System.out.println(url);
				} else {
					url = addURLParams(url, arg0);
				}

				GetMethod getMethod = new GetMethod(url);
				if (queryText != null && !queryText.isEmpty()) {
					getMethod.addRequestHeader(HttpHeaders.ACCEPT, "application/sparql-results+xml");
				}
				client.executeMethod(getMethod);
				stream = getMethod.getResponseBodyAsStream();

			} else {
				PostMethod postMethod = new PostMethod(url);
				for (int i = indexOfInput + 1; i < inputColumns.size(); i++) {
					postMethod.setParameter(inputColumns.get(i),
							PrimitiveObjectInspectorFactory.writableStringObjectInspector
									.getPrimitiveJavaObject(arg0[i]));
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
	public List<List<String>> getXpathForColumns() {
		// TODO Auto-generated method stub
		return xPathForColumns;
	}

	public static void main(String[] args) throws Exception {
		test();
	}

	public static void test() throws Exception {
		WebServiceGetUDTF udtf = new WebServiceGetUDTF();
		udtf.method = httpMethod.GET;
		Object[] obj = new Object[1];
		udtf.queryText = " query=SELECT+*+WHERE+%7B%0A++%09%09%3Fx+%3Chttp%3A%2F%2Fwww.ibm.com%2Fhealth%2Fbjdiag%2FPM25TBL%23DATE%3E+%3Fdate%3B%0A++++%09++%3Chttp%3A%2F%2Fwww.ibm.com%2Fhealth%2Fbjdiag%2FPM25TBL%23VALUE%3E+%3Fvalue%3B%0A%09%09++%3Chttp%3A%2F%2Fwww.ibm.com%2Fhealth%2Fbjdiag%2FPM25TBL%23MONTH%3E+1%3B%0A%09%09++%3Chttp%3A%2F%2Fwww.ibm.com%2Fhealth%2Fbjdiag%2FPM25TBL%23YEAR%3E+2015%3B%0A%09%09++%3Chttp%3A%2F%2Fwww.ibm.com%2Fhealth%2Fbjdiag%2FPM25TBL%23DAY%3E+22%0A++++%7D";

		InputStream is = udtf.getResponseAsStream(
				"http://9.12.235.45:9091/openrdf-sesame/repositories/bjdiag2?query=SELECT+*+WHERE+%7B%0A++%09%09%3Fx+%3Chttp%3A%2F%2Fwww.ibm.com%2Fhealth%2Fbjdiag%2FPM25TBL%23DATE%3E+%3Fdate%3B%0A++++%09++%3Chttp%3A%2F%2Fwww.ibm.com%2Fhealth%2Fbjdiag%2FPM25TBL%23VALUE%3E+%3Fvalue%3B%0A%09%09++%3Chttp%3A%2F%2Fwww.ibm.com%2Fhealth%2Fbjdiag%2FPM25TBL%23MONTH%3E+1%3B%0A%09%09++%3Chttp%3A%2F%2Fwww.ibm.com%2Fhealth%2Fbjdiag%2FPM25TBL%23YEAR%3E+2015%3B%0A%09%09++%3Chttp%3A%2F%2Fwww.ibm.com%2Fhealth%2Fbjdiag%2FPM25TBL%23DAY%3E+22%0A++++%7D",
				obj);

		udtf.outputColumnNames = new HashMapFactory().make();
		udtf.outputColumnNames.put("date", new Integer(0));
		udtf.outputColumnNames.put("date_typ", new Integer(1));

		udtf.outputColumnNames.put("x", new Integer(2));
		udtf.outputColumnNames.put("x_typ", new Integer(3));

		udtf.outputColumnNames.put("value", new Integer(4));
		udtf.outputColumnNames.put("value_typ", new Integer(5));

		Map h = new HashMap<String, String>();
		h.put("xs", "http://www.w3.org/2001/XMLSchema");
		h.put("sparql", "http://www.w3.org/2005/sparql-results#");

		NamespaceResolver namespace = new NamespaceResolver(h);
		List<List<String>> xPathForEachColumn = new LinkedList<List<String>>();
		List l = new LinkedList<String>();
		l.add("date");
		l.add("./sparql:binding[./@name=\"date\"]");
		l.add("./sparql:binding[./@name=\"date\"]//@datatype");
		
		xPathForEachColumn.add(l);
		l = new LinkedList<String>();
		l.add("x");
		l.add("./sparql:binding[./@name=\"x\"]");
		l.add("10002");
		xPathForEachColumn.add(l);
		
		l = new LinkedList<String>();
		l.add("value");
		l.add("./sparql:binding[./@name=\"value\"]");
		l.add("./sparql:binding[./@name=\"value\"]//@datatype");
		xPathForEachColumn.add(l);
		udtf.parseResponse(is, namespace, "//sparql:result", xPathForEachColumn);

	}

}
