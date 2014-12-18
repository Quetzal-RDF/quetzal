/******************************************************************************
 * Copyright (c) 2015 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *****************************************************************************/
 package com.ibm.research.proppaths;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

public class TestRandomCompleteDB2GraphMultipleEvalPerRound {
	public static enum EvaluationStrategy {
		STRATEGY_ORIGINAL("Original", true, false, true, false, false), // (i.e., level tracking and no runstats"), 
		STRATEGY_DELTA_RUNSTATS("Delta with RunStats", false, true, true, false, false), 
		STRATEGY_ORIGINAL_RUNSTATS("Original with Runstats", true,  true, true, false, false),
		STRATEGY_DELTA("Delta without RunStats", false, false, true, false, false),
		//STRATEGY_ORIGINAL_GROUPBY("Original GROUPBY", true, false, false, false, false),
		//STRATEGY_DELTA_GROUPBY_RUNSTATS("Delta GROUPBY with RunStats", false,  true, false, false, false),
		//STRATEGY_ORIGINAL_GROUPBY_RUNSTATS("Original GROUPBY with Runstats",true,  true, false, false, false),
		//STRATEGY_DELTA_GROUPBY("Delta GROUPBY without RunStats",false, false, false , false, false),
		STRATEGY_DELTA_INDEX_RUNSTATS("Delta with Index and RunStats", false, true, true, true, false), 
		//STRATEGY_DELTA_INDEX_RUNSTATS_SAMPLED_INDEX("Delta with Index and RunStats and Sampled Index", false, true, true, true, true), 
		//STRATEGY_DELTA_INDEX_GROUPBY_RUNSTATS("Delta GROUPBY with Index and RunStats", false, true, false, true, false),
		//STRATEGY_DELTA_INDEX_GROUPBY_RUNSTATS_SAMPLED_INDEX("Delta GROUPBY with RunStats and Sampled Index",false,  true, false, true, true);
		STRATEGY_CTE_GROUPBY("CTE GROUPBY",false, false, false , false, false);
		private String name;
		private boolean distinct;
		private boolean runStats;
		private boolean indices;
		private boolean runStatsSampledIndexAll;
		private boolean original; 
		private EvaluationStrategy(String name, boolean original, boolean runStats,  boolean distinct,boolean indices, boolean runStatsSampledIndexAll) {
			this.name = name;
			this.original = original;
			this.distinct = distinct;
			this.runStats = runStats;
			this.indices = indices;
			this.runStatsSampledIndexAll = runStatsSampledIndexAll;
		}
		
		public String getName() {
			return name;
		}
		
		public boolean isOriginal() {
			return original;
		}

		public boolean isDistinct() {
			return distinct;
		}

		public boolean isRunStats() {
			return runStats;
		}
		
		public boolean isIndices() {
			return indices;
		}

		public boolean isRunStatsSampledIndexAll() {
			return runStatsSampledIndexAll;
		}

		@Override
		public String toString() {
			return name;
		}
		
	}
	
	public static  int NUMBER_OF_RECORDED_EVALUATION_ROUND  = 5;//5;//7;
	public static  int NUMBER_OF__RECORDED_EVALUATION_PER_QUERY_PER_ROUND = 7;
	
	public static final String inputQueries =
		"0 15959 3\n" +
		"0 15959 6\n" +
		"0 15959 9\n" +
		"0 100 5\n" +
		"0 1000 5\n" +
		"0 10000 5\n" +
		"1 1 2\n" +
		"1 1 3\n" +
		"1 1 4\n" +
		"1 10 3\n" +
		"1 100 3\n";
	
