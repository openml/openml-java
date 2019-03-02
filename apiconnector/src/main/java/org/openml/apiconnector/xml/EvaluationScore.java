/*******************************************************************************
 * Copyright (C) 2017, Jan N. van Rijn <j.n.van.rijn@liacs.leidenuniv.nl>
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the copyright holder nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package org.openml.apiconnector.xml;

import org.openml.apiconnector.settings.Constants;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

public class EvaluationScore {
	
	@XStreamAsAttribute
	@XStreamAlias("xmlns:oml")
	private final String oml = Constants.OPENML_XMLNS;

	@XStreamAlias("oml:name")
	private final String function;

	@XStreamAlias("oml:value")
	private final Double value;
	
	@XStreamAlias("oml:stdev")
	private final Double stdev;
	
	@XStreamAlias("oml:array_data")
	private final String array_data;
	
	@XStreamAlias("oml:sample_size")
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
	
	public EvaluationScore(String function, Double value, Double stdev, String array_data) {
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

	public EvaluationScore(String function, Double value, String array_data, Integer repeat, Integer fold) {
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

	public EvaluationScore(String function, Double value, 
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

	public Double getValue() {
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
		return Math.abs(value - other.getValue()) < Constants.EPSILON;
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
