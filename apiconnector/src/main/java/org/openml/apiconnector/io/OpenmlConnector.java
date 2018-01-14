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
import org.openml.apiconnector.algorithms.ArffHelper;
import org.openml.apiconnector.algorithms.Caching;
import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.algorithms.TaskInformation;
import org.openml.apiconnector.settings.Constants;
import org.openml.apiconnector.settings.Settings;
import org.openml.apiconnector.xml.*;
import org.openml.apiconnector.xml.Task.Input.Estimation_procedure;
import org.openml.apiconnector.xstream.XstreamXmlMapping;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.zip.DataFormatException;

public class OpenmlConnector implements Serializable {
	private static final long serialVersionUID = 7362620508675762264L;

	/**
	 * When set to true, API will output information.
	 */
	protected int verboseLevel = 0;
	
	protected String api_key;
	
	protected final String OPENML_URL;
	
	protected String API_PART = "api_new/v1/";
	
	protected boolean apiKeySet = false;

	/**
	 * Creates a OpenML Connector with url and authentication
	 * 
	 * @param url - the openml server
	 * @param api_key - the api key to authenticate with
	 */
	public OpenmlConnector(String url, String api_key, boolean useJson) {
		if (url != null) {
			this.OPENML_URL = url;
		} else {
			this.OPENML_URL = Settings.BASE_URL;
		}
		
		this.api_key = api_key;
		
		if (api_key != null) {
			this.apiKeySet = true;
		}
		
		if (useJson) {
			API_PART += "json/";
		}
	}
	/**
	 * Creates a OpenML Connector with url and authentication
	 * 
	 * @param url - the openml server
	 * @param api_key - the api key to authenticate with
	 */
	public OpenmlConnector(String url, String api_key) {
		if (url != null) {
			this.OPENML_URL = url;
		} else {
			this.OPENML_URL = Settings.BASE_URL;
		}
		
		this.api_key = api_key;
		
		if (api_key != null) {
			this.apiKeySet = true;
		}
	}

	/**
	 * Creates a default OpenML Connector with authentication
	 * 
	 * api_key - the api key to authenticate with
	 */
	public OpenmlConnector(String api_key) {
		this.OPENML_URL = Settings.BASE_URL;
		this.api_key = api_key;
		
		if (api_key != null) {
			this.apiKeySet = true;
		}
	}
	
	
	/**
	 * Creates a default OpenML Connector
	 */
	public OpenmlConnector() {
		this.OPENML_URL = Settings.BASE_URL;
		
	}
	

	/**
	 * Return the api key that is used to authenticate with
	 */
	public String getApiKey() {
		if (this.apiKeySet) {
			return api_key;
		} else {
			return null;
		}
	}

	/**
	 * Updates the api key
	 * 
	 * api_key - the api key
	 */
	public void setApiKey(String api_key) { 
		this.api_key = api_key;
		
		if (api_key != null) {
			this.apiKeySet = true;
		}
	}


	/**
	 * Sets the verbose level.
	 * 
	 * level - higher means more output. 
	 * 			0 = none, 1 = communication with server, 2 = also files
	 */
	public void setVerboseLevel(int level) {
		verboseLevel = level;
	}
	
	public int getVerboselevel() {
		return verboseLevel;
	}

	public String getApiUrl(){
		return OPENML_URL + API_PART;
	}
	
	public File getXSD(String name) throws IOException {
		File file = File.createTempFile("name", "xsd");
		file.deleteOnExit();
		URL url = new URL(getApiUrl() + "xsd/" + name + "?api_key=" + getApiKey());
		FileUtils.copyURLToFile(url, file);
		return file;
	}
	
	/*
	public Data data_list() throws Exception {
		Object apiResult = HttpConnector.doApiRequest(API_URL + "data/list", session_hash, verboseLevel);
		if (apiResult instanceof Data) {
			return (Data) apiResult;
		} else {
			throw new DataFormatException("Casting Api Object to Data");
		}
	}*/

	/**
	 * Retrieves the description of a specified data set.
	 * 
	 * @param did
	 *            - The data_id of the data description to download.
	 * @return DataSetDescription - An object containing the description of the
	 *         data
	 * @throws Exception
	 *             - Can be: API Error (see documentation at openml.org), server
	 *             down, etc.
	 */
	public DataSetDescription dataGet(int did) throws Exception {
		URL request = new URL(OPENML_URL + API_PART + "data/" + did);
		if (Caching.in_cache(request, "datadescription", did, "xml")) {
			String dsdString = Conversion.fileToString(Caching.cached(request, "datadescription", did, "xml"));
			return (DataSetDescription) XstreamXmlMapping.getInstance().fromXML(dsdString);
		}
		
		Object apiResult = HttpConnector.doApiRequest(request, getApiKey(), verboseLevel);
		if (apiResult instanceof DataSetDescription) {
			if (Settings.CACHE_ALLOWED) {
				try {
					Caching.cacheXML(request, apiResult, "datadescription", did, "xml");
				} catch(IOException e) {
					Conversion.log("Warning", "DataGet", "Cache Store Exception: " + e.getMessage());
				}
			}
			return (DataSetDescription) apiResult;
		} else {
			throw new DataFormatException("Casting Api Object to DataSetDescription");
		}
	}
	
