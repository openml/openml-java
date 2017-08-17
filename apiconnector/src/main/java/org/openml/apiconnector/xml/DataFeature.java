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

import java.util.HashMap;
import java.util.Map;

import org.openml.apiconnector.settings.Constants;

public class DataFeature {
	
	private final String oml = Constants.OPENML_XMLNS;
	private Integer did;
	private Integer evaluation_engine_id;
	private String error;
	private Feature[] features;

	public DataFeature( Integer did, Integer evaluation_engine_id, Feature[] features ) {
		this.did = did;
		this.evaluation_engine_id = evaluation_engine_id;
		this.features = features;
	}
	
	public DataFeature( Integer did, Integer evaluation_engine_id, String error ) {
		this.did = did;
		this.evaluation_engine_id = evaluation_engine_id;
		this.error = error;
	}
	
	public Integer getDid() {
		return did;
	}
	
	public Integer getEvaluation_engine_id() {
		return evaluation_engine_id;
	}
	
	public String getError() {
		return error;
	}

	public Feature[] getFeatures() {
		return features;
	}
	
	public Map<String, Feature> getFeatureMap() {
		Map<String,Feature> fm = new HashMap<String, Feature>();
		for (Feature f : features) {
			fm.put(f.name, f);
		}
		return fm;
	}
	
	public String getOml() {
		return oml;
	}
	
	public static class Feature {
		private Integer index;
		private String name;
		private String data_type;
		private Boolean is_target;
		private Integer NumberOfDistinctValues;
		private Integer NumberOfUniqueValues;
		private Integer NumberOfMissingValues;
		private Integer NumberOfIntegerValues;
		private Integer NumberOfRealValues;
		private Integer NumberOfNominalValues;
		private Integer NumberOfValues;
		private Double MaximumValue;
		private Double MinimumValue;
		private Double MeanValue;
		private Double StandardDeviation;
		private String ClassDistribution;

		
		public Feature(Integer index, String name, String data_type,
				Boolean is_target, Integer numberOfDistinctValues,
				Integer numberOfUniqueValues, Integer numberOfMissingValues,
				Integer numberOfIntegerValues, Integer numberOfRealValues,
				Integer numberOfNominalValues, Integer numberOfValues,
				Double maximumValue, Double minimumValue, Double meanValue,
				Double standardDeviation, String classDistribution) {
			super();
			this.index = index;
			this.name = name;
			this.data_type = data_type;
			this.is_target = is_target;
			NumberOfDistinctValues = numberOfDistinctValues;
			NumberOfUniqueValues = numberOfUniqueValues;
			NumberOfMissingValues = numberOfMissingValues;
			NumberOfIntegerValues = numberOfIntegerValues;
			NumberOfRealValues = numberOfRealValues;
			NumberOfNominalValues = numberOfNominalValues;
			NumberOfValues = numberOfValues;
			MaximumValue = maximumValue;
			MinimumValue = minimumValue;
			MeanValue = meanValue;
			StandardDeviation = standardDeviation;
			ClassDistribution = classDistribution;
		}
		
		public String getName() {
			return name;
		}
		public String getDataType() {
			return data_type;
		}
		public Integer getIndex() {
			return index;
		}
		public Boolean getIs_target() {
			return is_target;
		}
		public Integer getNumberOfDistinctValues() {
			return NumberOfDistinctValues;
		}
		public Integer getNumberOfUniqueValues() {
			return NumberOfUniqueValues;
		}
		public Integer getNumberOfMissingValues() {
			return NumberOfMissingValues;
		}
		public Integer getNumberOfIntegerValues() {
			return NumberOfIntegerValues;
		}
		public Integer getNumberOfRealValues() {
			return NumberOfRealValues;
		}
		public Integer getNumberOfNominalValues() {
			return NumberOfNominalValues;
		}
		public Integer getNumberOfValues() {
			return NumberOfValues;
		}
		public Double getMaximumValue() {
			return MaximumValue;
		}
		public Double getMinimumValue() {
			return MinimumValue;
		}
		public Double getMeanValue() {
			return MeanValue;
		}
		public Double getStandardDeviation() {
			return StandardDeviation;
		}
		public String getClassDistribution() {
			return ClassDistribution;
		}
		@Override
		public String toString() {
			return index + " - " + name;
		}
	}
}
