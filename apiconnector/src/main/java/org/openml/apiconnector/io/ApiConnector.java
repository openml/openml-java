/*
 *  OpenmlApiConnector - Java integration of the OpenML Web API
 *  Copyright (C) 2014 
 *  @author Jan N. van Rijn (j.n.van.rijn@liacs.leidenuniv.nl)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *  
 */
package org.openml.apiconnector.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.DataFormatException;

import org.json.JSONException;
import org.json.JSONObject;
import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.algorithms.Hashing;
import org.openml.apiconnector.settings.Constants;
import org.openml.apiconnector.settings.Settings;
import org.openml.apiconnector.xml.ApiError;
import org.openml.apiconnector.xml.Authenticate;
import org.openml.apiconnector.xml.Data;
import org.openml.apiconnector.xml.DataFeature;
import org.openml.apiconnector.xml.DataFeatureUpload;
import org.openml.apiconnector.xml.DataQuality;
import org.openml.apiconnector.xml.DataQualityList;
import org.openml.apiconnector.xml.DataQualityUpload;
import org.openml.apiconnector.xml.DataSetDescription;
import org.openml.apiconnector.xml.Implementation;
import org.openml.apiconnector.xml.ImplementationExists;
import org.openml.apiconnector.xml.Job;
import org.openml.apiconnector.xml.RunEvaluate;
import org.openml.apiconnector.xml.RunReset;
import org.openml.apiconnector.xml.Task;
import org.openml.apiconnector.xml.TaskEvaluations;
import org.openml.apiconnector.xml.UploadDataSet;
import org.openml.apiconnector.xml.UploadImplementation;
import org.openml.apiconnector.xml.UploadRun;
import org.openml.apiconnector.xstream.XstreamXmlMapping;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.openml.apiconnector.xml.ImplementationDelete;
import org.openml.apiconnector.xml.ImplementationOwned;

import com.thoughtworks.xstream.XStream;

public class ApiConnector implements Serializable {
	
	private static final long serialVersionUID = 7362620508675762264L;
	private static final String API_PART = "rest_api/";
	private static final XStream xstream = XstreamXmlMapping.getInstance();
	
	private final String API_URL; 
	
	public ApiConnector() {
		this.API_URL = Settings.BASE_URL;
	}
	
	public ApiConnector( String url ) {
		this.API_URL = url;
	}
	
	/**
	 * Returns the URL to which api calls are made
	 * 
	 * @return the API endpoint
	 */
	public String getApiUrl() {
		return API_URL + API_PART;
	}
	
	/**
	 * Authenticates the current user. 
	 * 
	 * @param username - The username that is used for authentication
	 * @param password - The password used for authentication
	 * @return Authenticate - An object containing the Api Session Hash 
	 * (which can be used to authenticate without username / password)
	 * @throws Exception - Can be: API Error (see documentation at openml.org), 
	 * server down, etc.
	 */
	public Authenticate openmlAuthenticate( String username, String password ) throws Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>(2);
		params.add(new BasicNameValuePair("username", username));
		params.add(new BasicNameValuePair("password", Hashing.md5( password )));
		
