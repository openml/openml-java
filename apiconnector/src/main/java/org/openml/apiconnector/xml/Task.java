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
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.openml.apiconnector.algorithms.ArffHelper;
import org.openml.apiconnector.algorithms.OptionParser;
import org.openml.apiconnector.io.HttpConnector;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.settings.Constants;
import org.openml.apiconnector.settings.Settings;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import com.thoughtworks.xstream.converters.extended.ToAttributedValueConverter;

@XStreamAlias("oml:task")
public class Task implements Serializable {
	private static final long serialVersionUID = 987612341009L;

	@XStreamAsAttribute
	@XStreamAlias("xmlns:oml")
	private final String oml = Constants.OPENML_XMLNS;
	
	@XStreamAlias("oml:task_id")
	private Integer task_id;

	@XStreamAlias("oml:task_name")
	private String task_name;

	@XStreamAlias("oml:task_type_id")
	private Integer task_type_id;

	@XStreamAlias("oml:task_type")
	private String task_type;

	@XStreamImplicit(itemFieldName="oml:input")
	private Input[] inputs;
	
	@XStreamImplicit(itemFieldName="oml:output")
	private Output[] outputs;

	@XStreamImplicit(itemFieldName="oml:tag")
	private String[] tag;
	
	// for quick initialization. 
	public Task(int id)
	{
		this.task_id = id;
	}
	
	public Task(int id, String inputName1,Integer data_set_id, String target_feature, String inputName2, String data_splits_url, HashMap<String, String> parameters) 
	{
		this.task_id = id;
		Input firstInput = new Input(inputName1, data_set_id, target_feature);
		Input secondInput = new Input(inputName2, data_splits_url, parameters);
		this.inputs = new Input[2];
		this.inputs[0] = firstInput;
		this.inputs[1] = secondInput;
	}
	
