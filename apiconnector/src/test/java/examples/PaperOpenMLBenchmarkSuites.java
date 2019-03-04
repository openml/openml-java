package examples;

import static org.junit.Assert.assertArrayEquals;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.xml.Study;
import org.openml.apiconnector.xml.Tasks;

import testbase.TestBase;

public class PaperOpenMLBenchmarkSuites extends TestBase {

	// alias
	protected static final OpenmlConnector openml = client_write_test;
	
	@Test
	public void uploadStudy() throws Exception {
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
}
