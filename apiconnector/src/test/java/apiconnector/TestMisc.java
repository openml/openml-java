package apiconnector;

import static org.junit.Assert.*;

import org.junit.Test;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.xml.Study;

public class TestMisc {
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
