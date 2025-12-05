package codebase;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

public class Embedding {
	// the actual HashMap data structure which stores the embedding
	private HashMap<String, ArrayList<String>> embedding = new HashMap<String, ArrayList<String>>();
	private boolean orientable = false;

	// for storing the edges which have a half twist
	private HashSet<Pair<String, String>> type1Edges = new HashSet<Pair<String, String>>();

	// empty pro forma constructor
	public Embedding() {

	}

	// constructor from a 2d array of ints
	public Embedding(Integer[][] input) {
		// takes in a 2d array and adds to this object's main data structure
		// i.e. the graph embedding as a hashmap

		for (int i = 0; i < input.length; i++) {
			ArrayList<String> temp = new ArrayList<String>();
			for (int j = 0; j < input[i].length; j++) {
				temp.add(Integer.toString(input[i][j]));
			}
			this.embedding.put(Integer.toString(i), temp);
		}

	}

	// constructor from a 2d array of ints, with list of type 1 edges
	public Embedding(Integer[][] input, ArrayList<Pair<String, String>> type1List) {
		// takes in a 2d array and adds to this object's main data structure
		// i.e. the graph embedding as a hashmap
		for (int i = 0; i < input.length; i++) {
			ArrayList<String> temp = new ArrayList<String>();
			for (int j = 0; j < input[i].length; j++) {
				temp.add(Integer.toString(input[i][j]));
			}
			this.embedding.put(Integer.toString(i), temp);
		}

		for (int i = 0; i < type1List.size(); i++) {
			this.type1Edges.add(type1List.get(i));
		}

		this.setOrientable();
	}

	// constructor from a 2d array of ints, with list of type 1 edges
	public Embedding(Integer[][] input, Integer[][] type1List) {
		// takes in a 2d array and adds to this object's main data structure
		// i.e. the graph embedding as a hashmap
		for (int i = 0; i < input.length; i++) {
			ArrayList<String> temp = new ArrayList<String>();
			for (int j = 0; j < input[i].length; j++) {
				temp.add(Integer.toString(input[i][j]));
			}
			this.embedding.put(Integer.toString(i), temp);
		}

		ArrayList<Pair<String, String>> type1ArrayList = this.generatet1edges(type1List);

		for (int i = 0; i < type1ArrayList.size(); i++) {
			this.type1Edges.add(type1ArrayList.get(i));
		}

		// this.setOrientable();
	}

	// constructor from a HashMap
	public Embedding(HashMap<String, ArrayList<String>> input) {
		this.embedding = input;
		// this.setOrientable();
	}

	// constructor from a HashMap and ArrayList of type1 edges
	public Embedding(HashMap<String, ArrayList<String>> input, ArrayList<Pair<String, String>> type1List) {
		this.embedding = input;
		for (int i = 0; i < type1List.size(); i++) {
			this.type1Edges.add(type1List.get(i));
		}
		this.setOrientable();
	}

	// constructor from a HashMap and HashSet of type1 edges
	public Embedding(HashMap<String, ArrayList<String>> input, HashSet<Pair<String, String>> type1List) {
		this.embedding = input;
		this.type1Edges = type1List;
		// this.setOrientable();
	}

	// for flipping vertices
	// all type 0 edges become type 1 edges
	// all type 1 edges become type 0 edges
	public void flipNode(String currNode, Embedding e) {
		ArrayList<String> currNodeNeighbors = e.getEmbedding().get(currNode);
		Pair<String, String> currEdge = new Pair<String, String>("filler", "moreFiller");

		ArrayList<Pair<String, String>> addList = new ArrayList<Pair<String, String>>();
		ArrayList<Pair<String, String>> removeList = new ArrayList<Pair<String, String>>();

		// this.printString();

		// System.out.println("node being flipped: " + currNode);

		// go through this node's neighbors
		for (int i = 0; i < currNodeNeighbors.size(); i++) {
			// look at each edge in turn
			currEdge = new Pair<String, String>(currNode, currNodeNeighbors.get(i));
			// System.out.println("currEdge: " + currEdge.toString());
			// for iterating through this embedding's type1 edges

			// this edge is in the t1 edges
			if (type1EdgesSetContains(currEdge)) {
				removeList.add(currEdge);
			} else {// this edge is NOT in the t1 edges
				addList.add(currEdge);
			}

		}

		// this.printString();

		for (int i = 0; i < addList.size(); i++) {
			// System.out.println("Adding: " + addList.get(i).toString());
			// System.out.println(type1Edges.add(addList.get(i)));
			type1Edges.add(addList.get(i));
		}

		for (int i = 0; i < removeList.size(); i++) {
			// System.out.println("removal of: " + removeList.get(i));
			// System.out.println(removeT1edge(removeList.get(i)));
			removeT1edge(removeList.get(i));
		}
		// reverse list of neighbors to account for the 'flip'
		Collections.reverse(this.embedding.get(currNode));

	}

