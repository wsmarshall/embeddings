package codebase;

import java.util.ArrayList;
import java.util.HashMap;

public class Node {
	
	boolean left = true;
	
	//for marking if this node is a 'false vertex'
	//which takes the place of an edge crossing 
	//in a graph drawing
	boolean isCrossing = false;
	
	// for preserving data for potential graph operations
	private String id = "";


	private ArrayList<String> edgeList;


	// overloaded for a default argument for the string identifier
	public Node(Integer label, ArrayList<String> edgeList) {

		this.edgeList = edgeList;
	}

	// constructor containing an argument for the string identifier
	public Node(String s, ArrayList<String> edgeList) {
		// set string identifier
		this.id = s;		
		this.edgeList = edgeList;
	}
	
	public Node (boolean b) {
		left = b;
	}

	public String getID() {
		return this.id;
	}
	
	public void setID(String s) {
		this.id = s;
	}
	
	public boolean getLeft() {
		return this.left;
	}
	
	
	public ArrayList<String> getEdgeList(){
		return this.edgeList;
	}

}