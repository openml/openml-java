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
package org.openml.apiconnector.io;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.algorithms.TaskInformation;
import org.openml.apiconnector.settings.Constants;
import org.openml.apiconnector.settings.Settings;
import org.openml.apiconnector.xml.*;
import org.openml.apiconnector.xml.Task.Input.Estimation_procedure;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.zip.DataFormatException;

public class OpenmlBasicConnector implements Serializable {
	private static final long serialVersionUID = 7362620508675762264L;

	/**
	 * When set to true, API will output information.
	 */
	protected int verboseLevel = 0;
	
	protected String api_key = null;
	
	protected String OPENML_URL = Settings.BASE_URL;
	
	protected String API_PART = "api_new/v1/";
	
	/**
	 * @return Return the api key that is used to authenticate with
	 */
	public String getApiKey() {
		return api_key;
	}

	/**
	 * Updates the api key
	 * 
	 * @param api_key - the api key
	 */
	public void setApiKey(String api_key) { 
		this.api_key = api_key;
	}
	
	/**
	 * Sets the verbose level.
	 * 
	 * @param level - higher means more output. 
	 * 			0 = none, 1 = communication with server, 2 = also files
	 */
	public void setVerboseLevel(int level) {
		verboseLevel = level;
	}

	
	/**
	 * Returns the verbose level.
	 * 
	 * @return the verbose level
	 */
	public int getVerboselevel() {
		return verboseLevel;
	}

	/**
	 * Returns the total API URL. 
	 * 
	 * @return api url (server plus api part)
	 */
	public String getApiUrl(){
		return OPENML_URL + API_PART;
	}


	/**
	 * Returns an XSD file
	 * 
	 * @param name - the file name of the xsd (e.g., openml.data.upload)
	 * @return file - textual file with XSD content
	 * @throws IOException - problem downloading or storing the file
	 */
	public File getXSD(String name) throws IOException {
		File file = File.createTempFile("name", "xsd");
		file.deleteOnExit();
		URL url = new URL(getApiUrl() + "xsd/" + name + "?api_key=" + getApiKey());
		FileUtils.copyURLToFile(url, file);
		return file;
	}

	/**
	 * Retrieves the description of a specified data set. Uses cache, if allowed. 
	 * 
	 * @param did - The data_id of the data description to download.
	 * @return DataSetDescription - An object containing the description of the data
	 * @throws Exception - Can be: IOException (problem with connection, server),
	 *                   ApiException (contains error code, see OpenML
	 *                   documentation)
	 */
	public DataSetDescription dataGet(int did) throws Exception {
		URL request = new URL(OPENML_URL + API_PART + "data/" + did);
		String cacheSuffix = "datasets/" + did + "/description.xml";
		Object apiResult = HttpCacheController.doApiGetRequest(request, cacheSuffix, getApiKey(), verboseLevel);
		return (DataSetDescription) apiResult;
	}

	/**
	 * Retrieves a dataset in ARFF format. Uses cache, if allowed. 
	 * 
	 * @param dsd - Description of the Dataset
	 * @return File - dataset in arff format
	 * @throws Exception - Can be: IOException (problem with connection, server),
	 *                   ApiException (contains error code, see OpenML
	 *                   documentation)
	 */
	public File datasetGet(DataSetDescription dsd) throws Exception {
		String cacheSuffix = "datasets/" + dsd.getId() + "/dataset.arff";
		URL fileUrl = getOpenmlFileUrl(dsd.getFile_id(), dsd.getName());
		return HttpCacheController.getCachedFileFromUrl(fileUrl, cacheSuffix);
	}
	
	/**
	 * Retrieves a dataset in CSV format. Uses cache, if allowed. 
	 * 
	 * @param dsd - Description of the Dataset
	 * @return File - dataset in CSV format
	 * @throws Exception - Can be: IOException (problem with connection, server),
	 *                   ApiException (contains error code, see OpenML
	 *                   documentation)
	 */
	public File datasetGetCsv(DataSetDescription dsd) throws Exception {
		String cacheSuffix = "datasets/" + dsd.getId() + "/dataset.csv";
		URL fileUrl = getOpenmlFileUrl(dsd.getFile_id(), dsd.getName(), "get_csv");
		return HttpCacheController.getCachedFileFromUrl(fileUrl, cacheSuffix);
	}
	
	protected UploadDataSet dataUpload(File description, File dataset) throws Exception {
		MultipartEntity params = new MultipartEntity();
		params.addPart("description", new FileBody(description));
		if (dataset != null) {
			params.addPart("dataset", new FileBody(dataset));
		}
		URL request = new URL(OPENML_URL + API_PART + "data/");
		Object apiResult = HttpConnector.doApiPostRequest(request, params, getApiKey(), verboseLevel);
		return (UploadDataSet) apiResult;
	}
	
	/**
	 * Deletes a dataset from the server
	 * 
	 * @param did - The data id to be deleted
	 * @return An object with the integer id of the deleted dataset
	 * @throws Exception - Can be: IOException (problem with connection, server),
	 *                   ApiException (contains error code, see OpenML
	 *                   documentation)
	 */
	public DataDelete dataDelete(int did) throws Exception {
		URL request = new URL(OPENML_URL + API_PART + "data/" + did);
		Object apiResult = HttpConnector.doApiDeleteRequest(request, getApiKey(), verboseLevel);
		return (DataDelete) apiResult;
	}
	
