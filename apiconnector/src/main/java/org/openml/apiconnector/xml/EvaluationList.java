package org.openml.apiconnector.xml;

import java.io.Serializable;

import org.openml.apiconnector.settings.Constants;

public class EvaluationList {

	private final String oml = Constants.OPENML_XMLNS;
	
	private Evaluation[] evaluation;

	public Evaluation[] getEvaluations() {
		return evaluation;
	}

	public String getOml() {
		return oml;
	}

	public class Evaluation implements Serializable {
		private static final long serialVersionUID = 87L;
		
		private int run_id;
		private int task_id;
		private int setup_id;
		private int flow_id;
		private String function;
		private Double value;
		
		public int getRun_id() {
			return run_id;
		}
		public int getTask_id() {
			return task_id;
		}
		public int getFlow_id() {
			return flow_id;
		}
		public int getSetup_id() {
			return setup_id;
		}
		public String getFunction() {
			return function;
		}
		public double getValue() {
			return value;
		}
		
		public String toString() {
			return "[run: " + run_id + ", setup: " + setup_id + ", task: " + task_id + ", " + function + ": " + value + "]";
		}
	}
}
