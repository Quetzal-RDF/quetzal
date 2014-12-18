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
 package com.ibm.research.rdf.store.schema;

import java.util.Iterator;
import java.util.Map;

public class ReorgReport {
	
	private boolean storeEmpty;

	private float dphSpillRate;
	
	private float rphSpillRate;
	
	private float dphColumnUsage;
	
	private float rphColumnUsage;
	
	private int averageURILength;
	
	private int averageLongURILenght;
	
	private long numberOfLongURI;
	
	private boolean coloringUsedForDirect;
	
	private boolean coloringUsedForReverse;
	
	private int dphSpaceUsed;
	
	private int rphSpaceUsed;
	
	private int dphSpaceAlloted;
	
	private int rphSpaceAlloted;
	
	private com.ibm.wala.util.collections.Pair<Map<String, Integer>, Map<String, Integer>> dphMappings;
	
	private com.ibm.wala.util.collections.Pair<Map<String, Integer>, Map<String, Integer>> rphMappings;
	
	// Re-org Result
	
	private boolean reOrgRequiredForDPH;
	
	private int bucketSizeDPH;
	
	private int columnLengthDPH;
	
	private boolean columnLengthChangeForDirect;
		
	private boolean reOrgRequiredForRPH;
	
	private int bucketSizeRPH;
	
	private int columnLengthRPH;
	
	private boolean columnLengthChangeForReverse;
	
	private int gIDMaxStringLength;
	

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("***Re-Org Report***\n");
		
		//isColoringUsed
		buffer.append("Coloring used for DPH : "+isColoringUsedForDirect() +"\n");
		buffer.append("Coloring used for RPH: "+isColoringUsedForReverse() +"\n\n");
		
		//Spill rate
		buffer.append("Spill rate for DPH : "+getDphSpillRate() +"\n");
		buffer.append("Spill rate for RPH: "+getRphSpillRate() +"\n\n");
		
		//Column Utilization
		buffer.append("Column usage for DPH : "+getDphColumnUsage() +"\n");
		buffer.append("Column usage for RPH: "+getRphColumnUsage() +"\n\n");
		
		//URI Length
		buffer.append("Average URI Length : "+getAverageURILength() +"\n");
		buffer.append("Average Long URI Length: "+getAverageLongURILenght() +"\n");
		buffer.append("Number of Long URI: "+getNumberOfLongURI() +"\n\n");
		
		//Space Alloted
		buffer.append("Space alloted to each row in DPH : "+getDphSpaceAlloted() +"\n");
		buffer.append("Space alloted to each row in RPH: "+getRphSpaceAlloted() +"\n\n");
		
		//Space Used
		buffer.append("Average Space usage per row in DPH : "+getDphSpaceUsed() +"\n");
		buffer.append("Average Space usage per row in RPH: "+getRphSpaceUsed() +"\n\n");
		
		//Reorg Required
		buffer.append("DPH\t" + isReOrgRequiredForDPH() + "\tCol length" + getColumnLengthDPH() + "\tBucket size" + getBucketSizeDPH() + "\n");
		buffer.append("RPH\t" + isReOrgRequiredForRPH() + "\tCol length" + getColumnLengthRPH() + "\tBucket size" + getBucketSizeRPH() + "\n");
//		buffer.append("DS\t" + isColumnLengthChangeForDirect() + "\tCol length" + getColumnLengthDPH() +  "\n");
//		buffer.append("RS\t" + isColumnLengthChangeForReverse() + "\tCol length" + getColumnLengthRPH() +  "\n");
//		buffer.append("LS\t" + (isColumnLengthChangeForDirect() || isColumnLengthChangeForReverse()) +"\n");
		
