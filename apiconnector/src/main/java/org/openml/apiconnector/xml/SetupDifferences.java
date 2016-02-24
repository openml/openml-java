package org.openml.apiconnector.xml;

import java.io.Serializable;

import org.openml.apiconnector.settings.Constants;

public class SetupDifferences {
	private final String oml = Constants.OPENML_XMLNS;
	
	private Task[] tasks;
	
	public String getOml() {
		return oml;
	}
	public Task[] getTasks() {
		return tasks;
	}
	
	public class Task implements Serializable {
		private static final long serialVersionUID = -2631059019814267412L;
		private Integer setupA;
		private Integer setupB;
		private Integer task_id;
		private Integer task_size;
		private Integer differences;

		public Integer getSetupA() {
			return setupA;
		}

		public Integer getSetupB() {
			return setupB;
		}

		public Integer getTask_id() {
			return task_id;
		}

		public Integer getTask_size() {
			return task_size;
		}
		
		public Integer getDifferences() {
			return differences;
		}

		public String toString() {
			return setupA + "," + setupB + " on Task " + task_id + ": " + differences + "/" + task_size;
		}
	}
}
