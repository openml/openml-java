package org.openml.apiconnector.xstream;

import org.apache.commons.lang3.StringUtils;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class DoubleConverter implements Converter {
	// Handles empty Double tags (e.g., <oml:value /> for quality list)

	public boolean canConvert(Class type) {
		return type.equals(Double.class);
	}

	@Override
	public void marshal(Object Obj, HierarchicalStreamWriter arg1, MarshallingContext arg2) {

	}

	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		String value = reader.getValue();
		if (StringUtils.isEmpty(value)) {
			return null;
		}
		return Double.valueOf(value);
	}
}
