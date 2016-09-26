package moa.streams.openml;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openml.apiconnector.algorithms.TaskInformation;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.xml.DataFeature.Feature;
import org.openml.apiconnector.xml.DataSetDescription;
import org.openml.apiconnector.xml.Task;
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
public class OpenmlTaskReader extends AbstractOptionHandler implements InstanceStream {

	@Override
	public String getPurposeString() {
		return "Downloads a stream from OpenML";
	}

	private static final long serialVersionUID = 1L;

	public IntOption openmlTaskIdOption = new IntOption("taskId", 't', "The OpenML task that will be performed.", 1, 1,
			Integer.MAX_VALUE);

	public IntOption instanceRandomSeedOption = new IntOption("instanceRandomSeed", 'i',
			"Note that this option will be ignored. It is necessary to make the stream compatible with  MOA tasks. ",
			1);

	protected Instances instances;

	protected Reader fileReader;

	protected boolean hitEndOfFile;

	protected InstanceExample lastInstanceRead;

	protected int numInstancesRead;

	protected int numInstancesTotal;

	protected Task openmlTask;

	protected Integer openmlDatasetId;

	protected final OpenmlConnector apiconnector;
	
	protected List<String> attributeNames;

	public OpenmlTaskReader(OpenmlConnector apiconnector, int taskId) {
		this.apiconnector = apiconnector;
		this.openmlTaskIdOption.setValue(taskId);

		restart();
	}

	@Override
	protected void prepareForUseImpl(TaskMonitor monitor, ObjectRepository repository) {
		restart();
	}

	@Override
	public long estimatedRemainingInstances() {
		return numInstancesTotal - numInstancesRead;
	}

	@Override
	public InstancesHeader getHeader() {
		return new InstancesHeader(this.instances);
	}

	@Override
	public boolean hasMoreInstances() {
		return !hitEndOfFile;
	}

	@Override
	public boolean isRestartable() {
		return true;
	}

	@Override
	public InstanceExample nextInstance() {
		InstanceExample prevInstance = this.lastInstanceRead;
		this.hitEndOfFile = !readNextInstanceFromFile();
		return prevInstance;
	}
	
	public List<String> getAttributeNames() {
		return attributeNames;
	}

	@Override
	public void restart() {
		try {
			if (fileReader != null) {
				fileReader.close();
			}
			openmlTask = apiconnector.taskGet(this.openmlTaskIdOption.getValue());
			openmlDatasetId = TaskInformation.getSourceData(openmlTask).getData_set_id();
			attributeNames = new ArrayList<String>();
			
			if (openmlTask.getTask_type_id() != 4) {
				throw new RuntimeException(
						"Can only perform tasks of task type id 4 (Supervised Data Stream Classification).");
			}

			DataSetDescription dsd = apiconnector.dataGet(openmlDatasetId);
			Map<String, String> qualities = apiconnector.dataQualities(openmlDatasetId).getQualitiesMap();
			Map<String, Feature> features = GeneralHelpers.featuresToFeatureMap(apiconnector.dataFeatures(openmlDatasetId));
			String classname = TaskInformation.getSourceData(openmlTask).getTarget_feature();

			InputStream fileStream = new FileInputStream(dsd.getDataset(apiconnector.getApiKey()));

			numInstancesTotal = (int) Double.parseDouble(qualities.get("NumberOfInstances"));
			fileReader = new BufferedReader(new InputStreamReader(fileStream));
			instances = new Instances(this.fileReader, 1, features.get(classname).getIndex() + 1); // TODO: +1 to bypass SAMOA bug
			
			for (int i = 0; i < instances.numAttributes(); ++i) {
				attributeNames.add(instances.attribute(i).name());
			}

			this.numInstancesRead = 0;
			this.lastInstanceRead = null;
			this.hitEndOfFile = !readNextInstanceFromFile();
		} catch (Exception ioe) {
			throw new RuntimeException("ArffFileStream restart failed: " + ioe.getMessage(), ioe);
		}
	}

	protected boolean readNextInstanceFromFile() {
		try {
			if (this.instances.readInstance(fileReader)) {
				this.lastInstanceRead = new InstanceExample(instances.instance(0));
				this.instances.delete(); // keep instances clean
				this.numInstancesRead++;
				return true;
			}
			if (this.fileReader != null) {
				this.fileReader.close();
				this.fileReader = null;
			}
			return false;
		} catch (IOException ioe) {
			throw new RuntimeException("ArffFileStream failed to read instance from stream.", ioe);
		}
	}

	@Override
	public void getDescription(StringBuilder sb, int indent) {
		// TODO Auto-generated method stub
	}

}
