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

import org.apache.commons.lang3.ArrayUtils;
import org.openml.apiconnector.settings.Constants;

public class Trace {
	
	private final String oml = Constants.OPENML_XMLNS;
	private Integer run_id;
	private Trace_iteration[] trace_iterations;
	

	public Trace(Integer run_id) {
		this.run_id = run_id;
		this.trace_iterations = new Trace_iteration[0];
	}
	
	public Trace(Integer run_id, Trace_iteration[] trace_iterations) {
		this.run_id = run_id;
		this.trace_iterations = trace_iterations;
	}
	

	public void addIteration(Trace_iteration ti) {
		trace_iterations = ArrayUtils.addAll(trace_iterations, ti);
	}
	
	public Integer getRun_id() {
		return run_id;
	}
	
	public Trace_iteration[] getTrace_iterations() {
		return trace_iterations;
	}
	
	public String getOml() {
		return oml;
	}
	
	public static class Trace_iteration {
		private Integer repeat;
		private Integer fold;
		private Integer iteration;
		private String setup_string;
		private Double evaluation;
		private Boolean selected;
		
		public Trace_iteration(Integer repeat, Integer fold, Integer iteration,
				String setup_string, Double evaluation, Boolean selected) {
			super();
			this.repeat = repeat;
			this.fold = fold;
			this.iteration = iteration;
			this.setup_string = setup_string;
			this.evaluation = evaluation;
			this.selected = selected;
		}

		public Integer getRepeat() {
			return repeat;
		}
		
		public Integer getFold() {
			return fold;
		}
		
		public Integer getIteration() {
			return iteration;
		}
		
		public String getSetup_string() {
			return setup_string;
		}
		
		public Double getEvaluation() {
			return evaluation;
		}
		
		public Boolean getSelected() {
			return selected;
		}
	}
}