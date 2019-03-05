package org.openml.apiconnector.xml;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("oml:study_upload")
public class StudyUpload extends OpenmlApiResponse {

	private static final long serialVersionUID = 8578912L;
	
	@XStreamAlias("oml:id")
	private Integer id;
	
	public Integer getId() {
		return id;
	}
}
