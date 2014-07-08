package com.ibm.research.helix.polynomial;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Binary polynomial of maximal degree 62.
 * 
 * @author achille
 *
 */
public class LongBasedPolynomial implements IPolynomial{

	// the byte representation of the polynomial coefficients
	private long rep;
	private byte degree;
	/**
	 *  b[0] + b[1]*X + b[2]*X^2 +...+ b[n-1]*X^(n-1)
	 * @param coefs
	 */
	public LongBasedPolynomial(boolean[] coefs) {
		if (coefs.length>63) {
			throw new IllegalArgumentException();
		}
		rep = 0;
		for (int i=0;i<coefs.length;i++) {
			rep |= coefs[i]?(1 << i) : 0;
			if (coefs[i]) {
				degree = (byte) i;
			}
		}
		assert Long.toBinaryString(rep).length()-1 == degree : Long.toBinaryString(rep)+" degree "+degree;
		
	}
	public LongBasedPolynomial(long longRepresentation) {
		rep = longRepresentation;
		if (rep<0) {
//			throw new IllegalArgumentException();
		}
		if (rep == 0) {
			degree = 0;
		} else {
			degree = -1;
			while (longRepresentation!=0) {
				longRepresentation = longRepresentation >>> 1;
				degree++;
			}
		}
		//System.out.println();
		//assert Long.toBinaryString(rep).length()-1 == degree : Long.toBinaryString(rep)+" degree "+degree;
		
	}

	public long getDegree() {
		return degree;
	}
	
	public long getLongRepresentation() {
		return rep;
	}
	
	public LongBasedPolynomial substract(LongBasedPolynomial p) {
		long a = getLongRepresentation(), b = p.getLongRepresentation();
		long c = a ^ b;
		return new LongBasedPolynomial(c);
	}
	
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
	
	/**
	 * multiplication with a polynomial over finite space Z2
	 * @param p
	 * @return
	 * @author Songyun Duan
	 */
/*	public LongBasedPolynomial multiply(LongBasedPolynomial p) {
		long u = this.getLongRepresentation();
		long v = p.getLongRepresentation();
		int d1 = this.getDegree(), d2 = p.getDegree();
		if (d1 < d2) {
			long tmp = v;
			v = u;
			u = tmp;
		}
		long result = 0;
		while (v > 0) {
			if ((v & 1) > 0) 
				result ^= u;
			v = (v >> 1);
			u = (u << 1);
		}
		return new LongBasedPolynomial(result);
	}*/
	
	/*public LongBasedPolynomial add(LongBasedPolynomial p) {
		long u = this.getLongRepresentation();
		long v = p.getLongRepresentation();
		return new LongBasedPolynomial(u ^ v);
	}
	public LongBasedPolynomial power(long k) {
		LongBasedPolynomial p = new LongBasedPolynomial(this.getLongRepresentation());
		LongBasedPolynomial result = new LongBasedPolynomial(1);
		while (k > 0) {
			if ((k & 1) > 0) {
				result = result.multiply(p);
			}
			k = (k >> 1);
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
	/*public static List<LongBasedPolynomial> genIrreduciblePolynomials(int degree, int k) {
		Set<Long> values = new HashSet<Long>();
		List<LongBasedPolynomial> polynomials = new ArrayList<LongBasedPolynomial>();
		if (degree>63 || degree < 1 || k > (Math.pow(2, degree) - 2)/degree) {
			System.out.print("#polynomials for degree " + degree + " should be less than " + (Math.pow(2, degree)-2)/degree);
			throw new IllegalArgumentException();
		}
		
		for (long i = 0; (i < 1<<(degree-1)) && values.size() < k; ++i) {
			long bitArray = (1<<degree) + 1 + (i <<1);
			LongBasedPolynomial tmp = new LongBasedPolynomial(bitArray);
			System.out.println("trying " + Long.toBinaryString(bitArray));
			if (!values.contains(bitArray) && tmp.irreducibilityTest()) {
				values.add(bitArray);
				polynomials.add(tmp);
				System.out.println(Long.toBinaryString(bitArray));
			}		
		}
		
		return polynomials;
	}*/
	/**
	 * @return
	 * @author Songyun Duan
	 */
	/*public boolean irreducibilityTest(){
		// if the number of 1 bits in the polynomial is even, it is reducible
		if (numOfBitOne(this.getLongRepresentation()) % 2 == 0) 
			return false;
		LongBasedPolynomial h = new LongBasedPolynomial(2);
		int degree = (int) getDegree();
		List<Integer> factors = primeFactor(degree);
		Vector<Integer> tmpArray = new Vector<Integer>();
		tmpArray.add(0);
		for (int i : factors) {
			tmpArray.add(degree/i);
		}
		for (int i = 1; i < tmpArray.size(); ++i) {
			long currDegree = (long) 1 << (tmpArray.get(i) - tmpArray.get(i-1));
			h = h.power(currDegree).mod(this);
			LongBasedPolynomial g = gcd(h.substract(new LongBasedPolynomial(2)));
			if (g.getLongRepresentation() != 1) return false;		
		}
		
		int tmp = 1 << (degree - tmpArray.lastElement());
		h = (h.power(tmp).substract(new LongBasedPolynomial(2))).mod(this);
		if (h.getLongRepresentation() == 0) return true;
		else return false;
	}*/
	
