import cherrypy
import urllib
import warnings

class PythonEval(object):
    @cherrypy.expose
    def callPython(self, funcBody):
        cherrypy.log("function body:" + funcBody)
        ns = {}
        code = compile(funcBody, '<string>', 'exec')
        exec code in ns
        return ns['result']

if __name__ == '__main__':
    cherrypy.config.update({'server.socket_host': '0.0.0.0'})
    cherrypy.config.update({'server.socket_port': 8082})
    warnings.filterwarnings("ignore")
    cherrypy.quickstart(PythonEval())
