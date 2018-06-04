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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Random;
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
import org.junit.Ignore;
import org.junit.Test;
import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.algorithms.Hashing;
import org.openml.apiconnector.io.ApiException;
import org.openml.apiconnector.io.HttpConnector;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.xml.Data;
import org.openml.apiconnector.xml.DataDelete;
import org.openml.apiconnector.xml.DataFeature;
import org.openml.apiconnector.xml.DataQuality;
import org.openml.apiconnector.xml.DataSetDescription;
import org.openml.apiconnector.xml.DataTag;
import org.openml.apiconnector.xml.DataUntag;
import org.openml.apiconnector.xml.TaskDelete;
import org.openml.apiconnector.xml.TaskTag;
import org.openml.apiconnector.xml.TaskUntag;
import org.openml.apiconnector.xml.TaskInputs;
import org.openml.apiconnector.xml.UploadDataSet;
import org.openml.apiconnector.xml.UploadTask;
import org.openml.apiconnector.xml.Data.DataSet;
import org.openml.apiconnector.xml.TaskInputs.Input;
import org.openml.apiconnector.xstream.XstreamXmlMapping;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.thoughtworks.xstream.XStream;

public class TestDataFunctionality {
	private static final String data_file = "data/iris.arff";
	private static final int probe = 61;
	private static final String tag = "junittest";

	private static final String url = "https://test.openml.org/";
	private static final String key_read = "c1994bdb7ecb3c6f3c8f3b35f4b47f1f"; // mlr ..  sorry i borrowed it
	private static final OpenmlConnector client_write = new OpenmlConnector(url, "8baa83ecddfe44b561fd3d92442e3319");
	private static final OpenmlConnector client_read = new OpenmlConnector(url, key_read); 
	private static final OpenmlConnector live_client_read = new OpenmlConnector(key_read); 
	private static final XStream xstream = XstreamXmlMapping.getInstance();
	
	public static File createTestDatasetDescription() throws IOException {
		DataSetDescription dsd = new DataSetDescription("test", "Unit test should be deleted", "arff", "class");
		return Conversion.stringToTempFile(xstream.toXML(dsd), "test-data", "arff");
	}
	
	@Test
	public void testApiDataDownload() throws Exception {
		DataSetDescription dsd = client_read.dataGet(probe);
		DataFeature features = client_read.dataFeatures(probe);
		DataQuality qualities = client_read.dataQualities(probe);
		
		File tempDsd = Conversion.stringToTempFile(xstream.toXML(dsd), "data", "xml");
		File tempXsd = client_read.getXSD("openml.data.upload");
		
		String url = client_read.getApiUrl() + "data/" + probe;
		File rawFile = HttpConnector.getFileFromUrl(new URL(url + "?api_key=" + client_read.getApiKey()), false, "xml");
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
		client_read.dataUnprocessed(2, "normal");
		client_read.dataUnprocessed(2, "random");
	}

	@Test
	public void testApiUploadDownload() throws Exception {
		File description = createTestDatasetDescription();
		File toUpload = new File(data_file);
		UploadDataSet ud = client_write.dataUpload(description, toUpload);
		DataTag dt = client_write.dataTag(ud.getId(), tag);
		assertTrue(Arrays.asList(dt.getTags()).contains(tag));
		
		// Download dataset and check md5 thingy
		DataSetDescription dsd_downloaded = client_read.dataGet(ud.getId());
		File dataset = client_read.datasetGet(dsd_downloaded);
		assertEquals(Hashing.md5(dataset), Hashing.md5(toUpload));
		
		// create task upon it (clustering task)
		Input estimation_procedure = new Input("estimation_procedure", "17");
		Input data_set = new Input("source_data", "" + ud.getId());
		Input measure = new Input("evaluation_measures", "predictive_accuracy");
		Input[] inputs = {estimation_procedure, data_set, measure};
		UploadTask ut = client_write.taskUpload(inputsToTaskFile(inputs, 5));
		
		TaskTag tt = client_write.taskTag(ut.getId(), tag);
		assertTrue(Arrays.asList(tt.getTags()).contains(tag));
		TaskUntag tu = client_write.taskUntag(ut.getId(), tag);
		assertTrue(tu.getTags() == null);
		
		try {
			client_write.dataDelete(ud.getId());
			// this SHOULD fail, we should not be allowed to delete data that contains tasks.
			fail("Problem with API. Dataset ("+ud.getId()+") was deleted while it contains a task ("+ut.getId()+"). ");
		} catch(ApiException ae) {}
		
		
		// delete the task
		TaskDelete td = client_write.taskDelete(ut.getId());
		assertTrue(td.get_id().equals(ut.getId()));
		
		// and delete the data
		DataUntag du = client_write.dataUntag(ud.getId(), tag);
		assertTrue(du.getTags() == null);
		
		DataDelete dd = client_write.dataDelete(ud.getId());
		assertTrue(ud.getId() == dd.get_id());
	}
	
