package moa.classifiers.meta;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import moa.classifiers.AbstractClassifier;
import moa.classifiers.Classifier;
import moa.core.InstancesHeader;
import moa.core.Measurement;
import moa.core.ObjectRepository;
import moa.options.ClassOption;
import moa.options.FlagOption;
import moa.options.ListOption;
import moa.options.Option;
import moa.tasks.TaskMonitor;

public class StackingAttemptV2 extends AbstractClassifier {
	
	private static final long serialVersionUID = 1L;
	
	public ListOption baselearnersOption = new ListOption(
			"baseClassifiers", 'b', "The classifiers the ensemble consists of.", 
			new ClassOption("learner", ' ', "", Classifier.class, "trees.HoeffdingTree"), 
			new Option[]{
                new ClassOption("", ' ', "", Classifier.class, "bayes.NaiveBayes"), 
                new ClassOption("", ' ', "", Classifier.class, "functions.Perceptron"),
                new ClassOption("", ' ', "", Classifier.class, "functions.SGD"),
                new ClassOption("", ' ', "", Classifier.class, "functions.SPegasos"),
                new ClassOption("", ' ', "", Classifier.class, "lazy.kNN"),
                new ClassOption("", ' ', "", Classifier.class, "rules.RuleClassifier"),
                new ClassOption("", ' ', "", Classifier.class, "trees.HoeffdingAdaptiveTree"),
                new ClassOption("", ' ', "", Classifier.class, "trees.HoeffdingTree"),
                new ClassOption("", ' ', "", Classifier.class, "trees.HoeffdingOptionTree"),
                new ClassOption("", ' ', "", Classifier.class, "trees.RandomHoeffdingTree")
            },',');
	
	public ClassOption metaLearnerOption = new ClassOption(
			"metalearner", 
			'm', 
			"The meta-learner learning how to weight the votes", 
			Classifier.class, "trees.HoeffdingTree");
	
	public FlagOption cascadeOption = new FlagOption(
			"cascade", 
			'c',
			"Also passes on the base-features to the meta-classifier");
	
	public FlagOption hardvotesOption = new FlagOption(
			"hardvotes", 
			'h',
			"Uses hard votes instead of probabilities per classifier. ");
	
	protected Classifier[] ensemble;
	
	protected Classifier metaClassifier;
    
    protected Integer instancesSeen;
    
    protected Instances metaInstances;
    
    protected Integer baseClassIdx = -1;
	
	@Override
	public double[] getVotesForInstance(Instance inst) {
		double[] classes = new double[inst.numClasses()];
		double[] memberVotes = new double[ensemble.length + 1];
		
		for (int i = 0; i < ensemble.length; ++i) {
			double[] myVotes = normalize(ensemble[i].getVotesForInstance(inst));
			int currentVote = maxIndex(myVotes);
			
			if (hardvotesOption.isSet() || myVotes.length == 0) {
				memberVotes[i] = currentVote;
				classes[currentVote] += 1;
			} else {
				memberVotes[i] = 1 - myVotes[0];
				classes[memberVotes[i] > .5 ? 1 : 0] += 1;
			}
		}
		double[] metaInstanceValues;
		if (cascadeOption.isSet()) {
			double[] baseInstance = ArrayUtils.remove(inst.toDoubleArray(), baseClassIdx);
			metaInstanceValues = ArrayUtils.addAll(baseInstance, memberVotes);
		} else {
			metaInstanceValues = memberVotes;
		}
		
		Instance metaInstance = new DenseInstance(1.0, metaInstanceValues);
		metaInstance.setDataset(metaInstances);
		
		// TODO: do better
		return metaClassifier.getVotesForInstance(metaInstance);
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
	public void resetLearningImpl() {
		this.instancesSeen = 0;
        metaClassifier.resetLearning();
        for (int i = 0; i < this.ensemble.length; i++) {
            this.ensemble[i].resetLearning();
        }
	}

	@Override
	protected Measurement[] getModelMeasurementsImpl() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
    public void prepareForUseImpl(TaskMonitor monitor,
            ObjectRepository repository) {
		metaClassifier = (Classifier) metaLearnerOption.materializeObject(monitor, repository);
		metaClassifier.prepareForUse(monitor, repository);
		
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
    public void setModelContext(InstancesHeader ih) {
        super.setModelContext(ih);
        if(ih.numClasses() > 2) throw new RuntimeException("Only binary cases supported.");
        
		ArrayList<Attribute> metaInstancesAttributes = new ArrayList<Attribute>();
		List<String> values = new ArrayList<String>();
		values.add("0");
		values.add("1");
		
		// TODO: cascade stuff!
		
		Option[] learnerOptions = this.baselearnersOption.getList();
		baseClassIdx = ih.classIndex();
		
		if (cascadeOption.isSet()) {
			for (int i = 0; i < ih.numAttributes(); ++i) {
				if (i != baseClassIdx) { 
					metaInstancesAttributes.add(ih.attribute(i));
				}
			}
		}
		
		for (int i = 0; i < learnerOptions.length; i++) {
			if (hardvotesOption.isSet()) {
				metaInstancesAttributes.add(new Attribute("classifier" + i, values));
			} else {
				metaInstancesAttributes.add(new Attribute("classifier" + i));
			}
		}
		metaInstancesAttributes.add(new Attribute("class", values));
		
        metaInstances = new Instances("meta", metaInstancesAttributes, 0);
        metaInstances.setClassIndex(metaInstances.numAttributes() - 1);
    }
    
	@Override
	public void trainOnInstanceImpl(Instance inst) {
		double[] memberVotes = new double[ensemble.length + 1];
		double[] classes = new double[inst.numClasses()];
		
		for (int i = 0; i < this.ensemble.length; i++) {
			double[] myVotes = normalize(ensemble[i].getVotesForInstance(inst));
			int currentVote = maxIndex(myVotes);
			
			if (hardvotesOption.isSet() || myVotes.length == 0) {
				memberVotes[i] = currentVote;
				classes[currentVote] += 1;
			} else {
				memberVotes[i] = 1 - myVotes[0];
				classes[memberVotes[i] > .5 ? 1 : 0] += 1;
			}
			
			this.ensemble[i].trainOnInstance(inst);
        }
		int majorityClass = maxIndex(classes);
		memberVotes[ensemble.length] = ((int) inst.classValue()) == 1 && majorityClass == 1 ? 1.0 : 0.0;
		
		double[] metaInstanceValues;
		if (cascadeOption.isSet()) {
			double[] baseInstance = ArrayUtils.remove(inst.toDoubleArray(), baseClassIdx);
			metaInstanceValues = ArrayUtils.addAll(baseInstance, memberVotes);
		} else {
			metaInstanceValues = memberVotes;
		}
		
		Instance metaInstance = new DenseInstance(1.0, metaInstanceValues);
		metaInstance.setDataset(metaInstances);
		metaClassifier.trainOnInstance(metaInstance);
		
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
