package org.openml.apiconnector.xml;

import java.io.Serializable;

import org.openml.apiconnector.settings.Constants;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("oml:study_upload")
public class StudyUpload implements Serializable {

	private static final long serialVersionUID = 8578912L;
	
	@XStreamAsAttribute
	@XStreamAlias("xmlns:oml")
	private final String oml = Constants.OPENML_XMLNS;
	
	@XStreamAlias("oml:id")
	private Integer id;
	
	public Integer getId() {
		return id;
	}
}
