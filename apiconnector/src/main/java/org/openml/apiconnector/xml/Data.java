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
import java.util.TreeMap;

import org.openml.apiconnector.settings.Constants;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.converters.extended.ToAttributedValueConverter;

@XStreamAlias("oml:data")
public class Data {

	@XStreamAsAttribute
	@XStreamAlias("xmlns:oml")
	private final String oml = Constants.OPENML_XMLNS;

	@XStreamImplicit
	@XStreamAlias("oml:dataset")
	private DataSet[] data;
	
	public String getOml() {
		return oml;
	}
	public DataSet[] getData() {
		return data;
	}

	@XStreamAlias("oml:dataset")
	public static class DataSet {
		@XStreamAlias("oml:did")
		private int did;

		@XStreamAlias("oml:name")
        private String name;

		@XStreamAlias("oml:version")
		private String version;

		@XStreamAlias("oml:status")
		private String status;

		@XStreamAlias("oml:format")
		private String format;
		
		@XStreamAlias("oml:file_id")
		private Integer file_id;

		@XStreamImplicit
		@XStreamAlias("oml:quality")
		private Quality[] qualities;
		
		public int getDid() {
			return did;
		}
		
        public String getName() {
            return name;
        }
		
		public String getVersion() {
			return version;
		}
		
		public String getFormat() {
			return format;
		}
		
		public String getStatus() {
			return status;
		}
		
		public Integer getFileId() {
			return file_id;
		}
		
		public Quality[] getQualities() {
			return qualities;
		}
		
		public Map<String, String> getQualityMap() {
			Map<String, String> result = new TreeMap<String, String>();
			if (getQualities() != null) {
				for (Quality q : getQualities()) {
					result.put(q.getName(), q.getValue());
				}
			}
			return result;
		}

		@XStreamAlias("oml:quality")
		@XStreamConverter(value=ToAttributedValueConverter.class, strings={"value"})
		public static class Quality {

			@XStreamAsAttribute
			@XStreamAlias("name")
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
