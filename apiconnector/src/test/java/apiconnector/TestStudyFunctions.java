package apiconnector;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;
import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.xml.Study;
import org.openml.apiconnector.xml.StudyUpload;

public class TestStudyFunctions extends TestBase {
	
	@Test
	public void getStudy() throws Exception {
		Study study = client_read_test.studyGet(1);
		assertTrue(study.getTasks().length > 0);
		assertTrue(study.getDataset().length > 0);
		assertNull(study.getSetups());
		assertNull(study.getFlows());
		assertNull(study.getRuns());
		assertNull(study.getBenchmark_suite());
		assertEquals("task", study.getMain_entity_type());
	}
	
	@Test
	public void uploadStudy() throws Exception {
		// TODO!
		Integer[] taskIds = {1, 2, 3};
		Study study = new Study("test", "test", null, taskIds, null);
		File studyDescription = Conversion.stringToTempFile(xstream.toXML(study), "study", "xml");
		StudyUpload su = client_write_test.studyUpload(studyDescription);
		
		Study studyDownload = client_read_test.studyGet(su.getId());
		assertArrayEquals(taskIds, studyDownload.getTasks());
	}
}
