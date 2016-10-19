package mining;

import java.util.Iterator;
import java.util.List;

import data.dataInstance.ContinuousValue;
import data.dataInstance.Node;
import data.dataInstance.Value;
import weka.core.Instance;
import weka.core.Instances;

public abstract class Regressor extends Predictor {

	Regressor(Instances trainingSet, Instances workingSet, List<Node> workingN) {
		super(trainingSet, workingSet, workingN);
		// TODO Auto-generated constructor stub
	}

	protected void classify() throws Exception {
		Iterator<Node> workingNodesIt=workingNodes.iterator();
		
		for (int i = 0; i < this.workingSet.numInstances(); i++) {
		      Instance curr =  this.workingSet.instance(i);
		    //  System.out.println(curr.numClasses());
		      Object pred = myClassifier.classifyInstance(curr);
		      Node workingNode=workingNodesIt.next();
		     // System.out.println(workingNode+ "newY"+pred);
		      workingNode.setPreviousPredictedTarget(workingNode.getPredictedTarget());
		      workingNode.setPredictedTarget(new ContinuousValue(pred));
		     
		      ClassifiedCouple cc = new ClassifiedCouple(workingNodes.get(i),pred);
		 //     this.workingPredictionMap.put(cc.getNode(),cc);
		}
	}
	

}
