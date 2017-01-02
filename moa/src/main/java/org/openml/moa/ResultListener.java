package org.openml.moa;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.algorithms.MathHelper;
import org.openml.apiconnector.algorithms.TaskInformation;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.models.Metric;
import org.openml.apiconnector.models.MetricScore;
import org.openml.apiconnector.xml.Flow;
import org.openml.apiconnector.xml.Run;
import org.openml.apiconnector.xml.UploadRun;
import org.openml.apiconnector.xml.UploadRunAttach;
import org.openml.apiconnector.xml.Run.Parameter_setting;
import org.openml.apiconnector.xml.Task;
import org.openml.apiconnector.xml.Task.Output.Predictions.Feature;
import org.openml.apiconnector.xstream.XstreamXmlMapping;
import org.openml.moa.algorithm.MoaAlgorithm;

import com.yahoo.labs.samoa.instances.Attribute;
import com.yahoo.labs.samoa.instances.Instances;
import com.yahoo.labs.samoa.instances.InstancesHeader;

import moa.classifiers.Classifier;

public class ResultListener {

	private static final String[] MOA_TAGS = {"Moa"};
	
	// constants
	private final OpenmlConnector apiconnector;
	private final Integer openmlTaskId;
	private final InstancesHeader header;
	private final DecimalFormat df;
	
	// almost constants
	private int att_index_row_id = -1;
	private int att_index_confidence = -1;
	private int att_index_prediction = -1;
	private int att_index_correct = -1;
	private ArrayList<String> classes = new ArrayList<String>();
	
	// constants in normal data stream tasks
	private BufferedWriter bw;
	private File results;
	
	// counters
	private int predictionsUploaded = 0;
	private int predictionsBatch = 0;
	private Integer run_id = null;

	public ResultListener(Integer openmlTaskId, OpenmlConnector apiconnector) throws Exception {
		this.openmlTaskId = openmlTaskId;
		this.apiconnector = apiconnector;

		df = new DecimalFormat(".######");
		header = createInstanceHeader(openmlTaskId);
		bw = null;
		results = null;
	}

	public boolean sendToOpenML(Classifier classifier, Map<Metric, MetricScore> userdefinedMeasures) throws Exception {
		if (predictionsBatch == 0) {
			return false;
		}
		
		bw.close();
		bw = null;
		
		// some logistics for multiple prediction files
		int index = predictionsUploaded;
		predictionsUploaded += predictionsBatch;
		predictionsBatch = 0;
		
		// create run description xml
		Flow implementation = MoaAlgorithm.create(classifier);
		int implementation_id = MoaAlgorithm.getFlowId(implementation, classifier, apiconnector);
		implementation = apiconnector.flowGet(implementation_id); // updated

		ArrayList<Parameter_setting> ps = MoaAlgorithm.getOptions(implementation, classifier.getOptions().getOptionArray());

		Run run = new Run(openmlTaskId, null, implementation_id, classifier.getCLICreationString(Classifier.class),
				ps.toArray(new Parameter_setting[ps.size()]), MOA_TAGS);
		if (userdefinedMeasures != null) {
			for (Metric m : userdefinedMeasures.keySet()) {
				MetricScore score = userdefinedMeasures.get(m);
				run.addOutputEvaluation(m.name, m.implementation, score.getScore(), score.getArrayAsString(df));
			}
		}
		String runxml = XstreamXmlMapping.getInstance().toXML(run);
		File descriptionXML = Conversion.stringToTempFile(runxml, "moa_task_" + openmlTaskId, "xml");

		if (run_id == null) {
			// initial upload (and for normal tasks also the last one)
			Map<String, File> output_files = new HashMap<String, File>();
			output_files.put("predictions", results);
			try {
				UploadRun ur = apiconnector.runUpload(descriptionXML, output_files);
				run_id = ur.getRun_id();
				Conversion.log("OK", "Upload Result", "Result successfully uploaded, with rid " + ur.getRun_id());
				return true;
			} catch (Exception e) {
				Conversion.log("Error", "Upload Result", "Unable to upload: " + e.getMessage());
				return false;
			}
		} else {
			// upload of additional prediction batches (stream challenges)
			try {
				UploadRunAttach ura = apiconnector.runUploadAttach(run_id, index, descriptionXML, results);
				Conversion.log("OK", "Upload Result", "Successfully uploaded batch of predictions, " + ura.getRun_id());
				return true;
			} catch (Exception e) {
				Conversion.log("Error", "Upload Result", "Unable to upload: " + e.getMessage());
				return false;
			}
		}
	}

	public void addPrediction(int row_id, double[] predictions, Integer correct) throws IOException {
		// initialize header if this is not done already
		if (bw == null) {
			results = Conversion.stringToTempFile(header.toString(), header.getRelationName(), "arff");
			bw = new BufferedWriter(new FileWriter(results));
			bw.write(header.toString());
		}
		
		String line = "";
		String[] instance = new String[header.numAttributes()];
		int predicted = MathHelper.argmax(predictions, true);
		instance[att_index_prediction] = (predicted < 0) ? "?" : classes.get(predicted);
		instance[att_index_row_id] = "" + row_id;
		if (correct != null) {
			instance[att_index_correct] = classes.get(correct);
		} else {
			instance[att_index_correct] = null;
		}
		
		for (int i = 0; i < classes.size(); ++i) {
			if (i < predictions.length) {
				instance[att_index_confidence + i] = df.format(predictions[i]);
			} else {
				instance[att_index_confidence + i] = "0";
			}
		}

		instance[att_index_prediction] = predicted >= 0 ? classes.get(predicted) : "?";

		for (int i = 0; i < instance.length; ++i) {
			line += "," + ((instance[i] != null) ? instance[i] : "?");
		}
		bw.write(line.substring(1) + "\n");
		predictionsBatch += 1;
	}

	private InstancesHeader createInstanceHeader(Integer taskId) throws Exception {
		Task t = apiconnector.taskGet(taskId);
		ArrayList<Attribute> header = new ArrayList<Attribute>();
		Feature[] features = TaskInformation.getPredictions(t).getFeatures();
		classes = new ArrayList<String>(Arrays.asList(TaskInformation.getClassNames(apiconnector, t)));
		for (int i = 0; i < features.length; i++) {
			Feature f = features[i];
			if (f.getName().equals("confidence.classname")) {
				att_index_confidence = i;
				for (String s : TaskInformation.getClassNames(apiconnector, t)) {
					header.add(new Attribute("confidence." + s));
				}
			} else if (f.getName().equals("prediction")) {
				header.add(new Attribute("prediction", classes));
			} else {
				header.add(new Attribute(f.getName()));
			}
		}

		header.add(new Attribute("correct", classes));
		Instances inst = new Instances("openml_task_" + t.getTask_id() + "_predictions", header, 0);
		
		for (int i = 0; i < inst.numAttributes(); ++i) {
			if (inst.attribute(i).name().equals("row_id")) {
				att_index_row_id = i;
			} else if (inst.attribute(i).name().equals("correct")) {
				att_index_correct = i;
			} else if (inst.attribute(i).name().equals("prediction")) {
				att_index_prediction = i;
			}
		}
		
		return new InstancesHeader(inst);
	}
}
