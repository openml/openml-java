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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.openml.apiconnector.io.ApiException;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.xml.DataSetDescription;
import org.openml.apiconnector.xml.Task;
import org.openml.apiconnector.xml.Task.Input.Data_set;
import org.openml.apiconnector.xml.Task.Input.Estimation_procedure;
import org.openml.apiconnector.xml.Task.Input.Stream_schedule;
import org.openml.apiconnector.xml.Task.Output.Predictions;

public class TaskInformation {
	public static final int TASK_EXISTS_ERROR_CODE = 614;
	
	/**
	 * @param t - Input Task. 
	 * @return The number of repeats
	 * @throws Exception
	 */
	public static int getNumberOfRepeats( Task t ) throws Exception {
		Estimation_procedure ep = getEstimationProcedure(t);
		for(int i = 0; i < ep.getParameters().length; ++i) {
			if(ep.getParameters()[i].getName().equals("number_repeats") ) {
				return Integer.parseInt(ep.getParameters()[i].getValue());
			}
		}
		throw new Exception("Tasks estimation procedure does not contain \"number_repeats\" (task_id="+t.getTask_id()+")");
	}

	/**
	 * @param t - Input Task. 
	 * @return The number of samples
	 * @throws Exception
	 */
	public static int getNumberOfSamples( Task t ) throws Exception {
		Estimation_procedure ep = getEstimationProcedure(t);
		for(int i = 0; i < ep.getParameters().length; ++i) {
			if(ep.getParameters()[i].getName().equals("number_samples") ) {
				return Integer.parseInt(ep.getParameters()[i].getValue());
			}
		}
		throw new Exception("Tasks estimation procedure does not contain \"number_samples\" (task_id="+t.getTask_id()+")");
	}

	/**
	 * @param t - Input Task. 
	 * @return The number of folds
	 * @throws Exception
	 */
	public static int getNumberOfFolds( Task t ) throws Exception {
		Estimation_procedure ep = getEstimationProcedure(t);
		for(int i = 0; i < ep.getParameters().length; ++i) {
			if(ep.getParameters()[i].getName().equals("number_folds") ) {
				return Integer.parseInt(ep.getParameters()[i].getValue());
			}
		}
		throw new Exception("Tasks estimation procedure does not contain \"number_folds\" (task_id="+t.getTask_id()+")");
	}

	/**
	 * @param t - Input Task. 
	 * @return The percentage (holdout set)
	 * @throws Exception
	 */
	public static Integer getPercentage( Task t ) throws Exception {
		Estimation_procedure ep = getEstimationProcedure(t);
		for(int i = 0; i < ep.getParameters().length; ++i) {
			if(ep.getParameters()[i].getName().equals("percentage") ) {
				return Integer.parseInt(ep.getParameters()[i].getValue());
			}
		}
		throw new Exception("Tasks estimation procedure does not contain \"percentage\" (task_id="+t.getTask_id()+")");
	}
	
	/**
	 * @param t - Input Task. 
	 * @return The estimation procedure
	 * @throws Exception
	 */
	public static Estimation_procedure getEstimationProcedure( Task t ) throws Exception {
		for( int i = 0; i < t.getInputs().length; ++i ) {
			if(t.getInputs()[i].getName().equals("estimation_procedure") ) {
				return t.getInputs()[i].getEstimation_procedure();
			}
		}
		throw new Exception("Task does not define an estimation procedure (task_id="+t.getTask_id()+")");
	}
	

	/**
	 * @param t - Input Task. 
	 * @return The stream schedule
	 * @throws Exception
	 */
	public static Stream_schedule getStreamSchedule(Task t) throws Exception {
		for( int i = 0; i < t.getInputs().length; ++i ) {
			if(t.getInputs()[i].getName().equals("stream_schedule") ) {
				return t.getInputs()[i].getStream_schedule();
			}
		}
		throw new Exception("Task does not define a stream_schedule (task_id="+t.getTask_id()+")");
	}
	
	/**
	 * @param t - Input Task. 
	 * @return The cost matrix
	 * @throws Exception
	 */
	public static double[][] getCostMatrix( Task t ) throws Exception {
		for( int i = 0; i < t.getInputs().length; ++i ) {
			if(t.getInputs()[i].getName().equals("cost_matrix") ) {
				return t.getInputs()[i].getCost_Matrix();
			}
		}
		return null;
	}
	
	/**
	 * @param t - Input Task. 
	 * @return The source data
	 * @throws Exception
	 */
	public static Data_set getSourceData( Task t ) throws Exception {
		for( int i = 0; i < t.getInputs().length; ++i ) {
			if(t.getInputs()[i].getName().equals("source_data") ) {
				return t.getInputs()[i].getData_set();
			}
		}
		throw new Exception("Task does not define an estimation procedure (task_id="+t.getTask_id()+")");
	}
	
	/**
	 * @param t - Input Task. 
	 * @return The prediction format
	 * @throws Exception
	 */
	public static Predictions getPredictions( Task t ) throws Exception {
		for( int i = 0; i < t.getOutputs().length; ++i ) {
			if(t.getOutputs()[i].getName().equals("predictions") ) {
				return t.getOutputs()[i].getPredictions();
			}
		}
		throw new Exception("Task does not define an predictions (task_id="+t.getTask_id()+")");
	}
	
	/**
	 * @param t - Input Task. 
	 * @return The classnames of the input data
	 * @throws Exception
	 */
	public static String[] getClassNames( OpenmlConnector apiconnector, Task t ) throws Exception {
		DataSetDescription dsd = getSourceData(t).getDataSetDescription( apiconnector );
		String targetFeature = getSourceData(t).getTarget_feature();
		return getClassNames(apiconnector.datasetGet(dsd), t.getTask_id(), targetFeature);
	}
	
	public static String[] getClassNames( File dataset, int task_id, String targetFeature ) throws Exception {
		BufferedReader br = new BufferedReader( new FileReader( dataset ) );
		
		String line;
		
		while( (line = br.readLine()) != null) {
			if( ArffHelper.isDataDeclaration(line) ) {
				throw new Exception("Attribute not found before data declaration (task_id="+task_id+")");
			}
			if( ArffHelper.isAttributeDeclaration(line) ) {
				try {
					if( ArffHelper.getAttributeName( line ).equals( targetFeature ) ) {
						br.close();
						return ArffHelper.getNominalValues( line );
					}
				} catch( Exception e ) {/*Not going to happen*/}
			}
		}
		br.close();
		throw new Exception("Attribute not found (task_id="+task_id+")");
	}
	
	public static Integer[] getTaskIdsFromErrorMessage(ApiException e) {
		// returns task id from error message 
		if (e.getCode() != TASK_EXISTS_ERROR_CODE) {
			throw new RuntimeException("wrong error code, expected " + TASK_EXISTS_ERROR_CODE);
		}
		String[] result = e.getMessage().substring(e.getMessage().indexOf('[') + 1, e.getMessage().indexOf(']')).split(",");
		Integer[] parsed = new Integer[result.length];
		for (int i = 0; i < result.length; ++i) {
			parsed[i] = Integer.parseInt(result[i]);
		}
		return parsed;
	}
}
