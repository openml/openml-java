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

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("oml:study")
public class Study extends OpenmlApiResponse {
	private static final long serialVersionUID = 8578912L;
	
	@XStreamAlias("oml:id")
	private Integer id;

	@XStreamAlias("oml:alias")
	private String alias;

	@XStreamAlias("oml:main_entity_type")
	private String main_entity_type;

	@XStreamAlias("oml:benchmark_suite")
	private Integer benchmark_suite;

	@XStreamAlias("oml:name")
	private String name;

	@XStreamAlias("oml:description")
	private String description;

	@XStreamAlias("oml:creation_date")
	private String creation_date;
	
	@XStreamAlias("oml:creator")
	private Integer creator;
	
	@XStreamImplicit(itemFieldName="oml:tag")
	private Tag[] tag;

	@XStreamAlias("oml:data")
	private Data data;
	
	@XStreamAlias("oml:tasks")
	private Tasks tasks;
	
	@XStreamAlias("oml:flows")
	private Flows flows;
	
	@XStreamAlias("oml:setups")
	private Setups setups;
	
	@XStreamAlias("oml:runs")
	private Runs runs;
	
	public Study(String alias, String name, String description, Integer benchmarkSuite, Integer[] taskIds, Integer[] runIds) throws Exception {
		if (!((taskIds == null) ^ (runIds == null))) {
			throw new Exception("Requires task ids xor run ids");
		}
		this.alias = alias;
		this.name = name;
		this.description = description;
		this.main_entity_type = taskIds == null ? "run" : "task";
		this.benchmark_suite = benchmarkSuite;
		if (taskIds != null) { this.tasks = new Tasks(taskIds); }
		if (runIds != null) { this.runs = new Runs(runIds); }
	}

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getCreation_date() {
		return creation_date;
	}

	public Integer getCreator() {
		return creator;
	}

	public String getMain_entity_type() {
		return main_entity_type;
	}

	public Integer getBenchmark_suite() {
		return benchmark_suite;
	}
	
	// legacy
	public Tag[] getTag() {
		return tag;
	}

	public Integer[] getDataset() {
		if (data == null) {
			return null;
		}
		return data.getData();
	}

	public Integer[] getTasks() {
		if (tasks == null) {
			return null;
		}
		return tasks.getTasks();
	}
	
	public Integer[] getFlows() {
		if (flows == null) {
			return null;
		}
		return flows.getFlows();
	}
	
	public Integer[] getSetups() {
		if (setups == null) {
			return null;
		}
		return setups.getSetups();
	}
	
	public Integer[] getRuns() {
		if (runs == null) {
			return null;
		}
		return runs.getRuns();
	}
	
	public class Tag implements Serializable {
		private static final long serialVersionUID = 8576912L;
		
		@XStreamAlias("oml:name")
		private String name;
		
		@XStreamAlias("oml:window_start")
		private String window_start;

		@XStreamAlias("oml:write_access")
		private String write_access;

		public String getName() {
			return name;
		}

		public String getWindow_start() {
			return window_start;
		}

		public String getWrite_access() {
			return write_access;
		}
	}
	
	public static class Data {
		@XStreamImplicit
		@XStreamAlias("oml:data_id")
		Integer[] data_id;
		
		public Integer[] getData() {
			return data_id;
		}
	}
	
	public static class Tasks {
		@XStreamImplicit(itemFieldName="oml:task_id")
		Integer[] task_id;
		
		public Tasks(Integer[] task_id) {
			this.task_id = task_id;
		}
		
		public Integer[] getTasks() {
			return task_id;
		}
	}
	
	public static class Flows {
		@XStreamImplicit(itemFieldName="oml:flow_id")
		Integer[] flow_id;
		
		public Integer[] getFlows() {
			return flow_id;
		}
	}
	
	public static class Setups {
		@XStreamImplicit(itemFieldName="oml:setup_id")
		Integer[] setup_id;
		
		public Integer[] getSetups() {
			return setup_id;
		}
	}
	
	public static class Runs {
		@XStreamImplicit(itemFieldName="oml:run_id")
		Integer[] run_id;
		
		public Runs(Integer[] run_id) {
			this.run_id = run_id;
		}
		
		public Integer[] getRuns() {
			return run_id;
		}
	}
}
