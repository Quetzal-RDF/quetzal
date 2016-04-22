import rpy2.robjects as robjects
from lxml import etree
import rpy2.robjects.packages as rpackages

class TestCDK(object):

    def __init__(self):
        rxnsim = rpackages.importr('RxnSim')

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

        result = '<?xml version="1.0"?>'
        result += '<data>'

        smiles = robjects.StrVector(l)
        rcompute = robjects.r['ms.compute.sim.matrix']
        m = rcompute(smiles, format='smiles', standardize='T',fp_depth=6)

        for i in range(len(l)):
            for j in range(len(l)):
                if i==j:
                    continue
                index = (i * len(l)) + j
                result += "<row>"
                result += "<drug1>" + drugs[i] + "</drug1>"
                result += "<drug2>" + drugs[j] + "</drug2>"
                result += "<sim>" + str(m[index]) + "</sim>"
                result += "</row>"
        result += "</data>"
        return result
