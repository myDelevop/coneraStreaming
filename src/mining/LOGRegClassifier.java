package mining;

import java.util.List;

import data.dataInstance.Node;
import weka.classifiers.functions.Logistic;
import weka.core.Instances;

public class LOGRegClassifier extends Classifier {

	public LOGRegClassifier(Instances trainingSet, Instances workingSet, List<Node> workingN) throws Exception {
		super(trainingSet, workingSet, workingN);

		myClassifier=new Logistic();
		setConfig();
		mining();
	}

	@Override
	protected void setConfig() throws Exception {
	//	myClassifier.setOptions(new String("-I 10;-M 10").split(";"));

	}

}
