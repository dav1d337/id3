package de.uniba.cogsys.id3;

import java.io.IOException;

public class Main {

	public static void main(String[] args) {

		if (args.length != 1 || !args[0].endsWith(".csv")) {
			System.out.println("Invalid input, give just a csv-file as system argument");
			System.exit(0);
		}

		ID3 id3 = new ID3();
		try {
			// change class for changing target attribute
			DecisionTree resultingTree = id3.runAlgorithm(args[0], "class");
			resultingTree.buildGraph(resultingTree.root);
			resultingTree.exportGraph("IDTree");
			resultingTree.print();

		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

}
