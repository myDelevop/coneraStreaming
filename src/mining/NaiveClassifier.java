package mining;

import java.util.List;

import data.dataInstance.Node;
import weka.core.Instances;

public class NaiveClassifier extends Classifier {

	public NaiveClassifier(Instances trainingSet, Instances workingSet, List<Node> workingN) throws Exception {
		super(trainingSet, workingSet, workingN);
		// TODO Auto-generated constructor stub
		setConfig();
		mining();
	}

	@Override
	protected void setConfig() throws Exception {
		myClassifier= new weka.classifiers.bayes.NaiveBayes();
		//myClassifier.setOptions(new String("-K;-O").split(";"));
		myClassifier.setOptions(new String("-O").split(";"));
		
	}

}
