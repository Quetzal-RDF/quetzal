/******************************************************************************
 * Copyright (c) 2015 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *****************************************************************************/
 package com.ibm.research.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.coode.owlapi.rdf.rdfxml.RDFXMLRenderer;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.RDFOntologyFormat;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.rdf.util.RDFConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sparql.core.TriplePath;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.syntax.ElementPathBlock;
import com.hp.hpl.jena.sparql.syntax.ElementTriplesBlock;
import com.hp.hpl.jena.util.FileManager;
import com.ibm.research.owlql.ConjunctiveQuery;
import com.ibm.research.rdf.store.sparql11.model.Variable;
/**
 * 
 * @author achille
 *
 */
public class OCUtils {
	private static final Logger logger = LoggerFactory.getLogger(OCUtils.class);
	/**
	 * filesAndDirectoriesToConvertToNTriples.
	 * @param args
	 */
	public static void main(String[] args) {
		List<File> files = new ArrayList<File>();
		for (int i=0;i<args.length;i++) {
			File f = new File(args[i]);
			if (f.isDirectory()) {
				for (File fi: f.listFiles()) {
					if (fi.getName().endsWith(".rdf")) {
						files.add(fi);
					}
				}
			} else {
				files.add(f);
			}
		}
		
		for (File f: files) {
			try {
				File out = new File(f.getAbsolutePath()+".nt");
				logger.debug("Converting file {} to N-Triple file {}",f.getAbsolutePath(),out);
				convertToNtripleFormat(f, out);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	public static int nextAvailableSuffixVariable(Set<String> vars, String prefix) {
		
		int ret=-1;
		for (String var: vars) {
			String st = var;
			if (st.startsWith(prefix) && st.length()>prefix.length()) {
				String suffix = st.substring(prefix.length());
				try {
					int suffixInt = Integer.parseInt(suffix);
					ret = Math.max(ret, suffixInt);
				} catch (NumberFormatException e) {
					
				}
			}
		}
		ret++;
		return ret;
	}
	public static List<File> createRandomAboxAxiomPartitionsAsFiles(OWLOntology ont, int numberOfPartitions, long seed)
			throws OWLOntologyCreationException, IOException {
		List<OWLOntology> onts = createRandomAboxAxiomPartitions(ont, numberOfPartitions, seed);
		List<File> ret = new ArrayList<File>(onts.size());
		for (OWLOntology o: onts) {
			File tempFile = File.createTempFile("ont", ".rdf");
			saveRDFXML(o, tempFile);
			ret.add(tempFile);
			logger.debug("abox temp file: {}",tempFile.getAbsolutePath());
		}
		
		return ret;
	}
	
	public static File createOWLThingTypeDeclForIndividualsAsFile(OWLOntology ont) throws OWLOntologyCreationException, IOException {
		File tempFile = File.createTempFile("ont", ".ind.rdf");
		OWLOntology td = createOWLThingTypeDeclForIndividuals(ont);
		saveRDFXML(td, tempFile);
		logger.debug("owl thing type decl file: {}",tempFile.getAbsolutePath());
		return tempFile;
	}
	public static OWLOntology createOWLThingTypeDeclForIndividuals(OWLOntology ont) throws OWLOntologyCreationException, IOException  {
		OWLOntology ret = ont.getOntologyID()!=null && ont.getOntologyID().getOntologyIRI()!=null?
					ont.getOWLOntologyManager().createOntology(ont.getOntologyID().getOntologyIRI()):
					ont.getOWLOntologyManager().createOntology();
		
					OWLDataFactory fac = ret.getOWLOntologyManager().getOWLDataFactory();
		OWLClass owlThing = ret.getOWLOntologyManager().getOWLDataFactory().getOWLThing();
		for (OWLNamedIndividual ind: ont.getIndividualsInSignature()) {
			OWLNamedIndividual ni = fac.getOWLNamedIndividual(ind.getIRI());
			OWLAxiom ax = fac.getOWLClassAssertionAxiom(owlThing, ni);
			ret.getOWLOntologyManager().addAxiom(ret, ax);
		}
		return ret;
		
		
	}
	
	public static List<OWLOntology> createRandomAboxAxiomPartitions(OWLOntology ont, int numberOfPartitions, long seed) 
			throws OWLOntologyCreationException {
		List<OWLOntology> ret = new ArrayList<OWLOntology>(numberOfPartitions);
		for (int i=0;i<numberOfPartitions;i++) {
			OWLOntology o = ont.getOntologyID()!=null && ont.getOntologyID().getOntologyIRI()!=null?
					ont.getOWLOntologyManager().createOntology(ont.getOntologyID().getOntologyIRI()):
					ont.getOWLOntologyManager().createOntology();
			ret.add(o);
		}
		
		Random rdm = new Random(seed); 
		
		for (OWLAxiom ax: ont.getAxioms()) {
			if (OCUtils.isAboxAxiom(ax)) {
			/*	if (ax.getAxiomType().equals(AxiomType.DATA_PROPERTY_ASSERTION)
				|| ax.getAxiomType().equals(AxiomType.OBJECT_PROPERTY_ASSERTION)) {
					ret.get(numberOfPartitions-1).getOWLOntologyManager().addAxiom(ret.get(numberOfPartitions-1), ax);
				} else {*/
				int i = rdm.nextInt(numberOfPartitions);
				ret.get(i).getOWLOntologyManager().addAxiom(ret.get(i), ax);
				//}
			} else {
				for (int i=0;i<numberOfPartitions;i++) {
					ret.get(i).getOWLOntologyManager().addAxiom(ret.get(i), ax);
				}
			}
		}
		return ret;
		
	}
	public static OWLOntology load(String... inputFiles) throws OWLOntologyCreationException {
		if (inputFiles!=null) {
			File[] files = new File[inputFiles.length];
			for (int i=0;i<inputFiles.length;i++) {
				files[i] = new File(inputFiles[i]);
			}
			return load(files);
		} 
		return null;
	}
	public static OWLOntology load(File... inputFiles) throws OWLOntologyCreationException {
		OWLOntologyManager manager= OWLManager.createOWLOntologyManager();
		OWLOntology ont = manager.createOntology();
		for (File f:inputFiles) {
			manager= OWLManager.createOWLOntologyManager();
			manager.setSilentMissingImportsHandling(false);
			IRI documentIRI = IRI.create(f);
			OWLOntology ontology = manager.loadOntologyFromOntologyDocument(documentIRI);
			ont.getOWLOntologyManager().addAxioms(ont, ontology.getAxioms());
		}
		return ont;		
	}
	
    public static OWLOntology load(Model tbox) throws IOException, OWLOntologyCreationException{
    	File tempFile = File.createTempFile("tboxmodel", ".ttl");
    	FileOutputStream out = new FileOutputStream(tempFile);
    	tbox.write(out);
    	out.flush();
    	out.close();
    	return load(tempFile);
    }
	
	public static boolean isAboxAxiom(OWLAxiom ax) {
		return ax.getAxiomType().equals(AxiomType.DATA_PROPERTY_ASSERTION)
		|| ax.getAxiomType().equals(AxiomType.OBJECT_PROPERTY_ASSERTION)
		|| ax.getAxiomType().equals(AxiomType.CLASS_ASSERTION)
		|| ax.getAxiomType().equals(AxiomType.SAME_INDIVIDUAL)
		|| ax.getAxiomType().equals(AxiomType.DIFFERENT_INDIVIDUALS);
	}
	public static void saveRDFXML(OWLOntology ont, File out) throws IOException {
		saveRDFXML(ont, out, null);
	}
		
	public static void saveRDFXML(OWLOntology ont, File out, OWLOntologyFormat format) throws IOException {
		StringWriter sw = new StringWriter();
		if (format == null) {
			format =  ont.getOWLOntologyManager().getOntologyFormat(ont);
		}
		BufferedWriter bufw = new BufferedWriter(sw);//new FileWriter(newKBFile));
		if (ont.getOWLOntologyManager().getOntologyFormat(ont) instanceof RDFOntologyFormat) {
			((RDFOntologyFormat) ont.getOWLOntologyManager().getOntologyFormat(ont) ).setAddMissingTypes(false);
		}
		RDFXMLRenderer render = new RDFXMLRenderer(ont.getOWLOntologyManager(),ont,bufw, format);
		render.render();
		bufw.flush();
		String text = sw.toString();
		text = text.replace("NamedIndividual","Thing");
		bufw.close();
		bufw = new BufferedWriter(new FileWriter(out));
		bufw.write(text);
		bufw.flush();
		bufw.close();
	}
	
	public static Map<String, String> getPrefix2NamespaceMap(File owlFile) 
	throws ParserConfigurationException, SAXException, IOException  {
		SAXParserFactory fac = SAXParserFactory.newInstance();
		fac.setNamespaceAware(true);
		SAXParser parser = fac.newSAXParser();
		
		final Map<String, String> ret = new HashMap<String, String>();
		parser.parse(owlFile,new DefaultHandler(){

			
			public void endPrefixMapping(String prefix) throws SAXException {
				
			}

			
			public void startPrefixMapping(String prefix, String uri)
					throws SAXException {
				
				assert !ret.containsKey(prefix) : prefix +" : "+uri+"\n"+ret;
				ret.put(prefix, uri);
				
			}
			
		});
		return ret;
		/*OWLOntology ont = load(owlFile);
		return new HashMap<String, String>(ont.getOWLOntologyManager()
				.getOntologyFormat(ont).asPrefixOWLOntologyFormat().getPrefixName2PrefixMap());*/
		
	}
	

	/**
	 * reads an input stream consisting of semicolon separated SPARQL queries, and returns the list of SPARQL 
	 * queries as strings.
	 * @param ins
	 * @return
	 * @throws IOException
	 */
	public static List<String> readQueries(Reader ins) throws IOException {
	
		BufferedReader in = new BufferedReader(ins);
		StringBuffer buf = new StringBuffer();
		String line = null;
		while ((line=in.readLine())!=null) {
			if (!line.startsWith("#")) {
				buf.append(line+"\n");
			}
		}
		StringTokenizer t = new StringTokenizer(buf.toString(),";");
		List<String> ret =  new ArrayList<String>();
		while (t.hasMoreTokens()) {
			String query = t.nextToken().trim();
			if (!"".equals(query)) {
				ret.add(query);
			}
		}
		return ret;
		
	}
	public static List<String> readQueries(File... queryFiles) throws IOException {
		List<String> ret = new ArrayList<String>();
		for (File qf: queryFiles) {
			FileReader r = new FileReader(qf);
			ret.addAll(readQueries(r));
			r.close();
		}
		return ret;
	}
	

	public static ConjunctiveQuery parse(String sparqlQuery) {
		try {
			Query query= QueryFactory.create(sparqlQuery, Syntax.syntaxSPARQL_11);
			if (!query.isSelectType()) {
				throw new RuntimeException("Non select query: "+sparqlQuery.toString());
			}
			return new ConjunctiveQuery(query);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void convertToNtripleFormat(File in, File out) throws IOException {
		Model model = FileManager.get().loadModel(in.getAbsolutePath());
		BufferedWriter bw = new BufferedWriter(new FileWriter(out));
		model.write(bw, "N-TRIPLE");
		bw.close();
	}
	public static List<ConjunctiveQuery> loadConjunctiveQueries(File queryFile) throws IOException {
		List<ConjunctiveQuery> ret = new ArrayList<ConjunctiveQuery>();
		Reader in = new FileReader(queryFile);
		for (String queryText: SPARQLFileParser.readQueries(in)) {
			queryText= queryText.trim();
			if (queryText.startsWith("#")) {
				continue;
			}
			Query query= QueryFactory.create(queryText, Syntax.syntaxSPARQL_11);
			if (!query.isSelectType()) {
				logger.debug("Ignoring non select query: {}",query.toString());
				continue;
			}
			logger.debug("Query: {}"+query);
			ret.add(new ConjunctiveQuery(query));
		}
		in.close();
		return ret;
	}
	public static List<Query> loadQueries(File queryFile) throws IOException {
		List<Query> ret = new ArrayList<Query>();
		Reader in = new FileReader(queryFile);
		for (String queryText: SPARQLFileParser.readQueries(in)) {
			queryText= queryText.trim();
			if (queryText.startsWith("#")) {
				continue;
			}
			Query query= QueryFactory.create(queryText, Syntax.syntaxSPARQL_11);
			if (!query.isSelectType()) {
				logger.debug("Ignoring non select query: {}",query.toString());
				continue;
			}
			logger.debug("Query: {}"+query);
			ret.add(query);
		}
		in.close();
		return ret;
	}
	public static Set<ConjunctiveQuery> getMembershipQuery(OWLOntology ont) {
		Set<ConjunctiveQuery> ret = new HashSet<ConjunctiveQuery>();
		for (OWLClass owlclass: ont.getClassesInSignature(true)) {
			ElementTriplesBlock p = new ElementTriplesBlock();
			String x = "x";
			Node sub = Node.createVariable(x);
			Node pred = Node.createURI(RDFConstants.RDF_TYPE);
			Node obj = Node.createURI(owlclass.getIRI().toString());
			Triple qt = new Triple(sub, pred, obj);
			p.getPattern().add(qt);
			ConjunctiveQuery cq = new ConjunctiveQuery();
			cq.setQueryPattern(p);
			cq.addResultVar(x);
			cq.setDistinct(true);
			ret.add(cq);
		}
		return ret;
	}
	public static  List<File> getAllFiles(File inFileOrDir, boolean recurseDir) {
		final List<File> ret = new LinkedList<File>();
		Stack<File> stack = new Stack<File>();
		stack.add(inFileOrDir);
		while (!stack.isEmpty()) {
			File f = stack.pop();
			if (f.isFile()) {
				ret.add(f);
			} else  {
				assert f.isDirectory() : f;
				if (recurseDir) {
					for (File cf: f.listFiles()) {
						stack.add(cf);
					}
				}
			}
		}
		return ret;
	}
	
	public static ElementTriplesBlock toTriples(ElementPathBlock pb) {
		ElementTriplesBlock ret = new ElementTriplesBlock(); 
		for (TriplePath tp :pb.getPattern().getList()) {
			if (tp.isTriple()) {
				ret.addTriple(tp.asTriple());
			} else {
				throw new RuntimeException("Path query not supported");
			}
		} 
		return ret;
	}
	public static Set<String> getVars(Set<Var> vars) {
		Set<String> ret = new HashSet<String>();
		for (Var v: vars) {
			ret.add(v.getName());
		}
		return ret;
	}
	public static Set<String> getVariables(Set<Variable> vars) {
		Set<String> ret = new HashSet<String>();
		for (Variable v: vars) {
			ret.add(v.getName());
		}
		return ret;
	}


}
