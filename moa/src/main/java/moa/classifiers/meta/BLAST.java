package moa.classifiers.meta;

import weka.core.Instance;
import moa.classifiers.AbstractClassifier;
import moa.classifiers.Classifier;
import moa.core.Measurement;
import moa.core.ObjectRepository;
import moa.options.ClassOption;
import moa.options.IntOption;
import moa.options.ListOption;
import moa.options.Option;
import moa.tasks.TaskMonitor;

public class BLAST extends AbstractClassifier {
	
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

	public IntOption windowSizeOption = new IntOption(
            "windowSize",
            'w',
            "The window size over which Online Performance Estimation is done.",
            1000, 1, Integer.MAX_VALUE);
	
	protected Classifier[] ensemble;

    protected boolean[][] onlineHistory;
    
    protected double[] historyTotal;
    
    protected Integer wValue;
    
    protected Integer instancesSeen;
	
	@Override
	public double[] getVotesForInstance(Instance inst) {
		return ensemble[maxIndex(historyTotal)].getVotesForInstance(inst);
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
        this.onlineHistory = new boolean[this.ensemble.length][wValue];
        this.instancesSeen = 0;
        
        for (int i = 0; i < this.ensemble.length; i++) {
            this.ensemble[i].resetLearning();
        }
	}
	
	@Override
    public void prepareForUseImpl(TaskMonitor monitor,
            ObjectRepository repository) {
		wValue = windowSizeOption.getValue();
		
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
}
