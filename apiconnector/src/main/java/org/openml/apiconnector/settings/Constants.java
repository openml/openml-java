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
package org.openml.apiconnector.settings;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Constants {
	
	/**
	 * When doing an Api Request with an API session hash which is valid for
	 * less than the DEFAULT_TIME_MARGIN, a new session hash will be loaded. 
	 */
	public static final int DEFAULT_TIME_MARGIN = 300000; // 5 minutes
	
	/**
	 * Default Dataset format
	 */
	public static final String DATASET_FORMAT = "arff";
	
	/**
	 * Default XMLNS String to be included in generated XML files.
	 */
	public static final String OPENML_XMLNS = "http://openml.org/openml";
	
	public static final String OPENML_DIRECTORY = System.getProperty("user.home") + "/.openml";
	
	public static final int VERBOSE_LEVEL_XML = 1;
	public static final int VERBOSE_LEVEL_ARFF = 2;
	
	public static final String DATA_STATUS_PREP = "in_preparation";
	public static final String DATA_STATUS_ACTIVE = "active";
	
	public final static Double EPSILON = 0.00001;
	
	public final static DecimalFormat defaultDecimalFormat = new DecimalFormat("#.######",
			DecimalFormatSymbols.getInstance(Locale.ENGLISH));
}
