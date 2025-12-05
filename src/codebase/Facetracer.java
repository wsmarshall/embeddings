package codebase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.HashSet;

public class Facetracer {
	// 2 faces for inserting into
	static ArrayList<Pair<String, String>> face1 = null;

	static ArrayList<Pair<String, String>> face2 = null;

	// edges which must be included in the faces
	static Pair<String, String> e1 = new Pair<String, String>("", "");

	static Pair<String, String> e2 = new Pair<String, String>("", "");

	static Pair<String, String> e3 = new Pair<String, String>("", "");

	static Pair<String, String> e4 = new Pair<String, String>("", "");

	static ArrayList<Face> faces = new ArrayList<Face>();
	
	static boolean isQuad;
	static boolean isFaceSimple;

	// face tracing algorithm for a HashMap stored rotation system
	public static int getFaces(HashMap<String, ArrayList<String>> edgeNodeList) {
		int numFaces = 0;
		Pair<String, String> currEdge;

		boolean isQuad = true;// if the faces are quadrilateral

		// for holding the faces as they're traced

		ArrayList<Pair<String, String>> faceTracker = new ArrayList<Pair<String, String>>();

		ArrayList<Pair<String, String>> cycle = new ArrayList<Pair<String, String>>();
		// number of arrays in the rotation //System
		// Integer rotLen = rotation//SystemList.size();
		int faceLength = 0;

		// iterator for the HashMap
		Iterator<Entry<String, ArrayList<String>>> nodeIterator = edgeNodeList.entrySet().iterator();
		// loops through all nodes
		while (nodeIterator.hasNext()) {
			HashMap.Entry<String, ArrayList<String>> nextNode = (Map.Entry<String, ArrayList<String>>) nodeIterator
					.next();
			// System.out.println(nextNode.toString());
			// i is the size of the ArrayList of edges of the nextNode
			for (int i = 0; i < nextNode.getValue().size(); i++) {// loop through this node's outgoing edges
				Pair<String, String> tempEdge = new Pair<String, String>(nextNode.getKey(), nextNode.getValue().get(i));
				while (true) {// get the next face edge
					currEdge = new Pair<String, String>(tempEdge.getVal1(), tempEdge.getVal2());
					if (!includesEdge(cycle, currEdge)) {// this edge is not already part of a cycle
						cycle.add(currEdge);
						faceLength++;
						// System.out.println("edge added: " + currEdge.toString1());
						// System.out.println("facetracker contains check: " +
						// faceTracker.contains(currEdge));
						if (!(includesEdge(faceTracker, currEdge))) {
							// System.out.println("Edge not seen, ADDED: " + currEdge.toString1());
							faceTracker.add(currEdge);
						} else {// face tracker contains currEdge
							// KIM that for orientable, this means this is not a new face
							// since each "half-edge" occurs only once for faces on orientable surfaces
							cycle.clear();
							faceLength = 0;
							// System.out.println("EDGE SEEN ALREADY, BREAK OUT");
							break;
						}
					} else {// this edge is part of a current cycle already
						// in other words, this face has finished being traced
						// System.out.println("inside getFaces else");
						Face f = new Face(cycle);
//						System.out.println("Facetracer 79 cycle: " + cycle.toString());
						faces.add(f);
						// test this face for either of the two sets of edges it could contain for the
						// two
						// face in question for insertion (embedding method add2tok32n)
						if ((f.contains(e1) && f.contains(e2)) || f.contains(e3) && f.contains(e4)) {
							if (face1 != null) {// face1 is filled in, so fill in face2 now
								face2 = f.getEdges();
							} else {// face1 IS null, so fill it
								face1 = f.getEdges();
							}
						}
						numFaces++;
						if (faceLength != 4) {
							isQuad = false;
//							System.out.println("Facetracer 94, non quad face: " + f);
						}
						cycle.clear();
						faceLength = 0;// reset for next face tracing
						break; // breaks out of the while loop
					}

					String temp1 = currEdge.getVal2();
//					System.out.println("next 'source' node to parse: " + temp1);
					ArrayList<String> temp2 = edgeNodeList.get(temp1);
//					System.out.println("list parsing: " + temp2);
					String next = (temp2.get(((temp2.indexOf(currEdge.getVal1()) + 1) % (temp2.size()))));
					// System.out.println("next destination node to parse: " + next);
					// should be a key/value pair of node ID and arrayList of edge connected nodes
					tempEdge.setVal1(temp1);
					tempEdge.setVal2(next);
				}
			}
		}
		//System.out.println("Facetracer 112 number of faces: " + numFaces);
		// String f = "";
//		System.out.println("facetracer 115 faces: " + faces);
		if (isQuad) {
			setQuad(true);
			//System.out.println();
			System.out.println("IS QUADRILATERAL Facetracer 119");
			//System.out.println();
		} else {
			setQuad(false);
			System.out.println("NOT QUADRILATERAL EMBEDDING Facetracer 123");
		}
		//System.out.println("facetracer 124/125 facesimple check");
		//isFaceSimple();
		System.out.println();
		return numFaces;
	}

