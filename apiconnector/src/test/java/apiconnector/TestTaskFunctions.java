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

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.junit.Test;
import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.algorithms.TaskInformation;
import org.openml.apiconnector.io.ApiException;
import org.openml.apiconnector.io.HttpConnector;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.xml.DataQuality;
import org.openml.apiconnector.xml.Task;
import org.openml.apiconnector.xml.TaskInputs;
import org.openml.apiconnector.xml.Tasks;
import org.openml.apiconnector.xml.TaskInputs.Input;

public class TestTaskFunctions {

	private static final String url = "https://test.openml.org/";
	private static final OpenmlConnector client_write = new OpenmlConnector(url, "8baa83ecddfe44b561fd3d92442e3319");
	private static final OpenmlConnector client_read = new OpenmlConnector(url, "c1994bdb7ecb3c6f3c8f3b35f4b47f1f");

	private static final Integer taskId = 1;
	private static final Random random = new Random(System.currentTimeMillis());

	@Test
	public void testApiAdditional() throws Exception {
		Task t = client_read.taskGet(taskId);

		TaskInputs ti = client_read.taskInputs(taskId);
		assertTrue(ti.getInputsAsMap().size() > 2);

		URL splitsUrl = TaskInformation.getEstimationProcedure(t).getData_splits_url();

		Integer dataId = TaskInformation.getSourceData(t).getData_set_id();
		File splitsFile = HttpConnector.getFileFromUrl(splitsUrl, false, "arff");
		String[] splits = Conversion.fileToString(splitsFile).split("\n");
		DataQuality dq = client_read.dataQualities(dataId);
		int numInstances = dq.getQualitiesMap().get("NumberOfInstances").intValue();

		assertTrue(splits.length > numInstances); // basic check

	}

	@Test(expected = ApiException.class)
	public void testTaskCreationNoInputs() throws Exception {
		Input estimation_procedure = new Input("estimation_procedure", "1");
		Input data_set = new Input("source_data", "1");
		Input target_feature = new Input("target_feature", "class");
		Input[] inputs = { estimation_procedure, data_set, target_feature };
		File taskFile = TestDataFunctionality.inputsToTaskFile(inputs, 1);

		client_write.taskUpload(taskFile);
	}

	@Test(expected = ApiException.class)
	public void testTaskCreationIllegalValues() throws Exception {
		Input estimation_procedure = new Input("estimation_procedure", "15");
		Input data_set = new Input("illegal_source_data", "-1");
		Input target_feature = new Input("target_feature", "class");
		Input[] inputs = { estimation_procedure, data_set, target_feature };
		File taskFile = TestDataFunctionality.inputsToTaskFile(inputs, 4);

		client_write.taskUpload(taskFile);
	}

	@Test(expected = ApiException.class)
	public void testTaskCreationDuplicateValue() throws Exception {
		Input estimation_procedure = new Input("estimation_procedure", "15");
		Input data_set = new Input("source_data", "1");
		Input data_set2 = new Input("source_data", "1");
		Input target_feature = new Input("target_feature", "class");
		Input[] inputs = { estimation_procedure, data_set, target_feature, data_set2 };
		File taskFile = TestDataFunctionality.inputsToTaskFile(inputs, 4);

		client_write.taskUpload(taskFile);
	}

	@Test(expected = ApiException.class)
	public void testTaskCreationNotRequiredValues() throws Exception {
		Input estimation_procedure = new Input("estimation_procedure", "15");
		Input target_feature = new Input("target_feature", "class");
		Input[] inputs = { estimation_procedure, target_feature };
		File taskFile = TestDataFunctionality.inputsToTaskFile(inputs, 4);

		client_write.taskUpload(taskFile);
	}

	@Test(expected = ApiException.class)
	public void testTaskAlreadyExists() throws Exception {
		// try to create it twice, as it might not exists yet the first time. 
		for (int i = 0; i < 2; ++i) {
			Input estimation_procedure = new Input("estimation_procedure", "1");
			Input data_set = new Input("source_data", "1");
			Input target_feature = new Input("target_feature", "class");
			Input[] inputs = { estimation_procedure, data_set, target_feature };
			File taskFile = TestDataFunctionality.inputsToTaskFile(inputs, 1);
			client_write.taskUpload(taskFile);
		}
	}
	
	@Test
	public void testCreateTask() throws Exception {
		// try to create it twice, as it might not exists yet the first time. 
		int randomInt = random.nextInt();
		client_write.setVerboseLevel(1);
		// make sure that the task does not exists yet (so no error)
		Input estimation_procedure = new Input("estimation_procedure", "1");
		Input data_set = new Input("source_data", "1");
		Input target_feature = new Input("target_feature", "class_" + randomInt);
		Input evaluation_measure = new Input("evaluation_measures", "predictive_accuracy");
		
		// create task object
		Input[] inputs = { estimation_procedure, data_set, target_feature, evaluation_measure};
		File taskFile = TestDataFunctionality.inputsToTaskFile(inputs, 1);
		client_write.taskUpload(taskFile);
		
		// create task similar task object (with one value less)
		Input[] inputs2 = { estimation_procedure, data_set, target_feature };
		File taskFile2 = TestDataFunctionality.inputsToTaskFile(inputs2, 1);
		client_write.taskUpload(taskFile2);
	}

	@Test
	public void testApiTaskList() throws Exception {
		client_read.setVerboseLevel(1);
		Map<String, String> filters = new HashMap<String, String>();
		filters.put("tag", "study_14");
		Tasks tasks = client_read.taskList(filters);
		assertTrue(tasks.getTask().length > 20);
		for (org.openml.apiconnector.xml.Tasks.Task t : tasks.getTask()) {
			// assertTrue(t.getQualities().length > 5);
			assertTrue(t.getInputs().length > 2);
		}
	}

}
