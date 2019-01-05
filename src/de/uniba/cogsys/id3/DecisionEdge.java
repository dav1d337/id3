package de.uniba.cogsys.id3;

/**
 * Custom Class to represent the Edges between the Decision Vertex
 * @author Nicolas Morel
 *
 */
public class DecisionEdge {
	// Name of the attributeValue linking two Nodes
	private String attributeValue;
	
	public DecisionEdge (String attributeValue) {
		this.attributeValue = attributeValue;
	}
	
	public String getAttributeValue() {
		return this.attributeValue;
	}
	
}
