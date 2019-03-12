package org.openml.apiconnector.xml;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("oml:estimationprocedures")
public class EstimationProcedures extends OpenmlApiResponse {
	
	private static final long serialVersionUID = 8952083823925140697L;

	@XStreamImplicit
	@XStreamAlias("oml:estimationprocedure")
	EstimationProcedure[] estimationProcedure;

	public EstimationProcedure[] getEstimationProcedure() {
		return estimationProcedure;
	}
}