	// for "cleaning" a node
	// i.e. making all of its incident edges type 0
	// by flipping any neighbors necessary
	public void cleanNode(String node, Embedding e) {
//		 System.out.println("clean node printing: " + node);
		// this node's neighbors in an ArrayList
		ArrayList<String> neighbors = e.getEmbedding().get(node);
//		 System.out.println("neighbors: " + neighbors);
		// placeholder initialization
		Pair<String, String> currEdge = new Pair<String, String>("", "");
		for (int i = 0; i < neighbors.size(); i++) {
			String otherNode = neighbors.get(i);
			currEdge = new Pair<String, String>(node, otherNode);
			// this edge is a type 1 edge
			if (e.type1EdgesSetContains(currEdge)) {
				// flip it (so that this edge is no longer a type 1 edge
//				 System.out.println("flip clean node printing BEFORE: " + node);
				e.flipNode(otherNode, e);
//				 System.out.println("flip clean node printing AFTER: " + node);
			}
		}

	}

	// for everything except face tracing
	public HashMap<String, ArrayList<String>> getEmbedding() {
		return this.embedding;
	}

	// for face tracing ONLY
	public HashMap<String, ArrayList<String>> getEmbedding(boolean b) {
		return this.doubleCover();

	}

	// returns the number of nodes of this embedding
	public int getNodes() {
		int numNodes = 0;

		Iterator<Entry<String, ArrayList<String>>> nodeIterator = this.embedding.entrySet().iterator();
		while (nodeIterator.hasNext()) {
			HashMap.Entry<String, ArrayList<String>> nextNode = (Map.Entry<String, ArrayList<String>>) nodeIterator
					.next();
			numNodes++;
		}

		return numNodes;
	}

	// returns the number of edges of this embedding
	public int getEdges() {
		int numEdges = 0;

		Iterator<Entry<String, ArrayList<String>>> nodeIterator = this.embedding.entrySet().iterator();
		while (nodeIterator.hasNext()) {
			HashMap.Entry<String, ArrayList<String>> nextNode = (Map.Entry<String, ArrayList<String>>) nodeIterator
					.next();
			numEdges += nextNode.getValue().size();
		}

		return (numEdges / 2);
	}

	// returns an orientable "double cover" of this embedding
	// since this embedding might have type1 (i.e. "half twist", or
	// "moebius strip") edges
	public HashMap<String, ArrayList<String>> doubleCover() {
		HashMap<String, ArrayList<String>> orientableDoubleCover = new HashMap<String, ArrayList<String>>();

		String type0 = "";// key for type0 node
		String type1 = "";// key for type1 node
		Iterator<Entry<String, ArrayList<String>>> nodeIterator = this.embedding.entrySet().iterator();
		while (nodeIterator.hasNext()) {// loop through the embedding
			HashMap.Entry<String, ArrayList<String>> nextNode = (Map.Entry<String, ArrayList<String>>) nodeIterator
					.next();
			type0 = nextNode.getKey();// no marking (prepending "+") means type 0
			type1 = "+" + nextNode.getKey();// prepended "+" means it's a type 1
			ArrayList<String> edgeChecker = nextNode.getValue();
			// for the type0 node neighbors
			ArrayList<String> type0EdgeList = new ArrayList<String>();
			// for the type1 node neighbors
			ArrayList<String> type1EdgeList = new ArrayList<String>();

			// loop through the neighbors of the current node
			for (int i = 0; i < edgeChecker.size(); i++) {
				String current = edgeChecker.get(i);
				// check to see if current edge is type 1
				if (t1contains(type0, current)) {
					type1EdgeList.add(0, current);
					type0EdgeList.add("+" + current);
				} else {
					type1EdgeList.add(0, "+" + current);
					type0EdgeList.add(current);
				}
			}
			// add the current double nodes and list of neighbors to the results
			orientableDoubleCover.put(type0, type0EdgeList);
			orientableDoubleCover.put(type1, type1EdgeList);
		}
		// System.out.println("orientable double cover called, ");
		// System.out.println(orientableDoubleCover.toString());
		// return the results
		return orientableDoubleCover;
	}

