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

public class Settings {
	
	/**
	 * The webserver to which request are done. ends with tailing slash. 
	 * (Api suffix will be added by ApiConnector)
	 */
	public static final String BASE_URL = "http://www.openml.org/";
	/**
	 * The directory where cache files are saved. 
	 */
	public static String CACHE_DIRECTORY = Constants.OPENML_DIRECTORY + "/cache/";
	/**
	 * Whether caching is allowed. Keep value to true.
	 */
	public static boolean CACHE_ALLOWED = true;
	/**
	 * Use on servers. In this case no download / upload operations will be used, just cache. 
	 */
	public static boolean LOCAL_OPERATIONS = false;
	
}
