package utils;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;

import org.junit.Test;
import org.openml.apiconnector.io.HttpCacheController;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.settings.Settings;
import org.openml.apiconnector.xml.DataSetDescription;
import org.openml.apiconnector.xml.Task;

import testbase.BaseTestFramework;

public class TestCacheFunctions extends BaseTestFramework {
	
	private static void utilDatasetCache(OpenmlConnector openml, int did) throws Exception {
		Settings.CACHE_ALLOWED = true;
		Settings.CACHE_DIRECTORY = System.getProperty("user.home") + "/.openml_test/cache";
		
		String[] suffix = {
				"datasets/" + did + "/description.xml",
				"datasets/" + did + "/features.xml",
				"datasets/" + did + "/dataset.arff",
		};
		// first remove potential cache files, to ensure that this procedure placed them
		for (String s : suffix) {
			File toRemove = HttpCacheController.getCacheLocation(new URL(openml.getApiUrl()), s);
			assertTrue(toRemove.getAbsolutePath().startsWith(Settings.CACHE_DIRECTORY));
			if (toRemove.exists()) {
				toRemove.delete();
			}
		}
		
		DataSetDescription dsd = openml.dataGet(did);
		openml.dataFeatures(did);
		openml.datasetGet(dsd);
		
		for (String s : suffix) {
			File checkExists = HttpCacheController.getCacheLocation(new URL(openml.getApiUrl()), s);
			assertTrue(checkExists.exists());
		}

		// redo the calls, to check all cache operations work fine
		openml.dataGet(did);
		openml.dataFeatures(did);
		openml.datasetGet(dsd);
	}
	
	private static void utilTaskCache(OpenmlConnector openml, int taskId) throws Exception {
		Settings.CACHE_ALLOWED = true;
		Settings.CACHE_DIRECTORY = System.getProperty("user.home") + "/.openml_test/cache";
		
		String[] suffix = {
				"tasks/" + taskId + "/task.xml",
				"tasks/" + taskId + "/datasplits.arff",
		};
		// first remove potential cache files, to ensure that this procedure placed them
		for (String s : suffix) {
			File toRemove = HttpCacheController.getCacheLocation(new URL(openml.getApiUrl()), s);
			assertTrue(toRemove.getAbsolutePath().startsWith(Settings.CACHE_DIRECTORY));
			if (toRemove.exists()) {
				toRemove.delete();
			}
		}
		
		Task task = openml.taskGet(taskId);
		openml.taskSplitsGet(task);
		
		for (String s : suffix) {
			File checkExists = HttpCacheController.getCacheLocation(new URL(openml.getApiUrl()), s);
			assertTrue(checkExists.exists());
		}
		
		// redo the calls, to check all cache operations work fine
		openml.taskGet(taskId);
		openml.taskSplitsGet(task);
	}
	
	@Test
	public void testDatasetLive() throws Exception {
		utilDatasetCache(client_read_live, 61);
	}
	
	@Test
	public void testDatasetTest() throws Exception {
		utilDatasetCache(client_read_test, 5);
	}
	
	@Test
	public void testTaskLive() throws Exception {
		utilTaskCache(client_read_live, 59);
	}
	
	@Test
	public void testTaskTest() throws Exception {
		utilTaskCache(client_read_test, 115);
	}
}