	@Override
	public String toString() {
		// prints out a string representation of this graph embedding
		// intended to be human readable
		String output = "\n";
		Iterator<Entry<String, ArrayList<String>>> nodeIterator = this.embedding.entrySet().iterator();
		while (nodeIterator.hasNext()) {
			HashMap.Entry<String, ArrayList<String>> nextNode = (Map.Entry<String, ArrayList<String>>) nodeIterator
					.next();
			output += (nextNode.getKey() + ". " + nextNode.getValue() + "\n");
		}
		output += "\n";

		Iterator<Pair<String, String>> t1Iterator = this.type1Edges.iterator();
		while (t1Iterator.hasNext()) {
			Pair<String, String> p = t1Iterator.next();
			output += ("t1 edge: " + p.getVal1() + ", " + p.getVal2() + "\n");
		}
		output += "\n";

		return output;
	}

	// for use in FormatConverter to write an input text file
	// to a Java Integer[][] format as displayed in the console
	public String toJavaFormat() {
		String result = "";
		result += "{\n";
		Iterator<Entry<String, ArrayList<String>>> nodeIterator = this.embedding.entrySet().iterator();
		while (nodeIterator.hasNext()) {

			HashMap.Entry<String, ArrayList<String>> nextNode = (Map.Entry<String, ArrayList<String>>) nodeIterator
					.next();
			result += nextNode.getKey() + ": ";
			result += "{";
			ArrayList<String> neighbors = nextNode.getValue();
			for (int i = 0; i < neighbors.size(); i++) {
				result += neighbors.get(i);
				if (i != neighbors.size() - 1) {
					result += ", ";
				}
			}
			result += "}";
			if (nodeIterator.hasNext()) {
				result += ", \n";
			}
		}
		result += "};";
		return result;
	}

	public void printString() {
		System.out.println(this.toString());
	}

	// for testing if an edge is contained in the type1 edge collection
	// i.e. if a given edge is type 1
	private boolean t1contains(String s1, String s2) {
		Iterator<Pair<String, String>> t1iterator = type1Edges.iterator();
		while (t1iterator.hasNext()) {
			Pair<String, String> tester = t1iterator.next();
			if (tester.matches(s1, s2)) {
				return true;
			}
		}
		return false;
	}

	// for relabel testing and actual relabelling
	public boolean t1containsNodeRelabel(String ogLabel, String newLabel) {
		Iterator<Pair<String, String>> t1iterator = type1Edges.iterator();
		// System.out.println("BEFORE RELABEL t1edges: " +
		// this.getT1edges().toString());
		while (t1iterator.hasNext()) {
			Pair<String, String> tester = t1iterator.next();
			if (tester.getVal1().equals(ogLabel)) {// first entry in pair matches
				// System.out.println("first entry in pair matches, is: " + ogLabel);
				tester.setVal1(newLabel);// relabel
				tester.sort();
				// System.out.println("AFTER RELABEL t1edges: " + this.getT1edges().toString());
			} else if (tester.getVal2().equals(ogLabel)) {// second entry in pair matches
				// System.out.println("second entry in pair matches, is: " + ogLabel);
				tester.setVal2(newLabel);// relabel
				tester.sort();
				// System.out.println("AFTER RELABEL t1edges: " + this.getT1edges().toString());
			}
		}
		return true;
	}

