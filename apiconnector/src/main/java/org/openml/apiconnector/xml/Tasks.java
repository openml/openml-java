package org.openml.apiconnector.xml;

import org.openml.apiconnector.settings.Constants;

public class Tasks {

	private final String oml = Constants.OPENML_XMLNS;
	
	public Task[] task;
	
	public String getOml() {
		return oml;
	}

	public Task[] getTask() {
		return task;
	}

	public static class Task {
		private int task_id;
		private String task_type;
		private int did;
		private String name;
		private String status;
		private Quality[] qualities;
		
		public int getTask_id() {
			return task_id;
		}
		public String getTask_type() {
			return task_type;
		}
		public int getDid() {
			return did;
		}
		public String getName() {
			return name;
		}
		public String getStatus() {
			return status;
		}
		public Quality[] getQualities() {
			return qualities;
		}
		
		public static class Quality {
			private String name;
			private String value;
			
			public String getName() {
				return name;
			}
			public String getValue() {
				return value;
			}
		}
	}
}
