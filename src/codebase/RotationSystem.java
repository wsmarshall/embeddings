package codebase;

import java.util.Collections;
import java.util.ArrayList;

//this class mostly deprecated, use Facetracer instead for Heffter-Edmonds face tracing
public class RotationSystem {
	int[][] edges;
	boolean bipartite;
	// for the actual list of edges, information implicitly encoded by row-column
	// access
	ArrayList<ArrayList<Integer>> rotSysList = new ArrayList<ArrayList<Integer>>();
	ArrayList<ArrayList<Integer>> testList = new ArrayList<ArrayList<Integer>>();
	ArrayList<ArrayList<Integer>> nextRotSysList = new ArrayList<ArrayList<Integer>>();
	int permuteCount = 0;

	// how many permutations are there of this rotation System?
	int permutationCeiling = 1;
	int currPermutation = 1;
	ArrayList<Integer> subarraySizeTracker = new ArrayList<Integer>();// for size of subarrays
	ArrayList<Integer> permutationModTable = new ArrayList<Integer>();
	ArrayList<Integer> permutationModTracker = new ArrayList<Integer>();

	public RotationSystem(Graph g) {
		// get adj matrix from 'source' graph g
		edges = g.getEdgeAdjMatrix();
//		for(int i = 0; i < edges.length; i ++) {
//			for (int j = 0; j < edges.length; j++) {
//				//System.out.println("i:" + i + " j: " + j + " edges[i][j]: " + edges[i][j]);
//			}
//		}
		initialize(rotSysList, testList, nextRotSysList);

		// initialize size of each subarray
		for (int i = 0; i < nextRotSysList.size(); i++) {
			subarraySizeTracker.add(nextRotSysList.get(i).size() - 1);
		}

		// initialize the maximum number of permutations
		for (int i = 0; i < subarraySizeTracker.size(); i++) {
			permutationCeiling *= this.factorial(subarraySizeTracker.get(i));
		}
		//System.out.println("for K5 testing, number of permutations should be 7,776; they are: " + permutationCeiling);

		// for modulos for permuting through possible rotation //Systems
		// for keeping track of how often each subarray "ticks up" when permuting
		// through rotation //Systems
		int temp = 1;
		for (int i = 0; i < subarraySizeTracker.size() - 1; i++) {// skip last entry
			for (int j = i + 1; j < subarraySizeTracker.size(); j++) {
				temp *= this.factorial(subarraySizeTracker.get(j));
			}
			permutationModTracker.add(temp);
			temp = 1;
		}
		System.out.println(permutationModTracker.toString());
		// permutationModTracker.add(permutationCeiling);

		// used for testing for end of permutation cycling
		for (int i = 0; i < testList.size(); i++) {
			Collections.reverse(testList.get(i).subList(1, testList.get(i).size()));
		}
		// sort the edge lists by increasing order
		for (int i = 0; i < nextRotSysList.size(); i++) {
			(nextRotSysList.get(i)).sort(null);// null meaning element's natural ordering should be used
		}
//		System.out.println("rotSysList: " + rotSysList.toString());
//		System.out.println("nextRotSysList : " + nextRotSysList.toString());
//		System.out.println("testList: " + testList.toString());

		//not used TODO delete?
		this.bipartite = g.getBipartite();
	}

	public int getFaces(ArrayList<ArrayList<Integer>> rotSysList) {
		int numFaces = 0;
		Pair<String, String> currEdge;

		// for holding the faces as they're traced
		ArrayList<Face> faces = new ArrayList<Face>();

		ArrayList<Pair<String, String>> cycle = new ArrayList<Pair<String, String>>();
		// number of arrays in the rotation //System
		// Integer rotLen = rotation//SystemList.size();

		// face tracing algorithm for a rotation //System
		for (int i = 0; i < rotSysList.size(); i++) {
			for (int j = 0; j < rotSysList.get(i).size(); j++) {
				String currRow = Integer.toString(i);
				String currCol = Integer.toString(j);
				while (true) {// get the next face edge
					currEdge = new Pair<String, String>(currRow, Integer.toString(rotSysList.get(Integer.parseInt(currRow)).get(Integer.parseInt(currCol))));
					if (!(includesEdge(cycle, currEdge))) {
						cycle.add(currEdge);
						System.out.println("edge added: " + currEdge.toString1());
					} else {
						System.out.println("inside getFaces else");
						//TODO refactor this? necessary?
						Face f = new Face(cycle);
						System.out.println("cycle: " + cycle.toString());
						if (!includesFace(faces, f)) {
							System.out.println("face added: " + f.toString());
							faces.add(f);
							numFaces++;
						}
						cycle.clear();
						break;// breaks out of the while loop
					}

					// //System.out.println("current row: " + currRow + " current col: " + currCol);
					String temp1 = currEdge.getVal2();
					ArrayList<Integer> temp2 = rotSysList.get(Integer.parseInt(temp1));
					// //System.out.println( "(1) next row: " + temp1 + " next col: " +
					// ((temp2.indexOf(currRow) + 1) % temp2.size()));
					currCol = Integer.toString((temp2.indexOf(Integer.parseInt(currRow)) + 1) % temp2.size());
					currRow = temp1;
					// //System.out.println("(2) next row: " + currRow + " next col: " + currCol);

//					
//					int nextColIndex = (int)(rotSysList.get(currRow).get((currCol+1)%(rotSysList.get(i).size())));
//					
//					currEdge = new Pair<Integer, Integer>(nextColIndex, currRow);
//					currRow = (int) currEdge.getVal1();
//					currCol = (int) currEdge.getVal2();
				}
			}
		}
		System.out.println("number of faces: " + numFaces);
		return numFaces;
	}

