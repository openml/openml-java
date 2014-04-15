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
package org.openml.apiconnector.xml;

import java.util.Map;

import org.openml.apiconnector.settings.Constants;

public class DataQuality {
	
	private final String oml = Constants.OPENML_XMLNS;
	private Integer did;
	private Quality[] qualities;
	
	public DataQuality( Integer did, Quality[] qualities ) {
		this.did = did;
		this.qualities = qualities;
	}
	
	public DataQuality( Integer did, Map<String, Double> qualities ) {
		this.did = did;
		this.qualities = new Quality[qualities.size()];
		int iQualityNr = 0;
		for( String quality : qualities.keySet() ) {
			this.qualities[iQualityNr] = new Quality( quality, qualities.get( quality ) + "" ); 
			++iQualityNr;
		}
	}
	
	public Integer getDid() {
		return did;
	}
	
	public Quality[] getQualities() {
		return qualities;
	}
	
	public String[] getQualityNames() {
		String[] result = new String[qualities.length];
		for( int i = 0; i < qualities.length; ++i ) {
			result[i] = qualities[i].getName();
		}
		return result;
	}
	
	public String getOml() {
		return oml;
	}
	
	public static class Quality {
		private String name;
		private String value;
		
		public Quality( String name, String value ) {
			this.name = name;
			this.value = value;
		}
		
		public String getName() {
			return name;
		}
		
		public String getValue() {
			return value;
		}
	}
}