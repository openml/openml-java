package apiconnector;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;
import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.io.ApiException;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.xml.DataFeature;
import org.openml.apiconnector.xml.DataQuality;
import org.openml.apiconnector.xml.DataQuality.Quality;
import org.openml.apiconnector.xml.DataSetDescription;
import org.openml.apiconnector.xml.Flow;
import org.openml.apiconnector.xml.Run;
import org.openml.apiconnector.xml.RunEvaluation;
import org.openml.apiconnector.xml.RunTrace;
import org.openml.apiconnector.xml.TaskInputs;
import org.openml.apiconnector.xstream.XstreamXmlMapping;

import com.thoughtworks.xstream.XStream;

public class TestUserTypePrivileges {

	private static final String data_file = "data/iris.arff";
	private static final String url = "https://test.openml.org/";
	private static final XStream xstream = XstreamXmlMapping.getInstance();
	private static final OpenmlConnector client_write = new OpenmlConnector(url,"8baa83ecddfe44b561fd3d92442e3319");
	private static final OpenmlConnector client_read = new OpenmlConnector(url,"c1994bdb7ecb3c6f3c8f3b35f4b47f1f"); // R-TEAM
	private static final Integer EVAL_ID = 2;
	
	@Test(expected=ApiException.class)
	public void testApiDataQualityUpload() throws Exception {
		DataQuality dq = new DataQuality(1, EVAL_ID, new Quality[0]);
		String xml = xstream.toXML(dq);
		File description = Conversion.stringToTempFile(xml, "data-qualities", "xml");
		try {
			client_write.dataQualitiesUpload(description);
		} catch(ApiException e) {
			assertTrue(e.getCode() == 106);
			throw e;
		}
	}
	
	@Test(expected=ApiException.class)
	public void testApiDataFeatureUpload() throws Exception {
		DataFeature df = new DataFeature(1, EVAL_ID, new DataFeature.Feature[0]);
		String xml = xstream.toXML(df);
		File description = Conversion.stringToTempFile(xml, "data-features", "xml");
		try {
			client_write.dataFeaturesUpload(description);
		} catch(ApiException e) {
			assertTrue(e.getCode() == 106);
			throw e;
		}
	}
	
	@Test(expected=ApiException.class)
	public void testApiRunEvaluationUpload() throws Exception {
		RunEvaluation re = new RunEvaluation(1, 1);
		String xml = xstream.toXML(re);
		File description = Conversion.stringToTempFile(xml, "run-evaluation", "xml");
		try {
			client_write.runEvaluate(description);
		} catch(ApiException e) {
			assertTrue(e.getCode() == 106);
			throw e;
		}
	}
	
	@Test(expected=ApiException.class)
	public void testApiRunTraceUpload() throws Exception {
		RunTrace rt = new RunTrace(1);
		String xml = xstream.toXML(rt);
		File description = Conversion.stringToTempFile(xml, "run-trace", "xml");
		try {
			client_write.runTraceUpload(description);
		} catch(ApiException e) {
			assertTrue(e.getCode() == 106);
			throw e;
		}
	}
	
	@Test(expected=ApiException.class)
	public void testApiDataUpload() throws Exception {
		DataSetDescription dsd = new DataSetDescription("test", "Unit test should be deleted", "arff", "class");
		String xml = xstream.toXML(dsd);
		File description = Conversion.stringToTempFile(xml, "test-data", "arff");
		try {
			client_read.dataUpload(description, new File(data_file));
		} catch(ApiException e) {
			assertTrue(e.getCode() == 104);
			throw e;
		}
	}

	@Test(expected=ApiException.class)
	public void testApiDataTag() throws Exception {
		try {
			client_read.dataTag(1, "default_tag");
		} catch(ApiException e) {
			assertTrue(e.getCode() == 104);
			throw e;
		}
	}

	@Test(expected=ApiException.class)
	public void testApiDataUntag() throws Exception {
		try {
			client_read.dataUntag(1, "default_tag");
		} catch(ApiException e) {
			assertTrue(e.getCode() == 104);
			throw e;
		}
	}

	@Test(expected=ApiException.class)
	public void testApiDataDelete() throws Exception {
		try {
			client_read.dataDelete(1);
		} catch(ApiException e) {
			assertTrue(e.getCode() == 104);
			throw e;
		}
	}

	@Test(expected=ApiException.class)
	public void testApiFlowUpload() throws Exception {
		Flow f = new Flow("test2", "weka.classifiers.test.javaunittest", "test", "test should be deleted",
				"english", "UnitTest");
		String xml = xstream.toXML(f);
		File description = Conversion.stringToTempFile(xml, "flow", "xml");
		try {
			client_read.flowUpload(description, null, null);
		} catch(ApiException e) {
			assertTrue(e.getCode() == 104);
			throw e;
		}
	}

	@Test(expected=ApiException.class)
	public void testApiTaskUpload() throws Exception {
		TaskInputs task = new TaskInputs(1, 1, null, null);
		String xml = xstream.toXML(task);
		File description = Conversion.stringToTempFile(xml, "flow", "xml");
		try {
			client_read.taskUpload(description);
		} catch(ApiException e) {
			assertTrue(e.getCode() == 104);
			throw e;
		}
	}

	@Test(expected=ApiException.class)
	public void testApiRunUpload() throws Exception {
		Run run = new Run(1, null, 1, null, null, null);
		String xml = xstream.toXML(run);
		File description = Conversion.stringToTempFile(xml, "flow", "xml");
		try {
			client_read.runUpload(description, null);
		} catch(ApiException e) {
			assertTrue(e.getCode() == 104);
			throw e;
		}
	}

	@Test(expected=ApiException.class)
	public void testApiFlowDelete() throws Exception {
		try {
			client_read.flowDelete(1);
		} catch(ApiException e) {
			assertTrue(e.getCode() == 104);
			throw e;
		}
	}

	@Test(expected=ApiException.class)
	public void testApiTaskDelete() throws Exception {
		try {
			client_read.taskDelete(1);
		} catch(ApiException e) {
			assertTrue(e.getCode() == 104);
			throw e;
		}
	}

	@Test(expected=ApiException.class)
	public void testApiRunDelete() throws Exception {
		try {
			client_read.runDelete(1);
		} catch(ApiException e) {
			assertTrue(e.getCode() == 104);
			throw e;
		}
	}
}
