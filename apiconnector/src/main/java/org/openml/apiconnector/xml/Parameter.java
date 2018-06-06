package org.openml.apiconnector.xml;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("oml:parameter")
public class Parameter {
	
	@XStreamAlias("oml:id")
	private Integer id;
	
	@XStreamAlias("oml:flow_id")
	private Integer flow_id;
	
	@XStreamAlias("oml:full_name")
	private String full_name;
	
	@XStreamAlias("oml:name")
	private String name;
	
	@XStreamAlias("oml:data_type")
	private String data_type;
	
	@XStreamAlias("oml:default_value")
	private String default_value;
	
	@XStreamAlias("oml:value")
	private String value;
	
	@XStreamAlias("oml:description")
	private String description;
	

	public Parameter(String name, String data_type, String default_value, String description) {
		this.name = name;
		this.data_type = data_type;
		this.default_value = default_value;
		this.description = description;
	}
	
	public Parameter(Integer id, Integer flow_id, String full_name, String name, String data_type, String default_value, String value) {
		this.id = id;
		this.flow_id = flow_id;
		this.full_name = full_name;
		this.name = name;
		this.data_type = data_type;
		this.default_value = default_value;
		this.value = value;
	}
	public Integer getId() {
		return id;
	}
	
	public Integer getFlow_id() {
		return flow_id;
	}
	
	public String getFull_name() {
		return full_name;
	}
	
	public String getName() {
		return name;
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
	
	public String getDescription() {
		return description;
	}
}
