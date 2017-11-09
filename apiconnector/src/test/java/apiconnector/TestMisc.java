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

import org.junit.Test;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.xml.Study;

@Ignore public class TestMisc {
	private static final String key_read = "c1994bdb7ecb3c6f3c8f3b35f4b47f1f";
	private static final String url_live = "https://www.openml.org/";
	private static final String url_test = "https://test.openml.org/";
	private static final OpenmlConnector client_live_read = new OpenmlConnector(url_live, key_read); 
	private static final OpenmlConnector client_test_read = new OpenmlConnector(url_test, key_read); 
	
	@Test
	public void testApiGetStudy() throws Exception {
		Study s = client_live_read.studyGet(34);
		assertTrue(s.getTag().length > 0);
		assertTrue(s.getDataset().length == 105);
		assertTrue(s.getFlows().length == 27);
		assertTrue(s.getTasks().length == 105);
		assertTrue(s.getSetups().length == 30);
	}
	

	@Test
	public void testApiGetStudyData() throws Exception {
		Study s = client_live_read.studyGet(34, "data");
		assertTrue(s.getTag().length > 0);
		assertTrue(s.getDataset().length > 5);
		assertTrue(s.getFlows() == null);
		assertTrue(s.getTasks() == null);
		assertTrue(s.getSetups() == null);
	}
	

	@Test
	public void testApiGetStudyTasks() throws Exception {
		Study s = client_live_read.studyGet(34, "tasks");
		assertTrue(s.getTag().length > 0);
		assertTrue(s.getDataset() == null);
		assertTrue(s.getFlows() == null);
		assertTrue(s.getTasks().length > 5);
		assertTrue(s.getSetups() == null);
	}
	
	@Test
	public void testApiGetStudyFlows() throws Exception {
		Study s = client_live_read.studyGet(34, "flows");
		assertTrue(s.getTag().length > 0);
		assertTrue(s.getDataset() == null);
		assertTrue(s.getFlows().length > 5);
		assertTrue(s.getTasks() == null);
		assertTrue(s.getSetups() == null);
	}
	
	@Test
	public void testApiGetStudySetups() throws Exception {
		Study s = client_live_read.studyGet(34, "setups");
		assertTrue(s.getTag().length > 0);
		assertTrue(s.getDataset() == null);
		assertTrue(s.getFlows() == null);
		assertTrue(s.getTasks() == null);
		assertTrue(s.getSetups().length > 5);
	}
	

	@Test
	public void testApiGetStudyByAlias() throws Exception {
		Study s = client_test_read.studyGet("OpenML100", "data");
		assertTrue(s.getTag().length > 0);
		assertTrue(s.getDataset().length > 10);
		assertTrue(s.getFlows() == null);
		assertTrue(s.getTasks() == null);
		assertTrue(s.getSetups() == null);
	}
}
