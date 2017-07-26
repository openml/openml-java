/*
 *  OpenmlApiConnector - Java integration of the OpenML Web API
 *  Copyright (C) 2014 
 *  @author Jan N. van Rijn (j.n.van.rijn@liacs.leidenuniv.nl)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *  
 */
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
