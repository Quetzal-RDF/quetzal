package com.ibm.research.helix.polynomial;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * 
 * @author achille, Songyun Duan
 *
 */
public class BitSetBasedPolynomial implements IPolynomial {

	// the byte representation of the polynomial coefficients
	private BitSet rep;
	/**
	 *  b[0] + b[1]*X + b[2]*X^2 +...+ b[n-1]*X^(n-1)
	 * @param coefs
	 */
	public BitSetBasedPolynomial(boolean[] coefs) {
		rep = new BitSet();
		for (int i = 0; i < coefs.length; ++i) {
			if (coefs[i]) rep.set(i);
		}
	}
	public BitSetBasedPolynomial(BitSet bs) {
		rep = bs;//(BitSet) bs.clone();
	}
	public BitSetBasedPolynomial(long longRepresentation) {
		if (longRepresentation<0) {
			throw new IllegalArgumentException();
		}
		rep = new BitSet();
		int k = 0;
		while (longRepresentation != 0) {// && longRepresentation!=-1) {
			if ((longRepresentation & 1) > 0)
				rep.set(k);
			longRepresentation = longRepresentation >> 1;
			++k;
		}
	}

	public boolean[] toBooleanArray() {
		int length = rep.length();
		if (length == 0) {
			return new boolean[]{false};
		}
		boolean[] ret = new boolean[length];
		for (int i=0;i<length;i++) {
			ret[i] = rep.get(i);
		}
		return ret;
	}
	/* (non-Javadoc)
	 * @see com.ibm.research.helix.polynomial.IPolynomial#getDegree()
	 */
	public long getDegree() {
		return rep.length()-1;
	}
	
	/*public BitSet getBitSetRepresentation() {
		return (BitSet) rep.clone();
	}*/
	/* (non-Javadoc)
	 * @see com.ibm.research.helix.polynomial.IPolynomial#getLongRepresentation()
	 */
	public long getLongRepresentation() {
		if (getDegree() > 63)
			return -1;
		else {
			long result = 0;
			for (int i = 0; i < rep.length(); ++i) 
				if (rep.get(i)) 
					result += ((long)1 <<i);
			return result;
		}
	}
	
	/*public Polynomial substract(Polynomial p) {
		BitSet bs = getBitSetRepresentation();
		bs.xor(p.getBitSetRepresentation());
		return new Polynomial(bs);
	}*/
	
	/**
	 * check whether n is a prime number
	 * @param n
	 * @return
	 */
	public boolean primeTest(int n) {
		boolean ret = true;
		for (int i = 2; i <= Math.sqrt(n) & ret; ++i) {
			if (n % i == 0) 
				ret = false;
		}
		return ret;
	}
	public List<Integer> primeFactor(int n) {
		List<Integer> factors = new ArrayList<Integer>();
		for (int i = n; i > 1; --i) {
			if (n % i == 0 & primeTest(i)) {
				factors.add(i);
			}
		}
		return factors;
	}
	
	public static int numOfBitOne(long value) {
		int k = 0;
		while (value > 0) {
			k++;
			value &= (value -1);
		}
		return k;
	}
	
	public static long numOfBitOne(BitSet value) {
		long k = 0;
		for (int i = 0; i < value.length(); ++i) 
			if (value.get(i)) ++k;
		return k;
	}
	
	/**
	 * multiplication with a polynomial over finite space Z2
	 * @param p
	 * @return
	 * @author Songyun Duan
	 */
//	public Polynomial multiply(Polynomial p) {
//		BitSet u = getBitSetRepresentation();
//		BitSet v = p.getBitSetRepresentation();
//		long d1 = this.getDegree(), d2 = p.getDegree();
//		if (d1 < d2) {
//			BitSet tmp = (BitSet) v.clone();
//			v = (BitSet) u.clone();
//			u = tmp;
//		}
//		BitSet result = new BitSet();
//		while (v.length() > 0) {
//			if (v.get(0)) 
//				result.xor(u);
//			v = shift(v, -1);
//			u = shift(u, 1);
//		}
//		return new Polynomial(result);
//	}
	
