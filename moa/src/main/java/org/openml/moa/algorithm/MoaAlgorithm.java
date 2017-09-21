package org.openml.moa.algorithm;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.xml.Flow;
import org.openml.apiconnector.xml.FlowExists;
import org.openml.apiconnector.xml.Run;
import org.openml.apiconnector.xml.Run.Parameter_setting;
import org.openml.apiconnector.xml.UploadFlow;
import org.openml.apiconnector.xstream.XstreamXmlMapping;
import org.openml.moa.settings.MoaSettings;

import com.github.javacliparser.FileOption;
import com.github.javacliparser.FlagOption;
import com.github.javacliparser.ListOption;
import com.github.javacliparser.Option;

import moa.classifiers.Classifier;
import moa.options.ClassOption;

public class MoaAlgorithm {

	public static int getFlowId(Flow implementation, Classifier classifier, OpenmlConnector apiconnector)
			throws Exception {
		try {
			// First ask OpenML whether this implementation already exists
			FlowExists result = apiconnector.flowExists(implementation.getName(), implementation.getExternal_version());

			if (result.exists()) {
				return result.getId();
			}
		} catch (Exception e) { /* Suppress Exception since it is totally OK. */ }

		// It does not exist. Create it.
		String xml = XstreamXmlMapping.getInstance().toXML(implementation);
		// System.err.println(xml);
		File implementationFile = Conversion.stringToTempFile(xml, implementation.getName(), "xml");
		
		UploadFlow ui = apiconnector.flowUpload(implementationFile, null, null);
		return ui.getId();
	}

	public static ArrayList<Run.Parameter_setting> getOptions(Flow flow, Option[] options) {
		ArrayList<Run.Parameter_setting> result = new ArrayList<Run.Parameter_setting>();
		for (Option option : options) {
			if (option instanceof FlagOption) {
				FlagOption o = (FlagOption) option;
				result.add(new Parameter_setting(flow.getId(), o.getCLIChar() + "", o.isSet() ? "true" : "false"));
			} else if (option instanceof FileOption) {
				// ignore file options
				continue;
			} else if (option instanceof ListOption) {
				// TODO: do something better for subclassifiers
				ListOption o = (ListOption) option;
				List<String> values = new ArrayList<String>();
				for (int i = 0; i < o.getList().length; i++) {
					values.add(o.getList()[i].getValueAsCLIString());
				}
				Collections.sort(values);

				String cliString = "";
				for (String value : values) {
					cliString += "," + value;
				}

				result.add(new Parameter_setting(flow.getId(), option.getCLIChar() + "", cliString.substring(1)));
			} else if (option instanceof ClassOption) {
				ClassOption o = (ClassOption) option;
				if (o.getRequiredType().isAssignableFrom(Classifier.class)) {
					try {
						Classifier subclassifier = (Classifier) ClassOption.cliStringToObject(o.getValueAsCLIString(),
								o.getRequiredType(), null);
						Flow subimplementation = create(subclassifier);

						result.addAll(getOptions(flow.getComponentByName(subimplementation.getName()),
								subclassifier.getOptions().getOptionArray()));
						result.add(new Parameter_setting(flow.getId(), option.getCLIChar() + "",
								subclassifier.getClass().getName()));
					} catch (Exception e) {
						result.add(new Parameter_setting(flow.getId(), option.getCLIChar() + "",
								option.getValueAsCLIString()));
						e.printStackTrace();
					}
				} else {
					result.add(new Parameter_setting(flow.getId(), option.getCLIChar() + "",
							option.getValueAsCLIString()));
				}
			} /*else if (option instanceof WEKAClassOption) {
				try {
					String[] params = Utils.splitOptions(option.getValueAsCLIString());
					Flow subimplementation = wekaSubimplementation((WEKAClassOption) option);
					result.addAll(WekaAlgorithm.getParameterSetting(params,
							flow.getComponentByName(subimplementation.getName())));
					result.add(new Parameter_setting(flow.getId(), option.getCLIChar() + "", params[0]));
				} catch (Exception e) {
					result.add(new Parameter_setting(flow.getId(), option.getCLIChar() + "",
							option.getValueAsCLIString()));
					e.printStackTrace();
				}
			} */ else {
				result.add(new Parameter_setting(flow.getId(), option.getCLIChar() + "", option.getValueAsCLIString()));
			}
		}

		return result;
	}

	public static Flow create(Classifier classifier) {
		String classPath = classifier.getClass().getName();
		String classifierName = classPath.substring(classPath.lastIndexOf('.') + 1);
		String name = "moa." + classifierName;
		String version = "1.0"; // TODO: MOA does not support retrieval of version?
		String description = "Moa implementation of " + classifierName;
		String language = "English";
		String dependencies = MoaSettings.MOA_VERSION; 
		
		Flow i = new Flow(name, classPath, dependencies + "_" + version, description, language, dependencies);
		for (Option option : classifier.getOptions().getOptionArray()) {
			if (option instanceof FlagOption) {
				FlagOption fo = (FlagOption) option;
				i.addParameter(fo.getCLIChar() + "", "flag", "false", fo.getName() + ": " + fo.getPurpose());
			} else if (option instanceof ClassOption) {
				ClassOption co = (ClassOption) option;
				i.addParameter(co.getCLIChar() + "", "baselearner", co.getDefaultCLIString(),
						co.getName() + ": " + co.getPurpose());

				if (co.getRequiredType().isAssignableFrom(Classifier.class)) {
					try {
						Flow subimplementation = create((Classifier) ClassOption
								.cliStringToObject(co.getValueAsCLIString(), co.getRequiredType(), null));
						i.addComponent(co.getCLIChar() + "", subimplementation);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} /* else if (option instanceof WEKAClassOption) {
				WEKAClassOption wco = (WEKAClassOption) option;
				i.addParameter(wco.getCLIChar() + "", "baselearner", wco.getDefaultCLIString(),
						wco.getName() + ": " + wco.getPurpose());

				try {
					i.addComponent(wco.getCLIChar() + "", wekaSubimplementation(wco));
				} catch (Exception e) {
					e.printStackTrace();
				}
			} */ else {
				i.addParameter(option.getCLIChar() + "", "option", option.getDefaultCLIString(),
						option.getName() + ": " + option.getPurpose());
			}
		}

		return i;
	}
}