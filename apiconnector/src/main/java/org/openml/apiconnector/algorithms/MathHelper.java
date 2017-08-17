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
package org.openml.apiconnector.algorithms;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class MathHelper {

	public final static DecimalFormat defaultDecimalFormat = new DecimalFormat("#.######", DecimalFormatSymbols.getInstance( Locale.ENGLISH ) );
	public final static DecimalFormat visualDecimalFormat = new DecimalFormat("#.##", DecimalFormatSymbols.getInstance( Locale.ENGLISH ) );
	public final static Double EPSILON = 0.00001;
	
	/**
	 * Calculates the standard deviation of a population. 
	 * 
	 * @param population
	 * @param sample
	 * @return The standard deviation of the population
	 */
	public static double standard_deviation( Double[] population, boolean sample ) {
		if( population.length == 1 ) { return 0.0; }
		
		double variance = 0;
		double mean = sum(population) / population.length;
		
		for( double entry : population ) {
			variance += java.lang.Math.pow(entry - mean, 2);
		}
		variance /= sample ? population.length - 1 : population.length;
		
		return java.lang.Math.sqrt(variance);
	}
	
	/**
	 * Returns the sum of values in an array. 
	 * 
	 * @param array
	 * @return The sum of values in the array
	 */
	public static double sum( Double[] array ) {
		double total = 0;
		for( double add : array ) total += add;
		return total;
	}
	
	/**
	 * Returns the mean of values in an array
	 * 
	 * @param array
	 * @return The mean of the array
	 */
	public static double mean( Double[] array ) {
		return sum(array) / array.length;
	}
	
	/**
	 * Returns the index of the maximal value in a double[] 
	 * 
	 * @param array
	 * @param naturalNumbers
	 * @return The index of the highest number in the array
	 */
	public static int argmax( double[] array, boolean naturalNumbers ) {
		int best = -1;
		double value = (naturalNumbers) ? 0D : Double.MIN_VALUE;
		for( int i = 0; i < array.length; ++i ) {
			if(array[i] > value) {
				value = array[i];
				best = i;
			}
		}
		return best;
	}
}
