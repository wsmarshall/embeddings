package codebase;

import java.util.ArrayList;
import java.util.Arrays;

public class Face {
	private ArrayList<Pair<String, String>> edges = new ArrayList<Pair<String, String>>();
	// for storing just the nodes involved in a face
	private ArrayList<String> nodes = new ArrayList<String>();
	//just the string nodes, no "+" markers
	private ArrayList<String> plainNodes = new ArrayList<String>();
	
	private String edgeString = "";

	private boolean isQuadrilateral = true;

	// default constructor
	public Face() {

	}

	// feed in a cycle
	public Face(ArrayList<Pair<String, String>> cycle) {
		for (int i = 0; i < cycle.size(); i++) {
			this.edges.add(cycle.get(i));
			this.nodes.add(cycle.get(i).getVal1());
			if(cycle.get(i).getVal1().charAt(0) == '+') {
				this.plainNodes.add(cycle.get(i).getVal1().substring(1));
			} else {
				this.plainNodes.add(cycle.get(i).getVal1());
			}
		}

		for (int i = 0; i < cycle.size(); i++) {
			if (cycle.get(i).getVal1().equals("0")) {
				edgeString += (cycle.get(i).getVal1() + cycle.get(i).getVal2());
				for (int j = 1; j < cycle.size() - 1; j++) {
					edgeString += cycle.get(j % (cycle.size())).getVal2();
				}
			}
			// no longer needed for orientable
			// since hashset "half-edge" tracking?
			char[] tempArray = edgeString.toCharArray();
			Arrays.sort(tempArray);
			edgeString = new String(tempArray);
		}
//		System.out.println("face 47 testing nodes list: " + this.nodes);
//		System.out.println("face 48 testing plainNodes list: " + this.plainNodes);
	}

	public ArrayList<Pair<String, String>> getEdges() {
		return this.edges;
	}
	
	//returns a node-only face-representing ArrayList
	public ArrayList<String> getNodes(){
		return this.nodes;
	}
	
	//returns a node-only face-representing ArrayList
	//with no '+' prepending
	public ArrayList<String> getPlainNodes(){
		return this.plainNodes;
	}

	public int size() {
		return this.edges.size();
	}

	public String getEdgeString() {
		return this.edgeString;
	}

	public boolean equals(Face f) {
		// if each edge of this face has an equivalent edge on other face
		// then the faces are equal
		int numEdges = this.getEdges().size();
		int numEdgesEqual = this.checkCommonEdges(f);
		if (numEdgesEqual == numEdges) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return "cycle: " + this.edges.toString(); // + " edge string id: " + this.edgeString;
	}

	public String toStringActual() {
		String f = "";
		for (int i = 0; i < edges.size(); i++) {
			f += edges.get(i).toString1() + ", ";
		}
		f += " STOP ";
		return f;
	}

	// if this face is bounded by exactly 4 edges
	public boolean isQuadrilateral() {
		if (this.edges.size() != 4) {
			this.isQuadrilateral = false;
		}

		return this.isQuadrilateral;
	}

	// tests this face to see if it includes a given edge
	public boolean contains(Pair<String, String> e) {
		for (int i = 0; i < this.edges.size(); i++) {
			if (this.edges.get(i).isEquivalent(e)) {
				return true;
			}
		}
		return false;
	}
	
	//tests this face to see if it includes a given node id'd by string input
	public boolean contains (String s) {
		for(int i = 0; i < this.nodes.size(); i++) {
//			System.out.println("face 116 node: " + this.nodes.get(i));

			if(this.nodes.get(i).equals(s)) {
//				System.out.println("face 119 s: " + s);
				return true;
			}
		}
		return false;
	}

	// counts up how many edges this face has in common with another given face
	public int checkCommonEdges(Face other) {
		int counter = 0;
		ArrayList<Pair<String, String>> thisEdges = this.getEdges();
		ArrayList<Pair<String, String>> otherEdges = other.getEdges();

		for (int i = 0; i < thisEdges.size(); i++) {
			for (int j = 0; j < otherEdges.size(); j++) {
				if (thisEdges.get(i).isEquivalent(otherEdges.get(j))) {
					// System.out.println("edge 1: " + thisEdges.get(i));
					// System.out.println("edge 2: " + otherEdges.get(j));
					counter++;
				}
			}
		}
		// System.out.println("counter: " + counter);
		return counter;
	}

	// check to see if a quadrilateral face has a central face with two designated
	// nodes on either side
	public boolean checkKittyCornerFace(String a, String left, String right) {
		// check if a is in between left and right
		if ((this.nodes.contains(a)) && this.nodes.contains(left) && this.nodes.contains(right)) {
//			System.out.println("face 156: " + this.nodes.toString());
			if ((this.nodes.indexOf(a) == (((((this.nodes.indexOf(right) - 1) % 4) + 4) % 4)))
					&& (this.nodes.indexOf(a) == ((this.nodes.indexOf(left) + 1) % 4))) {
//				System.out.println("face 159");
				return true;
			} // but maybe the order of left and right in the neighbor list goes the other way
			else if ((this.nodes.indexOf(a) == ((this.nodes.indexOf(right) + 1) % 4))
					&& (this.nodes.indexOf(a) == (((((this.nodes.indexOf(left) - 1) % 4) + 4) % 4)))) {
//				System.out.println("face 135");
				return true;
			}
		}
		return false;
	}

	private void copyFace(ArrayList<String> face, ArrayList<String> nodes) {
		for (int i = 0; i < nodes.size(); i++) {
			face.add(nodes.get(i));
		}
	}

	// for returning a deep copy of this face
	public ArrayList<String> getFaceCopy() {
		ArrayList<String> faceCopy = new ArrayList<String>();
		this.copyFace(faceCopy, this.nodes);
		return faceCopy;
	}

	// for returning the 4th node in a quad face
	public String oddNodeOut(String a, String b, String c) {
		for (int i = 0; i < this.plainNodes.size(); i++) {
			if (!(this.plainNodes.get(i).compareTo(a) ==0) && !(this.plainNodes.get(i).compareTo(b) == 0) && !(this.plainNodes.get(i).compareTo(c) ==0)) {
				return this.plainNodes.get(i);
			}
		}
		System.out.println("Face 175 ERROR ERROR ERROR fourth node not found");
		return "";
	}
}
