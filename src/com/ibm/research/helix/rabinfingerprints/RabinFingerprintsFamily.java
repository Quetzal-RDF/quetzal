package com.ibm.research.helix.rabinfingerprints;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;

import com.ibm.research.helix.hash.IHashFunction;
import com.ibm.research.helix.hash.IHashFunctionFamily;
import com.ibm.research.helix.polynomial.BitSetBasedPolynomial;
import com.ibm.research.helix.polynomial.IPolynomial;
import com.ibm.research.helix.polynomial.LongBasedPolynomial;

/**
 * This class generates a family of Rabinfingerprint hash functions. For a degree (a prime number), the number
 * of hash functions in the family is (2^degree-2)/degree;
 * 
 * @author Songyun Duan
 *
 * @param <T>: T can be String or Integer/Long
 */
public class RabinFingerprintsFamily<T>  implements IHashFunctionFamily<T> {

//	private static String irreduciblePolyFile = "polynomials.csv";
	private static String irreduciblePolyFile = "IrreduciblePolynomial/polynomials.csv";
//	private static final byte[] PRIME_NUMBERS_LESS_THAN_64= new byte[] {
//		2,	3, 	5, 	7,	11,	13,	17,	19,	23 , 29 , 31 , 37 , 41, 43,	47,	53,	59,	61};
	private IToBitConverter<T> converter;
	private int degree;
	//private List<BitSetBasedPolynomial> seedIrreduciblePolynomials;
	private long size;
	
	public RabinFingerprintsFamily(IToBitConverter<T> converter) {
		this.converter = converter;
		this.degree = 37;
		size =Math.round((Math.pow(2,degree)-2)/degree); //seedIrreduciblePolynomials.size();
	}
	
	public RabinFingerprintsFamily(IToBitConverter<T> converter, int degree) {
		this.converter = converter;
		this.degree = degree;
		//seedIrreduciblePolynomials = new ArrayList<BitSetBasedPolynomial>();
		//seedIrreduciblePolynomials.addAll(BitSetBasedPolynomial.genIrreduciblePolynomials(degree, Integer.MAX_VALUE)); //((1<<degree)-2)/degree));
		size =Math.round((Math.pow(2,degree)-2)/degree); //seedIrreduciblePolynomials.size();
	}
	
/*	*//**
	 * 
	 * @param converter 
	 * @param primeDegreeForIrreduciblePolynomial must be a prime number less than or equal to 62
	 *//*
	public RabinFingerprintsFamily(IToBitConverter<T> converter, byte primeDegreeForIrreduciblePolynomial) {
		this.converter = converter;
		this.degree = primeDegreeForIrreduciblePolynomial;
		if (degree>62) {
			throw new IllegalArgumentException("The degree of the irreducible polynomials must be less than or equal to 62");
		}
		// simple primality test
		boolean isPrime = false;
		for (int i=0; i<PRIME_NUMBERS_LESS_THAN_64.length 
					&& degree>=PRIME_NUMBERS_LESS_THAN_64[i]; i++) {
			if (degree==PRIME_NUMBERS_LESS_THAN_64[i]) {
				isPrime = true;
			}
		}
		//
		if (!isPrime) {
			throw new IllegalArgumentException("The degree of the irreducible polynomials must be a prime number");
		}
		size =(long) ((Math.pow(2, degree)-2)/degree);
	}
*/	
	public Iterator<IHashFunction<T>> getFirstKMembers(int k) {
		return getRandomMembers(k, null);
	}
	
	/**
	 * Generate a set of RabinFingerprint hash functions
	 * @param k: number of hash functions
	 * @param degree: irreducible polynomial degree
	 * @param seed: for random number generator
	 * @return
	 */
	public Iterator<IHashFunction<T>> getRabinFingerprintHashFunctions(int k, int seed) {
		List<IPolynomial> listOfPolys = new ArrayList<IPolynomial>(); 
		try {
			listOfPolys = readIrreduciblePolys(irreduciblePolyFile, degree, k, seed);
		}
		catch(IOException e) {
			System.err.print(e.getMessage());
		}
		if (listOfPolys.size() == 0) {
			return getRandomMembers(k, new Random(seed));
		}
		
		List<IHashFunction<T>> listOfHashFuncs = new ArrayList<IHashFunction<T>>();
		for (IPolynomial p : listOfPolys) {
//			System.out.println(degree + ", " + p.getLongRepresentation());
			IHashFunction<T> h = new RabinFingerprintHashFunction<T>(converter, p);
			listOfHashFuncs.add(h);
		}
		return listOfHashFuncs.iterator();
	}
	
