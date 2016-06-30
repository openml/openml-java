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

import java.util.Arrays;

import org.openml.apiconnector.algorithms.MathHelper;
import org.openml.apiconnector.settings.Constants;
import org.openml.apiconnector.xml.Run.Data.File;
import org.apache.commons.lang3.ArrayUtils;

public class Run {

	private final String oml = Constants.OPENML_XMLNS;
	private Integer run_id;
	private Integer uploader;
	private int task_id;
	private Integer setup_id;
	private int flow_id;
	private String setup_string;
	private String error_message;
	private Parameter_setting[] parameter_settings;
	private String[] tag;
	private Data input_data;
	private Data output_data;

	public Run(int task_id, String error_message, int flow_id,
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

	public void addOutputEvaluation(String name, Integer repeat, Integer fold,
			Integer sample, String flow, Double value) {
		output_data.addEvaluation(name, repeat, fold, sample, flow, value);
	}

	public void addOutputEvaluation(String name, String flow, Double value,
			String array_data) {
		output_data.addEvaluation(name, flow, value, array_data);
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

	public static class Parameter_setting {
		private String name;
		private String value;
		private int component;

		public Parameter_setting(int component, String name, String value) {
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
		private Dataset[] dataset;
		private File[] file;
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

		public void addEvaluation(String name, Integer repeat, Integer fold, Integer sample, String flow, Double value) {
			EvaluationScore e = new EvaluationScore(flow, name, MathHelper.defaultDecimalFormat.format(value), null, repeat, fold, sample, null);
			evaluation = ArrayUtils.addAll(evaluation, e);
		}

		public void addEvaluation(String name, String flow, Double value, String array_data) {
			EvaluationScore e = new EvaluationScore(flow, name, (value != null) ? MathHelper.defaultDecimalFormat.format(value) : null, null, array_data);
			evaluation = ArrayUtils.addAll(evaluation, e);
		}

		public void addEvaluation(EvaluationScore score) {
			evaluation = ArrayUtils.addAll(evaluation, score);
		}

		public static class Dataset {
			private Integer did;
			private String name;
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
			private Integer did;
			private Integer file_id;
			private String name;
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
		}
	}
}
