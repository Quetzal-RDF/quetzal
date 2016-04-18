import cherrypy
import urllib
import warnings
import xpathTest as x

class PythonEval(object):
    xPathTest = x.xPathTest("../../drugbank.xml")

    @cherrypy.expose
    def getDrugBank(self):
        return self.xPathTest.extractDrugBank()
    
    @cherrypy.expose
    def getDrugBankNames(self):
        return self.xPathTest.extractDrugNames()

    @cherrypy.expose
    def getDrugTransporters(self, drugName):
        cherrypy.log("transporter request for:" + drugName)
        return self.xPathTest.extractTransporters(drugName)

    @cherrypy.expose
    def getSMILES(self, drugName):
        return self.xPathTest.extractSMILES(drugName)

    @cherrypy.expose
    def getDrugTargets(self, drugName):
        return self.xPathTest.extractTargets(drugName)

    @cherrypy.expose
    def postData(self, funcData):
        cherrypy.log("function data:" + funcData)
        return self.xPathTest.extractPost(funcData)

    @cherrypy.expose
    def evalPython(self, funcBody):
        cherrypy.log("function body:" + funcBody)
        ns = {}
        code = compile(funcBody, '<string>', 'exec')
        exec code in ns
        return ns['result']

if __name__ == '__main__':
    cherrypy.config.update({'server.socket_host': '0.0.0.0'})
    cherrypy.config.update({'server.socket_port': 8083})
    warnings.filterwarnings("ignore")
    cherrypy.quickstart(PythonEval())
