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

import org.openml.apiconnector.settings.Constants;

public class Data {

	private final String oml = Constants.OPENML_XMLNS;
	private DataSet[] data;
	
	public String getOml() {
		return oml;
	}
	public DataSet[] getData() {
		return data;
	}
	
	public static class DataSet {
		private int did;
		private String status;
		private Quality[] qualities;
		
		public int getDid() {
			return did;
		}
		
		public String getStatus() {
			return status;
		}
		
		public Quality[] getQualities() {
			return qualities;
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

			@Override
			public String toString() {
				return name;
			}
		}
	}
}
