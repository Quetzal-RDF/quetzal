import rpy2.robjects as robjects
from lxml import etree
import rpy2.robjects.packages as rpackages

class TestCDK(object):

    def __init__(self):
        rxnsim = rpackages.importr('RxnSim')
        gosemsim = rpackages.importr('GOSemSim')

    def computeChemSimilarity(self, funcData):
        root = etree.fromstring(funcData)
        rows = root.xpath('//row')

        l = []
        drugs=[]
        for row in rows:
            s = row.xpath('./smiles/text()')
            l.extend(s)
            d = row.xpath('./drug/text()')
            drugs.extend(d)

        for i in range(len(l)):
            l[i] = l[i].strip()
            
        result = '<?xml version="1.0"?>'
        result += '<data>'

        smiles = robjects.StrVector(l)
        rcompute = robjects.r['ms.compute.sim.matrix']
        print(l)
        m = rcompute(smiles, format='smiles', standardize='T',fp_depth=6)

        for i in range(len(l)):
            for j in range(len(l)):
                if i==j or drugs[i] > drugs[j]:
                    continue
                index = (i * len(l)) + j
                result += "<row>"
                result += "<drug1>" + drugs[i] + "</drug1>"
                result += "<drug2>" + drugs[j] + "</drug2>"
                print("<sim>" + str(m[index]) + "</sim>")
                result += "<sim>" + str(m[index]) + "</sim>"
                result += "</row>"
        result += "</data>"
        return result

    def computeGOSimilarity(self, funcData):
        print(funcData)
        root = etree.fromstring(funcData)
        rows = root.xpath('//row')

        drugs=[]
        drugsToGo={}

        for row in rows:
            d = row.xpath('./drug/text()')[0].strip()
            s = row.xpath('./GO/text()')[0].strip()

            if d in drugsToGo:
                l = drugsToGo[d]
            else:
                l = []
                drugsToGo[d] = l
            l.append(s)
            
        result = '<?xml version="1.0"?>'
        result += '<data>'

        compute = robjects.r['mgoSim'] 

        for d1 in drugsToGo.keys():
            for d2 in drugsToGo.keys():
                print(drugsToGo[d1])
                print(drugsToGo[d2])
                x = compute(robjects.StrVector(drugsToGo[d1]), robjects.StrVector(drugsToGo[d2]), ont='MF', organism="human", measure="Wang")
                result += "<row>"
                result += "<drug1>" + d1 + "</drug1>"
                result += "<drug2>" + d2 + "</drug2>"
                result += "<sim>" + str(x[0]) + "</sim>"
                result += "</row>"

        result += "</data>"
        return result