	/**
	 * Resets a dataset (removes features, qualities, etc)
	 * 
	 * @param did - The data id to reset
	 * @return An object with the integer id of the resetted dataset
	 * @throws Exception - Can be: IOException (problem with connection, server),
	 *                   ApiException (contains error code, see OpenML
	 *                   documentation)
	 */
	public DataReset dataReset(int did) throws Exception {
		URL request = new URL(OPENML_URL + API_PART + "data/reset/" + did);
		MultipartEntity params = new MultipartEntity();
		Object apiResult = HttpConnector.doApiPostRequest(request, params, getApiKey(), verboseLevel);
		return (DataReset) apiResult;
	}
	
	/**
	 * Updates the status of a dataset (requires admin rights)
	 * 
	 * @param did - The data id to reset
	 * @param status - The new status (active, deactivated)
	 * @return An object with the integer id of the updated dataset
	 * @throws Exception - Can be: IOException (problem with connection, server),
	 *                   ApiException (contains error code, see OpenML
	 *                   documentation)
	 */
	public DataStatusUpdate dataStatusUpdate(int did, String status) throws Exception {
		MultipartEntity params = new MultipartEntity();
		params.addPart("data_id", new StringBody("" + did));
		params.addPart("status", new StringBody(status));
		
		URL request = new URL(OPENML_URL + API_PART + "data/status/update");
		Object apiResult = HttpConnector.doApiPostRequest(request, params, getApiKey(), verboseLevel);
		return (DataStatusUpdate) apiResult;
	}
	
	/**
	 * Tags a dataset
	 * 
	 * @param id - the dataset to be tagged
	 * @param tag - textual string to be added as tag
	 * @return An object with the integer id of the updated dataset
	 * @throws Exception - Can be: IOException (problem with connection, server),
	 *                   ApiException (contains error code, see OpenML
	 *                   documentation)
	 */
	public DataTag dataTag(int id, String tag) throws Exception {
		MultipartEntity params = new MultipartEntity();
		params.addPart("data_id", new StringBody("" + id));
		params.addPart("tag", new StringBody(tag));
		
		URL request = new URL(OPENML_URL + API_PART + "data/tag");
		Object apiResult = HttpConnector.doApiPostRequest(request, params, getApiKey(), verboseLevel);
		return (DataTag) apiResult;
	}
	
	/**
	 * Untags a dataset
	 * 
	 * @param id - the dataset to be tagged
	 * @param tag - textual string to be removed as tag
	 * @return An object with the integer id of the updated dataset
	 * @throws Exception - Can be: IOException (problem with connection, server),
	 *                   ApiException (contains error code, see OpenML
	 *                   documentation)
	 */
	public DataUntag dataUntag(int id, String tag) throws Exception {
		MultipartEntity params = new MultipartEntity();
		params.addPart("data_id", new StringBody("" + id));
		params.addPart("tag", new StringBody(tag));
		
		URL request = new URL(OPENML_URL + API_PART + "data/untag");
		Object apiResult = HttpConnector.doApiPostRequest(request, params, getApiKey(), verboseLevel);
		
		return (DataUntag) apiResult;
	}

	/**
	 * Retrieves the features of a specified data set. Uses cache, if allowed. 
	 * 
	 * @param did
	 *            - The id of the data features to download.
	 * @return DataFeatures - An object containing the features of the data
	 * @throws Exception - Can be: IOException (problem with connection, server),
	 *                   ApiException (contains error code, see OpenML
	 *                   documentation)
	 */
	public DataFeature dataFeatures(int did) throws Exception {
		URL request = new URL(OPENML_URL + API_PART + "data/features/" + did);
		String cacheSuffix = "datasets/" + did + "/features.xml";
		Object apiResult = HttpCacheController.doApiGetRequest(request, cacheSuffix, getApiKey(), verboseLevel);
		return (DataFeature) apiResult;
	}
	
	/**
	 * Returns a list of dataset that corresponds to a set of filters
	 * 
	 * @param filters - map of filters, see OpenML docs for an exhaustive list
	 * @return a list of dataset objects
	 * @throws Exception - Can be: IOException (problem with connection, server),
	 *                   ApiException (contains error code, see OpenML
	 *                   documentation)
	 */
	public Data dataList(Map<String, String> filters) throws Exception {
		String suffix = "";
		if (filters != null) {
			for (String filter : filters.keySet()) {
				suffix += filter + "/" + filters.get(filter) + "/";
			}
		}
		URL request = new URL(OPENML_URL + API_PART + "data/list/" + suffix);
		Object apiResult = HttpConnector.doApiGetRequest(request, getApiKey(), verboseLevel);
		return (Data) apiResult;
	}
	
	/**
	 * Retrieves the qualities (meta-features) of a specified data set.
	 * 
	 * @param did - The id of the data qualities to download.
	 * @param evalEngine - id of the evaluation engine responsible for qualities
	 * @return DataQuality - An object containing the qualities of the data
	 * @throws Exception - Can be: IOException (problem with connection, server),
	 *                   ApiException (contains error code, see OpenML
	 *                   documentation)
	 */
	public DataQuality dataQualities(int did, Integer evalEngine) throws Exception {
		String suffix = "data/qualities/" + did;
		if (evalEngine != null) {
			suffix += "/" + evalEngine;
		}
		
		URL request = new URL(OPENML_URL + API_PART + suffix);
		Object apiResult = HttpConnector.doApiGetRequest(request, getApiKey(), verboseLevel);

		return (DataQuality) apiResult;
	}
	
