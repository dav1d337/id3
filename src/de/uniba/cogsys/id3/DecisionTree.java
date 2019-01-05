package de.uniba.cogsys.id3;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.jgrapht.Graph;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.io.ComponentNameProvider;
import org.jgrapht.io.DOTExporter;
import org.jgrapht.io.ExportException;
import org.jgrapht.io.GraphExporter;

/**
 * Representing a DecisionTree, which is able to print its status on the console and to a .dot File
 */
public class DecisionTree {

	public String[] allAttributes;
	public DecisionNode root = null;
	
	// For printing purposes, allow to build different vertex with the same label
	private int vertexIds = 0;
	
	// Printing Graph representation of the Decision Tree
	public Graph<DecisionVertex, DecisionEdge> graph = new SimpleDirectedGraph<>(DecisionEdge.class);
	
	/**
	 * Print the tree to console
	 */
	public void print() {
		System.out.println("Decision Tree for given csv File:");
		System.out.println();
		String indent = " ";
		print(root, indent, "");
	}

	/**
	 * Print a sub-tree to console
	 *
	 * @param nodeToPrint
	 *            the root note
	 * @param indent
	 *            the current indentation
	 * @param value
	 *            a string that should be used to increase the indentation
	 */
	private void print(DecisionNode nodeToPrint, String indent, String value) {
		if (value.isEmpty() == false) {
			System.out.println(indent + value);
		}
	
		String newIndent = indent + "  ";

		// if it is a class node print it directly
		if (nodeToPrint.isClassNode) {
			System.out.println(newIndent + " = " + nodeToPrint.className);
		} else {
			// if it is a decision node, print it and recursivly call {@code print()}
			DecisionNode node = nodeToPrint;
			System.out.println(newIndent + allAttributes[node.attribute] + " -->");
			newIndent = newIndent + "  ";
			for (int i = 0; i < node.nodes.length; i++) {
				print(node.nodes[i], newIndent, node.attributeValues[i]);
			}
		}
	}


	/**
	 * Build the print tree with the library Jgrapht. Each encountered node is recreated through
	 *  a {@code DecisionVertex} object. Jgrapht will then work n the basis of these Vertex.<br>
	 * The ID3 resulting tree is traversed from the root to the leafs and for each node an Edge is created. <br>
	 * If the encountered node isn't a classnode, the recursive function {@code buildEges} is called which explore the subtree.<br>
	 * 
	 * @param root
	 */
	public void buildGraph(DecisionNode root) {
		DecisionVertex rootVertex = new DecisionVertex(allAttributes[this.root.attribute], vertexIds++);
		graph.addVertex(rootVertex);
		buildEdges(root,rootVertex);
	}

	public void buildEdges(DecisionNode root, DecisionVertex rootVertex) {
			for(int i = 0; i<root.nodes.length; i++) {
				
				DecisionNode newRoot = root.nodes[i];
				
				if(newRoot.isClassNode) {
					DecisionVertex nodeVertex = new DecisionVertex(newRoot.className, vertexIds++);
					graph.addVertex(nodeVertex);
					graph.addEdge(rootVertex, nodeVertex, new DecisionEdge(root.attributeValues[i]));
				}else {
					DecisionVertex nodeVertex = new DecisionVertex(allAttributes[root.nodes[i].attribute], vertexIds++);
					graph.addVertex(nodeVertex);
					graph.addEdge(rootVertex, nodeVertex, new DecisionEdge(root.attributeValues[i]));
					buildEdges(newRoot, nodeVertex);
					
				}
			}
		
	}
	/**
	 * Export the PrintGraph to a .dot file. The given file can then be converted to an image via the Graphviz software.
	 */
	public void exportGraph(String fileName) {
		ComponentNameProvider<DecisionVertex> vertexIdProvider = new ComponentNameProvider<DecisionVertex>()
        {
            public String getName(DecisionVertex node)
            {
            	
                return String.valueOf(node.getID());
            }
        };
        ComponentNameProvider<DecisionVertex> vertexLabelProvider = new ComponentNameProvider<DecisionVertex>()
        {
            public String getName(DecisionVertex node)
            {
            	return node.getAttributeName();
            }
        };

        ComponentNameProvider<DecisionEdge> edgeLabelProvider = new ComponentNameProvider<DecisionEdge>()
        {
            public String getName(DecisionEdge edge)
            {
            	return edge.getAttributeValue();
            }
        };
        GraphExporter<DecisionVertex, DecisionEdge> exporter =
            new DOTExporter<>(vertexIdProvider, vertexLabelProvider, edgeLabelProvider);
        try {
        	BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get(fileName + ".dot"), StandardCharsets.UTF_8);
			exporter.exportGraph(graph, bufferedWriter);
		} catch (ExportException  | IOException e) {
			System.err.println("Error during the export, please retry and check the given fileName");
		}
       
	}
	

}
