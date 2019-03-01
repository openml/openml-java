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

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.openml.apiconnector.io.HttpConnector;
import org.openml.apiconnector.settings.Settings;

public class ArffHelper {
	
	/**
	 * Looks whether a specified file exists in the cache directory. Downloads it if it does not exists. 
	 * 
	 * @param type - Either splits or dataset
	 * @param identifier - The name of the arff file to look up (or store)
	 * @param url - Url to obtain it from
	 * @param serverMd5 - Md5 hash of the file to be downloaded. Used for checking the local version, if it exists.
	 * @return A file pointer to the specified arff file.
	 * @throws IOException
	 */
	public static File downloadAdCache(String type, int identifier, String extension, URL url, String serverMd5) throws Exception {
		if(Caching.in_cache(url, type, identifier, extension) && Settings.CACHE_ALLOWED) {
			File file = Caching.cached(url, type, identifier, extension);
			String clientMd5 = Hashing.md5(file);
			if(serverMd5 == null || serverMd5.equals("NotApplicable") || clientMd5.equals( serverMd5.trim())) {
				return file;
			} else {
				Conversion.log("WARNING", "ARFF Cache", type + " " + identifier + " hash and cache not identical: \n- Client: " + clientMd5 + "\n- Server: " + serverMd5);
			}
		}
		
		File dataset;
		if(Settings.CACHE_ALLOWED) {
			dataset = Caching.cacheFile(url, type, identifier, extension);
		} else {
			dataset = HttpConnector.getFileFromUrl(url, extension, false);
		}
		String hash = Hashing.md5(dataset);
        if (serverMd5 == null || serverMd5.equals("NotApplicable") || hash.equals(serverMd5.trim())) {
            return dataset;
        } else {
            throw new IOException("Md5 hashes do not correspond. File: " + dataset.getAbsolutePath() + ", hash: " + hash);
        }
	}
}
