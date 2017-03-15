package org.openml.apiconnector.xml;

import org.apache.commons.lang3.ArrayUtils;
import org.openml.apiconnector.settings.Constants;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("oml:run_evaluation")
public class RunEvaluation {
	
	@XStreamAsAttribute
	@XStreamAlias("xmlns:oml")
	private final String oml = Constants.OPENML_XMLNS;

	@XStreamAlias("oml:run_id")
	private Integer run_id;

	@XStreamAlias("oml:evaluation_engine_id")
	private Integer evaluation_engine_id;
	
	@XStreamAlias("oml:error")
	private String error;

	@XStreamAlias("oml:warning")
	private String warning;
	
	@XStreamImplicit(itemFieldName="oml:evaluation")
	private EvaluationScore[] evaluation;
	
	public RunEvaluation(int run_id, int evaluation_engine_id) {
		this.run_id = run_id;
		this.evaluation_engine_id = evaluation_engine_id;
		this.error = null;
		this.warning = null;
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
	
	public void setWarning( String warning ) {
		this.warning = warning;
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

	public String getWarning() {
		return warning;
	}

	public EvaluationScore[] getEvaluation_scores() {
		return evaluation;
	}
}