	public File datasetGet(DataSetDescription dsd) throws Exception {
		URL fileUrl = getOpenmlFileUrl(dsd.getFile_id(), dsd.getName());
		return ArffHelper.downloadAdCache("dataset", dsd.getId(), dsd.getFormat(), fileUrl, dsd.getMd5_checksum());
	}
	
	/**
	 * Uploads a new dataset
	 * 
	 * @param description - xml file describing the dataset, according to XSD
	 * @param dataset - arff file representing the dataset. optional. 
	 * @return The id under which the dataset was uploaded
	 * @throws Exception
	 */
	public UploadDataSet dataUpload(File description, File dataset) throws Exception {
		MultipartEntity params = new MultipartEntity();
		params.addPart("description", new FileBody(description));
		if (dataset != null) {
			params.addPart("dataset", new FileBody(dataset));
		}
		URL request = new URL(OPENML_URL + API_PART + "data/");
		Object apiResult = HttpConnector.doApiRequest(request, params, getApiKey(), verboseLevel);
		if (apiResult instanceof UploadDataSet) {
			return (UploadDataSet) apiResult;
		} else {
			throw new DataFormatException("Casting Api Object to UploadDataSet");
		}
	}
	
	/**
	 * Deletes a dataset from the server
	 * 
	 * @param did - The data id to be deleted
	 * @return The id of the dataset that was deleted
	 * @throws Exception
	 */
	public DataDelete dataDelete(int did) throws Exception {
		URL request = new URL(OPENML_URL + API_PART + "data/" + did);
		Object apiResult = HttpConnector.doApiDelete(request, getApiKey(), verboseLevel);
		if (apiResult instanceof DataDelete) {
			return (DataDelete) apiResult;
		} else {
			throw new DataFormatException("Casting Api Object to DataDelete");
		}
	}
	
	/**
	 * Tags a dataset
	 * 
	 * @param id - the dataset to be tagged
	 * @param tag - the tag to be used
	 * @return
	 * @throws Exception
	 */
	public DataTag dataTag(int id, String tag) throws Exception {
		MultipartEntity params = new MultipartEntity();
		params.addPart("data_id", new StringBody("" + id));
		params.addPart("tag", new StringBody(tag));
		
		URL request = new URL(OPENML_URL + API_PART + "data/tag");
		Object apiResult = HttpConnector.doApiRequest(request, params, getApiKey(), verboseLevel);
		if (apiResult instanceof DataTag) {
			return (DataTag) apiResult;
		} else {
			throw new DataFormatException("Casting Api Object to DataTag");
		}
	}
	
	/**
	 * Untags a dataset
	 * 
	 * @param id - the id of the dataset
	 * @param tag - the tag to be remoeved
	 * @return
	 * @throws Exception
	 */
	public DataUntag dataUntag(int id, String tag) throws Exception {
		MultipartEntity params = new MultipartEntity();
		params.addPart("data_id", new StringBody("" + id));
		params.addPart("tag", new StringBody(tag));
		
		URL request = new URL(OPENML_URL + API_PART + "data/untag");
		Object apiResult = HttpConnector.doApiRequest(request, params, getApiKey(), verboseLevel);
		if (apiResult instanceof DataUntag) {
			return (DataUntag) apiResult;
		} else {
			throw new DataFormatException("Casting Api Object to DataUntag");
		}
	}

	/**
	 * Retrieves the features of a specified data set.
	 * 
	 * @param did
	 *            - The data_id of the data features to download.
	 * @return DataFeatures - An object containing the features of the data
	 * @throws Exception
	 *             - Can be: API Error (see documentation at openml.org), server
	 *             down, etc.
	 */
	public DataFeature dataFeatures(int did) throws Exception {
		URL request = new URL(OPENML_URL + API_PART + "data/features/" + did);
		Object apiResult = HttpConnector.doApiRequest(request, getApiKey(), verboseLevel);
		if (apiResult instanceof DataFeature) {
			return (DataFeature) apiResult;
		} else {
			throw new DataFormatException("Casting Api Object to DataFeature");
		}
	}
	
	public Data dataList(Map<String, String> filters) throws Exception {
		String suffix = "";
		if (filters != null) {
			for (String filter : filters.keySet()) {
				suffix += filter + "/" + filters.get(filter) + "/";
			}
		}
		URL request = new URL(OPENML_URL + API_PART + "data/list/" + suffix);
		Object apiResult = HttpConnector.doApiRequest(request, getApiKey(), verboseLevel);
		if (apiResult instanceof Data) {
			return (Data) apiResult;
		} else {
			throw new DataFormatException("Casting Api Object to Data");
		}
	}

	/**
	 * Retrieves the qualities (meta-features) of a specified data set.
	 * 
	 * @param did
	 *            - The data_id of the data features to download.
	 * @return DataFeatures - An object containing the qualities of the data
	 * @throws Exception
	 *             - Can be: API Error (see documentation at openml.org), server
	 *             down, etc.
	 */
	public DataQuality dataQualities(int did) throws Exception {
		URL request = new URL(OPENML_URL + API_PART + "data/qualities/" + did);
		Object apiResult = HttpConnector.doApiRequest(request, getApiKey(), verboseLevel);
		
		if (apiResult instanceof DataQuality) {
			return (DataQuality) apiResult;
		} else {
			throw new DataFormatException("Casting Api Object to DataQuality");
		}
	}
	
