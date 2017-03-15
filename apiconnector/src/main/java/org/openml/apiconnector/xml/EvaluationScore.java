package org.openml.apiconnector.xml;

import org.openml.apiconnector.algorithms.MathHelper;
import org.openml.apiconnector.settings.Constants;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

public class EvaluationScore {
	
	@XStreamAsAttribute
	@XStreamAlias("xmlns:oml")
	private final String oml = Constants.OPENML_XMLNS;

	@XStreamAlias("oml:function")
	private final String function;

	@XStreamAlias("oml:value")
	private final String value;
	
	@XStreamAlias("oml:stdev")
	private final Double stdev;
	
	@XStreamAlias("oml:array_data")
	private final String array_data;
	
	@XStreamAlias("sample_size")
	private Integer sample_size; /*not final*/

	@XStreamAsAttribute
	@XStreamAlias("repeat")
	private final Integer repeat;
	@XStreamAsAttribute
	@XStreamAlias("fold")
	private final Integer fold;
	@XStreamAsAttribute
	@XStreamAlias("sample")
	private final Integer sample;
	
	public EvaluationScore(String function, String value, Double stdev, String array_data) {
		super();
		this.function = function;
		this.value = value;
		this.stdev = stdev;
		this.array_data = array_data;
		
		// unused
		this.repeat = null;
		this.fold = null;
		this.sample = null;
		this.sample_size = null;
	}

	public EvaluationScore(String function, String value, String array_data, Integer repeat, Integer fold) {
		super();
		this.function = function;
		this.value = value;
		this.array_data = array_data;
		this.fold = fold;
		this.repeat = repeat;
		
		// unused
		this.sample = null;
		this.sample_size = null;
		this.stdev = null;
	}

	public EvaluationScore(String function, String value, 
			String array_data, Integer repeat, Integer fold,
			Integer sample, Integer sample_size) {
		super();
		this.function = function;
		this.value = value;
		this.array_data = array_data;
		this.fold = fold;
		this.repeat = repeat;
		this.sample = sample;
		this.sample_size = sample_size;
		
		// unused
		this.stdev = null;
	}
	
	public String getFunction() {
		return function;
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
	
	public void setSample_size( int sample_size ) {
		this.sample_size = sample_size;
	}

	public Integer getSample_size() {
		return sample_size;
	}
	
	public boolean isSame( EvaluationScore other ) {
		return equalStrings( function, other.getFunction() ) && 
			equalIntegers( fold, other.getFold() ) && 
			equalIntegers( repeat, other.getRepeat() ) && 
			equalIntegers( sample, other.getSample() ); // do not compare on sample size, as this is just additional information
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
		if( sb.length() == 0 ) sb.append( ", GLOBAL" );
		
		return function + " - [" + sb.toString().substring( 2 ) + "]";
	}
	
	private static boolean equalStrings( String s1, String s2 ) {
		if( s1 != null && s2 != null ) {
			return s1.equals( s2 );
		} else {
			return s1 == null && s2 == null;
		}
	}
	
	private static boolean equalIntegers( Integer s1, Integer s2 ) {
		if( s1 != null && s2 != null ) {
			return s1.equals( s2 );
		} else {
			return s1 == null && s2 == null;
		}
	}
}