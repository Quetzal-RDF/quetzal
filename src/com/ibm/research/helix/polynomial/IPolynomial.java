package com.ibm.research.helix.polynomial;

public interface IPolynomial {

	public abstract long getDegree();

	/*public BitSet getBitSetRepresentation() {
		return (BitSet) rep.clone();
	}*/
	public abstract long getLongRepresentation();

	
	/**
	 * returns this mod p
	 */
	public abstract void mod(IPolynomial p);

	/**
	 * returns gcd(this, p). 
	 * Implementation using Euclide's algorithm
	 * @param p
	 * @return
	 */
	public abstract IPolynomial gcd(IPolynomial p);

	public abstract boolean isZero();

}