	/**
	 * Retrieves the qualities (meta-features) of a specified data set.
	 * 
	 * @param did
	 *            - The data_id of the data features to download.
 	 *            - The id of the evaluation engine whose qualities to download
	 * @return DataFeatures - An object containing the qualities of the data
	 * @throws Exception
	 *             - Can be: API Error (see documentation at openml.org), server
	 *             down, etc.
	 */
	public DataQuality dataQualities(int did, int evalEngine) throws Exception {
		URL request = new URL(OPENML_URL + API_PART + "data/qualities/" + did + "/" + evalEngine);
		Object apiResult = HttpConnector.doApiRequest(request, getApiKey(), verboseLevel);

		if (apiResult instanceof DataQuality) {
			return (DataQuality) apiResult;
		} else {
			throw new DataFormatException("Casting Api Object to DataQuality");
		}
	}
	
	/**
	 * Uploads data features (requires admin account)
	 * 
	 * @param description - the features
	 * @return
	 * @throws Exception
	 */
	public DataFeatureUpload dataFeaturesUpload(File description) throws Exception {
		MultipartEntity params = new MultipartEntity();
		params.addPart("description", new FileBody(description));

		if (verboseLevel >= Constants.VERBOSE_LEVEL_ARFF) {
			System.out.println(Conversion.fileToString(description) + "\n==========\n");
		}

		URL request = new URL(OPENML_URL + API_PART + "data/features");
		Object apiResult = HttpConnector.doApiRequest(request, params, getApiKey(), verboseLevel);
		
		if (apiResult instanceof DataFeatureUpload) {
			return (DataFeatureUpload) apiResult;
		} else {
			throw new DataFormatException("Casting Api Object to DataFeatureUpload");
		}
	}

	/**
	 * Uploads data qualities (requires admin account)
	 * 
	 * @param description - the qualitues (or meta-features)
	 * @return
	 * @throws Exception
	 */
	public DataQualityUpload dataQualitiesUpload(File description) throws Exception {
		MultipartEntity params = new MultipartEntity();
		params.addPart("description", new FileBody(description));

		URL request = new URL(OPENML_URL + API_PART + "data/qualities");
		Object apiResult = HttpConnector.doApiRequest(request, params, getApiKey(), verboseLevel);
		if (apiResult instanceof DataQualityUpload) {
			return (DataQualityUpload) apiResult;
		} else {
			throw new DataFormatException("Casting Api Object to DataQualityUpload");
		}
	}

	/**
	 * Returns a list with all available data qualities. 
	 * 
	 * @return
	 * @throws Exception
	 */
	public DataQualityList dataQualitiesList() throws Exception {
		URL request = new URL(OPENML_URL + API_PART + "data/qualities/list");
		Object apiResult = HttpConnector.doApiRequest(request, getApiKey(), verboseLevel);
		if (apiResult instanceof DataQualityList) {
			return (DataQualityList) apiResult;
		} else {
			throw new DataFormatException("Casting Api Object to DataQualityList");
		}
	}
	
	public DataUnprocessed dataUnprocessed(int evaluationEngineId, String mode) throws Exception {
		String suffix = "data/unprocessed/" + evaluationEngineId + "/" + mode;
		URL request = new URL(OPENML_URL + API_PART + suffix);
		Object apiResult = HttpConnector.doApiRequest(request, getApiKey(), verboseLevel);
		if (apiResult instanceof DataUnprocessed) {
			return (DataUnprocessed) apiResult;
		} else {
			throw new DataFormatException("Casting Api Object to DataUnprocessed");
		}
	}
	
	public DataUnprocessed dataqualitiesUnprocessed(int evaluationEngineId, String mode, boolean featureQualities, List<String> qualitiesToCalculate) throws Exception {
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
		
		URL request = new URL(OPENML_URL + API_PART + suffix);
		Object apiResult = HttpConnector.doApiRequest(request, params, getApiKey(), verboseLevel);
		if (apiResult instanceof DataUnprocessed) {
			return (DataUnprocessed) apiResult;
		} else {
			throw new DataFormatException("Casting Api Object to DataUnprocessed");
		}
	}
	
	/**
	 * @param task_id
	 *            - The numeric id of the task to be obtained.
	 * @return Task - An object describing the task
	 * @throws Exception
	 *             - Can be: API Error (see documentation at openml.org), server
	 *             down, etc.
	 */
	public Task taskGet(int task_id) throws Exception {
		URL request = new URL(OPENML_URL + API_PART + "task/" + task_id);
		if (Caching.in_cache(request, "task", task_id, "xml")) {
			String taskXml = Conversion.fileToString(Caching.cached(request, "task", task_id, "xml"));
			return (Task) XstreamXmlMapping.getInstance().fromXML(taskXml);
		}

		Object apiResult = HttpConnector.doApiRequest(request, getApiKey(), verboseLevel);
		if (apiResult instanceof Task) {
			if (Settings.CACHE_ALLOWED) {
				try {
					Caching.cacheXML(request, apiResult, "task", task_id, "xml");
				} catch(IOException e) {
					Conversion.log("Warning", "TaskGet", "Cache Store Exception: " + e.getMessage());
				}
			}
			return (Task) apiResult;
		} else {
			throw new DataFormatException("Casting Api Object to Task");
		}
	}

	
	public File taskSplitsGet(Task task) throws Exception {
		Estimation_procedure ep = TaskInformation.getEstimationProcedure(task);
		return ArffHelper.downloadAdCache("splits", task.getTask_id(), "arff", ep.getData_splits_url(), null);
	}
	