	public Iterator<IHashFunction<T>> getRandomMembers(int k, Random rdm) {
		assert k>0;
		Set<IHashFunction<T>> setOfHash = new HashSet<IHashFunction<T>>();
		//Set<Integer> seedIdx = new HashSet<Integer>();
		Set<BitSetBasedPolynomial> polyns;
		if (k >= size || rdm == null) {
			/*for (int i=0; i < size; ++i)
				seedIdx.add(i);*/
			polyns = BitSetBasedPolynomial.genIrreduciblePolynomials(degree, k);
		}
		else {
			/*while (seedIdx.size() < k) {
				int idx = (int)(rdm.nextDouble() * size);
				seedIdx.add(idx);
			}*/
			polyns = BitSetBasedPolynomial.genIrreduciblePolynomials(degree, k, rdm);
		}
		for (BitSetBasedPolynomial p : polyns) {
			LongBasedPolynomial polyn = new LongBasedPolynomial(p.getLongRepresentation());//toBooleanArray());
			IHashFunction<T> tHash = new RabinFingerprintHashFunction<T>(converter, polyn);
			setOfHash.add(tHash);
		}
		/*int n = 0;
		for (int j : seedIdx) {
			if (n < k) {
				LongBasedPolynomial polyn = new LongBasedPolynomial(seedIrreduciblePolynomials.get(j).getLongRepresentation());//toBooleanArray());
				IHashFunction<T> tHash = new RabinFingerprintHashFunction<T>(converter, polyn);
				setOfHash.add(tHash);
			}
			n++;
		}*/
		
		return setOfHash.iterator();
	}

	public long getSize() {
		return size;
	}
	
	/**
	 * 
	 * @param irredPolyFileName
	 * @param degree
	 * @param numPolys
	 * @param seed
	 * @return
	 * @throws IOException
	 */
	public static List<IPolynomial> readIrreduciblePolys(String irredPolyFileName, int degree, int numPolys, int seed) throws IOException {
		 List<IPolynomial> polys = new ArrayList<IPolynomial>();
		 long candidateSize = (long) (Math.pow(2, degree) -2)/degree;
		 Random rdm = new Random(seed);
		 InputStream propertyIn = RabinFingerprintsFamily.class.getClassLoader().getResourceAsStream(irredPolyFileName);
		 BufferedReader reader = new BufferedReader(new InputStreamReader(propertyIn));
		 String line = null;
		 boolean found = false;
		 int idx = 0;
		 while ((line=reader.readLine()) != null) {
//			 System.out.println(line);
			 int currDeg = 0;
			 long polyLongRep = 0;
			 StringTokenizer tokenizer = new StringTokenizer(line, ",");
			 if (tokenizer.hasMoreTokens()) currDeg = Integer.parseInt(tokenizer.nextToken());
			 if (tokenizer.hasMoreTokens())	polyLongRep = Long.parseLong(tokenizer.nextToken().trim());
			 if (currDeg != 0 && polyLongRep != 0) {
				 if (currDeg == degree) {
					 idx++;
					 IPolynomial p = new LongBasedPolynomial(polyLongRep);
					 if (idx <= numPolys)
						 polys.add(p);
					 else {
						 // decide whether to retain the current poly, if yes, replace one of the existing one;
						 double r = rdm.nextDouble();
						 if (r < numPolys / ((double) idx)) {
							 int rint = (int) Math.floor(rdm.nextDouble() * numPolys);
							 polys.set(rint, p);
						 }
					 }
					 found = true;
				 }
				 else if (found)
					 break;
			 }
		 }		 		 
		 reader.close();
		 return polys;
	}
	
