package de.uniba.cogsys.id3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Top-down Implementation of the greedy ID3 algorithm to find a decision tree based on information gain.
 */
public class ID3 {

	private String[] allAttributes;
	private int indexTargetAttribute = -1; // index of target attribute
	private Set<String> targetAttributeValues = new HashSet<String>();

	private int[] remainingAttributes;
	private List<String[]> trainingExamples = new ArrayList<String[]>();

	private void parseCSV(String input, String targetAttribute) throws IOException {
		
		BufferedReader reader = new BufferedReader(new FileReader(input));
		String line = reader.readLine();
		
		// read header
		allAttributes = line.split(",");

		// all attributes except class
		remainingAttributes = new int[allAttributes.length - 1]; 
		int pos = 0;
		
		for (int i = 0; i < allAttributes.length; i++) {
			if (allAttributes[i].equals(targetAttribute)) {
				// save index of class attribute
				indexTargetAttribute = i;
			} else {
				// otherwise add the attribute to the array of attributes
				remainingAttributes[pos++] = i;
			}
		}

		// read training examples
		while (((line = reader.readLine()) != null)) {
			String[] lineSplit = line.split(",");
			trainingExamples.add(lineSplit);
			targetAttributeValues.add(lineSplit[indexTargetAttribute]);
		}
		reader.close(); // close input file

	}

	/**
	 * Creates a decision tree from a training set
	 *
	 * @param input
	 *            path to an input file containing the training set
	 * @param targetAttribute
	 *            the target attribute (that will be used for classification)
	 * @return a decision tree
	 * @throws IOException
	 *             exception if error reading the file
	 */
	public DecisionTree runAlgorithm(String input, String targetAttribute) throws IOException {

		// read CSVt
		try {
			parseCSV(input, targetAttribute);
		} catch (IOException e) {
			throw new IOException("Couldn't read CSV File", e.getCause());
		}
		
		// create an empty decision tree
		DecisionTree tree = new DecisionTree();

		// create the tree recursively
		tree.root = id3(remainingAttributes, trainingExamples);
		tree.allAttributes = allAttributes;

		return tree;
	}

	/**
	 * Creating a subtree for given attributes and examples
	 *
	 * @param remainingAttributes
	 *            remaining attributes to create the tree
	 * @param trainingExamples
	 *            a list of training examples
	 * @return node of the subtree created
	 */
	private DecisionNode id3(int[] remainingAttributes, List<String[]> trainingExamples) {


		// calculate the frequency of each target attribute value and
		// at the same time check if there is a single class.
		Map<String, Integer> targetValuesFrequency = calculateFrequencyOfAttributeValues(trainingExamples,
				indexTargetAttribute);

		// if all instances are from the same class
		if (targetValuesFrequency.entrySet().size() == 1) {
			DecisionNode classNode = new DecisionNode();
			classNode.className = (String) targetValuesFrequency.keySet().toArray()[0];
			classNode.isClassNode = true;
			return classNode;
		}

		double entropyS = 0d;

		for (String value : targetAttributeValues) {

			Integer frequencyInt = targetValuesFrequency.get(value);
			if (frequencyInt != null) {
				double frequencyDouble = frequencyInt / (double) trainingExamples.size();
				// update entropyS
				entropyS -= frequencyDouble * Math.log(frequencyDouble) / Math.log(2);
			}
		}

		int attributeWithHighestGain = 0;
		double highestGain = Double.MIN_VALUE;

		for (int attribute : remainingAttributes) {
			double gain = calculateGain(attribute, trainingExamples, entropyS);

			// if same information gain, choose the one which is alphabetically in front of the other
			if (gain >= highestGain) {
				if (gain == highestGain) {
					int lex = allAttributes[attributeWithHighestGain].compareToIgnoreCase(allAttributes[attribute]);
					if (lex < 0) {
						highestGain = gain;
					} else {
						highestGain = gain;
						attributeWithHighestGain = attribute;
					}
				} else {
					highestGain = gain;
					attributeWithHighestGain = attribute;
				}
			}
		}

		DecisionNode decisionNode = new DecisionNode();
		decisionNode.attribute = attributeWithHighestGain;

		int[] newRemainingAttribute = new int[remainingAttributes.length - 1]; // removing element
		int pos = 0;
		for (int i = 0; i < remainingAttributes.length; i++) {
			if (remainingAttributes[i] != attributeWithHighestGain) {
				newRemainingAttribute[pos++] = remainingAttributes[i];
			}
		}

		// Split data to partitions according to the selected attribute
		Map<String, List<String[]>> partitions = new HashMap<String, List<String[]>>();
		for (String[] trainingExample : trainingExamples) {
			String value = trainingExample[attributeWithHighestGain];
			List<String[]> listExamples = partitions.get(value);
			if (listExamples == null) {
				listExamples = new ArrayList<String[]>();
				partitions.put(value, listExamples);
			}
			listExamples.add(trainingExample);
		}

		// create the values for the subnodes
		decisionNode.nodes = new DecisionNode[partitions.size()];
		decisionNode.attributeValues = new String[partitions.size()];

		// create nodes for every partition
		int index = 0;
		for (Entry<String, List<String[]>> partition : partitions.entrySet()) {
			decisionNode.attributeValues[index] = partition.getKey();
			decisionNode.nodes[index] = id3(newRemainingAttribute, partition.getValue()); // recursive call
			index++;
		}

		return decisionNode;
	}