	/**
	 * returns this mod p
	 */
	public void mod(IPolynomial polyn) {
//		System.out.println("in mod: "+binaryRep+" mod "+p.binaryRep);
//		System.out.println("Degree dividend: "+ getDegree()+", degree divisor:  "+ p.getDegree());
		LongBasedPolynomial p = (LongBasedPolynomial) polyn;
		if (p.isZero()) {
			throw new IllegalArgumentException();
		}
		if (p.getDegree()>getDegree()) {
			return;
		}
		long divisor = p.getLongRepresentation();
		long divisorDegree = p.getDegree();
		long dividend = getLongRepresentation();
		long dividendDegree = getDegree();
		// aligning divisor and dividend
		divisor = divisor << (dividendDegree - divisorDegree);
		
		while (dividendDegree>=p.getDegree() && dividend !=0) {
			dividend = (dividend^divisor);
			if (dividend != 0) {
				long rightShift = 0;
				while ( ((dividend & ((long) 1 << dividendDegree)) == 0)) {
					dividendDegree --;
					rightShift++;
				}
				divisor = divisor >> rightShift;
			} else {
				dividendDegree = 0;
			}
			
		}
		rep = dividend;
		degree = (byte) dividendDegree;
	}
	/**
	 * returns gcd(this, p). 
	 * Implementation using Euclide's algorithm
	 * @param p
	 * @return
	 */
	public IPolynomial gcd(IPolynomial p) {
		LongBasedPolynomial a = new LongBasedPolynomial(this.rep);
		LongBasedPolynomial b = (LongBasedPolynomial) p;
		b = new LongBasedPolynomial(b.rep);
		while (!b.isZero()) {
			LongBasedPolynomial temp = b;
			a.mod(b);
			b = a;
			a = temp;
		}
		return a;
	}
	
	public boolean isZero() {
		return rep == 0;
	}
	
	public String toString() {
		return Long.toString(rep, 2);
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//System.out.println(2^3);
		Random rnd = new Random(100); //101010
		LongBasedPolynomial a = new LongBasedPolynomial(new boolean[]{true, false, false, false, true, true});//new Polynomial(new boolean[]{false, true, false, true, false, true});//
		LongBasedPolynomial b =new LongBasedPolynomial(new boolean[]{true,true,true});//new Polynomial(new boolean[]{true,true, false, true}); // new Polynomial(new boolean[]{true, true, true, true,true});//
//		LongBasedPolynomial c = a.gcd(b);
//		System.out.println(c.toString());
/*		for (int i=0;i<0;i++) {
			
			long divisor = Math.abs(rnd.nextInt());
			long dividend = divisor + Math.abs(rnd.nextInt());
			Polynomial m = new Polynomial(dividend).mod(new Polynomial(divisor));
			if (m.getLongRepresentation() == dividend % divisor ) {
				System.out.println("CORRECT : ");
			} else {
				System.out.println("EXPECTED : "+dividend % divisor + " | RESULT : "+m.getLongRepresentation());
			}
		}*/
		
/*		System.out.println("The prime number for the degree:");
		boolean testResult = a.irreducibilityTest();
		if (testResult)
			System.out.println("The polynomial " + a.toString() + " is irreducible");
		else
			System.out.println("The polynomial " + a.toString() + " is reducible");
*/		
	/*	long startTime = System.currentTimeMillis();
		List<LongBasedPolynomial> polynomials = genIrreduciblePolynomials(9, 20);
		System.out.println("Running time is " + (System.currentTimeMillis() - startTime)/1000000.0 + " seconds.");*/
	}

}
