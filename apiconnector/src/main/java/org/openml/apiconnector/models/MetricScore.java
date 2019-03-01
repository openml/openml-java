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
package org.openml.apiconnector.models;

import java.text.DecimalFormat;

import org.openml.apiconnector.algorithms.MathHelper;

public class MetricScore {

	private Double score = null;
	private long nr_of_instances = 0;
	private Double[] array = null;
	private double[][] confusion_matrix = null;

	public MetricScore(Double score, long nr_of_instances) {
		this.score = score;
		this.nr_of_instances = nr_of_instances;
	}

	public MetricScore(Double[] array, long nr_of_instances) {
		this.array = array;
		this.nr_of_instances = nr_of_instances;
	}

	public MetricScore(Double score, Double[] array, long nr_of_instances) {
		this.score = score;
		this.array = array;
		this.nr_of_instances = nr_of_instances;
	}

	public MetricScore(double[][] confusion_matrix) {
		this.confusion_matrix = confusion_matrix;
	}

	public Double getScore() {
		return score;
	}

	public boolean hasArray() {
		return array != null || confusion_matrix != null;
	}

	public long getNrOfInstances() {
		return nr_of_instances;
	}

	public String getArrayAsString(DecimalFormat decimalFormat) {
		StringBuilder sb = new StringBuilder();
		if (array != null) {
			for (Double d : array) {
				if (Double.isNaN(d)) {
					sb.append("," + 0.0D);
				} else {
					sb.append("," + decimalFormat.format(d));
				}
			}
			return "[" + sb.toString().substring(1) + "]";
		} else if (confusion_matrix != null) {

			for (double[] perClass : confusion_matrix) {
				StringBuilder sbperClass = new StringBuilder();

				for (double i : perClass) {
					sbperClass.append("," + ((int) i));
				}

				sb.append(",[" + sbperClass.toString().substring(1) + "]");
			}
			return "[" + sb.toString().substring(1) + "]";
		} else {
			return null;
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		if (score != null) {
			sb.append(score);
		}

		if (score != null && hasArray()) {
			sb.append(", ");
		}

		if (hasArray() != false) {
			sb.append(getArrayAsString(MathHelper.defaultDecimalFormat));
		}

		return "[" + sb.toString() + "]";
	}
}
