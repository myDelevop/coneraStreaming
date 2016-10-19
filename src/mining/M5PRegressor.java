package mining;

import java.util.List;


import data.dataInstance.Node;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.M5P;
import weka.core.Instances;

public class M5PRegressor extends Regressor{
	
	public M5PRegressor(Instances trainingSet, Instances workingSet, List<Node> workingNodes) throws Exception{
		super(trainingSet,workingSet,workingNodes);
			
		this.myClassifier = new M5P();
		setConfig();
		mining();
				
		
	}


	@Override
	protected void setConfig() throws Exception {
		// 
		String[] options = {"-M",String.valueOf(Math.sqrt(trainingSet.numInstances()))};
		myClassifier.setOptions(options);
				
		
	}
}
