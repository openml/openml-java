package org.openml.moa.algorithm;

import java.util.HashMap;
import java.util.Map;

import org.openml.apiconnector.xml.DataFeature;
import org.openml.apiconnector.xml.DataFeature.Feature;

import com.yahoo.labs.samoa.instances.Instance;

import moa.core.Example;

public class GeneralHelpers {
	
	// TODO: will be added to new version of api connector (1.0.13)
	public static Map<String, Feature> featuresToFeatureMap(DataFeature df) {
		Map<String, Feature> fm = new HashMap<String, Feature>();
		for (Feature f : df.getFeatures()) {
			fm.put(f.getName(), f);
		}
		return fm;
	}
	
	public static String exampleToString(Example example) {
		StringBuilder sb = new StringBuilder();
		Instance current = ((Instance) example.getData());
		
		for (int i = 0; i < current.numAttributes(); ++i) {
			if (current.attribute(i).isNominal()) {
				int value = (int) current.value(i);
				String strValue = (value >= 0) ? '"' + current.attribute(i).value(value) + '"' : "?";
				sb.append(", \"" + current.attribute(i).name() + "\": " + strValue + "");
			} else {
				sb.append(", \"" + current.attribute(i).name() + "\": \"" + current.value(i) + "\"");
			}
			
		}
		
		return sb.toString().substring(2);
	}
}
