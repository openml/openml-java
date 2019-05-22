package org.openml.apiconnector.xml;

public enum EstimationProcedureType {
	// corresponding with database fields. 
	CROSSVALIDATION("crossvalidation"), LEAVEONEOUT("leaveoneout"), 
	HOLDOUT("holdout"), HOLDOUT_ORDERED("holdout_ordered"), 
	BOOTSTRAPPING("bootstrapping"), SUBSAMPLING("subsampling"), 
	TESTTHENTRAIN("testthentrain"), HOLDOUTUNLABELED("holdoutunlabeled"),
	CUSTOMHOLDOUT("customholdout"), TESTONTRAININGDATA("testontrainingdata");
	
	private String text;

	EstimationProcedureType(String text) {
		this.text = text;
	}

	/**
	 * @return The name of this parameter type;
	 */
	public String getName() {
		return this.text;
	}
}