	protected DataFeatureUpload dataFeaturesUpload(File description) throws Exception {
		MultipartEntity params = new MultipartEntity();
		params.addPart("description", new FileBody(description));

		if (verboseLevel >= Constants.VERBOSE_LEVEL_ARFF) {
			System.out.println(Conversion.fileToString(description) + "\n==========\n");
		}

		URL request = new URL(OPENML_URL + API_PART + "data/features");
		Object apiResult = HttpConnector.doApiPostRequest(request, params, getApiKey(), verboseLevel);
		
		return (DataFeatureUpload) apiResult;
	}
	
	protected DataQualityUpload dataQualitiesUpload(File description) throws Exception {
		MultipartEntity params = new MultipartEntity();
		params.addPart("description", new FileBody(description));

		URL request = new URL(OPENML_URL + API_PART + "data/qualities");
		Object apiResult = HttpConnector.doApiPostRequest(request, params, getApiKey(), verboseLevel);
		return (DataQualityUpload) apiResult;
	}

	/**
	 * Returns a list with all available data qualities. 
	 * 
	 * @return list with qualities
	 * @throws Exception - Can be: IOException (problem with connection, server),
	 *                   ApiException (contains error code, see OpenML
	 *                   documentation)
	 */
	public DataQualityList dataQualitiesList() throws Exception {
		URL request = new URL(OPENML_URL + API_PART + "data/qualities/list");
		Object apiResult = HttpConnector.doApiGetRequest(request, getApiKey(), verboseLevel);
		return (DataQualityList) apiResult;
	}
	
	/**
	 * Returns a list of unprocessed datasets, given an evaluation engine (no 
	 * features calculated, no errors registered)
	 * 
	 * @param evaluationEngineId - the evaluation engine id
	 * @param mode - either normal, reversed or random
	 * @return list of unprocessed datasets
	 * @throws Exception - Can be: IOException (problem with connection, server),
	 *                   ApiException (contains error code, see OpenML
	 *                   documentation)
	 */
	public DataUnprocessed dataUnprocessed(int evaluationEngineId, String mode) throws Exception {
		String suffix = "data/unprocessed/" + evaluationEngineId + "/" + mode;
		URL request = new URL(OPENML_URL + API_PART + suffix);
		Object apiResult = HttpConnector.doApiGetRequest(request, getApiKey(), verboseLevel);
		return (DataUnprocessed) apiResult;
	}

	/**
	 * Returns a list of datasets that don't have all qualities calculated yet, given an 
	 * evaluation engine and set of qualities
	 * 
	 * @param evaluationEngineId - the evaluation engine id
	 * @param mode - either normal, reversed or random
	 * @param featureQualities - false for dataset qualities, true for feature qualities
	 * @param qualitiesToCalculate - list of qualities under consideration
	 * @param priorityTag - datasets with this tag get prioritized 
	 * @return list of unprocessed datasets
	 * @throws Exception - Can be: IOException (problem with connection, server),
	 *                   ApiException (contains error code, see OpenML
	 *                   documentation)
	 */
	public DataUnprocessed dataqualitiesUnprocessed(int evaluationEngineId, String mode, boolean featureQualities, List<String> qualitiesToCalculate, String priorityTag) throws Exception {
		StringBuilder sb = new StringBuilder();
		for (String s : qualitiesToCalculate) {
			sb.append("," + s);
		}
		
		MultipartEntity params = new MultipartEntity();
		params.addPart("qualities", new StringBody(sb.toString().substring(1)));
		
		String suffix = "data/qualities/unprocessed/" + evaluationEngineId + "/" + mode;
		if (featureQualities) {
			suffix += "/feature";
		}
		if (priorityTag != null) {
			suffix += "/" + priorityTag;
		}
		
		URL request = new URL(OPENML_URL + API_PART + suffix);
		Object apiResult = HttpConnector.doApiPostRequest(request, params, getApiKey(), verboseLevel);
		return (DataUnprocessed) apiResult;
	}
	
	/**
	 * Downloads the task description
	 * 
	 * @param task_id - The numeric id of the task to be obtained.
	 * @return Task - An object describing the task
	 * @throws Exception - Can be: IOException (problem with connection, server),
	 *                   ApiException (contains error code, see OpenML
	 *                   documentation)
	 */
	public Task taskGet(int task_id) throws Exception {
		URL request = new URL(OPENML_URL + API_PART + "task/" + task_id);
		String cacheSuffix = "tasks/" + task_id + "/task.xml";
		Object apiResult = HttpCacheController.doApiGetRequest(request, cacheSuffix, getApiKey(), verboseLevel);
		return (Task) apiResult;
		
	}

	/**
	 * Downloads the data splits
	 * 
	 * @param task - the task object
	 * @return a file in arff format containing data splits
	 * @throws Exception - Can be: IOException (problem with connection, server),
	 *                   ApiException (contains error code, see OpenML
	 *                   documentation)
	 */
	public File taskSplitsGet(Task task) throws Exception {
		String cacheSuffix = "tasks/" + task.getTask_id() + "/datasplits.arff";
		Estimation_procedure ep = TaskInformation.getEstimationProcedure(task);
		return HttpCacheController.getCachedFileFromUrl(ep.getData_splits_url(), cacheSuffix);
	}
	
	/**
	 * Downloads a task according to new task format
	 * 
	 * @param task_id - the task id
	 * @return task object according to new format
	 * @throws Exception - Can be: IOException (problem with connection, server),
	 *                   ApiException (contains error code, see OpenML
	 *                   documentation)
	 */
	public TaskInputs taskInputs(int task_id) throws Exception {
		URL request = new URL(OPENML_URL + API_PART + "task/inputs/" + task_id);
		Object apiResult = HttpConnector.doApiGetRequest(request, getApiKey(), verboseLevel);
		return (TaskInputs) apiResult;
	}
	
