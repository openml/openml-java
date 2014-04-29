package org.openml.apiconnector.xml;

import org.apache.commons.lang3.ArrayUtils;
import org.openml.apiconnector.settings.Constants;

public class RunEvaluation {
	private final String oml = Constants.OPENML_XMLNS;
	
	private Integer run_id;
	private String error;
	private EvaluationScore[] evaluation;
	
	public RunEvaluation( int run_id ) {
		this.run_id = run_id;
		this.error = null;
	}
	
	public void addEvaluationMeasures( EvaluationScore[] em ) {
		evaluation = ArrayUtils.addAll( this.evaluation, em );
	}
	
	public void addEvaluationMeasure( EvaluationScore em ) {
		evaluation = ArrayUtils.addAll( this.evaluation, em );
	}
	
	public void setError( String error ) {
		this.error = error;
	}
	
	public String getOml() {
		return oml;
	}

	public Integer getRun_id() {
		return run_id;
	}

	public String getError() {
		return error;
	}

	public EvaluationScore[] getEvaluation_scores() {
		return evaluation;
	}
}
