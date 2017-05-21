package org.openml.apiconnector.xml;

import org.openml.apiconnector.settings.Constants;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("oml:evaluation_request")
public class EvaluationRequest {

	@XStreamAsAttribute
	@XStreamAlias("xmlns:oml")
	private final String oml = Constants.OPENML_XMLNS;
	
	@XStreamImplicit(itemFieldName="oml:run")
	private org.openml.apiconnector.xml.RunList.Run[] run;
	
	public org.openml.apiconnector.xml.RunList.Run[] getRuns() {
		return run;
	}
}