	/**
	 * Returns a list of all of the tasks given a set of filters
	 * @param filters - optional, a map of filters
	 * @return a list of tasks
	 * @throws Exception - Can be: IOException (problem with connection, server),
	 *                   ApiException (contains error code, see OpenML
	 *                   documentation)
	 */
	public Tasks taskList(Map<String, String> filters) throws Exception {
		String suffix = "task/list/";
		if (filters != null) {
			for (String filter : filters.keySet()) {
				suffix += filter + "/" + filters.get(filter) + "/";
			}
		}
		URL request = new URL(OPENML_URL + API_PART + suffix);
		Object apiResult = HttpConnector.doApiGetRequest(request, getApiKey(), verboseLevel);
		return (Tasks) apiResult;
	}
	
	/**
	 * Deletes a task
	 * 
	 * @param task_id - the task to be deleted
	 * @return an object with the information about deleted task
	 * @throws Exception - Can be: IOException (problem with connection, server),
	 *                   ApiException (contains error code, see OpenML
	 *                   documentation)
	 */
	public TaskDelete taskDelete(int task_id) throws Exception {
		URL request = new URL(OPENML_URL + API_PART + "task/" + task_id);
		Object apiResult = HttpConnector.doApiDeleteRequest(request, getApiKey(), verboseLevel);
		return (TaskDelete) apiResult;
	}
	
	protected UploadTask taskUpload(File description) throws Exception {
		MultipartEntity params = new MultipartEntity();
		params.addPart("description", new FileBody(description));
		
		URL request = new URL(OPENML_URL + API_PART + "task/");
		Object apiResult = HttpConnector.doApiPostRequest(request, params, getApiKey(), verboseLevel);
		return (UploadTask) apiResult;
	}
	
	/**
	 * Tags a task
	 * 
	 * @param id - the task id
	 * @param tag - the tag
	 * @return An object with the integer id of the updated task
	 * @throws Exception - Can be: IOException (problem with connection, server),
	 *                   ApiException (contains error code, see OpenML
	 *                   documentation)
	 */
	public TaskTag taskTag(int id, String tag) throws Exception {
		MultipartEntity params = new MultipartEntity();
		params.addPart("task_id", new StringBody("" + id));
		params.addPart("tag", new StringBody(tag));
		
		URL request = new URL(OPENML_URL + API_PART + "task/tag");
		Object apiResult = HttpConnector.doApiPostRequest(request, params, getApiKey(), verboseLevel);
		
		return (TaskTag) apiResult;
	}
	
	/**
	 * Untags a task
	 * 
	 * @param id - the task id
	 * @param tag - the tag to be removed
	 * @return n object with the integer id of the updated task
	 * @throws Exception - Can be: IOException (problem with connection, server),
	 *                   ApiException (contains error code, see OpenML
	 *                   documentation)
	 */
	public TaskUntag taskUntag(int id, String tag) throws Exception {
		MultipartEntity params = new MultipartEntity();
		params.addPart("task_id", new StringBody("" + id));
		params.addPart("tag", new StringBody(tag));
		
		URL request = new URL(OPENML_URL + API_PART + "task/untag");
		Object apiResult = HttpConnector.doApiPostRequest(request, params, getApiKey(), verboseLevel);
		
		return (TaskUntag) apiResult;
	}
	
	/**
	 * Flow description
	 * 
	 * @param flow_id - the id of the flow. 
	 * @return the flow object
	 * @throws Exception - Can be: IOException (problem with connection, server),
	 *                   ApiException (contains error code, see OpenML
	 *                   documentation)
	 */
	public Flow flowGet(int flow_id) throws Exception {
		URL request = new URL(OPENML_URL + API_PART + "flow/" + flow_id);
		Object apiResult = HttpConnector.doApiGetRequest(request, getApiKey(), verboseLevel);
		return (Flow) apiResult;
	}

	
	/**
	 * Tags a flow.
	 * 
	 * @param id - the flow id
	 * @param tag - the tag to be added to the flow
	 * @return An object with the integer id of the updated flow
	 * @throws Exception - Can be: IOException (problem with connection, server),
	 *                   ApiException (contains error code, see OpenML
	 *                   documentation)
	 */
	public FlowTag flowTag(int id, String tag) throws Exception {
		MultipartEntity params = new MultipartEntity();
		params.addPart("flow_id", new StringBody("" + id));
		params.addPart("tag", new StringBody(tag));
		
		URL request = new URL(OPENML_URL + API_PART + "flow/tag");
		Object apiResult = HttpConnector.doApiPostRequest(request, params, getApiKey(), verboseLevel);
		
		return (FlowTag) apiResult;
	}

	/**
	 * removes a tag from a flow
	 * 
	 * @param id - the flow id
	 * @param tag - the tag to be removed
	 * @return An object with the integer id of the updated flow
	 * @throws Exception - Can be: IOException (problem with connection, server),
	 *                   ApiException (contains error code, see OpenML
	 *                   documentation)
	 */
	public FlowUntag flowUntag(int id, String tag) throws Exception {
		MultipartEntity params = new MultipartEntity();
		params.addPart("flow_id", new StringBody("" + id));
		params.addPart("tag", new StringBody(tag));
		
		URL request = new URL(OPENML_URL + API_PART + "flow/untag");
		Object apiResult = HttpConnector.doApiPostRequest(request, params, getApiKey(), verboseLevel);
		
		return (FlowUntag) apiResult;
	}
	
