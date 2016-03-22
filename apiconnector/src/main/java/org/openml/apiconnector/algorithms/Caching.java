package org.openml.apiconnector.algorithms;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.openml.apiconnector.io.HttpConnector;
import org.openml.apiconnector.settings.Settings;

public class Caching {

	public static void cache(String s, String type, int identifier) throws IOException {
		String directoryPath = Settings.CACHE_DIRECTORY + "/" + type;
		File directory = new File(directoryPath);
		directory.mkdirs();
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(directory.getAbsolutePath() + "/" + identifier)));
		bw.append(s);
		bw.close();
	}

	public static void cache(Object o, String type, int identifier) throws IOException {
		String directoryPath = Settings.CACHE_DIRECTORY + "/" + type;
		File directory = new File(directoryPath);
		directory.mkdirs();
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(directory.getAbsolutePath() + "/" + identifier)));
		bw.append(HttpConnector.xstreamClient.toXML(o));
		bw.close();
	}

	public static boolean in_cache(String type, int identifier) {
		File check = new File(Settings.CACHE_DIRECTORY + "/" + type + "/" + identifier);
		return check.exists();
	}

	public static File cached(String type, int identifier) throws IOException {
		File cached = new File(Settings.CACHE_DIRECTORY + "/" + type + "/" + identifier);

		if (cached.exists() == false) {
			throw new IOException("Cache file of " + type + " #" + identifier + " not available.");
		} else {
			return cached;
		}
	}

}
