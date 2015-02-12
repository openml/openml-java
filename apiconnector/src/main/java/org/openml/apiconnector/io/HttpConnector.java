package org.openml.apiconnector.io;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.openml.apiconnector.settings.Constants;
import org.openml.apiconnector.xml.ApiError;
import org.openml.apiconnector.xstream.XstreamXmlMapping;

import com.thoughtworks.xstream.XStream;

public class HttpConnector implements Serializable {

	public static final String API_PART = "rest_api/";
	
	private static final long serialVersionUID = -8589069573065947493L;
	
	public static Object doApiRequest( String url, String function, String queryString, MultipartEntity entity, ApiSessionHash ash, int apiVerboseLevel ) throws Exception {
		XStream xstream = XstreamXmlMapping.getInstance();
		
		if( ash != null ) {
			if( entity == null ) {
				entity = new MultipartEntity();
			}
			entity.addPart("session_hash", new StringBody( ash.getSessionHash() ) );
		}
		
		String result = "";
		HttpClient httpclient = new DefaultHttpClient();
		String requestUri = url + API_PART + "?f=" + function;
		if( queryString != null ) {
			requestUri += queryString;
		}
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
		if(apiVerboseLevel >= Constants.VERBOSE_LEVEL_XML) {
			System.out.println("===== REQUEST URI: " + requestUri + " (Content Length: "+contentLength+") =====\n" + result + "\n=====\n");
		}
		
		Object apiResult = xstream.fromXML(result);
		if(apiResult instanceof ApiError) {
			ApiError apiError = (ApiError) apiResult;
			throw new ApiException( Integer.parseInt( apiError.getCode() ), apiError.getMessage() );
		}
		return apiResult;
	}
	
	public static Object doApiRequest(String url, String function, String queryString, ApiSessionHash ash, int apiVerboseLevel) throws Exception {
		return doApiRequest(url, function, queryString, null, ash, apiVerboseLevel);
	}
	
	private static String httpEntitiToString(HttpEntity resEntity) throws IOException {
		StringWriter writer = new StringWriter();
		IOUtils.copy(new InputStreamReader( resEntity.getContent() ), writer );
		return writer.toString();
	}
}
