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

import org.openml.apiconnector.settings.Constants;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.converters.extended.ToAttributedValueConverter;

@XStreamAlias("oml:tasks")
public class Tasks {

	@XStreamAsAttribute
	@XStreamAlias("xmlns:oml")
	private final String oml = Constants.OPENML_XMLNS;

	@XStreamImplicit
	@XStreamAlias("oml:task")
	public Task[] task;
	
	public String getOml() {
		return oml;
	}

	public Task[] getTask() {
		return task;
	}

	@XStreamAlias("oml:task")
	public static class Task {
		@XStreamAlias("oml:task_id")
		private Integer task_id;
		
		@XStreamAlias("oml:task_type_id")
		private Integer task_type_id;
		
		@XStreamAlias("oml:task_type")
		private String task_type;
		
		@XStreamAlias("oml:did")
		private int did;
		
		@XStreamAlias("oml:name")
		private String name;
		
		@XStreamAlias("oml:status")
		private String status;
		
		@XStreamImplicit
		@XStreamAlias("oml:input")
		private Input[] inputs;
		
		@XStreamImplicit
		@XStreamAlias("oml:quality")
		private Quality[] qualities;
		
		public int getTask_id() {
			return task_id;
		}
		public String getTask_type() {
			return task_type;
		}
		public int getDid() {
			return did;
		}
		public String getName() {
			return name;
		}
		public String getStatus() {
			return status;
		}
		public Input[] getInputs() {
			return inputs;
		}
		public Quality[] getQualities() {
			return qualities;
		}

		@XStreamAlias("oml:quality")
		@XStreamConverter(value=ToAttributedValueConverter.class, strings={"value"})
		public static class Quality {
			
			@XStreamAsAttribute
			@XStreamAlias("name")
			private String name;
			
			private String value;
			
			public String getName() {
				return name;
			}
			public String getValue() {
				return value;
			}
		}

		@XStreamAlias("oml:input")
		@XStreamConverter(value=ToAttributedValueConverter.class, strings={"value"})
		public static class Input {
			
			@XStreamAsAttribute
			@XStreamAlias("name")
			private String name;
			
			private String value;
			
			public String getName() {
				return name;
			}
			public String getValue() {
				return value;
			}
		}
	}
}