		return buffer.toString();
	}
	
	/**
	 * @return the dphSpillRate
	 */
	public float getDphSpillRate() {
		return dphSpillRate;
	}

	/**
	 * @param dphSpillRate the dphSpillRate to set
	 */
	public void setDphSpillRate(float dphSpillRate) {
		this.dphSpillRate = dphSpillRate;
	}

	/**
	 * @return the rphSpillRate
	 */
	public float getRphSpillRate() {
		return rphSpillRate;
	}

	/**
	 * @param rphSpillRate the rphSpillRate to set
	 */
	public void setRphSpillRate(float rphSpillRate) {
		this.rphSpillRate = rphSpillRate;
	}

	/**
	 * @return the dphColumnUsage
	 */
	public float getDphColumnUsage() {
		return dphColumnUsage;
	}

	/**
	 * @param dphColumnUsage the dphColumnUsage to set
	 */
	public void setDphColumnUsage(float dphColumnUsage) {
		this.dphColumnUsage = dphColumnUsage;
	}

	/**
	 * @return the rphColumnUsage
	 */
	public float getRphColumnUsage() {
		return rphColumnUsage;
	}

	/**
	 * @param rphColumnUsage the rphColumnUsage to set
	 */
	public void setRphColumnUsage(float rphColumnUsage) {
		this.rphColumnUsage = rphColumnUsage;
	}

	/**
	 * @return the averageURILength
	 */
	public int getAverageURILength() {
		return averageURILength;
	}

	/**
	 * @param averageURILength the averageURILength to set
	 */
	public void setAverageURILength(int averageURILength) {
		this.averageURILength = averageURILength;
	}

	/**
	 * @return the averageLongURILenght
	 */
	public int getAverageLongURILenght() {
		return averageLongURILenght;
	}

	/**
	 * @param averageLongURILenght the averageLongURILenght to set
	 */
	public void setAverageLongURILenght(int averageLongURILenght) {
		this.averageLongURILenght = averageLongURILenght;
	}

	/**
	 * @return the numberOfLongURI
	 */
	public long getNumberOfLongURI() {
		return numberOfLongURI;
	}

	/**
	 * @param numberOfLongURI the numberOfLongURI to set
	 */
	public void setNumberOfLongURI(long numberOfLongURI) {
		this.numberOfLongURI = numberOfLongURI;
	}

	/**
	 * @return the coloringUsedForDirect
	 */
	public boolean isColoringUsedForDirect() {
		return coloringUsedForDirect;
	}

	/**
	 * @param coloringUsedForDirect the coloringUsedForDirect to set
	 */
	public void setColoringUsedForDirect(boolean coloringUsedForDirect) {
		this.coloringUsedForDirect = coloringUsedForDirect;
	}

	/**
	 * @return the coloringUsedForReverse
	 */
	public boolean isColoringUsedForReverse() {
		return coloringUsedForReverse;
	}

	/**
	 * @param coloringUsedForReverse the coloringUsedForReverse to set
	 */
	public void setColoringUsedForReverse(boolean coloringUsedForReverse) {
		this.coloringUsedForReverse = coloringUsedForReverse;
	}

	/**
	 * @return the dphSpaceUsed
	 */
	public int getDphSpaceUsed() {
		return dphSpaceUsed;
	}

	/**
	 * @param dphSpaceUsed the dphSpaceUsed to set
	 */
	public void setDphSpaceUsed(int dphSpaceUsed) {
		this.dphSpaceUsed = dphSpaceUsed;
	}

	/**
	 * @return the rphSpaceUsed
	 */
	public int getRphSpaceUsed() {
		return rphSpaceUsed;
	}

	/**
	 * @param rphSpaceUsed the rphSpaceUsed to set
	 */
	public void setRphSpaceUsed(int rphSpaceUsed) {
		this.rphSpaceUsed = rphSpaceUsed;
	}

	/**
	 * @return the reOrgRequiredForDPH
	 */
	public boolean isReOrgRequiredForDPH() {
		return reOrgRequiredForDPH;
	}

	/**
	 * @param reOrgRequiredForDPH the reOrgRequiredForDPH to set
	 */
	public void setReOrgRequiredForDPH(boolean reOrgRequiredForDPH) {
		this.reOrgRequiredForDPH = reOrgRequiredForDPH;
	}

	/**
	 * @return the bucketSizeDPH
	 */
	public int getBucketSizeDPH() {
		return bucketSizeDPH;
	}

	/**
	 * @param bucketSizeDPH the bucketSizeDPH to set
	 */
	public void setBucketSizeDPH(int bucketSizeDPH) {
		this.bucketSizeDPH = bucketSizeDPH;
	}

	/**
	 * @return the columnLengthDPH
	 */
	public int getColumnLengthDPH() {
		return columnLengthDPH;
	}

	/**
	 * @param columnLengthDPH the columnLengthDPH to set
	 */
	public void setColumnLengthDPH(int columnLengthDPH) {
		this.columnLengthDPH = columnLengthDPH;
	}

	/**
	 * @return the columnLengthChangeForDirect
	 */
	public boolean isColumnLengthChangeForDirect() {
		return columnLengthChangeForDirect;
	}

	/**
	 * @param columnLengthChangeForDirect the columnLengthChangeForDirect to set
	 */
	public void setColumnLengthChangeForDirect(boolean columnLengthChangeForDirect) {
		this.columnLengthChangeForDirect = columnLengthChangeForDirect;
	}

	/**
	 * @return the reOrgRequiredForRPH
	 */
	public boolean isReOrgRequiredForRPH() {
		return reOrgRequiredForRPH;
	}

	/**
	 * @param reOrgRequiredForRPH the reOrgRequiredForRPH to set
	 */
	public void setReOrgRequiredForRPH(boolean reOrgRequiredForRPH) {
		this.reOrgRequiredForRPH = reOrgRequiredForRPH;
	}

	/**
	 * @return the bucketSizeRPH
	 */
	public int getBucketSizeRPH() {
		return bucketSizeRPH;
	}

	/**
	 * @param bucketSizeRPH the bucketSizeRPH to set
	 */
	public void setBucketSizeRPH(int bucketSizeRPH) {
		this.bucketSizeRPH = bucketSizeRPH;
	}

	/**
	 * @return the columnLengthRPH
	 */
	public int getColumnLengthRPH() {
		return columnLengthRPH;
	}

	/**
	 * @param columnLengthRPH the columnLengthRPH to set
	 */
	public void setColumnLengthRPH(int columnLengthRPH) {
		this.columnLengthRPH = columnLengthRPH;
	}

	/**
	 * @return the columnLengthChangeForReverse
	 */
	public boolean isColumnLengthChangeForReverse() {
		return columnLengthChangeForReverse;
	}

	/**
	 * @param columnLengthChangeForReverse the columnLengthChangeForReverse to set
	 */
	public void setColumnLengthChangeForReverse(boolean columnLengthChangeForReverse) {
		this.columnLengthChangeForReverse = columnLengthChangeForReverse;
	}

	/**
	 * @return the dphSpaceAlloted
	 */
	public int getDphSpaceAlloted() {
		return dphSpaceAlloted;
	}

	/**
	 * @param dphSpaceAlloted the dphSpaceAlloted to set
	 */
	public void setDphSpaceAlloted(int dphSpaceAlloted) {
		this.dphSpaceAlloted = dphSpaceAlloted;
	}

	/**
	 * @return the rphSpaceAlloted
	 */
	public int getRphSpaceAlloted() {
		return rphSpaceAlloted;
	}

	/**
	 * @param rphSpaceAlloted the rphSpaceAlloted to set
	 */
	public void setRphSpaceAlloted(int rphSpaceAlloted) {
		this.rphSpaceAlloted = rphSpaceAlloted;
	}

	/**
	 * @return the dphMappings
	 */
	public com.ibm.wala.util.collections.Pair<Map<String, Integer>, Map<String, Integer>> getDphMappings() {
		return dphMappings;
	}

	/**
	 * @param dphMappings the dphMappings to set
	 */
	public void setDphMappings(
			com.ibm.wala.util.collections.Pair<Map<String, Integer>, Map<String, Integer>> dphMappings) {
		this.dphMappings = dphMappings;
	}

	/**
	 * @return the rphMappings
	 */
	public com.ibm.wala.util.collections.Pair<Map<String, Integer>, Map<String, Integer>> getRphMappings() {
		return rphMappings;
	}

	/**
	 * @param rphMappings the rphMappings to set
	 */
	public void setRphMappings(
			com.ibm.wala.util.collections.Pair<Map<String, Integer>, Map<String, Integer>> rphMappings) {
		this.rphMappings = rphMappings;
	}

	/**
	 * @return the gIDMaxStringLength
	 */
	public int getgIDMaxStringLength() {
		return gIDMaxStringLength;
	}

	/**
	 * @param gIDMaxStringLength the gIDMaxStringLength to set
	 */
	public void setgIDMaxStringLength(int gIDMaxStringLength) {
		this.gIDMaxStringLength = gIDMaxStringLength;
	}

	/**
	 * 
	 * @return true if store is empty, false otherwise
	 */
	public boolean isStoreEmpty() {
		return storeEmpty;
	}

	/**
	 * 
	 * @param storeEmpty
	 */
	public void setStoreEmpty(boolean storeEmpty) {
		this.storeEmpty = storeEmpty;
	}
	
	/**
	 * 
	 * @param direct
	 * @return
	 */
	public int getMaxColorsRequired(boolean direct)
	{
		int maxColors = 0;
		
		com.ibm.wala.util.collections.Pair<Map<String,Integer>,Map<String,Integer>> pair = (direct)? dphMappings : rphMappings;
		
		if(pair == null)
			return 0;
		
	    Iterator<Integer> valueIteraror = pair.fst.values().iterator();
	    while(valueIteraror.hasNext())
	    {
	    	Integer i = valueIteraror.next();
    		if (i > maxColors) {
    			maxColors = i;
    		}
	    }
	    
	    valueIteraror = pair.snd.values().iterator();
	    
	    while(valueIteraror.hasNext())
	    {
	    	Integer i = valueIteraror.next();
    		if (i > maxColors) {
    			maxColors = i;
    		}
	    }
		
		return maxColors;
		
	}
	
}
