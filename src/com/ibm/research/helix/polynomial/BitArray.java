package com.ibm.research.helix.polynomial;


import java.util.Random;

public class BitArray {
	
	public static void main(String[] args) {
		Random r = new Random(100);
		int maxLength = Long.toBinaryString(Long.MAX_VALUE).length();
		for (int i=0;i<100000;i++) {
			long v =Math.abs(r.nextInt());
			//assert v <= Long.MAX_VALUE;
			String vS = Long.toBinaryString(v);
			int vSL = vS.length();
			if (maxLength == vSL) {
				i--;
				continue;
			}
			int shift = r.nextInt(maxLength - vSL);
			if (shift == 0) {
				i--;
				continue;
			}
			long result = v << shift;
			String resultS = Long.toBinaryString(result);
			BitArray ba = new BitArray(v);
			ba.leftShift(shift);
			if (!ba.toString().equals(resultS)) {
				System.err.println(vS + " << " + shift+".\n"+ ba.toString()+"\n"+resultS +"\n length "+ ba.toString().length()+"|"+resultS.length()) ;
				ba = new BitArray(v);
				ba.leftShift(shift);
			} else  {
				System.out.println(vS + " << " + shift);
			}
			result = v >> shift;
			resultS = Long.toBinaryString(result);
			ba = new BitArray(v);
			ba.rightShift(shift);
			if (!ba.toString().equals(resultS)) {
				System.err.println(vS + " >> " + shift+".\n"+ ba.toString()+"\n"+resultS +"\n length "+ ba.toString().length()+"|"+resultS.length()) ;
				ba = new BitArray(v);
				ba.rightShift(shift);
			} else  {
				System.out.println(vS + " >> " + shift);
			}
			
		}
	}
	/**
	 * array[0] contains the first 4 bytes
	 * array[i] contains the (i+1)th 4 bytes
	 */
	private int[] array;
	// position of the most significant bit in the last array
	private int posMostSignBit;
	
	public BitArray(long v) {
		this( (int) (v >> 32) !=0? new int[]{ (int) v,(int) (v >> 32)}: new int[]{(int)v});
	}
	public BitArray(int v) {
		this(new int[]{v});
	}
	public BitArray(int[] array) {
		if (array.length == 0) {
			throw new IllegalArgumentException();
		}
		this.array = array;
		posMostSignBit = computePosMostSignificantBit(array);
		
	}
	
	protected static int computePosMostSignificantBit(int[] array) {
		int posMostSignBit;
		int rep = array[array.length-1];
		if (rep == 0) {
			posMostSignBit = 0;
		} else {
			posMostSignBit = -1;
			while (rep>0) {
				rep= rep >> 1;
				posMostSignBit++;
			}
		}
		return posMostSignBit;
	}
	
	public boolean isZero() {
		return (array[array.length-1] & ((int)1 << posMostSignBit)) ==0;
	}
	
