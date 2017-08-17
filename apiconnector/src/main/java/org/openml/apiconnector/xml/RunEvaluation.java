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

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("oml:run_evaluation")
public class RunEvaluation {
	
	@XStreamAsAttribute
	@XStreamAlias("xmlns:oml")
	private final String oml = Constants.OPENML_XMLNS;

	@XStreamAlias("oml:run_id")
	private Integer run_id;

	@XStreamAlias("oml:evaluation_engine_id")
	private Integer evaluation_engine_id;
	
	@XStreamAlias("oml:error")
	private String error;

	@XStreamAlias("oml:warning")
	private String warning;
	
	@XStreamImplicit(itemFieldName="oml:evaluation")
	private EvaluationScore[] evaluation;
	
	public RunEvaluation(int run_id, int evaluation_engine_id) {
		this.run_id = run_id;
		this.evaluation_engine_id = evaluation_engine_id;
		this.error = null;
		this.warning = null;
	}
	
	public void addEvaluationMeasures( EvaluationScore[] em ) {
		evaluation = ArrayUtils.addAll( this.evaluation, em );
	}
	
	public void addEvaluationMeasure( EvaluationScore em ) {
		evaluation = ArrayUtils.addAll( this.evaluation, em );
	}
	
	public void setError(String error, int max_length) {
		String truncateMessage = "... (message cut-off due to excessive length)";
		if (error.length() <= max_length) {
			this.error = error;
		} else {
			this.error = error.substring(0, max_length - truncateMessage.length()) + truncateMessage;
		}
	}
	
	public void setWarning(String warning, int max_length) {
		String truncateMessage = "... (message cut-off due to excessive length)";
		if (warning.length() <= max_length) {
			this.warning = warning;
		} else {
			this.warning = warning.substring(0, max_length - truncateMessage.length()) + truncateMessage;
		}
	}
	
	public String getOml() {
		return oml;
	}

	public Integer getRun_id() {
		return run_id;
	}

	public String getError() {
		return error;
	}

	public String getWarning() {
		return warning;
	}

	public EvaluationScore[] getEvaluation_scores() {
		return evaluation;
	}
}
