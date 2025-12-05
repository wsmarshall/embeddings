package codebase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

//the main 'driver' program for everything else
public class Tester {
	public static void main(String[] args) {
		//custom check for Ringel 3,6 edge case
		Embedding ringel36 = FormatConverter.from();
//		System.out.println("tester 14");
		Facetracer.getFaces(ringel36.doubleCover());//isQuad
//		System.out.println("tester 16");
//		Facetracer.getFaces(ringel36.getEmbedding());//isNOTQUAD
		ringel36.bipartiteCheck();
		

				Embedding base36 = new Embedding(BaseCases.NObasek43Array, BaseCases.NOk43t1edges);
				Facetracer.getFaces(base36.doubleCover());
				Embedding other = Constructors.add2tok32n(base36, 1);
				
				String central = "4";
				// need a k(3, 8) - 2 edges
				other.bipartiteCheck();
				System.out.println("other: " + other);
				System.out.println("ringel36: " + ringel36);

//		
				Embedding step1 = ringel36.rawDSum(other, "7", central, "1", "y");
				System.out.print("tester 33");
				step1.bipartiteCheck();
				step1.printString();
		
				// make another 'other'
				other = otherGen();
		
				System.out.print("tester 40");
				Embedding step2 = step1.rawDSum(other, "08", central, "0212", "y");
				step2.bipartiteCheck();
				step2.printString();
				
				// make another 'other'
				other = otherGen();
		
				System.out.print("tester 48");
				Embedding step3 = step2.rawDSum(other, "009", central, "0061112", "y");
				step3.bipartiteCheck();
				step3.printString();
	
				FormatConverter.to(step3, 12, 6);

			}

			static Embedding otherGen() {
				Embedding base36 = new Embedding(BaseCases.NObasek43Array, BaseCases.NOk43t1edges);
				Facetracer.getFaces(base36.doubleCover());
				Embedding other = Constructors.add2tok32n(base36, 1);
				
				return other;
			}


	static void checkNodesAndEdges(Embedding e, int n, int i) {
		boolean nodeCheck = true;
		boolean edgeCheck = true;
		if (e.getEdges() != (((n * (n - 1)) / 2) - i)) {
			System.out.println("fails edge count, number of edges is: " + e.getEdges());
			edgeCheck = false;

		}
		if (e.getNodes() != n) {
			System.out.println("fails node number check, is: " + e.getNodes());
			nodeCheck = false;

		}
		if (!edgeCheck) {
			System.out.println(
					"FAIL ON NUMBER OF EDGES, is: " + e.getEdges() + ", should be: " + (((n * (n - 1)) / 2) - i));
		}
		if (!nodeCheck) {
			System.out.println("FAIL ON NUMBER OF NODES, is: " + e.getNodes() + ", should be: " + n);
		}
		if (nodeCheck && edgeCheck) {
			System.out.println("PASS PASS PASS: ALL SYSTEMS GO GO GO");
		}
	}

	static void minQuadCheckO(int n) {
		int maxT = n - 4;
		boolean check = true;
		int tParity = ((n * (n - 5)) / 2) % 4;
		int startingT = 0;
		Embedding testQuad;
		if (tParity == 1) {
			startingT = 1;
		} else if (tParity == 2) {
			startingT = 2;
		} else if (tParity == 3) {
			startingT = 3;
		}

		ArrayList<Integer> iList = new ArrayList<Integer>();

		for (int i = startingT; i <= maxT; i += 4) {
			Facetracer ft = new Facetracer();
			System.out.println("Tester 33, current tvalue (i): " + i);
			testQuad = Constructors.minQuadO(n, i);
			System.out.println("tester 37, testQuad: " + testQuad);
			System.out.println("###########################################################################");
			System.out.println("testing Q(" + n + ", " + i + ")");
			System.out.println(testQuad.toString());
			System.out.println("line break\n");
			System.out.println("number of nodes: " + testQuad.getNodes());
			System.out.println("number of edges: " + testQuad.getEdges());
			ft.getFaces(testQuad.getEmbedding());// don't need to face trace on the double cover since it's orientable
													// case here
			if (!ft.getMinQuadStats()) {
				System.out.println(Integer.toString(n) + ", " + Integer.toString(i) + " FAILS MINQUADSTATS");
				check = false;
				iList.add(i);
			}
			if (testQuad.getEdges() != (((n * (n - 1)) / 2) - i)) {
				System.out.println("fails edge count, number of edges is: " + testQuad.getEdges());
				check = false;
				if (!iList.contains(i)) {
					iList.add(i);
				}
			}
			if (testQuad.getNodes() != n) {
				System.out.println("fails node number check, is: " + testQuad.getNodes());
				check = false;
				if (!iList.contains(i)) {
					iList.add(i);
				}
			}
			if (!Embedding.nodeExists(testQuad.getEmbedding(), n - 1)) {
				System.out.println("fails node connected to every other node check ");
				check = false;
				if (!iList.contains(i)) {
					iList.add(i);
				}
			}

			if (check) {
				FormatConverter.to(testQuad, n, i);
			}

		}

		if (check) {
			System.out.println();
			System.out.println("OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");
			System.out.println("PASS minquad(" + n + ", " + maxT + ") MEETS REQUIREMENTS");
			System.out.println("OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");
			System.out.println();
		} else {
			System.out.println();
			System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
			System.out.println("FAILS minquad(" + n + ", " + maxT + ") FAIL FAIL FAIL FAIL");
			System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
			System.out.println();
			System.out.println("list of i: " + iList);
		}

	}

