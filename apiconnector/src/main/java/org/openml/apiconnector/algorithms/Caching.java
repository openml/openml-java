/*******************************************************************************
 * Copyright (C) 2017, Jan N. van Rijn <j.n.van.rijn@liacs.leidenuniv.nl>
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the copyright holder nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package org.openml.apiconnector.algorithms;

import org.openml.apiconnector.io.ApiException;
import org.openml.apiconnector.io.HttpConnector;
import org.openml.apiconnector.settings.Config;
import org.openml.apiconnector.settings.Settings;
import org.openml.apiconnector.xstream.XstreamXmlMapping;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class Caching {

	public static File cacheFile(URL url, String type, int identifier, String extension) throws IOException, URISyntaxException, ApiException {
		String directoryPath = Settings.CACHE_DIRECTORY + "/" + Config.getChachePrefixFromUrl(url) + type;
		File directory = new File(directoryPath);
		directory.mkdirs();
		String name = type + "_" + identifier + "." + extension;
		File current = HttpConnector.getFileFromUrl(url, directory.getAbsolutePath() + "/" + name, false);
		Conversion.log("OK", "Cache", "Stored to cache: " + type + "/" + name);
		return current;
	}

	public static void cacheXML(URL apiUrl, Object o, String type, int identifier, String extension) throws IOException {
		String directoryPath = Settings.CACHE_DIRECTORY + "/" + Config.getChachePrefixFromUrl(apiUrl) + type;
		File directory = new File(directoryPath);
		directory.mkdirs();
		String name = type + "_" + identifier + "." + extension;
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(directory.getAbsolutePath() + "/" + name)));
		bw.append(XstreamXmlMapping.getInstance().toXML(o));
		bw.close();
		Conversion.log("OK", "Cache", "Stored to cache: " + type + "/" + name);
	}

	public static boolean in_cache(URL apiUrl, String type, int identifier, String extension) throws MalformedURLException {
		File check = new File(Settings.CACHE_DIRECTORY + "/" + Config.getChachePrefixFromUrl(apiUrl) + type + "/" + type + "_" + identifier + "." + extension);
		return check.exists();
	}

	public static File cached(URL apiUrl, String type, int identifier, String extension) throws IOException {
		String name = type + "_" + identifier + "." + extension;
		File cached = new File(Settings.CACHE_DIRECTORY + "/" + Config.getChachePrefixFromUrl(apiUrl) + type + "/" + name);

		if (cached.exists() == false) {
			throw new IOException("Cache file of " + type + " #" + identifier + " not available.");
		} else {
			Conversion.log("OK", "Cache", "Obtained from cache: " + type + "/" + name);
			return cached;
		}
	}

}
