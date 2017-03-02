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

import java.util.HashMap;
import java.util.Map;

import org.openml.apiconnector.settings.Constants;

public class DataQuality {

	private final String oml = Constants.OPENML_XMLNS;
	private Integer did;
	private String error;
	private Quality[] qualities;

	public DataQuality(Integer did, Quality[] qualities) {
		this.did = did;
		this.qualities = qualities;
	}

	public DataQuality(Integer did, String error) {
		this.did = did;
		this.error = error;
	}

	public Integer getDid() {
		return did;
	}

	public String getError() {
		return error;
	}

	public Quality[] getQualities() {
		return qualities;
	}

	public String[] getQualityNames() {
		String[] result = new String[qualities.length];
		for (int i = 0; i < qualities.length; ++i) {
			result[i] = qualities[i].getName();
		}
		return result;
	}
	
	public Map<String,String> getQualitiesMap() {
		Map<String,String> qm = new HashMap<String, String>();
		for (Quality q : qualities) {
			qm.put(q.getName(), q.getValue());
		}
		return qm;
	}

	public String getOml() {
		return oml;
	}

	public static class Quality {
		private String name;
		private String value;
		private Integer interval_start;
		private Integer interval_end;
		private Integer feature_index;

		public Quality(String name, String value) {
			this.name = name;
			this.value = value;
			this.feature_index = null;
		}

		public Quality(String name, String value, Integer intervat_start, Integer interval_end, Integer index) {
			this.name = name;
			this.value = value;
			this.interval_start = intervat_start;
			this.interval_end = interval_end;
			this.feature_index = index;
		}

		public String getName() {
			return name;
		}

		public String getValue() {
			return value;
		}

		public Integer getInterval_start() {
			return interval_start;
		}

		public Integer getInterval_end() {
			return interval_end;
		}

		public Integer getFeature_index() {
			return feature_index;
		}

		@Override
		public String toString() {
			return name + ":" + value;
		}
	}
}