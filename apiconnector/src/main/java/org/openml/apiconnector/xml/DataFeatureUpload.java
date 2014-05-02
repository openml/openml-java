package org.openml.apiconnector.xml;

import org.openml.apiconnector.settings.Constants;

public class DataFeatureUpload {

	private final String oml = Constants.OPENML_XMLNS;
	private Integer did;
	
	public String getOml() {
		return oml;
	}
	
	public int getDid() {
		return did;
	}
	
}