	@Test
	public void testDataQualitiesWithNullValues() throws Exception {
		DataQuality dq = live_client_read.dataQualities(3);
		
		// check if test is actually up to date (otherwise we should use other dataset that contains null values)
		Collection<Double> qualityValues = dq.getQualitiesMap().values();
		assertTrue(qualityValues.contains(null));
		
		// test if converting back doesn't break anything
		xstream.toXML(dq);
	}
	
	@Test
	public void testApiUploadFromUrl() throws Exception {
		String dataUrl = "http://storm.cis.fordham.edu/~gweiss/data-mining/weka-data/cpu.arff";
		
		client_write.setVerboseLevel(1);
		DataSetDescription dsd = new DataSetDescription("anneal", "Unit test should be deleted", "arff", dataUrl, "class");
		String dsdXML = xstream.toXML(dsd);
		File description = Conversion.stringToTempFile(dsdXML, "test-data", "arff");
		
		UploadDataSet ud = client_write.dataUpload(description, null);
		DataTag dt = client_write.dataTag(ud.getId(), tag);
		assertTrue(Arrays.asList(dt.getTags()).contains(tag));
		
		// Download dataset and check md5 thingy
		DataSetDescription dsd_downloaded = client_read.dataGet(ud.getId());
		File dataset = client_read.datasetGet(dsd_downloaded);

		File obtainedFile = HttpConnector.getFileFromUrl(new URL(dataUrl), false, "xml");
		assertEquals(Hashing.md5(dataset), Hashing.md5(obtainedFile));
		
	}

	@Test
	@Ignore
	public void testApiDataList() throws Exception {
		Map<String, String> filters = new TreeMap<String, String>();
		filters.put("tag", "study_14");
		
		Data datasets = client_read.dataList(filters);
		assertTrue(datasets.getData() != null);
		
		assertTrue(datasets.getData().length > 20);
		for (DataSet dataset : datasets.getData()) {
			assertTrue("No qualities for dataset " + dataset.getDid(), dataset.getQualities() != null);
			assertTrue("Not enough qualities for dataset " + dataset.getDid(), dataset.getQualities().length > 5);
		}
	}

	@Test
	public void testApiAdditional() throws Exception {
		client_read.dataQualitiesList();
	}
	
	@Test
	public void testGetDataAsCsv() throws Exception {
		//client_read.setVerboseLevel(1);
		Random random = new Random();

		Map<String,String> filters = new TreeMap<String, String>();
		filters.put("limit", "100");
		
		DataSet[] all = client_read.dataList(filters).getData();
		
		for (int i = 0; i < 5; ) {
			DataSet current = all[random.nextInt(all.length)];
			
			String numInst = current.getQualityMap().get("NumberOfInstances");
			
			if (current.getFileId() == null || !current.getFormat().toLowerCase().equals("arff")) {
				continue;
			}
			
			String fullUrl = url + "data/get_csv/" + current.getFileId() + "/" + current.getName() + ".csv";
			System.out.println(fullUrl);
			final URL url = new URL(fullUrl);
			final Reader reader = new InputStreamReader(url.openStream(), "UTF-8");
			final CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT);
			try {
				if (numInst != null) {
					int numberOfInstances = (int) Double.parseDouble(numInst);
					int foundRecords = parser.getRecords().size() - 1; // -1 because of csv header
					assertEquals(foundRecords, numberOfInstances);
				}
			} finally {
			    parser.close();
			    reader.close();
			}

			// important
			i += 1;
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
	
	public static File inputsToTaskFile(Input[] inputs, int ttid) throws IOException {
		TaskInputs task = new TaskInputs(null, ttid, inputs, null);
		File taskFile = Conversion.stringToTempFile(xstream.toXML(task), "task", "xml");
		return taskFile;
	}
}