	/*public Polynomial add(Polynomial p) {
		BitSet u = getBitSetRepresentation();
		u.xor(p.getBitSetRepresentation());
		return new Polynomial(u);
	}*/
	/*public Polynomial power(long currDegree) {
		Polynomial p = new Polynomial(getBitSetRepresentation());
		Polynomial result = new Polynomial(1);
		while (currDegree > 0) {
			if ((currDegree & 1) > 0) {
				result = result.multiply(p);
			}
			currDegree = (currDegree >> 1);
			p = p.multiply(p);
		}
		return result;
	}*/
	
	/**
	 * @param degree: the degree of the generated monic polynomial; should be less than 62
	 * @param k: the number of polynomials to be generated
	 * @return
	 * @author Songyun Duan
	 */
	public static Set<BitSetBasedPolynomial> genIrreduciblePolynomials(int degree, int k) {
		Set<Long> values = new HashSet<Long>();
		Set<BitSetBasedPolynomial> polynomials = new HashSet<BitSetBasedPolynomial>();
		if (degree>63 /*|| degree < 1 || k > (Math.pow(2, degree) - 2)/degree*/) {
			//System.out.print("#polynomials for degree " + degree + " should be less than " + (Math.pow(2, degree)-2)/degree);
			throw new IllegalArgumentException();
		}
		
		for (long i = 0; (i < ((long)1<<(degree-1))) && values.size() < k; ++i) {
			long bitArray = ((long) 1<<degree) + 1 + (i <<1);
			BitSetBasedPolynomial tmp = new BitSetBasedPolynomial(bitArray);
//			System.out.println("trying " + tmp.toString());
			if (!values.contains(bitArray) && tmp.irreducibilityTest()) {
				values.add(bitArray);
				polynomials.add(tmp);
//				System.out.println(tmp.toString());
			}		
		}
		
		return polynomials;
	}
	
	public static void recordIrreduciblePolynomials(String resultFileName, int degree, int maxSize, Random rnd) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(resultFileName), true));
		Set<Long> valuesTried = new HashSet<Long>();
		int count = 0;
