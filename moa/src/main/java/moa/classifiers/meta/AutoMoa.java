package moa.classifiers.meta;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//import org.reflections.Reflections;

import com.github.javacliparser.FloatOption;
import com.github.javacliparser.IntOption;
import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.InstancesHeader;

import moa.classifiers.AbstractClassifier;
import moa.classifiers.Classifier;
import moa.core.Measurement;
import moa.core.ObjectRepository;
import moa.tasks.TaskMonitor;

public class AutoMoa extends AbstractClassifier {

	private static final long serialVersionUID = 1L;

	private static final int MAX_TOLLERATED_TRAINING_ERRROS = 10000;
	
	private int trainingErrors;

	protected Classifier[] ensemble;
	
	protected double[] historyTotal;
    
    protected Integer instancesSeen;
    
    List<Integer> topK;
    
    protected Set<Class<? extends AbstractClassifier>> allAvailableClasses;
    
    int activeClassifiers = 1;
	
	public FloatOption alphaOption = new FloatOption(
            "alpha",
            'a',
            "The fading factor.",
            0.99, 0, 1);
	
	public IntOption ensembleSizeOption = new IntOption(
            "ensemblesize",
            'n',
            "Ensemble Size",
            16, 2, Integer.MAX_VALUE);
	
	public IntOption gracePerionOption = new IntOption(
            "gracePeriod",
            'g',
            "How many instances before we reevalate the best classifier",
            1000, 1, Integer.MAX_VALUE);

	@Override
	public double[] getVotesForInstance(Instance inst) {
		double[] votes = new double[inst.classAttribute().numValues()];
		
		for (int i = 0; i < topK.size(); ++i) {
			double[] memberVotes = normalize(ensemble[topK.get(i)].getVotesForInstance(inst));
			double weight = historyTotal[topK.get(i)];
			
			// make internal classifiers so-called "hard classifiers"
			votes[maxIndex(memberVotes)] += 1.0 * weight;
		}
		
		return votes;
	}

	@Override
	public void resetLearningImpl() {
		this.historyTotal = new double[this.ensemble.length];
		for (int i = 0; i < this.ensemble.length; ++i) {
			this.historyTotal[i] = 1.0;
		}
		
        this.instancesSeen = 0;
        this.trainingErrors = 0;
        for (int i = 0; i < this.ensemble.length; i++) {
            this.ensemble[i].resetLearning();
        }
	}
	
	@Override
    public void setModelContext(InstancesHeader ih) {
        super.setModelContext(ih);
        
        for (int i = 0; i < this.ensemble.length; ++i) {
			this.ensemble[i].setModelContext(ih);
		}
    }

	@Override
	public boolean isRandomizable() {
		return false;
	}

