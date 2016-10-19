package cf;

import weka.core.Instances;

public class FixedSampler extends Sampler {

	
	protected int size(Instances data){
		
		return (int)samplePerc;
	}

	FixedSampler(String baseArff, String config, float samplePerc, int n) {
		super(baseArff, config, samplePerc, n);
		
	}

	
	public static void main(String[] args) {
		//movies2.arffTrain_1.arff configMovieEpured.ini .01 5
		FixedSampler s=new FixedSampler(args[0], args[1], new Float(args[2]), new Integer(args[3]));
		s.sample();

		
	
	

	}

}
