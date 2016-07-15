package org.openml.apiconnector.io;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.openml.apiconnector.settings.Constants;
import org.openml.apiconnector.xml.ApiError;
import org.openml.apiconnector.xstream.XstreamXmlMapping;

import com.thoughtworks.xstream.XStream;

public class HttpConnector implements Serializable {
	
	public static XStream xstreamClient = XstreamXmlMapping.getInstance();
	
	private static final long serialVersionUID = -8589069573065947493L;
	
	public static Object doApiRequest( String url, MultipartEntity entity, String ash, int apiVerboseLevel ) throws Exception {
		entity.addPart("api_key", new StringBody( ash ) );
		
		String result = "";
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost( url );
        httppost.setEntity(entity);
        
        CloseableHttpResponse response = httpclient.execute(httppost);
        HttpEntity resEntity = response.getEntity();
		
		long contentLength = 0;
		try {
            if (resEntity != null) {
            	result = httpEntitiToString(resEntity);
                contentLength = resEntity.getContentLength();
            } else {
            	throw new Exception("An exception has occured while reading data input stream. ");
            }
		} finally {
            try { response.close(); } catch (Exception ignore) {}
        }
		if(apiVerboseLevel >= Constants.VERBOSE_LEVEL_XML) {
			System.out.println("===== REQUEST URI (POST): " + url + " (Content Length: "+contentLength+") =====\n" + result + "\n=====\n");
		}
		
		Object apiResult = xstreamClient.fromXML(result);
		if(apiResult instanceof ApiError) {
			ApiError apiError = (ApiError) apiResult;
			String message = apiError.getMessage();
			if( apiError.getAdditional_information() != null ) {
				message += ": " + apiError.getAdditional_information();
			}
			throw new ApiException( Integer.parseInt( apiError.getCode() ), message );
		}
		return apiResult;
	}
	
	public static Object doApiRequest(String url, String ash, int apiVerboseLevel) throws Exception {
		return doApiRequest(url, new MultipartEntity(), ash, apiVerboseLevel);
	}
	
	public static Object doApiDelete(String url, String ash, int apiVerboseLevel) throws Exception {
		String result = "";
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpDelete httpdelete = new HttpDelete(url + "?api_key=" + ash );
        
        CloseableHttpResponse response = httpclient.execute(httpdelete);
		long contentLength = 0;
		try {
            HttpEntity resEntity = response.getEntity();
            if (resEntity != null) {
            	result = httpEntitiToString(resEntity);
                contentLength = resEntity.getContentLength();
            } else {
            	throw new Exception("An exception has occured while reading data input stream. ");
            }
		} finally {
            try { response.close(); } catch (Exception ignore) {}
        }
		
		if(apiVerboseLevel >= Constants.VERBOSE_LEVEL_XML) {
			System.out.println("===== REQUEST URI (DELETE): " + url + " (Content Length: "+contentLength+") =====\n" + result + "\n=====\n");
		}
		
		
		Object apiResult = xstreamClient.fromXML(result);
		if(apiResult instanceof ApiError) {
			ApiError apiError = (ApiError) apiResult;
			String message = apiError.getMessage();
			if( apiError.getAdditional_information() != null ) {
				message += ": " + apiError.getAdditional_information();
			}
			throw new ApiException( Integer.parseInt( apiError.getCode() ), message );
		}
		return apiResult;
	}
	
	private static String httpEntitiToString(HttpEntity resEntity) throws IOException {
		StringWriter writer = new StringWriter();
		IOUtils.copy(new InputStreamReader( resEntity.getContent() ), writer );
		return writer.toString();
	}
}
