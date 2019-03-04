package examples;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.xml.RunList;
import org.openml.apiconnector.xml.Study;
import org.openml.apiconnector.xml.StudyList;
import org.openml.apiconnector.xml.Tasks;

import testbase.BaseTestFramework;

public class TestPaperOpenMLBenchmarkSuites extends BaseTestFramework {

	// alias
	protected static final OpenmlConnector openml = client_write_test;
	
	@Test
    public void listBenchmarksuites() throws Exception {
        Map<String, String> filters = new TreeMap<String, String>();
		filters.put("status", "all");
		filters.put("main_entity_type", "task");
		filters.put("limit", "20");
		StudyList list = openml.studyList(filters);
		
		assertTrue(list.getStudies().length > 0);
    }
	
	@Test
    public void attachDetachStudy()  throws Exception {
        // find 250 tasks that we are interested in, e.g., the tasks that have between
	    // 100 and 10000 instances and between 4 and 20 attributes
		Map<String, String> filtersOrig = new TreeMap<String, String>();
	    filtersOrig.put("number_instances", "100..10000");
	    filtersOrig.put("number_features", "4..20");
	    filtersOrig.put("limit", "250");
	    Tasks tasksOrig = client_write_test.taskList(filtersOrig);
	    
	    // create the study
	    Study study = new Study(null, "test", "test", null, tasksOrig.getTaskIds(), null);
	    int studyId = openml.studyUpload(study);
	    
	    // until the benchmark suite is activated, we can also add some more tasks. Search for the letter dataset:
	    Map<String, String> filtersAdd = new TreeMap<String, String>();
	    filtersAdd.put("name", "letter");
	    filtersAdd.put("limit", "1");
	    Tasks tasksAdd = openml.taskList(filtersOrig);
	    openml.studyAttach(studyId, Arrays.asList(tasksAdd.getTaskIds()));
	    
	    // or even remove these again
	    openml.studyDetach(studyId, Arrays.asList(tasksAdd.getTaskIds()));
	    
	    // download the study
	    Study studyDownloaded = openml.studyGet(studyId);
	    assertArrayEquals(tasksOrig.getTaskIds(), studyDownloaded.getTasks());
    }
	
	@Test
	public void downloadResultsBenchmarkSuite()  throws Exception {
		Study benchmarkSuite = openml.studyGet("OpenML100", "tasks");
		
		Map<String, List<Integer>> filters = new TreeMap<String, List<Integer>>();
		filters.put("task", Arrays.asList(benchmarkSuite.getTasks()));
		RunList rl = openml.runList(filters, 200, null);
		
	    assertTrue(rl.getRuns().length > 0); 
    }
}
