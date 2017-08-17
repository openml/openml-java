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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.openml.apiconnector.settings.Constants;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("oml:setup_parameters")
public class SetupParameters {
	
	@XStreamAsAttribute
	@XStreamAlias("xmlns:oml")
	private final String oml = Constants.OPENML_XMLNS;

	@XStreamAlias("oml:flow_id")
	private Integer flow_id;
	
	@XStreamImplicit
	@XStreamAlias("oml:parameter")
	private Parameter[] parameters;
	
	public Integer getFlow_id() {
		return flow_id;
	}
	
	public Parameter[] getParameters() {
		return parameters;
	}
	
	public Map<String, Parameter> getParametersAsMap() {
		Map<String, Parameter> res = new HashMap<String, Parameter>();
		for (Parameter p : parameters) {
			res.put(p.getFull_name(), p);
		}
		return res;
	}

	@XStreamAlias("oml:parameter")
	public class Parameter implements Serializable {
		private static final long serialVersionUID = -4380189808506822529L;

		@XStreamAlias("oml:full_name")
		private String full_name;
		@XStreamAlias("oml:parameter_name")
		private String parameter_name;
		@XStreamAlias("oml:data_type")
		private String data_type;
		@XStreamAlias("oml:default_value")
		private String default_value;
		@XStreamAlias("oml:value")
		private String value;
		
		public Parameter(String full_name, String parameter_name,
				String data_type, String default_value, String value) {
			super();
			this.full_name = full_name;
			this.parameter_name = parameter_name;
			this.data_type = data_type;
			this.default_value = default_value;
			this.value = value;
		}
		
		public String getFull_name() {
			return full_name;
		}
		public String getParameter_name() {
			return parameter_name;
		}
		public String getData_type() {
			return data_type;
		}
		public String getDefault_value() {
			return default_value;
		}
		public String getValue() {
			return value;
		}
	}
}
