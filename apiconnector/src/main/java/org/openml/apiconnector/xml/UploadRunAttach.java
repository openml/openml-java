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
package org.openml.apiconnector.xml;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("oml:upload_run_attach")
public class UploadRunAttach {
	@XStreamAlias("oml:run_id")
	private Integer run_id;
	
	@XStreamImplicit
	@XStreamAlias("oml:predictionfile")
	private Predictionfile[] predictionfiles;

	public Integer getRun_id() {
		return run_id;
	}
	
	public Predictionfile[] getPredictionFiles() {
		return predictionfiles;
	}
	
	@XStreamAlias("oml:predictionfile")
	public class Predictionfile implements Serializable {
		private static final long serialVersionUID = 4879830180930759589L;

		@XStreamAlias("oml:name")
		private String name;

		@XStreamAlias("oml:upload_time")
		private String upload_time;
		
		@XStreamAlias("oml:file_id")
		private int file_id;
		
	}
}
