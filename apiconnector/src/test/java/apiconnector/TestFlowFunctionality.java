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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Arrays;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.io.ApiException;
import org.openml.apiconnector.xml.Flow;
import org.openml.apiconnector.xml.FlowTag;
import org.openml.apiconnector.xml.FlowUntag;
import org.openml.apiconnector.xml.Parameter;
import org.openml.apiconnector.xml.UploadFlow;

public class TestFlowFunctionality extends TestBase {
	private static final int probe = 10;
	
	private static final String tag = "junittest";
	
	@Test
	public void testApiFlowDownload() throws Exception {
		Flow flow = client_read_test.flowGet(probe);

		File tempXml = Conversion.stringToTempFile(xstream.toXML(flow), "flow", "xml");
		File tempXsd = client_read_test.getXSD("openml.implementation.upload");
		
		System.out.println(Conversion.fileToString(tempXml));
		
		assertTrue(Conversion.validateXML(tempXml, tempXsd));

		// very easy checks, should all pass
		assertTrue(flow.getId() == probe);

	}
	
	@Test
	public void testApiFlowUpload() throws Exception {
		
		Integer uploaded_id = -1;
		try {
			Flow created = new Flow("test", "weka.classifiers.test.janistesting", "test", "test should be deleted", "english", "UnitTest");
			created.addComponent("B", new Flow("test2", "weka.classifiers.test.janistesting.subflow2", "test2", "test should be deleted", "english", "UnitTest") );
			created.addComponent("C", new Flow("test3", "weka.classifiers.test.janistesting.subflow3", "test3", "test should be deleted", "english", "UnitTest") );
			created.addComponent("D", new Flow("test4", "weka.classifiers.test.janistesting.subflow4", "test4", "test should be deleted", "english", "UnitTest") );
			
			created.addParameter(new Parameter("test_a", "option", "bla1", "more bla1"));
			created.addParameter(new Parameter("test_p", "option", "bla2", "more bla2"));
			created.addParameter(new Parameter("test_q", "option", "blaq", "more blaqq"));
			String flowXML = xstream.toXML(created);
			
			File f = Conversion.stringToTempFile(flowXML, "test", "xml");
			
			UploadFlow uf = client_write_test.flowUpload(f, f, f);
			uploaded_id = uf.getId();
			FlowTag ft = client_write_test.flowTag(uf.getId(), tag);
			assertTrue(Arrays.asList(ft.getTags()).contains(tag));
			FlowUntag fu = client_write_test.flowUntag(uf.getId(), tag);
			assertTrue(fu.getTags() == null);
			
			Flow downloaded = client_read_test.flowGet(uf.getId());
			assertEquals(3, created.getParameter().length);
			assertEquals(3, created.getComponent().length);
			assertEquals(created.getParameter().length, downloaded.getParameter().length);
			assertEquals(created.getComponent().length, downloaded.getComponent().length);
		} catch (Exception e) {
			e.printStackTrace();
			// possibly the previous test failed, and left the exact same flow on the server.
			if (e instanceof ApiException) {
				ApiException apiException = (ApiException) e;
				if (apiException.getCode() == 171) {
					int index = apiException.getMessage().indexOf("implementation_id") + "implementation_id".length()+1;
					uploaded_id = Integer.parseInt(apiException.getMessage().substring(index));
				}
			}
			fail("Test failed: " + e.getMessage());
		} finally {
			client_write_test.flowDelete(uploaded_id);
		}
	}

	@Test
	public void testApiFlowUploadDuplicate() throws Exception {
		Flow created = new Flow("test2", "weka.classifiers.test.javaunittest", "test", "test should be deleted",
				"english", "UnitTest");
		created.addComponent("B", new Flow("test2", "weka.classifiers.test.janistesting", "test2",
				"test should be deleted", "english", "UnitTest"));
		created.addParameter(new Parameter("test_p", "option", "bla", "more bla"));
		created.setCustom_name("Jans flow");
		String flowXML = xstream.toXML(created);

		File f = Conversion.stringToTempFile(flowXML, "test", "xml");

		UploadFlow uf = client_write_test.flowUpload(f, f, f);

		try {
			UploadFlow uf2 = client_write_test.flowUpload(f, null, null);
			System.out.println(uf2.getId());

			fail("Test failed, flow upload should have been blocked.");
		} catch (Exception e) {
			// we expect an exception
			System.out.println(e.getMessage());
		}
		client_write_test.flowDelete(uf.getId());

	}

	@Test
	public void testUploadComplicatedFlow() throws Exception {
		Random random = new Random(System.currentTimeMillis());
		String complicatedFlow = FileUtils.readFileToString(new File("data/FilteredClassifier_RandomForest.xml"))
				.replace("{SENTINEL}", "SEN" + Math.abs(random.nextInt()));
		File f = Conversion.stringToTempFile(complicatedFlow, "test", "xml");
		
		UploadFlow uf = client_write_test.flowUpload(f, null, null);
		client_write_test.flowDelete(uf.getId());
	}
}
