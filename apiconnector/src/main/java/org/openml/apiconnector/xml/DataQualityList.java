package org.openml.apiconnector.xml;

import org.openml.apiconnector.settings.Constants;

public class DataQualityList {

	private final String oml = Constants.OPENML_XMLNS;
	private String[] quality;
	
	public String getOml() {
		return oml;
	}
	public String[] getQualities() {
		return quality;
	}
}
