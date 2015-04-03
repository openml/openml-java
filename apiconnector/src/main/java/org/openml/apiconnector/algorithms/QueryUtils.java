package org.openml.apiconnector.algorithms;

import org.json.JSONArray;
import org.openml.apiconnector.io.OpenmlConnector;

public class QueryUtils {

    public static int[] getIdsFromDatabase( OpenmlConnector apiconnector, String sql ) throws Exception {
        JSONArray runJson = (JSONArray) apiconnector.freeQuery(sql).get("data");

        int[] result = new int[runJson.length()];
        for( int i = 0; i < runJson.length(); ++i ) {
            result[i] = (int) ( (JSONArray) runJson.get( i )).getDouble( 0 );
        }

        return result;
    }
    
    public static double[] getNumbersFromDatabase( OpenmlConnector apiconnector, String sql ) throws Exception {
        JSONArray runJson = (JSONArray) apiconnector.freeQuery(sql).get("data");

        double[] result = new double[runJson.length()];
        for( int i = 0; i < runJson.length(); ++i ) {
            result[i] = ( (JSONArray) runJson.get( i )).getDouble( 0 );
        }

        return result;
    }

    public static double getIntFromDatabase( OpenmlConnector apiconnector, String sql ) throws Exception {
        int[] result = getIdsFromDatabase( apiconnector, sql );
        return result[0];
    }
}
