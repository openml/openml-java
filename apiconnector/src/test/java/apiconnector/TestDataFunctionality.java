/*
 *  OpenmlApiConnector - Java integration of the OpenML Web API
 *  Copyright (C) 2014 
 *  @author Jan N. van Rijn (j.n.van.rijn@liacs.leidenuniv.nl)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *  
 */
package apiconnector;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.Test;
import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.io.ApiException;
import org.openml.apiconnector.io.HttpConnector;
import org.openml.apiconnector.xml.Data;
import org.openml.apiconnector.xml.DataDelete;
import org.openml.apiconnector.xml.DataFeature;
import org.openml.apiconnector.xml.DataQuality;
import org.openml.apiconnector.xml.DataQualityList;
import org.openml.apiconnector.xml.DataReset;
import org.openml.apiconnector.xml.DataSetDescription;
import org.openml.apiconnector.xml.DataTag;
import org.openml.apiconnector.xml.DataUntag;
import org.openml.apiconnector.xml.TaskDelete;
import org.openml.apiconnector.xml.TaskTag;
import org.openml.apiconnector.xml.TaskUntag;
import org.openml.apiconnector.xml.TaskInputs;
import org.openml.apiconnector.xml.Data.DataSet;
import org.openml.apiconnector.xml.TaskInputs.Input;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import testbase.BaseTestFramework;

public class TestDataFunctionality extends BaseTestFramework {
	public static final String data_file = "data/iris.arff";
	private static final int probe = 61;
	private static final String tag = "junittest";
	
	@Test
	public void testApiDataDownload() throws Exception {
		DataSetDescription dsd = client_read_test.dataGet(probe);
		DataFeature features = client_read_test.dataFeatures(probe);
		DataQuality qualities = client_read_test.dataQualities(probe, null);
		
		File tempDsd = Conversion.stringToTempFile(xstream.toXML(dsd), "data", "xml");
		File tempXsd = client_read_test.getXSD("openml.data.upload");
		
		String url = client_read_test.getApiUrl() + "data/" + probe;
		File rawFile = HttpConnector.getTempFileFromUrl(new URL(url + "?api_key=" + client_read_test.getApiKey()), "xml");
		String raw = Conversion.fileToString(rawFile);
		
		assertTrue(Conversion.validateXML(tempDsd, tempXsd));
		
		
		String dsdFromOpenml = toPrettyString(raw, 0);
		String dsdFromConnector = toPrettyString(xstream.toXML(dsd), 0);
		
		if (!dsdFromOpenml.equals(dsdFromConnector)) {
			System.out.println("===== OBTAINED FROM OPENML: =====");
			System.out.println(dsdFromOpenml);
			System.out.println("===== LOCALLY AVILABLE: =====");
			System.out.println(dsdFromConnector);
		}
		
		assertTrue(dsdFromOpenml.equals(dsdFromConnector));
		
		// very easy checks, should all pass
		assertTrue(dsd.getId() == probe);
		assertTrue(features.getFeatures().length > 0);
		assertTrue(qualities.getQualities().length > 0);
	}
	
	@Test
	public void testApiDataUnprocessed() throws Exception {
		client_admin_test.dataUnprocessed(2, "normal");
		client_admin_test.dataUnprocessed(2, "random");
	}

	@Test
	public void testApiUploadDownload() throws Exception {
		DataSetDescription dsd = new DataSetDescription("test", "Unit test should be deleted", "arff", "class");
		File toUpload = new File(data_file);
		int dataId = client_write_test.dataUpload(dsd, toUpload);
		client_admin_test.dataStatusUpdate(dataId, "active");
		DataReset dr = client_write_test.dataReset(dataId);
		assertTrue(dr.get_id() == dataId);
		DataTag dt = client_write_test.dataTag(dataId, tag);
		assertTrue(Arrays.asList(dt.getTags()).contains(tag));
		
		DataSetDescription dsd_downloaded = client_read_test.dataGet(dataId);
		client_read_test.datasetGet(dsd_downloaded);
		
		// create task upon it (clustering task)
		Input estimation_procedure = new Input("estimation_procedure", "17");
		Input data_set = new Input("source_data", "" + dataId);
		Input measure = new Input("evaluation_measures", "predictive_accuracy");
		Input[] inputs = {estimation_procedure, data_set, measure};
		int taskId = client_write_test.taskUpload(new TaskInputs(null, 5, inputs, null));
		
		TaskTag tt = client_write_test.taskTag(taskId, tag);
		assertTrue(Arrays.asList(tt.getTags()).contains(tag));
		TaskUntag tu = client_write_test.taskUntag(taskId, tag);
		assertNull(tu.getTags());
		
		try {
			client_write_test.dataDelete(dataId);
			// this SHOULD fail, we should not be allowed to delete data that contains tasks.
			fail("Problem with API. Dataset ("+dataId+") was deleted while it contains a task ("+taskId+"). ");
		} catch(ApiException ae) {}
		
		
		// delete the task
		TaskDelete td = client_write_test.taskDelete(taskId);
		assertEquals((Integer) taskId, td.get_id());
		
		// and delete the data
		DataUntag du = client_write_test.dataUntag(dataId, tag);
		assertNull(du.getTags());
		
		DataDelete dd = client_write_test.dataDelete(dataId);
		assertEquals((Integer) dataId, dd.get_id());
	}
	
