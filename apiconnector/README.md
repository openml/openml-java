# Java Apiconnector
[![Build Status](https://travis-ci.org/openml/java.svg?branch=master)](https://travis-ci.org/openml/java)
[![Coverage Status](https://coveralls.io/repos/github/openml/java/badge.svg?branch=master)](https://coveralls.io/github/openml/java?branch=master)
[![License](https://img.shields.io/badge/License-BSD%203--Clause-blue.svg)](https://opensource.org/licenses/BSD-3-Clause)

Library for interfacing between OpenML and Java. Designed to not rely on Weka. For conveniently uploading of Weka Classifiers to OpenML, please consider using [OpenML Weka](https://github.com/openml/openml-weka/), either as Java library or as Weka package (available on the [Weka marketplace](http://weka.sourceforge.net/packageMetaData/)). 

# Obtain results from OpenML
The Openml Apiconnector is basically a one-on-one mapping towards the various [REST Api calls](https://www.openml.org/api_docs), with some additional convenience functions. This library should be used as basis for most OpenML related projects in Java. Most conveniently, it can be used to obtain the results from OpenML and plot these into a graph. For an extensive list of possibilities, the reader is refered to J. N. van Rijn, Massively Collaborative Machine Learning, Leiden University, 2016 (Chapter 4). In this guide, some example code is given. The following examples demonstrate how to generate CSV data for such plots, these can then be plotted with (for example) GNU Plot.

In general, the following function obtains most result:
```java
EvaluationList el = evaluationList(List<Integer> taskIds, List<Integer> setupIds, String function);
```
where taskIds and setupIds are lists filtering the results on the included ids (can be null) and function determines the evaluation measure we are interested in (typically, 'predictive_accuracy' or 'area_under_roc_curve' will do). Make sure to filter these results as much as possible, as this limits the load on the server. 

After that, the data needs to be formatted in such a way that the appropriate plotting script can handle it. This usually requires some (trivial) data processing. Full code and examples how to call these are provided in the UnitTest [PlotCsvGenerator.java](https://github.com/openml/java/blob/master/apiconnector/src/test/java/examples/PlotCsvGenerator.java). 


## Compare various classifiers across tasks
This function requires the following additional functions to be present: 
* formatSetupid(int setupid) - A function that formats the name of the setup. By default, it could just return its own value, but in order to be more informative, the name of the corresponding flow could be attached. 

This will lead to a plot similar to [Figure 4.10 of 'Massively Collaborative Machine Learning'](https://openaccess.leidenuniv.nl/bitstream/handle/1887/44814/thesis.pdf?sequence=2#figure.caption.26)

```java
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
```

## Plot results of flows on a given task
This function requires the following additional functions to be present: 
* flowEligible(Flow f) - A boolean function that determines whether a flow is eligible to be shown in the plot (useful when we want to show a subset of the flows, e.g., only the Weka flows).
* formatFlowname(Flow f) - A function that formats the name of the flow. By default, it could return f.getName(), but as some flow names tend to be quite long, obfuscating the legend, sometimes something a more complicated should be done. 

This will lead to a plot similar to [Figure 4.7 of 'Massively Collaborative Machine Learning'](https://openaccess.leidenuniv.nl/bitstream/handle/1887/44814/thesis.pdf?sequence=2#figure.caption.22).

```java
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
  // now the resulting csv can be plotted with a GNUplot command like "plot 'results.csv' using 1:4:xticlabels(3)" 
}
```

## Plot the effect of a given hyperparameter
This functionality requires as input a list of setups; please be aware that these setups need to be originated from the same flow. 

This will lead to a plot similar to [Figure 4.9 of 'Massively Collaborative Machine Learning'](https://openaccess.leidenuniv.nl/bitstream/handle/1887/44814/thesis.pdf?sequence=2#figure.caption.25).

```java
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
      if (flowId != setupDetails.getFlow_id()) { 
        throw new RuntimeException("Flow id of setups does not match"); 
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
```

# How to cite
If you found this library useful, please cite: J. N. van Rijn, Massively Collaborative Machine Learning, Leiden University, 2016. If you used OpenML in a scientific publication, please check out the [OpenML citation policy](https://www.openml.org/cite). 
