package com.ibm.rdf.store.sparql11;

import static org.junit.Assert.*;

import java.io.IOException;

import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector.PrimitiveCategory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.io.Text;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ibm.research.rdf.store.utilities.WebServiceGetUDTF;
import com.ibm.research.rdf.store.utilities.WebServicePostUDAF;

public class TestSparkHttpServices {

	// Run python HttpServices.py in the pythonR folder so you can get a local service started
	
	@Test
	public void testPostToService() throws HiveException {
		WebServicePostUDAF udaf = new WebServicePostUDAF();
//		udaf.initialize(inputParams);
//		udaf.process(argsToProcess);

	}
	
	@Test
	public void testGetDrugBank() throws HiveException {
		Object[] argsToProcess = new Object[1];
		argsToProcess[0] = PrimitiveObjectInspectorFactory.javaStringObjectInspector.create("http://localhost:8082/getDrugBank");
		ObjectInspector[] inputParams = new ObjectInspector[12];
		ObjectInspector sc =  (ObjectInspector) PrimitiveObjectInspectorFactory.getPrimitiveWritableConstantObjectInspector(PrimitiveCategory.STRING, new Text("drug, uniprot_transporter_id, action"));
		inputParams[0] = sc;
		sc =  (ObjectInspector) PrimitiveObjectInspectorFactory.getPrimitiveWritableConstantObjectInspector(PrimitiveCategory.STRING, new Text(""));
		inputParams[1] = sc;
		sc =  (ObjectInspector) PrimitiveObjectInspectorFactory.getPrimitiveWritableConstantObjectInspector(PrimitiveCategory.STRING, new Text("//row"));
		inputParams[2] = sc;
		sc =  (ObjectInspector) PrimitiveObjectInspectorFactory.getPrimitiveWritableConstantObjectInspector(PrimitiveCategory.STRING, new Text("drug"));
		inputParams[3] = sc;
		sc =  (ObjectInspector) PrimitiveObjectInspectorFactory.getPrimitiveWritableConstantObjectInspector(PrimitiveCategory.STRING, new Text("./drug"));
		inputParams[4] = sc;
		sc =  (ObjectInspector) PrimitiveObjectInspectorFactory.getPrimitiveWritableConstantObjectInspector(PrimitiveCategory.STRING, new Text("<string>"));
		inputParams[5] = sc;
		sc =  (ObjectInspector) PrimitiveObjectInspectorFactory.getPrimitiveWritableConstantObjectInspector(PrimitiveCategory.STRING, new Text("uniprot_transporter_id"));
		inputParams[6] = sc;
		sc =  (ObjectInspector) PrimitiveObjectInspectorFactory.getPrimitiveWritableConstantObjectInspector(PrimitiveCategory.STRING, new Text("./id"));
		inputParams[7] = sc;
		sc =  (ObjectInspector) PrimitiveObjectInspectorFactory.getPrimitiveWritableConstantObjectInspector(PrimitiveCategory.STRING, new Text("<string>"));
		inputParams[8] = sc;
		sc =  (ObjectInspector) PrimitiveObjectInspectorFactory.getPrimitiveWritableConstantObjectInspector(PrimitiveCategory.STRING, new Text("action"));
		inputParams[9] = sc;
		sc =  (ObjectInspector) PrimitiveObjectInspectorFactory.getPrimitiveWritableConstantObjectInspector(PrimitiveCategory.STRING, new Text("./action"));
		inputParams[10] = sc;
		sc =  (ObjectInspector) PrimitiveObjectInspectorFactory.getPrimitiveWritableConstantObjectInspector(PrimitiveCategory.STRING, new Text("<string>"));
		inputParams[11] = sc;
		WebServiceGetUDTF udtf = new WebServiceGetUDTF();
		udtf.initialize(inputParams);
		udtf.process(argsToProcess);
	}

}
