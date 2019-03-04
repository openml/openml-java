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

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.openml.apiconnector.io.ApiException;
import org.openml.apiconnector.xml.DataFeature;
import org.openml.apiconnector.xml.DataQuality;
import org.openml.apiconnector.xml.DataQuality.Quality;

import testbase.BaseTestFramework;

import org.openml.apiconnector.xml.DataSetDescription;
import org.openml.apiconnector.xml.Flow;
import org.openml.apiconnector.xml.Run;
import org.openml.apiconnector.xml.RunEvaluation;
import org.openml.apiconnector.xml.RunTrace;
import org.openml.apiconnector.xml.TaskInputs;

public class TestUserTypePrivileges extends BaseTestFramework {

	private static final String data_file = "data/iris.arff";
	
	private static final Integer EVAL_ID = 2;
	
	private static final int PRIVATE_DATASET_ID = 130;
	
	@Test(expected=ApiException.class)
	public void testApiDataQualityUpload() throws Exception {
		DataQuality dq = new DataQuality(1, EVAL_ID, new Quality[0]);
		try {
			client_write_test.dataQualitiesUpload(dq);
		} catch(ApiException e) {
			assertTrue(e.getCode() == 106);
			throw e;
		}
	}
	
	@Test(expected=ApiException.class)
	public void testApiAttemptDownloadPrivateDataset() throws Exception {
		client_read_test.dataGet(PRIVATE_DATASET_ID);
	}
	
	@Test(expected=ApiException.class)
	public void testApiAttemptDownloadPrivateDataFeatures() throws Exception {
		client_read_test.dataFeatures(PRIVATE_DATASET_ID);
	}
	
	@Test(expected=ApiException.class)
	public void testApiAttemptDownloadPrivateDataQualities() throws Exception {
		client_read_test.dataQualities(PRIVATE_DATASET_ID, null);
	}
	
	@Test(expected=IOException.class)
	public void testApiAttemptDownloadPrivateDataFile() throws Exception {
		DataSetDescription dsd = client_admin_test.dataGet(PRIVATE_DATASET_ID);
		client_read_test.datasetGet(dsd);
	}

	@Test
	public void testApiAdminAttemptDownloadPrivateDataset() throws Exception {
		DataSetDescription dsd = client_admin_test.dataGet(PRIVATE_DATASET_ID);
		client_admin_test.dataFeatures(PRIVATE_DATASET_ID);
		client_admin_test.dataQualities(PRIVATE_DATASET_ID, null);
		client_admin_test.datasetGet(dsd);
	}
	
	public void testApiAdminDownloadPrivateDataset() throws Exception {
		client_admin_test.dataGet(PRIVATE_DATASET_ID);
	}
	
	public void testApiAdminDownloadPrivateDataFeatures() throws Exception {
		client_admin_test.dataFeatures(PRIVATE_DATASET_ID);
	}
	
	public void testApiAdminDownloadPrivateDataQualities() throws Exception {
		client_admin_test.dataQualities(PRIVATE_DATASET_ID, null);
	}
	
	public void testApiAdminDownloadPrivateDataFile() throws Exception {
		DataSetDescription dsd = client_admin_test.dataGet(PRIVATE_DATASET_ID);
		client_admin_test.datasetGet(dsd);
	}
	
	@Test(expected=ApiException.class)
	public void testApiDataFeatureUpload() throws Exception {
		DataFeature df = new DataFeature(1, EVAL_ID, new DataFeature.Feature[0]);
		try {
			client_write_test.dataFeaturesUpload(df);
		} catch(ApiException e) {
			assertTrue(e.getCode() == 106);
			throw e;
		}
	}
	
	@Test(expected=ApiException.class)
	public void testApiRunEvaluationUpload() throws Exception {
		RunEvaluation re = new RunEvaluation(1, 1);
		try {
			client_write_test.runEvaluate(re);
		} catch(ApiException e) {
			assertTrue(e.getCode() == 106);
			throw e;
		}
	}
	
