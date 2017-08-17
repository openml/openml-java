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

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import org.openml.apiconnector.settings.Constants;
import org.openml.apiconnector.xml.Run.Data.File;
import org.apache.commons.lang3.ArrayUtils;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("oml:run")
public class Run {

	@XStreamAsAttribute
	@XStreamAlias("xmlns:oml")
	private final String oml = Constants.OPENML_XMLNS;

	@XStreamAlias("oml:run_id")
	private Integer run_id;
	@XStreamAlias("oml:uploader")
	private Integer uploader;
	@XStreamAlias("oml:task_id")
	private Integer task_id;
	@XStreamAlias("oml:flow_id")
	private int flow_id;
	@XStreamAlias("oml:flow_name")
	private String flow_name;
	@XStreamAlias("oml:setup_id")
	private Integer setup_id;
	@XStreamAlias("oml:setup_string")
	private String setup_string;
	@XStreamAlias("oml:error_message")
	private String error_message;
	@XStreamImplicit(itemFieldName="oml:parameter_setting")
	private Parameter_setting[] parameter_settings;
	@XStreamImplicit(itemFieldName="oml:tag")
	private String[] tag;
	@XStreamAlias("oml:input_data")
	private Data input_data;
	@XStreamAlias("oml:output_data")
	private Data output_data;

	public Run(Integer task_id, String error_message, int flow_id,
			String setup_string, Parameter_setting[] parameter_settings,
			String[] tags) {
		this.task_id = task_id;
		this.flow_id = flow_id;
		this.setup_string = setup_string;
		this.error_message = error_message;
		this.parameter_settings = parameter_settings;

		this.tag = tags;
		this.output_data = new Data();
		this.input_data = new Data();
	}

	public String getOml() {
		return oml;
	}

	public int getRun_id() {
		return run_id;
	}

	public int getUploader() {
		return uploader;
	}

	public int getTask_id() {
		return task_id;
	}

	public int getSetup_id() {
		return setup_id;
	}

	public int getFlow_id() {
		return flow_id;
	}

	public String getFlow_name() {
		return flow_name;
	}

	public String getError_message() {
		return error_message;
	}

	public String getSetup_string() {
		return setup_string;
	}

	public String[] getTag() {
		return tag;
	}

	public Parameter_setting[] getParameter_settings() {
		return parameter_settings;
	}

	public void addTag(String new_tag) {
		// check if tag is not already present
		if (tag != null) {
			if (Arrays.asList(tag).contains(new_tag) == true) {
				return;
			}
		}
		tag = ArrayUtils.addAll(tag, new_tag);
	}

	public void addInputData(String name, String url) {
		input_data.addDataset(name, url);
	}

	public void addOutputData(String name, String url) {
		output_data.addDataset(name, url);
	}

	public void addOutputEvaluation(EvaluationScore e) {
		output_data.addEvaluation(e);
	}

	public EvaluationScore[] getOutputEvaluation() {
		if (output_data == null) {
			return null;
		}
		return output_data.evaluation;
	}

	public File[] getOutputFile() {
		return output_data.file;
	}
	
	public Map<String, File> getOutputFileAsMap() {
		Map<String, File> result = new TreeMap<String, Run.Data.File>();
		if (output_data != null) {
			for (File f : output_data.file) {
				result.put(f.name, f);
			}
		}
		return result;
	}

	
	public static class Parameter_setting {
		@XStreamAlias("oml:name")
		private String name;
		@XStreamAlias("oml:value")
		private String value;
		@XStreamAlias("oml:component")
		private Integer component;

		public Parameter_setting(Integer component, String name, String value) {
			this.name = name;
			this.component = component;
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public int getComponent() {
			return component;
		}

		public String getValue() {
			return value;
		}

		@Override
		public String toString() {
			return component + "_" + name + ": " + value;
		}
	}

	public static class Data {
		@XStreamImplicit(itemFieldName="oml:dataset")
		private Dataset[] dataset;
		@XStreamImplicit(itemFieldName="oml:file")
		private File[] file;
		@XStreamImplicit(itemFieldName="oml:evaluation")
		private EvaluationScore[] evaluation;

		public Data() {
			dataset = new Dataset[0];
			evaluation = new EvaluationScore[0];
		}

		public Dataset[] getDataset() {
			return dataset;
		}

		public File[] file() {
			return file;
		}

		public EvaluationScore[] getEvaluation() {
			return evaluation;
		}

		public void addDataset(String name, String url) {
			Dataset d = new Dataset(name, url);
			dataset = ArrayUtils.addAll(dataset, d);
		}

		public void addEvaluation(EvaluationScore score) {
			evaluation = ArrayUtils.addAll(evaluation, score);
		}

		public static class Dataset {
			@XStreamAlias("oml:did")
			private Integer did;
			@XStreamAlias("oml:name")
			private String name;
			@XStreamAlias("oml:url")
			private String url;

			public Dataset(String name, String url) {
				this.name = name;
				this.url = url;
			}

			public int getDid() {
				return did;
			}

			public String getName() {
				return name;
			}

			public String getUrl() {
				return url;
			}
		}

		public static class File {
			@XStreamAlias("oml:did")
			private Integer did;
			@XStreamAlias("oml:file_id")
			private Integer file_id;
			@XStreamAlias("oml:name")
			private String name;
			@XStreamAlias("oml:format")
			private String format;
			@XStreamAlias("oml:upload_time")
			private String uploadTime;
			@XStreamAlias("oml:url")
			private String url;

			public int getDid() {
				return did;
			}

			public int getFileId() {
				return file_id;
			}

			public String getName() {
				return name;
			}

			public String getUrl() {
				return url;
			}
			
			public String getFormat() {
				return format;
			}
			
			public String getUploadTime() {
				return uploadTime;
			}
		}
	}
}
