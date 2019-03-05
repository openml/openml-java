package org.openml.apiconnector.xml;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("oml:study_attach")
public class StudyAttach extends OpenmlApiResponse {
	
	private static final long serialVersionUID = 2684468069381731009L;
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
