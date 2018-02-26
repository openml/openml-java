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
package org.openml.apiconnector.xml;

import java.io.Serializable;

import org.openml.apiconnector.settings.Constants;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("oml:runs")
public class RunList {

	@XStreamAsAttribute
	@XStreamAlias("xmlns:oml")
	private final String oml = Constants.OPENML_XMLNS;

	@XStreamImplicit
	@XStreamAlias("oml:run")
	private Run[] runs;

	public Run[] getRuns() {
		return runs;
	}

	public String getOml() {
		return oml;
	}

	@XStreamAlias("oml:run")
	public class Run implements Serializable {
		private static final long serialVersionUID = 87L;

		@XStreamAlias("oml:run_id")
		private int run_id;

		@XStreamAlias("oml:task_id")
		private int task_id;
		
		// should become int later
		@XStreamAlias("oml:task_type_id")
		private Integer task_type_id;

		@XStreamAlias("oml:setup_id")
		private int setup_id;

		@XStreamAlias("oml:uploader")
		private int uploader;
		
		@XStreamAlias("oml:upload_time")
		private String upload_time;
		
		@XStreamAlias("oml:error_message")
		private String error_message;
		
		public int getRun_id() {
			return run_id;
		}
		public int getTask_id() {
			return task_id;
		}
		public Integer getTask_type_id() {
			return task_type_id;
		}
		public int getSetup_id() {
			return setup_id;
		}
		public int getUploader() {
			return uploader;
		}
		public String getError_message() {
			return error_message;
		}
	}
}
