package apiconnector;

import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Test;
import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.xml.Flow;
import org.openml.apiconnector.xml.FlowDelete;
import org.openml.apiconnector.xml.UploadFlow;
import org.openml.apiconnector.xstream.XstreamXmlMapping;

import com.thoughtworks.xstream.XStream;

public class TestFlowFunctionality {
	private static final int probe = 100;

	private static final String url = "http://www.openml.org/";
	private static final String session_hash = "d488d8afd93b32331cf6ea9d7003d4c3";
	private static final OpenmlConnector client = new OpenmlConnector(url,session_hash);
	private static final XStream xstream = XstreamXmlMapping.getInstance();
	
	
	@Test
	public void testApiFlowDownload() {
		
		try {
			Flow download = client.flowGet(probe);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed: " + e.getMessage());
		}
	}
	
	@Test
	public void testApiFlowUpload() {
		client.setVerboseLevel(1);
		try {
			Flow created = new Flow("test", "test", "test should be deleted", "english", "UnitTest");
			created.addComponent("B", new Flow("test2", "test2", "test should be deleted", "english", "UnitTest") );
			String flowXML = xstream.toXML(created);
			
			System.out.println(flowXML);
			
			File f = Conversion.stringToTempFile(flowXML, "test", "xml");
			
			UploadFlow uf = client.flowUpload(f, f, f);
			
		//	client.flowTag(uf.getId(), "testTag"); // TODO: flow tag properly
			
			FlowDelete fd = client.flowDelete(uf.getId());
			
		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed: " + e.getMessage());
		}
	}
}
