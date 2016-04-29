import cherrypy
import urllib
import warnings
import xpathTest as x
import TestCDK as CDK

class PythonEval(object):
    xPathTest = x.xPathTest("drugbank.xml")
    cdk = CDK.TestCDK()

    @cherrypy.expose
    def getDrugBankNames(self):
        return self.xPathTest.extractDrugNames()

    @cherrypy.expose
    def getDrugBankNamesAndGroups(self):
        return self.xPathTest.extractDrugNamesAndGroups()

    @cherrypy.expose
    def getDrugTransporters(self, drugName):
        return self.xPathTest.extractTransporters(drugName)

    @cherrypy.expose
    def getSMILES(self, drugName):
        return self.xPathTest.extractSMILES(drugName)

    @cherrypy.expose
    def getDrugTargets(self, drugName):
        return self.xPathTest.extractTargets(drugName)

    @cherrypy.expose
    def postData(self, funcData):
        return self.xPathTest.extractPost(funcData, self.xPathTest.sumFunc)

    @cherrypy.expose
    def getSMILES(self, funcData):
        return self.xPathTest.extractPost(funcData, self.xPathTest.SMILESFunc)

    @cherrypy.expose
    def getTransporters(self, funcData):
        return self.xPathTest.extractPost(funcData, self.xPathTest.transportersFunc)

    @cherrypy.expose
    def getTargets(self, funcData):
        return self.xPathTest.extractPost(funcData, self.xPathTest.targetsFunc)

    @cherrypy.expose
    def computeChemSimilarity(self, funcData):
        return self.cdk.computeChemSimilarity(funcData)

    @cherrypy.expose
    def computeGOSimilarity(self, funcData):
        return self.cdk.computeGOSimilarity(funcData)

if __name__ == '__main__':
    cherrypy.config.update({'server.socket_host': '0.0.0.0'})
    cherrypy.config.update({'server.socket_port': 8083})
    warnings.filterwarnings("ignore")
    cherrypy.quickstart(PythonEval())
