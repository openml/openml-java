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

import java.util.Arrays;

import org.openml.apiconnector.settings.Constants;
import org.apache.commons.lang3.ArrayUtils;

public class Flow {
	private final String oml = Constants.OPENML_XMLNS;
	
	private Integer id;
	private String fullName;
	private Integer uploader;
	private String name;
	private String custom_name;
	private String class_name;
	private String version;
	private String external_version;
	private String description;
	private String[] creator;
	private String[] contributor;
	private String upload_date;
	private String licence;
	private String language;
	private String full_description;
	private String installation_notes;
	private String dependencies;
	private String implement;
	private Parameter[] parameter;
	private Component[] component;
	private String[] tag;
	private String source_url;
	private String binary_url;
	private String source_format;
	private String binary_format;
	private String source_md5;
	private String binary_md5;

	public Flow(String name, String class_name, String external_version, String description, String language, String dependencies ) {
		this.name = name;
		this.class_name = class_name;
		this.external_version = external_version;
		this.description = description;
		this.language = language;
		this.dependencies = dependencies;
	}

	public Flow(String name, String custom_name, String class_name, 
			String external_version, String description,
			String[] creator, String[] contributor, String licence,
			String language, String full_description,
			String installation_notes, String dependencies, String[] tag) {
		super();
		this.name = name;
		this.custom_name = custom_name;
		this.class_name = class_name;
		this.external_version = external_version;
		this.description = description;
		this.creator = creator;
		this.contributor = contributor;
		this.licence = licence;
		this.language = language;
		this.full_description = full_description;
		this.installation_notes = installation_notes;
		this.dependencies = dependencies;
		this.tag = tag;
	}
	
	public String getOml() {
		return oml;
	}

	public Integer getId() {
		return id;
	}

	public String getFullName() {
		return fullName;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getCustom_name() {
		return custom_name;
	}

	public void setCustom_name(String custom_name) {
		this.custom_name = custom_name;
	}

	public String getClass_name() {
		return class_name;
	}

	public void setClass_name(String class_name) {
		this.class_name = class_name;
	}

	public String getExternal_version() {
		return external_version;
	}
	
	public void setExternal_version(String external_version) {
		this.external_version = external_version;
	}

	public Integer getUploader() {
		return uploader;
	}

	public String getName() {
		return name;
	}
	
	public String getLastName() {
		return name.substring( name.lastIndexOf('.') + 1 );
	}

	public String getVersion() {
		return version;
	}

	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	public String[] getCreator() {
		return creator;
	}

	public String[] getContributor() {
		return contributor;
	}

	public String getUpload_date() {
		return upload_date;
	}

	public String getLicence() {
		return licence;
	}

	public String getLanguage() {
		return language;
	}

	public String getFull_description() {
		return full_description;
	}

	public String getInstallation_notes() {
		return installation_notes;
	}

	public String getDependencies() {
		return dependencies;
	}

	public String getImplement() {
		return implement;
	}

	public Parameter[] getParameter() {
		return parameter;
	}

	public Component[] getComponent() {
		return component;
	}
	
	public String[] getTag() {
		return tag;
	}

	public String getSource_url() {
		return source_url;
	}

	public String getBinary_url() {
		return binary_url;
	}

	public String getSource_format() {
		return source_format;
	}

	public String getBinary_format() {
		return binary_format;
	}

	public String getSource_md5() {
		return source_md5;
	}

	public String getBinary_md5() {
		return binary_md5;
	}
	
	public void addTag( String new_tag ) {
		// check if tag is not already present
		if( tag != null ) {
			if( Arrays.asList(tag).contains(new_tag) == true ) {
				return;
			}
		}
		tag = ArrayUtils.addAll( tag, new_tag );
	}

	public void addParameter(String name, String data_type, String default_value, String description) {
		Parameter p = new Parameter(name, data_type, default_value, description);
		this.parameter = ArrayUtils.addAll(this.parameter, p);
	}
	
	public void addParameter(Parameter p) {
		this.parameter = ArrayUtils.addAll(this.parameter, p);
	}
	
	public Flow getComponentByName( String name ) throws Exception {
		for( Component c : getComponent() ) {
			if( c.getImplementation().getName().equals( name ) )
				return c.getImplementation();
		}
		throw new Exception("Subimplementation not present.");
	}
	
	public void addComponent( String identifier, Flow implementation, boolean updateName ) {
		Component c = new Component( identifier, implementation );
		this.component = ArrayUtils.addAll( this.component, c );
		if (updateName) {
			this.name += "_" + implementation.getLastName();
		}
	}
	
	public void addComponent( String identifier, Flow implementation ) {
		addComponent(identifier, implementation, true);
	}
	
	public boolean parameter_exists( String name ) {
		if( parameter != null ) {
			for( Parameter p : parameter ) {
				if( p.getName().equals( name ) ) {
					return true;
				}
			}
		}
		return false;
	}
	
	public Flow getSubImplementation( String identifier ) throws Exception {
		if( component != null ) {
			for( Component p : component ) {
				if( p.identifier.equals(identifier)) {
					return p.getImplementation();
				}
			}
		}
		throw new Exception("Component with identifier " + identifier + " not found. ");
	}

	public static class Bibliographical_reference {
		private String citation;
		private String url;

		public Bibliographical_reference(String citation, String url) {
			this.citation = citation;
			this.url = url;
		}

		public String getCitation() {
			return citation;
		}

		public String getUrl() {
			return url;
		}
	}

	public static class Parameter {
		private String name;
		private String data_type;
		private String default_value;
		private String description;
		
		public Parameter(String name, String data_type, String default_value,
				String description) {
			this.name = name;
			this.data_type = data_type;
			this.default_value = default_value;
			this.description = description;
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

		public String getDescription() {
			return description;
		}
	}
	
	public static class Component {
		private String identifier;
		private Flow flow;
		
		public Component( String identifier, Flow flow ) {
			this.identifier = identifier;
			this.flow = flow;
		}

		public String getIdentifier() {
			return identifier;
		}

		public Flow getImplementation() {
			return flow;
		}
	}
}