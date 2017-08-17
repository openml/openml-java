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

import org.json.JSONArray;

public class OptionParser {

	/**
	 * Removes the first element of an option string. Used for generating option strings in Weka plugin. 
	 * 
	 * @param old - The original option string
	 * @return The processed option String
	 */
	public static String[] removeFirstElement( String[] old ) {
		int n = old.length-1;
		String[] newArray = new String[n];
		System.arraycopy( old, 1, newArray, 0, n);
		return newArray;
	}
	
	public static double[][] stringToArray( String input ) throws Exception {
		JSONArray ja = new JSONArray( input );
		
		double[][] result = new double[ja.length()][ja.length()];
		for( int i = 0; i < ja.length(); ++i ) {
			JSONArray current = (JSONArray) ja.get(i);
			if( current.length() < ja.length() ) { throw new Exception("Array dimensions not equal (i)."); }
			for( int j = 0; j< current.length(); ++j ) {
				if( j >= ja.length() ) { throw new Exception("Array dimensions not equal (ii)."); }
				result[i][j] = current.getDouble( j );
			}
		}
		
		return result;
	}
	
}
