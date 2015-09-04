package org.openml.apiconnector.xml;

import org.openml.apiconnector.settings.Constants;

public class TaskEvaluations {

	private final String oml = Constants.OPENML_XMLNS;
	
	private int task_id;
	private String task_name;
	private int task_type_id;
	private int input_data;
	private String estimation_procedure;
	private Evaluation[] evaluation;
	
	public String getOml() {
		return oml;
	}

	public int getTask_id() {
		return task_id;
	}

	public String getTask_name() {
		return task_name;
	}

	public int getTask_type_id() {
		return task_type_id;
	}

	public int getInput_data() {
		return input_data;
	}

	public String getEstimation_procedure() {
		return estimation_procedure;
	}

	public Evaluation[] getEvaluation() {
		return evaluation;
	}

	public class Evaluation {
		private int run_id;
		private int setup_id;
		private int flow_id;
		private Integer interval_start;
		private Integer interval_end;
		private String flow;
		private Measure[] measure;
		
		public int getRun_id() {
			return run_id;
		}
		
		public int getSetup_id() {
			return setup_id;
		}

		public int getFlow_id() {
			return flow_id;
		}

		public String getFlow() {
			return flow;
		}

		public Measure[] getMeasures() {
			return measure;
		}
		
		public Integer getInterval_start() {
			return interval_start;
		}

		public Integer getInterval_end() {
			return interval_end;
		}

		public String getMeasure( String name ) throws Exception {
			for( Measure m : measure ) {
				if( m.getName().equals( name ) )
					return m.getValue();
			}
			throw new Exception("Could not field specified measure for flow: " + flow_id + "(" + flow + ") - " + name);
		}

		public class Measure {
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