	public static final String SHORT_QUERIES_DIR ="../rdfstore-data/dbp38_sql/";
	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException, SQLException{
		if (args.length ==0 ) {
			System.err.println("Usage: resultFile stategy? externalSeed? #Round? #RecordedRunPerRound? globalWarmup (0|1)? localWarmup (0|1)?");
			return;
		}
		File result = new File(args[0]);
		int strategyIndex = -1;
		int externalSeed = -1;
		boolean globalWarmup = false;
		boolean localWarmup = true;
		try {
			if (args.length>1) {
				strategyIndex = Integer.parseInt(args[1]);
			}
			if (args.length>2) {
				externalSeed = Integer.parseInt(args[2]);
			}
			if (args.length>3) {
				NUMBER_OF_RECORDED_EVALUATION_ROUND = Integer.parseInt(args[3]);
			}
			if (args.length>4) {
				NUMBER_OF__RECORDED_EVALUATION_PER_QUERY_PER_ROUND = Integer.parseInt(args[4]);
			}
			if (args.length>5) {
				globalWarmup = Integer.parseInt(args[5]) == 1;
			}
			if (args.length>6) {
				localWarmup = Integer.parseInt(args[6]) == 1;
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			System.err.println("Usage: resultFile stategy? externalSeed? #Round? #RecordedRunPerRound?  globalWarmup (0|1)? localWarmup (0|1)?");
			return;
		}
		System.out.println("Result file = "+result.getAbsolutePath()
				+" strategy = "+strategyIndex+" externalSeed = "+externalSeed+" # of rounds = "+NUMBER_OF_RECORDED_EVALUATION_ROUND
				+" # of recorded evaluations per query per round = "+NUMBER_OF__RECORDED_EVALUATION_PER_QUERY_PER_ROUND+" globalWarmUp = "+globalWarmup
				+" localWarmUp = "+localWarmup);
		boolean resultFileExist = result.exists();
		BufferedWriter out = new BufferedWriter(new FileWriter(result, true));
		if (!resultFileExist) {
			out.append("startegy, RealQueryNum, qNum, startNum, hopNum, time in ms\n");
		}
		Connection conn = null;
		try {
			System.out.println("Connecting to dbpedia store ...");
			conn = TestDB2Graph.getConnection("jdbc:db2://9.47.204.38:50001/dbpedia","db2inst2", "db2admin");
			System.out.println("Connected to dbpedia");
		} catch (Exception e) {
			throw new RuntimeException("Connection to db failed", e);
		}
		
		try {
			
			EvaluationStrategy[] strategies= EvaluationStrategy.values();
			for (int i=0;i<strategies.length && strategyIndex>=0;i++) {
				if (i!=strategyIndex) {
					strategies[i] = null;
				}
			}
			System.out.println("Reading queries ...");
			int[][] queries = getAllQueries();
			System.out.println("All queries read");
			int[] query2NumOfSolutions = new int[queries.length];
			int[] queryStrategy2TotalRecordedTime = new int[queries.length*strategies.length] ;
			for (int i=0;i<queries.length;i++) {
				query2NumOfSolutions[i] = -1;
				
			}
			for (int i=0;i<queries.length*strategies.length;i++) {
				queryStrategy2TotalRecordedTime[i] = 0;
			}
				
				
			// perform NUMBER_OF_RECORDED_EVALUATION evaluations
			// In each evaluation, queries and strategies are executed in a different random order
			for (int k=0; k<=NUMBER_OF_RECORDED_EVALUATION_ROUND; k++) {
				if (k==0 && !globalWarmup) {
					continue;
				}
				if (k>0) {
					System.out.println(toOrdinalString(k)+ " evaluation round");
				} else {
					System.out.println("Warm up phase");
				}
				
				Random rnd = new Random(externalSeed<0?k: (k+1)*externalSeed);//10*(k+1));
				List<Integer> queryList = new ArrayList<Integer>(queries.length*strategies.length);
				for (int i=0;i<queries.length*strategies.length;i++) {
					queryList.add(i);
				}
				while (!queryList.isEmpty()) {
					int queryStrategy = queryList.remove(rnd.nextInt(queryList.size()));
					int queryIndex = queryStrategy % queries.length;
					EvaluationStrategy strategy = strategies[queryStrategy/queries.length];
					if (strategy == null) {
						continue;
					}
					int qNum = queries[queryIndex][0];
					int startNum = queries[queryIndex][1];
					int hopNum = queries[queryIndex][2]; 
					try {
						long time;
						int sols =0;
						if (localWarmup || k==0) {
							System.out.println( strategy +" warm up evaluation of query "+(queryIndex+1)+" (qNum = "+qNum+" startNum = "+startNum+" hopNum = "+hopNum+")");
							time = System.currentTimeMillis();
							sols = evaluate(strategy, conn, queryIndex, qNum, startNum, hopNum);
							time = System.currentTimeMillis() - time;
							System.out.println( strategy + " warm up evaluation of query "+(queryIndex+1)+" done in "+time +" ms (# solutions= "+sols);
							if( query2NumOfSolutions[queryIndex]!= -1 && query2NumOfSolutions[queryIndex]!= sols) {
								System.err.println(toOrdinalString(k+1)+ " "+ strategy +" evaluation of query "+(queryIndex+1)+" (qNum = "+qNum+" startNum = "+startNum+" hopNum = "+hopNum+"):" +
										" previous number of solutions: "+ query2NumOfSolutions[queryIndex]+" current number of solutions: "+sols);
							}
						}
						// recorded evaluation if k >0
						for (int j=0;j<NUMBER_OF__RECORDED_EVALUATION_PER_QUERY_PER_ROUND && k!=0  ;j++) {
							System.out.println(  toOrdinalString((k-1)*NUMBER_OF__RECORDED_EVALUATION_PER_QUERY_PER_ROUND+j+1)+ " "+ strategy + " recorded evaluation of query "+(queryIndex+1)+" (qNum = "+qNum+" startNum = "+startNum+" hopNum = "+hopNum+")");
							time = System.currentTimeMillis();
							sols = evaluate(strategy, conn,queryIndex, qNum, startNum, hopNum);
							time = System.currentTimeMillis() - time;
							System.out.println(  toOrdinalString((k-1)*NUMBER_OF__RECORDED_EVALUATION_PER_QUERY_PER_ROUND+j+1)+ " "+ strategy + " recorded evaluation of query "+(queryIndex+1)+" done in "+time
									+" ms (# solutions= "+sols);
							if( query2NumOfSolutions[queryIndex]!= -1 && sols!=-1 && query2NumOfSolutions[queryIndex]!= sols) {
								System.err.println(toOrdinalString(k+1)+ " "+ strategy +" evaluation of query "+(queryIndex+1)+" (qNum = "+qNum+" startNum = "+startNum+" hopNum = "+hopNum+"):" +
										" previous number of solutions: "+ query2NumOfSolutions[queryIndex]+" current number of solutions: "+sols);
							}
							queryStrategy2TotalRecordedTime[queryStrategy] += (int) time;
							System.out.println("Average of first "+ toOrdinalString((k-1)*NUMBER_OF__RECORDED_EVALUATION_PER_QUERY_PER_ROUND+j+1)+" evaluation(s) of query "+(queryIndex+1)+" for "+strategy +" strategy : "
									+((float) queryStrategy2TotalRecordedTime[queryStrategy])/((float) (k-1)*NUMBER_OF__RECORDED_EVALUATION_PER_QUERY_PER_ROUND+j+1) +" ms");
						}
						//
						if (query2NumOfSolutions[queryIndex] == -1 && sols!= -1) {
							 query2NumOfSolutions[queryIndex] = sols;
						}
						/*if (k>0) {
							queryStrategy2TotalRecordedTime[queryStrategy] += (int) time;
							System.out.println("Average of first "+ k+" evaluation(s) of query "+(queryIndex+1)+" for "+strategy +" strategy : "
									+((float) queryStrategy2TotalRecordedTime[queryStrategy])/((float) k) +" ms");
						}*/
					} catch (SQLException e) {
						System.err.println("Failure during "+toOrdinalString(k) +" evaluation of query "+(queryIndex+1)+" using strategy "+strategy);
						e.printStackTrace();
					}
				}
			}
			//
			for (int j=0;j<queries.length*strategies.length;j++) {
				int queryStrategy = j;
				int queryIndex = queryStrategy % queries.length;
				EvaluationStrategy strategy = strategies[queryStrategy/queries.length];
				if (strategy == null) {
					continue;
				}
				int qNum = queries[queryIndex][0];
				int startNum = queries[queryIndex][1];
				int hopNum = queries[queryIndex][2];
				System.out.println("Average evaluation time of query "+(queryIndex+1)+" (qNum = "+qNum+" startNum = "+startNum+" hopNum = "+hopNum+") over "
						+ NUMBER_OF_RECORDED_EVALUATION_ROUND*NUMBER_OF__RECORDED_EVALUATION_PER_QUERY_PER_ROUND+ " runs with  " + strategy+": "+
						((float) queryStrategy2TotalRecordedTime[queryStrategy])/((float) NUMBER_OF_RECORDED_EVALUATION_ROUND*NUMBER_OF__RECORDED_EVALUATION_PER_QUERY_PER_ROUND));
				out.append(strategy+", "+(queryIndex+1)+", "+qNum+", "+startNum+", "+hopNum+", "
						+((float) queryStrategy2TotalRecordedTime[queryStrategy])/((float) NUMBER_OF_RECORDED_EVALUATION_ROUND*NUMBER_OF__RECORDED_EVALUATION_PER_QUERY_PER_ROUND) );
				out.append("\n");
			}
			out.flush();
			
		} finally  {
			if (conn != null) {
				conn.close();
			}
			out.close();
		}
		

	}
	protected static String toOrdinalString(int n) {
		String ret = ""+n;
		if (ret.endsWith("1")) {
			ret +="st";
		} else if (ret.endsWith("2")) {
			ret +="nd";
		} else if (ret.endsWith("3")) {
			ret += "rd";
		} else {
			ret += "th";
		}
		return ret;
	}
	public static int[][]  getAllQueries() {
		try {
			int numberOfShortQueries = 20;
			int[][] queries = new int[11+numberOfShortQueries][3];
			BufferedReader r = new BufferedReader(new StringReader(inputQueries));
			String line;
			int pos =0;
			for (;pos<numberOfShortQueries; pos++) {
				queries[pos][0] = -1;
				queries[pos][1] = pos+1;
				queries[pos][2] = -1;
			}
			while ((line = r.readLine())!=null) {
				StringTokenizer tzr = new StringTokenizer(line);
				assert tzr.hasMoreTokens();
				int qNum = Integer.parseInt(tzr.nextToken());
				assert tzr.hasMoreTokens();
				int startNum = Integer.parseInt(tzr.nextToken());
				assert tzr.hasMoreTokens();
				int hopNum = Integer.parseInt(tzr.nextToken());
				queries[pos][0] = qNum;
				queries[pos][1] = startNum;
				queries[pos][2]	= hopNum;
				pos++;
			}
			assert pos == 11+numberOfShortQueries : pos;
			r.close();
			
			return queries;
		} catch (NumberFormatException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	public static int evaluate(Connection conn, int shortQueryNum) throws IOException, SQLException{
		File qf = new File(SHORT_QUERIES_DIR, "q"+shortQueryNum+".sql");
		BufferedReader in = new BufferedReader(new FileReader(qf));
		StringBuffer buf = new StringBuffer();
		String line ;
		while ((line=in.readLine())!=null) {
			buf.append(line+"\n");
		}
		in.close();
		String sql = buf.toString();
		Statement st = conn.createStatement();
		java.sql.ResultSet rs = st.executeQuery(sql);
		int count =0;
		while (rs.next()) {
			count++;
		}
		rs.close();
		st.close();
		
		return count;
	}
	public static int evaluate(EvaluationStrategy strategy, Connection conn, int realQueryNumStartingAtIndex0, int qNum, int startNum, int hopNum) throws IOException, SQLException {
		boolean runStats = strategy.isRunStats();
		boolean distinct = strategy.isDistinct();
		boolean indices  = strategy.isIndices();
		boolean runStatsSampledIndexAll = strategy.isRunStatsSampledIndexAll();
		if (qNum == -1) {
			//if (strategy.ordinal()  == EvaluationStrategy.STRATEGY_ORIGINAL.ordinal())
			
			{
				return evaluate(conn, startNum);
			}
			/*else {
				return -1;
			}*/
		}
		if (strategy.ordinal() == EvaluationStrategy.STRATEGY_CTE_GROUPBY.ordinal()) {
			return TestDB2GraphCTEGROUPBY.evaluate(conn, realQueryNumStartingAtIndex0-20);
		} else if (strategy.isOriginal()) {
			if (qNum == 0) {
				return TestDB2Graph.evaluate(conn, qNum, startNum, hopNum, runStats,distinct, indices, runStatsSampledIndexAll);
			} else {
				assert qNum == 1;
				return TestDB2Graph2.evaluate(conn, qNum, startNum, hopNum, runStats, distinct, indices, runStatsSampledIndexAll);
			}
		} else {
			if (qNum == 0) {
				return TestDB2GraphWithoutLevelInfo.evaluate(conn, qNum, startNum, hopNum, runStats,distinct, indices, runStatsSampledIndexAll);
			} else {
				assert qNum == 1;
				return TestDB2Graph2WithoutLevelInfo.evaluate(conn, qNum, startNum, hopNum, runStats, distinct, indices, runStatsSampledIndexAll);
			}
		}
	}
	
		
	
}
