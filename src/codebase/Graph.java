package codebase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class Graph {
	int tNodes; // total nodes, only here to test K_5
	int leftNodes;
	int rightNodes;

	int[][] edges; // an adjacency matrix for edges, 1 for edge 0 for no edge
	// for directed graphs, +1 for incoming edge, -1 for outgoing edge?
	// no directed graphs for now, will refactor if have to

	Integer[][] edgeRotSysList; // "list" of "lists" for rotation systems input
	// IE a rotation system initialization
	boolean rotationSystemInit = false;

	// too similar to nodeList? redundant?
	ArrayList<ArrayList<Integer>> edgeArrayList;

	// list for helping initialize the hashmap edgeNodeList
	ArrayList<Node> nodeList = new ArrayList<Node>();

	// an embedding which initializes a particular graph
	private Embedding e = new Embedding();
	// the edges list used for gluing and info tracking operations
	private HashMap<String, ArrayList<String>> edgeNodeList = new HashMap<String, ArrayList<String>>();
	// for tracking the edges which have a half twist
	private HashSet<Pair<String, String>> type1Edges = new HashSet<Pair<String, String>>();

	// for tracking if this embedding/rotation is orientable or not
	private boolean orientable = false;

	int numEdges;// should be product of leftNodes and rightNodes for complete bipartite
	// OR sum from 1 to (tNodes-1) for K_5 test case

	boolean bipartite = false; // just here to differentiate for test cases

	public Graph(Integer[][] edgeList, int numOfEdges) {
		tNodes = edgeList.length; // since every node will have an entry
		edges = new int[tNodes][tNodes];// java initializes arrays to 0
		for (int i = 0; i < edgeList.length; i++) {
			for (int j = 0; j < edgeList[i].length; j++) {
				edges[i][edgeList[i][j]] = 1; // initialize the edge adjacency matrix
			}
		}
		numEdges = numOfEdges; // gaussian formula for sum of integers from 1 to (nodes-1)
		// since # edges for each node capped (no self edges so far)

		// want a 'handle' on the rotation system exactly
		this.edgeRotSysList = edgeList;
		// set boolean flag
		this.rotationSystemInit = true;

		// initialize the hashmap that is base for operations
		this.initializeNodeList();

		this.edgeArrayList = new ArrayList<ArrayList<Integer>>();

		for (int i = 0; i < this.edgeRotSysList.length; i++) {
			edgeArrayList.add(new ArrayList<Integer>(Arrays.asList(this.edgeRotSysList[i])));
		}

		// checks hashmap of edges
//		Iterator<Entry<String, ArrayList<String>> > nodeIterator = edgeNodeList.entrySet().iterator(); 
//		//loops through all nodes
//		while(nodeIterator.hasNext()) {
//			Map.Entry<String, ArrayList<String>> nextNode = (Map.Entry<String, ArrayList<String>>) nodeIterator.next();
//			System.out.println("nextNode key: " + nextNode.getKey() + " nextNode value: " + nextNode.getValue());
//		}
//		
//		//check adjacency matrix
//		for (int i = 0; i < edges.length; i++) {
//			for(int j = 0; j < edges[i].length; j++) {
//				System.out.print(edges[i][j]); //initialize the edge adjacency matrix
//			}
//			System.out.println();
//		}
		this.e = new Embedding(edgeList);
	}

	public Graph(HashMap<String, ArrayList<String>> graphHashMap) {
		// graphHashMap is a defined rotation system for a specific graph
		// with node: neighborsArrayList key/value structuring
		this.rotationSystemInit = true;// so get faces method uses Facetracer (hashmap)
		// rather than rotationSystem (arrayList)

		// for counting number of edges
		ArrayList<String> edges = new ArrayList<String>();

		tNodes = graphHashMap.keySet().size();

		// to iterate through the rotation system given
		Iterator<Entry<String, ArrayList<String>>> nodeIterator = graphHashMap.entrySet().iterator();

		// add each entry to the this.edgeNodeList HashMap, which corresponds to a
		// rotationSystem
		int testNumEdges = 0;

		while (nodeIterator.hasNext()) {
			String tempKey;
			ArrayList<String> tempVal = new ArrayList<String>();

			HashMap.Entry<String, ArrayList<String>> nextNode = (Map.Entry<String, ArrayList<String>>) nodeIterator
					.next();
			tempKey = nextNode.getKey();
			ArrayList<String> currentVal = nextNode.getValue();

			testNumEdges += currentVal.size();

			this.edgeNodeList.put(tempKey, currentVal);

		}

		// test out edge counting
		// System.out.println("testNumEdges: " + testNumEdges + " testNumEdges/2: " +
		// testNumEdges/2);
		this.numEdges = testNumEdges / 2;

//		if(nonOrientable) {
//			//TODO numEdges/2?
//		}
		this.e = new Embedding(graphHashMap);

	}

	public Graph(Embedding embedding) {
		// graphHashMap is a defined rotation system for a specific graph
		// with node: neighborsArrayList key/value structuring
		this.rotationSystemInit = true;// so get faces method uses Facetracer (hashmap)
		// rather than rotationSystem (arrayList)

		HashMap<String, ArrayList<String>> graphHashMap = embedding.getEmbedding();
		this.e = embedding;

		// System.out.println("graph constructor check");
		// this.e.printString();

		// for counting number of edges
		ArrayList<String> edges = new ArrayList<String>();

		tNodes = graphHashMap.keySet().size();

		// to iterate through the rotation system given
		Iterator<Entry<String, ArrayList<String>>> nodeIterator = graphHashMap.entrySet().iterator();

		// add each entry to the this.edgeNodeList HashMap, which corresponds to a
		// rotationSystem
		int testNumEdges = 0;

		while (nodeIterator.hasNext()) {
			String tempKey;
			ArrayList<String> tempVal = new ArrayList<String>();

			HashMap.Entry<String, ArrayList<String>> nextNode = (Map.Entry<String, ArrayList<String>>) nodeIterator
					.next();
			tempKey = nextNode.getKey();
			ArrayList<String> currentVal = nextNode.getValue();

			testNumEdges += currentVal.size();

			this.edgeNodeList.put(tempKey, currentVal);

		}

		// test out edge counting
		// System.out.println("testNumEdges: " + testNumEdges + " testNumEdges/2: " +
		// testNumEdges/2);
		this.numEdges = testNumEdges / 2;

//		if(nonOrientable) {
//			//TODO numEdges/2?
//		}
		this.type1Edges = embedding.getT1edges();
		// System.out.println("embedding: ");
		// embedding.printString();

//		if (embedding.isOrientable()) {
//			this.orientable = true;
//		} // else is implicit since orientability initialized to false

	}

	public int getGenus() {// helper method for genus distr. calc'n.
		int genus = 0;// default value
		int numFaces = 0;
		Facetracer ft = new Facetracer();
		// get the number of faces first
		if (rotationSystemInit) {
			// System.out.println("rotation system init");
			if (this.e.getOrientable()) {
				numFaces = ft.getFaces(this.edgeNodeList);
			} else {
				// e.printString();
				numFaces = ft.getFaces(this.e.getEmbedding(true));
			}
		} else {
			RotationSystem r = new RotationSystem(this);
			numFaces = r.getFaces(r.getEdges());
		}
		// this and below asserts make sure remainders from division is never a problem
		// assert numEdges % 2 == 0 : "even number of edges, no problem here";

		if (!this.orientable) {// this embedding/rotation system is not orientable
			Integer numNodes = this.tNodes;
			System.out.println("NONORIENTABLE");
			System.out.println("tNodes: " + tNodes + ", numEdges: " + numEdges + ", numFaces: " + numFaces);
			// NB this is a NONORIENTABLE genus
			genus = (2 - numNodes + numEdges - numFaces);
		} else {
			System.out.println("ORIENTABLE:");
			System.out.println("tNodes: " + tNodes + " numEdges: " + numEdges + " numFaces: " + numFaces);
//			genus =(int) (1 - Math.ceil((tNodes - numEdges + numFaces)/ 2.0));
			genus = (int) (1 - ((tNodes - numEdges + numFaces) / 2));
			// System.out.println("genus: " + genus);
		}
		return genus;

	}

	public int getGenus(int faces) {// helper method for genus distr. calc'n.
		int genus = 0;// default value
		int numFaces = faces;

		// this and below asserts make sure remainders from division is never a problem
		// assert numEdges % 2 == 0 : "even number of edges, no problem here";

		if (bipartite) {
			Integer numNodes = leftNodes + rightNodes;
			// assert numNodes % 2 == 0 : "even number of nodes, no problem here";
			genus = (1 - (numNodes - numEdges + numFaces) / 2);
		} else {
			// System.out.println("tNodes: " + tNodes + " numEdges: " + numEdges + "
			// numFaces: " + numFaces);
			genus = (1 - (tNodes - numEdges + numFaces) / 2);
			// System.out.println("genus: " + genus);
		}
		return genus;

	}

	public ArrayList<Integer> getGenusDistribution() {// permutes through all possible rotation systems,
		// calculates genus of each, and returns distribution as arrayList
		// w/ position/key meaning genus, value: how many rotation systems give that
		// genus
		ArrayList<Integer> genusDistribution = new ArrayList<Integer>();

		RotationSystem r = new RotationSystem(this);
		// initial genus for the "first" rotation system
		System.out.println("first list: " + r.getEdges());
		genusDistribution.add(getGenus(r.getFaces(r.getEdges())));
		while (r.hasNext()) {
			genusDistribution.add(getGenus(r.getFaces(r.getNext())));
		}

		// print to console the distribution?
		return genusDistribution;
	}

	public boolean getBipartite() {
		return this.bipartite;
	}

	public int[][] getEdgeAdjMatrix() {
		return this.edges;
	}

	public ArrayList<ArrayList<Integer>> getEdgeArrayList() {
		return this.edgeArrayList;
	}

	private void initializeNodeList() {
		// initialize the nodelist and edgelist hashmap

		// edgeRotSysList is what we want
		for (int i = 0; i < edgeRotSysList.length; i++) {
			// System.out.println("i: " + i);
			edgeNodeList.put(Integer.toString(i), new ArrayList<String>());
			for (int j = 0; j < edgeRotSysList[i].length; j++) {
				// System.out.println("j: " + j);
				String e = Integer.toString(edgeRotSysList[i][j]);
				// System.out.println("e: " + e);
				edgeNodeList.get(Integer.toString(i)).add(e);
			}
		}
	}

	private void setLabelNodeList(String s) {
		for (int i = 0; i < this.nodeList.size(); i++) {
			nodeList.get(i).setID(s + i);
		}
	}

	public void setLabelEdgeNodeList(String s) {
		HashMap<String, ArrayList<String>> newHashMap = new HashMap<String, ArrayList<String>>();
		for (Map.Entry<String, ArrayList<String>> set : this.edgeNodeList.entrySet()) {
			String oldKey = set.getKey();
			String newKey = s + oldKey;
			ArrayList<String> oldList = set.getValue();
			ArrayList<String> newList = new ArrayList<String>();
			for (int i = 0; i < oldList.size(); i++) {
				newList.add(s + oldList.get(i));
			}

			newHashMap.put(newKey, newList);
		}
		this.edgeNodeList = newHashMap;
	}

	public ArrayList<Node> getNodeList() {
		return this.nodeList;
	}

	public HashMap<String, ArrayList<String>> getEdgeNodeList() {
		return this.edgeNodeList;
	}

	public Integer[][] getRotSysList() {
		return this.edgeRotSysList;
	}

	// 'diamond sum' operation for two graphs
	// s and t are the two nodes which will be excised
	// from this, and g2, respectively
	// u, v are two nodes which will be glued together from s and t's adjacent
	// nodes, respectively
	public Embedding dsum(Graph g2, String s, String t, String u, String v) {
		// clean each of the s, t vertices
		// System.out.println("this is: " + this.getEmbedding());
		this.getEmbedding().cleanNode(s, this.getEmbedding());
		g2.getEmbedding().cleanNode(t, g2.getEmbedding());

		// relabel nodes from each graph
		s = 0 + s;
		u = 0 + u;
		this.setLabelEdgeNodeList("0");
		this.relabelt1edges("0");
		t = 1 + t;
		v = 1 + v;
		g2.setLabelEdgeNodeList("1");
		g2.relabelt1edges("1");

		HashMap<String, ArrayList<String>> dsummed = new HashMap<String, ArrayList<String>>();

		// for a convenient handle on this graph's edgeNodeList (HashMap)
		HashMap<String, ArrayList<String>> eNodeList = this.getEdgeNodeList();
		// similarly for other graph's edgeNodeList (HashMap)
		HashMap<String, ArrayList<String>> oNodeList = g2.getEdgeNodeList();

		int neighbors = eNodeList.get(s).size();
//		System.out.println("neighbors, should be 3: " + neighbors);
		// check degrees on each of the eliminated nodes
		if (neighbors != oNodeList.get(t).size()) {
			System.out.println("UNSUCCESSFUL DIAMOND SUM: NODES OF UNEQUAL DEGREE");
			return new Embedding(dsummed);
		}

		// lists of adjacent nodes of each of s, t
		// for relabeling purposes
		ArrayList<String> S = eNodeList.get(s); // from this graph, g1
		ArrayList<String> T = oNodeList.get(t); // from 'other' graph, g2

		// for holding the new combined glued together list
		HashMap<String, ArrayList<String>> ST = new HashMap<String, ArrayList<String>>();

		// for mapping which nodes have been glued together
		// to their new labels
		// all even parity indices (KIM zero indexed) are previous labels
		// the next indices are the new labels (odd parity)
		// means adjacentGlued will be even in size
		ArrayList<String> adjacentGlued = new ArrayList<String>();

		// neighbors checked to be equal for s, t nodes already
		int forwardCount = 0;
		int backwardCount = 0;

		int uIndex = S.indexOf(u);
		int vIndex = T.indexOf(v);

		// add to collection (HashMap ST) of glued nodes
		while (true) {
			if (forwardCount >= neighbors) {
				break;
			}
			ArrayList<String> current = new ArrayList<String>();
			// add all neighbors of s, going clockwise ("forward") from u
			int forward = (uIndex + forwardCount) % neighbors;

			// current 'left' node which is being 'glued'
			String g1node = S.get(forward);

			// add all neighbors of t, going counterclockwise ("backward") from v
			int back = vIndex - backwardCount;
			if (back < 0) {// accounts for "going backward" past the start of an ArrayList
				back = neighbors + back;
			}
			// current 'right' node which is being 'glued'
			String g2node = T.get(back);

			// maintains clockwise/counterclockwise orientation around glued points for
			// rotation system

			// int indexOfS = current.indexOf(s);

			// adds left graph's node's neighbors to list
			if (eNodeList.get(g1node).contains(s)) {
				// puts s at front of neighbor list
				ArrayList<String> temp = frontloadExcisionPoint(s, eNodeList.get(g1node));
				current.addAll(temp);
			}
			// adds right graph's node's neighbors to list
			if (oNodeList.get(g2node).contains(t)) {
				// System.out.println("else if branch, endless loop");
				ArrayList<String> temp = frontloadExcisionPoint(t, oNodeList.get(g2node));
				current.addAll(temp);
			}
			// remove the excised points from the neighbor lists
			current.remove(s);
			current.remove(t);

			// new glued together node
			String g1g2 = g1node + g2node;

			// keep track of old label -> new label mapping
			// NB they're all next to each other, 'clumped'
			adjacentGlued.add(g1node);
			adjacentGlued.add(g1g2);

			//System.out.println("Graph 436 adjacentGlued: " + adjacentGlued);

			// relabels this graph's t1 edges
			this.getEmbedding().t1containsNodeRelabel(g1node, g1g2);
			adjacentGlued.add(g2node);
			adjacentGlued.add(g1g2);
			// relabels the other graph's t1 edges
			g2.getEmbedding().t1containsNodeRelabel(g2node, g1g2);

			ST.put(g1g2, current);
			forwardCount++;
			backwardCount++;

		} // ST now holds all glued together nodes, which are pairwise matched neighbors
			// of s and t
			// (recall that nodes s, t have been excised by the start of the diamond sum
			// operation)

		// test what is in ST currently
		// System.out.println();
		//System.out.println("Graph 456 ST status test: " + ST);

		// ST's lists need to be relabelled
		Iterator<Entry<String, ArrayList<String>>> STiterator = ST.entrySet().iterator();
		while (STiterator.hasNext()) {

			HashMap.Entry<String, ArrayList<String>> STnextNode = (Map.Entry<String, ArrayList<String>>) STiterator
					.next();
			//replacement list for the ST hashmap
			ArrayList<String> newList = new ArrayList<String>();
			//key, value handles for current node
			String tempKey = STnextNode.getKey();
			ArrayList<String> STcurrentNeighbors = STnextNode.getValue();
			
			for (int i = 0; i < STcurrentNeighbors.size(); i++) {
				String current = STcurrentNeighbors.get(i);
				if (adjacentGlued.contains(current)) {//this node has been glued and shoul be relabelled
					int refIndex = adjacentGlued.indexOf(current);
					newList.add(adjacentGlued.get(refIndex+1));
				} else {//no relabelling for this node needed
					newList.add(current);
				}
			}
			//put the new, relabelled neighbor list into the ST hashmap
			ST.replace(tempKey, newList);
		}

		Iterator<Entry<String, ArrayList<String>>> eNodeIterator = eNodeList.entrySet().iterator();
		Iterator<Entry<String, ArrayList<String>>> oNodeIterator = oNodeList.entrySet().iterator();

		// this while loop adds the glued nodes
		while (eNodeIterator.hasNext()) {
			HashMap.Entry<String, ArrayList<String>> eNextNode = (Map.Entry<String, ArrayList<String>>) eNodeIterator
					.next();

			// this node is not the excised node, nor is it one of the glued nodes
			if (!eNextNode.getKey().equals(s) && !adjacentGlued.contains(eNextNode.getKey())) {
				ArrayList<String> check = eNextNode.getValue();
				// does this node or any of its neighbors need to be relabelled?
				boolean relabelFlag = false;
				for (int i = 0; i < check.size(); i++) {
					if (adjacentGlued.contains(check.get(i))) {
						relabelFlag = true;
						break;
					}
				}
				// need to relabel some of this node's neighbors
				ArrayList<String> temp = new ArrayList<String>();
				if (relabelFlag) {
					for (int i = 0; i < check.size(); i++) {
						String contained = check.get(i);
						if (adjacentGlued.contains(contained)) {
							int index = adjacentGlued.indexOf(contained);
							temp.add(adjacentGlued.get(index + 1));

						} else {
							temp.add(contained);
						}

					}
					// node with relabelled outgoing edges added now to dsummed
					// System.out.println("Graph 506 adding: " + eNextNode.getKey() + ", " + temp);
					dsummed.put(eNextNode.getKey(), temp);
				} else { // no relabeling needed, add node to combined object
					// System.out.println("Graph 509 adding: " + eNextNode.getKey() + ", " +
					// eNextNode.getValue());
					dsummed.put(eNextNode.getKey(), eNextNode.getValue());
				}
			} else if (adjacentGlued.contains(eNextNode.getKey())) {
				// this node IS one of the glued nodes
				String tempId = "";

				// where is the string id
				// System.out.println("adjacentGlued: " + adjacentGlued);
				// System.out.println("key: " + eNextNode.getKey());
				int tempIdIndex = adjacentGlued.indexOf(eNextNode.getKey());

				// create the glued node key/string ID
				//System.out.println("Graph 521 tempIdIndex: " + tempIdIndex);
				tempId += adjacentGlued.get(tempIdIndex + 1);

				ArrayList<String> tempList = new ArrayList<String>();
				for (int i = 0; i < 4; i++) {
					tempList.add(adjacentGlued.get(tempIdIndex + i));
				}
//				System.out.println("Graph 528 tempId, tempList: " + tempId + ": " + tempList);
				// adjacentGlued.removeAll(tempList);
//				System.out.println("Graph 530 adding glued node: " + tempId + ", " + ST.get(tempId));

				// add it to the dsummed hashMap
				dsummed.put(tempId, ST.get(tempId));
			}
		}

		// this one does not add the glued nodes
		// but adds (and relabels) the other (right) graph's nodes
		while (oNodeIterator.hasNext()) {
			HashMap.Entry<String, ArrayList<String>> oNextNode = (Map.Entry<String, ArrayList<String>>) oNodeIterator
					.next();

			// this node is not the excised node, nor is it one of the glued nodes
			if (!oNextNode.getKey().equals(t) && !adjacentGlued.contains(oNextNode.getKey())) {
				ArrayList<String> check = oNextNode.getValue();
				boolean relabelFlag = false;
				for (int i = 0; i < check.size(); i++) {
					if (T.contains(check.get(i))) {
						relabelFlag = true;
						break;
					}
				}
				// need to relabel some of this node's neighbors
				ArrayList<String> temp = new ArrayList<String>();
				if (relabelFlag) {
					for (int i = 0; i < check.size(); i++) {
						String contained = check.get(i);
						if (adjacentGlued.contains(contained)) {
							int index = adjacentGlued.indexOf(contained);
							temp.add(adjacentGlued.get(index + 1));

						} else {
							temp.add(contained);
						}
					}
					// node with relabelled outgoing edges added now to dsummed
					dsummed.put(oNextNode.getKey(), temp);
				} else { // no relabeling needed, add node to combined object
					dsummed.put(oNextNode.getKey(), oNextNode.getValue());
				}
			}
		}

		// make sure all t1 edges relabelled correctly
		for (int i = 0; i < adjacentGlued.size(); i += 4) {
			// System.out.println("this edge relabelling: " + adjacentGlued.get(i) + ", " +
			// adjacentGlued.get(i + 1));
			this.getEmbedding().t1containsNodeRelabel(adjacentGlued.get(i), adjacentGlued.get(i + 1));
			// System.out.println("other edge relabelling: " + adjacentGlued.get(i+2) + ", "
			// + adjacentGlued.get(i + 3));
			g2.getEmbedding().t1containsNodeRelabel(adjacentGlued.get(i + 2), adjacentGlued.get(i + 3));
		}

		// collect all the t1 edges
		HashSet<Pair<String, String>> t1s = new HashSet<Pair<String, String>>();

		Embedding result = new Embedding(dsummed, t1s);
		// add all t1 edges from this graph to the result
		Iterator<Pair<String, String>> it = this.type1Edges.iterator();
		while (it.hasNext()) {
			Pair<String, String> p = it.next();
			if (result.type1EdgesSetContains(p)) {
				continue;
			} else {
				result.addT1edge(p);
			}
		}

		// add all t1 edges from the other graph to the result
		Iterator<Pair<String, String>> it2 = g2.type1Edges.iterator();
		while (it2.hasNext()) {
			Pair<String, String> p = it2.next();
			if (result.type1EdgesSetContains(p)) {
				continue;
			} else {
				result.addT1edge(p);
			}
		}

		// now that all t1 edges are added to the result
		// we need to run a quick orientability check/adjustment
		// result.isOrientable();
//		System.out.println("Graph 625 graph.dsum result: " + result);
		return result;
	}

	// for putting a node which will be excised at the front of an ArrayList of
	// nodes
	// note that nodes are unique in the list (no multiedges in the graphs)
	private ArrayList<String> frontloadExcisionPoint(String s, ArrayList<String> input) {
		ArrayList<String> output = new ArrayList<String>();
		int index = -1;
		s = s.trim();
		if (input.contains(s)) {
			index = input.indexOf(s);
		}
		if (index < 0) {
			System.out.println("ERROR: list " + input.toString() + " does NOT contain " + s);
		}

		output.addAll(input);
		String t = "";
		// System.out.println("string s: " + s + " and list: " + output.toString());
		while (!output.get(0).equals(s)) {
			t = output.remove(0).trim();
			output.add(t);
			// System.out.println("output list: " + output);
		}

		return output;
	}

	public Embedding getEmbedding() {
		return this.e;
	}

	// for relabelling the type1 edges to match in the first step of the dsum
	public void relabelt1edges(String leftRight) {
		Iterator<Pair<String, String>> it = this.getEmbedding().getT1edges().iterator();
		// loops through the hashSet of type 1 edges
		while (it.hasNext()) {
			Pair<String, String> p = it.next();
			// reset the first and second values
			p.setVal1(leftRight + p.getVal1());
			p.setVal2(leftRight + p.getVal2());
			// System.out.println("relabelled t1 edge: " + p.toString());
		}

	}

}
