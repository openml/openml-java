package org.openml.apiconnector.xml;

import java.io.Serializable;

import org.openml.apiconnector.settings.Constants;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

public abstract class OpenmlApiResponse implements Serializable {
	
	private static final long serialVersionUID = 3347172372685632488L;
	
	@XStreamAsAttribute
	@XStreamAlias("xmlns:oml")
	private final String oml = Constants.OPENML_XMLNS;
	
	public String getOml() {
		return oml;
	}
}