	public ArrayList<ArrayList<Integer>> getNext() {
		// last array always gets permuted, this is the 'least significant bit'
		permuteArray(nextRotSysList.get(nextRotSysList.size() - 1), nextRotSysList.size() - 1);
		for (int i = 0; i < nextRotSysList.size() - 1; i++) {
			if ((currPermutation % permutationModTracker.get(i)) == 0) {
				permuteArray(nextRotSysList.get(i), i);
			}
		}
		this.currPermutation++;
		System.out.println("current list:" + this.nextRotSysList);
		return this.nextRotSysList;
	}

	public void permuteArray(ArrayList<Integer> arraylist, int position) {// get next lexicographic order array
		// cited: https://stemhash.com/efficient-permutations-in-lexicographic-order/
		// will skip first entry always

		// check if it's the 'last' in the permutation cycle
		if (arraylist.equals(testList.get(position))) {
			arraylist.sort(null);
		} else {
			for (int i = arraylist.size() - 1; i > 0; i--) {
				if (i + 1 <= arraylist.size() - 1 && arraylist.get(i) < arraylist.get(i + 1)) {
					// System.out.println("inside else");
					for (int j = arraylist.size() - 1; j > 0; j--) {
						if (arraylist.get(j) > arraylist.get(i)) {
							Collections.swap(arraylist, i, j);
							Collections.reverse(arraylist.subList(i + 1, testList.get(i).size()));
							return;
						}
					}
				}
			}
		}
	}

//		for (int i = (this.nextRotSysList.size() - 1); i >= 0; i--) {
//			for (int j = this.nextRotSysList.get(i).size() - 1; j > 0; j--) {
//				// skip the first entry to account for cyclic permutations
//				if ((j + 1) < this.nextRotSysList.get(i).size()
//						&& this.nextRotSysList.get(i).get(j) < this.rotSysList.get(i).get(j + 1)) {
//					// if the array entry S[j] < S[j+1]
//					for (int k = this.nextRotSysList.get(i).size() - 1; k > 0; k--) {
//						if (this.nextRotSysList.get(i).get(j) < this.nextRotSysList.get(i).get(k)) {
//							Collections.swap(nextRotSysList.get(i), j, k);
//							// nextRotation//SystemIter should be the next lexicographic permutation
//							return nextRotSysList;
//						} else {
//							continue;
//						}
//					}
//				}
//			}
//		}
//		return nextRotSysList;
//	}

	public boolean hasNext() {// if no next, this returns false
		boolean hasNext = true;
		// checks to see if 'next' list is the same as the known 'end' of the
		// permutation cycle
		// which is the [1, n, n-1,..., 2] permutation
		// System.out.println("next: " + nextRotSysList.toString());
		// System.out.println("final: " + testList.toString());
		if (nextRotSysList.equals(testList)) {
			hasNext = false;
		}
		return hasNext;
	}

	public Pair<Integer, Integer> getSwap(Pair<Integer, Integer> p) {
		// when thinking about A x B,
		// this is the "B" which takes in (u,v) and returns (v,u)
		return new Pair<Integer, Integer>(p.getVal2(), p.getVal1());
	}

	private void initialize(ArrayList<ArrayList<Integer>> rotSysList, ArrayList<ArrayList<Integer>> testList,
			ArrayList<ArrayList<Integer>> nextRotSysList) {
		// initializes rotation system list
		for (int i = 0; i < this.edges.length; i++) {
			rotSysList.add(new ArrayList<Integer>());
			testList.add(new ArrayList<Integer>());
			nextRotSysList.add(new ArrayList<Integer>());
			for (int j = 0; j < this.edges.length; j++) {// square 2d array
				if (this.edges[i][j] == 1) {
					// System.out.println("inside forloop, i:" + i + " j: " + j);
					rotSysList.get(i).add(j);
					testList.get(i).add(j);
					nextRotSysList.get(i).add(j);
					// System.out.println("inside forloop, rotSysList: " + rotSysList.toString());
				}
//				//unnecessary
//				else {
//					rotSysList.get(i).add(-1);
//					
//				}
			}
		}
	}

	private boolean includesEdge(ArrayList<Pair<String, String>> list, Pair<String, String> edge) {
		boolean contains = false;
		
		for (int i = 0; i < list.size(); i++) {
			if (edge.getVal1() == list.get(i).getVal1()) {
				System.out.println("Inside includesEdge first If");
				if (edge.getVal2() == list.get(i).getVal2()) {
					System.out.println("Inside includesEdge second If");
					return true;
				}
			}
		}

		return contains;
	}

	private boolean includesFace(ArrayList<Face> faces, Face f) {
		boolean contains = false;

		String id = f.getEdgeString();
		System.out.println("id: " + id);
		for (int i = 0; i < faces.size(); i++) {
			System.out.println(id + " vs. " + faces.get(i).getEdgeString());
			if (id.equals(faces.get(i).getEdgeString())) {
				return true;
			}
		}

		return contains;
	}

	public ArrayList<ArrayList<Integer>> getEdges() {
		return this.nextRotSysList;
	}
	
	//quick utility function
	public int factorial(int n) {
		int current = 1;
		for (int i = n; i > 1; i--) {
			current *= i;
		}
		return current;
	}
}