	public TaskInputs taskInputs(int task_id) throws Exception {
		URL request = new URL(OPENML_URL + API_PART + "task/inputs/" + task_id);
		setVerboseLevel(1);
		Object apiResult = HttpConnector.doApiRequest(request, getApiKey(), verboseLevel);
		if (apiResult instanceof TaskInputs) {
			return (TaskInputs) apiResult;
		} else {
			throw new DataFormatException("Casting Api Object to TaskInputs");
		}
	}
	
	public Tasks taskList(String tag) throws Exception {
		URL request = new URL(OPENML_URL + API_PART + "task/list/tag/" + tag);
		Object apiResult = HttpConnector.doApiRequest(request, getApiKey(), verboseLevel);
		if (apiResult instanceof Tasks) {
			return (Tasks) apiResult;
		} else {
			throw new DataFormatException("Casting Api Object to Tasks");
		}
	}
	
	/**
	 * Returns a list of all of the tasks with a certain tag and type
	 * @param type - the task type
	 * @param tag - the task tag
	 * @return
	 * @throws DataFormatException
	 */
	public Tasks taskList(int type, String tag) throws Exception {
		URL request = new URL(OPENML_URL + API_PART + "task/list/tag/" + tag + "/type/" + type + "/");
		Object apiResult = HttpConnector.doApiRequest(request, getApiKey(), verboseLevel);
		if (apiResult instanceof Tasks) {
			return (Tasks) apiResult;
		} else {
			throw new DataFormatException("Casting Api Object to Tasks");
		}
	}
	
	/**
	 * Deletes a task
	 * 
	 * @param task_id - the task to be deleted
	 * @return
	 * @throws Exception
	 */
	public TaskDelete taskDelete(int task_id) throws Exception {
		URL request = new URL(OPENML_URL + API_PART + "task/" + task_id);
		Object apiResult = HttpConnector.doApiDelete(request, getApiKey(), verboseLevel);
		if (apiResult instanceof TaskDelete) {
			return (TaskDelete) apiResult;
		} else {
			throw new DataFormatException("Casting Api Object to TaskDelete");
		}
	}
	
	/**
	 * Uploads a task
	 * 
	 * @param description - task description. 
	 * @return
	 * @throws Exception
	 */
	public UploadTask taskUpload(File description) throws Exception {
		MultipartEntity params = new MultipartEntity();
		params.addPart("description", new FileBody(description));
		
		URL request = new URL(OPENML_URL + API_PART + "task/");
		Object apiResult = HttpConnector.doApiRequest(request, params, getApiKey(), verboseLevel);
		if (apiResult instanceof UploadTask) {
			return (UploadTask) apiResult;
		} else {
			throw new DataFormatException("Casting Api Object to UploadTask");
		}
	}
	
	/**
	 * Tags a task
	 * 
	 * @param id - the task id
	 * @param tag - the tag
	 * @return
	 * @throws Exception
	 */
	public TaskTag taskTag(int id, String tag) throws Exception {
		MultipartEntity params = new MultipartEntity();
		params.addPart("task_id", new StringBody("" + id));
		params.addPart("tag", new StringBody(tag));
		
		URL request = new URL(OPENML_URL + API_PART + "task/tag");
		Object apiResult = HttpConnector.doApiRequest(request, params, getApiKey(), verboseLevel);
		
		if (apiResult instanceof TaskTag) {
			return (TaskTag) apiResult;
		} else {
			throw new DataFormatException("Casting Api Object to TaskTag");
		}
	}
	
	/**
	 * Untags a task
	 * 
	 * @param id - the task id
	 * @param tag - the tag to be removed
	 * @return
	 * @throws Exception
	 */
	public TaskUntag taskUntag(int id, String tag) throws Exception {
		MultipartEntity params = new MultipartEntity();
		params.addPart("task_id", new StringBody("" + id));
		params.addPart("tag", new StringBody(tag));
		
		URL request = new URL(OPENML_URL + API_PART + "task/untag");
		Object apiResult = HttpConnector.doApiRequest(request, params, getApiKey(), verboseLevel);
		
		if (apiResult instanceof TaskUntag) {
			return (TaskUntag) apiResult;
		} else {
			throw new DataFormatException("Casting Api Object to TaskUntag");
		}
	}
	
