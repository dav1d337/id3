package de.uniba.cogsys.id3;

/**
* Represents a single node of a {@code DecisionTree}
 */
public class DecisionNode {
	
	public int attribute; // id
	public DecisionNode[] nodes; // list of child nodes
	public String[] attributeValues; // values for the attribute

	public boolean isClassNode;
	public String className; // name if its a ClassNode (node corresponding to the target class)
}