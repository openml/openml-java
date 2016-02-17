package org.openml.moa;

import java.util.Arrays;

import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.settings.Config;
import org.openml.apiconnector.settings.Settings;
import org.openml.apiconnector.xml.Job;
import org.openml.moa.settings.MoaSettings;

import weka.core.Utils;
import moa.DoTask;

public class RunJob {
	
	private static OpenmlConnector apiconnector;
	
	public static void main( String[] args ) throws Exception {
		int n;
		int ttid;
		
		String strN = Utils.getOption('N', args);
		String strTtid = Utils.getOption('T', args);
		String strConfig = Utils.getOption('C', args);
		
		Config c;
		if (strConfig != null && strConfig.equals("") == false) {
			c = new Config(strConfig);
		} else {
			c = new Config();
		}
		
		if( c.getServer() != null ) {
			apiconnector = new OpenmlConnector( c.getServer(), c.getApiKey() );
		} else { 
			apiconnector = new OpenmlConnector( c.getApiKey() );
		}
		
		if( c.get("cache_allowed") != null) {
			if (c.get("cache_allowed").equals("false") ) {
				Settings.CACHE_ALLOWED = false;
			}
		}
		
		n = ( strN.equals("") ) ? 1 : Integer.parseInt(strN);
		ttid = ( strTtid.equals("") ) ? 4 : Integer.parseInt(strTtid);
		
		for( int i = 0; i < n; ++i ) {
			doTask(ttid, c);
		}
	}
	
	public static void doTask(int ttid, Config config) {
		try {
			
			String moaVersion = MoaSettings.MOA_VERSION;
			
			Conversion.log( "OK", "Request Job", "Moa Version: " + moaVersion + "; ttid: " + ttid);
			Job j = apiconnector.jobRequest( moaVersion, "" + ttid );
			Conversion.log( "OK","Start Job","Task: " + j.getTask_id() + "; learner: " + j.getLearner() );
			
			String[] taskArgs = new String[7];
			taskArgs[0] = "openml.OpenmlDataStreamClassification";
			taskArgs[1] = "-l";
			taskArgs[2] = "(" + j.getLearner() + ")";
			taskArgs[3] = "-t";
			taskArgs[4] = ""+j.getTask_id();
			taskArgs[5] = "-c";
			taskArgs[6] = config.toString().replace('\n', ';');
			
			Conversion.log("OK", "CMD", Arrays.toString(taskArgs));
			
			DoTask.main( taskArgs );
		} catch( Exception e ) {
			e.printStackTrace();
		}
	}
}
