package examples;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import org.junit.Test;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.xml.EvaluationList;
import org.openml.apiconnector.xml.EvaluationList.Evaluation;
import org.openml.apiconnector.xml.Flow;
import org.openml.apiconnector.xml.SetupParameters;
import org.openml.apiconnector.xml.Study;

public class PlotCsvGenerator {

	private static final String key_read = "c1994bdb7ecb3c6f3c8f3b35f4b47f1f";
	private static final String url = "https://www.openml.org/";
	private static final OpenmlConnector openml = new OpenmlConnector(url, key_read);
	
	@Test
	public void testCompareSetups() throws Exception {
		Study study = openml.studyGet(34);
		String measure = "predictive_accuracy";
		File resultFile = new File("results.csv");
		compareClassifiersAcrossTasks(openml, 
				Arrays.asList(study.getTasks()), 
				Arrays.asList(study.getSetups()), 
				measure, resultFile);
	}
	
	@Test
	public void testFlowsOnTaks() throws Exception {
		int taskId = 6;
		String measure = "predictive_accuracy";
		File resultFile = new File("flowsOnTask" + taskId + ".csv");
		int resultLimit = 20;

		flowsOnTask(openml, taskId, measure, resultFile, resultLimit);
	}
	
	@Test
	public void testHyperparameterEffect() throws Exception {
		Integer[] taskIds = {6, 21, 28, 41, 58};
		Integer[] setupIds = {8920, 8921, 8922, 8923, 8924, 8925, 8926, 8927, 8929, 8930, 8932, 8937, 8938};
		String measure = "predictive_accuracy";
		String hyperparameter = "weka.RBFKernel(4)_G";
		File resultFile = new File("effectG.csv");

		hyperparameterEffect(openml, Arrays.asList(taskIds), Arrays.asList(setupIds), hyperparameter, measure, resultFile);
	}
	
	public static void compareClassifiersAcrossTasks(OpenmlConnector openml, List<Integer> taskIds, List<Integer> setupIds, String evaluationMeasure, File resultsFile) throws Exception {
		// obtains all evaluations that comply to the three filters
		EvaluationList results = openml.evaluationList(taskIds, setupIds, evaluationMeasure);
		// initialize data structure for storing the results, mapping from
		// param value to a mapping from task id to result value
		Map<Integer, Map<Integer, Double>> resultMap = new TreeMap<Integer, Map<Integer, Double>>();
		
		// loop over all the results obtained from OpenML
		for (Evaluation e : results.getEvaluations()) {
			if (!resultMap.containsKey(e.getTask_id())) {
				resultMap.put(e.getTask_id(), new TreeMap<Integer, Double>());
			}
			resultMap.get(e.getTask_id()).put(e.getSetup_id(), e.getValue());
		}
		
		// initialize the csv writer and the header
		BufferedWriter bw = new BufferedWriter(new FileWriter(resultsFile));
		bw.write("\"Task id\"");
		for (int setupId : setupIds) { bw.write("\t\"" + formatSetupid(setupId) + "\""); }
		
		for (int taskId : taskIds) {
			bw.write("\n" + taskId);
			for (int setupId : setupIds) {
				if (resultMap.get(taskId).containsKey(setupId)) {
					bw.write("\t" + resultMap.get(taskId).get(setupId));
				} else {
					System.err.println("Warning: task " + taskId + " does not contain setup " + setupId);
					bw.write("\t0.0");
				}
			}
		}
		
		bw.close();
		
		/* now the file can be plotted with a GNUplot script like:
		 * 
		 * set style data boxplot
		 * set xtics rotate by -45
		 * sub(s) = system(sprintf("echo \"%s\" | sed 's/@/ /g'", s))
		 * header = system("head -1 'results.csv'")
		 * set for [i=1:words(header)] xtics (sub(word(header, i)) i)
		 * plot for [i=2:40] 'results.csv' using (i):i lt -1 lc rgb "blue" pointsize 0.2 notitle
		 */
	}

