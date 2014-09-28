package org.openml.apiconnector.algorithms;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.openml.apiconnector.io.OpenmlConnector;

public class QueryUtils {

	public static int[] getIdsFromDatabase( OpenmlConnector apiconnector, String sql ) throws JSONException, IOException {
		JSONArray runJson = (JSONArray) apiconnector.openmlFreeQuery( sql ).get("data");
		
		int[] result = new int[runJson.length()];
		for( int i = 0; i < runJson.length(); ++i ) {
			result[i] = (int) ( (JSONArray) runJson.get( i )).getDouble( 0 );
		}
		
		return result; 
	}
	
	public static double getIntFromDatabase( OpenmlConnector apiconnector, String sql ) throws JSONException, IOException {
		int[] result = getIdsFromDatabase( apiconnector, sql );
		return result[0];
	}
}