	/**
	 * Deletes a flow
	 * 
	 * @param id
	 *            - The numeric id of the implementation to be deleted.
	 * @return ImplementationDelete - An object containing the id of the deleted
	 *         implementation
	 * @throws Exception - Can be: IOException (problem with connection, server),
	 *                   ApiException (contains error code, see OpenML
	 *                   documentation)
	 */
	public FlowDelete flowDelete(int id) throws Exception {
		URL request = new URL(OPENML_URL + API_PART + "flow/" + id);
		Object apiResult = HttpConnector.doApiDeleteRequest(request, getApiKey(), verboseLevel);
		return (FlowDelete) apiResult;
	}
	
	public FlowDelete flowForceDelete(int id) throws Exception {
		URL request = new URL(OPENML_URL + API_PART + "flow/" + id + "/force");
		Object apiResult = HttpConnector.doApiDeleteRequest(request, getApiKey(), verboseLevel);
		return (FlowDelete) apiResult;
	}
	

	/**
	 * Checks whether a flow exists, by name/external_version combination
	 * 
	 * @param name
	 *            - The name of the implementation to be checked
	 * @param external_version
	 *            - The external version (workbench version). If not a proper
	 *            revision number is available, it is recommended to use a MD5
	 *            hash of the source code.
	 * @return ImplementationExists - An object describing whether this
	 *         implementation is already known on the server.
	 * @throws Exception - Can be: IOException (problem with connection, server),
	 *                   ApiException (contains error code, see OpenML
	 *                   documentation)
	 */
	public FlowExists flowExists(String name, String external_version) throws Exception {
		URL request = new URL(OPENML_URL + API_PART + "flow/exists/" + name + "/" + external_version);
		Object apiResult = HttpConnector.doApiGetRequest(request, getApiKey(), verboseLevel);
		return (FlowExists) apiResult;
	}
	
	protected UploadFlow flowUpload(File description, File binary, File source) throws Exception {
		MultipartEntity params = new MultipartEntity();
		params.addPart("description", new FileBody(description));
		if (source != null)
			params.addPart("source", new FileBody(source));
		if (binary != null)
			params.addPart("binary", new FileBody(binary));

		URL request = new URL(OPENML_URL + API_PART + "flow");
		Object apiResult = HttpConnector.doApiPostRequest(request, params, getApiKey(), verboseLevel);
		return (UploadFlow) apiResult;
	}
	

	/**
	 * Uploads a run
	 * 
	 * @param description
	 *            - An XML file describing the run. See documentation at
	 *            openml.org.
	 * @param output_files
	 *            - A Map&gt;String,File&lt; containing all relevant output files. Key
	 *            "predictions" usually contains the predictions that were
	 *            generated by this run.
	 * @return UploadRun - An object containing information on the
	 *         implementation upload.
	 * @throws Exception - Can be: IOException (problem with connection, server),
	 *                   ApiException (contains error code, see OpenML
	 *                   documentation)
	 */
	protected UploadRun runUpload(File description, Map<String, File> output_files) throws Exception {
		MultipartEntity params = new MultipartEntity();
		if (verboseLevel >= Constants.VERBOSE_LEVEL_ARFF) {
			System.out.println(Conversion.fileToString(output_files.get("predictions")) + "\n==========\n");
		}
		if (verboseLevel >= Constants.VERBOSE_LEVEL_XML) {
			System.out.println(Conversion.fileToString(description) + "\n==========");
		}
		params.addPart("description", new FileBody(description));
		if (output_files != null) {
			for (String s : output_files.keySet()) {
				params.addPart(s, new FileBody(output_files.get(s)));
			}
		}
		
		URL request = new URL(OPENML_URL + API_PART + "run/");
		Object apiResult = HttpConnector.doApiPostRequest(request, params, getApiKey(), verboseLevel);
		return (UploadRun) apiResult;
	}
	
	/**
	 * Returns a list with run results. Must be restricted with tasks, setups or both. 
	 * 
	 * @param filters - filters to limit the number of results
	 * @param limit - maximal number of results allowed
	 * @param offset - number of results to ignore (from the beginning)
	 * @return the list with runs
	 * @throws Exception - Can be: IOException (problem with connection, server),
	 *                   ApiException (contains error code, see OpenML
	 *                   documentation)
	 */
	public RunList runList(Map<String, List<Integer>> filters, Integer limit, Integer offset) throws Exception {
		String suffix = "";
		
		for (String name : filters.keySet()) {
			suffix += "/" + name + "/" + StringUtils.join(filters.get(name), ',');
		}
		
		if (limit != null) {
			suffix += "/limit/" + limit;
		}

		if (offset != null) {
			suffix += "/offset/" + offset;
		}
		
		URL request = new URL(OPENML_URL + API_PART + "run/list" + suffix);
		Object apiResult = HttpConnector.doApiGetRequest(request, getApiKey(), verboseLevel);
		return (RunList) apiResult;
	}
	
	/**
	 * Returns a list with evaluation results. Must be restricted with tasks, setups or both. 
	 * 
	 * @param task_id - a list with task ids to include (null to not restrict on tasks)
	 * @param setup_id - a list with setup ids to include (null to not restrict on setups)
	 * @param function - the evaluation measure interested in
	 * @param limit - the maximal result size
	 * @return a list with evaluations
	 * @throws Exception - Can be: IOException (problem with connection, server),
	 *                   ApiException (contains error code, see OpenML
	 *                   documentation)
	 */
	public EvaluationList evaluationList(List<Integer> task_id, List<Integer> setup_id, String function, Integer limit) throws Exception {
		String suffix = "";
		
		if (task_id != null) {
			suffix += "/task/" + StringUtils.join(task_id, ',');
		}
		if (setup_id != null) {
			suffix += "/setup/" + StringUtils.join(setup_id, ',');
		}
		if (function != null) {
			suffix += "/function/" + function;
		}
		if (limit != null) {
			suffix += "/limit/" + limit;
		}
		
		URL request = new URL(OPENML_URL + API_PART + "evaluation/list" + suffix);
		Object apiResult = HttpConnector.doApiGetRequest(request, getApiKey(), verboseLevel);
		return (EvaluationList) apiResult;
	}
	
