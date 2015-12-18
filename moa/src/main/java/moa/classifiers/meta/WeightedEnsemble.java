package moa.classifiers.meta;

import weka.core.Instance;
import moa.classifiers.AbstractClassifier;
import moa.classifiers.Classifier;
import moa.core.Measurement;
import moa.core.ObjectRepository;
import moa.options.ClassOption;
import moa.options.FloatOption;
import moa.options.ListOption;
import moa.options.Option;
import moa.tasks.TaskMonitor;

public class WeightedEnsemble extends AbstractClassifier {
	
	private static final long serialVersionUID = 1L;
	
	public ListOption baselearnersOption = new ListOption(
			"baseClassifiers", 'b', "The classifiers the ensemble consists of.", 
			new ClassOption("learner", ' ', "", Classifier.class, "trees.HoeffdingTree"), 
			new Option[]{
                new ClassOption("", ' ', "", Classifier.class, "bayes.NaiveBayes"), 
                new ClassOption("", ' ', "", Classifier.class, "functions.Perceptron"),
                new ClassOption("", ' ', "", Classifier.class, "functions.SGD"),
                new ClassOption("", ' ', "", Classifier.class, "lazy.kNN"),
                new ClassOption("", ' ', "", Classifier.class, "trees.HoeffdingTree")},
           ',');

	public FloatOption alphaOption = new FloatOption(
            "alpha",
            'a',
            "The fading factor.",
            0.99, 0, 1);
	
	protected Classifier[] ensemble;
    
    protected double[] historyTotal;
    
    protected double aValue;
    
    protected Integer instancesSeen;
	
	@Override
	public double[] getVotesForInstance(Instance inst) {
		double[] votes = new double[inst.classAttribute().numValues()];
	//	System.out.println(inst.classAttribute());
		
		for (int i = 0; i < ensemble.length; ++i) {
			double[] memberVotes = normalize(ensemble[i].getVotesForInstance(inst));
	//		System.out.println(ensemble[i].getCLICreationString(Classifier.class) + " - " +  Arrays.toString(memberVotes));
			
			if (memberVotes.length <= votes.length) {
				for (int j = 0; j < memberVotes.length; ++j) {
					votes[j] += memberVotes[j] * historyTotal[i];
				}
			}
		}
		
		return votes;
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

	@Override
	public void resetLearningImpl() {
		this.historyTotal = new double[this.ensemble.length];
		for (int i = 0; i < this.ensemble.length; ++i) {
			this.historyTotal[i] = 1.0;
		}
		
        this.instancesSeen = 0;
        
        for (int i = 0; i < this.ensemble.length; i++) {
            this.ensemble[i].resetLearning();
        }
	}
	
	@Override
    public void prepareForUseImpl(TaskMonitor monitor,
            ObjectRepository repository) {
		aValue = alphaOption.getValue();
		
        Option[] learnerOptions = this.baselearnersOption.getList();
        this.ensemble = new Classifier[learnerOptions.length];
        for (int i = 0; i < learnerOptions.length; i++) {
        	monitor.setCurrentActivity("Materializing learner " + (i + 1)
                    + "...", -1.0);
            this.ensemble[i] = (Classifier) ((ClassOption) learnerOptions[i]).materializeObject(monitor, repository);
            if (monitor.taskShouldAbort()) {
                return;
            }
            monitor.setCurrentActivity("Preparing learner " + (i + 1) + "...",
                    -1.0);
            this.ensemble[i].prepareForUse(monitor, repository);
            if (monitor.taskShouldAbort()) {
                return;
            }
        }
        super.prepareForUseImpl(monitor, repository);
    }

	@Override
	public void trainOnInstanceImpl(Instance inst) {
		
		for (int i = 0; i < this.ensemble.length; i++) {
			
			// Online Performance estimation
			double[] votes = ensemble[i].getVotesForInstance(inst);
			boolean correct = (maxIndex(votes) * 1.0 == inst.classValue());
			
			historyTotal[i] = historyTotal[i] * aValue;
			if (correct) {
				historyTotal[i] += 1;
			}
			
            this.ensemble[i].trainOnInstance(inst);
        }
		
		instancesSeen += 1;
	}
	
	private int maxIndex(double[] scores) {
		int bestIdx = 0;
		for (int i = 1; i < scores.length; ++i) {
			if (scores[i] > scores[bestIdx]) {
				bestIdx = i;
			}
		}
		return bestIdx;
	}
	
	private double[] normalize(double[] input) {
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
