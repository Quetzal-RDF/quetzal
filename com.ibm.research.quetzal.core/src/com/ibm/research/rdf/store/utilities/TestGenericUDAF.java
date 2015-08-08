package com.ibm.research.rdf.store.utilities;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.parse.SemanticException;
import org.apache.hadoop.hive.ql.udf.generic.AbstractGenericUDAFResolver;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDAFEvaluator;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDAFParameterInfo;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.io.Text;

public class TestGenericUDAF extends AbstractGenericUDAFResolver {

	@Override
	public GenericUDAFEvaluator getEvaluator(GenericUDAFParameterInfo info) throws SemanticException {
		// Type-checking goes here
		return new TestUDFEvaluator();
	}

	public static class TestUDFEvaluator extends GenericUDAFEvaluator {

		private int debug = 0;

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
		public ObjectInspector init(Mode m, ObjectInspector[] parameters) throws HiveException {
			// TODO Auto-generated method stub
			super.init(m, parameters);

			ArrayList<ObjectInspector> foi = new ArrayList<ObjectInspector>();
			foi.add(PrimitiveObjectInspectorFactory.writableStringObjectInspector);
			foi.add(PrimitiveObjectInspectorFactory.writableStringObjectInspector);
			ArrayList<String> fname = new ArrayList<String>();
			fname.add("firstname");
			fname.add("lastname");

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
			int k = 0;
			TableAggregator agg = (TableAggregator) arg0;
			for (int i = 0; i < arg1.length; i++) {
				if (!(arg1[i] instanceof Text)) {
					k++;
				}
			}
			String[] s = new String[k];
			for (int i = 0; i < k; i++) {
				s[i] = (String) arg1[i];
			}
			agg.add(s);

		}

		@SuppressWarnings("deprecation")
		@Override
		public void merge(AggregationBuffer arg0, Object arg1) throws HiveException {
			// TODO Auto-generated method stub

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
		// UDAF logic goes here!
	}
}