	/**
	 * Flow description
	 * 
	 * @param flow_id - the id of the flow. 
	 * @return
	 * @throws Exception
	 */
	public Flow flowGet(int flow_id) throws Exception {
		URL request = new URL(OPENML_URL + API_PART + "flow/" + flow_id);
		Object apiResult = HttpConnector.doApiRequest(request, getApiKey(), verboseLevel);
		if (apiResult instanceof Flow) {
			return (Flow) apiResult;
		} else {
			throw new DataFormatException("Casting Api Object to Implementation");
		}
	}

	
	/**
	 * Tags a flow.
	 * 
	 * @param id - the flow id
	 * @param tag - the tag to be added to the flow
	 * @return
	 * @throws Exception
	 */
	public FlowTag flowTag(int id, String tag) throws Exception {
		MultipartEntity params = new MultipartEntity();
		params.addPart("flow_id", new StringBody("" + id));
		params.addPart("tag", new StringBody(tag));
		
		URL request = new URL(OPENML_URL + API_PART + "flow/tag");
		Object apiResult = HttpConnector.doApiRequest(request, params, getApiKey(), verboseLevel);
		
		if (apiResult instanceof FlowTag) {
			return (FlowTag) apiResult;
		} else {
			throw new DataFormatException("Casting Api Object to FlowTag");
		}
	}

	/**
	 * removes a tag from a flow
	 * 
	 * @param id - the flow id
	 * @param tag - the tag to be removed
	 * @return
	 * @throws Exception
	 */
	public FlowUntag flowUntag(int id, String tag) throws Exception {
		MultipartEntity params = new MultipartEntity();
		params.addPart("flow_id", new StringBody("" + id));
		params.addPart("tag", new StringBody(tag));
		
		URL request = new URL(OPENML_URL + API_PART + "flow/untag");
		Object apiResult = HttpConnector.doApiRequest(request, params, getApiKey(), verboseLevel);
		
		if (apiResult instanceof FlowUntag) {
			return (FlowUntag) apiResult;
		} else {
			throw new DataFormatException("Casting Api Object to FlowUntag");
		}
	}
	
	/**
	 * Deletes a flow
	 * 
	 * @param id
	 *            - The numeric id of the implementation to be deleted.
	 * @return ImplementationDelete - An object containing the id of the deleted
	 *         implementation
	 * @throws Exception
	 *             - Can be: API Error (see documentation at openml.org), server
	 *             down, etc.
	 */
	public FlowDelete flowDelete(int id) throws Exception {
		URL request = new URL(OPENML_URL + API_PART + "flow/" + id);
		Object apiResult = HttpConnector.doApiDelete(request, getApiKey(), verboseLevel);
		if (apiResult instanceof FlowDelete) {
			return (FlowDelete) apiResult;
		} else {
			throw new DataFormatException("Casting Api Object to ImplementationDelete");
		}
	}
	
