/*
 *  OpenmlApiConnector - Java integration of the OpenML Web API
 *  Copyright (C) 2014 
 *  @author Jan N. van Rijn (j.n.van.rijn@liacs.leidenuniv.nl)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *  
 */
package org.openml.apiconnector.algorithms;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.openml.apiconnector.io.OpenmlConnector;
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
	public static File downloadAndCache(String type, int identifier, String extension, String url, String serverMd5) throws IOException {
		if(Caching.in_cache(type, identifier, extension)) {
			File file = Caching.cached(type, identifier, extension);
			String clientMd5 = Hashing.md5(file);
			if(serverMd5 == null || clientMd5.equals( serverMd5.trim())) {
				return file;
			} else {
				Conversion.log("WARNING", "ARFF Cache", type + " " + identifier + " hash and cache not identical: \n- Client: " + clientMd5 + "\n- Server: " + serverMd5);
			}
		}
		
		if( Settings.LOCAL_OPERATIONS ) {
			throw new IOException("Cache file of " + type + " #" + identifier + " not available, and only local operations are allowed. ");
		}
		
		File dataset;
		if( Settings.CACHE_ALLOWED ) {
			dataset = Caching.cache(new URL(url), type, identifier, extension);
		} else {
			dataset = Conversion.stringToTempFile(OpenmlConnector.getStringFromUrl( url ), type + "_" + identifier + "", extension );
		}
		return dataset;
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
