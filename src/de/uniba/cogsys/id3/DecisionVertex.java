package de.uniba.cogsys.id3;

/**
 * Custom class to represent a DecisionNode in the JGraphT library
 * @author Nicolas Morel
 *
 */
public class DecisionVertex {
	// Name which will be print on the node
	private String attributeName;
	// ID for differenciation purposes, with Vertex which the same attributeName
	private int id;
	
	public DecisionVertex (String attributeName, int id) {
		this.attributeName = attributeName;
		this.id = id;
	}
	
	public String getAttributeName() {
		return this.attributeName;
	}
	
	public int getID() {
		return this.id;
	}
}
