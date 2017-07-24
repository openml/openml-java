package examples;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
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

public class PlotCsvGenerator {

	private static final String key_read = "c1994bdb7ecb3c6f3c8f3b35f4b47f1f";
	private static final String url = "https://www.openml.org/";
	private static final OpenmlConnector openml = new OpenmlConnector(url, key_read); 
	
	
	@Test
	public void testFlowsOnTaks() throws Exception {
		int taskId = 6;
		String measure = "predictive_accuracy";
		File resultFile = new File("flowsOnTask" + taskId + ".csv");
		int resultLimit = 20;
		
		flowsOnTask(openml, taskId, measure, resultFile, resultLimit);
	}
	
	public static String formatFlowname(Flow f) {
		// this function provides a name mapping in case you want to display custom names
		return f.getName().substring(5) + "(" + f.getVersion() + ")";
	}
	
	public static boolean flowEligible(Flow f) {
		return f.getName().startsWith("weka.") && f.getComponent() == null;
	}
	
	public static void flowsOnTask(OpenmlConnector openml, int taskId, String evaluationMeasure, File resultsFile, int resultLimit) throws Exception {
		// Data structure that maps from flow id to a list of results (on the evalutionMeasure)
		Map<Integer,List<Double>> flowidResult = new TreeMap<Integer, List<Double>>();
		// Data structure that maps from result (on the evalutionMeasure) to flowId
		Map<Double,List<Integer>> resultFlowid = new TreeMap<Double, List<Integer>>(Collections.reverseOrder());
		// Data structure to keep track of flow ids already used
		TreeSet<Integer> usedFlowids = new TreeSet<Integer>();
		
		// bookkeeping to prepare the call; the function requires a list of taskIds
		List<Integer> taskIds = new ArrayList<Integer>();
		taskIds.add(taskId);
		
		// obtains the results from OpenML
		EvaluationList el = openml.evaluationList(taskIds, null, evaluationMeasure);
		
		// prepare our data structures:
		for (Evaluation e : el.getEvaluations()) {
			// obtain relevant information
			Integer flowid = e.getFlow_id();
			Double result = e.getValue();
			
			// initialize the keys in bookkeeping data structures:
			if (!flowidResult.containsKey(flowid)) { flowidResult.put(flowid, new ArrayList<Double>()); }
			if (!resultFlowid.containsKey(result)) { resultFlowid.put(result, new ArrayList<Integer>()); }
			
			// fill the data structures
			flowidResult.get(flowid).add(result);
			resultFlowid.get(result).add(flowid);
		}
		
		// initialize the csv writer
		BufferedWriter bw = new BufferedWriter(new FileWriter(resultsFile));
		bw.write("\"rank\", \"id\", \"classifier\", \"result\"\n");
		
		int rank = 1;
		// loop over the results in order of the "best result"
		outer: for (Iterator<Double> result = resultFlowid.keySet().iterator(); result.hasNext(); ) {
			
			// note that for each result, we can have multiple flows that scored that result. Loop over these
			for (int flowid : resultFlowid.get(result.next())) {
				// obtain the flow from OpenML (for the name in the key)
				Flow flow = openml.flowGet(flowid);
				
				// function that only returns true for flows we are interested in
				if (flowEligible(flow) && !usedFlowids.contains(flow.getId())) {
					// this flow could have had multiple runs with different hyper-parameters, loop over these
					for (Double score : flowidResult.get(flowid)) {
						bw.write(rank + "\t" + flowid + "\t\"" + formatFlowname(flow) + "\"\t" + score + "\n");
					}
					
					// generate some output
					System.out.println(rank + ". " + formatFlowname(flow) + ": " + flowidResult.get(flowid).size() + " results. ");
					
					// keep track that we used this flow (and will not reuse it)
					usedFlowids.add(flow.getId());
					
					// update rank
					rank += 1;
					
					// Break if the plot contains more than resultLimit lines
					if (rank > resultLimit) { break outer; } 
				}
			}
		}
		bw.close();
		// now the resulting csv can be plotted with a GNUplot command like "plot "results.csv" using 1:4:xticlabels(3)" 
	}
}
