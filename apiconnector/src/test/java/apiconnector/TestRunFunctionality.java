package apiconnector;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.xml.Run;
import org.openml.apiconnector.xml.RunList;
import org.openml.apiconnector.xml.RunTag;
import org.openml.apiconnector.xml.RunUntag;
import org.openml.apiconnector.xml.UploadRun;
import org.openml.apiconnector.xml.UploadRunAttach;
import org.openml.apiconnector.xstream.XstreamXmlMapping;

import com.thoughtworks.xstream.XStream;

public class TestRunFunctionality {
	private static final int probe = 67;
	private static final int probeChallenge = 13976;
	private static final String predictions_path = "data/predictions_task53.arff";

	private static final String url = "http://capa.win.tue.nl/";
	private static final String session_hash = "d488d8afd93b32331cf6ea9d7003d4c3";
	private static final OpenmlConnector client = new OpenmlConnector(url,session_hash);
	private static final XStream xstream = XstreamXmlMapping.getInstance();
	private static final String tag = "junittest";
	
	@Test
	public void testApiRunDownload() {
		
		try {
			Run run = client.runGet(probe);
			
			File tempXml = Conversion.stringToTempFile(xstream.toXML(run), "run", "xml");
			File tempXsd = client.getXSD("openml.run.upload");
			
			assertTrue(Conversion.validateXML(tempXml, tempXsd));
			
			// very easy checks, should all pass
			assertTrue(run.getRun_id() == probe);
			
		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed: " + e.getMessage());
		}
	}
	
	@Test
	public void testApiRunList() throws Exception {
		List<Integer> uploaderId = new ArrayList<Integer>();
		uploaderId.add(29);
		
		RunList rl = client.runList(null, null, null, uploaderId);
		
		for (org.openml.apiconnector.xml.RunList.Run r : rl.getRuns()) {
			assertTrue(uploaderId.contains(r.getUploader()));
		}
		
	}
	
	@Test
	public void testApiRunUpload() {
		try {
			String[] tags = {"first_tag", "another_tag"};
			
			Run r = new Run(probe, null, 100, null, null, tags);
			String runXML = xstream.toXML(r);
			
			File runFile = Conversion.stringToTempFile(runXML, "runtest",  "xml");
			File predictions = new File(predictions_path); 
			
			Map<String,File> output_files = new HashMap<String, File>();
			
			output_files.put("predictions", predictions);
			
			UploadRun ur = client.runUpload(runFile, output_files);
			
			Run newrun = client.runGet(ur.getRun_id());
			
			Set<String> uploadedTags = new HashSet<String>(Arrays.asList(newrun.getTag()));
			Set<String> providedTags = new HashSet<String>(Arrays.asList(tags));
			
			assertTrue(uploadedTags.equals(providedTags));
			
			RunTag rt = client.runTag(ur.getRun_id(), tag);
			assertTrue(Arrays.asList(rt.getTags()).contains(tag));
			RunUntag ru = client.runUntag(ur.getRun_id(), tag);
			assertTrue(Arrays.asList(ru.getTags()).contains(tag) == false);
			
			client.runDelete(ur.getRun_id());
			
		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed: " + e.getMessage());
		}
	}
	
	@Test
	public void testApiUploadRunAttach() throws Exception {
		Run r = new Run(probeChallenge, null, 100, null, null, null);
		String runXML = xstream.toXML(r);
		File runFile = Conversion.stringToTempFile(runXML, "runtest",  "xml");
		File predictions = new File(predictions_path); 
		UploadRun ur = client.runUpload(runFile, null);
		for (int i = 0; i < 5; i+=1) {
			UploadRunAttach ura = client.runUploadAttach(ur.getRun_id(), i , runFile, predictions);
			
			assertTrue(ura.getPredictionFiles().length == i+1);
		}
	}
}
