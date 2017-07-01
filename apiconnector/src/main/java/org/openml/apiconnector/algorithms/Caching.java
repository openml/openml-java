package org.openml.apiconnector.algorithms;

import org.openml.apiconnector.io.HttpConnector;
import org.openml.apiconnector.settings.Settings;
import org.openml.apiconnector.xstream.XstreamXmlMapping;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class Caching {

	public static void cache(String s, String type, int identifier, String extension) throws IOException {
		String directoryPath = Settings.CACHE_DIRECTORY + "/" + type;
		File directory = new File(directoryPath);
		directory.mkdirs();
		String name = type + "_" + identifier + "." + extension;
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(directory.getAbsolutePath() + "/" + name)));
		bw.append(s);
		bw.close();
		Conversion.log("OK", "Cache", "Stored to cache: " + type + "/" + name);
	}
	

	public static File cache(URL url, String type, int identifier, String extension) throws IOException, URISyntaxException {
		String directoryPath = Settings.CACHE_DIRECTORY + "/" + type;
		File directory = new File(directoryPath);
		directory.mkdirs();
		String name = type + "_" + identifier + "." + extension;
		File current = HttpConnector.getFileFromUrl(url,directory.getAbsolutePath() + "/" + name, false);
		Conversion.log("OK", "Cache", "Stored to cache: " + type + "/" + name);
		return current;
	}

	public static void cache(Object o, String type, int identifier, String extension) throws IOException {
		String directoryPath = Settings.CACHE_DIRECTORY + "/" + type;
		File directory = new File(directoryPath);
		directory.mkdirs();
		String name = type + "_" + identifier + "." + extension;
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(directory.getAbsolutePath() + "/" + name)));
		bw.append(XstreamXmlMapping.getInstance().toXML(o));
		bw.close();
		Conversion.log("OK", "Cache", "Stored to cache: " + type + "/" + name);
	}

	public static boolean in_cache(String type, int identifier, String extension) {
		File check = new File(Settings.CACHE_DIRECTORY + "/" + type + "/" + type + "_" + identifier + "." + extension);
		return check.exists();
	}

	public static File cached(String type, int identifier, String extension) throws IOException {
		String name = type + "_" + identifier + "." + extension;
		File cached = new File(Settings.CACHE_DIRECTORY + "/" + type + "/" + name);

		if (cached.exists() == false) {
			throw new IOException("Cache file of " + type + " #" + identifier + " not available.");
		} else {
			Conversion.log("OK", "Cache", "Obtained from cache: " + type + "/" + name);
			return cached;
		}
	}

}
