package org.openml.apiconnector.xml;

import org.openml.apiconnector.settings.Constants;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("oml:run_untag")
public class RunUntag {

	@XStreamAsAttribute
	@XStreamAlias("xmlns:oml")
	private final String oml = Constants.OPENML_XMLNS;

	@XStreamAlias("oml:id")
	private Integer id;
	

	@XStreamImplicit(itemFieldName="oml:tag")
	private String[] tag;

	public Integer get_id() {
		return id;
	}
	
	public String[] getTags() {
		return tag;
	}
}