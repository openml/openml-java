package org.openml.apiconnector.xml;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("oml:estimationprocedure")
public class EstimationProcedure extends OpenmlApiResponse {
	
	private static final long serialVersionUID = 2776327287554288556L;

	@XStreamAlias("oml:id")
	private int id;

	@XStreamAlias("oml:ttid")
	private int ttid;

	@XStreamAlias("oml:name")
	private String name;

	@XStreamAlias("oml:type")
	private EstimationProcedureType type;

	@XStreamAlias("oml:repeats")
	private Integer repeats;

	@XStreamAlias("oml:folds")
	private Integer folds;

	@XStreamAlias("oml:percentage")
	private Integer percentage;

	@XStreamAlias("oml:stratified_sampling")
	private String stratifiedSampling;

	public int getId() {
		return id;
	}

	public int getTtid() {
		return ttid;
	}

	public String getName() {
		return name;
	}

	public EstimationProcedureType getType() {
		return type;
	}

	public Integer getRepeats() {
		return repeats;
	}

	public Integer getFolds() {
		return folds;
	}

	public Integer getPercentage() {
		return percentage;
	}

	public String getStratifiedSampling() {
		return stratifiedSampling;
	}
}
