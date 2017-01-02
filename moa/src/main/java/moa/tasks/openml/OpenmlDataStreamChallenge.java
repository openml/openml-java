package moa.tasks.openml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.settings.Config;
import org.openml.apiconnector.settings.Constants;
import org.openml.moa.ResultListener;
import org.openml.moa.algorithm.InstancesHelper;

import moa.classifiers.Classifier;
import moa.core.Example;
import moa.core.ObjectRepository;
import moa.evaluation.LearningCurve;
import moa.evaluation.LearningPerformanceEvaluator;
import moa.options.ClassOption;

import com.github.javacliparser.FileOption;
import com.github.javacliparser.IntOption;
import com.github.javacliparser.StringOption;

import moa.streams.InstanceStream;
import moa.streams.openml.OpenmlChallengeReader;
import moa.tasks.MainTask;
import moa.tasks.TaskMonitor;

import com.yahoo.labs.samoa.instances.Attribute;
import com.yahoo.labs.samoa.instances.Instance;

public class OpenmlDataStreamChallenge extends MainTask {

	private static final long serialVersionUID = 514834511072776265L;

	@Override
	public String getPurposeString() {
		return "Evaluates a classifier on an OpenML Data Stream Challenge Task.";
	}

	public ClassOption learnerOption = new ClassOption("learner", 'l', "Classifier to train.", Classifier.class,
			"bayes.NaiveBayes");

	public IntOption openmlTaskIdOption = new IntOption("taskId", 't', "The OpenML task that will be performed.", 1, 1,
			Integer.MAX_VALUE);

	public ClassOption evaluatorOption = new ClassOption("evaluator", 'e',
			"Classification performance evaluation method.", LearningPerformanceEvaluator.class,
			"BasicClassificationPerformanceEvaluator");

	public IntOption sampleFrequencyOption = new IntOption("sampleFrequency", 'f',
			"How many instances between samples of the learning performance.", 100000, 0, Integer.MAX_VALUE);

	public StringOption openmlConfigOption = new StringOption("openmlConfig", 'c',
			"A semi-colon separated string passing on OpenML config items" + "Leave empty to use config file on disk ("
					+ Constants.OPENML_DIRECTORY + "/openml.conf)",
			"");

	public FileOption dumpFileOption = new FileOption("dumpFile", 'd', "File to append intermediate csv reslts to.",
			null, "csv", true);

	public FileOption outputPredictionFileOption = new FileOption("outputPredictionFile", 'o',
			"File to append output predictions to.", null, "pred", true);

	private InstanceStream stream;
	private ResultListener resultListener;
	private Config config;
	private OpenmlConnector apiconnector;

	@Override
	public Class<?> getTaskResultType() {
		return LearningCurve.class;
	}

