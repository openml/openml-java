package utils;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.client.HttpResponseException;
import org.junit.Ignore;
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
		Settings.CACHE_DIRECTORY = System.getProperty("user.home") + "/.openml_" + subdomain_test + "/cache";
		
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
	
	private static void utilTaskCache(OpenmlConnector openml, List<Pair<String, String>> expected, int taskId) throws Exception {
		Settings.CACHE_ALLOWED = true;
		Settings.CACHE_DIRECTORY = System.getProperty("user.home") + "/.openml_" + subdomain_test + "/cache";
		// first remove potential cache files, to ensure that this procedure placed them
		for (Pair<String, String> pair : expected) {
			File toRemove = HttpCacheController.getCacheLocation(new URL(pair.getRight()), pair.getLeft());
			assertTrue(toRemove.getAbsolutePath().startsWith(Settings.CACHE_DIRECTORY));
			toRemove.delete();
		}
		
		Task task = openml.taskGet(taskId);
		openml.taskSplitsGet(task);

		for (Pair<String, String> pair : expected) {
			File checkExists = HttpCacheController.getCacheLocation(new URL(pair.getRight()), pair.getLeft());
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
		List<Pair<String, String>> expected = Arrays.asList(
				Pair.of("tasks/59/task.xml", "https://www.openml.org/api/v1/task/59"),
				Pair.of("tasks/59/datasplits.arff", "https://api.openml.org/api_splits/get/59/Task_59_splits.arff")
		);
		utilTaskCache(client_read_live, expected, 59);
	}
	
	@Test
	public void testTaskTest() throws Exception {
		List<Pair<String, String>> expected = Arrays.asList(
				Pair.of("tasks/115/task.xml", url_test + "api/v1/task/115"),
				Pair.of("tasks/115/datasplits.arff", url_test + "/api_splits/get/115/Task_115_splits.arff")
		);
		utilTaskCache(client_read_test, expected, 115);
	}
	
	@Test(expected=HttpResponseException.class)
	public void testCacheRejectsOnError() throws Exception {
		Integer illegalTaskId = 999999;
		URL illigalSplits = new URL(url_test + "/api_splits/get/" + illegalTaskId + "/Task_" + illegalTaskId + "_splits.arff");
		String suffix = "tasks/" + illegalTaskId + "/datasplits.arff";
		HttpCacheController.getCachedFileFromUrl(illigalSplits, suffix);
	}
}