	public EvaluationRequest evaluationRequest(int evaluationEngineId, String mode, int numRequests, Map<String, String> additionalFilters) throws Exception {
		String suffix = "evaluation/request/" + evaluationEngineId + "/" + mode + "/" + numRequests;
		if (additionalFilters != null) {
			for (String filter : additionalFilters.keySet()) {
				suffix += "/" + filter + "/" + additionalFilters.get(filter);
			}
		}
		
		URL request = new URL(OPENML_URL + API_PART + suffix);
		Object apiResult = HttpConnector.doApiGetRequest(request, getApiKey(), verboseLevel);
		return (EvaluationRequest) apiResult;
	}

	/**
	 * Tags a run
	 * 
	 * @param id - The run id
	 * @param tag - The tag
	 * @return An object with the integer id of the updated run
	 * @throws Exception - Can be: IOException (problem with connection, server),
	 *                   ApiException (contains error code, see OpenML
	 *                   documentation)
	 */
	public RunTag runTag(int id, String tag) throws Exception {
		MultipartEntity params = new MultipartEntity();
		params.addPart("run_id", new StringBody("" + id));
		params.addPart("tag", new StringBody(tag));
		URL request = new URL(OPENML_URL + API_PART + "run/tag");
		Object apiResult = HttpConnector.doApiPostRequest(request, params, getApiKey(), verboseLevel);
		return (RunTag) apiResult;
	}


	/**
	 * Removes a tag from a run
	 * 
	 * @param id - the run id
	 * @param tag - the tag to be removed
	 * @return An object with the integer id of the updated run
	 * @throws Exception - Can be: IOException (problem with connection, server),
	 *                   ApiException (contains error code, see OpenML
	 *                   documentation)
	 */
	public RunUntag runUntag(int id, String tag) throws Exception {
		MultipartEntity params = new MultipartEntity();
		params.addPart("run_id", new StringBody("" + id));
		params.addPart("tag", new StringBody(tag));
		
		URL request = new URL(OPENML_URL + API_PART + "run/untag");
		Object apiResult = HttpConnector.doApiPostRequest(request, params, getApiKey(), verboseLevel);
		return (RunUntag) apiResult;
	}
	
	protected RunEvaluate runEvaluate(File description) throws Exception {
		MultipartEntity params = new MultipartEntity();
		params.addPart("description", new FileBody(description));

		URL request = new URL(OPENML_URL + API_PART + "run/evaluate");
		Object apiResult = HttpConnector.doApiPostRequest(request, params, getApiKey(), verboseLevel);
		return (RunEvaluate) apiResult;
	}

	/**
	 * Uploads trace results in the database, typically used when an internal parameter optimization loop was executed. (admin rights required, typically executed by evaluation engine)
	 * 
	 * @param trace - the trace description xml
	 * @return an object with the id of the uploaded trace
	 * @throws Exception - Can be: IOException (problem with connection, server),
	 *                   ApiException (contains error code, see OpenML
	 *                   documentation)
	 */
	protected RunTraceUpload runTraceUpload(File trace) throws Exception {
		MultipartEntity params = new MultipartEntity();
		params.addPart("trace", new FileBody(trace));

		URL request = new URL(OPENML_URL + API_PART + "run/trace");
		Object apiResult = HttpConnector.doApiPostRequest(request, params, getApiKey(), verboseLevel);
		return (RunTraceUpload) apiResult;
	}

	/**
	 * Retrieves a run trace (Hyperparameter settings for HPO procedure)
	 * 
	 * @param trace_id - the trace description xml
	 * @return the trace
	 * @throws Exception - Can be: IOException (problem with connection, server),
	 *                   ApiException (contains error code, see OpenML
	 *                   documentation)
	 */
	public RunTrace runTrace(int trace_id) throws Exception {

		URL request = new URL(OPENML_URL + API_PART + "run/trace/" + trace_id);
		Object apiResult = HttpConnector.doApiGetRequest(request, getApiKey(), verboseLevel);
		return (RunTrace) apiResult;
	}

	/**
	 * Downloads run information
	 * 
	 * @param runId - the run id
	 * @return the run
	 * @throws Exception - Can be: IOException (problem with connection, server),
	 *                   ApiException (contains error code, see OpenML
	 *                   documentation)
	 */
	public Run runGet(int runId) throws Exception {
		URL request = new URL(OPENML_URL + API_PART + "run/" + runId);
		Object apiResult = HttpConnector.doApiGetRequest(request, getApiKey(), verboseLevel);
		return (Run) apiResult;
	}

