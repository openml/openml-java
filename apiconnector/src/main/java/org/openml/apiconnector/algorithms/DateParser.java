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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class DateParser {

	public static final DateFormat humanReadable = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	public static final DateFormat defaultOrder  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	/**
	 * Parses MySQL date format to Unix Time Stamp.
	 * 
	 * @param mysqlTime - A string representing the date field.
	 * @param timezone - String representation of timezone 
	 * @return Unix Time Stamp of mysqlTime
	 * @throws ParseException - Parse problem
	 */
	public static long mysqlDateToTimeStamp(String mysqlTime, String timezone) throws ParseException {
		DateFormat current = (DateFormat) defaultOrder.clone();
		Calendar cal = Calendar.getInstance();
	    current.setTimeZone(TimeZone.getTimeZone(timezone));
	    cal.setTime(current.parse(mysqlTime));
	    return cal.getTime().getTime();
	}
	
	public static long secondsSince(String xmlDate) throws ParseException {
		DateFormat current = (DateFormat) defaultOrder.clone();
		Calendar cal = Calendar.getInstance();
		cal.setTime(current.parse(xmlDate.replace('T', ' ')));
		
		return (System.currentTimeMillis() - cal.getTimeInMillis()) / 1000;
	}
	
	public static long unixTimestamp(String xmlDate) throws ParseException {
		DateFormat current = (DateFormat) defaultOrder.clone();
		Calendar cal = Calendar.getInstance();
		cal.setTime(current.parse(xmlDate.replace('T', ' ')));
		
		return cal.getTimeInMillis();
	}
}