	public FlowDelete flowForceDelete(int id) throws Exception {
		URL request = new URL(OPENML_URL + API_PART + "flow/" + id + "/force");
		Object apiResult = HttpConnector.doApiDelete(request, getApiKey(), verboseLevel);
		if (apiResult instanceof FlowDelete) {
			return (FlowDelete) apiResult;
		} else {
			throw new DataFormatException("Casting Api Object to ImplementationDelete");
		}
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
	 * @throws Exception
	 *             - Can be: API Error (see documentation at openml.org), server
	 *             down, etc.
	 */
	public FlowExists flowExists(String name, String external_version) throws Exception {
		URL request = new URL(OPENML_URL + API_PART + "flow/exists/" + name + "/" + external_version);
		Object apiResult = HttpConnector.doApiRequest(request, getApiKey(), verboseLevel);
		if (apiResult instanceof FlowExists) {
			return (FlowExists) apiResult;
		} else {
			throw new DataFormatException("Casting Api Object to ImplementationExists");
		}
	}
	
	/**
	 * Uploads a flow
	 * 
	 * @param description
	 *            - An XML file describing the implementation. See documentation
	 *            at openml.org.
	 * @param binary
	 *            - A file containing the implementation binary.
	 * @param source
	 *            - A file containing the implementation source.
	 * @return UploadImplementation - An object containing information on the
	 *         implementation upload.
	 * @throws Exception
	 *             - Can be: API Error (see documentation at openml.org), server
	 *             down, etc.
	 */
	public UploadFlow flowUpload(File description, File binary, File source) throws Exception {
		MultipartEntity params = new MultipartEntity();
		params.addPart("description", new FileBody(description));
		if (source != null)
			params.addPart("source", new FileBody(source));
		if (binary != null)
			params.addPart("binary", new FileBody(binary));

		URL request = new URL(OPENML_URL + API_PART + "flow");
		Object apiResult = HttpConnector.doApiRequest(request, params, getApiKey(), verboseLevel);
		if (apiResult instanceof UploadFlow) {
			return (UploadFlow) apiResult;
		} else {
			throw new DataFormatException("Casting Api Object to UploadImplementation");
		}
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
	 * @throws Exception
	 *             - Can be: API Error (see documentation at openml.org), server
	 *             down, etc.
	 */
	public UploadRun runUpload(File description, Map<String, File> output_files) throws Exception {
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
		Object apiResult = HttpConnector.doApiRequest(request, params, getApiKey(), verboseLevel);
		if (apiResult instanceof UploadRun) {
			return (UploadRun) apiResult;
		} else {
			throw new DataFormatException("Casting Api Object to UploadRun");
		}
	}
	
	public UploadRunAttach runUploadAttach(int run_id, int index, File description, File predictions) throws Exception {
		MultipartEntity params = new MultipartEntity();
		params.addPart("description", new FileBody(description));
		params.addPart("predictions", new FileBody(predictions));
		
		URL request = new URL(OPENML_URL + API_PART + "run/" + run_id + "/" + index);
		Object apiResult = HttpConnector.doApiRequest(request, params, getApiKey(), verboseLevel);
		if (apiResult instanceof UploadRunAttach) {
			return (UploadRunAttach) apiResult;
		} else {
			throw new DataFormatException("Casting Api Object to UploadRunAttach");
		}
	}
	
	/**
	 * Returns a list with run results. Must be restricted with tasks, setups or both. 
	 * 
	 * @param filters - filters to limit the number of results
	 * @param limit - maximal number of results allowed
	 * @param offset - number of results to ignore (from the beginning)
	 * @return
	 * @throws Exception
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
		Object apiResult = HttpConnector.doApiRequest(request, getApiKey(), verboseLevel);
		if (apiResult instanceof RunList) {
			return (RunList) apiResult;
		} else {
			throw new DataFormatException("Casting Api Object to RunList");
		}
	}
	
	/**
	 * Returns a list with run results. Must be restricted with tasks, setups or both. 
	 * 
	 * @param task_id - a list with task ids to include (null to not restrict on tasks)
	 * @param setup_id - a list with setup ids to include (null to not restrict on setups)
	 * @param function - the evaluation measure interested in
	 * @return
	 * @throws Exception
	 */
	public EvaluationList evaluationList(List<Integer> task_id, List<Integer> setup_id, String function) throws Exception {
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
		
		URL request = new URL(OPENML_URL + API_PART + "evaluation/list" + suffix);
		Object apiResult = HttpConnector.doApiRequest(request, getApiKey(), verboseLevel);
		if (apiResult instanceof EvaluationList) {
			return (EvaluationList) apiResult;
		} else {
			throw new DataFormatException("Casting Api Object to EvaluationList");
		}
	}
	
	public EvaluationRequest evaluationRequest(int evaluationEngineId, String mode, Map<String, String> additionalFilters) throws Exception {
		String suffix = "evaluation/request/" + evaluationEngineId + "/" + mode;
		if (additionalFilters != null) {
			for (String filter : additionalFilters.keySet()) {
				suffix += "/" + filter + "/" + additionalFilters.get(filter);
			}
		}
		
		URL request = new URL(OPENML_URL + API_PART + suffix);
		Object apiResult = HttpConnector.doApiRequest(request, getApiKey(), verboseLevel);
		if (apiResult instanceof EvaluationRequest) {
			return (EvaluationRequest) apiResult;
		} else {
			throw new DataFormatException("Casting Api Object to EvaluationRequest");
		}
	}

	/**
	 * Tags a run
	 * 
	 * @param id - The run id
	 * @param tag - The tag
	 * @return
	 * @throws Exception
	 */
	public RunTag runTag(int id, String tag) throws Exception {
		MultipartEntity params = new MultipartEntity();
		params.addPart("run_id", new StringBody("" + id));
		params.addPart("tag", new StringBody(tag));
		URL request = new URL(OPENML_URL + API_PART + "run/tag");
		Object apiResult = HttpConnector.doApiRequest(request, params, getApiKey(), verboseLevel);
		if (apiResult instanceof RunTag) {
			return (RunTag) apiResult;
		} else {
			throw new DataFormatException("Casting Api Object to RunTag");
		}
	}


	/**
	 * Removes a tag from a run
	 * 
	 * @param id - the run id
	 * @param tag - the tag to be removed
	 * @return
	 * @throws Exception
	 */
	public RunUntag runUntag(int id, String tag) throws Exception {
		MultipartEntity params = new MultipartEntity();
		params.addPart("run_id", new StringBody("" + id));
		params.addPart("tag", new StringBody(tag));
		
		URL request = new URL(OPENML_URL + API_PART + "run/untag");
		Object apiResult = HttpConnector.doApiRequest(request, params, getApiKey(), verboseLevel);
		if (apiResult instanceof RunUntag) {
			return (RunUntag) apiResult;
		} else {
			throw new DataFormatException("Casting Api Object to RunUntag");
		}
	}

	/**
	 * Stores evaluation measures of a run (admin rights required, typically executed by evaluation engine)
	 * 
	 * @param description - description file (complying to xsd)
	 * @return
	 * @throws Exception
	 */
	public RunEvaluate runEvaluate(File description) throws Exception {
		MultipartEntity params = new MultipartEntity();
		params.addPart("description", new FileBody(description));

		URL request = new URL(OPENML_URL + API_PART + "run/evaluate");
		Object apiResult = HttpConnector.doApiRequest(request, params, getApiKey(), verboseLevel);
		if (apiResult instanceof RunEvaluate) {
			return (RunEvaluate) apiResult;
		} else {
			throw new DataFormatException("Casting Api Object to RunEvaluate");
		}
	}

	/**
	 * Uploads trace results in the database, typically used when an internal parameter optimization loop was executed. (admin rights required, typically executed by evaluation engine)
	 * 
	 * @param trace - the trace description xml
	 * @return
	 * @throws Exception
	 */
	public RunTraceUpload runTraceUpload(File trace) throws Exception {
		MultipartEntity params = new MultipartEntity();
		params.addPart("trace", new FileBody(trace));

		URL request = new URL(OPENML_URL + API_PART + "run/trace");
		Object apiResult = HttpConnector.doApiRequest(request, params, getApiKey(), verboseLevel);
		if (apiResult instanceof RunTraceUpload) {
			return (RunTraceUpload) apiResult;
		} else {
			throw new DataFormatException("Casting Api Object to RunTraceUpload");
		}
	}

	/**
	 * Stores trace results in the database, typically used when an internal parameter optimization loop was executed. (admin rights required, typically executed by evaluation engine)
	 * 
	 * @param trace_id - the trace description xml
	 * @return
	 * @throws Exception
	 */
	public RunTrace runTrace(int trace_id) throws Exception {

		URL request = new URL(OPENML_URL + API_PART + "run/trace/" + trace_id);
		Object apiResult = HttpConnector.doApiRequest(request, getApiKey(), verboseLevel);
		if (apiResult instanceof RunTrace) {
			return (RunTrace) apiResult;
		} else {
			throw new DataFormatException("Casting Api Object to RunTrace");
		}
	}

	/**
	 * Downloads run information
	 * 
	 * @param runId - the run id
	 * @return
	 * @throws Exception
	 */
	public Run runGet(int runId) throws Exception {
		URL request = new URL(OPENML_URL + API_PART + "run/" + runId);
		Object apiResult = HttpConnector.doApiRequest(request, getApiKey(), verboseLevel);
		if (apiResult instanceof Run) {
			return (Run) apiResult;
		} else {
			throw new DataFormatException("Casting Api Object to Run");
		}
	}

	/**
	 * Deletes a run and all it's important components
	 * 
	 * @param id
	 *            - The numeric id of the run to be deleted.
	 * @return RunDelete - An object containing the id of the deleted run
	 * @throws Exception
	 *             - Can be: API Error (see documentation at openml.org), server
	 *             down, etc.
	 */
	public RunDelete runDelete(int id) throws Exception {
		URL request = new URL(OPENML_URL + API_PART + "run/" + id);
		Object apiResult = HttpConnector.doApiDelete(request, getApiKey(), verboseLevel);
		if (apiResult instanceof RunDelete) {
			return (RunDelete) apiResult;
		} else {
			throw new DataFormatException("Casting Api Object to RunDelete");
		}
	}

	
	/**
	 * Resets the evaluation of a run (admin right required)
	 * 
	 * @param run_id
	 * @return
	 * @throws Exception
	 */
	public RunReset runReset(int run_id) throws Exception {
		URL request = new URL(OPENML_URL + API_PART + "run/reset/" + run_id);
		Object apiResult = HttpConnector.doApiRequest(request, getApiKey(), verboseLevel);
		if (apiResult instanceof RunReset) {
			return (RunReset) apiResult;
		} else {
			throw new DataFormatException("Casting Api Object to RunReset");
		}
	}

	/**
	 * A list with the parameter settings of a setup
	 * 
	 * @param description - a file equivalent to run description, but only featuring the parts important to parameters
	 * @return
	 * @throws Exception
	 */
	public SetupExists setupExists(File description) throws Exception {
		MultipartEntity params = new MultipartEntity();
		if (verboseLevel >= Constants.VERBOSE_LEVEL_XML) {
			System.out.println(Conversion.fileToString(description) + "\n==========");
		}
		params.addPart("description", new FileBody(description));
		URL request = new URL(OPENML_URL + API_PART + "setup/exists");
		Object apiResult = HttpConnector.doApiRequest(request, params, getApiKey(), verboseLevel);
		if (apiResult instanceof SetupExists) {
			return (SetupExists) apiResult;
		} else {
			throw new DataFormatException("Casting Api Object to SetupExists");
		}
	}
	
	/**
	 * A list with the parameter settings of a setup
	 * 
	 * @param setup_id
	 * @return
	 * @throws Exception
	 */
	public SetupParameters setupParameters(int setup_id) throws Exception {
		URL request = new URL(OPENML_URL + API_PART + "setup/" + setup_id);
		Object apiResult = HttpConnector.doApiRequest(request, getApiKey(), verboseLevel);
		if (apiResult instanceof SetupParameters) {
			return (SetupParameters) apiResult;
		} else {
			throw new DataFormatException("Casting Api Object to SetupParameters");
		}
	}

	/**
	 * Tags a setup
	 * 
	 * @param id - the setup id
	 * @param tag - the tag
	 * @return
	 * @throws Exception
	 */
	public SetupTag setupTag(int id, String tag) throws Exception {
		MultipartEntity params = new MultipartEntity();
		params.addPart("setup_id", new StringBody("" + id));
		params.addPart("tag", new StringBody(tag));
		
		URL request = new URL(OPENML_URL + API_PART + "setup/tag");
		Object apiResult = HttpConnector.doApiRequest(request, params, getApiKey(), verboseLevel);
		if (apiResult instanceof SetupTag) {
			return (SetupTag) apiResult;
		} else {
			throw new DataFormatException("Casting Api Object to SetupTag");
		}
	}

	/**
	 * Removes a tag from a setup
	 * 
	 * @param id - the setup id
	 * @param tag - the tag to be removed
	 * @return
	 * @throws Exception
	 */
	public SetupUntag setupUntag(int id, String tag) throws Exception {
		MultipartEntity params = new MultipartEntity();
		params.addPart("setup_id", new StringBody("" + id));
		params.addPart("tag", new StringBody(tag));
		
		URL request = new URL(OPENML_URL + API_PART + "setup/untag");
		Object apiResult = HttpConnector.doApiRequest(request, params, getApiKey(), verboseLevel);
		if (apiResult instanceof SetupUntag) {
			return (SetupUntag) apiResult;
		} else {
			throw new DataFormatException("Casting Api Object to SetupUntag");
		}
	}

	/**
	 * Deletes a setup. Only applicable when no runs are attached. 
	 * 
	 * @param id
	 *            - The numeric id of the setup to be deleted.
	 * @return SetupDelete - An object containing the id of the deleted
	 *         setup
	 * @throws Exception
	 *             - Can be: API Error (see documentation at openml.org), server
	 *             down, etc.
	 */
	public SetupDelete setupDelete(int id) throws Exception {
		URL request = new URL(OPENML_URL + API_PART + "setup/" + id);
		Object apiResult = HttpConnector.doApiDelete(request, getApiKey(), verboseLevel);
		if (apiResult instanceof SetupDelete) {
			return (SetupDelete) apiResult;
		} else {
			throw new DataFormatException("Casting Api Object to SetupDelete");
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
	 * @return
	 * @throws Exception
	 */
	public SetupDifferences setupDifferences(int setupA, int setupB, int task_id, int task_size, int differences) throws Exception {
		MultipartEntity params = new MultipartEntity();
		params.addPart("task_id", new StringBody("" + task_id));
		params.addPart("task_size", new StringBody("" + task_size));
		params.addPart("differences", new StringBody("" + differences));
		
		URL request = new URL(OPENML_URL + API_PART + "setup/differences/" + setupA + "/" + setupB);
		Object apiResult = HttpConnector.doApiRequest(request, params, getApiKey(), verboseLevel);
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
	 * @return
	 * @throws Exception
	 */
	public SetupDifferences setupDifferences(Integer setupA, Integer setupB, Integer task_id)  throws Exception {
		String suffix = setupA + "/" + setupB;
		if (task_id != null) {
			suffix += "/" + task_id;
		}
		
		URL request = new URL(OPENML_URL + API_PART + "setup/differences/" + suffix);
		Object apiResult = HttpConnector.doApiRequest(request, getApiKey(), verboseLevel);
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
		Object apiResult = HttpConnector.doApiRequest(request, params, getApiKey(), verboseLevel);
		if (apiResult instanceof FileUpload) {
			return (FileUpload) apiResult;
		} else {
			throw new DataFormatException("Casting Api Object to UploadFile");
		}
	}

	/**
	 * Returns a scheduled job
	 * 
	 * @param workbench
	 *            - The workbench that will execute the task.
	 * @param task_type_id
	 *            - The task type id that the workbench should execute. Weka
	 *            generally performs Supervised Classification tasks, whereas
	 *            MOA performs Data Stream tasks. For task id's, please see
	 *            openml.org.
	 * @return Job - An object describing the task to be executed
	 * @throws Exception
	 *             - Can be: API Error (see documentation at openml.org), server
	 *             down, no tasks available for this workbench.
	 */
	public Job jobRequest(String workbench, String task_type_id) throws Exception {
		return jobRequest(workbench, task_type_id, null, null, null);
	}
	
	public Job jobRequest(String workbench, String task_type_id, String task_tag, String setup_tag, Integer setup_id) throws Exception {
		MultipartEntity params = new MultipartEntity();
		params.addPart("workbench", new StringBody(workbench));
		params.addPart("task_type_id", new StringBody(task_type_id));
		if (task_tag != null) {
			params.addPart("task_tag", new StringBody(task_tag));
		}
		if (setup_tag != null) {
			params.addPart("setup_tag", new StringBody(setup_tag));
		}
		if (setup_id != null) {
			params.addPart("setup_id", new StringBody(setup_id+""));
		}
		
		URL request = new URL(OPENML_URL + API_PART + "job/request");
		Object apiResult = HttpConnector.doApiRequest(request, params, getApiKey(), verboseLevel);
		if (apiResult instanceof Job) {
			return (Job) apiResult;
		} else {
			throw new DataFormatException("Casting Api Object to Job");
		}
	}
	
	public Study studyGet(String studyAlias, String dataType) throws Exception {
		String suffix = "";
		if (dataType != null) { suffix += "/" + dataType; }
		
		URL request = new URL(OPENML_URL + API_PART + "study/" + studyAlias + suffix);
		Object apiResult = HttpConnector.doApiRequest(request, getApiKey(), verboseLevel);
		if (apiResult instanceof Study) {
			return (Study) apiResult;
		} else {
			throw new DataFormatException("Casting Api Object to Study");
		}
	}
	
	public Study studyGet(int studyId) throws Exception {
		return studyGet("" + studyId, null);
	}
	
	public Study studyGet(int studyId, String dataType) throws Exception {
		return studyGet("" + studyId, dataType);
	}

	public URL getOpenmlFileUrl(Integer file_id, String filename) throws Exception {
		if (filename == null) {
			filename = "file"; }
		String suffix = api_key == null ? "" : "?api_key=" + getApiKey();
		return new URL(OPENML_URL + "data/v1/download/" + file_id + "/" + URLEncoder.encode(filename, "UTF-8") +  suffix );
	}
}