	/**
	 * Deletes a run and all it's important components
	 * 
	 * @param id
	 *            - The numeric id of the run to be deleted.
	 * @return RunDelete - An object containing the id of the deleted run
	 * @throws Exception - Can be: IOException (problem with connection, server),
	 *                   ApiException (contains error code, see OpenML
	 *                   documentation)
	 */
	public RunDelete runDelete(int id) throws Exception {
		URL request = new URL(OPENML_URL + API_PART + "run/" + id);
		Object apiResult = HttpConnector.doApiDeleteRequest(request, getApiKey(), verboseLevel);
		return (RunDelete) apiResult;
	}

	
	/**
	 * Resets the evaluation of a run (admin right required)
	 * 
	 * @param run_id - the id of the run to reset
	 * @return an object with the id of the resetted run
	 * @throws Exception - Can be: IOException (problem with connection, server),
	 *                   ApiException (contains error code, see OpenML
	 *                   documentation)
	 */
	public RunReset runReset(int run_id) throws Exception {
		URL request = new URL(OPENML_URL + API_PART + "run/reset/" + run_id);
		Object apiResult = HttpConnector.doApiGetRequest(request, getApiKey(), verboseLevel);
		return (RunReset) apiResult;
	}

	/**
	 * A list with the parameter settings of a setup
	 * 
	 * @param description - a file equivalent to run description, but only featuring the parts important to parameters
	 * @return an object with a boolean and the setup id, iff exists
	 * @throws Exception - Can be: IOException (problem with connection, server),
	 *                   ApiException (contains error code, see OpenML
	 *                   documentation)
	 */
	public SetupExists setupExists(File description) throws Exception {
		MultipartEntity params = new MultipartEntity();
		if (verboseLevel >= Constants.VERBOSE_LEVEL_XML) {
			System.out.println(Conversion.fileToString(description) + "\n==========");
		}
		params.addPart("description", new FileBody(description));
		URL request = new URL(OPENML_URL + API_PART + "setup/exists");
		Object apiResult = HttpConnector.doApiPostRequest(request, params, getApiKey(), verboseLevel);
		return (SetupExists) apiResult;
	}
	
	/**
	 * A list with the parameter settings of a setup
	 * 
	 * @param setup_id - the id of the setup
	 * @return an object with the list of parameters
	 * @throws Exception - Can be: IOException (problem with connection, server),
	 *                   ApiException (contains error code, see OpenML
	 *                   documentation)
	 */
	public SetupParameters setupParameters(int setup_id) throws Exception {
		URL request = new URL(OPENML_URL + API_PART + "setup/" + setup_id);
		Object apiResult = HttpConnector.doApiGetRequest(request, getApiKey(), verboseLevel);
		return (SetupParameters) apiResult;
	}

	/**
	 * Tags a setup
	 * 
	 * @param id - the setup id
	 * @param tag - the tag
	 * @return an object with the id of the tagged object
	 * @throws Exception - Can be: IOException (problem with connection, server),
	 *                   ApiException (contains error code, see OpenML
	 *                   documentation)
	 */
	public SetupTag setupTag(int id, String tag) throws Exception {
		MultipartEntity params = new MultipartEntity();
		params.addPart("setup_id", new StringBody("" + id));
		params.addPart("tag", new StringBody(tag));
		
		URL request = new URL(OPENML_URL + API_PART + "setup/tag");
		Object apiResult = HttpConnector.doApiPostRequest(request, params, getApiKey(), verboseLevel);
		return (SetupTag) apiResult;
	}

	/**
	 * Removes a tag from a setup
	 * 
	 * @param id - the setup id
	 * @param tag - the tag to be removed
	 * @return an object with the id of the untagged setup
	 * @throws Exception - Can be: IOException (problem with connection, server),
	 *                   ApiException (contains error code, see OpenML
	 *                   documentation)
	 */
	public SetupUntag setupUntag(int id, String tag) throws Exception {
		MultipartEntity params = new MultipartEntity();
		params.addPart("setup_id", new StringBody("" + id));
		params.addPart("tag", new StringBody(tag));
		
		URL request = new URL(OPENML_URL + API_PART + "setup/untag");
		Object apiResult = HttpConnector.doApiPostRequest(request, params, getApiKey(), verboseLevel);
		return (SetupUntag) apiResult;
	}

	/**
	 * Deletes a setup. Only applicable when no runs are attached. 
	 * 
	 * @param id - the id of the setup to be deleted
	 *            - The numeric id of the setup to be deleted.
	 * @return SetupDelete - An object containing the id of the deleted
	 *         setup
	 * @throws Exception - Can be: IOException (problem with connection, server),
	 *                   ApiException (contains error code, see OpenML
	 *                   documentation)
	 */
	public SetupDelete setupDelete(int id) throws Exception {
		URL request = new URL(OPENML_URL + API_PART + "setup/" + id);
		Object apiResult = HttpConnector.doApiDeleteRequest(request, getApiKey(), verboseLevel);
		return (SetupDelete) apiResult;
	}
	
	/**
	 * Returns a list of predictions on which two setups disagree
	 * 
	 * @param setupA - a setup id
	 * @param setupB - a setup id
	 * @param task_id - the task id
	 * @return a list with predictions that differ between the setups
	 * @throws Exception - Can be: IOException (problem with connection, server),
	 *                   ApiException (contains error code, see OpenML
	 *                   documentation)
	 */
	public SetupDifferences setupDifferences(Integer setupA, Integer setupB, Integer task_id)  throws Exception {
		String suffix = setupA + "/" + setupB;
		if (task_id != null) {
			suffix += "/" + task_id;
		}
		
		URL request = new URL(OPENML_URL + API_PART + "setup/differences/" + suffix);
		Object apiResult = HttpConnector.doApiGetRequest(request, getApiKey(), verboseLevel);
		if (apiResult instanceof SetupDifferences) {
			return (SetupDifferences) apiResult;
		} else {
			throw new DataFormatException("Casting Api Object to SetupDifferences");
		}
	}
	