	public void rightShift(int shift) {
		if (shift == 0 || isZero()) {
			return;
		}
		int blocksToRemove = shift / 32;
		int rightShift = shift % 32;
		int newArraySize = array.length-blocksToRemove;
		if (posMostSignBit<rightShift) {
			newArraySize --; 
		}
		if (newArraySize <=0) {
			array = new int[1];
			posMostSignBit =0;
			return;
		}
		int msb = array[array.length-1];
		if (newArraySize != array.length) {
			int[] newArray= new int[newArraySize];
			System.arraycopy(array, array.length - blocksToRemove,newArray, 0, newArraySize);
			array =newArray;
		}
		
		for (int i=0; i<array.length;i++) {
			array[i]>>=rightShift;
			if (i<array.length-1) {
				for (int s=0;s<rightShift;s++) {
					if ((array[i+1] & ((int) 1 << s))!=0) {
						array[i]|= ((int)1 << 31 - (rightShift-1 -s));
					}
				}
			}
		}

		if (posMostSignBit<rightShift) {
			//
			for (int s=0;s<=posMostSignBit;s++) {//s<rightShift;s++) {
				if ((msb & ((int) 1 << s))!=0) {
					array[array.length]|= ((int)1 << 31 - (rightShift-1 -s));
				}
			}
			posMostSignBit = 31 - (rightShift -1 - posMostSignBit); 
			
		} else {
			posMostSignBit -=rightShift;
		}
		

	}
	public void leftShift(int shift) {
		if (shift==0 || isZero()) {
			return;
		}
		int leftZeroBlocks = shift / 32;
		int leftShift = shift % 32;
		int newArraySize = array.length+ leftZeroBlocks;
		if ((posMostSignBit+leftShift)>=32) {
			newArraySize++; // for the overflow
		}
		if (newArraySize != array.length) {
			int[] newArray= new int[newArraySize];
			System.arraycopy(array,0,newArray, leftZeroBlocks,array.length);
			array =newArray;
		}
		int startShift = array.length-1;
		if ((posMostSignBit+leftShift)>=32) {
			// copy the most (32-leftShit) significant bit of array[array.length-1]
			// into newArray[array.length] 
			for (int s=0;s<=leftShift+posMostSignBit-32;s++) {
				if ((array[array.length-2] & ((int)1 << posMostSignBit-s)) !=0) {
					array[array.length-1] |= ((int) 1 << posMostSignBit+leftShift-32-s);
				}
			}
			startShift = array.length -2;
			posMostSignBit = posMostSignBit+leftShift-32;
		} else {
			posMostSignBit += leftShift;
		}
		if (leftShift!=0) {
			for (int i =startShift; i>=leftZeroBlocks; i-- ) {
				//left shift
				array[i] <<=leftShift;
				if (i>leftZeroBlocks) {
					// copy the most leftShift significant bit of array[i-leftZeroBlocks-1]
					// into newArray[i] 
					for (int s=0;s<leftShift;s++) {
						if ( (array[i-1] & ((int)1 << 31-s)) != 0 ) {
							array[i] |= ((int)1 << leftShift-1-s);
						}
					}
				}
			}
		}
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i=array.length-1;i>=0;i--) {
			String st = Integer.toBinaryString(array[i]);
			if (i!=array.length-1) {
				for (int k=0;k<32-st.length();k++) {
					buf.append("0");
				}
			}
			buf.append(Integer.toBinaryString(array[i]));
		}
		return buf.toString();
	}

	public void and(BitArray ba) {
		if (ba.array.length<array.length) {
			int min = ba.array.length;
			int[] newArray = new int[min];
			System.arraycopy(array, 0, newArray, 0, min);
			array = newArray;
		}
		for (int i=array.length-1;i>=0;i--) {
			array[i]&=ba.array[i];
		}
		removeTrailingZeros();
	}
	public void xor(BitArray ba) {
		if (ba.array.length>array.length) {
			int max = ba.array.length;
			int[] newArray = new int[max];
			System.arraycopy(array, 0, newArray, 0, array.length);
			array = newArray;
		}
		for (int i=array.length-1;i>=0;i--) {
			array[i]^=ba.array[i];
		}
		removeTrailingZeros();
	}
	public void or(BitArray ba) {
		if (ba.array.length>array.length) {
			int max = ba.array.length;
			int[] newArray = new int[max];
			System.arraycopy(array, 0, newArray, 0, array.length);
			array = newArray;
		}
		for (int i=array.length-1;i>=0;i--) {
			array[i]|=ba.array[i];
		}
		//removeTrailingZeros();
		posMostSignBit = computePosMostSignificantBit(array);
	}
	protected void removeTrailingZeros() {
		int size = 0;
		for (int i=array.length-1;i>=0;i--) {
			if (array[i]!=0) {
				size = i+1;
				break;
			}
		}
		if (size == 0) {
			array = new int[1];
			posMostSignBit =0;
			return;
		} 
		if (size!=array.length) {
			int[] newArray = new int[size];
			System.arraycopy(array, 0, newArray, 0, size);
			array = newArray;
		} 
		posMostSignBit = computePosMostSignificantBit(array);
	}
	
	public BitArray copy() {
		int[] newArray = new int[array.length];
		System.arraycopy(array, 0,newArray ,0, array.length);
		return new BitArray(newArray);
	}
}
