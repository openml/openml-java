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
package org.openml.apiconnector.xml;

import java.util.HashMap;
import java.util.Map;

import org.openml.apiconnector.settings.Constants;

public class DataQuality {

	private final String oml = Constants.OPENML_XMLNS;
	private Integer did;
	private Integer evaluation_engine_id;
	private String error;
	private Quality[] qualities;

	public DataQuality(Integer did, Integer evaluation_engine_id, Quality[] qualities) {
		this.did = did;
		this.evaluation_engine_id = evaluation_engine_id;
		this.qualities = qualities;
	}

	public DataQuality(Integer did, Integer evaluation_engine_id, String error) {
		this.did = did;
		this.evaluation_engine_id = evaluation_engine_id;
		this.error = error;
	}

	public Integer getDid() {
		return did;
	}
	
	public Integer getEvaluation_engine_id() {
		return evaluation_engine_id;
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
		private Integer feature_index;
		private String value;
		private Integer interval_start;
		private Integer interval_end;

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
