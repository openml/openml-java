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

	public static void main(String[] args) throws Exception {
		int n;

		String strN = Utils.getOption('N', args);
		String strTaskId = Utils.getOption('t', args);
		String strLearner = Utils.getOption('l', args);
		String strConfig = Utils.getOption('C', args);

		Config c;
		if (strConfig != null && strConfig.equals("") == false) {
			c = new Config(strConfig);
		} else {
			c = new Config();
		}

		if (c.getServer() != null) {
			apiconnector = new OpenmlConnector(c.getServer(), c.getApiKey());
		} else {
			apiconnector = new OpenmlConnector(c.getApiKey());
		}

		if (c.get("cache_allowed") != null) {
			if (c.get("cache_allowed").equals("false")) {
				Settings.CACHE_ALLOWED = false;
			}
		}
		
		if (strLearner.equals("") || strTaskId.equals("")) {
			// default mode, do task request
			n = (strN.equals("")) ? 1 : Integer.parseInt(strN);
			for (int i = 0; i < n; ++i) {
				String moaVersion = MoaSettings.MOA_VERSION;
				
				Conversion.log("OK", "Request Job", "Moa Version: " + moaVersion + "; ttid: 4");
				Job j = apiconnector.jobRequest(moaVersion, "4");
				Conversion.log("OK", "Start Job", "Task: " + j.getTask_id() + "; learner: " + j.getLearner());
				
				doTask(j.getTask_id(), j.getLearner(), c);
			}
		} else {
			Integer taskId = Integer.parseInt(strTaskId);
			
			doTask(taskId, strLearner, c);
		}
	}

	public static void doTask(int task_id, String learnerStr, Config config) {
		try {
			String[] taskArgs = new String[7];
			taskArgs[0] = "openml.OpenmlDataStreamClassification";
			taskArgs[1] = "-l";
			taskArgs[2] = "(" + learnerStr + ")";
			taskArgs[3] = "-t";
			taskArgs[4] = "" + task_id;
			taskArgs[5] = "-c";
			taskArgs[6] = config.toString().replace('\n', ';');

			Conversion.log("OK", "CMD", Arrays.toString(taskArgs));

			DoTask.main(taskArgs);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
