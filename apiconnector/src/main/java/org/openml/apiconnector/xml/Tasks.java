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
