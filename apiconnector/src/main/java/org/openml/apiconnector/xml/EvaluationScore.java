package org.openml.apiconnector.xml;

import org.openml.apiconnector.algorithms.MathHelper;

public class EvaluationScore {

	private String function;
	private String implementation;
	private String label;
	
	private String value;
	private Double stdev;
	private String array_data;
	
	private Integer repeat;
	private Integer fold;
	private Integer sample;
	private Integer sample_size;
	
	private Integer interval_start;
	private Integer interval_end;
	
	public EvaluationScore(String implementation, String function, String label,
			String value, Double stdev, String array_data) {
		super();
		this.implementation = implementation;
		this.function = function;
		this.label = label;
		this.value = value;
		this.stdev = stdev;
		this.array_data = array_data;
	}

	public EvaluationScore(String implementation, String function, String label,
			String value, String array_data, Integer repeat, Integer fold) {
		super();
		this.implementation = implementation;
		this.function = function;
		this.label = label;
		this.value = value;
		this.array_data = array_data;
		this.fold = fold;
		this.repeat = repeat;
	}

	public EvaluationScore(String implementation, String function, String label,
			String value, String array_data, Integer repeat, Integer fold,
			Integer sample, Integer sample_size) {
		super();
		this.implementation = implementation;
		this.function = function;
		this.label = label;
		this.value = value;
		this.array_data = array_data;
		this.fold = fold;
		this.repeat = repeat;
		this.sample = sample;
		this.sample_size = sample_size;
	}

	public EvaluationScore(String implementation, String function, String label,
			String value, String array_data, Integer interval_start,
			Integer interval_end, boolean dummy ) {
		super();
		this.implementation = implementation;
		this.function = function;
		this.label = label;
		this.value = value;
		this.array_data = array_data;
		this.interval_start = interval_start;
		this.interval_end = interval_end;
	}

	public String getImplementation() {
		return implementation;
	}

	public String getFunction() {
		return function;
	}

	public String getLabel() {
		return label;
	}

	public String getValue() {
		return value;
	}

	public double getStdev() {
		return stdev;
	}

	public String getArray_data() {
		return array_data;
	}

	public Integer getFold() {
		return fold;
	}

	public Integer getRepeat() {
		return repeat;
	}

	public Integer getSample() {
		return sample;
	}

	public Integer getSample_size() {
		return sample_size;
	}

	public Integer getInterval_start() {
		return interval_start;
	}

	public Integer getInterval_end() {
		return interval_end;
	}
	
	public boolean isSame( EvaluationScore other ) {
		return equalStrings( implementation, other.getImplementation() ) && 
			equalStrings( function, other.getFunction() ) && 
			equalStrings( label, other.getLabel() ) &&
			equalIntegers( fold, other.getFold() ) && 
			equalIntegers( repeat, other.getRepeat() ) && 
			equalIntegers( sample, other.getSample() ) && 
			equalIntegers( interval_start, other.getInterval_start() ) &&
			equalIntegers( interval_end, other.getInterval_end() ); // do not compare on sample size, as this is just additional information
	}
	
	public boolean sameValue( EvaluationScore other ) {
		try {
			double myValue = Double.parseDouble( value );
			double otherValue = Double.parseDouble( other.getValue() );
			
			return Math.abs( myValue - otherValue ) < MathHelper.EPSILON;
		} catch( NumberFormatException e ) {
			return false;
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if( repeat != null ) sb.append( ", repeat " + repeat );
		if( fold != null ) sb.append( ", fold " + fold );
		if( sample != null ) sb.append( ", sample " + sample );
		if( interval_start != null ) sb.append( ", interval_start " + interval_start );
		if( sb.length() == 0 ) sb.append( ", GLOBAL" );
		
		return function + " (" + implementation + ") - [" + sb.toString().substring( 2 ) + "]";
	}
	
	private static boolean equalStrings( String s1, String s2 ) {
		if( s1 != null && s2 != null ) {
			return s1.equals( s2 );
		} else {
			return s1 == null && s2 == null;
		}
	}
	/*
	private static boolean equalDoubles( Double s1, Double s2 ) {
		if( s1 != null && s2 != null ) {
			return s1.equals( s2 );
		} else {
			return s1 == null && s2 == null;
		}
	}*/
	
	private static boolean equalIntegers( Integer s1, Integer s2 ) {
		if( s1 != null && s2 != null ) {
			return s1.equals( s2 );
		} else {
			return s1 == null && s2 == null;
		}
	}
}