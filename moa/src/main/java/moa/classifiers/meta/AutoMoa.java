package moa.classifiers.meta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.github.javacliparser.FlagOption;

//import org.reflections.Reflections;

import com.github.javacliparser.IntOption;
import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.InstancesHeader;

import moa.classifiers.AbstractClassifier;
import moa.classifiers.Classifier;
import moa.core.Measurement;
import moa.core.ObjectRepository;
import moa.options.ClassOption;
import moa.tasks.TaskMonitor;

public class AutoMoa extends AbstractClassifier {

	private static final long serialVersionUID = 1L;

	protected Classifier[] ensemble;
	
	protected double[] historyTotal;
    
    protected Integer instancesSeen;
    
    List<Integer> topK;
    
    protected Set<Class<? extends AbstractClassifier>> allAvailableClasses;
    
    int activeClassifiers = 1;

    protected boolean[][] onlineHistory;

    public ClassOption baseclassifierOption = new ClassOption(
    		"baselearner", 'b', "The classifier to Optimize", Classifier.class, "trees.HoeffdingTree");
    
	public IntOption ensembleSizeOption = new IntOption(
            "ensembleSize",
            'n',
            "The number of base classifiers.",
            16, 1, Integer.MAX_VALUE);
    
	public IntOption windowSizeOption = new IntOption(
            "windowSize",
            'w',
            "The window size over which Online Performance Estimation is done.",
            1000, 1, Integer.MAX_VALUE);
	
	public FlagOption weightClassifiersOption = new FlagOption(
			"weightClassifiers", 
			'p',
			"Uses online performance estimation to weight the classifiers");

	public IntOption activeClassifiersOption = new IntOption(
			"activeClassifiers",
			'k',
			"The number of active classifiers (used for voting)",
			1, 1, Integer.MAX_VALUE);


	@Override
	public void resetLearningImpl() {
		this.historyTotal = new double[this.ensemble.length];
        this.onlineHistory = new boolean[this.ensemble.length][windowSizeOption.getValue()];
        this.instancesSeen = 0;
        
        for (int i = 0; i < this.ensemble.length; i++) {
            this.ensemble[i].resetLearning();
        }
	}
	
	@Override
	public void trainOnInstanceImpl(Instance inst) {
		int wValue = windowSizeOption.getValue();
		
		for (int i = 0; i < this.ensemble.length; i++) {
			
			// Online Performance estimation
			double[] votes = ensemble[i].getVotesForInstance(inst);
			boolean correct = (maxIndex(votes) * 1.0 == inst.classValue());
			
			if (correct && !onlineHistory[i][instancesSeen%wValue]) {
				// performance estimation increases
				onlineHistory[i][instancesSeen%wValue] = true;
				historyTotal[i] += 1.0 / wValue;
			} else if (!correct && onlineHistory[i][instancesSeen%wValue]) {
				// performance estimation decreases
				onlineHistory[i][instancesSeen%wValue] = false;
				historyTotal[i] -= 1.0 / wValue;
			} else {
				// nothing happens
			}
			
            this.ensemble[i].trainOnInstance(inst);
        }

		instancesSeen += 1;
		topK = topK(historyTotal, activeClassifiersOption.getValue());
		// TODO: drop half and reinitialize
	}
	
	@Override
    public String getPurposeString() {
        return "For tuning hyper-parameters in a stream setting, based on the " 
        		+ "BLAST algorithm. See also: "
        		+ "'Having a Blast: Meta-Learning and Heterogeneous Ensembles "
        		+ "for Data Streams' (ICDM 2015).";
    }

	@Override
	public double[] getVotesForInstance(Instance inst) {
		double[] votes = new double[inst.classAttribute().numValues()];
		
		for (int i = 0; i < topK.size(); ++i) {
			double[] memberVotes = normalize(ensemble[topK.get(i)].getVotesForInstance(inst));
			double weight = 1.0;
			
			if (weightClassifiersOption.isSet()) {
				weight = historyTotal[topK.get(i)];
			}
			
			// make internal classifiers so-called "hard classifiers"
			votes[maxIndex(memberVotes)] += 1.0 * weight;
		}
		
		return votes;
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
		// Auto-generated method stub
		
	}

	@Override
	protected Measurement[] getModelMeasurementsImpl() {
		// Auto-generated method stub
		return null;
	}
	
	@Override
    public void prepareForUseImpl(TaskMonitor monitor, ObjectRepository repository) {
		
        this.ensemble = new Classifier[ensembleSizeOption.getValue()];
        for (int i = 0; i < ensembleSizeOption.getValue(); i++) {
        	monitor.setCurrentActivity("Materializing learner " + (i + 1) + "...", -1.0);
        	// TODO: vary param settings
            this.ensemble[i] = (Classifier) ((ClassOption) baseclassifierOption).materializeObject(monitor, repository);
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
        
        topK = topK(historyTotal, activeClassifiersOption.getValue());
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
	
	protected static int maxIndex(double[] scores) {
		int bestIdx = 0;
		for (int i = 1; i < scores.length; ++i) {
			if (scores[i] > scores[bestIdx]) {
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
