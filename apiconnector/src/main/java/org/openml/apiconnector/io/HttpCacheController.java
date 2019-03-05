package org.openml.apiconnector.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.FilenameUtils;
import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.settings.Config;
import org.openml.apiconnector.settings.Settings;
import org.openml.apiconnector.xstream.XstreamXmlMapping;

import com.thoughtworks.xstream.XStream;

public class HttpCacheController extends HttpConnector {
	
	private static final long serialVersionUID = 1257139667469866906L;
	private static XStream xstream = XstreamXmlMapping.getInstance();
	
	/**
	 * Performs a HTTP get call, and caches it locally, if allowed
	 * 
	 * @param url - The URL to do the request on
	 * @param ash - api key to authenticate
	 * @param apiVerboseLevel - for verbosity
	 * @return Object - string response wrapped as object
	 * @throws Exception
	 *             - Can be: server down, problem with URL, etc
	 */
	public static Object doApiGetRequest(URL url, String cacheSuffix, String ash, int apiVerboseLevel) throws Exception {
		File cachedData = getCacheLocation(url, cacheSuffix);
		if (cachedData.exists() && Settings.CACHE_ALLOWED) {
			Conversion.log("OK", "Cache", "Obtained from cache: " + cacheSuffix);
			return xstream.fromXML(cachedData);
		} else {
			// do request
			Object apiResult = HttpConnector.doApiGetRequest(url, ash, apiVerboseLevel);
			
			if (Settings.CACHE_ALLOWED) {
				// make directories
				cachedData.getParentFile().mkdirs();
				
				// save to file
				BufferedWriter bw = new BufferedWriter(new FileWriter(cachedData));
				bw.append(xstream.toXML(apiResult));
				bw.close();
				Conversion.log("OK", "Cache", "Stored to cache: " + cacheSuffix);
			}
			return apiResult;
		}
	}
	
	/**
	 * Returns a file from the openml server and if allowed, caches it locally (otherwise
	 * returns a temp file)
	 * 
	 * @param url - The URL to obtain
	 * @param cacheSuffix - path to save the file
	 * @return File - a pointer to the file that was saved.
	 * @throws Exception
	 *             - Can be: server down, problem with URL, etc
	 */
	public static File getCachedFileFromUrl(URL url, String cacheSuffix) throws Exception {
		File cachedData = getCacheLocation(url, cacheSuffix);
		if (cachedData.exists() && Settings.CACHE_ALLOWED) {
			Conversion.log("OK", "Cache", "Obtained from cache: " + cacheSuffix);
			return cachedData;
		} else {
			if (Settings.CACHE_ALLOWED) {
				// saves it to file
				return HttpConnector.getFileFromUrl(url, cachedData.getAbsolutePath(), null);
			} else {
				// returns a temp file
				return HttpConnector.getFileFromUrl(url, null, FilenameUtils.getExtension(cachedData.getName()));
			}
		}
	}
	
	/**
	 * Given a URL to retrieve and a cache suffix, returns the file where it will reside. 
	 * 
	 * @param url - The URL to obtain
	 * @param cacheSuffix - path to save the file
	 * @return File - a pointer to the file where it will be saved
	 * @throws MalformedURLException
	 *             - problem with URL
	 */
	public static File getCacheLocation(URL url, String cacheSuffix) throws MalformedURLException {
		return new File(Settings.CACHE_DIRECTORY + "/" + Config.getChachePrefixFromUrl(url) + cacheSuffix);
	}
}
