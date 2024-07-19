package org.openml.apiconnector.io;

import java.io.File;
import java.util.Map;

import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.settings.Settings;
import org.openml.apiconnector.xml.DataFeature;
import org.openml.apiconnector.xml.DataQuality;
import org.openml.apiconnector.xml.DataSetDescription;
import org.openml.apiconnector.xml.Flow;
import org.openml.apiconnector.xml.Run;
import org.openml.apiconnector.xml.RunEvaluation;
import org.openml.apiconnector.xml.RunTrace;
import org.openml.apiconnector.xml.Study;
import org.openml.apiconnector.xml.TaskInputs;
import org.openml.apiconnector.xstream.XstreamXmlMapping;

import com.thoughtworks.xstream.XStream;

public class OpenmlConnector extends OpenmlBasicConnector {

	private static final long serialVersionUID = -2063937610741462487L;

	private static final XStream xstream = XstreamXmlMapping.getInstance();
	
	public OpenmlConnector() {
		this.openmlUrl = Settings.BASE_URL;
		this.apiKey = null;
	}

	public OpenmlConnector(String url, String api_key) {
		this.openmlUrl = url;
		this.apiKey = api_key;
	}

	public OpenmlConnector(String url, String api_key, String api_part) {
		this.openmlUrl = url;
		this.apiKey = api_key;
		this.apiPart = api_part;
	}

	public OpenmlConnector(String api_key) {
		this.apiKey = api_key;
	}

	/**
	 * Uploads a new dataset
	 * 
	 * @param dsd     - object describing the dataset
	 * @param dataset - file representing the dataset (arff). optional. In case it
	 *                is not provided, the dataset description should have an URL
	 *                provided
	 * @return The id under which the dataset was uploaded
	 * @throws Exception - Can be: IOException (problem with connection, server),
	 *                   ApiException (contains error code, see OpenML
	 *                   documentation)
	 */
	public int dataUpload(DataSetDescription dsd, File dataset) throws Exception {
		String dsdXML = xstream.toXML(dsd);
		File description = Conversion.stringToTempFile(dsdXML, "dataset", "xml");
		return super.dataUpload(description, dataset).getId();
	}

	/**
	 * Uploads data features of a given dataset (requires admin account)
	 * 
	 * @param features - object describing the features
	 * @return the id of the dataset to which the features were attached
	 * @throws Exception - Can be: IOException (problem with connection, server),
	 *                   ApiException (contains error code, see OpenML
	 *                   documentation)
	 */
	public int dataFeaturesUpload(DataFeature features) throws Exception {
		String featuresXML = xstream.toXML(features);
		File description = Conversion.stringToTempFile(featuresXML, "features", "xml");
		return super.dataFeaturesUpload(description).getDid();
	}

	/**
	 * Uploads data qualities of a given dataset (requires admin account)
	 * 
	 * @param qualities - object describing the qualities (or meta-features)
	 * @return id of the dataset to which the qualities were attached
	 * @throws Exception - Can be: IOException (problem with connection, server),
	 *                   ApiException (contains error code, see OpenML
	 *                   documentation)
	 */
	public int dataQualitiesUpload(DataQuality qualities) throws Exception {
		String featuresXML = xstream.toXML(qualities);
		File description = Conversion.stringToTempFile(featuresXML, "features", "xml");
		return super.dataQualitiesUpload(description).getDid();
	}
	

	/**
	 * Uploads a flow
	 * 
	 * @param flow - An XML file describing the implementation. See documentation
	 *            at openml.org.
	 * @return the id of the uploaded flow
	 * @throws Exception - Can be: IOException (problem with connection, server),
	 *                   ApiException (contains error code, see OpenML
	 *                   documentation)
	 */
	public int flowUpload(Flow flow) throws Exception {
		String flowXML = xstream.toXML(flow);
		File description = Conversion.stringToTempFile(flowXML, "flow", "xml");
		return super.flowUpload(description, null, null).getId();
	}
	

	/**
	 * Uploads the evaluation of a run (admin rights required)
	 * 
	 * @param runEvaluation - description file (complying to xsd)
	 * @return the id of the run that was evaluated
	 * @throws Exception - Can be: IOException (problem with connection, server),
	 *                   ApiException (contains error code, see OpenML
	 *                   documentation)
	 */
	public int runEvaluate(RunEvaluation runEvaluation) throws Exception {
		String evalXML = xstream.toXML(runEvaluation);
		File description = Conversion.stringToTempFile(evalXML, "run", "xml");
		return super.runEvaluate(description).getRun_id();
	}
	
	/**
	 * Uploads a run
	 * 
	 * @param run
	 *            - An XML file describing the run. See documentation at
	 *            openml.org.
	 * @param outputFiles
	 *            - A Map&gt;String,File&lt; containing all relevant output files. Key
	 *            "predictions" usually contains the predictions that were
	 *            generated by this run.
	 * @return the id of the uploaded run
	 * @throws Exception - Can be: IOException (problem with connection, server),
	 *                   ApiException (contains error code, see OpenML
	 *                   documentation)
	 */
	public int runUpload(Run run, Map<String, File> outputFiles) throws Exception {
		String runXML = xstream.toXML(run);
		File description = Conversion.stringToTempFile(runXML, "run", "xml");
		return super.runUpload(description, outputFiles).getRun_id();
	}
	

	/**
	 * Uploads trace results of a run. (admin rights required)
	 * 
	 * @param trace - the trace description
	 * @return the id of the run to which the trace was attached
	 * @throws Exception - Can be: IOException (problem with connection, server),
	 *                   ApiException (contains error code, see OpenML
	 *                   documentation)
	 */
	public int runTraceUpload(RunTrace trace) throws Exception {
		String traceXML = xstream.toXML(trace);
		File description = Conversion.stringToTempFile(traceXML, "trace", "xml");
		return super.runTraceUpload(description).getRun_id();
	}

	/**
	 * Uploads study
	 * 
	 * @param study - object describing the study
	 * @return id of the study
	 * @throws Exception - Can be: IOException (problem with connection, server),
	 *                   ApiException (contains error code, see OpenML
	 *                   documentation)
	 */
	public int studyUpload(Study study) throws Exception {
		String studyXML = xstream.toXML(study);
		File description = Conversion.stringToTempFile(studyXML, "study", "xml");
		return super.studyUpload(description).getId();
	}

	/**
	 * Uploads a task
	 * 
	 * @param task - task description. 
	 * @return id of the just up[loaded tasl
	 * @throws Exception - Can be: IOException (problem with connection, server),
	 *                   ApiException (contains error code, see OpenML
	 *                   documentation)
	 */
	public int taskUpload(TaskInputs task) throws Exception {
		String taskXML = xstream.toXML(task);
		File description = Conversion.stringToTempFile(taskXML, "task", "xml");
		return super.taskUpload(description).getId();
	}
}