//		Set<Long> values = new HashSet<Long>();
//		Set<BitSetBasedPolynomial> polynomials = new HashSet<BitSetBasedPolynomial>();
		if (degree>63 ) {
			throw new IllegalArgumentException();
		}
		long numberOfCandidates = ((long)1<<(degree-1));
		while(count < maxSize && valuesTried.size() < numberOfCandidates) {
			long bitArray  = 1 +  (((long) 1)<<degree);
			for (int d=1;d<=degree-1;d++) {
				if (rnd.nextBoolean()) {
					bitArray += ( ((long)1) << d);
				}
			}
			if (valuesTried.add(bitArray)) {
				BitSetBasedPolynomial tmp;  
				if ((tmp= new BitSetBasedPolynomial(bitArray)).irreducibilityTest()) {
					count++;
					writer.append(degree + "," + tmp.getLongRepresentation() + "\n");
				}		
			}
		}
		System.out.println("degree = " + degree + ", #functions expected: " + maxSize + "; #functions generated: " + count);
		if (writer != null ) {
			writer.flush();
			writer.close();
		}

	}
	
	public static Set<BitSetBasedPolynomial> genIrreduciblePolynomials(int degree, int maxSize, Random rnd) {
		Set<Long> valuesTried = new HashSet<Long>();
		Set<Long> values = new HashSet<Long>();
		Set<BitSetBasedPolynomial> polynomials = new HashSet<BitSetBasedPolynomial>();
		if (degree>63 /*|| degree < 1 || k > (Math.pow(2, degree) - 2)/degree*/) {
			throw new IllegalArgumentException();
		}
		long numberOfCandidates = ((long)1<<(degree-1));
//		while (values.size()<maxSize && values.size()<numberOfCandidates) {
		while(values.size() < maxSize && valuesTried.size() < numberOfCandidates) {
			long bitArray  = 1 +  (((long) 1)<<degree);
			for (int d=1;d<=degree-1;d++) {
				if (rnd.nextBoolean()) {
					bitArray += ( ((long)1) << d);
				}
			}
			if (valuesTried.add(bitArray)) {
				BitSetBasedPolynomial tmp;  
	//			System.out.println("trying " + tmp.toString());
				if ((tmp= new BitSetBasedPolynomial(bitArray)).irreducibilityTest()) {
					values.add(bitArray);
					polynomials.add(tmp);
	//				System.out.println(tmp.toString());
				}		
			}
		}
		return polynomials;
	}
	/**
	 * @return a boolean value indicating whether the polynomial is irreducible
	 * @author Songyun Duan
	 */
	//	public boolean irreducibilityTest2(){
	//		// if the number of 1 bits in the polynomial is even, it is reducible
	//		if (numOfBitOne(getBitSetRepresentation()) % 2 == 0) 
	//			return false;
	//		Polynomial h = new Polynomial(2);
	//		int degree = (int) getDegree();
	//		List<Integer> factors = primeFactor(degree);
	//		Vector<Integer> tmpArray = new Vector<Integer>();
	//		tmpArray.add(0);
	//		for (int i : factors) {
	//			tmpArray.add(degree/i);
	//		}
	//		for (int i = 1; i < tmpArray.size(); ++i) {
	//			long currDegree = (long)1 << (tmpArray.get(i) - tmpArray.get(i-1));
	//			h = h.power(currDegree);
	//			h = h.mod(this);
	//			Polynomial g = gcd(h.substract(new Polynomial(2)));
	//			if (g.getLongRepresentation() != 1) return false;		
	//		}
	//		
	//		long tmp = (long)1 << (degree - tmpArray.lastElement());
	//		h = h.power(tmp).substract(new Polynomial(2));
	//		h = h.mod(this);
	//		if (h.isZero()) return true;
	//		else return false;
	//	}
	//	
	/*public boolean irreducibilityTest3(){
		// if the number of 1 bits in the polynomial is even, it is reducible
		if (numOfBitOne(getBitSetRepresentation()) % 2 == 0) 
			return false;
		int degree = (int) getDegree();
		List<Integer> factors = primeFactor(degree);
		Vector<Integer> tmpArray = new Vector<Integer>();
		for (int k : factors) {
			tmpArray.add(degree/k);
		}
		BitSet bs = new BitSet();
		for (int k : tmpArray) {
			bs.clear();
			bs.set(0);
			bs = shift(bs, 1 << k);
			Polynomial g = gcd(new Polynomial(bs).substract(new Polynomial(2)).mod(this));
			if (g.getLongRepresentation() != 1) return false;
		}
		bs.clear();
		bs.set(0);
		bs = shift(bs, 1 << degree);
		Polynomial g = new Polynomial(bs).substract(new Polynomial(2)).mod(this);
		if (g.isZero()) return true;
		else return false; 
	}*/
	
	public boolean irreducibilityTest(){
		// if the number of 1 bits in the polynomial is even, it is reducible
		if (numOfBitOne(rep) % 2 == 0) 
			return false;
		int degree = (int) getDegree();
		BitSet bs = new BitSet();
		for (int k =1; k <= degree/2;k++) {
			bs.clear();
			bs.set(1<<k);
			bs.set(1);
			IPolynomial bsPolynomial = new BitSetBasedPolynomial(bs);
			bsPolynomial.mod(this);
			IPolynomial gcd = this.gcd(bsPolynomial);
			if (gcd.getLongRepresentation() != 1) return false;
		}
		return true;
	}
	
	/**
	 * @return a boolean value indicating whether the polynomial is irreducible
	 * @author Songyun Duan
	 */
