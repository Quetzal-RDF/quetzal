package com.ibm.research.rdf.store.utilities.io;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ibm.research.rdf.store.sparql11.model.BlankNodeVariable;
import com.ibm.research.rdf.store.sparql11.model.Constant;
import com.ibm.research.rdf.store.sparql11.model.IRI;
import com.ibm.research.rdf.store.sparql11.model.QueryTripleTerm;
import com.ibm.research.rdf.store.sparql11.model.StringLiteral;
import com.ibm.research.rdf.store.sparql11.model.Variable;
import com.ibm.wala.util.collections.EmptyIterator;
import com.ibm.wala.util.collections.HashMapFactory;
import com.ibm.wala.util.collections.NonNullSingletonIterator;

public class SparqlXmlResultReader implements SparqlSelectResult {

	private final Document doc;

	private final List<Variable> vars;

	private final boolean isAskQuery;

	public SparqlXmlResultReader(InputStream is) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		doc = docBuilder.parse(is);
		doc.getDocumentElement().normalize();

		vars = new LinkedList<Variable>();
		NodeList header = doc.getElementsByTagName("head");
		assert header.getLength() == 1;
		Node hn = header.item(0);
		assert hn instanceof Element;
		NodeList varNames = ((Element)hn).getElementsByTagName("variable");
		for (int i = 0 ; i < varNames.getLength(); i++) {
			Node n = varNames.item(i);
			assert n instanceof Element;
			String varName = ((Element)n).getAttribute("name");
			Variable v = new Variable(varName);
			vars.add(v);
		}

		NodeList askResult = doc.getElementsByTagName("boolean");
		if (askResult.getLength() == 1) {
			isAskQuery = true;
		} else {
			assert askResult.getLength() == 0;
			isAskQuery = false;
		}

	}

	@Override
	public Iterator<Variable> variables() {
		return vars.iterator();
	}

	@Override
	public Iterator<Row> rows() {
		if (isAskQuery) {
			NodeList askResult = doc.getElementsByTagName("boolean");
			if (Boolean.parseBoolean(askResult.item(0).getTextContent())) {
				return new NonNullSingletonIterator<Row>(new Row() {
					@Override
					public QueryTripleTerm get(Variable v) {
						return null;
					}

					@Override
					public Iterator<Variable> variables() {
						return vars.iterator();
					}
				});
			} else {
				return EmptyIterator.instance();
			}
		} else {
			final NodeList listOfResults = doc.getElementsByTagName("result");

			return new Iterator<Row>() {
				private int i = 0;

				@Override
				public boolean hasNext() {
					return i < listOfResults.getLength();
				}

				@Override
				public Row next() {
					assert hasNext();
					Node n = listOfResults.item(i++);
					assert n instanceof Element;
					final NodeList bindings = ((Element)n).getElementsByTagName("binding");
					return new Row() {
						@Override
						public boolean equals(Object o) {
							if (o instanceof Row) {
								return check(this, (Row)o) && check((Row)o, this);
							}
							
							return false;
						}
						
						private boolean check(Row me, Row o) {
							Iterator<Variable> vs = me.variables();
							while (vs.hasNext()) {
								Variable v = vs.next();
								if (! me.get(v).equals(o.get(v))) {
									return false;
								}
							}
							return true;
						}
						
						@Override
						public QueryTripleTerm get(Variable v) {
							assert vars.contains(v);
							for(int i = 0; i < bindings.getLength(); i++) {
								Node n = bindings.item(i);
								assert n instanceof Element;
								if (v.getName().equals(((Element)n).getAttribute("name"))) {
									NodeList uris = ((Element)n).getElementsByTagName("uri");
									assert uris.getLength() <= 1;
									if (uris.getLength() == 1) {
										return new QueryTripleTerm(new IRI(uris.item(0).getTextContent()));
									}

									NodeList blanks = ((Element)n).getElementsByTagName("bnode");
									assert blanks.getLength() <= 1;
									if (blanks.getLength() == 1) {
										return new QueryTripleTerm(new BlankNodeVariable(blanks.item(0).getTextContent()));
									}

									NodeList literals = ((Element)n).getElementsByTagName("literal");
									assert literals.getLength() <= 1;
									if (literals.getLength() == 1) {
										Element lit = (Element) literals.item(0);
										String type = lit.getAttribute("datatype");
										String lang = lit.getAttribute("language");
										StringLiteral sl = new StringLiteral(lit.getTextContent());
										if (lang != null) {
											sl.setLanguage(lang);
										}
										if (type != null) {
											sl.setType(new IRI(type));
										}
										return new QueryTripleTerm(new Constant(sl));
									}
									assert false : "unknown data type " + n.getTextContent();
								}
							}
							return null;
						}

						@Override
						public Iterator<Variable> variables() {
							return vars.iterator();
						}	
					};
				}

				@Override
				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		}
	}
	
	public static String getParamsString(Map<String, String> params) throws UnsupportedEncodingException {
		StringBuilder result = new StringBuilder();

		for (Map.Entry<String, String> entry : params.entrySet()) {
			result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
			result.append("=");
			result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
			result.append("&");
		}

		String resultString = result.toString();
		return resultString.length() > 0
				? resultString.substring(0, resultString.length() - 1)
						: resultString;
	}

	public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
		URL url = new URL(args[0]);
		System.err.println(url);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		con.setDoInput(true);
		con.setDoOutput(true);

		BufferedReader queryReader = new BufferedReader(new FileReader(args[1]));
		String line;
		Map<String, String> parameters = HashMapFactory.make();
		String query = "";
		while ((line = queryReader.readLine()) != null) {
			query += line + "\n";
		}
		System.err.println(query);
		queryReader.close();
		
		parameters.put("query", query);		 
		parameters.put("format", "xml");		 
				
		DataOutputStream out = new DataOutputStream(con.getOutputStream());
		out.writeBytes(getParamsString(parameters));
		out.flush();
		out.close();
		
		int status = con.getResponseCode();
		if (status == HttpURLConnection.HTTP_OK) {
			SparqlXmlResultReader x = new SparqlXmlResultReader(con.getInputStream());
			x.rows().forEachRemaining(r -> {
				x.variables().forEachRemaining(v -> { 
					System.err.print(v.getName() + " -> " + r.get(v) + " ");
				});
				System.err.println();
			});
		} else {
			System.err.println(con.getResponseCode());
		}
	}
}
