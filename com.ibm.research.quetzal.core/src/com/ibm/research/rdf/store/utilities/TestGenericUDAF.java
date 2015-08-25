package com.ibm.research.rdf.store.utilities;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.hadoop.hive.ql.exec.UDFArgumentTypeException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.parse.SemanticException;
import org.apache.hadoop.hive.ql.udf.generic.AbstractGenericUDAFResolver;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDAFEvaluator;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDAFParameterInfo;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.WritableConstantStringObjectInspector;
import org.apache.hadoop.hive.serde2.typeinfo.PrimitiveTypeInfo;
import org.apache.hadoop.io.Text;

public class TestGenericUDAF extends AbstractGenericUDAFResolver {

	@Override
	public GenericUDAFEvaluator getEvaluator(GenericUDAFParameterInfo info) throws SemanticException {
		// Type-checking goes here
		return new TestUDFEvaluator();
	}

	public static class TestUDFEvaluator extends GenericUDAFEvaluator {

		private int debug = 0;
		private String urlForService = null;
		private int inputFieldSize = 0;
		private ArrayList<String> fname = new ArrayList<String>();

		@SuppressWarnings("deprecation")
		public static class TableAggregator implements AggregationBuffer {
			public List<String[]> data;

			public TableAggregator() {
				data = new LinkedList<String[]>();
			}

			public void add(String[] datum) {
				data.add(datum);
			}
		}

		@Override
		/**
		 * Assumption: all output parameters are WritableConstantStringObjectInspectors.  First WritableConstantStringObjectInspector
		 * is the url for a service call.  This is ugly but I don't see a way around it.
		 */
		public ObjectInspector init(Mode m, ObjectInspector[] parameters) throws HiveException {
			// TODO Auto-generated method stub
			super.init(m, parameters);

			ArrayList<ObjectInspector> foi = new ArrayList<ObjectInspector>();
			
			int i = 0;
			for (ObjectInspector param : parameters) {
				// parameters are either input parameters, or constants.  the first constant is the url, followed by the output parameter names
				if (param instanceof WritableConstantStringObjectInspector) {
					WritableConstantStringObjectInspector wc = (WritableConstantStringObjectInspector) param;
					if (i == 0) {
						urlForService = wc.getWritableConstantValue().toString();
						i++;
					} else {
						String str = wc.getWritableConstantValue().toString();
						int sep = str.indexOf(":");
						String type = str.substring(sep+1);
						if (type.equals("Decimal")) {
							foi.add(PrimitiveObjectInspectorFactory.writableHiveDecimalObjectInspector);
						} else if (type.equals("Date")) {
							foi.add(PrimitiveObjectInspectorFactory.writableDateObjectInspector);
						} else if (type.equals("Timestamp")) {
							foi.add(PrimitiveObjectInspectorFactory.writableDateObjectInspector);
						} else if (type.equals("int")) {
							foi.add(PrimitiveObjectInspectorFactory.writableIntObjectInspector);
						} else if (type.equals("float")) {
							foi.add(PrimitiveObjectInspectorFactory.writableFloatObjectInspector);
						} else if (type.equals("double")) {
							foi.add(PrimitiveObjectInspectorFactory.writableDoubleObjectInspector);
						} else if (type.equals("boolean")) {
							foi.add(PrimitiveObjectInspectorFactory.writableBooleanObjectInspector);
						} else {
							foi.add(PrimitiveObjectInspectorFactory.writableStringObjectInspector);	
						}
						fname.add(str.substring(0, sep));
					}
				} else {
					inputFieldSize++;
				}

			}

			return ObjectInspectorFactory.getStandardListObjectInspector(
					ObjectInspectorFactory.getStandardStructObjectInspector(fname, foi));
		}

		@SuppressWarnings("deprecation")
		@Override
		public AggregationBuffer getNewAggregationBuffer() throws HiveException {
			// TODO Auto-generated method stub
			return new TableAggregator();
		}

		@SuppressWarnings("deprecation")
		@Override
		public void iterate(AggregationBuffer arg0, Object[] arg1) throws HiveException {
			// TODO Auto-generated method stub
			TableAggregator agg = (TableAggregator) arg0;
			
			String[] s = new String[inputFieldSize];
			for (int i = 0; i < inputFieldSize; i++) {
				s[i] = (String) arg1[i];
			}
			agg.add(s);

		}

		@SuppressWarnings("deprecation")
		@Override
		public void merge(AggregationBuffer arg0, Object arg1) throws HiveException {
			// TODO Auto-generated method stub
			// KAVITHA: Not sure a merge operation should even be supported -- it doesnt make sense to send some subset of the table
			// to an outside service because we know nothing about the semantics of the service
		}

		@SuppressWarnings("deprecation")
		@Override
		public void reset(AggregationBuffer arg0) throws HiveException {
			// TODO Auto-generated method stub

		}

		@SuppressWarnings("deprecation")
		@Override
		public Object terminate(AggregationBuffer arg0) throws HiveException {
			// TODO Auto-generated method stub
			List<String[]> r = ((TableAggregator) arg0).data;
			
			// serialize the data as XML
			
			
			ArrayList<Text[]> result = new ArrayList<Text[]>();

			for (String s[] : r) {
				Text[] k = new Text[s.length];
				for (int i = 0; i < s.length; i++) {
					k[i] = new Text(s[i]);
				}
				result.add(k);
			}
			return result;
		}

		@SuppressWarnings("deprecation")
		@Override
		public Object terminatePartial(AggregationBuffer arg0) throws HiveException {
			// TODO Auto-generated method stub
			return null;
		}
		
		
		private String serialize(List<String[]> data) {
			StringBuffer buf = new StringBuffer();
			buf.append("<data>");
			for (String s[] : data) {
				Text[] k = new Text[s.length];
				buf.append("<row>");
				for (int i = 0; i < s.length; i++) {
					k[i] = new Text(s[i]);
				}
				buf.append("<row>");
			}
			buf.append("</data>");
			
			return buf.toString();
		}
	}
}