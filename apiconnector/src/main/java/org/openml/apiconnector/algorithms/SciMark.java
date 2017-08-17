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

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import jnt.scimark2.Constants;
import jnt.scimark2.Random;
import jnt.scimark2.kernel;

public class SciMark implements Serializable {
	
	private static SciMark instance = null;
	private static final long serialVersionUID = -5563065042084199486L;
	private boolean benchmarkDone;
	private double results[] = new double[5];
	private double average;
	
	String[] os = new String[5];
	
	public static void main( String[] args ) {
		SciMark scimark = new SciMark();
		String[] d = scimark.getOsInfo();
		System.out.println("Operating System: " + d[3] + " v" + d[4] + "\nJava version: " + d[1] + " by " + d[0] + "\nSystem architecture: " + d[2]  );
		System.out.println("Composite score: " + scimark.doBenchmark() );
		System.out.println("[ " + StringUtils.join( scimark.getStringArray(), ", " ) + " ]" );
		
	}
	
	public static SciMark getInstance() {
		if( instance == null ) {
			instance = new SciMark();
		}
		return instance;
	}
	
	/**
	 * Initiates a new SciMark instance. 
	 */
	public SciMark() {
		benchmarkDone = false;
		
		os[0] = System.getProperty("java.vendor");
		os[1] = System.getProperty("java.version");
		os[2] = System.getProperty("os.arch");
		os[3] = System.getProperty("os.name");
		os[4] = System.getProperty("os.version");
	}
	
	/**
	 * @return scores of the SciMark benchmark
	 */
	public double doBenchmark() {
		Conversion.log("INFO", "SciMark Benchmark", "Doing JVM Benchmark.");
		double min_time = Constants.RESOLUTION_DEFAULT;

		int FFT_size = Constants.FFT_SIZE;
		int SOR_size =  Constants.SOR_SIZE;
		int Sparse_size_M = Constants.SPARSE_SIZE_M;
		int Sparse_size_nz = Constants.SPARSE_SIZE_nz;
		int LU_size = Constants.LU_SIZE;
		
		Random R = new Random(Constants.RANDOM_SEED);

		results[0] = kernel.measureFFT( FFT_size, min_time, R);
		results[1] = kernel.measureSOR( SOR_size, min_time, R);
		results[2] = kernel.measureMonteCarlo(min_time, R);
		results[3] = kernel.measureSparseMatmult( Sparse_size_M, 
					Sparse_size_nz, min_time, R);
		results[4] = kernel.measureLU( LU_size, min_time, R);


		average = ( results[0] + results[1] + results[2] + results[3] + results[4]) / 5;
		benchmarkDone = true;
		return average;
	}
	
	/**
	 * @return Array containing basic information about the OS. 
	 */
	public String[] getOsInfo() {
		return os;
	}
	
	/**
	 * @return Scores from the benchmark converted to Strings. 
	 */
	public String[] getStringArray() {
		String[] res = new String[results.length];
		for(int i = 0; i < results.length; ++i) {
			res[i] = "" + results[i];
		}
		return res;
	}
	
	/**
	 * @return The average of all SciMark benchmarks
	 */
	public double getResult() {
		if( benchmarkDone == false )
			doBenchmark();
		return average;
	}
}
