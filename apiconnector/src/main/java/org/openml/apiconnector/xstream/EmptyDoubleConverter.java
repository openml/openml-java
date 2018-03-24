package org.openml.apiconnector.xstream;

import org.apache.commons.lang3.StringUtils;

import com.thoughtworks.xstream.converters.basic.DoubleConverter;

public class EmptyDoubleConverter extends DoubleConverter {
	// Handles empty Double tags (e.g., <oml:value /> for quality list)

	public boolean canConvert(Class type) {
		return type.equals(Double.class);
	}
	
	public Object fromString(String str) {
		if (StringUtils.isEmpty(str)) {
			return null;
		}
		return Double.valueOf(str);
	}
}
