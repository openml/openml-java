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

	@XStreamImplicit
	@XStreamAlias("oml:parameter")
	private Parameter[] parameters;
	
	public Parameter[] getParameters() {
		return parameters;
	}
	
	public Map<String, String> getParametersAsMap() {
		Map<String, String> res = new HashMap<String, String>();
		for (Parameter p : parameters) {
			res.put(p.getFull_name(), p.getValue());
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