	@Override
	public String toString() {
		return task_name;
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

	public String getTask_name() {
		return task_name;
	}

	public Integer getTask_type_id() {
		return task_type_id;
	}

	public String getTask_type() {
		return task_type;
	}

	public Input[] getInputs() {
		return inputs;
	}
	
	public Map<String, Input> getInputsAsMap() {
		Map<String, Input> result = new TreeMap<String, Task.Input>();
		for (Input i : inputs) {
			result.put(i.getName(), i);
		}
		return result;
	}

	public Output[] getOutputs() {
		return outputs;
	}

	public String[] getTags() {
		return tag;
	}

	public class Input implements Serializable {
		private static final long serialVersionUID = 987612341019L;
		
		public Input (String name, Integer datasetId, String targetFeature)
		{
			this.name = name;
			this.data_set = new Data_set(datasetId, targetFeature);
			
		}
		
		public Input (String name, String data_splits_url, HashMap<String, String> parameters)
		{
			this.name = name;
			this.estimation_procedure = new Estimation_procedure(data_splits_url, parameters);
		}
		
		@XStreamAsAttribute
		private String name;
		
		@XStreamAlias("oml:data_set")
		private Data_set data_set;

		@XStreamAlias("oml:stream_schedule")
		private Stream_schedule stream_schedule;
		
		@XStreamAlias("oml:estimation_procedure")
		private Estimation_procedure estimation_procedure;
		
		@XStreamAlias("oml:cost_matrix")
		private String cost_matrix;
		
		@XStreamAlias("oml:evaluation_meaures")
		private Evaluation_measures evaluation_measures;

		@XStreamAlias("oml:time_limit")
		private Double time_limit;

		@XStreamAlias("oml:quality_measure")
		private String quality_measure;
		
		public String getName() {
			return name;
		}

		public Data_set getData_set() {
			return data_set;
		}
		
		public Stream_schedule getStream_schedule() {
			return stream_schedule;
		}
		
		public Double getTime_limit() {
			return time_limit;
		}
		
		public String getQuality_measure() {
			return quality_measure;
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

		@XStreamAlias("oml:data_set")
		public class Data_set implements Serializable {
			private static final long serialVersionUID = 987612341029L;

			public Data_set(Integer data_set_id, String target_feature)
			{
				this.data_set_id = data_set_id;
				this.target_feature = target_feature;
			}
			@XStreamAlias("oml:data_set_id")
			private Integer data_set_id;

			@XStreamAlias("oml:labeled_data_set_id")
			private Integer labeled_data_set_id;

			@XStreamAlias("oml:target_feature")
			private String target_feature;

			@XStreamAlias("oml:target_feature_left")
			private String target_feature_left;

			@XStreamAlias("oml:target_feature_right")
			private String target_feature_right;

			@XStreamAlias("oml:target_feature_event")
			private String target_feature_event;

			@XStreamAlias("oml:target_value")
			private String target_value;
			
			@XStreamOmitField
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
			public String getTarget_value() {
				return target_value;
			}
			
			public DataSetDescription getDataSetDescription( OpenmlConnector apiconnector ) throws Exception {
				if(dsdCache == null) {
					dsdCache = apiconnector.dataGet(data_set_id);
				}
				return dsdCache;
			}
		}
		
		@XStreamAlias("oml:stream_schedule")
		public class Stream_schedule implements Serializable {
			private static final long serialVersionUID = -4788645256661953298L;

			@XStreamAlias("oml:train_url")
			private URL train_url;

			@XStreamAlias("oml:test_url")
			private URL test_url;

			@XStreamAlias("oml:start_time")
			private String start_time;

			@XStreamAlias("oml:initial_batch_size")
			private Integer initial_batch_size;

			@XStreamAlias("oml:batch_size")
			private Integer batch_size;

			@XStreamAlias("oml:batch_time")
			private Integer batch_time;

			public URL getTrain_url() {
				return train_url;
			}

			public URL getTest_url() {
				return test_url;
			}

			public String getStart_time() {
				return start_time;
			}

			public Integer getInitial_batch_size() {
				return initial_batch_size;
			}

			public Integer getBatch_size() {
				return batch_size;
			}

			public Integer getBatch_time() {
				return batch_time;
			}
			
			@Override
			public String toString() {
				return "[" + start_time + "; init: " + initial_batch_size + "; size: " + batch_size + "; time: " + batch_time + "]"; 
			}
		}

		@XStreamAlias("oml:estimation_procedure")
		public class Estimation_procedure implements Serializable {
			private static final long serialVersionUID = 987612341039L;

			public Estimation_procedure(String data_splits_url, HashMap<String, String> parameterList)
			{
				int size = parameterList.size();
				this.parameters = new Parameter[size];
				this.data_splits_url = data_splits_url;
				Iterator<String> keysIterator = parameterList.keySet().iterator();
				int counter = 0;
				while(keysIterator.hasNext())
				{
					String key = keysIterator.next();
					this.parameters[counter] = new Parameter(key, parameterList.get(key));
					counter++;
					
				}
			}
			@XStreamAlias("oml:type")
			private String type;

			@XStreamAlias("oml:data_splits_url")
			private String data_splits_url;

			@XStreamImplicit(itemFieldName="oml:parameter")
			private Parameter[] parameters;
			
			@XStreamOmitField
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
			
			public File getDataSplits( int task_id ) throws Exception {
				if( data_splits_cache == null ) {
					// TODO: we want to get rid of the server calculated Md5 ... 
					String serverMd5 = null;
					if( Settings.LOCAL_OPERATIONS ) {
						serverMd5 = HttpConnector.getStringFromUrl(getData_splits_url().replace("/get/", "/md5/"), false);
					}
					//	String identifier = getData_splits_url().substring( getData_splits_url().lastIndexOf('/') + 1 );
					data_splits_cache = ArffHelper.downloadAndCache("splits", task_id, "arff", getData_splits_url(), serverMd5 );
				}
				return data_splits_cache;
			}
			

			@XStreamConverter(value=ToAttributedValueConverter.class, strings={"value"})
			public class Parameter implements Serializable {
				private static final long serialVersionUID = 987612341099L;
				
				public Parameter(String parameterName, String value)
				{
					name = parameterName;
					this.value = value;
				}

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

			@XStreamImplicit(itemFieldName="oml:evaluation_measure")
			private String[] evaluation_measure;

			public String[] getEvaluation_measure() {
				return evaluation_measure;
			}
		}
	}
	
	public class Output implements Serializable {
		private static final long serialVersionUID = 987612341059L;
		
		@XStreamAsAttribute
		private String name;
		
		@XStreamAlias("oml:predictions")
		private Predictions predictions;
		
		public String getName() {
			return name;
		}

		public Predictions getPredictions() {
			return predictions;
		}
		
		public class Predictions implements Serializable {
			private static final long serialVersionUID = 987612341069L;

			@XStreamAlias("oml:format")
			private String format;
			
			@XStreamImplicit(itemFieldName="oml:feature")
			private Feature[] features;
			
			public String getFormat() {
				return format;
			}

			public Feature[] getFeatures() {
				return features;
			}

			@XStreamAlias("oml:feature")
			public class Feature implements Serializable {
				private static final long serialVersionUID = 987612341079L;
				
				@XStreamAsAttribute
				private String name;
				
				@XStreamAsAttribute
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
