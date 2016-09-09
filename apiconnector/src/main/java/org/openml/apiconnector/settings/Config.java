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
package org.openml.apiconnector.settings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.openml.apiconnector.algorithms.Conversion;

/**
 * A Class that loads a config file with username/password and server information.
 * Highly recommended to use config file and this class when executing experiments
 * on a server. 
 * 
 * @author J. N. van Rijn <j.n.van.rijn@liacs.leidenuniv.nl>
 */
public class Config implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private boolean loaded = false;
	private HashMap<String, String> config;
	
	/**
	 * @throws IOException - Could not load config file
	 */
	public Config() {
		try {
			String configfile = Constants.OPENML_DIRECTORY + "/openml.conf";
			load(new File( configfile ) );
		} catch( IOException ioe ) {
			Conversion.log("Warning", "Load Config", "Could not locate default config file.");
		}
	}
	
	public Config( String config ) {
		process( Arrays.asList( config.split(";") ) );
	}
	
	public void updateStaticSettings() {
		if( get("cache_allowed") != null ) {
			if( get("cache_allowed").equals("false") ) {
				Settings.CACHE_ALLOWED = false;
			}
		}
		if( get("cache_directory") != null ) {
			Settings.CACHE_DIRECTORY = config.get("cache_directory");
		}
	}
	
	/**
	 * @param f The location (absolute or relative) where the config
	 * file can be found. 
	 * @throws IOException - Could not load config file
	 */
	private void load( File f ) throws IOException {
		
		BufferedReader br = new BufferedReader(new FileReader(f));
		List<String> lines = new ArrayList<String>();
		while( br.ready() ) {
			lines.add( br.readLine() );
		}
		br.close();
		process( lines );
	}
	
	private void process( List<String> lines ) {
		config = new HashMap<String, String>();
		// default server, can be overridden. 
		config.put("server", Settings.BASE_URL );
		
		for( String line : lines ) {
			String[] l = line.split("=");
			if( l.length == 2 ) {
				config.put( l[0].trim(), l[1].trim() );
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
		if( result == null ) {
			return new String[0];
		} else {
			String[] tags = result.split(",");
			for( int i = 0; i < tags.length; ++i ) {
				tags[i] = tags[i].trim();
			}
			return tags;
		}
	}
	
	/**
	 * @param key - Item name to be loaded from the config file. 
	 * @return Field "key", if specified in the config file. null otherwise
	 */
	public String get( String key ) {
		if( loaded ) {
			if( config.containsKey( key ) ) {
				return config.get( key );
			}
		}
		return null;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for( String s : config.keySet() ) {
			if( s.equals("password") ) {
				sb.append(s + "=" + config.get(s).length() + "chars\n");
			} else {
				sb.append(s + "=" + config.get(s)+"\n");
			}
		}
		return sb.toString();
	}
}