		Object apiResult = doApiRequest("openml.authenticate", "", new UrlEncodedFormEntity(params, "UTF-8"));
        if( apiResult instanceof Authenticate){
        	return (Authenticate) apiResult;
        } else {
        	throw new DataFormatException("Casting Api Object to Authenticate");
        }
	}
	/**
	 * Retrieves an array of id's off all valid data sets in the system. 
	 * 
	 * @return Data - An object containing the all valid data id's
	 * @throws Exception - Can be: API Error (see documentation at openml.org), 
	 * server down, etc.
	 */
	public Data openmlData() throws Exception {
		Object apiResult = doApiRequest("openml.data", "" );
        if( apiResult instanceof Data){
        	return (Data) apiResult;
        } else {
        	throw new DataFormatException("Casting Api Object to Data");
        }
	}
	
	/**
	 * Retrieves the description of a specified data set. 
	 * 
	 * @param did - The data_id of the data description to download. 
	 * @return DataSetDescription - An object containing the description of the data
	 * @throws Exception - Can be: API Error (see documentation at openml.org), 
	 * server down, etc.
	 */
	public DataSetDescription openmlDataDescription( int did ) throws Exception {
		Object apiResult = doApiRequest("openml.data.description", "&data_id=" + did );
        if( apiResult instanceof DataSetDescription){
        	return (DataSetDescription) apiResult;
        } else {
        	throw new DataFormatException("Casting Api Object to DataSetDescription");
        }
	}
	
	/**
	 * Retrieves the features of a specified data set. 
	 * 
	 * @param did - The data_id of the data features to download. 
	 * @return DataFeatures - An object containing the features of the data
	 * @throws Exception - Can be: API Error (see documentation at openml.org), 
	 * server down, etc.
	 */
	public DataFeature openmlDataFeatures( int did ) throws Exception {
		Object apiResult = doApiRequest("openml.data.features", "&data_id=" + did );
        if( apiResult instanceof DataFeature){
        	return (DataFeature) apiResult;
        } else {
        	throw new DataFormatException("Casting Api Object to DataFeature");
        }
	}
	
	/**
	 * Retrieves the qualities (meta-features) of a specified data set. 
	 * 
	 * @param did - The data_id of the data features to download. 
	 * @return DataFeatures - An object containing the qualities of the data
	 * @throws Exception - Can be: API Error (see documentation at openml.org), 
	 * server down, etc.
	 */
	public DataQuality openmlDataQuality( int did ) throws Exception {
		Object apiResult = doApiRequest("openml.data.qualities", "&data_id=" + did );
        if( apiResult instanceof DataQuality){
        	return (DataQuality) apiResult;
        } else {
        	throw new DataFormatException("Casting Api Object to DataQuality");
        }
	}
	
	/**
	 * Retrieves the qualities (meta-features) of a specified data set on a specified interval. 
	 * 
	 * @param did - The data_id of the data features to download. 
	 * @param interval_start - Constraint on where the interval should start. Null if no constraints
	 * @param interval_end - Constraint on where the interval should end. Null if no constraints
	 * @param interval_size - Constraint on where interval sizes. Null if no constraints
	 * @return DataFeatures - An object containing the qualities of the data
	 * @throws Exception - Can be: API Error (see documentation at openml.org), 
	 * server down, etc.
	 */
	public DataQuality openmlDataQuality( Integer did, Integer interval_start, Integer interval_end, Integer interval_size ) throws Exception {
		String queryString = "&data_id=" + did;
		if( interval_start != null ) { queryString += "&interval_start=" + interval_start; }
		if( interval_end   != null ) { queryString += "&interval_end=" + interval_end; }
		if( interval_size  != null ) { queryString += "&interval_size=" + interval_size; }
		
		Object apiResult = doApiRequest("openml.data.qualities", queryString  );
		
        if( apiResult instanceof DataQuality){
        	return (DataQuality) apiResult;
        } else {
        	throw new DataFormatException("Casting Api Object to DataQuality");
        }
	}
	
	public DataFeatureUpload openmlDataFeatureUpload( File description, String session_hash ) throws Exception {
		MultipartEntity params = new MultipartEntity();
		params.addPart("description", new FileBody(description));
		params.addPart("session_hash",new StringBody(session_hash));
		
		Object apiResult = doApiRequest("openml.data.features.upload", "", params );
		if( apiResult instanceof DataFeatureUpload){
        	return (DataFeatureUpload) apiResult;
        } else {
        	throw new DataFormatException("Casting Api Object to DataFeatureUpload");
        }
	}
	
	public DataQualityUpload openmlDataQualityUpload( File description, String session_hash ) throws Exception {
		MultipartEntity params = new MultipartEntity();
		params.addPart("description", new FileBody(description));
		params.addPart("session_hash",new StringBody(session_hash));
		
		Object apiResult = doApiRequest("openml.data.qualities.upload", "", params );
		if( apiResult instanceof DataQualityUpload){
        	return (DataQualityUpload) apiResult;
        } else {
        	throw new DataFormatException("Casting Api Object to DataQualityUpload");
        }
	}
	
	public DataQualityList openmlDataQualityList() throws Exception {
		Object apiResult = doApiRequest("openml.data.qualities.list", "" );
		if( apiResult instanceof DataQualityList){
        	return (DataQualityList) apiResult;
        } else {
        	throw new DataFormatException("Casting Api Object to DataQualityList");
        }
	}
	
	/**
	 * @param implementation_id - Numeric ID of the implementation to be obtained. 
	 * @return Implementation - An object containing the description of the implementation
	 * @throws Exception - Can be: API Error (see documentation at openml.org), 
	 * server down, etc.
	 */
	public Implementation openmlImplementationGet(int implementation_id) throws Exception {
		Object apiResult = doApiRequest("openml.implementation.get", "&implementation_id=" + implementation_id );
        if( apiResult instanceof Implementation){
        	return (Implementation) apiResult;
        } else {
        	throw new DataFormatException("Casting Api Object to Implementation");
        }
	}
	
	/**
	 * @param session_hash - A session hash (obtainable by openmlAuthenticate)
	 * @return ImplementationOwned - An object containing all implementation_ids that are owned by the current user.
	 * @throws Exception - Can be: API Error (see documentation at openml.org), 
	 * server down, etc.
	 */
	public ImplementationOwned openmlImplementationOwned( String session_hash ) throws Exception {
		MultipartEntity params = new MultipartEntity();
		params.addPart("session_hash",new StringBody(session_hash));
		
		Object apiResult = doApiRequest("openml.implementation.owned", "", params);
		if( apiResult instanceof ImplementationOwned){
        	return (ImplementationOwned) apiResult;
        } else {
        	throw new DataFormatException("Casting Api Object to ImplementationOwned");
        }
	}
	
	/**
	 * @param id - The numeric id of the implementation to be deleted. 
	 * @param session_hash - A session hash (obtainable by openmlAuthenticate)
	 * @return ImplementationDelete - An object containing the id of the deleted implementation
	 * @throws Exception - Can be: API Error (see documentation at openml.org), 
	 * server down, etc.
	 */
	public ImplementationDelete openmlImplementationDelete( int id, String session_hash ) throws Exception {
		MultipartEntity params = new MultipartEntity();
		params.addPart("implementation_id",new StringBody(""+id));
		params.addPart("session_hash",new StringBody(session_hash));
		
		Object apiResult = doApiRequest("openml.implementation.delete", "", params);
		if( apiResult instanceof ImplementationDelete){
        	return (ImplementationDelete) apiResult;
        } else {
        	throw new DataFormatException("Casting Api Object to ImplementationDelete");
        }
	}
	
	/**
	 * @param name - The name of the implementation to be checked
	 * @param external_version - The external version (workbench version). If not a proper revision number is available, 
	 * it is recommended to use a MD5 hash of the source code.
	 * @return ImplementationExists - An object describing whether this implementation is already known on the server.
	 * @throws Exception - Can be: API Error (see documentation at openml.org), 
	 * server down, etc.
	 */
	public ImplementationExists openmlImplementationExists( String name, String external_version ) throws Exception {
		Object apiResult = doApiRequest("openml.implementation.exists", "&name=" + name + "&external_version=" + external_version );
        if( apiResult instanceof ImplementationExists){
        	return (ImplementationExists) apiResult;
        } else {
        	throw new DataFormatException("Casting Api Object to ImplementationExists");
        }
	}
	
	/**
	 * @param task_id - The numeric id of the task to be obtained.
	 * @return Task - An object describing the task
	 * @throws Exception - Can be: API Error (see documentation at openml.org), 
	 * server down, etc.
	 */
	public Task openmlTaskSearch( int task_id ) throws Exception {
		Object apiResult = doApiRequest("openml.task.search", "&task_id=" + task_id );
        if( apiResult instanceof Task){
        	return (Task) apiResult;
        } else {
        	throw new DataFormatException("Casting Api Object to Task");
        }
	}
	
	public TaskEvaluations openmlTaskEvaluations( int task_id ) throws Exception {
		Object apiResult = doApiRequest("openml.task.evaluations", "&task_id=" + task_id );
        if( apiResult instanceof TaskEvaluations){
        	return (TaskEvaluations) apiResult;
        } else {
        	throw new DataFormatException("Casting Api Object to TaskEvaluations");
        }
	}
	
	public TaskEvaluations openmlTaskEvaluations( Integer task_id, Integer interval_start, Integer interval_end, Integer interval_size ) throws Exception {
		String queryString = "&task_id=" + task_id;
		if( interval_start != null ) { queryString += "&interval_start=" + interval_start; }
		if( interval_end   != null ) { queryString += "&interval_end=" + interval_end; }
		if( interval_size  != null ) { queryString += "&interval_size=" + interval_size; }
		
		Object apiResult = doApiRequest("openml.task.evaluations",  queryString );
        if( apiResult instanceof TaskEvaluations){
        	return (TaskEvaluations) apiResult;
        } else {
        	throw new DataFormatException("Casting Api Object to TaskEvaluations");
        }
	}
	
	/**
	 * @param description - An XML file describing the data. See documentation at openml.org
	 * @param dataset - The actual dataset. Preferably in ARFF format, but almost everything is OK. 
	 * @param session_hash - A session hash (obtainable by openmlAuthenticate)
	 * @return UploadDataSet - An object containing information on the data upload. 
	 * @throws Exception - Can be: API Error (see documentation at openml.org), 
	 * server down, etc.
	 */
	public UploadDataSet openmlDataUpload( File description, File dataset, String session_hash ) throws Exception {
		MultipartEntity params = new MultipartEntity();
		params.addPart("description", new FileBody(description));
		if( dataset != null) params.addPart("dataset", new FileBody(dataset));
		params.addPart("session_hash",new StringBody(session_hash));
        
        Object apiResult = doApiRequest("openml.data.upload", "", params);
        if( apiResult instanceof UploadDataSet){
        	return (UploadDataSet) apiResult;
        } else {
        	throw new DataFormatException("Casting Api Object to UploadDataSet");
        }
	}
	
	/**
	 * @param description - An XML file describing the data. See documentation at openml.org. Should contain the url field.
	 * @param session_hash - A session hash (obtainable by openmlAuthenticate)
	 * @return UploadDataSet - An object containing information on the data upload. 
	 * @throws Exception - Can be: API Error (see documentation at openml.org), 
	 * server down, etc.
	 */
	public UploadDataSet openmlDataUpload( File description, String session_hash ) throws Exception {
		return openmlDataUpload(description, null, session_hash);
	}
	
	/**
	 * @param description - An XML file describing the implementation. See documentation at openml.org.
	 * @param binary - A file containing the implementation binary. 
	 * @param source - A file containing the implementation source.
	 * @param session_hash - A session hash (obtainable by openmlAuthenticate)
	 * @return UploadImplementation - An object containing information on the implementation upload. 
	 * @throws Exception - Can be: API Error (see documentation at openml.org), 
	 * server down, etc.
	 */
	public UploadImplementation openmlImplementationUpload( File description, File binary, File source, String session_hash ) throws Exception {
		MultipartEntity params = new MultipartEntity();
		params.addPart("description", new FileBody(description));
		if(source != null)
			params.addPart("source", new FileBody(source));
		if(binary != null)
			params.addPart("binary", new FileBody(binary));
		params.addPart("session_hash",new StringBody(session_hash));
		
        Object apiResult = doApiRequest("openml.implementation.upload", "", params);
        if( apiResult instanceof UploadImplementation){
        	return (UploadImplementation) apiResult;
        } else {
        	throw new DataFormatException("Casting Api Object to UploadImplementation");
        }
	}
	
	/**
	 * @param description - An XML file describing the run. See documentation at openml.org.
	 * @param output_files - A Map<String,File> containing all relevant output files. Key "predictions" 
	 * usually contains the predictions that were generated by this run. 
	 * @param session_hash - A session hash (obtainable by openmlAuthenticate)
	 * @return UploadRun - An object containing information on the implementation upload. 
	 * @throws Exception - Can be: API Error (see documentation at openml.org), 
	 * server down, etc.
	 */
	public UploadRun openmlRunUpload( File description, Map<String,File> output_files, String session_hash ) throws Exception {
		MultipartEntity params = new MultipartEntity();
		if(Settings.API_VERBOSE_LEVEL >= Constants.VERBOSE_LEVEL_ARFF ) {
			System.out.println( Conversion.fileToString(output_files.get("predictions")) + "\n==========\n" );
		}
		if(Settings.API_VERBOSE_LEVEL >= Constants.VERBOSE_LEVEL_XML ) {
			System.out.println( Conversion.fileToString(description)+"\n==========");
		}
		params.addPart("description", new FileBody(description));
		for( String s : output_files.keySet() ) {
			params.addPart(s,new FileBody(output_files.get(s)));
		}
		params.addPart("session_hash",new StringBody(session_hash));
		Object apiResult = doApiRequest("openml.run.upload", "", params);
        if( apiResult instanceof UploadRun){
        	return (UploadRun) apiResult;
        } else {
        	throw new DataFormatException("Casting Api Object to UploadRun");
        }
	}
	
	public RunEvaluate openmlRunEvaluate( File description, String session_hash ) throws Exception {
		MultipartEntity params = new MultipartEntity();
		params.addPart("description", new FileBody(description));
		params.addPart("session_hash",new StringBody(session_hash));
		
		Object apiResult = doApiRequest("openml.run.evaluate", "", params );
		if( apiResult instanceof RunEvaluate){
        	return (RunEvaluate) apiResult;
        } else {
        	throw new DataFormatException("Casting Api Object to RunEvaluate");
        }
	}
	
	public RunReset openmlRunReset( int run_id, String session_hash ) throws Exception {
		MultipartEntity params = new MultipartEntity();
		params.addPart("run_id", new StringBody(""+run_id));
		params.addPart("session_hash",new StringBody(session_hash));
		
		Object apiResult = doApiRequest("openml.run.reset", "", params );
		if( apiResult instanceof RunReset){
        	return (RunReset) apiResult;
        } else {
        	throw new DataFormatException("Casting Api Object to RunReset");
        }
	}
	
	/**
	 * @param workbench - The workbench that will execute the task.
	 * @param task_type_id - The task type id that the workbench should execute. 
	 * Weka generally performs Supervised Classification tasks, whereas MOA performs 
	 * Data Stream tasks. For task id's, please see openml.org.
	 * @return Job - An object describing the task to be executed
	 * @throws Exception - Can be: API Error (see documentation at openml.org), 
	 * server down, no tasks available for this workbench.
	 */
	public Job openmlJobGet( String workbench, String task_type_id ) throws Exception {
		Object apiResult = doApiRequest("openml.job.get", "&workbench=" + workbench + "&task_type_id=" + task_type_id );
        if( apiResult instanceof Job ){
        	return (Job) apiResult;
        } else {
        	throw new DataFormatException("Casting Api Object to Job");
        }
	}
	
	/**
	 * @param sql - The query to be executed 
	 * @return An JSON object containing the result of the query, along with meta data
	 * @throws JSONException
	 * @throws IOException
	 */
	public JSONObject openmlFreeQuery( String sql ) throws JSONException, IOException {
		return new JSONObject( getStringFromUrl( API_URL + "api_query/?q=" + URLEncoder.encode( sql, "ISO-8859-1" ) ) );
	}
	
	/**
	 * @param url - The URL to obtain
	 * @return String - The content of the URL
	 * @throws IOException - Can be: server down, etc.
	 */
	public static String getStringFromUrl( String url ) throws IOException {
		return IOUtils.toString(  new URL( url ) );
	}
	
	/**
	 * @param url - The URL to obtain
	 * @param filepath - Where to safe the file.
	 * @return File - a pointer to the file that was saved. 
	 * @throws IOException - Can be: server down, etc.
	 */
	public static File getFileFromUrl( String url, String filepath ) throws IOException {
		File file = new File( filepath );
		FileUtils.copyURLToFile( new URL(url), file );
		return file;
	}
	
	public URL getOpenmlFileUrl( int id ) throws MalformedURLException, IOException {
		return new URL( API_URL + "files/download/" + id + "/filename" );
	}
	
	private Object doApiRequest(String function, String queryString) throws Exception {
		return doApiRequest(function, queryString, null);
	}
	
	private Object doApiRequest(String function, String queryString, HttpEntity entity) throws Exception {
		String result = "";
		HttpClient httpclient = new DefaultHttpClient();
		String requestUri = API_URL + API_PART + "?f=" + function + queryString;
		long contentLength = 0;
		try {
            HttpPost httppost = new HttpPost( requestUri );
            
            if(entity != null) {
            	httppost.setEntity(entity);
            }
            
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity resEntity = response.getEntity();
            if (resEntity != null) {
            	result = httpEntitiToString(resEntity);
                contentLength = resEntity.getContentLength();
            } else {
            	throw new Exception("An exception has occured while reading data input stream. ");
            }
		} finally {
            try { httpclient.getConnectionManager().shutdown(); } catch (Exception ignore) {}
        }
		if(Settings.API_VERBOSE_LEVEL >= Constants.VERBOSE_LEVEL_XML)
			System.out.println("===== REQUEST URI: " + requestUri + " (Content Length: "+contentLength+") =====\n" + result + "\n=====\n");
		
		Object apiResult = xstream.fromXML(result);
		if(apiResult instanceof ApiError) {
			ApiError apiError = (ApiError) apiResult;
			throw new ApiException( Integer.parseInt( apiError.getCode() ), apiError.getMessage() );
		}
		return apiResult;
	}
	
	private static String httpEntitiToString(HttpEntity resEntity) throws IOException {
		StringWriter writer = new StringWriter();
		IOUtils.copy(new InputStreamReader( resEntity.getContent() ), writer );
		return writer.toString();
	}
}
