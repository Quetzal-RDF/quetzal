package com.ibm.research.rdf.store.sparql11.model;

import java.util.ArrayList;
import java.util.List;

import com.ibm.research.rdf.store.sparql11.XTree;

public class FunctionBase {

	public enum ServiceKind { GET, POST };
	
	protected IRI name;
	protected List<Variable> inVariables;
	protected List<Variable> outVariables;

	private ServiceKind requestKind;
	
	public FunctionBase() {
		name = null;
		inVariables = new ArrayList<Variable>();
		outVariables = new ArrayList<Variable>();
	}

	public ServiceKind kind() {
		return requestKind;
	}
	
	public void setGet() {
		requestKind = ServiceKind.GET;
	}
	
	public void setPost() {
		requestKind = ServiceKind.POST;
	}

	public void setName(IRI s) {
		name = s;
	}

	public void setName(XTree s) {
		name = new IRI(s.getText());
	}

	public IRI getName() {
		return name;
	}

	public void addInVar(Variable v) {
		inVariables.add(v);
	}

	public void addInVar(String v) {
		inVariables.add(new Variable(v));
	}

	public List<Variable> getInVariables() {
		return inVariables;
	}

	public void addOutVar(Variable v) {
		outVariables.add(v);
	}

	public void addOutVar(String v) {
		outVariables.add(new Variable(v));
	}

	public List<Variable> getOutVariables() {
		return outVariables;
	}

}