	@Override
	public void getModelDescription(StringBuilder arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected Measurement[] getModelMeasurementsImpl() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/*@Override
    public void prepareForUseImpl(TaskMonitor monitor, ObjectRepository repository) {
		allAvailableClasses = new HashSet<Class<? extends AbstractClassifier>>();
    	Reflections reflections = new Reflections("moa.classifiers");
    	Set<Class<? extends AbstractClassifier>> allClasses = reflections.getSubTypesOf(AbstractClassifier.class);
    	
    	for (Class<? extends AbstractClassifier> c : allClasses) {
    		if (Modifier.isAbstract(c.getModifiers()) != true) {
    			allAvailableClasses.add(c);
    		}
    	}
    	
        int ensembleSize = ensembleSizeOption.getValue();
        this.ensemble = new Classifier[ensembleSize];
        for (int i = 0; i < ensembleSize; i++) {
        	monitor.setCurrentActivity("Materializing learner " + (i + 1) + "...", -1.0);
        	
    		try {
				this.ensemble[i] = (Classifier) getRamdonClassifier().newInstance();
            } catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				new RuntimeException(e.getMessage());
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block

				e.printStackTrace();
				new RuntimeException(e.getMessage());
			}
        	
            if (monitor.taskShouldAbort()) {
                return;
            }
            monitor.setCurrentActivity("Preparing learner " + (i + 1) + "...", -1.0);
            this.ensemble[i].prepareForUse(monitor, repository);
            if (monitor.taskShouldAbort()) {
                return;
            }
        }
        super.prepareForUseImpl(monitor, repository);
        
        topK = topK(historyTotal, activeClassifiers);
    } */
	

	protected Class<? extends AbstractClassifier> getRamdonClassifier() {
		List<Class<? extends AbstractClassifier>> classesList = new ArrayList<Class<? extends AbstractClassifier>>(allAvailableClasses);
    	
    	Collections.shuffle(classesList);
    	Class<? extends AbstractClassifier> currentClass = classesList.get(0);
    	System.err.println("Initiating: " + currentClass + ", abstract? " + Modifier.isAbstract(currentClass.getModifiers()));
    	return currentClass;
	}

	@Override
	public void trainOnInstanceImpl(Instance inst) {
		
		for (int i = 0; i < this.ensemble.length; i++) {
			
			// Online Performance estimation
			double[] votes = ensemble[i].getVotesForInstance(inst);
			boolean correct = (maxIndex(votes) * 1.0 == inst.classValue());
			
			historyTotal[i] = historyTotal[i] * alphaOption.getValue();
			if (correct) {
				historyTotal[i] += 1 - alphaOption.getValue();
			}
			try {
				this.ensemble[i].trainOnInstance(inst);
			} catch(RuntimeException e) {
				this.trainingErrors += 1;
				
				if (trainingErrors > MAX_TOLLERATED_TRAINING_ERRROS) {
					
					throw new RuntimeException("Too much training errors! Latest: " + e.getMessage());
				}
			}
        }
		
		instancesSeen += 1;
		//if (instancesSeen % gracePerionOption.getValue() == 0) {
			topK = topK(historyTotal, activeClassifiers);
		//}
		
		if (instancesSeen % gracePerionOption.getValue() == 0) {
			for (Integer i : bottomK(historyTotal, activeClassifiers)) {
				System.out.println("Dropping: " + ensemble[i].getClass().getName());
				try {
					this.ensemble[i] = (Classifier) getRamdonClassifier().newInstance();
					System.out.println("Adding: " + ensemble[i].getClass().getName());
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					throw new RuntimeException(e.getMessage());
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					throw new RuntimeException(e.getMessage()); 
				}
				
			}
		}
	}
	
	protected static List<Integer> topK(double[] scores, int k) {
		double[] scoresWorking = Arrays.copyOf(scores, scores.length);
		
		List<Integer> topK = new ArrayList<Integer>();
		
		for (int i = 0; i < k; ++i) {
			int bestIdx = maxIndex(scoresWorking);
			topK.add(bestIdx);
			scoresWorking[bestIdx] = -1;
		}
		
		return topK;
	}
	
	protected static List<Integer> bottomK(double[] scores, int k) {
		double[] scoresWorking = Arrays.copyOf(scores, scores.length);
		
		List<Integer> bottomK = new ArrayList<Integer>();
		
		for (int i = 0; i < k; ++i) {
			int bestIdx = minIndex(scoresWorking);
			bottomK.add(bestIdx);
			scoresWorking[bestIdx] = Integer.MAX_VALUE;
		}
		
		return bottomK;
	}
	
	protected static int maxIndex(double[] scores) {
		int bestIdx = 0;
		for (int i = 1; i < scores.length; ++i) {
			if (scores[i] > scores[bestIdx]) {
				bestIdx = i;
			}
		}
		return bestIdx;
	}
	
	protected static int minIndex(double[] scores) {
		int bestIdx = 0;
		for (int i = 1; i < scores.length; ++i) {
			if (scores[i] < scores[bestIdx]) {
				bestIdx = i;
			}
		}
		return bestIdx;
	}
	
	protected static double[] normalize(double[] input) {
		double sum = 0.0;
		for (int i = 0; i < input.length; ++i) {
			sum += input[i];
		}
		for (int i = 0; i < input.length; ++i) {
			input[i] /= sum;
		}
		return input;
	}
}
