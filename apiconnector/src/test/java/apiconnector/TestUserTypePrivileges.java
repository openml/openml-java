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
import org.openml.apiconnector.xml.RunEvaluation;
import org.openml.apiconnector.xstream.XstreamXmlMapping;

import com.thoughtworks.xstream.XStream;

public class TestUserTypePrivileges {

	private static final String data_file = "data/iris.arff";
	private static final String url = "https://test.openml.org/";
	private static final XStream xstream = XstreamXmlMapping.getInstance();
	private static final OpenmlConnector client_write = new OpenmlConnector(url,"8baa83ecddfe44b561fd3d92442e3319");
	private static final OpenmlConnector client_read = new OpenmlConnector(url,"c1994bdb7ecb3c6f3c8f3b35f4b47f1f"); // R-TEAM
	
	@Test(expected=ApiException.class)
	public void testApiDataQualityUpload() throws Exception {
		DataQuality dq = new DataQuality(1, new Quality[0]);
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
		DataFeature df = new DataFeature(1, new DataFeature.Feature[0]);
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
}
