package com.ibm.research.rdf.store.utilities;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
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

import org.apache.hadoop.hive.serde2.io.ShortWritable;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.WritableConstantStringObjectInspector;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.ibm.research.rdf.store.runtime.service.types.TypeMap;
import com.ibm.wala.util.collections.Pair;

public interface WebServiceInterface {
	public Map<String, Integer> getOutputColumnNames();
	
	default public void handleOutputTypeSpecification(List<ObjectInspector> foi, String outputColSpec, List<String> l) {
		StringTokenizer tokenizer = new StringTokenizer(outputColSpec, ",");
		int indexOfCol = 0;
		while (tokenizer.hasMoreTokens()) {
			String outName = tokenizer.nextToken();
			getOutputColumnNames().put(outName, indexOfCol); 
			l.add(outName);
			indexOfCol++;
			getOutputColumnNames().put(outName + "_TYP", indexOfCol);
			l.add(outName + "_TYP");
			indexOfCol++;
			foi.add(PrimitiveObjectInspectorFactory.writableStringObjectInspector);			// output column name
			foi.add(PrimitiveObjectInspectorFactory.writableShortObjectInspector);			// output column type as a type map
		}
	}
	
	default public List<Object[]> parseResponse(InputStream is, NamespaceResolver namespace, String xPathForRows, List<List<String>> xPathForEachColumn) throws Exception {		
		
		System.out.println("In parse response");
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);

		DocumentBuilder builder = factory.newDocumentBuilder(); 

		Document doc = builder.parse(new InputSource(is));
	//	printDocument(doc);
		XPath xPath = XPathFactory.newInstance().newXPath();

	    xPath.setNamespaceContext(namespace);
		
		NodeList rows = (NodeList) xPath.compile(xPathForRows).evaluate(doc,
				XPathConstants.NODESET);

		List<Object[]> result = new LinkedList<Object[]>();
		for (int i = 0; i < rows.getLength(); i++) {
			Node row = rows.item(i);
			Object[] k = null;
			k = new Object[xPathForEachColumn.size() * 2];
			
			IntWritable indexValue = null;
			
			for (int j = 0; j < xPathForEachColumn.size(); j++) {
				List<String> p = xPathForEachColumn.get(j);
				String xPathForColValue = p.get(1);
				String xPathForColType = p.get(2);
				if (xPathForColValue.isEmpty()) {
					xPathForColValue = "./s:binding[./@name='" + p.get(0) + "']";
					xPathForColType = "./s:binding[./@name='" + p.get(0) + "']//@datatype";
				}
				Node column = (Node) xPath.compile(xPathForColValue).evaluate(row,
						XPathConstants.NODE);
		//		System.out.println("GOT HERE:" + xPathForColValue + " " + column);
				String value = column.getTextContent();
				String type = null;
				if (xPathForColType.equals(String.valueOf(TypeMap.IRI_ID))) {
					type = TypeMap.IRI_TYPE_IRI;
				} else if (xPathForColType.startsWith(".") || xPathForColType.startsWith("/")) {
					Node t = ((Node) xPath.compile(xPathForColType).evaluate(row,
							XPathConstants.NODE));
					if (t != null) {
						type = ((Node) xPath.compile(xPathForColType).evaluate(row,
							XPathConstants.NODE)).getTextContent();
					} else {
						type = xPathForColType;
					}
				} else {
					type = xPathForColType;
				}
				
				k[j * 2] = new Text(value.trim());
				ShortWritable st = new ShortWritable();
				st.set(getTypedValue(value, type));
				k[(j * 2) +1] =  st;
			}

			row.getParentNode().removeChild(row);
			result.add(k);
			
		}
		System.out.println("Parsed response");
//		br.close();
		is.close();
		
//		prettyPrint(result);
		return result;
	}
	
	public static void prettyPrint(List<Object[]> table) {
		for (Object[] row : table) {
			prettyPrintRecord(row);
		}
		System.out.println("*******************");
	}
	
	public static void prettyPrintRecord(Object[] row) {
		for (Object r : row) {
			if (r == null) {
				System.out.print(r + " ");
				continue;
			}
			System.out.print(r  + " ");
		}
		System.out.println("");
	}

	default short getTypedValue(String value, String colType) {
		if (colType.endsWith(TypeMap.DECIMAL_IRI)) {
			return TypeMap.DECIMAL_ID;
		} else if (colType.endsWith(TypeMap.DATE_IRI)) {
			return TypeMap.DATE_ID;
		} else if (colType.endsWith(TypeMap.DATETIME_IRI)) {
			return TypeMap.DATETIME_ID;
		} else if (colType.endsWith(TypeMap.INT_IRI)) {
			return TypeMap.INT_ID;
		} else if (colType.endsWith(TypeMap.INTEGER_IRI)) {
			return TypeMap.INTEGER_ID;
		} else if (colType.endsWith(TypeMap.SHORT_IRI)) {
			return TypeMap.SHORT_ID;
		} else if (colType.endsWith(TypeMap.FLOAT_IRI)) {
			return TypeMap.FLOAT_ID;
		} else if (colType.endsWith(TypeMap.DOUBLE_IRI)) {
			return TypeMap.DOUBLE_ID;
		} else if (colType.endsWith(TypeMap.BOOLEAN_IRI)) {
			return TypeMap.BOOLEAN_ID;
		} else if (colType.equals(TypeMap.IRI_TYPE_IRI)) {
			return TypeMap.IRI_ID;
		} else {
			return TypeMap.SIMPLE_LITERAL_ID;
		}
	}

	default void printDocument(Document doc) throws Exception {
		OutputFormat format = new OutputFormat(doc);
		format.setIndenting(true);
		XMLSerializer serializer = new XMLSerializer(System.out, format);
		serializer.serialize(doc);
	}
	
	default NamespaceResolver createNamespaces(String str) {
		StringTokenizer tokenizer = new StringTokenizer(str, ",");
		Map<String, String> namespaces = new HashMap<String, String>();
		while (tokenizer.hasMoreTokens()) {
			StringTokenizer toks = new StringTokenizer(tokenizer.nextToken(), "=");
			assert toks.countTokens() == 2;
			namespaces.put(toks.nextToken(), toks.nextToken());
		}
		return new NamespaceResolver(namespaces);
	}
	
	public List<List<String>> getXpathForColumns();

	default void addColXpathTuple(ObjectInspector[] parameters, int index) {
		PrimitiveObjectInspector colName = (PrimitiveObjectInspector) parameters[index];
		PrimitiveObjectInspector colXpath = (PrimitiveObjectInspector) parameters[index + 1];
		PrimitiveObjectInspector colTypeXpath = (PrimitiveObjectInspector) parameters[index + 2];
		assert colName instanceof WritableConstantStringObjectInspector && colXpath instanceof WritableConstantStringObjectInspector && colTypeXpath instanceof WritableConstantStringObjectInspector;
		List<String> l = new LinkedList<String>();
		l.add(((WritableConstantStringObjectInspector) colName).getWritableConstantValue().toString());
		l.add(((WritableConstantStringObjectInspector) colXpath).getWritableConstantValue().toString());
		l.add(((WritableConstantStringObjectInspector) colTypeXpath).getWritableConstantValue().toString());

		getXpathForColumns().add(l);
	}
}
