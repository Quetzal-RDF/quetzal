import cherrypy
import urllib
import warnings
import xpathTest

class PythonEval(object):
    @cherrypy.expose
    def getDrugBank(self):
        return xpathTest.extractDrugBank()
    
    @cherrypy.expose
    def getDrugBankNames(self):
        return xpathTest.extractDrugNames()

    @cherrypy.expose
    def getDrugTransporters(self, drugName):
        cherrypy.log("transporter request for:" + drugName)
        return xpathTest.extractTransporters(drugName)

    @cherrypy.expose
    def getSMILES(self, drugName):
        return xpathTest.extractSMILES(drugName)

    @cherrypy.expose
    def getDrugTargets(self, drugName):
        return xpathTest.extractTargets(drugName)

    @cherrypy.expose
    def postData(self, funcData):
        cherrypy.log("function data:" + funcData)
        return xpathTest.extractPost(funcData)

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
