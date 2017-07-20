package org.openml.apiconnector.xml;

import java.io.Serializable;

import org.openml.apiconnector.settings.Constants;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("oml:study")
public class Study implements Serializable {
	private static final long serialVersionUID = 8578912L;
	
	@XStreamAsAttribute
	@XStreamAlias("xmlns:oml")
	private final String oml = Constants.OPENML_XMLNS;
	
	@XStreamAlias("oml:id")
	private Integer id;

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
	
	class Data {
		@XStreamImplicit
		@XStreamAlias("oml:data_id")
		Integer[] data_id;
		
		public Integer[] getData() {
			return data_id;
		}
	}
	
	class Tasks {
		@XStreamImplicit
		@XStreamAlias("oml:task_id")
		Integer[] task_id;
		
		public Integer[] getTasks() {
			return task_id;
		}
	}
	
	class Flows {
		@XStreamImplicit
		@XStreamAlias("oml:flow_id")
		Integer[] flow_id;
		
		public Integer[] getFlows() {
			return flow_id;
		}
	}
	
	class Setups {
		@XStreamImplicit
		@XStreamAlias("oml:setup_id")
		Integer[] setup_id;
		
		public Integer[] getSetups() {
			return setup_id;
		}
	}
}
