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

import org.apache.commons.lang3.ArrayUtils;
import org.openml.apiconnector.settings.Constants;

public class RunTrace {
	
	private final String oml = Constants.OPENML_XMLNS;
	private Integer run_id;
	private Trace_iteration[] trace_iterations;
	

	public RunTrace(Integer run_id) {
		this.run_id = run_id;
		this.trace_iterations = new Trace_iteration[0];
	}
	
	public RunTrace(Integer run_id, Trace_iteration[] trace_iterations) {
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
