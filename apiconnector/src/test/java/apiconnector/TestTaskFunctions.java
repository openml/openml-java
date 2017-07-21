package apiconnector;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;

import org.junit.Test;
import org.openml.apiconnector.algorithms.TaskInformation;
import org.openml.apiconnector.io.ApiException;
import org.openml.apiconnector.io.HttpConnector;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.xml.DataQuality;
import org.openml.apiconnector.xml.Task;
import org.openml.apiconnector.xml.Tasks;
import org.openml.apiconnector.xml.Task_new.Input;

public class TestTaskFunctions {


	private static final String url = "https://test.openml.org/";
	private static final OpenmlConnector client_write = new OpenmlConnector(url, "8baa83ecddfe44b561fd3d92442e3319");
	private static final OpenmlConnector client_read = new OpenmlConnector(url, "c1994bdb7ecb3c6f3c8f3b35f4b47f1f");
	
	private static final Integer taskId = 1;
	
	
	@Test
	public void testApiAdditional() throws Exception {
		Task t = client_read.taskGet(taskId);
		
		String splitsUrl = TaskInformation.getEstimationProcedure(t).getData_splits_url();
		
		
		Integer dataId = TaskInformation.getSourceData(t).getData_set_id();
		String[] splits = HttpConnector.getStringFromUrl(new URL(splitsUrl), false).split("\n");
		DataQuality dq = client_read.dataQualities(dataId);
		int numInstances = (int) Double.parseDouble(dq.getQualitiesMap().get("NumberOfInstances"));
		
		assertTrue(splits.length > numInstances); // basic check

	}

	@Test(expected=ApiException.class)
	public void testTaskCreationIllegalValues() throws Exception {
		Input estimation_procedure = new Input("estimation_procedure", "15"); 
		Input data_set = new Input("illegal_source_data", "-1");
		Input target_feature = new Input("target_feature", "class");
		Input[] inputs = {estimation_procedure, data_set, target_feature };
		File taskFile = TestDataFunctionality.inputsToTaskFile(inputs, 4);
		
		client_write.taskUpload(taskFile);
	}

	@Test(expected=ApiException.class)
	public void testTaskCreationDuplicateValue() throws Exception {
		Input estimation_procedure = new Input("estimation_procedure", "15"); 
		Input data_set = new Input("source_data", "1");
		Input data_set2 = new Input("source_data", "1");
		Input target_feature = new Input("target_feature", "class");
		Input[] inputs = {estimation_procedure, data_set, target_feature, data_set2};
		File taskFile = TestDataFunctionality.inputsToTaskFile(inputs, 4);
		
		client_write.taskUpload( taskFile );
	}

	@Test(expected=ApiException.class)
	public void testTaskCreationNotRequiredValues() throws Exception {
		Input estimation_procedure = new Input("estimation_procedure", "15"); 
		Input target_feature = new Input("target_feature", "class");
		Input[] inputs = {estimation_procedure, target_feature};
		File taskFile = TestDataFunctionality.inputsToTaskFile(inputs, 4);
		
		client_write.taskUpload( taskFile );
	}
	
	
	@Test
	public void testApiTaskList() throws Exception {
		Tasks tasks = client_read.taskList("study_14");
		assertTrue(tasks.getTask().length > 20);
		for (org.openml.apiconnector.xml.Tasks.Task t : tasks.getTask()) {
			assertTrue(t.getQualities().length > 5);
			assertTrue(t.getInputs().length > 2);
		}
	}
	
}
