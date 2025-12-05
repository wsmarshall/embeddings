package codebase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class Constructors {

	// intakes a "vanilla" k3,2n
	// adds two nodes to the right side to get a k3,(2n+2) - 2k2
	// distance for our purposes is either 1 or 3 ONLY
	static Embedding add2tok32n(Embedding e, int distance) {
		HashMap<String, ArrayList<String>> embedding = e.getEmbedding();
		int size = embedding.size();
		Facetracer ft = new Facetracer();

		Iterator<Entry<String, ArrayList<String>>> eIterator = e.getEmbedding().entrySet().iterator();

		String central = "", nextHub = "", lastHub = "";// the central, "saturated" node and the two which end up
														// with edges "missing"
		String firstInsert = "x"; // one of the two inserted nodes
		String secondInsert = "y"; // the other of the two inserted nodes

		// these are the things we are inserting between

		// find the three nodes on the "left" with degree 2n
		int counter = 0;
		while (counter < 1) {
			HashMap.Entry<String, ArrayList<String>> currentNode = (Map.Entry<String, ArrayList<String>>) eIterator
					.next();
			if (currentNode.getValue().size() == 3) {// one of the "right" nodes
				continue;
			} else {// one of the "left" nodes we want to assign
				if (central.isEmpty()) {
					central = currentNode.getKey();
					counter++;
				}
			} // string keys is now "loaded in" to the 'central hub'
		}
		// for calling the actual insertion method (2 calls)
		// lefts and rights are to the left or right of the central node in the face
		// being
		// inserted into
		String left1, right1;
		String left2, right2;

		// find the insertion points
		ArrayList<String> centralNeighbors = e.getEmbedding().get(central);
		System.out.println("constructors 53 central: " + central);
//		System.out.println("constructors 54 centralNeighbors: " + centralNeighbors.toString());
		left1 = centralNeighbors.get(0);
		right1 = centralNeighbors.get(1);
		left2 = centralNeighbors.get(distance);
		right2 = centralNeighbors.get(distance + 1);
//		System.out.println("constructors 59 left1, right1: " + left1 + ", " + right1);
//		System.out.println("constructors 60 left2, right2: " + left2 + ", " + right2);

//		//strip out all "+" characters in the faces
//		//put into the 'current' list
//		for(int i = 0; i < faces.size(); i++) {
//			Face current = faces.get(i);
//			System.out.println("cons 65 current: " + current.getNodes());
//			for(int j = 0; j < current.getNodes().size(); j++) {
//				if(current.getNodes().get(j).charAt(0) == '+') {
//					ArrayList<String> nodes = current.getNodes();
//					String copyReplace = nodes.get(j).substring(1);
//					nodes.remove(j);
//					current.getNodes().add(j, copyReplace);
//				}
//			}
//		}

		// a list of faces which must involve central, and NOT +central
//		ArrayList<Face> centralOnly = new ArrayList<Face>();
//		for (int i = 0; i < faces.size(); i++) {
////			System.out.println("constructors 79 faces: " + faces.get(i).getNodes());
//			if (faces.get(i).contains(central) || faces.get(i).contains("+" + central)) {
//				centralOnly.add(faces.get(i));
//				System.out.println("constructors 82 central involving face: " + faces.get(i).getNodes());
//			}
//		}
//		System.out.println("central only: " + centralOnly);

		// cleans the central node
		e.cleanNode(central, e);
		
		System.out.println("constructors 89 facetracing:");
		ft.getFaces(e.doubleCover());
		ArrayList<Face> faces = ft.getFaces();

		// find the two faces
		ArrayList<String> face1 = new ArrayList<String>();
		ArrayList<String> face2 = new ArrayList<String>();

		for (int i = 0; i < faces.size(); i++) {
//			System.out.println("constructors 92: " + faces.get(i).getNodes().toString());
			if (face1.isEmpty()) {
//				System.out.println("here");
				if (faces.get(i).checkKittyCornerFace(central, left1, right1)) {
//					System.out.println("here1");
					face1 = faces.get(i).getFaceCopy();
//					System.out.println("left1: " + left1);
//					System.out.println("right1: " + right1);
					// need to load nextHub
					nextHub = faces.get(i).oddNodeOut(central, left1, right1);
				}
			}
			if (face2.isEmpty()) {
//				System.out.println("there");
				if (faces.get(i).checkKittyCornerFace(central, left2, right2)) {
//					System.out.println("THERE!");
					face2 = faces.get(i).getFaceCopy();
					// need to load lastHub
//					System.out.println("left2, right2: " + left2 + ", " + right2);
					lastHub = faces.get(i).oddNodeOut(central, left2, right2);
				}
			}
			if (!face1.isEmpty() && !face2.isEmpty()) {// both faces found already
				System.out.println("Constructors 76 BOTH FACES SUCCESSFULLY FOUND");
				System.out.println("Constructors 77 FACE1: " + face1.toString());
				System.out.println("Constructors 78 FACE2: " + face2.toString());
				System.out.println();
				break;
			}

		}

		// both faces now are loaded properly
		// central, nextHub, and lastHub all loaded in
		// insertion points are now loaded into left1, ..., right2

		// TODO replace this bit with the neighbor list check
		// check left1's neighbors
//		System.out.println("cons 133 left1's face's opposite node: " + nextHub);
//		System.out.println("cons 134 left1's neighbors: " + e.getEmbedding().get(left1));

		// check left2's neighbors
//		System.out.println("cons 137 left2's face's opposite node: " + lastHub);
//		System.out.println("cons 138 left2's neighbors: " + e.getEmbedding().get(left2));

		// if necessary, flip either nextHub or lastHub if they have t1 edges
		if (e.type1EdgesSetContains(new Pair<String, String>(nextHub, left1))
				|| e.type1EdgesSetContains(new Pair<String, String>(nextHub, right1))) {
//			System.out.println("constructors 134 flipping occurring");
			e.flipNode(nextHub, e);// recall a quadFace can only have 0, 2, or 4 t1 edges
			// thus flipping central, then the other is sufficient to eliminate all t1 edges

//			nextFlipped = true;
		}

		if (e.type1EdgesSetContains(new Pair<String, String>(lastHub, left1))
				|| e.type1EdgesSetContains(new Pair<String, String>(lastHub, right2))) {
//			System.out.println("constructors 139 flipping occurring");
			e.flipNode(lastHub, e);// recall a quadFace can only have 0, 2, or 4 t1 edges
			// thus flipping central, then the other is sufficient to eliminate all t1 edges

		}

//		System.out.println("nextHub: " + nextHub);
//		System.out.println("lastHub: " + lastHub);

		e.insertDegree2Node(firstInsert, central, nextHub, left1, right1);
		e.insertDegree2Node(secondInsert, central, lastHub, left2, right2);

		// nb this is the original input embedding, but we've (ideally) inserted into it
		// by now
		return e;
	}

	// test method for making an arbitrary k3,2n NONORIENTABLE graph
	static Embedding NOconstructk32n(int n) {
		// pro forma initialization
		Embedding result = new Embedding();

		// used as the "accumulating mass" that will become result
		Embedding left = new Embedding(BaseCases.NObasek43Array, BaseCases.NOk43t1edges);
		// System.out.println("Constructor 283 left: " + left);
		Embedding right = new Embedding(BaseCases.NObasek43Array, BaseCases.NOk43t1edges);
		// System.out.println("Constructor 285 right: " + right);
		// first left and right gives k36 nonorientable
		// then succesive result + NOk43 gives 3,8; 3,10; 3,12; etc. (NO)
		result = left.dsum(right, 3);
		// System.out.println("Constructor result 289: " + result);
		int count = 6;
		while (count < n) {
			right = new Embedding(BaseCases.NObasek43Array, BaseCases.NOk43t1edges);
			result = result.dsum(right, 3);
			count += 2;
		}

		return result;
	}

	public static Embedding Ringel(int n) {
		// initialization to avoid error
		Embedding result = new Embedding();
		HashMap<String, ArrayList<String>> interResult = new HashMap<String, ArrayList<String>>();
		ArrayList<Pair<String, String>> t1edges = new ArrayList<Pair<String, String>>();

		// result is a K(n+1, 2n) embedding
		if (n % 2 == 1) {// if n is odd
			result = oddRingel(n, interResult, t1edges);
		}
		// result is a K(n+1, 2n) embedding
		else if (n % 2 == 0) {// if n is even
			result = evenRingel(n, interResult, t1edges);
		}

		return result;
	}

	// a generator for certain kinds of Current graphs
	public static Embedding currentGenerator(int n) {
		// initialization to avoid error
		Embedding result = new Embedding();
		HashMap<String, ArrayList<String>> interResult = new HashMap<String, ArrayList<String>>();
		ArrayList<Pair<String, String>> t1edges = new ArrayList<Pair<String, String>>();

		// Kn,n minus a perfect matching, n congruent to 1 mod 4 && n >= 9
		if (((n % 4) == 1) && (n >= 9)) {
			result = oneModFourCurrent(n, interResult, t1edges);
		} else if (((n % 4) == 3) && (n >= 7)) {// Kn,n minus a perfect matching, n congruent to 3 mod 4 && n >= 7
			result = threeModFourCurrent(n, interResult, t1edges);
		}

		return result;
	}

	// for generating a Ringel Embedding from an even n value input
	// gives a K(n+1, 2n) embedding
	private static Embedding evenRingel(int n, HashMap<String, ArrayList<String>> interResult,
			ArrayList<Pair<String, String>> t1edges) {
		int maxNode = 2 * n;// for the 2n right vertices
		int smallerRows = n + 1;// for the n+1 left vertices
		char start = 'a';// left nodes correspond to letters
		ArrayList<String> nodeLetterLabels = new ArrayList<String>();// for keeping track of the node labels which are
		// letters
		char end = (char) (start + smallerRows);// how many alphabet labels we need
		// for holding the forward and reversed letter label
		ArrayList<String> letterRow = new ArrayList<String>();
		for (char current = start; current < end; current++) {
			letterRow.add(String.valueOf(current));
		}

		// for the letter neighbors
		boolean ascending = true;
		ArrayList<String> neighbors = new ArrayList<String>();// for the neighbors of

		// fill and put into interResult the number nodes with letter neighbors
		for (int i = 0; i < maxNode; i++) {
			if (ascending) {
				for (int j = 0; j < letterRow.size(); j++) {
					neighbors.add(letterRow.get(j));
				}
				ascending = false;
			} else {
				for (int j = letterRow.size() - 1; j >= 0; j--) {
					neighbors.add(letterRow.get(j));
				}
				ascending = true;
			}
			interResult.put(Integer.toString(i), neighbors);
			neighbors = new ArrayList<String>();
		}

		// TODO encode row 'a' and 'b'

		// encode row 'a'
		neighbors.add(Integer.toString(n));
		neighbors.add(Integer.toString(n + 1));
		neighbors.add(Integer.toString(n - 1));
		neighbors.add(Integer.toString(n - 2));
		boolean countingUp = true;
		for (int i = 4; i < maxNode; i += 2) {
			if (countingUp) {// countingUp is true
				neighbors.add(Integer.toString(Integer.parseInt(neighbors.get(i - 4)) + 2));
				neighbors.add(Integer.toString(Integer.parseInt(neighbors.get(i - 3)) + 2));
				countingUp = false;
			} else {// countingUp is false
				neighbors.add(Integer.toString(Integer.parseInt(neighbors.get(i - 4)) - 2));
				neighbors.add(Integer.toString(Integer.parseInt(neighbors.get(i - 3)) - 2));
				countingUp = true;
			}
		}
		interResult.put("a", neighbors);
		neighbors = new ArrayList<String>();

		// encode row 'b'
		neighbors.add(Integer.toString(0));
		neighbors.add(Integer.toString(maxNode - 1));
		neighbors.add(Integer.toString(1));
		neighbors.add(Integer.toString(2));
		neighbors.add(Integer.toString(maxNode - 2));
		neighbors.add(Integer.toString(maxNode - 3));
		countingUp = true;
		for (int i = 6; i < maxNode; i += 2) {
			if (countingUp) {// countingUp is true
				neighbors.add(Integer.toString(Integer.parseInt(neighbors.get(i - 4)) + 2));
				neighbors.add(Integer.toString(Integer.parseInt(neighbors.get(i - 3)) + 2));
				countingUp = false;
			} else {// countingUp is false
				neighbors.add(Integer.toString(Integer.parseInt(neighbors.get(i - 4)) - 2));
				neighbors.add(Integer.toString(Integer.parseInt(neighbors.get(i - 3)) - 2));
				countingUp = true;
			}
		}
		interResult.put("b", neighbors);
		neighbors = new ArrayList<String>();

		// encode rows 'c'...
		ascending = false;
		for (char current = 'c'; current < end; current++) {
			if (ascending) {
				for (int i = 0; i < maxNode; i++) {
					neighbors.add(Integer.toString(i));
				}
				ascending = false;
			} else {
				for (int i = maxNode - 1; i >= 0; i--) {
					neighbors.add(Integer.toString(i));
				}
				ascending = true;
			}
			interResult.put(String.valueOf(current), neighbors);
			neighbors = new ArrayList<String>();
		}

		// encode 'a' type 1 edges
		for (int i = 0; i < n; i++) {
			t1edges.add(new Pair<String, String>("a", Integer.toString(i)));
		}

		t1edges.add(new Pair<String, String>("b", "0"));

		// encode 'b' type 1 edges
		for (int i = (n + 1); i < maxNode; i++) {
			t1edges.add(new Pair<String, String>("b", Integer.toString(i)));
		}

		Embedding result = new Embedding(interResult, t1edges);
		return result;
	}

	// for generating a Ringel embedding from an odd integer input n
	// gives a k(n+1, 2n) embedding
	private static Embedding oddRingel(int n, HashMap<String, ArrayList<String>> interResult,
			ArrayList<Pair<String, String>> t1edges) {
		int maxRows = 2 * n;
		int smallerRows = n + 1;
		char start = 'a';// want some nodes to be letters, others to be natural numbers
		char second = 'b';// a and b need to have 2,3 transposed in their neighbor node lists
		ArrayList<String> nodeLetterLabels = new ArrayList<String>();// for keeping track of the node labels which are
																		// letters
		char end = (char) (start + smallerRows);// how many alphabet labels we need

		ArrayList<String> neighbors = new ArrayList<String>();

		// fill the 'a' node row with neighbors, making sure to transpose 2 and 3 in the
		// descending sequence
		for (int i = maxRows - 1; i >= 0; i--) {
			if (i == 1) {
				neighbors.add("2");
			} else if (i == 2) {
				neighbors.add("1");
			} else {
				neighbors.add(Integer.toString(i));
			}
		}
		// put into the embedding hashMap
		interResult.put(String.valueOf(start), neighbors);
		neighbors = new ArrayList<String>();
		nodeLetterLabels.add(String.valueOf(start));

		// fill the 'b' node row with neighbors, transposing 2 and 3 in the ascending
		// sequence
		for (int i = 0; i < maxRows; i++) {
			if (i == 1) {
				neighbors.add("2");
			} else if (i == 2) {
				neighbors.add("1");
			} else {
				neighbors.add(Integer.toString(i));
			}
		}
		// put into the embedding hashMap
		interResult.put(String.valueOf(second), neighbors);
		neighbors = new ArrayList<String>();
		nodeLetterLabels.add(String.valueOf(second));

		// fill the rest of the 'max length' rows
		boolean descending = true;// since 'c' is a descending row
		for (char current = 'c'; current < end; current++) {
			nodeLetterLabels.add(String.valueOf(current));

			if (descending) {
				for (int i = maxRows - 1; i >= 0; i--) {
					neighbors.add(Integer.toString(i));
				}
				interResult.put(String.valueOf(current), neighbors);
				neighbors = new ArrayList<String>();
			} else {// the row is ascending instead
				for (int i = 0; i < maxRows; i++) {
					neighbors.add(Integer.toString(i));
				}
				interResult.put(String.valueOf(current), neighbors);
				neighbors = new ArrayList<String>();
			}
			if (descending) {// flip flag
				descending = false;
			} else {
				descending = true;
			}
		}
		// fill the 'number labelled' node rows
		// with the 'letter' nodes as neighbors
		descending = false;
		ArrayList<String> numberLabelNeighbors = new ArrayList<String>();

		for (int i = 0; i < maxRows; i++) {
			if (descending) {// from last to first letter node label
				for (int j = nodeLetterLabels.size() - 1; j >= 0; j--) {
					numberLabelNeighbors.add(String.valueOf(nodeLetterLabels.get(j)));
				}
				interResult.put(Integer.toString(i), numberLabelNeighbors);
				numberLabelNeighbors = new ArrayList<String>();
				// flip the flag for the next row
				descending = false;
			} else {// from first to last letter node label
				for (int j = 0; j < nodeLetterLabels.size(); j++) {
					numberLabelNeighbors.add(String.valueOf(nodeLetterLabels.get(j)));
				}
				interResult.put(Integer.toString(i), numberLabelNeighbors);
				numberLabelNeighbors = new ArrayList<String>();
				// flip the flag for the next row
				descending = true;
			}
		}

		// add in the 4 necessary t1 edges
		t1edges.add(new Pair<String, String>("a", "1"));
		t1edges.add(new Pair<String, String>("a", "2"));
		t1edges.add(new Pair<String, String>("b", "1"));
		t1edges.add(new Pair<String, String>("b", "2"));

		Embedding result = new Embedding(interResult, t1edges);

		return result;
	}

	// for generating a Current graph from n congruent to 3 (mod 4) && n >= 7
	// generates a Kn,n minus a perfect matching
	private static Embedding threeModFourCurrent(int n, HashMap<String, ArrayList<String>> interResult,
			ArrayList<Pair<String, String>> t1edges) {

		int maxNode = 2 * n;
		int endPoint = (n - 2);// the 'endpoint' of the various series being used

		// initialize the two rows of integers
		ArrayList<Integer> row0 = new ArrayList<Integer>();
		ArrayList<Integer> row1 = new ArrayList<Integer>();

		// initialize the first two elements of row0
		row0.add(-5);
		row0.add(1);

		// flag for 'flipping' the negative sign
		boolean negative = false;
		for (int i = 7; i <= endPoint; i += 2) {// fill the first section of row0
			if (negative) {
				row0.add(i * (-1));
				negative = false;
			} else {
				row0.add(i);
				negative = true;
			}
		} // first part of row0 now initialized

		// add next 'hardcoded' bits
		row0.add(5);
		row0.add(-1);
		row0.add(3);
		row0.add(-3);
		// reset negative flag
		negative = true;
		for (int i = 7; i <= endPoint; i += 2) {// fill the second section of row0
			if (negative) {
				row0.add(i * (-1));
				negative = false;
			} else {
				row0.add(i);
				negative = true;
			}
		} // second part of row0 now set, all of it should be in place

		// add first two elements of row1
		row1.add(-1);
		row1.add(5);

		for (int i = endPoint; i > 5; i -= 2) {// fill in first part of row1
			row1.add(i);
		}

		// add next necessary elements of row1
		row1.add(1);
		row1.add(3);
		row1.add(-3);
		row1.add(-5);

		for (int i = endPoint; i > 5; i -= 2) {// fill in second part of row1
			row1.add(i * (-1));
		}

		// row0, row1 now filled with necessary values
//		System.out.println("Constructors 328 test: " + row0);
//		System.out.println("Constructors 329 test: " + row1);

		ArrayList<String> row0Actual = new ArrayList<String>();
		ArrayList<String> row1Actual = new ArrayList<String>();

		// fill actual lists for adding to the HashMap
		for (int i = 0; i < row0.size(); i++) {// row0, row1 should be of same size/length
			row0Actual.add(Integer.toString(myMod(0 + row0.get(i), maxNode)));
			row1Actual.add(Integer.toString(myMod(1 + row1.get(i), maxNode)));
		}
		// put the rows into an array
		interResult.put("0", row0Actual);
		interResult.put("1", row1Actual);

		boolean even;
		for (int i = 2; i < maxNode; i++) {
			ArrayList<String> neighbors = new ArrayList<String>();
			if (i % 2 == 0) {
				even = true;
			} else {
				even = false;
			}

			for (int j = 0; j < row0.size(); j++) {
				if (even) {
					neighbors.add(Integer.toString(myMod(i + row0.get(j), maxNode)));
				} else {
					neighbors.add(Integer.toString(myMod(i + row1.get(j), maxNode)));
				}
			}

			interResult.put(Integer.toString(i), neighbors);

		}
		// type 1 edges for even nodes are of form (i, i+1) for all even i in Z_2n
		for (int i = 0; i < maxNode; i += 2) {
			Pair<String, String> t1edge = new Pair<String, String>(Integer.toString(i),
					Integer.toString(myMod((i + 1), maxNode)));
			t1edges.add(t1edge);
		}
		// type 1 edges for all nodes are (j, j+3) for all j
		for (int j = 0; j < maxNode; j++) {
			Pair<String, String> t1edge = new Pair<String, String>(Integer.toString(j),
					Integer.toString(myMod(j + 3, maxNode)));
			t1edges.add(t1edge);
		}
//		System.out.println("constructors 375 t1 edges: " + t1edges);

		Embedding result = new Embedding(interResult, t1edges);
		return result;
	}

	// for generating a Current graph from n congruent to 1 (mod 4) && n >= 9
	// generates a Kn,n minus a perfect matching
	private static Embedding oneModFourCurrent(int n, HashMap<String, ArrayList<String>> interResult,
			ArrayList<Pair<String, String>> t1edges) {
		int maxNode = 2 * n;
		int endPoint = (n - 2);// the 'endpoint' of the various series being used

		// initialize the two rows of integers
		ArrayList<Integer> row0 = new ArrayList<Integer>();
		ArrayList<Integer> row1 = new ArrayList<Integer>();

		// initialize the first two elements of row0
		row0.add(-3);
		row0.add(1);

		// for 'flipping' the negative sign
		boolean negative = false;
		for (int i = 5; i <= endPoint; i += 2) {// fill the first section of row0
			if (negative) {
				row0.add(i * (-1));
				negative = false;
			} else {
				row0.add(i);
				negative = true;
			}
		} // first part of row0 now initialized
		row0.add(3);
		row0.add(-1);
		negative = true;
		for (int i = 5; i <= endPoint; i += 2) {// fill the second section of row0
			if (negative) {
				row0.add(i * (-1));
				negative = false;
			} else {
				row0.add(i);
				negative = true;
			}
		} // second part of row0 now set, all of it should be in place

		// add first two elements of row1
		row1.add(-1);
		row1.add(3);

		for (int i = endPoint; i > 3; i -= 2) {// fill in first part of row1
			row1.add(i);
		}

		// add next necessary elements of row1
		row1.add(1);
		row1.add(-3);

		for (int i = endPoint; i > 3; i -= 2) {// fill in second part of row1
			row1.add(i * (-1));
		}

		// row0, row1 now filled with necessary values
//		System.out.println("Constructors 437 test: " + row0);
//		System.out.println("Constructors 438 test: " + row1);

		ArrayList<String> row0Actual = new ArrayList<String>();
		ArrayList<String> row1Actual = new ArrayList<String>();

		// fill actual lists for adding to the HashMap
		for (int i = 0; i < row0.size(); i++) {// row0, row1 should be of same size/length
			row0Actual.add(Integer.toString(myMod(0 + row0.get(i), maxNode)));
			row1Actual.add(Integer.toString(myMod(1 + row1.get(i), maxNode)));
		}
		// put the rows into an array
		interResult.put("0", row0Actual);
		interResult.put("1", row1Actual);

		boolean even;
		for (int i = 2; i < maxNode; i++) {
			ArrayList<String> neighbors = new ArrayList<String>();
			if (i % 2 == 0) {
				even = true;
			} else {
				even = false;
			}

			for (int j = 0; j < row0.size(); j++) {
				if (even) {
					neighbors.add(Integer.toString(myMod(i + row0.get(j), maxNode)));
				} else {
					neighbors.add(Integer.toString(myMod(i + row1.get(j), maxNode)));
				}
			}

			interResult.put(Integer.toString(i), neighbors);

		}
		// type 1 edges
		for (int i = 0; i < maxNode; i++) {
			Pair<String, String> t1edge = new Pair<String, String>(Integer.toString(i),
					Integer.toString(myMod((i + 1), maxNode)));
			t1edges.add(t1edge);
		}

		Embedding result = new Embedding(interResult, t1edges);
		return result;
	}

	// general constructor method for any complete (ORIENTABLE) bipartite graph
	// minus
	// a matching
	// as of 23/02/06 start implementing arbitrary m, k ignored (i.e. construct any
	// given K6,n graph)
	// NB m is assumed to be greater than or equal to n
	public static HashMap<String, ArrayList<String>> constructO(int m, int n, int k, boolean b) {
		if (!b) { // non orientable, since false is fed in
			HashMap<String, ArrayList<String>> result = new HashMap<String, ArrayList<String>>();

			return result;

		} else {// orientable, since b is true
			// to build up the 'm' bit, we use the fact that
			// km,n = k(m-4),n dsum k6,n
			// so now need k(m-4),n

			// instead use the for m,n modulo 4 equivalence classes
			// for the 4 base cases 4, 5, 6, 3
			int modM = m % 4;
			int modN = n % 4;
			// default initialization for m = 0 mod 4

			Graph baseM = new Graph(BaseCases.basek64array, 24);
			Graph intermediateRight = new Graph(BaseCases.basek63array, 18);

			// covering all base kcases for base Kx,y
			// for x,y in mod 4 equivalent classes
			if (modM == 0) {// base m is 4
				modM = 4;
				intermediateRight = new Graph(BaseCases.basek64array, 24);
				if (modN == 0) {// base n is 4
					modN = 4;
					baseM = new Graph(BaseCases.basek44array, 16);
				} else if (modN == 1) {// base n is 5
					modN = 5;
					baseM = new Graph(BaseCases.basek45array, 20);
				} else if (modN == 2) {// base n is 6
					modN = 6;
					// base initialization already done
				} else if (modN == 3) {// base n is 3
					baseM = new Graph(BaseCases.basek34array, 12);
				}
			} else if (modM == 1) {// m is 5
				modM = 5;
				intermediateRight = new Graph(BaseCases.basek65array, 30);
				if (modN == 0) {// base n is 4
					modN = 4;
					baseM = new Graph(BaseCases.basek45array, 20);
				} else if (modN == 1) {// base n is 5
					modN = 5;
					baseM = new Graph(BaseCases.basek55array, 25);
				} else if (modN == 2) {// base n is 6
					modN = 6;
					baseM = new Graph(BaseCases.basek65array, 30);
				} else if (modN == 3) {// base n is 3
					// System.out.println("test flag");
					baseM = new Graph(BaseCases.basek35array, 15);
					// intermediateRight = new Graph(basek63array, 18);
				}
			} else if (modM == 2) {// m is 6
				modM = 6;
				intermediateRight = new Graph(BaseCases.basek66array, 36);
				if (modN == 0) {// base n is 4
					modN = 4;
					// base initialization already done
				} else if (modN == 1) {// base n is 5
					modN = 5;
					baseM = new Graph(BaseCases.basek65array, 30);
				} else if (modN == 2) {// base n is 6
					modN = 6;
					baseM = new Graph(BaseCases.basek66array, 36);
				} else if (modN == 3) {// base n is 3
					baseM = new Graph(BaseCases.basek63array, 18);
				}
			} else if (modM == 3) {// m is 3
				// intermediateRight already initialized to k63
				if (modN == 0) {// base n is 4
					modN = 4;
					baseM = new Graph(BaseCases.basek34array, 12);
				} else if (modN == 1) {// base n is 5
					modN = 5;
					baseM = new Graph(BaseCases.basek35array, 15);
				} else if (modN == 2) {// base n is 6
					modN = 6;
					baseM = new Graph(BaseCases.basek63array, 18);
				} else if (modN == 3) {// base n is 3
					baseM = new Graph(BaseCases.basek33array, 9);
				}
			}

			Graph interResult = new Graph(baseM.getEdgeNodeList());
			// System.out.println("left side of m building: ");
			// printEmbedding(baseM.getEdgeNodeList());
			Graph interRight = new Graph(intermediateRight.getEdgeNodeList());
			// System.out.println("right side of m building: ");
			// printEmbedding(interRight.getEdgeNodeList());

			// build up m first
			int mCounter = modN;
			while (mCounter < n) {
				// System.out.println("modM: " + modM);
				// System.out.println("modN: " + modN);
				// System.out.println("mCounter, N: " + mCounter + ", " + n);
				interResult = new Graph(Oglue(interResult.getEdgeNodeList(), interRight.getEdgeNodeList(), modM));
				interRight = new Graph(intermediateRight.getEdgeNodeList());
				mCounter += 4;
			}

			// now interResult is a km, modN graph
			// System.out.println("Should now be: ");
			// printEmbedding(interResult.getEdgeNodeList());

			// FOR BUILDING UP THE K6,N
			HashMap<String, ArrayList<String>> result = buildk6n(n).getEmbedding();

			// "result" is now a k6m embedding
			// recall "interResult" is a km, modN graph
			// System.out.println("interResult TEST: ");
			// printEmbedding(interResult.getEdgeNodeList());
			// Tester.check(interResult.getEdgeNodeList());
			// System.out.println("result TEST: ");
			// printEmbedding(result);
			// Tester.check(result);

			HashMap<String, ArrayList<String>> basekmn = interResult.getEdgeNodeList();
			Graph rightGraph = new Graph(result);
			Graph leftGraph = new Graph(basekmn);

			// "blank" initialization
			HashMap<String, ArrayList<String>> finalResult = new HashMap<String, ArrayList<String>>();

			System.out.println("modM, modN, m, n: " + modM + ", " + modN + ", " + m + ", " + n);
			// then build up the kmn by gluing the kxn with k6n from first bit
			while (modM < m) {
				// System.out.println("Result should be k610: ");
				// printEmbedding(result);
				finalResult = Oglue(leftGraph.getEdgeNodeList(), rightGraph.getEdgeNodeList(), n);
				modM += 4;
				rightGraph = new Graph(result);
				leftGraph = new Graph(finalResult);
				// System.out.println("-----------------------------------------------\nBEGIN
				// CHECK RESULT");
				// Tester.check(finalResult);
				// System.out.println("END CHECK
				// RESULT\n-------------------------------------------------");
			}
			// System.out.println("RESULT: ");
			// printEmbedding(result);
			return finalResult;
		}
	}

	// private helper method for the construct function
	// used in the process of building an arbitrary kmn graph
	// glues ORIENTABLE embeddings together (no t1 edge lists)
	private static HashMap<String, ArrayList<String>> Oglue(HashMap<String, ArrayList<String>> left,
			HashMap<String, ArrayList<String>> right, int degree) {
		HashMap<String, ArrayList<String>> result;

		// graph initializations from the fed in embeddings
		// since dsum is only defined on graph objects
		Graph leftG = new Graph(new Embedding(left));
		Graph rightG = new Graph(new Embedding(right));

		// for finding nodes of appropriate degree to excise and glue together
		Iterator<Entry<String, ArrayList<String>>> leftIterator = left.entrySet().iterator();
		Iterator<Entry<String, ArrayList<String>>> rightIterator = right.entrySet().iterator();

		// for the dsum operation, recall s,t excised nodes from left, right embeddings
		// merging/gluing nodes starts with neighbors u,v from s,t respectively
		String s = "";
		String t = "";
		String u = "";
		String v = "";

		// find node of appropriate degree from left embedding
		boolean findLPoint = false;
		while (!findLPoint) {
			HashMap.Entry<String, ArrayList<String>> baseLNode = (Map.Entry<String, ArrayList<String>>) leftIterator
					.next();// iterate over nodes
			if (baseLNode.getValue().size() == degree) {// node is of correct degree
				findLPoint = true;
				// node from left embedding to be excised
				s = baseLNode.getKey();
				// neighbor of excised node to be glued
				u = baseLNode.getValue().get(0);
			}
		}

		// find node of appropriate degree from right embedding
		boolean findRPoint = false;
		while (!findRPoint) {
			HashMap.Entry<String, ArrayList<String>> baseRNode = (Map.Entry<String, ArrayList<String>>) rightIterator
					.next();// iterate over nodes
			if (baseRNode.getValue().size() == degree) {// node is of correct degree
				findRPoint = true;
				// node from right embedding to be excised
				t = baseRNode.getKey();
				// neighbor of excised node to be glued
				v = baseRNode.getValue().get(0);
			}
		}
		// diamond sum the two graphs, get embedding out (NOT Graph object)
//		System.out.println("Graphs for gluing");
//		printEmbedding(leftG.getEdgeNodeList());
//		printEmbedding(rightG.getEdgeNodeList());
//		 System.out.println("s, t, u, v: " + s + ", " + t + ", " + u + ", " + v);
		result = leftG.dsum(rightG, s, t, u, v).getEmbedding();
		// System.out.println("result from oglue dsum:");
		return result;
	}

	static int countt1edges(Embedding e, ArrayList<Pair<String, String>> a) {
		int count = 0;
		for (int i = 0; i < a.size(); i++) {
			if (e.type1EdgesSetContains(a.get(i))) {
				count++;
			}
		}
		return count;
	}

	// returns a string indicator for a node of the degree specified by input
	// of an embedding/hashmap?
	private static String findNode(HashMap<String, ArrayList<String>> a, int n) {
		String node = "";
		Iterator<Entry<String, ArrayList<String>>> nodeIterator = a.entrySet().iterator();
		while (nodeIterator.hasNext()) {
			HashMap.Entry<String, ArrayList<String>> nextNode = (Map.Entry<String, ArrayList<String>>) nodeIterator
					.next();
			int temp = (nextNode.getValue().size());
			// System.out.println("constructors 649 temp: " + temp);
			if (temp == n) {
				// System.out.println("constructors 651 temp found! " + temp);
				node = nextNode.getKey();
				// System.out.println("constructors 653 node: " + node);
				return node;
			}
		}
//		System.out.println("constructors 657 NODE NOT FOUND");
		return node;
	}

	public static void printEmbedding(HashMap<String, ArrayList<String>> input) {
		Iterator<Entry<String, ArrayList<String>>> nodeIterator = input.entrySet().iterator();
		while (nodeIterator.hasNext()) {
			HashMap.Entry<String, ArrayList<String>> nextNode = (Map.Entry<String, ArrayList<String>>) nodeIterator
					.next();
			System.out.println("key: " + nextNode.getKey() + ", value: " + nextNode.getValue());

		}
		System.out.println("-----------");
	}

	public static String findPrefix(String s, HashMap<String, ArrayList<String>> h) {
		String nodeLabel = "";

		Iterator<Entry<String, ArrayList<String>>> nodeIterator = h.entrySet().iterator();
		while (nodeIterator.hasNext()) {
			HashMap.Entry<String, ArrayList<String>> nextNode = (Map.Entry<String, ArrayList<String>>) nodeIterator
					.next();

			boolean flag = true;
			String compare = nextNode.getKey();
			for (int i = 0; i < s.length(); i++) {
				if (!(compare.charAt(i) == (s.charAt(i)))) {
					flag = false;
				}
			}
			if (flag) {
				// System.out.println("Constructors 689 z found: " + compare);
				return compare;
			}

		}

		return nodeLabel;
	}

	// for minquad base case generation
	public static Embedding baseCaseGenerator72() {
		Embedding result = new Embedding();

		// initialize the variables we're using here
		Embedding base;
		Embedding base7;

		base = new Embedding(BaseCases.Obase80);
		// for tracking the while loop
		int nCounter = 8;
		String z, x, otherNode, a, b;

		// where the magic happens
		while (nCounter < 12) {
			Embedding interResult;
//				System.out.println("constructors 77 nCounter: " + nCounter);

			base7 = new Embedding(BaseCases.Obase72);

			// now have both base7 and base initialized
//				System.out.println("constructors 90 base7: " + base7);
			// need to initialize z,x,otherNode, a, b
			// z should be of degree 2
			z = findNode(base7.getEmbedding(), 2);
			// an arbitrary neighbor of z
			x = base7.getEmbedding().get(z).get(0);
			// neighbor of x to be merged from left
			a = base7.getEmbedding().get(x).get(0);

			// for the first, parenthesized diamond sum
			Embedding k6nMinus1 = buildk6n(nCounter - 1);
//				System.out.println("constructors 101, k6nMinus1: " + k6nMinus1);
			otherNode = findNode(k6nMinus1.getEmbedding(), 6);
			// neighbor of othernode to be merged with 'a' from right
			b = k6nMinus1.getEmbedding().get(otherNode).get(0);
//				System.out.println("constructors 105 x,otherNode,a,b: " + x + ", " + otherNode + ", " + a + ", " + b);
			// first diamond sum
			interResult = base7.rawDSum(k6nMinus1, x, otherNode, a, b);

//				System.out.println("constructors 109 first diamond sum: " + interResult);

			// reset z,x,otherNode,a,b for second diamond sum
			// since z is no longer of degree 2, but need to use it after the
			// first dsum/merge anyway
			z = findPrefix("07", interResult.getEmbedding());
			x = z;
			a = interResult.getEmbedding().get(x).get(0);
			// the 'right' structure is base
//				System.out.println("nCounter-1: " + Integer.toString(nCounter-1));
//				System.out.println("constructors 118 base: " + base);
			otherNode = findNode(base.getEmbedding(), nCounter - 1);
			b = base.getEmbedding().get(otherNode).get(0);
//				System.out.println("constructors 121 base: " + base);
//				System.out.println("constructors 122 x, otherNode, a, b: " + x + ", " + otherNode + ", " + a + ", " + b);
			base = interResult.rawDSum(base, x, otherNode, a, b);
			result = base;
//				System.out.println("constructors 124 result: " + result);
			nCounter += 4;
		}
		return result;
	}

	// for minquad base case generation
	public static Embedding baseCaseGenerator112() {
		// initialization to avoid compiler error
		Embedding result = new Embedding();

		int baseNFlag = 5;

		// initialize the variables we're using here
		Embedding base = new Embedding(BaseCases.base5);
		Embedding base11;

		// for tracking the while loop
		int nCounter = baseNFlag;
		String z, x, otherNode, a, b;
		result = base;

		// where the magic happens
		while (nCounter < 13) {
			Embedding interResult;
			System.out.println("constructors 141 nCounter: " + nCounter);
			base11 = new Embedding(BaseCases.Obase118);
			// now have both base7 and base initialized
//						System.out.println("constructors 141 base11: " + base11);
			// need to initialize z,x,otherNode, a, b
			// change the labelling such that z gets an 'z' at the beginning of the key for
			// the node
			String newLabel = "z";
			String oldKey = "11";
			base11.nodeRelabel(oldKey, newLabel);
			// z should be of degree 2
			z = findNode(base11.getEmbedding(), 2);
			// an arbitrary neighbor of z
			x = base11.getEmbedding().get(z).get(0);
			// neighbor of x to be merged from left
			a = base11.getEmbedding().get(x).get(0);
			// System.out.println("constructors 166 base11: " + base11);
			// for the first, parenthesized diamond sum
			Embedding k10nMinus1 = buildk10n(nCounter - 1);
//						System.out.println("constructors 170, k10nMinus1: " + k10nMinus1);
//				Facetracer.getFaces(k10nMinus1.getEmbedding());
			otherNode = findNode(k10nMinus1.getEmbedding(), 10);
			// neighbor of othernode to be merged with 'a' from right
			b = k10nMinus1.getEmbedding().get(otherNode).get(0);
			System.out.println(
					"constructors 173 z, x, otherNode, a, b: " + z + ", " + x + ", " + otherNode + ", " + a + ", " + b);
			// first diamond sum
			interResult = base11.rawDSum(k10nMinus1, x, otherNode, a, b);

//						System.out.println("constructors 177 first diamond sum: " + interResult);

			// reset z,x,otherNode,a,b for second diamond sum
			// since z is no longer of degree 2, but need to use it after the
			// first dsum/merge anyway
			z = findPrefix("0z", interResult.getEmbedding());
			x = z;
			a = interResult.getEmbedding().get(x).get(0);
			// the 'right' structure is base
			System.out.println("constructors 187 nCounter-1: " + Integer.toString(nCounter - 1));
//						System.out.println("constructors 188 base: " + base);
			otherNode = findNode(base.getEmbedding(), nCounter - 1);
			b = base.getEmbedding().get(otherNode).get(0);
//						System.out.println("constructors 121 base: " + base);
//						System.out.println("constructors 122 x, otherNode, a, b: " + x + ", " + otherNode + ", " + a + ", " + b);
			base = interResult.rawDSum(base, x, otherNode, a, b);
			result = base;
//						System.out.println("constructors 195 result: " + result);
			nCounter += 8;
		}

		return result;
	}

	// for constructing an ORIENTABLE minquad embedding
	// for constructing an ORIENTABLE minquad embedding
	public static Embedding minQuadO(int n, int t) {
		// initialization to avoid compiler error
		Embedding result = new Embedding();

		// base cases are n=4,5,6,7 for the equivalence classes 0,1,2,3
		int baseNFlag = n % 8;// 0 = 4, 1 = 5, 2 = 6, 3 = 7
//				System.out.println("baseNFlag: " + baseNFlag);

		// equivalence classes for t = 0,1,2,3
		int baseTFlag = t % 8;
//				System.out.println("base tflag:" + baseTFlag);

		// t's parity is determined by n
		int tParity = ((n * (n - 5)) / 2) % 4;
//				System.out.println("Tparity: " + tParity);
		int tParityFlag = t % 4;

		// quick parity check for validity
		if (tParity != tParityFlag) {
			System.out.println("----------------------------------------------------");
			System.out.println("INVALID t VALUE -- NON MATCHING PARITY BETWEEN n determined, t");
			System.out.println("----------------------------------------------------");
			System.out.println();
			return new Embedding();
		}

		// initialize the variables we're using here
		Embedding base = new Embedding();
		Embedding base11;

		int startingT = 0;

		// TODO change the base case initializations
		if (baseNFlag == 0) {// start with 8,0
//					System.out.println("constructors 46");
			baseNFlag = 8;
			int check = Math.min(4, t);
			if (check == 4) {
				System.out.println("constructors 50");
				base = new Embedding(BaseCases.Obase84);
				startingT = 4;
			} else {
				base = new Embedding(BaseCases.Obase80);
				startingT = 0;
			}
		} else if (baseNFlag == 1) { // start with 9, 2
//					System.out.println("constructors 57");
			baseNFlag = 9;
			base = new Embedding(BaseCases.Obase92);
			startingT = 2;
		} else if (baseNFlag == 2) { // start with 10,1 or 10,5
			System.out.println("constructors 62");
			baseNFlag = 10;
			if (t < 5) {
				System.out.println("constructors 66");
				base = new Embedding(BaseCases.Obase101);
				startingT = 1;
			} else {
				System.out.println("constructors 70");
				base = new Embedding(BaseCases.Obase105);
				startingT = 5;
			}
		} else if (baseNFlag == 3) {// start with 11,1 or 11,5
			baseNFlag = 11;
			if (t < 5) {
				base = new Embedding(BaseCases.Obase111);
				startingT = 1;
			} else {
				base = new Embedding(BaseCases.Obase115);
				startingT = 5;
			}
		} else if (baseNFlag == 4) {// start with 12,2 or 12,6
			baseNFlag = 12;
			if (t < 6) {
				base = new Embedding(BaseCases.Obase122);
				startingT = 2;
			} else {
				base = new Embedding(BaseCases.Obase126);
				startingT = 6;
			}

		} else if (baseNFlag == 5) {// start with 13,0 or 13,4 or 13,8 ??
			boolean initialized = false;
			baseNFlag = 13;
			int check = Math.min(8, t);
			if (check == 8) {
				System.out.println("constructors 98");
				initialized = true;
				base = new Embedding(BaseCases.Obase138);
				startingT = 8;
			}
			check = Math.min(4, t);
			if (check == 4 && !initialized) {
				System.out.println("constructors 239");
				initialized = true;
				base = new Embedding(BaseCases.Obase134);
				startingT = 4;
			}
			check = Math.min(0, t);
			if (check == 0 && !initialized) {
				System.out.println("constructors 246");
				base = new Embedding(BaseCases.Obase13);
				startingT = 0;
			}

		} else if (baseNFlag == 6) {// start with 14,3 or 14,7
			baseNFlag = 14;
			int check = Math.min(7, t);
			if (check == 7) {
				base = new Embedding(BaseCases.Obase147);
				startingT = 7;
			} else {
				base = new Embedding(BaseCases.Obase143);
				startingT = 3;
			}

		} else if (baseNFlag == 7) {
			System.out.println("constructors 123");
			baseNFlag = 7;
			base = new Embedding(BaseCases.Obase73);
			startingT = 3;
		} else {// ERROR
			System.out.println("ERROR, NO BASE EQUIVALENCE CLASS MATCHES");
			base = new Embedding();
			return result;
		}
		// for tracking the while loop
		int nCounter = baseNFlag;
		int tCounter = startingT;
		String z, x, otherNode, a, b;
		result = base;

		// where the magic happens
		while (nCounter < n) {
			Embedding interResult;
			System.out.println("constructors 141 nCounter: " + nCounter);
			if ((tCounter % 8) != (t % 8)) {// get t up by increment of 4
				System.out.println("Constructors 143");
				base11 = new Embedding(BaseCases.Obase114);
				tCounter += 4;
			} else if (tCounter < t) {// get t up by increment of 8
				System.out.println("constructors 147");
				base11 = new Embedding(BaseCases.Obase118);
				tCounter += 8;
			} else {// increment n only
				System.out.println("constructors 151");
				base11 = new Embedding(BaseCases.Obase110);
			}
			// now have both base7 and base initialized
//					System.out.println("constructors 141 base11: " + base11);
			// need to initialize z,x,otherNode, a, b
			// change the labelling such that z gets an 'z' at the beginning of the key for
			// the node
			String newLabel = "z";
			String oldKey = "11";
			base11.nodeRelabel(oldKey, newLabel);
			// z should be of degree 2
			z = findNode(base11.getEmbedding(), 2);
			// an arbitrary neighbor of z
			x = base11.getEmbedding().get(z).get(0);
			// neighbor of x to be merged from left
			a = base11.getEmbedding().get(x).get(0);
			// System.out.println("constructors 166 base11: " + base11);
			// for the first, parenthesized diamond sum
			Embedding k10nMinus1 = buildk10n(nCounter - 1);
//					System.out.println("constructors 170, k10nMinus1: " + k10nMinus1);
			Facetracer.getFaces(k10nMinus1.getEmbedding());
			otherNode = findNode(k10nMinus1.getEmbedding(), 10);
			// neighbor of othernode to be merged with 'a' from right
			b = k10nMinus1.getEmbedding().get(otherNode).get(0);
			System.out.println(
					"constructors 173 z, x, otherNode, a, b: " + z + ", " + x + ", " + otherNode + ", " + a + ", " + b);
			// first diamond sum
			interResult = base11.rawDSum(k10nMinus1, x, otherNode, a, b);

//					System.out.println("constructors 177 first diamond sum: " + interResult);

			// reset z,x,otherNode,a,b for second diamond sum
			// since z is no longer of degree 2, but need to use it after the
			// first dsum/merge anyway
			z = findPrefix("0z", interResult.getEmbedding());
			x = z;
			a = interResult.getEmbedding().get(x).get(0);
			// the 'right' structure is base
			System.out.println("constructors 187 nCounter-1: " + Integer.toString(nCounter - 1));
//					System.out.println("constructors 188 base: " + base);
			otherNode = findNode(base.getEmbedding(), nCounter - 1);
			b = base.getEmbedding().get(otherNode).get(0);
//					System.out.println("constructors 121 base: " + base);
//					System.out.println("constructors 122 x, otherNode, a, b: " + x + ", " + otherNode + ", " + a + ", " + b);
			base = interResult.rawDSum(base, x, otherNode, a, b);
			result = base;
//					System.out.println("constructors 195 result: " + result);
			nCounter += 8;
		}

		return result;
	}

	// this is the nonorientable constructor
	public static Embedding minQuad(int n, int t) {
		// avoiding error initialization
		Embedding result = new Embedding();

		// base cases are n=4,5,6,7 for the equivalence classes 0,1,2,3
		int baseNFlag = n % 4;// 0 = 4, 1 = 5, 2 = 6, 3 = 7
//			System.out.println("baseNFlag: " + baseNFlag);

		// equivalence classes for t = 0,1,2,3
		int baseTFlag = t % 4;
//			System.out.println("base tflag:" + baseTFlag);

		// t's parity is determined by n
		int tParity = ((n * (n - 5)) / 2) % 2;
//			System.out.println("Tparity: " + tParity);
		int tParityFlag = t % 2;

		// quick parity check for validity
		if (tParity != tParityFlag) {
			System.out.println("----------------------------------------------------");
			System.out.println("INVALID t VALUE -- NON MATCHING PARITY BETWEEN n determined, t");
			System.out.println("----------------------------------------------------");
			System.out.println();
			return new Embedding();
		}

		// initialize the variables we're using here
		Embedding base;
		Embedding base7;

		int startingT = 0;

		if (baseNFlag == 0) {// start with 4,0
//				System.out.println("constructors 44");
			baseNFlag = 4;
			base = new Embedding(BaseCases.base4, BaseCases.base4t1edges);
		} else if (baseNFlag == 1) { // start with 5, 0
//				System.out.println("constructors 48");
			baseNFlag = 5;
			base = new Embedding(BaseCases.base5);
		} else if (baseNFlag == 2) { // start with 6,1
//				System.out.println("constructors 52");
			baseNFlag = 6;
			base = new Embedding(BaseCases.base61, BaseCases.base61t1edges);
			startingT = 1;
		} else {// start with either 7,1 or possibly 7,3
			baseNFlag = 7;
			int check = Math.min(3, t);
			if (check == 3) {// want startingT to be as large as possible, <= t
//					System.out.println("constructors 60");
				base = new Embedding(BaseCases.base73, BaseCases.base73t1edges);
				startingT = 3;
			} else {
//					System.out.println("constructors 64");
				startingT = 1;
				base = new Embedding(BaseCases.base71, BaseCases.base70t1edges);
			}
		}
		// for tracking the while loop
		int nCounter = baseNFlag;
		int tCounter = startingT;
		String z, x, otherNode, a, b;

		// where the magic happens
		while (nCounter < n) {
			Embedding interResult;
//				System.out.println("constructors 77 nCounter: " + nCounter);
			if ((tCounter % 4) != baseTFlag) {// get t up by increment of 2
//					System.out.println("Constructors 78");
				base7 = new Embedding(BaseCases.base72, BaseCases.base72t1edges);
				tCounter += 2;
			} else if (tCounter < t) {// get t up by increment of 4
//					System.out.println("constructors 82");
				base7 = new Embedding(BaseCases.base74, BaseCases.base74t1edges);
				tCounter += 4;
			} else {// increment n only
//					System.out.println("constructors 86");
				base7 = new Embedding(BaseCases.base70, BaseCases.base70t1edges);
			}
			// now have both base7 and base initialized
//				System.out.println("constructors 90 base7: " + base7);
			// need to initialize z,x,otherNode, a, b
			// z should be of degree 2
			z = findNode(base7.getEmbedding(), 2);
			// an arbitrary neighbor of z
			x = base7.getEmbedding().get(z).get(0);
			// neighbor of x to be merged from left
			a = base7.getEmbedding().get(x).get(0);

			// for the first, parenthesized diamond sum
			Embedding k6nMinus1 = buildk6n(nCounter - 1);
//				System.out.println("constructors 101, k6nMinus1: " + k6nMinus1);
			otherNode = findNode(k6nMinus1.getEmbedding(), 6);
			// neighbor of othernode to be merged with 'a' from right
			b = k6nMinus1.getEmbedding().get(otherNode).get(0);
//				System.out.println("constructors 105 x,otherNode,a,b: " + x + ", " + otherNode + ", " + a + ", " + b);
			// first diamond sum
			interResult = base7.rawDSum(k6nMinus1, x, otherNode, a, b);

//				System.out.println("constructors 109 first diamond sum: " + interResult);

			// reset z,x,otherNode,a,b for second diamond sum
			// since z is no longer of degree 2, but need to use it after the
			// first dsum/merge anyway
			z = findPrefix("07", interResult.getEmbedding());
			x = z;
			a = interResult.getEmbedding().get(x).get(0);
			// the 'right' structure is base
//				System.out.println("nCounter-1: " + Integer.toString(nCounter-1));
//				System.out.println("constructors 118 base: " + base);
			otherNode = findNode(base.getEmbedding(), nCounter - 1);
			b = base.getEmbedding().get(otherNode).get(0);
//				System.out.println("constructors 121 base: " + base);
//				System.out.println("constructors 122 x, otherNode, a, b: " + x + ", " + otherNode + ", " + a + ", " + b);
			base = interResult.rawDSum(base, x, otherNode, a, b);
			result = base;
//				System.out.println("constructors 124 result: " + result);
			nCounter += 4;
		}
		return result;
	}

	// intakes arbitrary natural number n
	// returns k10n (orientable)
	public static Embedding buildk10n(int n) {
		// FOR BUILDING UP THE K10,N
		// first building block, left embedding
		Graph first = new Graph(BaseCases.base103, 30);
		HashMap<String, ArrayList<String>> result = first.getEmbedding().getEmbedding();

		// second building block, right embedding
		Graph second = new Graph(BaseCases.base103, 30);
		HashMap<String, ArrayList<String>> right = second.getEmbedding().getEmbedding();

		/*
		 * if (m < n) {// check base assumption about m,n
		 * System.out.println("m is not greater than or equal to n?!"); return result; }
		 */

		// build up a k10n graph
		int counter = 3;
		while (counter < n) {
			// System.out.println("Constructors 266 IN HERE");
			result = Oglue(result, right, 10);
			second = new Graph(BaseCases.base103, 30);
			right = second.getEdgeNodeList();
			counter++;
		}
		return new Embedding(result);
	}

	// intakes arbitrary natural number n
	// returns k6n (orientable)
	public static Embedding buildk6n(int n) {
		// FOR BUILDING UP THE K6,N
		// first building block, left embedding
		Graph first = new Graph(BaseCases.basek63array, 18);
		HashMap<String, ArrayList<String>> result = first.getEmbedding().getEmbedding();

		// second building block, right embedding
		Graph second = new Graph(BaseCases.basek63array, 18);
		HashMap<String, ArrayList<String>> right = second.getEmbedding().getEmbedding();

		/*
		 * if (m < n) {// check base assumption about m,n
		 * System.out.println("m is not greater than or equal to n?!"); return result; }
		 */

		// build up a k6m graph
		int counter = 3;
		while (counter < n) {
			// System.out.println("Constructors 266 IN HERE");
			result = Oglue(result, right, 6);
			second = new Graph(BaseCases.basek63array, 18);
			right = second.getEdgeNodeList();
			counter++;
		}
		return new Embedding(result);
	}

	// private helper function for modulo on negative numbers
	// e.g. -13 % 64 = 51
	private static int myMod(int x, int n) {
		int r = x % n;
		if (r < 0) {
			r = (r += n) % n;
		}

		return r;
	}

}
