package org.openml.apiconnector.xml;

import java.io.Serializable;

import org.openml.apiconnector.settings.Constants;

public class RunList {

	private final String oml = Constants.OPENML_XMLNS;
	
	private Run[] runs;

	public Run[] getRuns() {
		return runs;
	}

	public String getOml() {
		return oml;
	}

	public class Run implements Serializable {
		private static final long serialVersionUID = 87L;
		
		private int run_id;
		private int task_id;
		private int setup_id;
		private int uploader;
		private String error_message;
		
		public int getRun_id() {
			return run_id;
		}
		public int getTask_id() {
			return task_id;
		}
		public int getSetup_id() {
			return setup_id;
		}
		public int getUploader() {
			return uploader;
		}
		public String getError_message() {
			return error_message;
		}
	}
}
