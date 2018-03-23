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
package org.openml.apiconnector.xstream;

import org.openml.apiconnector.xml.Data;
import org.openml.apiconnector.xml.DataDelete;
import org.openml.apiconnector.xml.DataFeature;
import org.openml.apiconnector.xml.DataFeatureUpload;
import org.openml.apiconnector.xml.DataQuality;
import org.openml.apiconnector.xml.DataQualityList;
import org.openml.apiconnector.xml.DataQualityUpload;
import org.openml.apiconnector.xml.DataTag;
import org.openml.apiconnector.xml.DataUnprocessed;
import org.openml.apiconnector.xml.DataUntag;
import org.openml.apiconnector.xml.EvaluationList;
import org.openml.apiconnector.xml.EvaluationRequest;
import org.openml.apiconnector.xml.EvaluationScore;
import org.openml.apiconnector.xml.FileUpload;
import org.openml.apiconnector.xml.FlowTag;
import org.openml.apiconnector.xml.FlowUntag;
import org.openml.apiconnector.xml.RunDelete;
import org.openml.apiconnector.xml.RunEvaluate;
import org.openml.apiconnector.xml.RunEvaluation;
import org.openml.apiconnector.xml.FlowDelete;
import org.openml.apiconnector.xml.FlowExists;
import org.openml.apiconnector.xml.DataSetDescription;
import org.openml.apiconnector.xml.ApiError;
import org.openml.apiconnector.xml.Flow;
import org.openml.apiconnector.xml.Job;
import org.openml.apiconnector.xml.Run;
import org.openml.apiconnector.xml.RunList;
import org.openml.apiconnector.xml.RunReset;
import org.openml.apiconnector.xml.RunTag;
import org.openml.apiconnector.xml.RunTraceUpload;
import org.openml.apiconnector.xml.RunUntag;
import org.openml.apiconnector.xml.SetupDelete;
import org.openml.apiconnector.xml.SetupDifferences;
import org.openml.apiconnector.xml.SetupExists;
import org.openml.apiconnector.xml.SetupParameters;
import org.openml.apiconnector.xml.SetupTag;
import org.openml.apiconnector.xml.SetupUntag;
import org.openml.apiconnector.xml.Study;
import org.openml.apiconnector.xml.Task;
import org.openml.apiconnector.xml.TaskDelete;
import org.openml.apiconnector.xml.TaskTag;
import org.openml.apiconnector.xml.TaskUntag;
import org.openml.apiconnector.xml.TaskInputs;
import org.openml.apiconnector.xml.Tasks;
import org.openml.apiconnector.xml.RunTrace;
import org.openml.apiconnector.xml.UploadDataSet;
import org.openml.apiconnector.xml.UploadFlow;
import org.openml.apiconnector.xml.UploadRun;
import org.openml.apiconnector.xml.UploadRunAttach;
import org.openml.apiconnector.xml.UploadTask;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.extended.ToAttributedValueConverter;
import com.thoughtworks.xstream.core.ClassLoaderReference;
import com.thoughtworks.xstream.core.util.CompositeClassLoader;
import com.thoughtworks.xstream.io.naming.NoNameCoder;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class XstreamXmlMapping {
	
	/**
	 * Generates a bidirectional mapping between the XML Objects (server) and 
	 * the Java Objects (OpenmlApiConnector package opg.openml.apiconnector.xml).
	 * 
	 * @return XStream - An XStream instance capable of mapping XML objects and
	 * OpenmlApiConnector Objects to each other. 
	 */
	public static XStream getInstance(ClassLoaderReference clr) {
		XStream xstream = new XStream(null,new DomDriver("UTF-8", new NoNameCoder()),clr);
		xstream.registerConverter(new EmptyDoubleConverter());
		xstream.processAnnotations(SetupParameters.class);
		xstream.processAnnotations(SetupExists.class);
		xstream.processAnnotations(SetupTag.class);
		xstream.processAnnotations(SetupUntag.class);
		
		xstream.processAnnotations(Data.class);
		xstream.processAnnotations(DataSetDescription.class);
		xstream.processAnnotations(DataTag.class);
		xstream.processAnnotations(DataUntag.class);
		xstream.processAnnotations(DataUnprocessed.class);
		xstream.processAnnotations(DataQuality.class);
		
		xstream.processAnnotations(TaskTag.class);
		xstream.processAnnotations(TaskUntag.class);
		xstream.processAnnotations(Tasks.class);
		xstream.processAnnotations(Task.class);
		
		xstream.processAnnotations(RunTag.class);
		xstream.processAnnotations(RunUntag.class);
		xstream.processAnnotations(RunList.class);
		xstream.processAnnotations(Run.class);
		
		xstream.processAnnotations(FlowTag.class);
		xstream.processAnnotations(FlowUntag.class);
		
		xstream.processAnnotations(Study.class);
		
		xstream.processAnnotations(EvaluationRequest.class);
		xstream.processAnnotations(RunEvaluation.class);
		xstream.processAnnotations(EvaluationScore.class);
		
		xstream.processAnnotations(UploadRunAttach.class);
		
		xstream.ignoreUnknownElements();
		
		// data feature
		xstream.alias("oml:data_features", DataFeature.class);
		xstream.aliasField("oml:did", DataFeature.class, "did");
		xstream.aliasField("oml:evaluation_engine_id", DataFeature.class, "evaluation_engine_id");
		xstream.aliasField("oml:error", DataFeature.class, "error");
		xstream.aliasAttribute(DataFeature.class, "oml", "xmlns:oml");
		xstream.addImplicitCollection(DataFeature.class, "features", "oml:feature", DataFeature.Feature.class);
		
		xstream.alias("oml:data_feature", DataFeature.Feature.class);
		xstream.aliasField("oml:index", DataFeature.Feature.class, "index");
		xstream.aliasField("oml:name", DataFeature.Feature.class, "name");
		xstream.aliasField("oml:data_type", DataFeature.Feature.class, "data_type");
		xstream.aliasField("oml:is_target", DataFeature.Feature.class, "is_target");
		xstream.aliasField("oml:NumberOfDistinctValues", DataFeature.Feature.class, "NumberOfDistinctValues" );
		xstream.aliasField("oml:NumberOfUniqueValues", DataFeature.Feature.class, "NumberOfUniqueValues" );
		xstream.aliasField("oml:NumberOfMissingValues", DataFeature.Feature.class, "NumberOfMissingValues" );
		xstream.aliasField("oml:NumberOfIntegerValues", DataFeature.Feature.class, "NumberOfIntegerValues" );
		xstream.aliasField("oml:NumberOfRealValues", DataFeature.Feature.class, "NumberOfRealValues" );
		xstream.aliasField("oml:NumberOfNominalValues", DataFeature.Feature.class, "NumberOfNominalValues" );
		xstream.aliasField("oml:NumberOfValues", DataFeature.Feature.class, "NumberOfValues" );
		xstream.aliasField("oml:MaximumValue", DataFeature.Feature.class, "MaximumValue" );
		xstream.aliasField("oml:MinimumValue", DataFeature.Feature.class, "MinimumValue" );
		xstream.aliasField("oml:MeanValue", DataFeature.Feature.class, "MeanValue" );
		xstream.aliasField("oml:StandardDeviation", DataFeature.Feature.class, "StandardDeviation" );
		xstream.aliasField("oml:ClassDistribution", DataFeature.Feature.class, "ClassDistribution" );
		
		// data qualities upload
		xstream.alias("oml:data_qualities_upload", DataQualityUpload.class);
		xstream.aliasField("oml:did", DataQualityUpload.class, "did");
		
		// data qualities list
		xstream.alias("oml:data_qualities_list", DataQualityList.class);
		xstream.addImplicitCollection(DataQualityList.class, "quality", "oml:quality", String.class);

		// data features upload
		xstream.alias("oml:data_features_upload", DataFeatureUpload.class);
		xstream.aliasField("oml:did", DataFeatureUpload.class, "did");
		
		// upload data set
		xstream.alias("oml:upload_data_set", UploadDataSet.class);
		xstream.aliasField("oml:id", UploadDataSet.class, "id");
		
		// data delete
		xstream.alias("oml:data_delete", DataDelete.class);
		xstream.aliasField("oml:id", DataDelete.class, "id");
		
		// flow 
		xstream.alias("oml:flow", Flow.class);
		xstream.aliasAttribute(Flow.class, "oml", "xmlns:oml");
		
		xstream.addImplicitCollection(Flow.class, "creator", "oml:creator", String.class);
		xstream.addImplicitCollection(Flow.class, "contributor", "oml:contributor", String.class);
		xstream.addImplicitCollection(Flow.class, "parameter", "oml:parameter", Flow.Parameter.class);
		xstream.addImplicitCollection(Flow.class, "component", "oml:component", Flow.Component.class);
		xstream.addImplicitCollection(Flow.class, "tag", "oml:tag", String.class);
		
		xstream.aliasField("oml:id", Flow.class, "id");
		xstream.aliasField("oml:fullName", Flow.class, "fullName");
		xstream.aliasField("oml:name", Flow.class, "name");
		xstream.aliasField("oml:custom_name", Flow.class, "custom_name");
		xstream.aliasField("oml:class_name", Flow.class, "class_name");
		xstream.aliasField("oml:version", Flow.class, "version");
		xstream.aliasField("oml:external_version", Flow.class, "external_version");
		xstream.aliasField("oml:uploader", Flow.class, "uploader");
		xstream.aliasField("oml:upload_date", Flow.class, "upload_date");
		xstream.aliasField("oml:description", Flow.class, "description");
		xstream.aliasField("oml:licence", Flow.class, "licence");
		xstream.aliasField("oml:language", Flow.class, "language");
		xstream.aliasField("oml:full_description", Flow.class, "full_description");
		xstream.aliasField("oml:installation_notes", Flow.class, "installation_notes");
		xstream.aliasField("oml:dependencies", Flow.class, "dependencies");
		
		xstream.aliasField("oml:source_url", Flow.class, "source_url");
		xstream.aliasField("oml:source_format", Flow.class, "source_format");
		xstream.aliasField("oml:source_md5", Flow.class, "source_md5");
		
		xstream.aliasField("oml:binary_url", Flow.class, "binary_url");
		xstream.aliasField("oml:binary_format", Flow.class, "binary_format");
		xstream.aliasField("oml:binary_md5", Flow.class, "binary_md5");
		
		// flow component
		xstream.alias("oml:flow_component", Flow.Component.class);
		xstream.aliasField("oml:identifier", Flow.Component.class, "identifier");
		xstream.aliasField("oml:flow", Flow.Component.class, "flow");
		
		// parameter
		xstream.alias("oml:parameter", Flow.Parameter.class);
		xstream.aliasField("oml:name", Flow.Parameter.class, "name");
		xstream.aliasField("oml:data_type", Flow.Parameter.class, "data_type");
		xstream.aliasField("oml:default_value", Flow.Parameter.class, "default_value");
		xstream.aliasField("oml:description", Flow.Parameter.class, "description");
		
		// upload flow
		xstream.alias("oml:upload_flow", UploadFlow.class);
		xstream.aliasField("oml:id", UploadFlow.class, "id");
		
		// delete flow
		xstream.alias("oml:flow_delete", FlowDelete.class);
		xstream.aliasField("oml:id", FlowDelete.class, "id");
		
		// flow exists
		xstream.alias("oml:flow_exists", FlowExists.class);
		xstream.aliasField("oml:exists", FlowExists.class, "exists");
		xstream.aliasField("oml:id", FlowExists.class, "id");
		
		// generic error
		xstream.alias("oml:error", ApiError.class);
		xstream.aliasAttribute(ApiError.class, "oml", "xmlns:oml");
		xstream.aliasField("oml:code", ApiError.class, "code");
		xstream.aliasField("oml:message", ApiError.class, "message");
		xstream.aliasField("oml:additional_information", ApiError.class, "additional_information");

		// upload task
		xstream.alias("oml:upload_task", UploadTask.class);
		xstream.aliasField("oml:id", UploadTask.class, "id");
		
		// task inputs (used for uploading tasks)
		xstream.alias("oml:task_inputs", TaskInputs.class);
		xstream.aliasAttribute(TaskInputs.class, "oml", "xmlns:oml");
		
		xstream.alias("oml:input", TaskInputs.Input.class);
		
		xstream.aliasField("oml:task_id", TaskInputs.class, "task_id");
		xstream.aliasField("oml:task_type_id", TaskInputs.class, "task_type_id");
		xstream.addImplicitCollection(TaskInputs.class, "inputs", "oml:input", TaskInputs.Input.class);
		xstream.addImplicitCollection(TaskInputs.class, "tags", "oml:tag", String.class);
		
		xstream.useAttributeFor(TaskInputs.Input.class, "name");
		xstream.registerConverter(new ToAttributedValueConverter(TaskInputs.Input.class, xstream.getMapper(), xstream.getReflectionProvider(), xstream.getConverterLookup(), "value"));

		// task delete
		xstream.alias("oml:task_delete", TaskDelete.class);
		xstream.aliasField("oml:id", TaskDelete.class, "id");

		// evaluation list
		xstream.alias("oml:evaluations", EvaluationList.class);
		xstream.aliasAttribute(EvaluationList.class, "oml", "xmlns:oml");
		xstream.addImplicitCollection(EvaluationList.class, "evaluation", "oml:evaluation", EvaluationList.Evaluation.class);
		xstream.aliasField("oml:run", RunList.class, "run");
		
		xstream.aliasField("oml:run_id", EvaluationList.Evaluation.class, "run_id");
		xstream.aliasField("oml:task_id", EvaluationList.Evaluation.class, "task_id");
		xstream.aliasField("oml:setup_id", EvaluationList.Evaluation.class, "setup_id");
		xstream.aliasField("oml:flow_id", EvaluationList.Evaluation.class, "flow_id");
		xstream.aliasField("oml:function", EvaluationList.Evaluation.class, "function");
		xstream.aliasField("oml:value", EvaluationList.Evaluation.class, "value");
		
		// upload run
		xstream.alias("oml:upload_run", UploadRun.class);
		xstream.aliasField("oml:run_id", UploadRun.class, "run_id");
		
		// reset run
		xstream.alias("oml:run_reset", RunReset.class);
		xstream.aliasField("oml:id", RunReset.class, "run_id");

		// delete run
		xstream.alias("oml:run_delete", RunDelete.class);
		xstream.aliasField("oml:id", RunDelete.class, "id");
		
		// run evaluate
		xstream.alias("oml:run_evaluate", RunEvaluate.class);
		xstream.aliasField("oml:run_id", RunEvaluate.class, "run_id");

		// run trace
		xstream.alias("oml:run_trace", RunTraceUpload.class);
		xstream.aliasField("oml:run_id", RunTraceUpload.class, "run_id");
		
		// reset run
		xstream.alias("oml:file_upload", FileUpload.class);
		xstream.aliasField("oml:id", FileUpload.class, "id");
		xstream.aliasField("oml:url", FileUpload.class, "url");
		
		// run getjob
		xstream.alias("oml:job", Job.class);
		xstream.aliasField("oml:task_id", Job.class, "task_id");
		xstream.aliasField("oml:learner", Job.class, "learner");
		
		// run trace
		xstream.alias("oml:trace", RunTrace.class);
		xstream.aliasAttribute(RunTrace.class, "oml", "xmlns:oml");
		xstream.aliasField("oml:run_id", RunTrace.class, "run_id");
		xstream.addImplicitCollection(RunTrace.class, "trace_iterations", "oml:trace_iteration", RunTrace.Trace_iteration.class);
		
		xstream.aliasField("oml:repeat", RunTrace.Trace_iteration.class, "repeat");
		xstream.aliasField("oml:fold", RunTrace.Trace_iteration.class, "fold");
		xstream.aliasField("oml:iteration", RunTrace.Trace_iteration.class, "iteration");
		xstream.aliasField("oml:setup_string", RunTrace.Trace_iteration.class, "setup_string");
		xstream.aliasField("oml:evaluation", RunTrace.Trace_iteration.class, "evaluation");
		xstream.aliasField("oml:selected", RunTrace.Trace_iteration.class, "selected");
		
		// setupDifferences
		xstream.alias("oml:setup_differences", SetupDifferences.class);
		xstream.aliasAttribute(RunList.class, "oml", "xmlns:oml");
		xstream.addImplicitCollection(SetupDifferences.class, "tasks", "oml:task", SetupDifferences.Task.class);
	//	xstream.aliasField("oml:run", RunList.class, "run");

		xstream.aliasField("oml:setupA", SetupDifferences.Task.class, "setupA");
		xstream.aliasField("oml:setupB", SetupDifferences.Task.class, "setupB");
		xstream.aliasField("oml:task_id", SetupDifferences.Task.class, "task_id");
		xstream.aliasField("oml:task_size", SetupDifferences.Task.class, "task_size");
		xstream.aliasField("oml:differences", SetupDifferences.Task.class, "differences");
		
		// delete setup
		xstream.alias("oml:setup_delete", SetupDelete.class);
		xstream.aliasField("oml:id", SetupDelete.class, "id");
		
		return xstream;
	}
	
	public static XStream getInstance() {
		return getInstance(new ClassLoaderReference(new CompositeClassLoader()));
	}
}
