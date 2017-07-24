[![Build Status](https://travis-ci.org/openml/java.svg?branch=master)](https://travis-ci.org/openml/java)
[![Coverage Status](https://coveralls.io/repos/github/openml/java/badge.svg?branch=master)](https://coveralls.io/github/openml/java?branch=master)

# Java Apiconnector
Library for interfacing between OpenML and Java. Designed to not rely on Weka. For conveniently uploading of Weka Classifiers to OpenML, please consider using [OpenML Weka](https://github.com/openml/openml-weka/), either as Java library or as Weka package (available on the [Weka marketplace](http://weka.sourceforge.net/packageMetaData/)). 

# Obtain results from OpenML
The Openml Apiconnector is basically a one-on-one mapping towards the various [REST Api calls](https://www.openml.org/api_docs), with some additional convenience functions. This library should be used as basis for most OpenML related projects in Java. Most conveniently, it can be used to obtain the results from OpenML and plot these into a graph. For an extensive list of possibilities, the reader is refered to J. N. van Rijn, Massively Collaborative Machine Learning, Leiden University, 2016 (Chapter 4). In this guide, some example code is given. The following examples demonstrate how to generate CSV data for such plots, these can then be plotted with (for example) GNU Plot.

In general, the following function obtains most result:
```java
EvaluationList el = evaluationList(List<Integer> taskIds, List<Integer> setupIds, String function);
```
where taskIds and setupIds are lists filtering the results on the included ids (can be null) and function determines the evaluation measure we are interested in (typically, 'predictive_accuracy' or 'area_under_roc_curve' will do). Make sure to filter these results as much as possible, as this limits the load on the server. 

After that, the data needs to be formatted in such a way that the appropriate plotting script can handle it. This usually requires some (trivial) data processing. Full code and examples how to call these are provided in the UnitTest [PlotCsvGenerator.java](https://github.com/openml/java/blob/master/apiconnector/src/test/java/examples/PlotCsvGenerator.java). 

## Plot results of flows on a given task

This function requires to additional functions to be present: 
* flowEligible(Flow f) - A boolean function that determines whether a flow is eligible to be shown in the plot (useful when we want to show a subset of the flows, e.g., only the Weka flows).
* formatFlowname(Flow f) - A function that formats the name of the flow. By default, it could return f.getName(), but as some flow names tend to be quite long, obfuscating the legend, sometimes something a more complicated should be done. 

This will lead to a plot similar to [Figure 4.7 of 'Massively Collaborative Machine Learning'](https://openaccess.leidenuniv.nl/bitstream/handle/1887/44814/thesis.pdf?sequence=2#figure.caption.22)

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
  // now the resulting csv can be plotted with a GNUplot command like "plot "results.csv" using 1:4:xticlabels(3)" 
}
```


# How to cite
If you found this library useful, please cite: J. N. van Rijn, Massively Collaborative Machine Learning, Leiden University, 2016. If you used OpenML in a scientific publication, please check out the [OpenML citation policy](https://www.openml.org/cite). 
