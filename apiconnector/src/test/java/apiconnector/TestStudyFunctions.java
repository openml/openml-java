package apiconnector;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;
import org.openml.apiconnector.xml.Study;
import org.openml.apiconnector.xml.StudyAttach;
import org.openml.apiconnector.xml.StudyDetach;
import org.openml.apiconnector.xml.StudyList;

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
		Integer[] taskIdsInitial = {1, 2, 3};
		Integer[] taskIdsAdditional = {4, 5, 6};
		Study study = new Study(null, "test", "test", null, taskIdsInitial, null);
		int studyId = client_write_test.studyUpload(study);
		
		Study studyDownload = client_read_test.studyGet(studyId);
		assertArrayEquals(taskIdsInitial, studyDownload.getTasks());
		
		StudyAttach sa = client_write_test.studyAttach(studyId, Arrays.asList(taskIdsAdditional));
		assertTrue(taskIdsInitial.length + taskIdsAdditional.length == sa.getLinkedEntities());

		StudyDetach sd = client_write_test.studyDetach(studyId, Arrays.asList(taskIdsInitial));
		assertTrue(taskIdsAdditional.length == sd.getLinkedEntities());

		Study studyDownload2 = client_read_test.studyGet(studyId);
		assertArrayEquals(taskIdsAdditional, studyDownload2.getTasks());
	}

	@Test
	public void studyList() throws Exception {
		Map<String, String> filters = new TreeMap<String, String>();
		filters.put("status", "all");
		filters.put("limit", "20");
		StudyList sl = client_read_test.studyList(filters);
		assertTrue(sl.getStudies().length > 5);

		for (Study s : sl.getStudies()) {
			assertTrue(s.getId() > 0);
		}
	}
}
