package org.openml.apiconnector.io;

import java.io.File;
import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.xml.Study;
import org.openml.apiconnector.xml.StudyUpload;
import org.openml.apiconnector.xstream.XstreamXmlMapping;

import com.thoughtworks.xstream.XStream;

public class OpenmlConnector extends OpenmlBasicConnector {
	
	private static final long serialVersionUID = -2063937610741462487L;
	
	private static final XStream xstream = XstreamXmlMapping.getInstance();

	protected StudyUpload studyUpload(Study study) throws Exception {
		String studyXML = xstream.toXML(study);
		File description = Conversion.stringToTempFile(studyXML, "study", "xml");
		return super.studyUpload(description);
	}
}
