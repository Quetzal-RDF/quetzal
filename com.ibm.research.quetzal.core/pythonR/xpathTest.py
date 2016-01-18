from lxml import etree
import random


def extractPost(funcData):
    root = etree.fromstring(funcData)
    rows = root.xpath('//row')
    row = next(iter(rows), None)
    colNames = []
    for col in row:
        colNames.append(col.tag)

    result = '<?xml version="1.0"?>'
    result += '<data>'

    for row in rows:
        result += "<row>"
        k = 0;
        for col in row:
            result += "<" + colNames[k] + ">" + col.text + "</" + colNames[k] + ">"
            k+=1
        result += "<sum>%d</sum>" % random.randint(0,100)
        result += "</row>"
    result += "</data>"
    return result

def extractSMILES():
    root = etree.parse("../../drugbank.xml")
    rows = root.xpath('/x:drugbank/x:drug', namespaces={'x': 'http://www.drugbank.ca'})
    for row in rows:
        print row.xpath('.//x:property[./x:kind/text()="SMILES"]/x:value/text()', namespaces={'x': 'http://www.drugbank.ca'})
 #       print row.xpath('./x:calculated_properties/x:property[./x:kind/text()="SMILES"]/x:value/text()', namespaces={'x': 'http://www.drugbank.ca'})

def extractSMILES(drugName):
    root = etree.parse("../../drugbank.xml")
    rows = root.xpath('/x:drugbank/x:drug[./x:name/text()="'+ drugName + '"]', namespaces={'x': 'http://www.drugbank.ca'})

    result = '<?xml version="1.0"?>'
    result += '<data xmlns="http://www.drugbank.ca">'

    for row in rows:
        smiles = row.xpath('.//x:property[./x:kind/text()="SMILES"]/x:value/text()', namespaces={'x': 'http://www.drugbank.ca'})
        result += "<row>" + "<drug>" + drugName+ "</drug> <smiles>" + smiles[0] + "</smiles> </row>"
    result += '</data>'
    return result

def extractDrugNames():
    root = etree.parse("../../drugbank.xml")
    rows = root.xpath('/x:drugbank/x:drug[./x:transporters/x:transporter/x:polypeptide/x:external-identifiers/x:external-identifier/x:resource/text()="UniProtKB"]', namespaces={'x': 'http://www.drugbank.ca'})

    result = '<?xml version="1.0"?>'
    result += '<data xmlns="http://www.drugbank.ca">'
    for row in rows:
        drug = row.xpath('./x:name/text()', namespaces={'x': 'http://www.drugbank.ca'})
        result += "<row>" + "<drug>" + drug[0] + "</drug>  </row>"
    result += '</data>'

    return result


def extractTransporters(drugName):
    root = etree.parse("../../drugbank.xml")
    rows = root.xpath('/x:drugbank/x:drug[./x:transporters/x:transporter/x:polypeptide/x:external-identifiers/x:external-identifier/x:resource/text()="UniProtKB"][./x:name/text()="'+ drugName + '"]', namespaces={'x': 'http://www.drugbank.ca'})

    result = '<?xml version="1.0"?>'
    result += '<data xmlns="http://www.drugbank.ca">'
    for row in rows:
        transporters = row.xpath('./x:transporters/x:transporter', namespaces={'x': 'http://www.drugbank.ca'})
        for transporter in transporters:
            actions = transporter.xpath('./x:actions/x:action', namespaces={'x': 'http://www.drugbank.ca'})
            for action in actions:
                act = action.xpath('./text()', namespaces={'x': 'http://www.drugbank.ca'})
                id = transporter.xpath('./x:polypeptide/x:external-identifiers/x:external-identifier[./x:resource/text()="UniProtKB"]/x:identifier/text()', namespaces={'x': 'http://www.drugbank.ca'})
                result += "<row>" + "<drug>" + drugName + "</drug> <id>" + id[0] + "</id> <action>" + act[0] + "</action> </row>"
    result += '</data>'

    return result

def extractTargets(drugName):
    root = etree.parse("../../drugbank.xml")
    rows = root.xpath('/x:drugbank/x:drug[./x:targets/x:target/x:polypeptide/x:external-identifiers/x:external-identifier/x:resource/text()="UniProtKB"][./x:name/text()="'+ drugName + '"]', namespaces={'x': 'http://www.drugbank.ca'})

    result = '<?xml version="1.0"?>'
    result += '<data xmlns="http://www.drugbank.ca">'
    for row in rows:
        targets = row.xpath('./x:targets/x:target', namespaces={'x': 'http://www.drugbank.ca'})
        for transporter in transporters:
            actions = transporter.xpath('./x:actions/x:action', namespaces={'x': 'http://www.drugbank.ca'})
            for action in actions:
                act = action.xpath('./text()', namespaces={'x': 'http://www.drugbank.ca'})
                id = transporter.xpath('./x:polypeptide/x:external-identifiers/x:external-identifier[./x:resource/text()="UniProtKB"]/x:identifier/text()', namespaces={'x': 'http://www.drugbank.ca'})
                result += "<row>" + "<drug>" + drugName + "</drug> <id>" + id[0] + "</id> <action>" + act[0] + "</action> </row>"
    result += '</data>'

    return result

def extractDrugBank():
    root = etree.parse("db_small.xml")
    rows = root.xpath('/x:drugbank/x:drug[./x:transporters/x:transporter/x:polypeptide/x:external-identifiers/x:external-identifier/x:resource/text()="UniProtKB"]', namespaces={'x': 'http://www.drugbank.ca'})

    result = '<?xml version="1.0"?>'
    result += '<data xmlns="http://www.drugbank.ca">'
    for row in rows:
        drug = row.xpath('./x:name/text()', namespaces={'x': 'http://www.drugbank.ca'})
        transporters = row.xpath('./x:transporters/x:transporter', namespaces={'x': 'http://www.drugbank.ca'})
        for transporter in transporters:
            actions = transporter.xpath('./x:actions/x:action', namespaces={'x': 'http://www.drugbank.ca'})
            for action in actions:
                act = action.xpath('./text()', namespaces={'x': 'http://www.drugbank.ca'})
                id = transporter.xpath('./x:polypeptide/x:external-identifiers/x:external-identifier[./x:resource/text()="UniProtKB"]/x:identifier/text()', namespaces={'x': 'http://www.drugbank.ca'})
                result += "<row>" + "<drug>" + drug[0] + "</drug> <id>" + id[0] + "</id> <action>" + act[0] + "</action> </row>"
    result += '</data>'

    return result

print extractSMILES('Ibuprofen')
print extractTransporters('Ibuprofen')
print extractDrugNames()
