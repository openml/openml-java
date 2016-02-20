package moa.classifiers.meta;

import weka.core.Instance;
import moa.options.FloatOption;

public class BlastDecayed extends BlastAbstract {
	
	private static final long serialVersionUID = 1L;

	public FloatOption alphaOption = new FloatOption(
            "alpha",
            'a',
            "The fading factor.",
            0.99, 0, 1);

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
	public void trainOnInstanceImpl(Instance inst) {
		
		for (int i = 0; i < this.ensemble.length; i++) {
			
			// Online Performance estimation
			double[] votes = ensemble[i].getVotesForInstance(inst);
			boolean correct = (maxIndex(votes) * 1.0 == inst.classValue());
			
			historyTotal[i] = historyTotal[i] * alphaOption.getValue();
			if (correct) {
				historyTotal[i] += 1 - alphaOption.getValue();
			}
			
            this.ensemble[i].trainOnInstance(inst);
        }
		
		instancesSeen += 1;
		if (instancesSeen % gracePerionOption.getValue() == 0) {
			topK = topK(historyTotal, activeClassifiersOption.getValue());
		}
	}
}
