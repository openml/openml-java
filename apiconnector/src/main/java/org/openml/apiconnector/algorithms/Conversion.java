/*******************************************************************************
 * Copyright (C) 2017, Jan N. van Rijn <j.n.van.rijn@liacs.leidenuniv.nl>
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the copyright holder nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package org.openml.apiconnector.algorithms;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

public class Conversion {

	/**
	 * Stores a string into a temporarily existing file. 
	 * 
	 * @param string - The string to store in the file. 
	 * @param filename - The name of the file.
	 * @param format - The extension of the file. 
	 * @param directory - the tmp directory
	 * @return A pointer to the temp file that was created. 
	 * @throws IOException
	 */
	public static File stringToTempFile( String string, String filename, String format, File directory) throws IOException {
		File file = File.createTempFile(filename, '.' + format, directory);
		BufferedWriter br = new BufferedWriter(new FileWriter(file));
		br.write(string);
		br.close();
		file.deleteOnExit();
		return file;
	}

	/**
	 * Stores a string into a temporarily existing file. 
	 * 
	 * @param string - The string to store in the file. 
	 * @param filename - The name of the file.
	 * @param format - The extension of the file. 
	 * @return A pointer to the temp file that was created. 
	 * @throws IOException
	 */
	public static File stringToTempFile( String string, String filename, String format) throws IOException {
		return stringToTempFile(string, filename, format, null);
	}
	
	/**
	 * Parses a comma separated string to a integer array
	 * 
	 * @param commaSeparated - the comma separated string to parse
	 * @return the resulting integer array
	 * @throws NumberFormatException
	 */
	public static int[] commaSeparatedStringToIntArray( String commaSeparated ) throws NumberFormatException {
		String[] splitted = commaSeparated.replaceAll("\\s","").split(","); // remove spaces, split on comma
		int[] result = new int[splitted.length];
		for(int i = 0; i < result.length; ++i) {
			result[i] = Integer.parseInt(splitted[i]);
		}
		return result;
	}
	
	/**
	 * Reads a file and stores the content in a string
	 * 
	 * @param f - File pointer to the file that needs to be read. Should be non-binary. 
	 * @return A string containing the content of the file. 
	 * @throws IOException
	 */
	public static String fileToString( File f ) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(f));
		StringBuilder sb = new StringBuilder();
		String line = br.readLine();
		while( line != null ) {
			sb.append( line + "\n" );
			line = br.readLine();
		}
		br.close();
		return sb.toString();
	}
	
	public static void log( String status, String action, String message ) {
		log( status, action, message, System.err );
	}
	
	public static void log( String status, String action, String message, PrintStream writer ) {
		writer.println( "["+DateParser.humanReadable.format( System.currentTimeMillis() )+"] ["+status+"] ["+action+"] " + message );
	}
	
	public static double percentage(int observation, int total) {
		return ((double) observation / (double) total) * 100;
	}
	
	public static boolean validateXML(File xml, File xsd) throws SAXException, IOException {
		Source xmlFile = new StreamSource(xml);
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = schemaFactory.newSchema(xsd);
		Validator validator = schema.newValidator();
		try {
		  validator.validate(xmlFile);
		  return true;
		} catch (SAXException e) {
		  return false;
		}
	}
}