	private static boolean includesEdge(ArrayList<Pair<String, String>> list, Pair<String, String> edge) {
		boolean contains = false;

		for (int i = 0; i < list.size(); i++) {
			if (edge.getVal1().equals(list.get(i).getVal1())) {
				// System.out.println("Inside includesEdge first If");
				if (edge.getVal2().equals(list.get(i).getVal2())) {
					// System.out.println("Inside includesEdge second If");
					return true;
				}
			}
		}

		return contains;
	}

	private static boolean includesFace(ArrayList<Face> faces, Face f) {
		boolean contains = false;

		String id = f.getEdgeString();
		if (id.isEmpty()) {
			return true;
		}

		System.out.println("id: " + id);
		for (int i = 0; i < faces.size(); i++) {
			System.out.println(id + " vs. " + faces.get(i).getEdgeString());
			if (id.equals(faces.get(i).getEdgeString())) {
				return true;
			}
		}

		return contains;
	}

	// goes through the traced faces to check that each face shares at most 1 edge
	// with any other given face
	// must be called after getFaces is called such that faces is not empty
	public static boolean isFaceSimple() {
		// copy over 'stripped' (no '+') faces to faceList
		ArrayList<Face> faceList = new ArrayList<Face>();
		ArrayList<Pair<String, String>> edges;
		ArrayList<Pair<String, String>> strippedEdges = new ArrayList<Pair<String, String>>();
		for (int i = 0; i < faces.size(); i++) {
			edges = faces.get(i).getEdges();
			//System.out.println("edges: " + edges);
			for (int j = 0; j < edges.size(); j++) {
				boolean replaced = false;
				String node1 = "";
				String node2 = "";
				String temp = edges.get(j).getVal1();
				String temp2 = edges.get(j).getVal2();
				if (temp.contains("+")) {
					//System.out.println("temp: " + temp);
					node1 = temp.replace("+", "");
					replaced = true;
					//System.out.println("node1: " + node1);
				} else {
					node1 = temp;
				}
				if (temp2.contains("+")) {
					//System.out.println("temp2: " + temp2);
					node2 = temp2.replace("+", "");
					replaced = true;
					//System.out.println("node2: " + node2);
				} else {
					node2 = temp2;
				}
				// new nodes for the edges are created
				if (replaced) {
					Pair<String, String> tempEdge = new Pair<String, String>(node1, node2);
					strippedEdges.add(tempEdge);
				} else {
					strippedEdges.add(edges.get(j));
				}
			}
			// strippedEdges is now all a face stripped of the "+" character
			Face f = new Face(strippedEdges);
			//System.out.println("the new face: " + f);
			faceList.add(f);
			strippedEdges.clear();
		}
		// faceList is now a collection of faces stripped of the "+" character
		// in all of the edges
		
		//System.out.println("testing faceList: " + faceList);
		
		// now need to remove all equivalent faces
		for (int i = 0; i < faceList.size(); i++) {
			Face f1 = faceList.get(i);
			for (int j = 0; j < faceList.size(); j++) {
				if (i != j) {
					Face f2 = faceList.get(j);
					if (f1.equals(f2)) {
						faceList.remove(j);
					}
				}
			}

		}
		// faceList should now have no equivalent faces

		//System.out.println("faces: " + faceList);
		for (int i = 0; i < faceList.size() - 1; i++) {
			for (int j = 1; j < faceList.size(); j++) {
				if (i == j) {
					continue;
				}

				int tempCounter = faceList.get(i).checkCommonEdges(faceList.get(j));
				if (tempCounter > 1) {
					isFaceSimple = false;
					System.out.println("IS NOT FACE SIMPLE facetracer 236");
					faces.clear();//for repeated access when automated testing
					return false;
				}
			}
		}
		isFaceSimple = true;
		System.out.println("IS FACE SIMPLE facetracer 242");
		faces.clear();//for repeated access when automated testing
		return true;
	}

	public static boolean faceContains(Face f, Pair<String, String> e) {
		if (f.contains(e)) {
			return true;
		} else {
			return false;
		}
	}

	public static ArrayList<Pair<String, String>> getFace1() {
		return face1;
	}

	public static ArrayList<Pair<String, String>> getFace2() {
		return face2;
	}

	public static void setE1(Pair<String, String> e) {
		e1 = e;
	}

	public static Pair<String, String> getE1() {
		return e1;
	}

	public static void setE2(Pair<String, String> e) {
		e2 = e;
	}

	public static Pair<String, String> getE2() {
		return e2;
	}

	public static void setE3(Pair<String, String> e) {
		e3 = e;
	}

	public static Pair<String, String> getE3() {
		return e3;
	}

	public static void setE4(Pair<String, String> e) {
		e4 = e;
	}

	public static Pair<String, String> getE4() {
		return e4;
	}
	
	//for setting this object's isQuad flag variable
	private static void setQuad(boolean b) {
		isQuad = b;
	}
	
	//returns isQuad /\ isFaceSimple
	public boolean getMinQuadStats() {
		return isQuad && isFaceSimple;
	}
	
	public ArrayList<Face> getFaces(){
		return this.faces;
	}

}