	public static void main_test(String[] args) throws IOException {
		String resultFile = args[0];
/*		int[] candDegrees = {2,	3, 	5, 	7,	11,	13,	17,	19,	23 , 29 , 31 , 37}; // , 41, 43,	47,	53,	59,	61};
//		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(resultFile)));
		for (int degree : candDegrees) {
			int numHashFunctions = (int) (Math.pow(2, degree)-2)/degree;
			if (numHashFunctions > 10000)
				numHashFunctions = 10000;
//			List<IHashFunction<String>> hashFunctions = new ArrayList<IHashFunction<String>>();
			RabinFingerprintsFamily<String> rabinFamily = new RabinFingerprintsFamily<String>(new StringToBitConverter(), degree);
			BitSetBasedPolynomial.recordIrreduciblePolynomials(resultFile, degree, numHashFunctions, new Random(100));
		}
*/
		int targetDegree = 37;
		int targetNumPolys = 2;
		int seed = 100;
		List<IPolynomial> listOfPolys = readIrreduciblePolys(resultFile, targetDegree, targetNumPolys, seed);
		System.out.println("The number of polynomials generated: " + listOfPolys.size());
		List<IHashFunction<String>> listOfHashFuncs = new ArrayList<IHashFunction<String>>();
		for (IPolynomial p : listOfPolys) {
			System.out.println(targetDegree + ", " + p.getLongRepresentation());
			IHashFunction<String> h = new RabinFingerprintHashFunction<String>(new StringToBitConverter(), p);
			listOfHashFuncs.add(h);
		}
		String str = "hello world";
		for (IHashFunction<String> h : listOfHashFuncs) {
			System.out.println(str + ", " + ((RabinFingerprintHashFunction<String>)h).getRange() + ", hashed to " + h.hash(str));
		}
	}
	
	public static void main(String[] args) throws IOException {
		int targetRange = 128;
		RabinFingerprintsFamily<String> setOfHash = new RabinFingerprintsFamily<String>(new StringToBitConverter());
		Iterator<IHashFunction<String>> iter = setOfHash.getRabinFingerprintHashFunctions(1, 1000);
		List<IHashFunction<String>> rabinHashList = new ArrayList<IHashFunction<String>>();
		while (iter != null && iter.hasNext() ) {
			rabinHashList.add(iter.next());
		}
		
		int[] hashed = new int[targetRange];
		for (int i = 0; i < hashed.length; ++i) {
			hashed[i] = -1;
		}
		
		BufferedReader reader = new BufferedReader(new FileReader(new File(args[0])));
		String line = null;
		int numLines = 0; 
		
//		int count = 0;
		if (rabinHashList != null) {
			Map<Long, Integer> hashValues = new HashMap<Long, Integer>();
			while ((line = reader.readLine()) != null) {
				numLines++;
				System.out.println(line);
				Set<Long> vals = new HashSet<Long>();
				int k = 0;
				boolean available = true;
				while (k < rabinHashList.size() && available) 
				{
					IHashFunction rabinHash = rabinHashList.get(k++);
					long hashVal = rabinHash.hash(line)%targetRange;
					if (hashed[(int)hashVal] == -1) {
						hashed[(int)hashVal] ++;
						vals.add(hashVal);
						
						if (!hashValues.containsKey(hashVal))
							hashValues.put(hashVal, 0);
						hashValues.put(hashVal, hashValues.get(hashVal)+1);
						System.out.println("Hash " + line + " to " + hashVal);

						available = false;
					}
				}
			}
			
			int countUnused = 0;
			for (int i = 0; i < hashed.length; ++i) {
				if (hashed[i] == -1) 
					countUnused ++;
			}
			
			System.out.println("the number of used buckets is " + (targetRange - countUnused));
			System.out.println("The number of conflicts is " + (numLines -targetRange + countUnused));
			
			int sum = 0;
			for (long val : hashValues.keySet()) {
				sum += (hashValues.get(val)-1);
				if (hashValues.get(val) > 1) {
//					System.out.println("At " + val + ": " + (hashValues.get(val)-1) + " conflicts!");
				}
			}
//			System.out.println("The number of distinct hash values is " + hashValues.size());
			System.out.println("The total number of conflicts is " + sum);					
//			System.out.println("Hashed to the same values: " + count);

			System.out.println("expected conflicts is " + (numLines - (1- Math.pow((targetRange-1) / (double) targetRange, numLines)) * targetRange));
//			System.out.println("expected #hashed to same value is " + (1- Math.pow(127/128.0, 1)) * 2000);
		}
		
		if (reader!= null) {
			reader.close();
		}

		
	}
	
