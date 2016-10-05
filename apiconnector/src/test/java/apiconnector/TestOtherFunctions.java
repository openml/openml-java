package apiconnector;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.openml.apiconnector.algorithms.TaskInformation;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.xml.DataQuality;
import org.openml.apiconnector.xml.Task;
import org.openml.apiconnector.xml.Tasks;

public class TestOtherFunctions {


	private static final String url = "http://test.openml.org/";
	private static final String session_hash = "d488d8afd93b32331cf6ea9d7003d4c3";
	private static final OpenmlConnector client = new OpenmlConnector(url,session_hash);
	private static final Integer taskId = 1;
	
	
	@Test
	public void testApiAdditional() {
		try {
			Task t = client.taskGet(taskId);
			
			String splitsUrl = TaskInformation.getEstimationProcedure(t).getData_splits_url();
			
			
			Integer dataId = TaskInformation.getSourceData(t).getData_set_id();
			String[] splits = OpenmlConnector.getStringFromUrl(splitsUrl).split("\n");
			DataQuality dq = client.dataQualities(dataId);
			int numInstances = (int) Double.parseDouble(dq.getQualitiesMap().get("NumberOfInstances"));
			
			assertTrue(splits.length > numInstances); // basic check
		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed: " + e.getMessage());
		}
	}

	@Test
	public void testApiTaskList() {
		try {
			Tasks tasks = client.taskList("study_1");
			assertTrue(tasks.getTask().length > 20);
			for (org.openml.apiconnector.xml.Tasks.Task t : tasks.getTask()) {
				assertTrue(t.getQualities().length > 5);
				assertTrue(t.getInputs().length > 2);
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed: " + e.getMessage());
		}
	}
	
}
