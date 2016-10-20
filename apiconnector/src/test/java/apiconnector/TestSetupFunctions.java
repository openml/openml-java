package apiconnector;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.io.ApiException;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.xml.Run;
import org.openml.apiconnector.xml.SetupExists;
import org.openml.apiconnector.xml.SetupTag;
import org.openml.apiconnector.xml.SetupUntag;
import org.openml.apiconnector.xstream.XstreamXmlMapping;

import com.thoughtworks.xstream.XStream;

public class TestSetupFunctions {
	
	private static final String url = "http://test.openml.org/";
	private static final String session_hash = "d488d8afd93b32331cf6ea9d7003d4c3";
	private static final OpenmlConnector client = new OpenmlConnector(url,session_hash);
	private static final XStream xstream = XstreamXmlMapping.getInstance();
	private static final String tag = "junittest";
	
	@Test
	public void testFindRightSetup() throws Exception {
		client.setVerboseLevel(2);
		Integer[] run_ids = {541980, 541944, 541932};
		
		for (Integer run_id : run_ids) {
			Run r = client.runGet(run_id);
			int setup_id = r.getSetup_id();
			
			File description = getDescriptionFile(r, run_id);
			SetupExists se = client.setupExists(description);
			assertTrue(se.exists());
			assertTrue(se.getId() == setup_id);

			try {
				SetupTag st = client.setupTag(setup_id, tag);
				assertTrue(Arrays.asList(st.getTags()).contains(tag));
			} catch(ApiException ae) {
				// tolerate. 
				assertTrue(ae.getMessage().equals("Entity already tagged by this tag. "));
			}
			SetupUntag su = client.setupUntag(setup_id, tag);
			assertTrue(su.getTags() == null);
			
		}
	}
	
	@Test
	public void testFindNonexistingSetup() throws Exception {
		Integer[] run_ids = {541980, 541944, 541932};
		Map<String,String> searchReplace = new TreeMap<String,String>();
		searchReplace.put("<oml:value>Inversion</oml:value>", "<oml:value>bla2</oml:value>"); // matches run 541980
		searchReplace.put("<oml:value>weka.classifiers.trees.J48 -C 0.25 -M 2</oml:value>", "<oml:value>weka.classifiers.trees.J50 -C 0.25 -M 2</oml:value>"); // matches run 541944
		searchReplace.put("<oml:value>False</oml:value>", "<oml:value>NoValidBooleans</oml:value>"); // matches run 541932
		
		for (Integer run_id : run_ids) {
			Run r = client.runGet(run_id);
			
			File description = getDescriptionFile(r, run_id);
			
			// adjust the run file
			Path path = Paths.get(description.getAbsolutePath());
			Charset charset = StandardCharsets.UTF_8;
			
			String content = new String(Files.readAllBytes(path), charset);
			for (String search : searchReplace.keySet()) {
				content = content.replaceAll(search, searchReplace.get(search));
			}
			Files.write(path, content.getBytes(charset));
			SetupExists se = client.setupExists(description);
			assertTrue(se.exists() == false);
			
			// now try it with empty run file
			Run rEmpty = new Run(null, null, r.getFlow_id(), null, null, null);
			File runEmpty = Conversion.stringToTempFile(xstream.toXML(rEmpty), "openml-retest-run" + run_id, "xml");

			SetupExists se2 = client.setupExists(runEmpty);
			assertTrue(se2.exists() == false);
		}
	}
	
	private static File getDescriptionFile(Run r, int run_id) throws Exception {
		Integer descriptionFileId = r.getOutputFileAsMap().get("description").getFileId();
		URL descriptionUrl = client.getOpenmlFileUrl(descriptionFileId, "description_run" + run_id + ".xml");
		File description = File.createTempFile("description_run" + run_id, ".xml");
		FileUtils.copyURLToFile(descriptionUrl, description);
		return description;
	}
	
}
