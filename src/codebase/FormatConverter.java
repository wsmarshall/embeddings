package codebase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;

public class FormatConverter {
	// converts adjacency lists from text file formats to
	// Java Integer[][] arrays
	public static Embedding from() {
		Embedding result = null;
		HashMap<String, ArrayList<String>> part1 = new HashMap<String, ArrayList<String>>();
		HashSet<Pair<String, String>> part2 = new HashSet<Pair<String, String>>();
		String nodeLabel = "";

		//on windows tower home setup
		File input = new File("C:\\Users\\John Deere\\Documents\\GitHub\\GE\\src\\codebase\\Input.txt");
		//on macbook away setup
//		File input = new File("/Users/tom/eclipse-workspace/GE/src/codebase/Input.txt");
		try {
			Scanner sc = new Scanner(input);
			// System.out.println("testing format converter");
			while (sc.hasNextLine()) {// reading line by line
				String current = sc.nextLine();
				int indexOfFullStop = current.indexOf("."); // if it's a colon line
				int indexOfDollar = current.indexOf("$");
				if (indexOfFullStop >= 0) {// it's a line with a node and neighbor list
					ArrayList<String> nodeNeighbors = new ArrayList<String>();
					String key = current.substring(0, indexOfFullStop);
					String n = current.substring(indexOfFullStop + 1);
					n = n.trim();
					String[] neighbors = n.split(" ");
					for (int i = 0; i < neighbors.length; i++) {
						nodeNeighbors.add(neighbors[i]);
					}
					System.out.println("key: " + key + ", value: " + nodeNeighbors);
					part1.put(key, nodeNeighbors);
				} else if (indexOfDollar >= 0) {// it's a t1 edge line
					String sub = current.substring(1);
					sub = sub.trim();
					String[] neighbors = sub.split(" ");
					Pair<String, String> p = new Pair<String, String>(neighbors[0], neighbors[1]);
					// System.out.println("pair: " + p.toString());
					part2.add(p);
				} else {// it's a blank newline
					continue;
				}

			}

			result = new Embedding(part1, part2);
			System.out.println("\n" + result.toJavaFormat());
			sc.close();
			return result;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return result;
		}

	}

	// converts adj lists from Java Integer[][] arrays to
	// text file formats
	public static void to(Embedding e, int n, int t) {
		// working absolute file path::== C:\\Users\\John
		// Deere\\Documents\\GitHub\\GE\\src\\codebase\\Output.txt
		String out = "";
		String t1 = "";

		HashMap<String, ArrayList<String>> embedding = e.getEmbedding();
		HashSet<Pair<String, String>> t1edges = e.getT1edges();

		// loop through HashMap, write to string

		Iterator<Entry<String, ArrayList<String>>> nodeIterator = embedding.entrySet().iterator();
		while (nodeIterator.hasNext()) {
			HashMap.Entry<String, ArrayList<String>> nextNode = (Map.Entry<String, ArrayList<String>>) nodeIterator
					.next();
			out += nextNode.getKey() + ". ";
			ArrayList<String> neighbors = nextNode.getValue();
			for (int i = 0; i < neighbors.size(); i++) {
				out += neighbors.get(i) + " ";
			}
			out += " \n";
		}

		// loop through HashSet, write to string
		Iterator<Pair<String, String>> t1Iterator = t1edges.iterator();
		while (t1Iterator.hasNext()) {
			Pair<String, String> nextT1Edge = t1Iterator.next();
			t1 += "$ " + nextT1Edge.getVal1() + " " + nextT1Edge.getVal2() + "\n";
		}
		// write to file
		try {
			// just used to create the new file
			File newFile = new File("D:\\Dropbox\\wms\\A Current Classes Folder\\01 research\\RingelTestResults\\"
					+ "Q(" + n + "_" + t + ").txt");
			if (newFile.createNewFile()) {
				FileWriter myWriter = new FileWriter(
						"D:\\Dropbox\\wms\\A Current Classes Folder\\01 research\\RingelTestResults\\" + "Q(" + n + "_"
								+ t + ").txt");
				myWriter.write(out + "\n" + t1);
				myWriter.close();
			}
		} catch (IOException exception) {
			exception.printStackTrace();
		}

	}

	// converts adj lists from Java Integer[][] arrays to
	// text file formats
	// overloaded
	// public static void to(Integer[][] adj, Integer[][] t1edges) {
	// Embedding e = new Embedding(adj, t1edges);
	// to(e);
	// }
}
