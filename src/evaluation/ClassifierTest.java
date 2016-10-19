package evaluation;

import java.util.Iterator;
import java.util.List;

import data.dataInstance.ContinuousValue;
import data.dataInstance.Node;
import data.network.Network;
import mining.ClassifiedCouple;
import weka.core.Instance;
import weka.core.Instances;

class ClassifierTest {
	private Network n;
	
	private  weka.classifiers.Classifier myClassifier;
	
	private Instances instanceSet;
	private List<Node> nodes;
	
	ClassifierTest(weka.classifiers.Classifier myClassifier, Instances instanceSet, List<Node> nodes){
		this.n=n;
		this.myClassifier=myClassifier;
		this.instanceSet=instanceSet;
		this.nodes=nodes;
	}

	/* 
	 * predice nodi del network
	 */
	void classify() throws Exception {
		Iterator<Node> nodesIt=nodes.iterator();
		
		for (int i = 0; i < this.instanceSet.numInstances(); i++) {
		      Instance curr =  this.instanceSet.instance(i);
		      double pred = myClassifier.classifyInstance(curr);
		      Node node=nodesIt.next();
		      node.setPredictedTarget(new ContinuousValue(pred));
		     
		}
	}
}
