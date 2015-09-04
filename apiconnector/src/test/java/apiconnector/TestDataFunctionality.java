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

import java.io.File;

import org.junit.Test;
import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.xml.DataDelete;
import org.openml.apiconnector.xml.DataFeature;
import org.openml.apiconnector.xml.DataQuality;
import org.openml.apiconnector.xml.DataSetDescription;
import org.openml.apiconnector.xml.UploadDataSet;
import org.openml.apiconnector.xstream.XstreamXmlMapping;

import com.thoughtworks.xstream.XStream;


public class TestDataFunctionality {
	private static final String data_file = "C:\\data\\iris.arff";
	private static final int probe = 61;

	private static final String url = "http://www.openml.org/";
	private static final String session_hash = "d488d8afd93b32331cf6ea9d7003d4c3";
	private static final OpenmlConnector client = new OpenmlConnector(url,session_hash);
	private static final XStream xstream = XstreamXmlMapping.getInstance();
	
	
	@Test
	public void testApiDataDownload() {
		
		
		try {
			DataSetDescription dsd = client.dataGet( probe );
			DataFeature features = client.dataFeatures( probe );
			DataQuality qualities = client.dataQualities( probe );
			
			File tempDsd = Conversion.stringToTempFile(xstream.toXML(dsd), "data", "xml");
			File tempXsd = client.getXSD("openml.data.upload");
			
			System.out.println(Conversion.fileToString(tempXsd));
			
			assertTrue(Conversion.validateXML(tempDsd, tempXsd));
			
			// very easy checks, should all pass
			assertTrue( dsd.getId() == probe );
			assertTrue( features.getFeatures().length > 0 );
			assertTrue( qualities.getQualities().length > 0 );
		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed: " + e.getMessage());
		}
	}

	@Test
	public void testApiUploadDownload() {
		client.setVerboseLevel(2);
		try {
			DataSetDescription dsd = new DataSetDescription("test", "Unit test should be deleted", "arff", "class");
			String dsdXML = xstream.toXML(dsd);
			File description = Conversion.stringToTempFile(dsdXML, "test-data", "arff");
			System.out.println(dsdXML);
			UploadDataSet ud = client.dataUpload(description, new File(data_file));
			System.out.println(xstream.toXML(ud));
			
			client.dataTag(ud.getId(), "testTag");
			
			DataDelete dd = client.dataDelete(ud.getId());
			
			assertTrue( ud.getId() == dd.get_id() );
			
		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed: " + e.getMessage());
		}
	}
	
	@Test
	public void testApiAdditional() {
		try {
			client.dataQualitiesList();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed: " + e.getMessage());
		}
	}

}
