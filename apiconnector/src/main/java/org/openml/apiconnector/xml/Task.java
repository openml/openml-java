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

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import org.openml.apiconnector.algorithms.ArffHelper;
import org.openml.apiconnector.algorithms.OptionParser;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.settings.Constants;
import org.openml.apiconnector.settings.Settings;

public class Task implements Serializable {
	private static final long serialVersionUID = 987612341009L;

	private final String oml = Constants.OPENML_XMLNS;
	
	private Integer task_id;
	private String task_type;
	private Input[] inputs;
	private Output[] outputs;
	private String[] tag;
	
	// for quick initialization. 
	public Task(int id) {
		this.task_id = id;
	}
	
	@Override
	public String toString() {
		return "Task " + getTask_id() + ": " + getTask_type();
	}
	
	@Override
	public boolean equals(Object other) {
		if(other instanceof Task) {
			if( ((Task)other).getTask_id() == getTask_id() )
				return true;
		}
		return false;
	}
	
	public String getOml() {
		return oml;
	}

	public Integer getTask_id() {
		return task_id;
	}

	public String getTask_type() {
		return task_type;
	}

	public Input[] getInputs() {
		return inputs;
	}

	public Output[] getOutputs() {
		return outputs;
	}

	public String[] getTags() {
		return tag;
	}

	public class Input implements Serializable {
		private static final long serialVersionUID = 987612341019L;
		private String name;
		private Data_set data_set;
		private Estimation_procedure estimation_procedure;
		String cost_matrix;
		private Evaluation_measures evaluation_measures;
		
		public String getName() {
			return name;
		}

		public Data_set getData_set() {
			return data_set;
		}
		
		public double[][] getCost_Matrix() throws Exception {
			if( cost_matrix != null && cost_matrix.equals("") == false ) {
				return OptionParser.stringToArray(cost_matrix);
			} else { 
				return null;
			}
		}
		
		public Estimation_procedure getEstimation_procedure() {
			return estimation_procedure;
		}

		public Evaluation_measures getEvaluation_measures() {
			return evaluation_measures;
		}
		
		public class Data_set implements Serializable {
			private static final long serialVersionUID = 987612341029L;
			private Integer data_set_id;
			private Integer labeled_data_set_id;
			private String target_feature;
			private String target_feature_left;
			private String target_feature_right;
			private String target_feature_event;
			
			// do not serialize
			private DataSetDescription dsdCache;
			
			public Integer getData_set_id() {
				return data_set_id;
			}
			public Integer getLabeled_data_set_id() {
				return labeled_data_set_id;
			}
			public String getTarget_feature() {
				return target_feature;
			}
			public String getTarget_feature_left() {
				return target_feature_left;
			}
			public String getTarget_feature_right() {
				return target_feature_right;
			}
			public String getTarget_feature_event() {
				return target_feature_event;
			}
			
			public DataSetDescription getDataSetDescription( OpenmlConnector apiconnector ) throws Exception {
				if(dsdCache == null) {
					dsdCache = apiconnector.dataGet(data_set_id);
				}
				return dsdCache;
			}
		}
		
		public class Estimation_procedure implements Serializable {
			private static final long serialVersionUID = 987612341039L;
			private String type;
			private String data_splits_url;
			private Parameter[] parameters;
			
			// do not serialize
			private File data_splits_cache;
			
			public String getType() {
				return type;
			}

			public String getData_splits_url() {
				return data_splits_url;
			}

			public Parameter[] getParameters() {
				return parameters;
			}
			
			public File getDataSplits( int task_id ) throws IOException {
				if( data_splits_cache == null ) {
					// TODO: we want to get rid of the server calculated Md5 ... 
					String serverMd5 = null;
					if( Settings.LOCAL_OPERATIONS ) {
						serverMd5 = OpenmlConnector.getStringFromUrl( getData_splits_url().replace("/get/", "/md5/") );
					}
					//	String identifier = getData_splits_url().substring( getData_splits_url().lastIndexOf('/') + 1 );
					data_splits_cache = ArffHelper.downloadAndCache("splits", task_id, getData_splits_url(), serverMd5 );
				}
				return data_splits_cache;
			}

			public class Parameter implements Serializable {
				private static final long serialVersionUID = 987612341099L;
				private String name;
				private String value;
				
				public String getName() {
					return name;
				}
				public String getValue() {
					return value;
				}
			}
		}
		
		public class Evaluation_measures implements Serializable {
			private static final long serialVersionUID = 987612341049L;
			private String[] evaluation_measure;

			public String[] getEvaluation_measure() {
				return evaluation_measure;
			}
		}
	}
	
	public class Output implements Serializable {
		private static final long serialVersionUID = 987612341059L;
		private String name;
		private Predictions predictions;
		
		public String getName() {
			return name;
		}

		public Predictions getPredictions() {
			return predictions;
		}
		
		public class Predictions implements Serializable {
			private static final long serialVersionUID = 987612341069L;
			private String format;
			private Feature[] features;
			
			public String getFormat() {
				return format;
			}

			public Feature[] getFeatures() {
				return features;
			}

			public class Feature implements Serializable {
				private static final long serialVersionUID = 987612341079L;
				private String name;
				private String type;
				
				public String getName() {
					return name;
				}
				public String getType() {
					return type;
				}
			}
		}
	}
}