	/**
	 * Calculates the information gain of an attribute in a training set
	 *
	 * @param attributePos
	 *            the position of the attribute
	 * @param trainingExamples
	 *            the training set
	 * @param entropyS
	 *            Entropy(S)
	 * @return the gain
	 */
	private double calculateGain(int attributePos, List<String[]> trainingExamples, double entropyS) {
		// Count the frequency of each value for the attribute
		Map<String, Integer> valuesFrequency = calculateFrequencyOfAttributeValues(trainingExamples, attributePos);

		double sum = 0;
		for (Entry<String, Integer> entry : valuesFrequency.entrySet()) {
			sum += entry.getValue() / ((double) trainingExamples.size())
					* calculateEntropyIfValue(trainingExamples, attributePos, entry.getKey());
		}
		return entropyS - sum;
	}

	/**
	 * Calculate the entropy for the target attribute, if a given attribute has a
	 * given value.
	 *
	 * @param trainingExamples
	 *            training set
	 * @param attributeIF
	 *            the given attribute
	 * @param valueIF
	 *            the given value
	 * @return entropy
	 */
	private double calculateEntropyIfValue(List<String[]> trainingExamples, int attributeIF, String valueIF) {

		Map<String, Integer> valuesFrequency = new HashMap<String, Integer>();

		int counter = 0;
		for (String[] instance : trainingExamples) {

			if (instance[attributeIF].equals(valueIF)) {
				String targetValue = instance[indexTargetAttribute];
				// increment by 1
				if (valuesFrequency.get(targetValue) == null) {
					valuesFrequency.put(targetValue, 1); 
				} else {
					valuesFrequency.put(targetValue, valuesFrequency.get(targetValue) + 1);
				}
				counter++;
			}
		}

		double entropy = 0;
		for (String value : targetAttributeValues) {
			Integer count = valuesFrequency.get(value);
			if (count != null) {
				double frequency = count / (double) counter;
				entropy -= frequency * Math.log(frequency) / Math.log(2);
			}
		}
		return entropy;
	}

	/**
	 * This method calculates the frequency of each value for an attribute in a
	 * given set of instances
	 *
	 * @param trainingExamples
	 *           the training set
	 * @param indexAttribute
	 *            The attribute.
	 * @return A map where the keys are attributes and values are the number of
	 *         times that the value appeared in the training example.
	 *         key: a string indicating a value;	value: the frequency
	 */
	private Map<String, Integer> calculateFrequencyOfAttributeValues(List<String[]> trainingExamples, int indexAttribute) {

		Map<String, Integer> targetValuesFrequency = new HashMap<String, Integer>();

		for (String[] instance : trainingExamples) {

			String targetValue = instance[indexAttribute];
			
			if (targetValuesFrequency.get(targetValue) == null) {
				targetValuesFrequency.put(targetValue, 1); // increment frequency by 1
			} else {
				targetValuesFrequency.put(targetValue, targetValuesFrequency.get(targetValue) + 1);
			}
		}

		return targetValuesFrequency;
	}
}