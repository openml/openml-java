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
import java.util.zip.DataFormatException;

import org.json.JSONObject;
import org.openml.apiconnector.algorithms.Caching;
import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.settings.Settings;
import org.openml.apiconnector.xml.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

public class OpenmlConnector implements Serializable {
	private static final long serialVersionUID = 7362620508675762264L;

	/**
	 * When set to true, API will output information.
	 */
	private int verboseLevel = 0;
	
	private final String session_hash;
	
	private final String API_URL;
	
	public OpenmlConnector(String url, String session_hash) {
		this.API_URL = url;
		this.session_hash = session_hash;
	}

	/**
	 * Creates a default OpenML Connector with authentication
	 */
	public OpenmlConnector(String session_hash) {
		this.API_URL = Settings.BASE_URL;
		this.session_hash = session_hash;
	}

	public String getSessionHash() throws Exception {
		return session_hash;
	}

	public void setVerboseLevel(int level) {
		verboseLevel = level;
	}
	
	public int getVerboselevel() {
		return verboseLevel;
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
	public DataSetDescription data_get(int did) throws Exception {
		if (Settings.LOCAL_OPERATIONS) {
			String dsdString = Conversion.fileToString(Caching.cached("datadescription", did));
			return (DataSetDescription) HttpConnector.xstreamClient.fromXML(dsdString);
		}

		Object apiResult = HttpConnector.doApiRequest(API_URL + "data/" + did, session_hash, verboseLevel);
		if (apiResult instanceof DataSetDescription) {
			if (Settings.CACHE_ALLOWED) {
				Caching.cache(apiResult, "datadescription", did);
			}
			return (DataSetDescription) apiResult;
		} else {
			throw new DataFormatException("Casting Api Object to DataSetDescription");
		}
	}
	
	public UploadDataSet data_upload(File description, File dataset) throws Exception {
		MultipartEntity params = new MultipartEntity();
		params.addPart("description", new FileBody(description));
		if (dataset != null) {
			params.addPart("dataset", new FileBody(dataset));
		}
		
		Object apiResult = HttpConnector.doApiRequest(API_URL + "data/", params, session_hash, verboseLevel);
		if (apiResult instanceof UploadDataSet) {
			return (UploadDataSet) apiResult;
		} else {
			throw new DataFormatException("Casting Api Object to UploadDataSet");
		}
	}
	
	public DataDelete data_delete(int did) throws Exception {
		Object apiResult = HttpConnector.doApiDelete(API_URL + "data/" + did, session_hash, verboseLevel);
		if (apiResult instanceof DataDelete) {
			return (DataDelete) apiResult;
		} else {
			throw new DataFormatException("Casting Api Object to DataDelete");
		}
	}
	
	public DataTag dataTag(int id, String tag) throws Exception {
		MultipartEntity params = new MultipartEntity();
		params.addPart("data_id", new StringBody("" + id));
		params.addPart("tag", new StringBody(tag));
		
		Object apiResult = HttpConnector.doApiRequest(API_URL + "data/tag", params, session_hash, verboseLevel);
		if (apiResult instanceof DataTag) {
			return (DataTag) apiResult;
		} else {
			throw new DataFormatException("Casting Api Object to DataTag");
		}
	}

	/**
	 * @param sql
	 *            - The query to be executed
	 * @return An JSON object containing the result of the query, along with
	 *         meta data
	 * @throws Exception
	 */
	public JSONObject freeQuery(String sql) throws Exception {
		return new JSONObject(getStringFromUrl(API_URL + "api_query/?q=" + URLEncoder.encode(sql, "ISO-8859-1")
				+ "&hash=" + getSessionHash()));
	}

	/**
	 * @param url
	 *            - The URL to obtain
	 * @return String - The content of the URL
	 * @throws IOException
	 *             - Can be: server down, etc.
	 */
	public static String getStringFromUrl(String url) throws IOException {
		String result = IOUtils.toString(new URL(url));
		return result;
	}

	/**
	 * @param url
	 *            - The URL to obtain
	 * @param filepath
	 *            - Where to safe the file.
	 * @return File - a pointer to the file that was saved.
	 * @throws IOException
	 *             - Can be: server down, etc.
	 */
	public static File getFileFromUrl(String url, String filepath) throws IOException {
		File file = new File(filepath);
		FileUtils.copyURLToFile(new URL(url), file);
		return file;
	}
}