	// for checking orientability of the Embedding
	// RETURNS FALSE AS OF NOW (working code commented out)
	public boolean isOrientable() {

		// setup to make a deep copy of this embedding's HashMap for checking
		// Orientability
		HashMap<String, ArrayList<String>> embeddingCopy = new HashMap<String, ArrayList<String>>();
		String copyNode = "";
		ArrayList<String> copyNodeNeighbors = new ArrayList<String>();
		Iterator<Entry<String, ArrayList<String>>> nodeCopyIterator = this.embedding.entrySet().iterator();

		while (nodeCopyIterator.hasNext()) {
			HashMap.Entry<String, ArrayList<String>> nextNode = (Map.Entry<String, ArrayList<String>>) nodeCopyIterator
					.next();
			copyNode = nextNode.getKey();
			ArrayList<String> neighbors = nextNode.getValue();
			for (int i = 0; i < neighbors.size(); i++) {
				copyNodeNeighbors.add(neighbors.get(i));
			}
			embeddingCopy.put(copyNode, copyNodeNeighbors);
			copyNodeNeighbors = new ArrayList<String>();
		}

		// setup to make deep copy of this embedding's t1 edges for checking
		// Orientability
		HashSet<Pair<String, String>> embeddingCopyT1Edges = new HashSet<Pair<String, String>>();
		Iterator<Pair<String, String>> t1Iterator = this.type1Edges.iterator();
		while (t1Iterator.hasNext()) {
			Pair<String, String> nextT1Edge = t1Iterator.next();
			Pair<String, String> t1EdgeCopy = new Pair<String, String>(nextT1Edge.getVal1(), nextT1Edge.getVal2());
			embeddingCopyT1Edges.add(t1EdgeCopy);
		}

		// embeddingCopy should now be a full, separate copy of this embedding
		// embedCopy is an Embedding
		// embeddingCopy is a HashMap
		// t1 edges are also in embeddingcopyT1Edges

		// make the copy
		Embedding embedCopy = new Embedding(embeddingCopy, embeddingCopyT1Edges);

		ArrayList<Pair<String, String>> spanTree = new ArrayList<Pair<String, String>>();
		ArrayList<Pair<String, String>> spanTreeComplement = new ArrayList<Pair<String, String>>();

		// queue for BFS; NB can only use add and remove due to interface
		Queue<String> q = new ArrayDeque<String>();

		// for checking against already visited nodes
		HashSet<String> visited = new HashSet<String>();
		String currentNode = "";
		ArrayList<String> currentNodeNeighbors = new ArrayList<String>();

		Iterator<Entry<String, ArrayList<String>>> nodeIterator = embeddingCopy.entrySet().iterator();
		while (nodeIterator.hasNext()) { // get the first node we examine (doesn't matter strictly which is first)
			HashMap.Entry<String, ArrayList<String>> nextNode = (Map.Entry<String, ArrayList<String>>) nodeIterator
					.next();
			currentNode = nextNode.getKey();
			currentNodeNeighbors = nextNode.getValue();

			// System.out.println("embedding 402 current node neighbors size: " +
			// currentNodeNeighbors.size());

			while (visited.size() < embeddingCopy.size()) {// need to visit all nodes
				if (!visited.contains(currentNode)) {// currentNode not visited yet
					for (int i = 0; i < currentNodeNeighbors.size(); i++) {
						if (!visited.contains(currentNodeNeighbors.get(i))) {
							spanTree.add(new Pair<String, String>(currentNode, currentNodeNeighbors.get(i)));
						} else {
							spanTreeComplement.add(new Pair<String, String>(currentNode, currentNodeNeighbors.get(i)));
						}
						q.add(currentNodeNeighbors.get(i));// addall neighbors to queue
					}
					visited.add(currentNode);// mark currentNode visited
				}
				currentNode = q.remove();// go to next node in q
				currentNodeNeighbors = embeddingCopy.get(currentNode); // set up neighbors
				break;
			} // spanTree should nowcontain a spanning tree of the graph
				// break;// leave the spanning tree generating loop
		}

		// iterate through the spanning tree, flipping necessary vertices
		for (int i = 0; i < spanTree.size(); i++) {
			Pair<String, String> currentEdge = spanTree.get(i);
			if (embedCopy.type1EdgesSetContains(currentEdge)) {// thisedge is a type1 edge
				embedCopy.flipNode(currentEdge.getVal2(), embedCopy);
			}
		}

		boolean allTreeEdgesZero = true;
		for (int i = 0; i < spanTree.size(); i++) {
			if (embedCopy.type1EdgesSetContains(spanTree.get(i))) {
				allTreeEdgesZero = false;
				break;// out of for loop
			}
		}

		boolean allOtherEdgesZero = true;
		for (int i = 0; i < spanTreeComplement.size(); i++) {
			if (embedCopy.type1EdgesSetContains(spanTreeComplement.get(i))) {
				allOtherEdgesZero = false;
				break;// out of for loop
			}
		}

		// this.printString();

		if (allTreeEdgesZero && allOtherEdgesZero) { // this Embedding is orientable
			this.orientable = true;
			System.out.println("embedding 449, IS ORIENTABLE");
			return true;
		} else { // this Embedding is NOT orientable
			System.out.println("embedding 452, this embedding is NOT ORIENTABLE");
			this.orientable = false;
			return false;
		}
//		 return false;
	}

