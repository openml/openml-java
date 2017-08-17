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
package org.openml.apiconnector.settings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.openml.apiconnector.algorithms.Conversion;

/**
 * A Class that loads a config file with username/password and server
 * information. Highly recommended to use config file and this class when
 * executing experiments on a server.
 * 
 * @author J. N. van Rijn &gt;j.n.van.rijn@liacs.leidenuniv.nl&lt;
 */
public class Config implements Serializable {

	private static final long serialVersionUID = 1L;
	private boolean loaded = false;
	private HashMap<String, String> config;
	
	public Config() {
		try {
			String configfile = Constants.OPENML_DIRECTORY + "/openml.conf";
			load(new File(configfile));
		} catch (IOException ioe) {
			Conversion.log("Warning", "Load Config", "Could not locate default config file.");
		}
	}

	public Config(String config) {
		process(Arrays.asList(config.split(";")));
	}

	public void updateStaticSettings() {
		if (get("cache_allowed") != null) {
			if (get("cache_allowed").equals("false")) {
				Settings.CACHE_ALLOWED = false;
			}
		}
		if (get("cache_directory") != null) {
			Settings.CACHE_DIRECTORY = config.get("cache_directory");
		}
	}

	/**
	 * @param f
	 *            The location (absolute or relative) where the config file can
	 *            be found.
	 * @throws IOException
	 *             - Could not load config file
	 */
	private void load(File f) throws IOException {

		BufferedReader br = new BufferedReader(new FileReader(f));
		List<String> lines = new ArrayList<String>();
		while (br.ready()) {
			lines.add(br.readLine());
		}
		br.close();
		process(lines);
	}

	private void process(List<String> lines) {
		config = new HashMap<String, String>();
		// default server, can be overridden.
		config.put("server", Settings.BASE_URL);

		for (String line : lines) {
			String[] l = line.split("=");
			if (l.length == 2) {
				config.put(l[0].trim(), l[1].trim());
			}
		}
		loaded = true;
	}

	/**
	 * @return The username specified in the config file
	 */
	public String getApiKey() {
		return get("api_key");
	}

	/**
	 * @return The server address specified in the config file
	 */
	public String getServer() {
		return get("server");
	}

	public String[] getTags() {
		String result = get("tags");
		if (result == null) {
			return new String[0];
		} else {
			String[] tags = result.split(",");
			for (int i = 0; i < tags.length; ++i) {
				tags[i] = tags[i].trim();
			}
			return tags;
		}
	}
	
	public String getChachePrefixFromUrl() throws MalformedURLException {
		return getChachePrefixFromUrl(new URL(getServer()));
	}
	
	public static String getChachePrefixFromUrl(URL server) throws MalformedURLException {
		String[] host = server.getHost().split("\\.");
		String result = "";
		for (int i = 0; i < host.length; ++i) {
			result = host[i] + "/" + result;
		}
		return result;
	}

	/**
	 * @param key
	 *            - Item name to be loaded from the config file.
	 * @return Field "key", if specified in the config file. null otherwise
	 */
	public String get(String key) {
		if (loaded) {
			if (config.containsKey(key)) {
				return config.get(key);
			}
		}
		return null;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (String s : config.keySet()) {
			if (s.equals("password")) {
				sb.append(s + "=" + config.get(s).length() + "chars; ");
			} else {
				sb.append(s + "=" + config.get(s) + "; ");
			}
		}
		return sb.toString();
	}
}
