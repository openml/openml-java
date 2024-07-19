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
package apiconnector;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.junit.Test;
import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.io.ApiException;
import org.openml.apiconnector.xml.EvaluationRequest;
import org.openml.apiconnector.xml.EvaluationScore;
import org.openml.apiconnector.xml.Run;
import org.openml.apiconnector.xml.RunList;
import org.openml.apiconnector.xml.RunTag;
import org.openml.apiconnector.xml.RunUntag;
import org.openml.apiconnector.xml.Task;

import testbase.BaseTestFramework;

public class TestRunFunctionality extends BaseTestFramework {
	private static final int classif_task_id = 67;
	private static final int curve_task_id = 763; // anneal
	private static final int num_repeats = 1;
	private static final int num_folds = 10;
	private static final int num_samples = 9; // training set size approximately 900
	private static final String predictions_path = "data/predictions_task53.arff";
	private static final int FLOW_ID = 10;
	
	private static final String tag = "junittest";
	
	@Test
	public void testApiRunDownload() throws Exception {
		
		Run run = client_read_live.runGet(classif_task_id);
		
		File tempXml = Conversion.stringToTempFile(xstream.toXML(run), "run", "xml");
		File tempXsd = client_read_live.getXSD("openml.run.upload");
		
		assertTrue(Conversion.validateXML(tempXml, tempXsd));
		
		// very easy checks, should all pass
		assertTrue(run.getRun_id() == classif_task_id);
		
	}
	
	@Test
	public void testApiRunList() throws Exception {
		List<Integer> uploaderFilter = new ArrayList<Integer>();
		uploaderFilter.add(1);
		uploaderFilter.add(16);
		
		Map<String, List<Integer>> filters = new HashMap<String, List<Integer>>();
		filters.put("uploader", uploaderFilter);
		
		RunList rl = client_read_live.runList(filters, 100, 0);
		assertTrue(rl.getRuns().length == 100);
		
		for (Run r : rl.getRuns()) {
			assertTrue(uploaderFilter.contains(r.getUploader()));
		}
		
	}
	
	@Test
	public void testApiRunUpload() throws Exception {
		String[] tags = {"first_tag", "another_tag"};
		
		Run r = new Run(classif_task_id, null, FLOW_ID, null, null, tags);
		
		for (int i = 0; i < num_repeats; ++i) {
			for (int j = 0; j < num_folds; ++j) {
				r.addOutputEvaluation(new EvaluationScore("predictive_accuracy", 1.0, "[1.0, 1.0]", i, j, null, null));
			}
		}
		
		File predictions = new File(predictions_path); 
		
		Map<String,File> output_files = new HashMap<String, File>();
		
		output_files.put("predictions", predictions);
		
		int runId = client_write_test.runUpload(r, output_files);
		
		Run newrun = client_write_test.runGet(runId);
		
		Set<String> uploadedTags = new HashSet<String>(Arrays.asList(newrun.getTag()));
		Set<String> providedTags = new HashSet<String>(Arrays.asList(tags));
		
		assertTrue(uploadedTags.equals(providedTags));
		
		RunTag rt = client_write_test.runTag(runId, tag);
		assertTrue(Arrays.asList(rt.getTags()).contains(tag));
		RunUntag ru = client_write_test.runUntag(runId, tag);
		assertTrue(Arrays.asList(ru.getTags()).contains(tag) == false);
		
		client_write_test.runDelete(runId);
	}
	
	@Test
	public void testApiRunUploadIllegalMeasure() throws Exception {
		Run r = new Run(classif_task_id, null, FLOW_ID, null, null, null);
		r.addOutputEvaluation(new EvaluationScore("unexisting", 1.0, "[1.0, 1.0]", 0, 0, null, null));
		client_write_test.runUpload(r, null);
	}
	
	@Test
	public void testApiRunUploadWronglyParameterziedMeasureRepeats() throws Exception {
		Run r = new Run(classif_task_id, null, FLOW_ID, null, null, null);
		r.addOutputEvaluation(new EvaluationScore("predictive_accuracy", 1.0, "[1.0, 1.0]", num_repeats, 0, null, null));
		client_write_test.runUpload(r, null);
	}
	
	@Test
	public void testApiRunUploadWronglyParameterziedMeasureFolds() throws Exception {
		Run r = new Run(classif_task_id, null, FLOW_ID, null, null, null);
		r.addOutputEvaluation(new EvaluationScore("predictive_accuracy", 1.0, "[1.0, 1.0]", 0, num_folds, null, null));
		client_write_test.runUpload(r, null);
	}
	
	@Test
	public void testApiRunUploadWronglyParameterziedMeasureSample() throws Exception {
		Run r = new Run(classif_task_id, null, FLOW_ID, null, null, null);
		r.addOutputEvaluation(new EvaluationScore("predictive_accuracy", 1.0, "[1.0, 1.0]", 0, 0, 0, 0));
		client_write_test.runUpload(r, null);
	}
	
	@Test
	public void testApiRunUploadWronglyParameterziedMeasureSampleCurveTask() throws Exception {
		Run r = new Run(curve_task_id, null, FLOW_ID, null, null, null);
		r.addOutputEvaluation(new EvaluationScore("predictive_accuracy", 1.0, "[1.0, 1.0]", 0, 0, num_samples, null));
		client_write_test.runUpload(r, null);
	}
	

	@Test
	public void testApiRunUploadSamples() throws Exception {
		Run r = new Run(curve_task_id, null, FLOW_ID, null, null, null);
		for (int i = 0; i < num_samples; ++i) {
			r.addOutputEvaluation(new EvaluationScore("predictive_accuracy", 1.0, "[1.0, 1.0]", 0, 0, i, null));
			client_write_test.runUpload(r, null);
		}
	}
	
	@Test
	public void testApiEvaluationRequest() throws Exception {
		// this test assumes that there are runs on the test server. 
		// might not be the case just after reset 
		
		// gives evaluation id "42", which does not exist. 
		// therefore we get an actual run that is not evaluated by this engine back. 
		Map<String, String> filters = new TreeMap<String, String>();
		Integer[] ttids = {3,4};
		String ttidString = Arrays.toString(ttids).replaceAll(" ", "").replaceAll("\\[", "").replaceAll("\\]", "");
		int numRequests = 25;
		filters.put("ttid", ttidString);
		EvaluationRequest er = client_admin_test.evaluationRequest(42, "random", numRequests, filters);
		assertTrue(er.getRuns().length == numRequests);
		Run r = client_write_test.runGet(er.getRuns()[0].getRun_id());
		Task t = client_write_test.taskGet(r.getTask_id());
		assertTrue(Arrays.asList(ttids).contains(t.getTask_type_id()));
	}
}
