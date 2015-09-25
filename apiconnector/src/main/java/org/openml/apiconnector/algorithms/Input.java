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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;


public class Input {

	public static InputStreamReader getURL( URL url ) throws IOException {
		URLConnection urlConnection = url.openConnection();
		urlConnection.setConnectTimeout(1000);
		urlConnection.setReadTimeout(30000);
		return new InputStreamReader( urlConnection.getInputStream() );
	}
	
	// same as above, with difference that it can make use of cached files. 
	/*public static Reader getURL( Integer file_id, OpenmlConnector openml ) throws Exception {
		if( Caching.in_cache("file", file_id)) {
			return new FileReader( Caching.cached("file", file_id) );
		} else {
			URL url = openml.getOpenmlFileUrl(file_id, "predictions");
			URLConnection urlConnection = url.openConnection();
			urlConnection.setConnectTimeout(1000);
			urlConnection.setReadTimeout(30000);
			return new InputStreamReader( urlConnection.getInputStream() );
		}
	}*/
	
	public static InputStreamReader getFile( String filename ) throws IOException {
		return new InputStreamReader( new FileInputStream( new File( filename ) ) );
	}
	
	public static String filename( String sUrl ) {
		if(sUrl.substring(sUrl.lastIndexOf('/') + 1).contains(".") == false ) {
			return sUrl.substring( sUrl.lastIndexOf('/') + 1 );
		}
		return sUrl.substring( sUrl.lastIndexOf('/') + 1, sUrl.lastIndexOf('.') );
	}
}
