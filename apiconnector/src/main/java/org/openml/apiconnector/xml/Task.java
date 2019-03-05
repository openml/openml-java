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

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONArray;
import org.openml.apiconnector.io.OpenmlBasicConnector;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import com.thoughtworks.xstream.converters.extended.ToAttributedValueConverter;

@XStreamAlias("oml:task")
public class Task extends OpenmlApiResponse {
	private static final long serialVersionUID = 987612341009L;
	
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
		
		public JSONArray getCost_Matrix() throws Exception {
			if (cost_matrix.length() > 0) {
				return new JSONArray(cost_matrix);
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
			
			public DataSetDescription getDataSetDescription( OpenmlBasicConnector apiconnector ) throws Exception {
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
			
			
			public String getType() {
				return type;
			}

			public URL getData_splits_url() throws MalformedURLException {
				return new URL(data_splits_url);
			}

			public Parameter[] getParameters() {
				return parameters;
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
