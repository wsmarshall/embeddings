package codebase;

import java.io.Serializable;

//modified from and with thanks to Suraj Mishra's Tuple class

public class Pair<String1, String2> implements Serializable {// for implementing edges
	/**
	 * serialVersionUID added as default value (nothing being done with that right
	 * now)
	 */
	private static final long serialVersionUID = 1L;
	// TODO account for direction - a getDirection public method which takes in an
	// originating edge
	// and spits out positive (for incoming) and negative (for outgoing) results?
	private String val1;
	private String val2;

	public Pair(String val1, String val2) {
		this.val1 = val1;
		this.val2 = val2;
	}

	public static <String1, String2> Pair<String, String> of(String val1, String val2) {
		return new Pair<String, String>(val1, val2);
	}

	/*
	 * public boolean equals(Pair<String, String> p) {
	 * System.out.println("pair equals: " + (this.getVal1().equals(p.getVal1()) &&
	 * (this.getVal2().equals(p.getVal2())))); return
	 * (this.getVal1().equals(p.getVal1()) && (this.getVal2().equals(p.getVal2())));
	 * }
	 */

	// for testing if a Pair matches a given Pair
	public boolean isEquivalent(Pair<String, String> p) {
		// System.out.println("is equivalent check: " + this.toString() + ", " +
		// p.toString());
		//System.out.println("Pair: " + p.toString());
		return ((this.getVal1().equals(p.getVal1()) && (this.getVal2().equals(p.getVal2())))
				|| ((this.getVal1().equals(p.getVal2()) && (this.getVal2().equals(p.getVal1())))));
	}
	
	//for testing if a pair contains a given String value
	public boolean contains(String s) {
		return ((this.getVal1().equals(s)) || (this.getVal2().equals(s)));
	}

	// for testing if a Pair matches a given edge
	public boolean matches(String s1, String s2) {
		return ((this.getVal1().equals(s1) && (this.getVal2().equals(s2)))
				|| ((this.getVal1().equals(s2) && (this.getVal2().equals(s1)))));
	}

	@Override
	public String toString() {
		return val1 + " " + val2;
	}

	public String toString1() {
		return val1.toString() + val2.toString();
	}

	public String getVal1() {
		return val1;
	}

	public String getVal2() {
		return val2;
	}

	public void setVal1(String s) {
		this.val1 = s;
	}

	public void setVal2(String s) {
		this.val2 = s;
	}

	public void sort() {
		if (this.getVal1().compareTo(this.getVal2()) > 0) {
			String temp = this.val1;
			this.setVal1(val2);
			this.setVal2(temp);
		}
	}

}
