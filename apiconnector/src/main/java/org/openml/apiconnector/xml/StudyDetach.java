package org.openml.apiconnector.xml;

import org.openml.apiconnector.settings.Constants;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("oml:study_detach")
public class StudyDetach {

	@XStreamAsAttribute
	@XStreamAlias("xmlns:oml")
	private final String oml = Constants.OPENML_XMLNS;

	@XStreamAlias("oml:id")
	private Integer id;
	@XStreamAlias("oml:main_entity_type")
	private String main_entity_type;
	
	@XStreamAlias("oml:linked_entities")
	private Integer linkedEntities;

	public Integer getId() {
		return id;
	}

	public String getMain_entity_type() {
		return main_entity_type;
	}

	public Integer getLinkedEntities() {
		return linkedEntities;
	}
}
