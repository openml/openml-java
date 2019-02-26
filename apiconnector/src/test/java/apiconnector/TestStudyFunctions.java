package apiconnector;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openml.apiconnector.xml.Study;

public class TestStudyFunctions extends TestBase {
	
	@Test
	public void getStudy() throws Exception {
		Study study = client_read_test.studyGet(1);
		assertTrue(study.getTasks().length > 0);
		assertTrue(study.getDataset().length > 0);
		assertTrue(study.getSetups() == null);
		assertTrue(study.getFlows() == null);
	}
}