//	public boolean irreducibilityTest2(){
//		// if the number of 1 bits in the polynomial is even, it is reducible
//		if (numOfBitOne(getBitSetRepresentation()) % 2 == 0) 
//			return false;
//		Polynomial h = new Polynomial(2);
//		int degree = (int) getDegree();
//		List<Integer> factors = primeFactor(degree);
//		Vector<Integer> tmpArray = new Vector<Integer>();
//		tmpArray.add(0);
//		for (int i : factors) {
//			tmpArray.add(degree/i);
//		}
//		for (int i = 1; i < tmpArray.size(); ++i) {
//			long currDegree = (long)1 << (tmpArray.get(i) - tmpArray.get(i-1));
//			h = h.power(currDegree);
//			h = h.mod(this);
//			Polynomial g = gcd(h.substract(new Polynomial(2)));
//			if (g.getLongRepresentation() != 1) return false;		
//		}
//		
//		long tmp = (long)1 << (degree - tmpArray.lastElement());
//		h = h.power(tmp).substract(new Polynomial(2));
//		h = h.mod(this);
//		if (h.isZero()) return true;
//		else return false;
//	}
//	
	/* (non-Javadoc)
	 * @see com.ibm.research.helix.polynomial.IPolynomial#mod(com.ibm.research.helix.polynomial.Polynomial)
	 */
	public void mod(IPolynomial polyn) {
		BitSetBasedPolynomial p = (BitSetBasedPolynomial) polyn;
		if (p.isZero()) {
			throw new IllegalArgumentException();
		}
		if (p.getDegree()>getDegree()) {
			return;
		}
		BitSet divisor = p.rep;
		long divisorDegree = p.getDegree();
		BitSet dividend = rep;
		long dividendDegree = getDegree();
		// aligning divisor and dividend
		divisor = shift(divisor, dividendDegree - divisorDegree);
		//
		while (dividend.length()-1>=p.getDegree() && dividend.length() > 0) {
			long prevLen = dividend.length();
			dividend.xor(divisor);
			if (dividend.length() > 0) {
				long offset = prevLen - dividend.length();
				divisor = shift(divisor, 0-offset);
			}
		}
		//return new Polynomial(dividend);
		rep = dividend;
	}
	
		public static BitSet shift(BitSet bs, long offset) {
		BitSet result = new BitSet();
		int idx = bs.nextSetBit(0);
		while (idx >= 0) {
			if (idx + offset >= 0) {
				result.set((int) (idx+offset));
			}
			idx = bs.nextSetBit(idx + 1);
		}
		return result;
	}
	/* (non-Javadoc)
	 * @see com.ibm.research.helix.polynomial.IPolynomial#gcd(com.ibm.research.helix.polynomial.Polynomial)
	 */
	public IPolynomial gcd(IPolynomial polyn) {
		BitSetBasedPolynomial p = (BitSetBasedPolynomial) polyn;
		p = new BitSetBasedPolynomial((BitSet)p.rep.clone());
		BitSetBasedPolynomial a = new BitSetBasedPolynomial((BitSet)rep.clone());
		BitSetBasedPolynomial b = p;
		while (!b.isZero()) {
			BitSetBasedPolynomial temp = b;
			a.mod(b);
			b = a;
			a = temp;
		}
		return a;
	}
	
	/* (non-Javadoc)
	 * @see com.ibm.research.helix.polynomial.IPolynomial#isZero()
	 */
	public boolean isZero() {
		return rep.length() == 0;
	}
	
	public String toString() {
		String str = "";
		for (int i = rep.length()-1; i >= 0; --i) {
			str = str.concat(rep.get(i)?"1":"0");
		}
		return str;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		IPolynomial a = new BitSetBasedPolynomial(new boolean[]{true, false, false, false, true, true});//new Polynomial(new boolean[]{false, true, false, true, false, true});//
		IPolynomial b =new BitSetBasedPolynomial(new boolean[]{true,true,true});//new Polynomial(new boolean[]{true,true, false, true}); // new Polynomial(new boolean[]{true, true, true, true,true});//
//		System.out.println(a.toString());
		
		int degree = 17;
		long time = System.currentTimeMillis();
		Set<BitSetBasedPolynomial> polynomials = genIrreduciblePolynomials(degree, Integer.MAX_VALUE);
		time = System.currentTimeMillis() - time;
		for (IPolynomial p:polynomials) {
//			System.out.println("irreducible polynomial: " + p.toString());
		}
		System.out.println("The theoretical number of polynomials is " + ((1<<degree)-2)/degree);
		System.out.println("The actual number of polynomials is " + polynomials.size());
		System.out.println("time: "+ (time/1000)+" in sec"); 
		
	}

}
