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
import java.io.Serializable;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.zip.DataFormatException;

import org.json.JSONObject;
import org.openml.apiconnector.algorithms.Caching;
import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.settings.Constants;
import org.openml.apiconnector.settings.Settings;
import org.openml.apiconnector.xml.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.openml.apiconnector.xstream.XstreamXmlMapping;

import com.thoughtworks.xstream.XStream;

public class OpenmlConnector implements Serializable {
    private static final long serialVersionUID = 7362620508675762264L;

    /**
     * When set to true, API will output information.
     */
    private int verboseLevel = 0;

    private final ApiSessionHash sessionHash;

    private final String API_URL;

    /**
     * Creates the instance of the openMl api connector
     * @param url Url of the openMl API
     */
    public OpenmlConnector( String url ) {
        this.API_URL = url;
        this.sessionHash = new ApiSessionHash(this.API_URL, verboseLevel);
    }

    /**
     * Creates a default OpenML Connector with authentication
     */
    public OpenmlConnector() {
        this.API_URL = Settings.BASE_URL;
        this.sessionHash = new ApiSessionHash(this.API_URL, verboseLevel);
    }

    /**
     * Creates a default OpenML Connector with authentication
     * @param username Username that will be used to log into OenMl API
     * @param password Password that will be used to log into OpenML API
     */
    public OpenmlConnector( String username, String password ) {
        this.API_URL = Settings.BASE_URL;
        this.sessionHash = new ApiSessionHash(this.API_URL, verboseLevel);
        sessionHash.set(username, password);
    }

    /**
     * Creates an OpenML Connector to the specified URL with authentication
     */
    public OpenmlConnector( String url, String username, String password ){
        this.API_URL = url;
        this.sessionHash = new ApiSessionHash(url, verboseLevel);
        sessionHash.set(username, password);
    }

    /**
     * Sets crendetial and automatically connect to the service using provided credentials
     * @param username
     * @param password
     * @return
     */
    public boolean setCredentials(String username, String password) {
        return sessionHash.set(username,password);
    }

    public boolean checkCredentials() {
        return sessionHash.checkCredentials();
    }

    public boolean checkCredentials(String username, String password) {
        return sessionHash.checkCredentials(username, password);
    }

    public String getSessionHash() throws Exception {
        return sessionHash.getSessionHash();
    }

    public void setVerboseLevel( int level ) {
        verboseLevel = level;
    }

    /**
     * Returns the URL to which api calls are made
     *
     * @return the API endpoint
     */
    public String getApiUrl() {
        return API_URL + HttpConnector.API_PART;
    }

    /**
     * Authenticates the current user.
     *
     * @return Authenticate - An object containing the Api Session Hash
     * (which can be used to authenticate without username / password)
     * @throws Exception - Can be: API Error (see documentation at openml.org),
     * server down, etc.
     */
    public Authenticate authenticate() throws Exception {
        return sessionHash.openmlAuthenticate();
    }

    /**
     * Retrieves an array of id's off all valid data sets in the system.
     *
     * @return Data - An object containing the all valid data id's
     * @throws Exception - Can be: API Error (see documentation at openml.org),
     * server down, etc.
     */
    public Data listData() throws Exception {
        Object apiResult = HttpConnector.doApiRequest(API_URL, "openml.data", "", sessionHash, verboseLevel );
        if( apiResult instanceof Data){
            return (Data) apiResult;
        } else {
            throw new DataFormatException("Casting Api Object to Data");
        }
    }

    /**
     * Alias for openmlDataDescription.
     *
     * @param did - The data_id of the data description to download.
     * @return DataSetDescription - An object containing the description of the data
     * @throws Exception - Can be: API Error (see documentation at openml.org),
     * server down, etc.
     */
    public DataSetDescription getData( int did ) throws Exception {
        return dataDescription(did);
    }

    /**
     * Retrieves the description of a specified data set.
     *
     * @param did - The data_id of the data description to download.
     * @return DataSetDescription - An object containing the description of the data
     * @throws Exception - Can be: API Error (see documentation at openml.org),
     * server down, etc.
     */
    public DataSetDescription dataDescription( int did ) throws Exception {
        if( Settings.LOCAL_OPERATIONS ) {
            String dsdString = Conversion.fileToString( Caching.cached("datadescription", did ) );
            return (DataSetDescription) XstreamXmlMapping.getInstance().fromXML( dsdString );
        }

        Object apiResult = HttpConnector.doApiRequest(API_URL, "openml.data.description", "&data_id=" + did, sessionHash, verboseLevel );
        if( apiResult instanceof DataSetDescription){
            if( Settings.CACHE_ALLOWED ) { Caching.cache( apiResult, "datadescription", did ); }
            return (DataSetDescription) apiResult;
        } else {
            throw new DataFormatException("Casting Api Object to DataSetDescription");
        }
    }