	public static void main_2(String[] args) throws IOException {
		int targetRange = 1028;
//		RabinFingerprintsFamily<String> setOfHash = new RabinFingerprintsFamily<String>(new StringToBitConverter(), degree);
//		Iterator<IHashFunction<String>> iter = setOfHash.getFirstKMembers(2);
		RabinFingerprintsFamily<Long> setOfHash = new RabinFingerprintsFamily<Long>(new LongToBitConverter());
		Iterator<IHashFunction<Long>> iter = setOfHash.getRabinFingerprintHashFunctions(1, 10000);
		List<IHashFunction<Long>> rabinHashList = new ArrayList<IHashFunction<Long>>();
		while (iter != null && iter.hasNext() ) {
			rabinHashList.add(iter.next());
		}
		int count = 0;
		if (rabinHashList != null) {
			Map<Long, Integer> hashValues = new HashMap<Long, Integer>();
			int max = 600;
			for (int i = 0; i < max; ++i) {
				Set<Long> vals = new HashSet<Long>();
				int k = 0;
				while (k < rabinHashList.size()) {
//				while (k < 3) {
					IHashFunction rabinHash = rabinHashList.get(k++);
					long hashVal = rabinHash.hash((long) i)%targetRange;
					vals.add(hashVal);
					if (!hashValues.containsKey(hashVal))
						hashValues.put(hashVal, 0);
					hashValues.put(hashVal, hashValues.get(hashVal)+1);
					System.out.println("Hash " + i + " to " + hashVal);
				}
				if (vals.size() < rabinHashList.size()) 
					count += (rabinHashList.size() - vals.size());
			}
			int sum = 0;
			for (long val : hashValues.keySet()) {
				sum += (hashValues.get(val)-1);
				if (hashValues.get(val) > 1) {
//					System.out.println("At " + val + ": " + (hashValues.get(val)-1) + " conflicts!");
				}
			}
//			System.out.println("The number of distinct hash values is " + hashValues.size());
			System.out.println("The total number of conflicts is " + sum);					
//			System.out.println("Hashed to the same values: " + count);

			System.out.println("expected conflicts is " + (max - (1- Math.pow((targetRange-1) / (double) targetRange, max)) * targetRange));
//			System.out.println("expected #hashed to same value is " + (1- Math.pow(127/128.0, 1)) * 2000);
		}


/*		BufferedReader reader = new BufferedReader(new FileReader(new File(args[0])));
		BufferedReader reader2 = new BufferedReader(new FileReader(new File(args[1])));
		String line;
		Set<String> setOfVals = new HashSet<String>(), setOfVals2 = new HashSet<String>();
		while ((line = reader.readLine()) != null) {
			String[] tokens = line.split(",");
			if (tokens.length > 1) {
				String[] vals = tokens[0].split(";");
				System.out.println("The string is " + vals[1]);
				if (vals.length > 1) {
					String[] elems = vals[1].split("\"");
					if (elems.length > 1);
//					System.out.println("The element is " + elems[1]);
					setOfVals.add(elems[1].toLowerCase());
				}
			}
		}

		while ((line = reader2.readLine()) != null) {
			String[] tokens = line.split(",");
			if (tokens.length > 1) {
				String[] vals = tokens[0].split(";");
				System.out.println("The string is " + vals[1]);
				if (vals.length > 1) {
					String[] elems = vals[1].split("\"");
					if (elems.length > 1);
//					System.out.println("The element is " + elems[1]);
					setOfVals2.add(elems[1].toLowerCase());
				}
			}
		}
		
		int degree = 17;
		RabinFingerprintsFamily<String> setOfHash = new RabinFingerprintsFamily<String>(new StringToBitConverter(), degree);
		MinHashFamily<String> minHashFuncs = new MinHashFamily<String>(setOfHash);
		Iterator<IHashFunction<Collection<String>>> iter = minHashFuncs.getRandomMembers(50, new Random(System.currentTimeMillis()));
		int numOfEuqal = 0, sum = 0;
		while (iter!= null && iter.hasNext()) {
			MinHashFunction<String> curHash = (MinHashFunction<String>) iter.next();
			long val = curHash.hash(setOfVals), val2 = curHash.hash(setOfVals2);
			++sum;
			if (val == val2) ++numOfEuqal;
		}
		double sim = numOfEuqal / (sum+0.0);
		int size1 = setOfVals.size(), size2 = setOfVals2.size();
		System.out.println("The size of sets: set1 = " + size1 + "; set2 = " + size2);
		System.out.println("The estimated intersection of two sets is " + sim/(1+sim) * (size1 + size2));
		
		sum = 0;
		for (String elem : setOfVals) {
			for (String elem2 : setOfVals2) {
				if (elem.equals(elem2)) ++sum;
			}
		}
		System.out.println("The intersection of two sets is " + sum);

		System.out.println("The estimated similarity of two sets is " + sim);
		System.out.println("The similarity of two sets is " + sum /(size1 + size2 - sum + 0.0));
*/
	
	}
}