	public boolean getOrientable() {
		return this.orientable;
	}

	private void setOrientable() {
		this.isOrientable();
	}

	// for checking if this object's hashSet containing type1 edges
	// contains a particular pair (or its equivalent
	public boolean type1EdgesSetContains(Pair<String, String> p) {
		Iterator<Pair<String, String>> it = this.type1Edges.iterator();
		while (it.hasNext()) {
			if (it.next().isEquivalent(p)) {
				return true;
			}
		}
		return false;
	}

	// for checking if this object's hashSet containing type1 edges
	// contains a particular pair (or its equivalent
	public boolean type1EdgesSetContains(String s) {
		Iterator<Pair<String, String>> it = this.type1Edges.iterator();
		while (it.hasNext()) {
			if (it.next().contains(s)) {
				return true;
			}
		}
		return false;
	}

	public HashSet<Pair<String, String>> getT1edges() {
		return this.type1Edges;
	}

	public void addT1edge(Pair<String, String> addP) {
		this.type1Edges.add(addP);
	}

	public boolean removeT1edge(Pair<String, String> removeP) {
		Iterator<Pair<String, String>> t1Iterator = this.type1Edges.iterator();
		while (t1Iterator.hasNext()) {
			Pair<String, String> nextT1Edge = t1Iterator.next();
			if (removeP.isEquivalent(nextT1Edge)) {
				this.type1Edges.remove(nextT1Edge);
				return true;
			}
		}
		return false;
	}

	public Embedding dsum(Embedding e2, int degree) {
		Embedding result = null;

		if (false) {

		} else {
			// System.out.println("non orientable");
			result = NOglue(this, e2, degree);
		}

		return result;
	}

	// helper method for the construct function
	// used in the process of building an arbitrary kmn graph
	// glues orientable OR nonorientable embeddings together
	public static Embedding NOglue(Embedding leftE, Embedding rightE, int degree) {
		Embedding resultE;

		// graph initializations from the fed in embeddings
		// since dsum is only defined on graph objects
		Graph leftG = new Graph(leftE);
		Graph rightG = new Graph(rightE);

		// for finding nodes of appropriate degree to excise and glue together
		Iterator<Entry<String, ArrayList<String>>> leftIterator = leftE.getEmbedding().entrySet().iterator();
		Iterator<Entry<String, ArrayList<String>>> rightIterator = rightE.getEmbedding().entrySet().iterator();

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
		// System.out.println("Graphs for gluing");
		// leftG.getEmbedding().printString();
		// leftG.getEmbedding().printString();
		// System.out.println("s, t, u, v: " + s + ", " + t + ", " + u + ", " + v);
//		System.out.println("Embedding 569 flag");
		resultE = leftG.dsum(rightG, s, t, u, v);
//		System.out.println("result of gluing: ");
//		resultE.printString();
		return resultE;
	}