	public static void flowsOnTask(OpenmlConnector openml, int taskId, String evaluationMeasure, File resultsFile,
			int resultLimit) throws Exception {
		// Data structure that maps from flow id to a list of results (on the
		// evalutionMeasure)
		Map<Integer, List<Double>> flowidResult = new TreeMap<Integer, List<Double>>();
		// Data structure that maps from result (on the evalutionMeasure) to
		// flowId
		Map<Double, List<Integer>> resultFlowid = new TreeMap<Double, List<Integer>>(Collections.reverseOrder());
		// Data structure to keep track of flow ids already used
		TreeSet<Integer> usedFlowids = new TreeSet<Integer>();

		// bookkeeping to prepare the call; the function requires a list of
		// taskIds
		List<Integer> taskIds = new ArrayList<Integer>();
		taskIds.add(taskId);

		// obtains the results from OpenML
		EvaluationList results = openml.evaluationList(taskIds, null, evaluationMeasure);

		// prepare our data structures:
		for (Evaluation e : results.getEvaluations()) {
			// obtain relevant information
			Integer flowid = e.getFlow_id();
			Double result = e.getValue();

			// initialize the keys in bookkeeping data structures:
			if (!flowidResult.containsKey(flowid)) {
				flowidResult.put(flowid, new ArrayList<Double>());
			}
			if (!resultFlowid.containsKey(result)) {
				resultFlowid.put(result, new ArrayList<Integer>());
			}

			// fill the data structures
			flowidResult.get(flowid).add(result);
			resultFlowid.get(result).add(flowid);
		}

		// initialize the csv writer
		BufferedWriter bw = new BufferedWriter(new FileWriter(resultsFile));
		bw.write("\"rank\", \"id\", \"classifier\", \"result\"\n");

		int rank = 1;
		// loop over the results in order of the "best result"
		outer: for (Iterator<Double> itt = resultFlowid.keySet().iterator(); itt.hasNext();) {
			Double result = itt.next();
			// note that for each result, we can have multiple flows that scored
			// that result. Loop over these
			for (int flowid : resultFlowid.get(result)) {
				// obtain the flow from OpenML (for the name in the key)
				Flow flow = openml.flowGet(flowid);

				// function that only returns true for flows we are interested
				// in
				if (flowEligible(flow) && !usedFlowids.contains(flow.getId())) {
					// this flow could have had multiple runs with different
					// hyper-parameters, loop over these
					for (Double score : flowidResult.get(flowid)) {
						bw.write(rank + "\t" + flowid + "\t\"" + formatFlowname(flow) + "\"\t" + score + "\n");
					}

					// generate some output
					System.out.println(
							rank + ". " + formatFlowname(flow) + ": " + flowidResult.get(flowid).size() + " results. ");

					// keep track that we used this flow (and will not reuse it)
					usedFlowids.add(flow.getId());

					// update rank
					rank += 1;

					// Break if the plot contains more than resultLimit lines
					if (rank > resultLimit) {
						break outer;
					}
				}
			}
		}
		bw.close();
		// now the resulting csv can be plotted with a GNUplot command like
		// "plot 'results.csv' using 1:4:xticlabels(3)"
	}

	public static void hyperparameterEffect(OpenmlConnector openml, List<Integer> taskIds, List<Integer> setupIds,
			String hyperparameter, String evaluationMeasure, File resultsFile) throws Exception {
		// obtains all evaluations that comply to the three filters
		EvaluationList results = openml.evaluationList(taskIds, setupIds, evaluationMeasure);

		// initialize data structure for storing the results, mapping from
		// param value to a mapping from task id to result value
		Map<Double, Map<Integer, Double>> hyperparameterEffect = new TreeMap<Double, Map<Integer, Double>>();
		
		// for sanity checking: all setups need to have the same flow id
		Integer flowId = null;
		
		// loop over all the results obtained from OpenML
		for (Evaluation e : results.getEvaluations()) {
			// we have a setup id -> use this to obtain the full setup object
			SetupParameters setupDetails = openml.setupParameters(e.getSetup_id());
			// sanity checking
			if (flowId == null) {
				flowId = setupDetails.getFlow_id();
			} else {
				if (!flowId.equals(setupDetails.getFlow_id())) {
					throw new RuntimeException("Flow id of setups does not match. Expected: " + flowId + ", found: " + setupDetails.getFlow_id()); 
				}
			}
			
			// use convenience function to convert hyperparameters object into hashmap
			Map<String, SetupParameters.Parameter> params = setupDetails.getParametersAsMap();
			// obtain the value of the hyperparameter we are interested in
			Double hyperparameterValue = Double.parseDouble(params.get(hyperparameter).getValue());
			// and add this to our data structure
			if (!hyperparameterEffect.containsKey(hyperparameterValue)) {
				hyperparameterEffect.put(hyperparameterValue, new TreeMap<Integer, Double>());
			}
			hyperparameterEffect.get(hyperparameterValue).put(e.getTask_id(), e.getValue());
			
		}

		// initialize the csv writer and the header
		BufferedWriter bw = new BufferedWriter(new FileWriter(resultsFile));
		bw.write("\"" + hyperparameter + "\"");
		for (int taskId : taskIds) { bw.write("\t\"Task " + taskId + "\""); }
		bw.write("\n");
		
		// loops over the results and print to csv
		for (Iterator<Double> itt = hyperparameterEffect.keySet().iterator(); itt.hasNext();) {
			Double paramVal = itt.next();
			bw.append(""+paramVal);
			for (int taskId : taskIds) {
				if (hyperparameterEffect.get(paramVal).containsKey(taskId)) {
					bw.append("\t" + hyperparameterEffect.get(paramVal).get(taskId));
				} else {
					bw.append(",");
				}
			}
			bw.append("\n");
		}
		bw.close();

		// now the file can be plotted with a GNUplot command like "plot for [i=2:10] "results.csv" using 1:i with lp title columnheader"
	}
	
	public static String formatSetupid(int setupId) throws Exception {
		SetupParameters sp = openml.setupParameters(setupId);
		Flow f = openml.flowGet(sp.getFlow_id());
		
		return f.getName();
	}

	public static String formatFlowname(Flow f) {
		// this function provides a name mapping in case you want to display
		// custom names
		return f.getName().substring(5) + "(" + f.getVersion() + ")";
	}

	public static boolean flowEligible(Flow f) {
		return f.getName().startsWith("weka.") && f.getComponent() == null;
	}
}