	@Override
	protected Object doMainTask(TaskMonitor monitor, ObjectRepository repository) {
		// TODO: outputPredictionResultStream and ResultListener seem similar. see if one could reuse the other
		
		String configStr = openmlConfigOption.getValue();

		if (configStr != null && !configStr.equals("")) {
			Conversion.log("OK", "Config", "Loaded from config option. ");
			config = new Config(configStr);
		} else {
			try {
				config = new Config();
				Conversion.log("OK", "Config", "Loaded from config file. ");
			} catch (Exception e) {
				throw new RuntimeException("Error loading config file openml.conf. Please check whether it exists. " + e.getMessage());
			}
		}

		if (config.getServer() != null) {
			apiconnector = new OpenmlConnector(config.getServer(), config.getApiKey());
		} else {
			apiconnector = new OpenmlConnector(config.getApiKey());
		}

		Classifier learner = (Classifier) getPreparedClassOption(this.learnerOption);
		stream = new OpenmlChallengeReader(apiconnector, openmlTaskIdOption.getValue());
		int batch_size = ((OpenmlChallengeReader) stream).getBatchSize();
		int initial_batch_size = ((OpenmlChallengeReader) stream).getInitialBatchSize();
		try {
			resultListener = new ResultListener(openmlTaskIdOption.getValue(), apiconnector);
		} catch (Exception e) {
			throw new RuntimeException("Error initializing ResultListener. " + e.getMessage());
		}

		LearningCurve learningCurve = new LearningCurve("learning evaluation instances");

		learner.setModelContext(stream.getHeader());
		Attribute classAttribute = stream.getHeader().attribute(stream.getHeader().classIndex());
		String relationName = stream.getHeader().getRelationName();
		Conversion.log("OK", "Download", "Obtained Stream Header. Data stream: " + relationName);
		Conversion.log("OK", "Download", "Attributes: " + ((OpenmlChallengeReader)stream).getAttributeNames());
		Conversion.log("OK", "Download", "Target attribute: " + classAttribute);
		
		long instancesProcessed = 0;
		monitor.setCurrentActivity("Evaluating learner...", -1.0);

		File dumpFile = this.dumpFileOption.getFile();
		PrintStream immediateResultStream = null;
		if (dumpFile != null) {
			try {
				if (dumpFile.exists()) {
					immediateResultStream = new PrintStream(new FileOutputStream(dumpFile, true), true);
				} else {
					immediateResultStream = new PrintStream(new FileOutputStream(dumpFile), true);
				}
			} catch (Exception ex) {
				throw new RuntimeException("Unable to open immediate result file: " + dumpFile, ex);
			}
		}
		// File for output predictions
		File outputPredictionFile = this.outputPredictionFileOption.getFile();
		PrintStream outputPredictionResultStream = null;
		if (outputPredictionFile != null) {
			try {
				if (outputPredictionFile.exists()) {
					outputPredictionResultStream = new PrintStream(new FileOutputStream(outputPredictionFile, true), true);
				} else {
					outputPredictionResultStream = new PrintStream(new FileOutputStream(outputPredictionFile), true);
				}
			} catch (Exception ex) {
				throw new RuntimeException("Unable to open prediction result file: " + outputPredictionFile, ex);
			}
		}
		
		int instanceCounter = initial_batch_size;

		while (stream.hasMoreInstances()) {
			Example nextInstance = stream.nextInstance();
			
			if (((Instance) nextInstance.getData()).classIsMissing() == false) {
				// TRAIN INSTANCES
				learner.trainOnInstance(nextInstance);
			} else {
				// TEST INSTANCES
				double[] prediction = learner.getVotesForInstance(nextInstance);

				try {
					resultListener.addPrediction(instanceCounter++, InstancesHelper.toProbDist(prediction), null);
				} catch (IOException e) {
					throw new RuntimeException("Error adding prediction: " + e.getMessage());
				}
				
				instancesProcessed++;
				// TODO: sometimes makes errors
				/*if (instancesProcessed % this.sampleFrequencyOption.getValue() == 0 || stream.hasMoreInstances() == false) {
					long evaluateTime = TimingUtils.getNanoCPUTimeOfCurrentThread();
					double time = TimingUtils.nanoTimeToSeconds(evaluateTime - evaluateStartTime);
					double timeIncrement = TimingUtils.nanoTimeToSeconds(evaluateTime - lastEvaluateStartTime);
					double RAMHoursIncrement = learner.measureByteSize() / (1024.0 * 1024.0 * 1024.0); // GBs
					RAMHoursIncrement *= (timeIncrement / 3600.0); // Hours
					RAMHours += RAMHoursIncrement;
					lastEvaluateStartTime = evaluateTime;
					learningCurve.insertEntry(
						new LearningEvaluation(
							new Measurement[] {
								new Measurement("learning evaluation instances", instancesProcessed),
								new Measurement("evaluation time (" + (preciseCPUTiming ? "cpu " : "") + "seconds)", time),
								new Measurement("model cost (RAM-Hours)", RAMHours) },
							evaluator, learner));
					
					if (immediateResultStream != null) {
						if (firstDump) {
							immediateResultStream.println(learningCurve.headerToString());
							firstDump = false;
						}
						immediateResultStream.println(learningCurve.entryToString(learningCurve.numEntries() - 1));
						immediateResultStream.flush();
					}
				} */
				if (instancesProcessed % INSTANCES_BETWEEN_MONITOR_UPDATES == 0) {
					if (monitor.taskShouldAbort()) {
						return null;
					}
					long estimatedRemainingInstances = stream.estimatedRemainingInstances();
					monitor.setCurrentActivityFractionComplete(estimatedRemainingInstances < 0 ? -1.0
							: (double) instancesProcessed / (double) (instancesProcessed + estimatedRemainingInstances));
					if (monitor.resultPreviewRequested()) {
						monitor.setLatestResultPreview(learningCurve.copy());
					}
				}
			}
			
			if (instancesProcessed % batch_size == 0) {
				try {
					resultListener.sendToOpenML(learner, null);
				} catch (Exception e) {
					throw new RuntimeException("Error uploading result to OpenML: " + e.getMessage());
				}
			}
		}
		if (immediateResultStream != null) {
			immediateResultStream.close();
		}
		if (outputPredictionResultStream != null) {
			outputPredictionResultStream.close();
		}
		
		try {
			resultListener.sendToOpenML(learner, null);
		} catch (Exception e) {
			throw new RuntimeException("Error uploading result to OpenML: " + e.getMessage());
		}

		return learningCurve;
	}
}
