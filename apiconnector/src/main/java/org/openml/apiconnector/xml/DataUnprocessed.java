package org.openml.apiconnector.xml;

import org.openml.apiconnector.settings.Constants;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("oml:data_unprocessed")
public class DataUnprocessed {

	@XStreamAsAttribute
	@XStreamAlias("xmlns:oml")
	private final String oml = Constants.OPENML_XMLNS;
	
	@XStreamImplicit(itemFieldName="oml:dataset")
	private org.openml.apiconnector.xml.Data.DataSet[] dataset;
	
	public org.openml.apiconnector.xml.Data.DataSet[] getDatasets() {
		return dataset;
	}
}
