package apiconnector;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;
import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.xml.Study;
import org.openml.apiconnector.xml.StudyUpload;

public class TestStudyFunctions extends TestBase {
	
	@Test
	public void testApiGetStudy() throws Exception {
		Study s = client_read_live.studyGet(34);
		assertTrue(s.getDataset().length == 105);
		assertTrue(s.getFlows().length == 27);
		assertTrue(s.getTasks().length == 105);
		assertTrue(s.getSetups().length == 30);
	}
	

	@Test
	public void testApiGetStudyData() throws Exception {
		Study s = client_read_live.studyGet(34, "data");
		assertTrue(s.getDataset().length > 5);
		assertTrue(s.getFlows() == null);
		assertTrue(s.getTasks() == null);
		assertTrue(s.getSetups() == null);
	}
	

	@Test
	public void testApiGetStudyTasks() throws Exception {
		Study s = client_read_live.studyGet(34, "tasks");
		assertTrue(s.getDataset() == null);
		assertTrue(s.getFlows() == null);
		assertTrue(s.getTasks().length > 5);
		assertTrue(s.getSetups() == null);
	}
	
	@Test
	public void testApiGetStudyFlows() throws Exception {
		Study s = client_read_live.studyGet(34, "flows");
		assertTrue(s.getTag().length > 0);
		assertTrue(s.getDataset() == null);
		assertTrue(s.getFlows().length > 5);
		assertTrue(s.getTasks() == null);
		assertTrue(s.getSetups() == null);
	}
	
	@Test
	public void testApiGetStudySetups() throws Exception {
		Study s = client_read_live.studyGet(34, "setups");
		assertTrue(s.getDataset() == null);
		assertTrue(s.getFlows() == null);
		assertTrue(s.getTasks() == null);
		assertTrue(s.getSetups().length > 5);
	}
	

	@Test
	public void testApiGetStudyByAlias() throws Exception {
		Study s = client_read_test.studyGet("OpenML100", "tasks");
		assertTrue(s.getDataset().length > 10);
		assertTrue(s.getFlows() == null);
		assertTrue(s.getTasks().length > 10);
		assertTrue(s.getSetups() == null);
	}
	
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
		Study study = new Study(null, "test", "test", null, taskIds, null);
		String studyXML = xstream.toXML(study);
		File studyDescription = Conversion.stringToTempFile(studyXML, "study", "xml");
		StudyUpload su = client_write_test.studyUpload(studyDescription);
		
		Study studyDownload = client_read_test.studyGet(su.getId());
		assertArrayEquals(taskIds, studyDownload.getTasks());
	}
}
