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
		if(Caching.in_cache(url, type, identifier, extension)) {
			File file = Caching.cached(url, type, identifier, extension);
			String clientMd5 = Hashing.md5(file);
			if(serverMd5 == null || serverMd5.equals("NotApplicable") || clientMd5.equals( serverMd5.trim())) {
				return file;
			} else {
				Conversion.log("WARNING", "ARFF Cache", type + " " + identifier + " hash and cache not identical: \n- Client: " + clientMd5 + "\n- Server: " + serverMd5);
			}
		}
		
		File dataset;
		if( Settings.CACHE_ALLOWED ) {
			dataset = Caching.cacheFile(url, type, identifier, extension);
		} else {
			dataset = Conversion.stringToTempFile(HttpConnector.getStringFromUrl(url, false), type + "_" + identifier + "", extension );
		}
		String hash = Hashing.md5(dataset);
        if (serverMd5 == null || serverMd5.equals("NotApplicable") || hash.equals(serverMd5.trim())) {
            return dataset;
        } else {
            throw new IOException("Md5 hashes do not correspond. File: " + dataset.getAbsolutePath() + ", hash: " + hash);
        }
	}
	
	/**
	 * @param line - Line from an Arff File. 
	 * @return true if this is the line declaring the start of the data field (@DATA); false otherwise
	 */
	public static boolean isDataDeclaration( String line ) {
		if( line.length() == 0 ) return false;
		if( line.charAt( 0 ) == '%' ) return false; // comment; 
		if( line.toUpperCase().contains( "@DATA" ) )
			return true;
		return false;
	}
	
	/**
	 * @param line - Line from an Arff File. 
	 * @return true if this is the line declaring an attribute (@ATTRIBUTE); false otherwise
	 */
	public static boolean isAttributeDeclaration( String line ) {
		if( line.length() == 0 ) return false;
		if( line.charAt( 0 ) == '%' ) return false; // comment; 
		if( line.toUpperCase().contains( "@ATTRIBUTE" ) ) {
			return true;
		}
		return false;
	}
	
	/**
	 * @param attributeLine - Line declaring an attribute from an Arff File. 
	 * @return The name of the attribute
	 * @throws Exception
	 */
	public static String getAttributeName( String attributeLine ) throws Exception {
		if( isAttributeDeclaration( attributeLine ) == false )
			throw new Exception("Not a valid attribute. ");
		
		String[] words = attributeLine.trim().split("\\s+");
		if( words.length < 2 ) throw new Exception("Not a valid attribute.");
		
		if( words[1].charAt( 0 ) == '\'' && words[1].charAt( words[1].length()-1 ) == '\'' ) {
			return words[1].substring( 1, words[1].length() - 1 );
		}
		
		return words[1];
	}
	
	/**
	 * @param attributeLine - Line declaring a nominal attribute from an Arff File. 
	 * @return The values of this nominal attribute
	 * @throws Exception
	 */
	public static String[] getNominalValues( String attributeLine ) throws Exception {
		if( isAttributeDeclaration( attributeLine ) == false )
			throw new Exception("Not a valid attribute. ");
		
		int idxStartBracket = attributeLine.indexOf('{');
		int idxEndBracket   = attributeLine.indexOf('}');
		
		if( idxStartBracket == -1 || idxEndBracket == -1 ) {
			throw new Exception("Not a nominal attribute. ");
		}
		if( idxStartBracket > idxEndBracket ) {
			throw new Exception("Not a legal nominal attribute. ");
		}
		String[] classes = attributeLine.substring( idxStartBracket + 1, idxEndBracket ).split(",");
		for( int i = 0; i < classes.length; ++i ) {
			classes[i] = classes[i].trim();
			if( classes[i].charAt( 0 ) == '\'' && classes[i].charAt( classes[i].length()-1 ) == '\'' ) {
				classes[i] = classes[i].substring( 1, classes[i].length() - 1 );
			}
		}
		return classes;
	}
	
}
