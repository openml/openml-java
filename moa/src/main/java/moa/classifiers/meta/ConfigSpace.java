package moa.classifiers.meta;

import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import com.github.javacliparser.FlagOption;
import com.github.javacliparser.Option;

import moa.classifiers.Classifier;
import moa.classifiers.trees.HoeffdingTree;

public class ConfigSpace {
	
	protected final Classifier classifier;
	protected final Map<String, Hyperparameter> hyperparameters;
	
	public ConfigSpace(Classifier classifier) {
		if (!(classifier instanceof HoeffdingTree)) {
			throw new RuntimeException("Classifier type not recognized. ");
		}
		this.classifier = classifier;
		hyperparameters = new TreeMap<String, ConfigSpace.Hyperparameter>();
	}
	
	public void addHyperparameter(String name, Hyperparameter hyperparameter) {
		hyperparameters.put(name, hyperparameter);
	}
	
	public String sampleConfigurationCliString(Random random) {
		// TODO: huge hack!!
		String cliString = "trees." + classifier.getCLICreationString(classifier.getClass());
		
		for (Option option : classifier.getOptions().getOptionArray()) {
			if (hyperparameters.containsKey(option.getName())) {
				
				if (option instanceof FlagOption) {
					boolean value = ((BooleanHyperparameter)hyperparameters.get(option.getName())).sampleBoolean(random);
					if (value) {
						cliString += " -" + option.getCLIChar();
					}
				} else {
					cliString += " -" + option.getCLIChar() + " " + hyperparameters.get(option.getName()).sample(random);
				}
			}
		}
		
		return cliString;
	}
	
	
	static interface HyperparameterInterface {
		public String sample(Random random);
	}
	
	static abstract class Hyperparameter implements HyperparameterInterface {
		protected static final int logbase = 2;
		
		protected static double sampleLogscale(Random random, double min, double max) {
			double logMin = Math.log(min) / Math.log(logbase);
			double logMax =  Math.log(max) / Math.log(logbase);
			double randomValue = random.nextDouble();
			double value = logMin + (logMax - logMin) * randomValue;
			return value;
		}
	}
	
	static class IntHyperparameter extends Hyperparameter {
		private int min;
		private int max;
		private boolean logscale;
		
		public IntHyperparameter(int min, int max, boolean logscale) {
			if (min >= max) {
				throw new RuntimeException();
			}
			this.min = min;
			this.max = max;
			this.logscale = logscale;
		}
		
		public int sampleInt(Random random) {
			if (logscale) {
				
				double safeMin = min;
				double safeMax = max;
				if (logscale) {
					if (min == 0) {
						safeMin = 0.0001;
					}
					if (max == 0) {
						safeMax = -0.0001;
					}
				}
				
				return (int) Math.pow(logbase, sampleLogscale(random, safeMin, safeMax));
			} else {
				return min + random.nextInt(max - min);
			}
		}
		
		public String sample(Random random) {
			return sampleInt(random) + "";
		}
	}
	
	static class DoubleHyperparameter extends Hyperparameter {
		private double min;
		private double max;
		private boolean logscale;
		
		public DoubleHyperparameter(double min, double max, boolean logscale) {
			if (min >= max) {
				throw new RuntimeException();
			}
			if (logscale && (min == 0 || max == 0)) {
				throw new RuntimeException();
			}
			this.min = min;
			this.max = max;
			this.logscale = logscale;
		}

		public double sampleDouble(Random random) {
			if (logscale) {
				return Math.pow(logbase, sampleLogscale(random, min, max));
			} else {
				return min + (max - min) * random.nextDouble();
			}
		}
		
		public String sample(Random random) {
			return sampleDouble(random) + "";
		}
	}
	
	static class BooleanHyperparameter extends Hyperparameter {
		
		public BooleanHyperparameter() {
			
		}
		
		public boolean sampleBoolean(Random random) {
			return random.nextBoolean();
		}
		
		public String sample(Random random) {
			return sampleBoolean(random) + "";
		}
	}
	
}