	// to help generate arraylists of the type1 edges from hard coded examples
	private static ArrayList<Pair<String, String>> generatet1edges(Integer[][] input) {
		ArrayList<Pair<String, String>> output = new ArrayList<Pair<String, String>>();

		for (int i = 0; i < input.length; i++) {
			Pair<String, String> temp = new Pair<String, String>(Integer.toString(input[i][0]),
					Integer.toString(input[i][1]));
			output.add(temp);
		}
		// System.out.println("t1 edges: " + output);
		return output;
	}

	public Embedding rawDSum(Embedding other, String s, String t, String u, String v) {
		Embedding result;
		Graph g = new Graph(this);
		Graph h = new Graph(other);
		result = g.dsum(h, s, t, u, v);
		// System.out.println("Embedding 509 RAW DSUM RESULT");
		// Constructors.printEmbedding(result.getEmbedding());
		return result;
	}

	// returns a boolean indicator for existence of a node of the degree specified
	// by input
	public static boolean nodeExists(HashMap<String, ArrayList<String>> a, int n) {
		boolean flag = false;
		Iterator<Entry<String, ArrayList<String>>> nodeIterator = a.entrySet().iterator();
		while (nodeIterator.hasNext()) {
			HashMap.Entry<String, ArrayList<String>> nextNode = (Map.Entry<String, ArrayList<String>>) nodeIterator
					.next();
			int temp = (nextNode.getValue().size());
			// System.out.println("constructors 674 temp: " + temp);
			if (temp == n) {
				// System.out.println("constructors 676 temp found! " + temp);
				String node = nextNode.getKey();
				// System.out.println("constructors 678 node found: " + node);
				flag = true;
				return flag;
			}
		}
//		System.out.println("constructors 657 NODE NOT FOUND");
		return flag;
	}

	public void nodeRelabel(String oldLabel, String newLabel) {
		ArrayList<String> currentNeighbors = this.getEmbedding().get(oldLabel);
		this.getEmbedding().remove(oldLabel);
		this.getEmbedding().put(newLabel, currentNeighbors);

		Iterator<Entry<String, ArrayList<String>>> nodeIterator = this.getEmbedding().entrySet().iterator();
		while (nodeIterator.hasNext()) {
//			System.out.println("embeddings 597");
			HashMap.Entry<String, ArrayList<String>> nextNode = (Map.Entry<String, ArrayList<String>>) nodeIterator
					.next();
			String currentKey = nextNode.getKey();
			currentNeighbors = this.getEmbedding().get(currentKey);
			for (int i = 0; i < currentNeighbors.size(); i++) {
				if (currentNeighbors.get(i).equals(oldLabel)) {
					currentNeighbors.set(i, newLabel);
				}
			}
		}
	}

	public void insertDegree2Node(String newVertex, String a, String b, String left, String right) {
		// in connection to a, is "left", "right"
		// in connection to b, is "right", "left"
		ArrayList<String> newNode = new ArrayList<String>();
		newNode.add(a);
		newNode.add(b);
		embedding.put(newVertex, newNode);

		ArrayList<String> aNeighbors = embedding.get(a);
		int index;
		if (aNeighbors.indexOf(left) < aNeighbors.indexOf(right)) {
			index = aNeighbors.indexOf(left) + 1;
		} else {
			index = aNeighbors.indexOf(right) + 1;
		}
		aNeighbors.add(index, newVertex);

		ArrayList<String> bNeighbors = embedding.get(b);
		if (bNeighbors.indexOf(left) < bNeighbors.indexOf(right)) {
			index = bNeighbors.indexOf(left) + 1;
		} else {
			index = bNeighbors.indexOf(right) + 1;
		}
		bNeighbors.add(index, newVertex);

	}

