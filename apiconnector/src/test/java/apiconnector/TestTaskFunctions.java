/*******************************************************************************
 * Copyright (C) 2017, Jan N. van Rijn <j.n.van.rijn@liacs.leidenuniv.nl>
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the copyright holder nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package apiconnector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.json.JSONArray;
import org.junit.Test;
import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.algorithms.TaskInformation;
import org.openml.apiconnector.io.ApiException;
import org.openml.apiconnector.io.HttpConnector;
import org.openml.apiconnector.xml.DataQuality;
import org.openml.apiconnector.xml.DataSetDescription;
import org.openml.apiconnector.xml.Task;
import org.openml.apiconnector.xml.TaskInputs;
import org.openml.apiconnector.xml.Tasks;
import org.openml.apiconnector.xml.TaskInputs.Input;

import testbase.BaseTestFramework;

public class TestTaskFunctions extends BaseTestFramework {

	public static final Integer[] TASK_ILLEGAL_INPUT_CODES = {621, 622};
	
	private static final Integer taskId = 1;

	@Test
	public void testApiAdditional() throws Exception {
		Task t = client_read_test.taskGet(taskId);
		
		int epId = TaskInformation.getEstimationProcedure(t).getId();
		client_read_test.estimationProcedureGet(epId);

		TaskInputs ti = client_read_test.taskInputs(taskId);
		assertTrue(ti.getInputsAsMap().size() > 2);

		URL splitsUrl = TaskInformation.getEstimationProcedure(t).getData_splits_url();

		Integer dataId = TaskInformation.getSourceData(t).getData_set_id();
		File splitsFile = HttpConnector.getTempFileFromUrl(splitsUrl, "arff");
		String[] splits = Conversion.fileToString(splitsFile).split("\n");
		DataQuality dq = client_read_test.dataQualities(dataId, null);
		int numInstances = dq.getQualitiesMap().get("NumberOfInstances").intValue();

		assertTrue(splits.length > numInstances); // basic check

	}

	@Test(expected = ApiException.class)
	public void testTaskCreationNoInputs() throws Exception {
		Input estimation_procedure = new Input("estimation_procedure", "1");
		Input data_set = new Input("source_data", "1");
		Input target_feature = new Input("target_feature", "class");
		Input[] inputs = { estimation_procedure, data_set, target_feature };

		client_write_test.taskUpload(new TaskInputs(null, 1, inputs, null));
	}

	@Test(expected = ApiException.class)
	public void testTaskCreationIllegalValues() throws Exception {
		Input estimation_procedure = new Input("estimation_procedure", "15");
		Input data_set = new Input("illegal_source_data", "-1");
		Input target_feature = new Input("target_feature", "class");
		Input[] inputs = { estimation_procedure, data_set, target_feature };

		client_write_test.taskUpload(new TaskInputs(null, 4, inputs, null));
	}

	@Test(expected = ApiException.class)
	public void testTaskCreationDuplicateValue() throws Exception {
		Input estimation_procedure = new Input("estimation_procedure", "15");
		Input data_set = new Input("source_data", "1");
		Input data_set2 = new Input("source_data", "1");
		Input target_feature = new Input("target_feature", "class");
		Input[] inputs = { estimation_procedure, data_set, target_feature, data_set2 };

		client_write_test.taskUpload(new TaskInputs(null, 4, inputs, null));
	}

	@Test(expected = ApiException.class)
	public void testTaskCreationNotRequiredValues() throws Exception {
		Input estimation_procedure = new Input("estimation_procedure", "15");
		Input target_feature = new Input("target_feature", "class");
		Input[] inputs = { estimation_procedure, target_feature };

		client_write_test.taskUpload(new TaskInputs(null, 4, inputs, null));
	}

	@Test(expected = ApiException.class)
	public void testTaskAlreadyExists() throws Exception {
		// try to create it twice, as it might not exists yet the first time. 
		for (int i = 0; i < 2; ++i) {
			Input estimation_procedure = new Input("estimation_procedure", "1");
			Input data_set = new Input("source_data", "1");
			Input target_feature = new Input("target_feature", "class");
			Input[] inputs = { estimation_procedure, data_set, target_feature };
			client_write_test.taskUpload(new TaskInputs(null, 1, inputs, null));
		}
	}
	
	@Test
	public void testCreateTaskIllegalValues() throws Exception {
		Random random = new Random();
		
		Input[] inputs = new Input[4];
		inputs[0] = new Input("estimation_procedure", "1");
		inputs[1] = new Input("source_data", "2");
		inputs[2] = new Input("target_feature", "class");
		inputs[3] = new Input("evaluation_measures", "predictive_accuracy");
		
		for (int i = 0; i < 4; ++i) {
			Input[] derived = Arrays.copyOf(inputs, inputs.length);
			derived[i] = new Input(derived[i].getName(), derived[i].getValue() + "_" + random.nextInt());
			try {
				client_write_test.taskUpload(new TaskInputs(null, 1, derived, null));
				// previous statement should not terminate without ApiException.
				throw new Exception("Tasks did not get blocked by Api");
			} catch(ApiException e) {
				assertTrue(Arrays.asList(TASK_ILLEGAL_INPUT_CODES).contains(e.getCode()));
			}
		}
	}
	
	@Test
	public void testAvoidInactiveDataset() throws Exception {
		File toUpload = new File(TestDataFunctionality.data_file);
		DataSetDescription dsd = new DataSetDescription("test", "Unit test should be deleted", "arff", "class");
		int dataId = client_write_test.dataUpload(dsd, toUpload);
		
		Input[] inputs = new Input[3];
		inputs[0] = new Input("estimation_procedure", "1");
		inputs[1] = new Input("source_data", "" + dataId);
		inputs[2] = new Input("target_feature", "class");
		
		try {
			client_write_test.taskUpload(new TaskInputs(null, 6, inputs, null));
			fail("Should not be able to upload task.");
		} catch(Exception e) { }
	}
	
	@Test
	public void testCreateChallengeTask() throws Exception {
		Input[] inputs = new Input[4];
		inputs[0] = new Input("estimation_procedure", "18");
		inputs[1] = new Input("source_data", "128");
		inputs[2] = new Input("source_data_labeled", "129");
		inputs[3] = new Input("target_feature", "class");
		
		int taskId = 0;
		try {
			taskId = client_write_test.taskUpload(new TaskInputs(null, 6, inputs, null));
		} catch (ApiException e) {
			taskId = TaskInformation.getTaskIdsFromErrorMessage(e)[0];
			throw e;
		} finally {
			client_write_test.taskDelete(taskId);
		}
	}
	
	@Test
	public void testCreateTaskWithCostMatrix() throws Exception {
		JSONArray costMatrixOrig = new JSONArray("[[0, 1], [10, 0]]");
		
		Input[] inputs = new Input[4];
		inputs[0] = new Input("estimation_procedure", "1");
		inputs[1] = new Input("source_data", "2");
		inputs[2] = new Input("target_feature", "class");
		inputs[3] = new Input("cost_matrix", costMatrixOrig.toString());
		
		int uploadId = 0;
		try {
			uploadId = client_write_test.taskUpload(new TaskInputs(null, 1, inputs, null));
			Task downloaded = client_read_test.taskGet(uploadId);
			JSONArray costMatrixDownloaded = TaskInformation.getCostMatrix(downloaded);
			assertEquals(costMatrixOrig.toString(), costMatrixDownloaded.toString());
		} catch (ApiException e) {
			uploadId = TaskInformation.getTaskIdsFromErrorMessage(e)[0];
			throw e;
		} finally {
			client_write_test.taskDelete(uploadId);
		}
	}
	
	@Test(expected = ApiException.class)
	public void testCreateClassificationTaskNumericTarget() throws Exception {
		Input[] inputs = new Input[3];
		inputs[0] = new Input("estimation_procedure", "1");
		inputs[1] = new Input("source_data", "1");
		inputs[2] = new Input("target_feature", "carbon");
		
		client_write_test.taskUpload(new TaskInputs(null, 1, inputs, null));
	}
	
	@Test(expected = ApiException.class)
	public void testCreateRegressionTaskNominalTarget() throws Exception {
		Input[] inputs = new Input[4];
		inputs[0] = new Input("estimation_procedure", "7");
		inputs[1] = new Input("source_data", "1");
		inputs[2] = new Input("target_feature", "class");
		
		client_write_test.taskUpload(new TaskInputs(null, 2, inputs, null));
	}
	
	@Test
	public void testCreateTask() throws Exception {
		Integer uploadId1 = null;
		Integer uploadId2 = null;
		
		Input estimation_procedure = new Input("estimation_procedure", "1");
		Input data_set = new Input("source_data", "2");
		Input target_feature = new Input("target_feature", "bkblk"); // some random attribute that is unlikely to have tasks
		Input evaluation_measure = new Input("evaluation_measures", "predictive_accuracy");
		
		try {
			// create task object
			Input[] inputs = { estimation_procedure, data_set, target_feature, evaluation_measure};
			try {
				// try catch for deleting tasks that were already on the server
				uploadId1 = client_write_test.taskUpload(new TaskInputs(null, 1, inputs, null));
			} catch(ApiException e) {
				uploadId1 = TaskInformation.getTaskIdsFromErrorMessage(e)[0];
				throw e;
			}
			
			// create task similar task object (with one value less)
			Input[] inputs2 = { estimation_procedure, data_set, target_feature };
			try {
				// try catch for deleting tasks that were already on the server
				uploadId2 = client_write_test.taskUpload(new TaskInputs(null, 1, inputs2, null));
			} catch(ApiException e) {
				uploadId2 = TaskInformation.getTaskIdsFromErrorMessage(e)[0];
				throw e;
			}
		} finally {
			// make sure that the task does not exists anymore
			if (uploadId1 != null) { try { client_write_test.taskDelete(uploadId1); } catch(ApiException a) {} }
			if (uploadId2 != null) { try { client_write_test.taskDelete(uploadId2); } catch(ApiException a) {} }
		}
	}

	@Test
	public void testApiTaskList() throws Exception {
		Map<String, String> filters = new HashMap<String, String>();
		filters.put("type", "1");
		filters.put("limit", "100");
		Tasks tasks = client_read_test.taskList(filters);
		assertTrue(tasks.getTask().length == 100);
		for (org.openml.apiconnector.xml.Tasks.Task t : tasks.getTask()) {
			// assertTrue(t.getQualities().length > 5);
			assertTrue(t.getInputs().length > 2);
		}
	}
}