	@Test
	public void testDataQualitiesWithNullValues() throws Exception {
		DataQuality dq = client_read_live.dataQualities(3, null);
		
		// check if test is actually up to date (otherwise we should use other dataset that contains null values)
		Collection<Double> qualityValues = dq.getQualitiesMap().values();
		assertTrue(qualityValues.contains(null));
		
		// test if converting back doesn't break anything
		xstream.toXML(dq);
	}
	
	@Test
	public void testApiUploadFromUrl() throws Exception {
		String dataUrl = "http://storm.cis.fordham.edu/~gweiss/data-mining/weka-data/cpu.arff";
		
		DataSetDescription dsd = new DataSetDescription("anneal", "Unit test should be deleted", "arff", dataUrl, "class");
		
		int dataId = client_write_test.dataUpload(dsd, null);
		DataTag dt = client_write_test.dataTag(dataId, tag);
		assertTrue(Arrays.asList(dt.getTags()).contains(tag));
		
		// Download dataset and check md5 thingy
		DataSetDescription dsd_downloaded = client_read_test.dataGet(dataId);
		client_read_test.datasetGet(dsd_downloaded);

		HttpConnector.getTempFileFromUrl(new URL(dataUrl), "xml");
	}

	@Test
	public void testApiDataList() throws Exception {
		Map<String, String> filters = new TreeMap<String, String>();
		filters.put("limit", "10");
		
		Data datasets = client_read_test.dataList(filters);
		assertTrue(datasets.getData() != null);
		
		assertTrue(datasets.getData().length == 10);
		for (DataSet dataset : datasets.getData()) {
			assertTrue("No qualities for dataset " + dataset.getDid(), dataset.getQualities() != null);
			assertTrue("Not enough qualities for dataset " + dataset.getDid(), dataset.getQualities().length > 5);
		}
	}

	@Test
	public void testApiAdditional() throws Exception {
		DataQualityList dql = client_read_test.dataQualitiesList();
		assertTrue(dql.getQualities().length > 50);
	}
	
	public void verifyCsvDataset(DataSet dataset) throws Exception {
		int numInstances = (int) Double.parseDouble(dataset.getQualityMap().get("NumberOfInstances"));
		int numFeatures = (int) Double.parseDouble(dataset.getQualityMap().get("NumberOfFeatures"));
		
		if (!dataset.getFormat().toLowerCase().equals("arff")) {
			return;
		}
		
		String fullUrl = url_test + "data/get_csv/" + dataset.getFileId() + "/" + dataset.getName() + ".csv";
		System.out.println(fullUrl);
		final URL url = new URL(fullUrl);
		final Reader reader = new InputStreamReader(url.openStream(), "UTF-8");
		final CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT);
		try {
			List<CSVRecord> records = parser.getRecords();
			int foundRecords = (int) parser.getRecordNumber() - 1; // -1 because of csv header
			assertEquals(numInstances, foundRecords);
			int foundColumns = records.get(0).size();
			assertEquals(numFeatures, foundColumns);
		} finally {
		    parser.close();
		    reader.close();
		}
	}
	
	@Test
	public void testGetDataAsCsvRandom() throws Exception {
		Map<String,String> filters = new TreeMap<String, String>();
		filters.put("limit", "100");
		filters.put("number_instances", "10..10000");
		
		DataSet[] all = client_read_test.dataList(filters).getData();
		
		for (int i = 0; i < 10; ++i) {
			DataSet current = all[i];
			verifyCsvDataset(current);
		}
	}
	
	@Test
	public void testGetDataAsCsvSingle() throws Exception {
		Map<String,String> filters = new TreeMap<String, String>();
		filters.put("data_id", "87");
		
		DataSet[] all = client_read_test.dataList(filters).getData();
		for (DataSet current : all) {
			verifyCsvDataset(current);
		}
	}
	
	// function that formats xml consistently, making it easy to compare them. 
	public static String toPrettyString(String xml, int indent) throws Exception {
        // Turn xml string into a document
        Document document = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(new InputSource(new ByteArrayInputStream(xml.getBytes("utf-8"))));

        // Remove whitespaces outside tags
        XPath xPath = XPathFactory.newInstance().newXPath();
        NodeList nodeList = (NodeList) xPath.evaluate("//text()[normalize-space()='']",
                                                      document,
                                                      XPathConstants.NODESET);

        for (int i = 0; i < nodeList.getLength(); ++i) {
            Node node = nodeList.item(i);
            node.getParentNode().removeChild(node);
        }

        // Setup pretty print options
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setAttribute("indent-number", indent);
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        // Return pretty print xml string
        StringWriter stringWriter = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
	        return stringWriter.toString();
	}
}
