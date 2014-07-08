PREFIX dc: http://purl.org/dc/terms/
PREFIX wrapper: http://jazz.net/xmlns/alm/rm/Reference/v0.1#
PREFIX b: http://jazz.net/xmlns/alm/rm/Base/v0.1#
PREFIX jazz: http://jazz.net/xmlns/foundation/1.0/
PREFIX att: http://schema.ibm.com/rdm/2008/Attribute#
PREFIX nav: http://com.ibm.rdm/navigation#

SELECT ?resource, ?created, ?modified, ?creator, ?resourcecontextid, ?tag, ?contributor, ?name, ?namePredicate, ?parent, ?reference, ?format, ?formatPredicate 
WHERE {{{
?resource <http://com.ibm.rdm/navigation#parent> <https://front.side/rdm/folders/folder1> .
?resource <http://purl.org/dc/terms/isPartOf> <https://front.side/rdm/resources> .
?resource <http://purl.org/dc/terms/created> ?created .
?resource <http://purl.org/dc/terms/modified> ?modified .
?resource <http://purl.org/dc/terms/creator> ?creator .
?resource <http://jazz.net/xmlns/foundation/1.0/resourceContextId> ?resourcecontextid .
?resource <http://purl.org/dc/terms/contributor> ?contributor .
?resource <http://com.ibm.rdm/navigation#parent> ?parent .
?resource ?formatPredicate ?format
FILTER (?formatPredicate = http://purl.org/dc/terms/format) OR (?formatPredicate = http://jazz.net/xmlns/alm/rm/Reference/v0.1#contentType)

}
 OPTIONAL 
{
?resource <http://com.ibm.rdm/navigation#tag> ?tag
}
}
 OPTIONAL 
{
?resource <http://jazz.net/xmlns/alm/rm/Reference/v0.1#reference> ?reference
}
}