	@Test(expected=ApiException.class)
	public void testApiRunTraceUpload() throws Exception {
		RunTrace rt = new RunTrace(1);
		try {
			client_write_test.runTraceUpload(rt);
		} catch(ApiException e) {
			assertTrue(e.getCode() == 106);
			throw e;
		}
	}
	
	@Test(expected=ApiException.class)
	public void testApiDataUpload() throws Exception {
		DataSetDescription dsd = new DataSetDescription("test", "Unit test should be deleted", "arff", "class");
		try {
			client_read_test.dataUpload(dsd, new File(data_file));
		} catch(ApiException e) {
			assertTrue(e.getCode() == 104);
			throw e;
		}
	}
	
	@Test(expected=ApiException.class)
	public void testApiStatusActivate() throws Exception {
		DataSetDescription dsd = new DataSetDescription("test", "Unit test should be deleted", "arff", "class");
		int dataId = client_write_test.dataUpload(dsd, new File(data_file));
		try {
			client_write_test.dataStatusUpdate(dataId, "active");
		} catch(ApiException e) {
			assertTrue(e.getCode() == 696);
			throw e;
		}
		client_admin_test.dataStatusUpdate(dataId, "active");
		client_write_test.dataStatusUpdate(dataId, "deactivated");
		client_admin_test.dataStatusUpdate(dataId, "active");
	}

	@Test(expected=ApiException.class)
	public void testApiDataTag() throws Exception {
		try {
			client_read_test.dataTag(1, "default_tag");
		} catch(ApiException e) {
			assertTrue(e.getCode() == 104);
			throw e;
		}
	}

	@Test(expected=ApiException.class)
	public void testApiDataUntag() throws Exception {
		try {
			client_read_test.dataUntag(1, "default_tag");
		} catch(ApiException e) {
			assertTrue(e.getCode() == 104);
			throw e;
		}
	}

	@Test(expected=ApiException.class)
	public void testApiDataDelete() throws Exception {
		try {
			client_read_test.dataDelete(1);
		} catch(ApiException e) {
			assertTrue(e.getCode() == 104);
			throw e;
		}
	}

	@Test(expected=ApiException.class)
	public void testApiFlowUpload() throws Exception {
		Flow flow = new Flow("test2", "weka.classifiers.test.javaunittest", "test", "test should be deleted", "english", "UnitTest");
		try {
			client_read_test.flowUpload(flow);
		} catch(ApiException e) {
			assertTrue(e.getCode() == 104);
			throw e;
		}
	}

	@Test(expected=ApiException.class)
	public void testApiTaskUpload() throws Exception {
		TaskInputs task = new TaskInputs(1, 1, null, null);
		try {
			client_read_test.taskUpload(task);
		} catch(ApiException e) {
			assertTrue(e.getCode() == 104);
			throw e;
		}
	}

	@Test(expected=ApiException.class)
	public void testApiRunUpload() throws Exception {
		Run run = new Run(1, null, 1, null, null, null);
		try {
			client_read_test.runUpload(run, null);
		} catch(ApiException e) {
			assertTrue(e.getCode() == 104);
			throw e;
		}
	}

	@Test(expected=ApiException.class)
	public void testApiFlowDelete() throws Exception {
		try {
			client_read_test.flowDelete(1);
		} catch(ApiException e) {
			assertTrue(e.getCode() == 104);
			throw e;
		}
	}

	@Test(expected=ApiException.class)
	public void testApiTaskDelete() throws Exception {
		try {
			client_read_test.taskDelete(1);
		} catch(ApiException e) {
			assertTrue(e.getCode() == 104);
			throw e;
		}
	}

	@Test(expected=ApiException.class)
	public void testApiRunDelete() throws Exception {
		try {
			client_read_test.runDelete(1);
		} catch(ApiException e) {
			assertTrue(e.getCode() == 104);
			throw e;
		}
	}
}
