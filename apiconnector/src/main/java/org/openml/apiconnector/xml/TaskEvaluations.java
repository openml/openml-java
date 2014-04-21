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
		private int implementation_id;
		private String implementation;
		private Measure[] measure;
		
		public int getRun_id() {
			return run_id;
		}
		
		public int getSetup_id() {
			return setup_id;
		}

		public int getImplementation_id() {
			return implementation_id;
		}

		public String getImplementation() {
			return implementation;
		}

		public Measure[] getMeasures() {
			return measure;
		}
		
		public String getMeasure( String name ) throws Exception {
			for( Measure m : measure ) {
				if( m.getName().equals( name ) )
					return m.getValue();
			}
			throw new Exception("Could not field specified measure: " + name );
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
