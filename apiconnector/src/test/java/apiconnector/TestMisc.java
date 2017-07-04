package apiconnector;

import static org.junit.Assert.*;

import org.junit.Test;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.xml.Study;

public class TestMisc {
	private static final String key_read = "c1994bdb7ecb3c6f3c8f3b35f4b47f1f";
	private static final String url = "https://www.openml.org/";
	private static final OpenmlConnector client_read = new OpenmlConnector(url, key_read); 
	
	@Test
	public void testApiGetStudy() throws Exception {
		Study s = client_read.studyGet(34);
		assertTrue(s.getTag().length > 0);
		assertTrue(s.getDataset().length == 105);
		assertTrue(s.getFlows().length == 27);
		assertTrue(s.getTasks().length == 105);
		assertTrue(s.getSetups().length == 30);
	}
}
