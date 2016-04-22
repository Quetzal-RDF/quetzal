from lxml import etree
from xml.sax.saxutils import escape
import random

class xPathTest(object):

    def __init__(self, input):
        root = etree.parse(input)
        self.drugsToTargets = {}
        self.drugsToTransporters = {}
        self.drugsToSMILES = {}
        self.extractDrugsToDict(root, '/x:drugbank/x:drug[./x:targets/x:target/x:polypeptide/x:external-identifiers/x:external-identifier/x:resource/text()="UniProtKB"][./x:groups/x:group/text()="approved"]', './x:targets/x:target/x:polypeptide/x:external-identifiers/x:external-identifier[./x:resource/text()="UniProtKB"]/x:identifier/text()', self.drugsToTargets)
        self.extractDrugsToDict(root, '/x:drugbank/x:drug[./x:transporters/x:transporter/x:polypeptide/x:external-identifiers/x:external-identifier/x:resource/text()="UniProtKB"][./x:groups/x:group/text()="approved"]', './x:transporters/x:transporter/x:polypeptide/x:external-identifiers/x:external-identifier[./x:resource/text()="UniProtKB"]/x:identifier/text()', self.drugsToTransporters)
        self.extractDrugsToDict(root, '/x:drugbank/x:drug[./x:groups/x:group/text()="approved"]', './/x:property[./x:kind/text()="SMILES"]/x:value/text()', self.drugsToSMILES)
 

    def sumFunc(self, text):
        return "<sum>%d</sum>" % random.randint(0,100)

    def extractPost(self, funcData, func):
        print(funcData)
        root = etree.fromstring(funcData)
        rows = root.xpath('//row')
        row = next(iter(rows), None)
        assert len(row) == 1
        result = '<?xml version="1.0"?>'
        result +='<data xmlns="http://www.drugbank.ca">'

        for row in rows:
            for val in func(row[0].text.strip()):
                print(val)
                if val == "":
                    continue
                result += "<row>"
                result += "<" + row[0].tag + ">" + row[0].text + "</" + row[0].tag + ">" + val
                result += "</row>"
                print(result)
        result += "</data>"
        return result

    def transportersFunc(self, text):
        print("searching for#" + text + "#")
        if text not in self.drugsToTransporters:
            yield "" 
        else:
            for id in self.drugsToTransporters[text]:
                yield "<transporter>%s</transporter>" % id

    def SMILESFunc(self, text):
        if text not in self.drugsToSMILES:
            yield "" 
        else:
            for id in self.drugsToSMILES[text]:
                yield "<smiles>%s</smiles>" % id

    def targetsFunc(self, text):
        if text not in self.drugsToTargets:
            yield "" 
        else:
            for id in self.drugsToTargets[text]:
                yield "<target>%s</target>" % id


    def extractSMILES(self):
        rows = self.root.xpath('/x:drugbank/x:drug', namespaces={'x': 'http://www.drugbank.ca'})
        for row in rows:
            print(row.xpath('.//x:property[./x:kind/text()="SMILES"]/x:value/text()', namespaces={'x': 'http://www.drugbank.ca'}))
     #       print row.xpath('./x:calculated_properties/x:property[./x:kind/text()="SMILES"]/x:value/text()', namespaces={'x': 'http://www.drugbank.ca'})

    def extractSMILES(self, drugName):
        rows = self.root.xpath('/x:drugbank/x:drug[./x:name/text()="'+ drugName + '"]', namespaces={'x': 'http://www.drugbank.ca'})

        result = '<?xml version="1.0"?>'
        result += '<data xmlns="http://www.drugbank.ca">'

        for row in rows:
            smiles = row.xpath('.//x:property[./x:kind/text()="SMILES"]/x:value/text()', namespaces={'x': 'http://www.drugbank.ca'})
            if (len(smiles) > 0):
                result += "<row>" + "<drug>" + drugName+ "</drug> <smiles>" + smiles[0] + "</smiles> </row>"
        result += '</data>'
        return result

    def extractDrugNames(self):
        rows = self.drugsToTransporters.keys()

        result = '<?xml version="1.0"?>'
        result += '<data xmlns="http://www.drugbank.ca">'
        for row in rows:
            result += "<row>" + "<drug>" + row + "</drug>  </row>"
        result += '</data>'

        return result

    def extractDrugNamesAndGroups(self):
        rows = self.root.xpath('/x:drugbank/x:drug[./x:transporters/x:transporter/x:polypeptide/x:external-identifiers/x:external-identifier/x:resource/text()="UniProtKB"]', namespaces={'x': 'http://www.drugbank.ca'})

        result = '<?xml version="1.0"?>'
        result += '<data xmlns="http://www.drugbank.ca">'
        for row in rows:
            drug = row.xpath('./x:name/text()', namespaces={'x': 'http://www.drugbank.ca'})
            group = row.xpath('./x:groups/x:group/text()', namespaces={'x': 'http://www.drugbank.ca'})
            for g in group:
                result += "<row>" + "<drug>" + drug[0] + "</drug> <group>" + g + "</group> </row>"
        result += '</data>'

        return result


    def extractTransporters(self, drugName):
        rows = self.root.xpath('/x:drugbank/x:drug[./x:transporters/x:transporter/x:polypeptide/x:external-identifiers/x:external-identifier/x:resource/text()="UniProtKB"][./x:name/text()="'+ drugName + '"]', namespaces={'x': 'http://www.drugbank.ca'})

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

    def extractTargets(self, drugName):
        rows = self.root.xpath('/x:drugbank/x:drug[./x:targets/x:target/x:polypeptide/x:external-identifiers/x:external-identifier/x:resource/text()="UniProtKB"][./x:name/text()="'+ drugName + '"]', namespaces={'x': 'http://www.drugbank.ca'})

        result = '<?xml version="1.0"?>'
        result += '<data xmlns="http://www.drugbank.ca">'
        for row in rows:
            targets = row.xpath('./x:targets/x:target', namespaces={'x': 'http://www.drugbank.ca'})
            for target in targets:
                actions = target.xpath('./x:actions/x:action', namespaces={'x': 'http://www.drugbank.ca'})
                for action in actions:
                    act = action.xpath('./text()', namespaces={'x': 'http://www.drugbank.ca'})
                    id = target.xpath('./x:polypeptide/x:external-identifiers/x:external-identifier[./x:resource/text()="UniProtKB"]/x:identifier/text()', namespaces={'x': 'http://www.drugbank.ca'})
                    if len(id) > 0 and len(act) > 0:
                        result += "<row>" + "<drug>" + drugName + "</drug> <id>" + id[0] + "</id> <action>" + act[0] + "</action> </row>"
        result += '</data>'

        return result

    def extractDrugBankTargetsInNT(self):
        rows = self.root.xpath('/x:drugbank/x:drug[./x:targets/x:target/x:polypeptide/x:external-identifiers/x:external-identifier/x:resource/text()="UniProtKB"]', namespaces={'x': 'http://www.drugbank.ca'})

        result = '<?xml version="1.0"?>'
        result += '<data xmlns="http://www.drugbank.ca">'
        for row in rows:
            drug = row.xpath('./x:name/text()', namespaces={'x': 'http://www.drugbank.ca'})
            targets= row.xpath('./x:targets/x:target', namespaces={'x': 'http://www.drugbank.ca'})
            for target in targets:
                actions = target.xpath('./x:actions/x:action', namespaces={'x': 'http://www.drugbank.ca'})
                for action in actions:
                    act = action.xpath('./text()', namespaces={'x': 'http://www.drugbank.ca'})
                    id = target.xpath('./x:polypeptide/x:external-identifiers/x:external-identifier[./x:resource/text()="UniProtKB"]/x:identifier/text()', namespaces={'x': 'http://www.drugbank.ca'})
                    if len(id) > 0 and len(act) > 0:
                        print("<http://www.research.ibm.com/Tiresias/entity/bio/DRUG/" + drug[0] + "> <http://www.research.ibm.com/Tiresias/prop/target> <http://purl.uniprot.org/uniprot/" + id[0] + "> .")


    def extractDrugBankTargets(self):
        rows = self.root.xpath('/x:drugbank/x:drug[./x:targets/x:target/x:polypeptide/x:external-identifiers/x:external-identifier/x:resource/text()="UniProtKB"]', namespaces={'x': 'http://www.drugbank.ca'})

        result = '<?xml version="1.0"?>'
        result += '<data xmlns="http://www.drugbank.ca">'
        for row in rows:
            drug = row.xpath('./x:name/text()', namespaces={'x': 'http://www.drugbank.ca'})
            targets= row.xpath('./x:targets/x:target', namespaces={'x': 'http://www.drugbank.ca'})
            for target in targets:
                actions = target.xpath('./x:actions/x:action', namespaces={'x': 'http://www.drugbank.ca'})
                for action in actions:
                    act = action.xpath('./text()', namespaces={'x': 'http://www.drugbank.ca'})
                    id = target.xpath('./x:polypeptide/x:external-identifiers/x:external-identifier[./x:resource/text()="UniProtKB"]/x:identifier/text()', namespaces={'x': 'http://www.drugbank.ca'})
                    if len(id) > 0 and len(act) > 0:
                        result += "<row>" + "<drug>" + drug[0] + "</drug> <id>" + id[0] + "</id> <action>" + act[0] + "</action> </row>"
        result += '</data>'

        return result

    def extractDrugBank(self):
        rows = self.root.xpath('/x:drugbank/x:drug[./x:transporters/x:transporter/x:polypeptide/x:external-identifiers/x:external-identifier/x:resource/text()="UniProtKB"]', namespaces={'x': 'http://www.drugbank.ca'})

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

    def extractDrugsToDict(self, root, drugXpathExpr, tXpathExpr, dict):
        rows = root.xpath(drugXpathExpr, namespaces={'x': 'http://www.drugbank.ca'})

        for row in rows:
            drug = row.xpath('./x:name/text()', namespaces={'x': 'http://www.drugbank.ca'})
            # drug[0] = drug[0].replace(">", "")
            assert len(drug) == 1
            targets = row.xpath(tXpathExpr, namespaces={'x': 'http://www.drugbank.ca'})
            ids = []
            ids.extend(targets)
            if len(ids) > 0:
                dict[drug[0]] = ids
        print(dict)

#print extractSMILES('Ibuprofen')
# print extractTransporters('Vasopressin')
# print(extractDrugNames())
# extractDrugBankTargetsInNT()