	static void minQuadCheck(int n) {
		int maxT = n - 4;
		boolean check = true;
		int tParity = ((n * (n - 5)) / 2) % 2;
		int startingT = 0;
		Embedding testQuad;
		if (tParity == 1) {
			startingT = 1;
		}

		ArrayList<Integer> iList = new ArrayList<Integer>();

		for (int i = startingT; i <= maxT; i += 2) {
			Facetracer ft = new Facetracer();
			// System.out.println("Tester 33, current tvalue (i): " + i);
			testQuad = Constructors.minQuad(n, i);
			// System.out.println("tester 37, testQuad: " + testQuad);
			System.out.println("###########################################################################");
			System.out.println("testing Q(" + n + ", " + i + ")");
			System.out.println("number of nodes: " + testQuad.getNodes());
			System.out.println("number of edges: " + testQuad.getEdges());
			ft.getFaces(testQuad.getEmbedding(true));// since need to face trace on the double cover
			if (!ft.getMinQuadStats()) {
				System.out.println(Integer.toString(n) + ", " + Integer.toString(i) + " FAILS MINQUADSTATS");
				check = false;
				iList.add(i);
			}
			if (testQuad.getEdges() != (((n * (n - 1)) / 2) - i)) {
				System.out.println("fails edge count, number of edges is: " + testQuad.getEdges());
				check = false;
				if (!iList.contains(i)) {
					iList.add(i);
				}
			}
			if (testQuad.getNodes() != n) {
				System.out.println("fails node number check, is: " + testQuad.getNodes());
				check = false;
				if (!iList.contains(i)) {
					iList.add(i);
				}
			}
			if (!Embedding.nodeExists(testQuad.getEmbedding(), n - 1)) {
				System.out.println("fails node connected to every other node check ");
				check = false;
				if (!iList.contains(i)) {
					iList.add(i);
				}
			}
			/*
			 * if(check) { FormatConverter.to(testQuad, n, i); }
			 */
		}

		if (check) {
			System.out.println();
			System.out.println("OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");
			System.out.println("PASS minquad(" + n + ", " + maxT + ") MEETS REQUIREMENTS");
			System.out.println("OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");
			System.out.println();
		} else {
			System.out.println();
			System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
			System.out.println("FAILS minquad(" + n + ", " + maxT + ") FAIL FAIL FAIL FAIL");
			System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
			System.out.println();
			System.out.println("list of i: " + iList);
		}

	}

	static void check(Embedding e) {
		HashMap<String, ArrayList<String>> input = e.getEmbedding();
		String info = "K";
		HashMap<String, String> degrees = new HashMap<String, String>();
		Iterator<Entry<String, ArrayList<String>>> nodeIterator = input.entrySet().iterator();
		while (nodeIterator.hasNext()) {
			HashMap.Entry<String, ArrayList<String>> nextNode = (Map.Entry<String, ArrayList<String>>) nodeIterator
					.next();
			String temp = Integer.toString(nextNode.getValue().size());
			if (!degrees.containsKey(temp)) {
				degrees.put(temp, temp);
			}
		}

		Iterator<Entry<String, String>> nodePutIterator = degrees.entrySet().iterator();
		while (nodePutIterator.hasNext()) {
			HashMap.Entry<String, String> nextPutNode = (Map.Entry<String, String>) nodePutIterator.next();
			String temp = nextPutNode.getValue();
			info += temp;
			info += ",";
		}
		Graph checkG = new Graph(e);
		info += "\n" + "      genus: ";
		info += Integer.toString(checkG.getGenus());
		info += "\n";
		System.out.println("Graph info:" + info);
	}

}