	/**
	 * Returns a list of predictions on which two setups disagree
	 * 
	 * @param setupA - a setup id
	 * @param setupB - a setup id
	 * @param task_id - the task id
	 * @param task_size - // TODO
	 * @param differences // TODO
	 * @return a list with predictions that differ between the setups
	 * @throws Exception - Can be: IOException (problem with connection, server),
	 *                   ApiException (contains error code, see OpenML
	 *                   documentation)
	 */
	public SetupDifferences setupDifferences(int setupA, int setupB, int task_id, int task_size, int differences) throws Exception {
		MultipartEntity params = new MultipartEntity();
		params.addPart("task_id", new StringBody("" + task_id));
		params.addPart("task_size", new StringBody("" + task_size));
		params.addPart("differences", new StringBody("" + differences));

		URL request = new URL(OPENML_URL + API_PART + "setup/differences/" + setupA + "/" + setupB);
		Object apiResult = HttpConnector.doApiPostRequest(request, params, getApiKey(), verboseLevel);
		if (apiResult instanceof SetupDifferences) {
			return (SetupDifferences) apiResult;
		} else {
			throw new DataFormatException("Casting Api Object to SetupDifferences");
		}
	}
	
	public FileUpload fileUpload(File file) throws Exception {
		MultipartEntity params = new MultipartEntity();
		if (verboseLevel >= Constants.VERBOSE_LEVEL_ARFF) {
			System.out.println(Conversion.fileToString(file) + "\n==========\n");
		}
		params.addPart("file", new FileBody(file));

		URL request = new URL(OPENML_URL + API_PART + "file/upload");
		Object apiResult = HttpConnector.doApiPostRequest(request, params, getApiKey(), verboseLevel);
		if (apiResult instanceof FileUpload) {
			return (FileUpload) apiResult;
		} else {
			throw new DataFormatException("Casting Api Object to UploadFile");
		}
	}
	
	public Study studyGet(String studyAlias, String dataType) throws Exception {
		String suffix = "";
		if (dataType != null) { suffix += "/" + dataType; }
		
		URL request = new URL(OPENML_URL + API_PART + "study/" + studyAlias + suffix);
		Object apiResult = HttpConnector.doApiGetRequest(request, getApiKey(), verboseLevel);
		if (apiResult instanceof Study) {
			return (Study) apiResult;
		} else {
			throw new DataFormatException("Casting Api Object to Study");
		}
	}
	
	protected StudyUpload studyUpload(File description) throws Exception {
		MultipartEntity params = new MultipartEntity();
		if (verboseLevel >= Constants.VERBOSE_LEVEL_ARFF) {
			System.out.println(Conversion.fileToString(description) + "\n==========\n");
		}
		params.addPart("description", new FileBody(description));
		URL request = new URL(OPENML_URL + API_PART + "study/");
		Object apiResult = HttpConnector.doApiPostRequest(request, params, getApiKey(), verboseLevel);
		if (apiResult instanceof StudyUpload) {
			return (StudyUpload) apiResult;
		} else {
			throw new DataFormatException("Casting Api Object to StudyUpload");
		}
	}
	
	public StudyList studyList(Map<String, String> filters) throws Exception {
		String suffix = "";
		
		for (String name : filters.keySet()) {
			suffix += "/" + name + "/" + filters.get(name);
		}
		
		URL request = new URL(OPENML_URL + API_PART + "study/list" + suffix);
		Object apiResult = HttpConnector.doApiGetRequest(request, getApiKey(), verboseLevel);
		if (apiResult instanceof StudyList) {
			return (StudyList) apiResult;
		} else {
			throw new DataFormatException("Casting Api Object to StudyList");
		}
	}
	
	public StudyAttach studyAttach(int id, List<Integer> entity_ids) throws Exception {
		MultipartEntity params = new MultipartEntity();
		params.addPart("ids", new StringBody(StringUtils.join(entity_ids, ',')));
		
		URL request = new URL(OPENML_URL + API_PART + "study/" + id + "/attach");
		Object apiResult = HttpConnector.doApiPostRequest(request, params, getApiKey(), verboseLevel);
		if (apiResult instanceof StudyAttach) {
			return (StudyAttach) apiResult;
		} else {
			throw new DataFormatException("Casting Api Object to StudyAttach");
		}
	}
	
	public StudyDetach studyDetach(int id, List<Integer> entity_ids) throws Exception {
		MultipartEntity params = new MultipartEntity();
		params.addPart("ids", new StringBody(StringUtils.join(entity_ids, ',')));
		
		URL request = new URL(OPENML_URL + API_PART + "study/" + id + "/detach");
		Object apiResult = HttpConnector.doApiPostRequest(request, params, getApiKey(), verboseLevel);
		if (apiResult instanceof StudyDetach) {
			return (StudyDetach) apiResult;
		} else {
			throw new DataFormatException("Casting Api Object to StudyDetach");
		}
	}
	
	public Study studyGet(int studyId) throws Exception {
		return studyGet("" + studyId, null);
	}
	
	public Study studyGet(int studyId, String dataType) throws Exception {
		return studyGet("" + studyId, dataType);
	}


	public URL getOpenmlFileUrl(Integer file_id, String filename) throws Exception {
		return getOpenmlFileUrl(file_id, filename, "download");
	}
	
	public URL getOpenmlFileUrl(Integer file_id, String filename, String phpFunction) throws Exception {
		if (filename == null) {
			filename = "file"; }
		String suffix = api_key == null ? "" : "?api_key=" + getApiKey();
		return new URL(OPENML_URL + "data/v1/" + phpFunction + "/" + file_id + "/" + URLEncoder.encode(filename, "UTF-8") +  suffix );
	}
}
