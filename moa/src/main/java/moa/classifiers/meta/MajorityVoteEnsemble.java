package moa.classifiers.meta;

import moa.classifiers.AbstractClassifier;
import moa.classifiers.Classifier;
import moa.core.Measurement;
import moa.core.ObjectRepository;
import moa.options.ClassOption;
import moa.options.ListOption;
import moa.options.Option;
import moa.tasks.TaskMonitor;
import weka.core.Instance;

public class MajorityVoteEnsemble extends AbstractClassifier {
	
	private static final long serialVersionUID = -1259164191416754435L;
	
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
	
	protected Classifier[] ensemble;
    
    protected Integer instancesSeen;

	@Override
	public void resetLearningImpl() {
        this.instancesSeen = 0;
        
        for (int i = 0; i < this.ensemble.length; i++) {
            this.ensemble[i].resetLearning();
        }
	}
	
	@Override
	public double[] getVotesForInstance(Instance inst) {
		double[] votes = new double[inst.classAttribute().numValues()];
		
		for (int i = 0; i < ensemble.length; ++i) {
			double[] memberVotes = normalize(ensemble[i].getVotesForInstance(inst));
			
			votes[maxIndex(memberVotes)] += 1.0;
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
    public void prepareForUseImpl(TaskMonitor monitor,
            ObjectRepository repository) {
		
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
            this.ensemble[i].trainOnInstance(inst);
        }
		
		instancesSeen += 1;
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
