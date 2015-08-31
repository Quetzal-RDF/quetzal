import cherrypy

from pythonRConnector import *

import warnings

class HelloWorld(object):
    @cherrypy.expose
    def index(self):
        return "Hello world!"
    @cherrypy.expose
    def test(self, name="World"):
        cherrypy.log("name:" + name)
        return "Hello: " + name
    @cherrypy.expose
    def testPost(self):
        cl = cherrypy.request.headers['Content-Length']
        rawbody = cherrypy.request.body.read(int(cl))
        cherrypy.log("body:" + rawbody)
        return "Hello: " + rawbody
    @cherrypy.expose
    def callR(self, funcName, funcBody, funcData):
        cherrypy.log("function name:" + funcName)
        cherrypy.log("function body:" + funcBody)
        cherrypy.log("function data:" + funcData)
        return "called:" + funcBody
    @cherrypy.expose
    def registerSparqlRFunc(self):
        cl = cherrypy.request.headers['Content-Length']
        rawbody = cherrypy.request.body.read(int(cl))
        cherrypy.log("register func body:" + rawbody)
        try:
        	registerRFunction(rawbody)
        except Exception as e:
        	cherrypy.log("Register func error: " + str(e))
        	return "Register func error: " + str(e)
        else:
        	return "Register func success!"
    
    @cherrypy.expose
    def callSparqlRFunc(self, func="foo"):
        cl = cherrypy.request.headers['Content-Length']
        rawbody = cherrypy.request.body.read(int(cl))
        cherrypy.log("call func body:" + rawbody)
        try:
        	ret = callRFunction(func, rawbody)
        except Exception as e:
        	cherrypy.log("Call func error: " + str(e))
        	return "Call func error: " + str(e)
        else:
        	return ret
        	
    @cherrypy.expose
    def callSparqlRFuncWithXMLDoc(self, func="foo"):
        cl = cherrypy.request.headers['Content-Length']
        rawbody = cherrypy.request.body.read(int(cl))
        cherrypy.log("call func body with xml doc:" + rawbody)
        try:
        	ret = callRFunctionWithXMLDoc(func, rawbody)
        except Exception as e:
        	cherrypy.log("Call func error: " + str(e))
        	return "Call func error: " + str(e)
        else:
        	return ret

    @cherrypy.expose
    def callRCode(self, funcBody, funcData):
        cherrypy.log("function body:" + funcBody)
        cherrypy.log("function data:" + funcData)
        try:
		code = "(function(`__input`) { " + funcBody + " })"
        	ret = callRFunctionWithXMLDoc(code, funcData)
                print (ret)
        except Exception as e:
        	cherrypy.log(code)
        	cherrypy.log("Call func error: " + str(e))
        	return "Call func error: " + str(e)
        else:
        	return ret


if __name__ == '__main__':
    cherrypy.config.update({'server.socket_host': '0.0.0.0'})
    cherrypy.config.update({'server.socket_port': 8082})
    warnings.filterwarnings("ignore")
    initR()
    cherrypy.quickstart(HelloWorld())

