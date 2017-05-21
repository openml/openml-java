package org.openml.apiconnector.xml;

import java.io.Serializable;

import org.openml.apiconnector.settings.Constants;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("oml:runs")
public class RunList {

	@XStreamAsAttribute
	@XStreamAlias("xmlns:oml")
	private final String oml = Constants.OPENML_XMLNS;

	@XStreamImplicit
	@XStreamAlias("oml:run")
	private Run[] runs;

	public Run[] getRuns() {
		return runs;
	}

	public String getOml() {
		return oml;
	}
	
	public class Run implements Serializable {
		private static final long serialVersionUID = 87L;

		@XStreamAlias("oml:run_id")
		private int run_id;

		@XStreamAlias("oml:task_id")
		private int task_id;

		@XStreamAlias("oml:setup_id")
		private int setup_id;

		@XStreamAlias("oml:uploader")
		private int uploader;
		
		@XStreamAlias("oml:upload_time")
		private String upload_time;
		
		@XStreamAlias("oml:error_message")
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
