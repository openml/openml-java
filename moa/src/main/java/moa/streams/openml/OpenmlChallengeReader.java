package moa.streams.openml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.algorithms.TaskInformation;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.xml.DataFeature.Feature;
import org.openml.apiconnector.xml.Task;
import org.openml.apiconnector.xml.Task.Input.Stream_schedule;
import org.openml.moa.algorithm.GeneralHelpers;

import com.github.javacliparser.IntOption;
import com.yahoo.labs.samoa.instances.Instances;
import com.yahoo.labs.samoa.instances.InstancesHeader;

import moa.core.InstanceExample;
import moa.core.ObjectRepository;
import moa.options.AbstractOptionHandler;
import moa.streams.InstanceStream;
import moa.tasks.TaskMonitor;

/**
 * Stream reader of ARFF files from OpenML.
 *
 * @author Jan N. van Rijn (j.n.van.rijn@liacs.leidenuniv.nl)
 * @version $Revision: 2 $
 */
public class OpenmlChallengeReader extends AbstractOptionHandler implements InstanceStream {

	@Override
	public String getPurposeString() {
		return "Downloads a challenge from OpenML";
	}

	private static final long serialVersionUID = 1L;

	public IntOption openmlTaskIdOption = new IntOption("taskId", 't', "The OpenML task that will be performed.", 1, 1,
			Integer.MAX_VALUE);

	public IntOption instanceRandomSeedOption = new IntOption("instanceRandomSeed", 'i',
			"Note that this option will be ignored. It is necessary to make the stream compatible with  MOA tasks. ",
			1);

	protected Instances instances;

	protected Task openmlTask;
	
	protected int classidx;

	protected final OpenmlConnector apiconnector;
	
	protected List<String> attributeNames;

	protected Reader fileReader;

	protected int numTrainInstancesRead;

	protected int numTestInstancesRead;
	
	protected int initialBatchSize;
	
	protected int batchSize;
	
	protected int numInstancesTotal;
	
	private String trainURL;
	
	private String testURL;
	
	private String classname;
	
	private boolean trainStream;

	public OpenmlChallengeReader(OpenmlConnector apiconnector, int taskId) {
		this.apiconnector = apiconnector;
		this.openmlTaskIdOption.setValue(taskId);

		restart();
	}

	@Override
	protected void prepareForUseImpl(TaskMonitor monitor, ObjectRepository repository) {
		restart();
	}

	@Override
	public long estimatedRemainingInstances() { // TODO check
		return numInstancesTotal - numTrainInstancesRead;
	}

	@Override
	public InstancesHeader getHeader() {
		return new InstancesHeader(this.instances);
	}

	@Override
	public boolean hasMoreInstances() {
		return numTestInstancesRead < numInstancesTotal - initialBatchSize;
	}

	@Override
	public boolean isRestartable() {
		return true;
	}

	@Override
	public InstanceExample nextInstance() {
		try {
			return readNextInstanceFromFile();
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}
	
	public List<String> getAttributeNames() {
		return attributeNames;
	}
	
	public int getBatchSize() {
		return batchSize;
	}
	
	public int getInitialBatchSize() {
		return initialBatchSize;
	}

	@Override
	public void restart() {
		try {
			if (fileReader != null) {
				fileReader.close();
			}
			openmlTask = apiconnector.taskGet(this.openmlTaskIdOption.getValue());
			int openmlDatasetId = TaskInformation.getSourceData(openmlTask).getData_set_id();
			attributeNames = new ArrayList<String>();
			
			if (openmlTask.getTask_type_id() != 9) {
				throw new RuntimeException("Can only perform tasks of task type id 9 (Streaming Challenge).");
			}

			Map<String, String> qualities = apiconnector.dataQualities(openmlDatasetId).getQualitiesMap();
			Map<String, Feature> features = GeneralHelpers.featuresToFeatureMap(apiconnector.dataFeatures(openmlDatasetId));
			classname = TaskInformation.getSourceData(openmlTask).getTarget_feature();
			classidx = features.get(classname).getIndex() + 1; // TODO: +1 to bypass SAMOA bug
			Stream_schedule schedule = TaskInformation.getStreamSchedule(openmlTask);
			
			trainURL = schedule.getTrain_url().toString();
			testURL = schedule.getTest_url().toString();
			initialBatchSize = schedule.getInitial_batch_size();
			batchSize = schedule.getBatch_size();
			trainStream = true;
			
			numInstancesTotal = (int) Double.parseDouble(qualities.get("NumberOfInstances"));
			fileReader = new BufferedReader(new InputStreamReader(new URL(trainURL.replace("start", "0").replace("size", "" + initialBatchSize)).openStream()));
			instances = new Instances(this.fileReader, 1, classidx); 
			
			for (int i = 0; i < instances.numAttributes(); ++i) {
				attributeNames.add(instances.attribute(i).name());
			}

			this.numTrainInstancesRead = 0;
			this.numTestInstancesRead = 0;
		} catch (Exception ioe) {
			throw new RuntimeException("ArffFileStream restart failed: " + ioe.getMessage(), ioe);
		}
	}

	protected InstanceExample readNextInstanceFromFile() throws InterruptedException {
		try {
			// stream logistics
			if (trainStream) {
				if (numTrainInstancesRead >= initialBatchSize) {
					if ((numTrainInstancesRead - initialBatchSize) % batchSize == 0) {
						trainStream = false;
						URL url = new URL(testURL.replace("start", numTrainInstancesRead + "").replace("size", "" + batchSize));
						openStreamCautious(url);
					}
				}
			} else {
				if (numTestInstancesRead % batchSize == 0) {
					trainStream = true;
					URL url = new URL(trainURL.replace("start", numTrainInstancesRead + "").replace("size", "" + batchSize));
					openStreamCautious(url);
				}
			}
			
			this.instances.readInstance(fileReader);
			InstanceExample lastInstanceRead = new InstanceExample(instances.instance(0));
			this.instances.delete(); // keep instances clean
			if (trainStream) {
				this.numTrainInstancesRead++;
				//Conversion.log("OK", "Train Instance", "Number: " + numTrainInstancesRead);
			} else {
				this.numTestInstancesRead++;
				//Conversion.log("OK", "Test Instance", "Number: " + numTestInstancesRead);
			}
			return lastInstanceRead;
		} catch (IOException ioe) {
			throw new RuntimeException("ArffFileStream failed to read instance from stream.", ioe);
		}
	}
	
	public void openStreamCautious(URL urlToOpen) throws InterruptedException {
		instances = null;
		fileReader = null;
		Conversion.log("OK", "Obtain", "Batch URL: " + urlToOpen);
		while (instances == null) {
			try {
				fileReader = new BufferedReader(new InputStreamReader(urlToOpen.openStream()));
				instances = new Instances(this.fileReader, 1, classidx);
				
				// TODO: somehow, file reader recovers from an illegal arff structure. we need to catch this
				if (instances.numAttributes() == 0) {
					Conversion.log("OK", "Obtain", "New Training batch not yet available, try again in 10s");
					Thread.sleep(10000);
					instances = null;
				}
			} catch(Exception e) {
				Conversion.log("OK", "Obtain", "New Training batch not yet available, try again in 10s");
				Thread.sleep(10000);
			}
		} 
	}

	@Override
	public void getDescription(StringBuilder sb, int indent) {
		// TODO Auto-generated method stub
	}

}
