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
package org.openml.apiconnector.io;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;
import java.util.zip.DataFormatException;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.openml.apiconnector.algorithms.DateParser;
import org.openml.apiconnector.algorithms.Hashing;
import org.openml.apiconnector.settings.Constants;
import org.openml.apiconnector.xml.Authenticate;

class ApiSessionHash implements Serializable {
	
	private static final long serialVersionUID = 7831245113631L;
	
	private final String server;
	private String username;
	private String password;
	private String sessionHash;
	private long validUntil;
	
	/**
	 * Creates a new session hash. 
	 */
	public ApiSessionHash( String server ) {
		this.server = server;
		sessionHash = null;
		username = null;
	}
	
	/**
	 * @return true if the API session hash is still valid, false otherwise
	 */
	public boolean isValid() {
		Date utilDate = new Date();
		return validUntil > utilDate.getTime() + Constants.DEFAULT_TIME_MARGIN;
	}
	
	/**
	 * @param username - Username to authenticate with
	 * @param password - Password to authenticate with
	 * @return true if authentication was successful; false otherwise.
	 * @throws ParseException
	 */
	public boolean set( String username, String password ) {
		this.username = username;
		this.password = password;
		try {
			update();
			return true;
		} catch( Exception e ) {
			return false;
		}
	}
	
	/**
	 * Executes authentication request.
	 * 
	 * @throws Exception - On authentication failure
	 */
	public void update() throws Exception {
		Authenticate auth = openmlAuthenticate(username, password);
		this.validUntil = DateParser.mysqlDateToTimeStamp(auth.getValidUntil(),auth.getTimezone());
		this.sessionHash = auth.getSessionHash();
		return;
	}

	/**
	 * @return The username that was set
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @return The password that was set
	 */
	public String getSessionHash() throws Exception {
		if( isValid() == false ) {
			update();
		}
		return sessionHash;
	}
	
	/**
	 * Checks given credentials whether these can be used to login on the server
	 * @param username
	 * @param password
	 * @return whether the user can login with the given credentials. 
	 */
	public boolean checkCredentials(String username, String password) {
		try {
			openmlAuthenticate(username, password);
			return true;
		} catch( Exception e ) {
			return false;
		}
	}
	
	/**
	 * Checks stored credentials whether these can be used to login on the server
	 * @return whether the user can login with the given credentials. 
	 */
	public boolean checkCredentials() {
		try {
			openmlAuthenticate(username, password);
			return true;
		} catch( Exception e ) {
			return false;
		}
	}
	
	/**
	 * Authenticates the current user, based on internally stored credentials. 
	 * 
	 * @return Authenticate - An object containing the Api Session Hash 
	 * (which can be used to authenticate without username / password)
	 * @throws Exception - Can be: API Error (see documentation at openml.org), 
	 * server down, etc.
	 */
	public Authenticate openmlAuthenticate() throws Exception {
		return openmlAuthenticate(username, password);
	}
	
	/**
	 * Authenticates the current user. 
	 * 
	 * @param username - The username that is used for authentication
	 * @param password - The password used for authentication
	 * @return Authenticate - An object containing the Api Session Hash 
	 * (which can be used to authenticate without username / password)
	 * @throws Exception - Can be: API Error (see documentation at openml.org), 
	 * server down, etc.
	 */
	private Authenticate openmlAuthenticate(String username, String password) throws Exception {
		MultipartEntity params = new MultipartEntity();
		params.addPart("username",new StringBody(username));
		params.addPart("password",new StringBody(Hashing.md5(password)));
		
		Object apiResult = HttpConnector.doApiRequest(server,"openml.authenticate", "", params, null);
        if( apiResult instanceof Authenticate){
        	return (Authenticate) apiResult;
        } else {
        	throw new DataFormatException("Casting Api Object to Authenticate");
        }
	}
}
