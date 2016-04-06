package com.ibm.research.rdf.store.utilities;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

public class NamespaceResolver implements NamespaceContext {

	private Map<String, String> namespaces = new HashMap<String, String>();
	
	public NamespaceResolver(Map<String, String> namespaces) {
		this.namespaces = namespaces;
	}
    public String getNamespaceURI(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("No prefix provided!");
        } else if (namespaces.containsKey(prefix)) {
            return namespaces.get(prefix);
        } else {
            return XMLConstants.DEFAULT_NS_PREFIX;
        }
    }

    public String getPrefix(String namespaceURI) {
        // Not needed in this context.
        return null;
    }

    public Iterator getPrefixes(String namespaceURI) {
        // Not needed in this context.
        return null;
    }
    
    public String toString() {
    	return namespaces.toString();
    }

}