	// TODO
	// for checking the results of a Ringel construction
	// should be a K(2n+1,2n) - 2nK(2) graph
	//assumes this Embedding is bipartite (otherwise things get awkward fast)
	public void bipartiteCheck() {

		//System.out.println("SIZE - HAS: " + this.getEmbedding().size() + " NODES");
		
		// start BFS
		HashSet<String> visited = new HashSet<String>();
		HashSet<String> visitedLeft = new HashSet<String>();
		HashSet<String> visitedRight = new HashSet<String>();

		Iterator<Entry<String, ArrayList<String>>> nodeIterator = this.getEmbedding().entrySet().iterator();

		// first node we visit, doesn't really matter which one
		int distance = 0;
		while (nodeIterator.hasNext()) {
			HashMap.Entry<String, ArrayList<String>> nextNode = (Map.Entry<String, ArrayList<String>>) nodeIterator
					.next();
			String currentKey = nextNode.getKey();
			if (!visited.contains(currentKey)) {
				visited.add(currentKey);
				if (distance % 2 == 0) {
					visitedLeft.add(currentKey);
				} else {
					visitedRight.add(currentKey);
				}
				distance++;
				ArrayList<String> currentNeighbors = nextNode.getValue();
				for (int i = 0; i < currentNeighbors.size(); i++) {
					if (!visited.contains(currentNeighbors.get(i))) {
						visited.add(currentNeighbors.get(i));
						if (distance % 2 == 0) {
							visitedLeft.add(currentNeighbors.get(i));
						} else {
							visitedRight.add(currentNeighbors.get(i));
						}
					}
				}
				distance++;
			}
		}
		// all nodes shold now be in visited
//		System.out.println("embedding 727 visited: " + visited);
//		System.out.println("embedding 728 visitedLeft: " + visitedLeft);
//		System.out.println("embedding 729 visitedRight: " + visitedRight);
		
		//count up the degrees present across both
		//store in a HashMap
		HashMap<Integer, Integer> leftInfo = new HashMap<Integer,Integer>();
		HashMap<Integer, Integer> rightInfo = new HashMap<Integer,Integer>();
		
		Iterator<String> it1 = visitedLeft.iterator();
		while (it1.hasNext()) {
			String current = it1.next();
			int degree = this.getEmbedding().get(current).size();
			if(leftInfo.containsKey(degree)) {
				int previousValue = leftInfo.get(degree);
				leftInfo.replace(degree, previousValue+1);
			} else {
				leftInfo.put(degree, 1);
			}
		}
		
		Iterator<String> it2 = visitedRight.iterator();
		while (it2.hasNext()) {
			String current = it2.next();
			int degree = this.getEmbedding().get(current).size();
			if(rightInfo.containsKey(degree)) {
				int previousValue = rightInfo.get(degree);
				rightInfo.replace(degree, previousValue+1);
			} else {
				rightInfo.put(degree, 1);
			}
		}
		
		//go through and print out various pieces of information
		System.out.println();
		System.out.println("Total nodes: " + this.getEmbedding().size());
		System.out.println("Number of nodes in left group: " + visitedLeft.size());
		System.out.println("Number of nodes in right group: " + visitedRight.size());
		
		// display the number of nodes with each degree from left
		Iterator<Entry<Integer, Integer>> leftIterator = leftInfo.entrySet().iterator();
		while (leftIterator.hasNext()) {
			HashMap.Entry<Integer, Integer> nextNode = (Map.Entry<Integer, Integer>) leftIterator
					.next();
			System.out.println("Left contains " + nextNode.getValue() + " nodes of degree: " + nextNode.getKey());
		}
		
		// display the number of nodes with each degree from left
		Iterator<Entry<Integer, Integer>> rightIterator = rightInfo.entrySet().iterator();
		while (rightIterator.hasNext()) {
			HashMap.Entry<Integer, Integer> nextNode = (Map.Entry<Integer, Integer>) rightIterator
					.next();
			System.out.println("Right contains " + nextNode.getValue() + " nodes of degree: " + nextNode.getKey());
		}
		
		Facetracer.getFaces(this.doubleCover());
		
	}
}
