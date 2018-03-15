package org.openml.apiconnector.xstream;

import org.apache.commons.lang3.StringUtils;

import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.basic.DoubleConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;

public class EmptyDoubleConverter extends DoubleConverter {
	// Handles empty Double tags (e.g., <oml:value /> for quality list)

	public boolean canConvert(Class type) {
		return type.equals(Double.class);
	}

	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		String value = reader.getValue();
		if (StringUtils.isEmpty(value)) {
			return null;
		}
		return Double.valueOf(value);
	}
}
