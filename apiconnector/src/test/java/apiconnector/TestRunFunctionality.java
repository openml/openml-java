package apiconnector;

import static org.junit.Assert.fail;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.xml.Run;
import org.openml.apiconnector.xml.RunTag;
import org.openml.apiconnector.xml.UploadRun;
import org.openml.apiconnector.xstream.XstreamXmlMapping;

import com.thoughtworks.xstream.XStream;

public class TestRunFunctionality {
	private static final int probe = 68;
	private static final String predictions_path = "C:\\data\\predictions_68.arff";

	private static final String url = "http://www.openml.org/";
	private static final String session_hash = "d488d8afd93b32331cf6ea9d7003d4c3";
	private static final OpenmlConnector client = new OpenmlConnector(url,session_hash);
	private static final XStream xstream = XstreamXmlMapping.getInstance();
	
	@Test
	public void testApiRunDownload() {
		
		try {
			Run download = client.runGet(probe);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed: " + e.getMessage());
		}
	}
	
	@Test
	public void testApiRunUpload() {
		client.setVerboseLevel(1);
		try {
			Run r = new Run(probe, null, 100, null, null, null);
			String runXML = xstream.toXML(r);
			
			File runFile = Conversion.stringToTempFile(runXML, "runtest",  "xml" );
			File predictions = new File(predictions_path); 
			
			Map<String,File> output_files = new HashMap<String, File>();
			
			output_files.put("predictions", predictions);
			
			UploadRun ur = client.runUpload(runFile, output_files);
			
			RunTag rt = client.runTag(ur.getRun_id(), "testTag");
			
			client.runDelete(ur.getRun_id());
			
		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed: " + e.getMessage());
		}
		
	}
}