    public DataTag dataTag( int id, String tag ) throws Exception {
        String qs = "&data_id=" + id + "&tag=" + tag;
        Object apiResult = HttpConnector.doApiRequest(API_URL, "openml.data.tag", qs, sessionHash, verboseLevel );
        if( apiResult instanceof DataTag){
            return (DataTag) apiResult;
        } else {
            throw new DataFormatException("Casting Api Object to DataTag");
        }
    }

    public LicencesList listLicences() throws Exception {
        Object apiResult = HttpConnector.doApiRequest(API_URL, "openml.data.licences", "", sessionHash, verboseLevel);
        if (apiResult instanceof LicencesList) {
            return (LicencesList) apiResult;
        } else {
            throw new DataFormatException("Casting Api Object to LicencesList");
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
    public DataFeature dataFeatures( int did ) throws Exception {
        Object apiResult = HttpConnector.doApiRequest(API_URL, "openml.data.features", "&data_id=" + did, sessionHash, verboseLevel );
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
    public DataQuality dataQuality( int did ) throws Exception {
        Object apiResult = HttpConnector.doApiRequest(API_URL, "openml.data.qualities", "&data_id=" + did, sessionHash, verboseLevel );
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
    public DataQuality dataQuality( Integer did, Integer interval_start, Integer interval_end, Integer interval_size ) throws Exception {
        String queryString = "&data_id=" + did;
        if( interval_start != null ) { queryString += "&interval_start=" + interval_start; }
        if( interval_end   != null ) { queryString += "&interval_end=" + interval_end; }
        if( interval_size  != null ) { queryString += "&interval_size=" + interval_size; }

        Object apiResult = HttpConnector.doApiRequest(API_URL, "openml.data.qualities", queryString, sessionHash, verboseLevel );

        if( apiResult instanceof DataQuality){
            return (DataQuality) apiResult;
        } else {
            throw new DataFormatException("Casting Api Object to DataQuality");
        }
    }

    public DataFeatureUpload uploadDataFeature( File description ) throws Exception {
        MultipartEntity params = new MultipartEntity();
        params.addPart("description", new FileBody(description));

        if(verboseLevel >= Constants.VERBOSE_LEVEL_ARFF ) {
            System.out.println( Conversion.fileToString(description) + "\n==========\n" );
        }

        Object apiResult = HttpConnector.doApiRequest(API_URL, "openml.data.features.upload", "", params, sessionHash, verboseLevel );
        if( apiResult instanceof DataFeatureUpload){
            return (DataFeatureUpload) apiResult;
        } else {
            throw new DataFormatException("Casting Api Object to DataFeatureUpload");
        }
    }

    public DataQualityUpload uploadDataQuality( File description ) throws Exception {
        MultipartEntity params = new MultipartEntity();
        params.addPart("description", new FileBody(description));

        Object apiResult = HttpConnector.doApiRequest(API_URL, "openml.data.qualities.upload", "", params, sessionHash, verboseLevel );
        if( apiResult instanceof DataQualityUpload){
            return (DataQualityUpload) apiResult;
        } else {
            throw new DataFormatException("Casting Api Object to DataQualityUpload");
        }
    }

    public DataQualityList listDataQuality() throws Exception {
        Object apiResult = HttpConnector.doApiRequest(API_URL, "openml.data.qualities.list", "", sessionHash, verboseLevel );
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
    public Implementation getImplementation(int implementation_id) throws Exception {
        Object apiResult = HttpConnector.doApiRequest(API_URL, "openml.implementation.get", "&implementation_id=" + implementation_id, sessionHash, verboseLevel );
        if( apiResult instanceof Implementation){
            return (Implementation) apiResult;
        } else {
            throw new DataFormatException("Casting Api Object to Implementation");
        }
    }

    public ImplementationTag tagImplementation( int id, String tag ) throws Exception {
        String qs = "&implementation_id=" + id + "&tag=" + tag;
        Object apiResult = HttpConnector.doApiRequest(API_URL, "openml.implementation.tag", qs, sessionHash, verboseLevel );
        if( apiResult instanceof ImplementationTag){
            return (ImplementationTag) apiResult;
        } else {
            throw new DataFormatException("Casting Api Object to ImplementationTag");
        }
    }

    /**
     * @return ImplementationOwned - An object containing all implementation_ids that are owned by the current user.
     * @throws Exception - Can be: API Error (see documentation at openml.org),
     * server down, etc.
     */
    public ImplementationOwned verifyImplementationOwnership() throws Exception {
        MultipartEntity params = new MultipartEntity();

        Object apiResult = HttpConnector.doApiRequest(API_URL, "openml.implementation.owned", "", params, sessionHash, verboseLevel );
        if( apiResult instanceof ImplementationOwned){
            return (ImplementationOwned) apiResult;
        } else {
            throw new DataFormatException("Casting Api Object to ImplementationOwned");
        }
    }

    /**
     * @param id - The numeric id of the implementation to be deleted.
     * @return ImplementationDelete - An object containing the id of the deleted implementation
     * @throws Exception - Can be: API Error (see documentation at openml.org),
     * server down, etc.
     */
    public ImplementationDelete deleteImplementation( int id ) throws Exception {
        MultipartEntity params = new MultipartEntity();
        params.addPart("implementation_id",new StringBody(""+id));

        Object apiResult = HttpConnector.doApiRequest(API_URL, "openml.implementation.delete", "", params, sessionHash, verboseLevel );
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
    public ImplementationExists implementationExists( String name, String external_version ) throws Exception {
        Object apiResult = HttpConnector.doApiRequest(API_URL, "openml.implementation.exists", "&name=" + name + "&external_version=" + external_version, sessionHash, verboseLevel );
        if( apiResult instanceof ImplementationExists){
            return (ImplementationExists) apiResult;
        } else {
            throw new DataFormatException("Casting Api Object to ImplementationExists");
        }
    }

    /**
     * List tasks of specific type
     * @param task_type_id
     * @return
     * @throws Exception
     */
    public Tasks listTasks( int task_type_id ) throws Exception {
        Object apiResult = HttpConnector.doApiRequest(API_URL, "openml.tasks", "&task_type_id=" + task_type_id, sessionHash, verboseLevel );
        if( apiResult instanceof Tasks){
            return (Tasks) apiResult;
        } else {
            throw new DataFormatException("Casting Api Object to Tasks");
        }
    }

    /**
     * @param task_id - The numeric id of the task to be obtained.
     * @return Task - An object describing the task
     * @throws Exception - Can be: API Error (see documentation at openml.org),
     * server down, etc.
     */
    public Task getTask( int task_id ) throws Exception {
        if( Settings.LOCAL_OPERATIONS ) {
            String taskXml = Conversion.fileToString( Caching.cached("task", task_id ) );
            return (Task) XstreamXmlMapping.getInstance().fromXML( taskXml );
        }

        Object apiResult = HttpConnector.doApiRequest(API_URL, "openml.task.get", "&task_id=" + task_id, sessionHash, verboseLevel );
        if( apiResult instanceof Task){
            if( Settings.CACHE_ALLOWED ) { Caching.cache( apiResult, "task", task_id ); }
            return (Task) apiResult;
        } else {
            throw new DataFormatException("Casting Api Object to Task");
        }
    }

    public TaskTag tagTask( int id, String tag ) throws Exception {
        String qs = "&task_id=" + id + "&tag=" + tag;
        Object apiResult = HttpConnector.doApiRequest(API_URL, "openml.task.tag", qs, sessionHash, verboseLevel );
        if( apiResult instanceof TaskTag){
            return (TaskTag) apiResult;
        } else {
            throw new DataFormatException("Casting Api Object to TaskTag");
        }
    }

    public TaskEvaluations taskEvaluations( int task_id ) throws Exception {
        Object apiResult = HttpConnector.doApiRequest( API_URL, "openml.task.evaluations", "&task_id=" + task_id, sessionHash, verboseLevel );
        if( apiResult instanceof TaskEvaluations) {
            return (TaskEvaluations) apiResult;
        } else {
            throw new DataFormatException("Casting Api Object to TaskEvaluations");
        }
    }

    public TaskEvaluations taskEvaluations( Integer task_id, Integer interval_start, Integer interval_end, Integer interval_size ) throws Exception {
        String queryString = "&task_id=" + task_id;
        if( interval_start != null ) { queryString += "&interval_start=" + interval_start; }
        if( interval_end   != null ) { queryString += "&interval_end=" + interval_end; }
        if( interval_size  != null ) { queryString += "&interval_size=" + interval_size; }

        Object apiResult = HttpConnector.doApiRequest(API_URL, "openml.task.evaluations", queryString, sessionHash, verboseLevel );
        if( apiResult instanceof TaskEvaluations){
            return (TaskEvaluations) apiResult;
        } else {
            throw new DataFormatException("Casting Api Object to TaskEvaluations");
        }
    }

    /**
     * @param description - An XML file describing the data. See documentation at openml.org
     * @param dataset - The actual dataset. Preferably in ARFF format, but almost everything is OK.
     * @return UploadDataSet - An object containing information on the data upload.
     * @throws Exception - Can be: API Error (see documentation at openml.org),
     * server down, etc.
     */
    public UploadDataSet uploadData( File description, File dataset ) throws Exception {
        MultipartEntity params = new MultipartEntity();
        params.addPart("description", new FileBody(description));
        if( dataset != null) params.addPart("dataset", new FileBody(dataset));

        Object apiResult = HttpConnector.doApiRequest(API_URL, "openml.data.upload", "", params, sessionHash, verboseLevel );
        if( apiResult instanceof UploadDataSet){
            return (UploadDataSet) apiResult;
        } else {
            throw new DataFormatException("Casting Api Object to UploadDataSet");
        }
    }

    /**
     * @param description - A DataSetDescription describing the data. Should contain the url field.
     * @param dataset - The actual dataset. Preferably in ARFF format, but almost everything is OK.
     * @return UploadDataSet - An object containing information on the data upload.
     * @throws Exception - Can be: API Error (see documentation at openml.org),
     * server down, etc.
     */
    public UploadDataSet uploadData( DataSetDescription description, File dataset) throws Exception {
        XStream xstream = XstreamXmlMapping.getInstance();
        return uploadData(Conversion.stringToTempFile(xstream.toXML(description), "description", "xml"), dataset);
    }

    /**
     * @param description - An XML file describing the data. See documentation at openml.org. Should contain the url field.
     * @return UploadDataSet - An object containing information on the data upload.
     * @throws Exception - Can be: API Error (see documentation at openml.org),
     * server down, etc.
     */
    public UploadDataSet uploadData( File description ) throws Exception {
        return uploadData(description, null);
    }

    /**
     * @param description - An XML file describing the implementation. See documentation at openml.org.
     * @param binary - A file containing the implementation binary.
     * @param source - A file containing the implementation source.
     * @return UploadImplementation - An object containing information on the implementation upload.
     * @throws Exception - Can be: API Error (see documentation at openml.org),
     * server down, etc.
     */
    public UploadImplementation uploadImplementation( File description, File binary, File source ) throws Exception {
        MultipartEntity params = new MultipartEntity();
        params.addPart("description", new FileBody(description));
        if(source != null)
            params.addPart("source", new FileBody(source));
        if(binary != null)
            params.addPart("binary", new FileBody(binary));

        Object apiResult = HttpConnector.doApiRequest(API_URL, "openml.implementation.upload", "", params, sessionHash, verboseLevel );
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
     * @return UploadRun - An object containing information on the implementation upload.
     * @throws Exception - Can be: API Error (see documentation at openml.org),
     * server down, etc.
     */
    public UploadRun uploadRun( File description, Map<String,File> output_files ) throws Exception {
        MultipartEntity params = new MultipartEntity();
        if(verboseLevel >= Constants.VERBOSE_LEVEL_ARFF ) {
            System.out.println( Conversion.fileToString(output_files.get("predictions")) + "\n==========\n" );
        }
        if(verboseLevel >= Constants.VERBOSE_LEVEL_XML ) {
            System.out.println( Conversion.fileToString(description)+"\n==========");
        }
        params.addPart("description", new FileBody(description));
        for( String s : output_files.keySet() ) {
            params.addPart(s,new FileBody(output_files.get(s)));
        }
        Object apiResult = HttpConnector.doApiRequest(API_URL, "openml.run.upload", "", params, sessionHash, verboseLevel );
        if( apiResult instanceof UploadRun){
            return (UploadRun) apiResult;
        } else {
            throw new DataFormatException("Casting Api Object to UploadRun");
        }
    }

    public RunTag tagRun( int id, String tag ) throws Exception {
        String qs = "&run_id=" + id + "&tag=" + tag;
        Object apiResult = HttpConnector.doApiRequest(API_URL, "openml.run.tag", qs, sessionHash, verboseLevel );
        if( apiResult instanceof RunTag){
            return (RunTag) apiResult;
        } else {
            throw new DataFormatException("Casting Api Object to RunTag");
        }
    }

    public RunEvaluate evaluateRun( File description ) throws Exception {
        MultipartEntity params = new MultipartEntity();
        params.addPart("description", new FileBody(description));

        Object apiResult = HttpConnector.doApiRequest(API_URL, "openml.run.evaluate", "", params, sessionHash, verboseLevel );
        if( apiResult instanceof RunEvaluate){
            return (RunEvaluate) apiResult;
        } else {
            throw new DataFormatException("Casting Api Object to RunEvaluate");
        }
    }

    public Run getRun(int runId) throws Exception {
        Object apiResult = HttpConnector.doApiRequest(API_URL, "openml.run.get", "&run_id=" + runId, sessionHash, verboseLevel);
        if( apiResult instanceof Run){
            return (Run) apiResult;
        } else {
            throw new DataFormatException("Casting Api Object to Task");
        }
    }

    /**
     * @param id - The numeric id of the run to be deleted.
     * @return ImplementationDelete - An object containing the id of the deleted implementation
     * @throws Exception - Can be: API Error (see documentation at openml.org),
     * server down, etc.
     */
    public RunDelete deleteRun( int id ) throws Exception {
        MultipartEntity params = new MultipartEntity();
        params.addPart("run_id",new StringBody(""+id));

        Object apiResult = HttpConnector.doApiRequest(API_URL, "openml.run.delete", "", params, sessionHash, verboseLevel );
        if( apiResult instanceof RunDelete){
            return (RunDelete) apiResult;
        } else {
            throw new DataFormatException("Casting Api Object to RunDelete");
        }
    }

    public RunReset resetRun( int run_id ) throws Exception {
        MultipartEntity params = new MultipartEntity();
        params.addPart("run_id", new StringBody(""+run_id));

        Object apiResult = HttpConnector.doApiRequest(API_URL, "openml.run.reset", "", params, sessionHash, verboseLevel );
        if( apiResult instanceof RunReset){
            return (RunReset) apiResult;
        } else {
            throw new DataFormatException("Casting Api Object to RunReset");
        }
    }

    public FileUpload uploadFile( File file ) throws Exception {
        MultipartEntity params = new MultipartEntity();
        if(verboseLevel >= Constants.VERBOSE_LEVEL_ARFF ) {
            System.out.println( Conversion.fileToString(file) + "\n==========\n" );
        }
        params.addPart("file", new FileBody(file));

        Object apiResult = HttpConnector.doApiRequest(API_URL, "openml.file.upload", "", params, sessionHash, verboseLevel );
        if( apiResult instanceof FileUpload){
            return (FileUpload) apiResult;
        } else {
            throw new DataFormatException("Casting Api Object to UploadFile");
        }
    }

    public SetupTag setupTag( int id, String tag ) throws Exception {
        String qs = "&setup_id=" + id + "&tag=" + tag;
        Object apiResult = HttpConnector.doApiRequest(API_URL, "openml.setup.tag", qs, sessionHash, verboseLevel );
        if( apiResult instanceof SetupTag){
            return (SetupTag) apiResult;
        } else {
            throw new DataFormatException("Casting Api Object to SetupTag");
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
    public Job jobGet( String workbench, String task_type_id ) throws Exception {
        Object apiResult = HttpConnector.doApiRequest(API_URL, "openml.job.get", "&workbench=" + workbench + "&task_type_id=" + task_type_id, sessionHash, verboseLevel );
        if( apiResult instanceof Job ){
            return (Job) apiResult;
        } else {
            throw new DataFormatException("Casting Api Object to Job");
        }
    }

    /**
     * @param sql - The query to be executed
     * @return An JSON object containing the result of the query, along with meta data
     * @throws Exception
     */
    public JSONObject freeQuery( String sql ) throws Exception {
        return new JSONObject( getStringFromUrl( API_URL + "api_query/?q=" + URLEncoder.encode( sql, "ISO-8859-1" ) + "&hash=" + getSessionHash() ) );
    }

    /**
     * @param url - The URL to obtain
     * @return String - The content of the URL
     * @throws IOException - Can be: server down, etc.
     */
    public static String getStringFromUrl( String url ) throws IOException {
        String result = IOUtils.toString(  new URL( url ) );
	/*	if(API_VERBOSE_LEVEL >= Constants.VERBOSE_LEVEL_XML) {
			System.out.println("===== REQUEST URI: " + url + " (Content Length: "+result.length()+") =====\n" + result + "\n=====\n");
		} */
        return result;
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

    public URL getOpenmlFileUrl( int id, String filename ) throws Exception {
        if( filename == null ) filename = "file";
        String suffix = "?session_hash=" + sessionHash.getSessionHash();
        return new URL( API_URL + "data/download/" + id + "/" + filename + suffix );
    }
}
