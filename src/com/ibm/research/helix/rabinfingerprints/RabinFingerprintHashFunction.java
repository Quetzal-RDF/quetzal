package com.ibm.research.helix.rabinfingerprints;

import com.ibm.research.helix.hash.IHashFunction;
import com.ibm.research.helix.polynomial.IPolynomial;
import com.ibm.research.helix.polynomial.LongBasedPolynomial;

public class RabinFingerprintHashFunction<T> implements IHashFunction<T> {

	private IToBitConverter<T> converter;
	private IPolynomial irreductibleP;
	public RabinFingerprintHashFunction(IToBitConverter<T> converter,  IPolynomial irreductibleP) {
		this.converter = converter;
		this.irreductibleP = irreductibleP;
	}

	public long getRange() {
		return irreductibleP.getLongRepresentation();
	}
	
/*	public static long invertBit(long v) {
		System.out.println(Long.toBinaryString(v));
		long u = 0;
		while (v > 0) {
			if ((v & 1)== 1)
				u = (u << 1) + 1;
			v >>= 1;
		}
		System.out.println(Long.toBinaryString(u));
		return u;
	}
*/
	
	public long hash(T object) {
		//IPolynomial m = new LongBasedPolynomial(Math.abs(converter.toLong(object)) | ((long)1)<< irreductibleP.getDegree());
		long value = converter.toLong(object);
		long degree = irreductibleP.getDegree();

//		if (value < irreductibleP.getLongRepresentation()) 
		{
			int c2=0x27d4eb2d; // a prime or an odd constant
			value = (value ^ 61) ^ (value >>> 16);
			value = value + (value << 3);
			value = value ^ (value >>> 4);
			value = value * c2;
			value = value ^ (value >>> 15);
		}

/*		value = (~value) + (value << 21); // key = (key << 21) - key - 1;
		value = value ^ (value >>> 24);
		value = (value + (value << 3)) + (value << 8); // key * 265
		value = value ^ (value >>> 14);
		value = (value + (value << 2)) + (value << 4); // key * 21
		value = value ^ (value >>> 28);
		value = value + (value << 31);
*/
		IPolynomial m = new LongBasedPolynomial(value);
		m.mod(new LongBasedPolynomial(irreductibleP.getLongRepresentation()));

		return Math.abs(m.getLongRepresentation());
	}
	
	public static void main(String[] args) {

	}


}
