CONNECT TO RDFSTORE;

SELECT * FROM TABLE(rdf_gsxx('folding', 'graph1', 'wn:instances/synset-A_battery-noun-1'));
SELECT * FROM TABLE(rdf_gxxv('folding', 'graph2', 'wn:instances/synset-Europe-noun-1'));
SELECT * FROM TABLE(rdf_gsxv('folding', 'graph1', 'wn:instances/synset-Iceland-noun-1', 'wn:instances/synset-Europe-noun-1'));
SELECT * FROM TABLE(rdf_xspv('folding', 'wn:instances/synset-Iceland-noun-1', 'wn:schema/partMeronymOf', 'wn:instances/synset-Europe-noun-1'));

SELECT * FROM TABLE(rdf_sameSubject2('folding', NULL, 'wn:instances/synset-web-noun-7', 'wn:schema/hyponymOf', NULL, NULL, NULL));
SELECT * FROM TABLE(rdf_sameObject2('folding', NULL, 'wn:instances/synset-Afghanistan-noun-1', 'wn:schema/partMeronymOf', NULL, NULL, 'wn:instances/synset-Jalalabad-noun-1'));
---bogus version of the one above (no results)
SELECT * FROM TABLE(rdf_sameObject2('folding', NULL, 'wn:instances/synset-Afghanistan-noun-1', 'wn:schema/partMeronymOf', NULL, NULL, 'wn:instances/synset-JJJJJJ-noun-1'));
SELECT * FROM TABLE(rdf_sameObject4('folding', NULL, 'wn:instances/synset-Afghanistan-noun-1', 'wn:schema/partMeronymOf', NULL, NULL, NULL, 'wn:schema/partMeronymOf', 'wn:instances/synset-Jalalabad-noun-1', NULL, 'wn:instances/synset-Hindu_Kush-noun-1'));

SELECT * FROM TABLE(rdf_subjectChain2('folding', NULL, 'wn:instances/synset-warhorse-noun-1', NULL, NULL, NULL));
SELECT * FROM TABLE(rdf_objectChain2('folding', NULL, 'wn:instances/synset-art-noun-1', NULL, NULL, NULL));