package mining;

import java.util.Iterator;
import java.util.List;


import data.dataInstance.DiscreteValue;
import data.dataInstance.Node;
import weka.core.Instance;
import weka.core.Instances;

public abstract class Classifier extends Predictor {
	
	boolean singleClassValue=false; // l classificato predice un'unica classe

	Classifier(Instances trainingSet, Instances workingSet, List<Node> workingN) {
		super(trainingSet, workingSet, workingN);
		// TODO Auto-generated constructor stub
	}


	
	protected void classify() throws Exception {
		
		
		if(!singleClassValue){
			Iterator<Node> workingNodesIt=workingNodes.iterator();
			
			for (int i = 0; i < this.workingSet.numInstances(); i++) {
			      Instance curr =  this.workingSet.instance(i);
			    //  System.out.println(curr.numClasses());
			      Object pred = myClassifier.classifyInstance(curr);
			      curr.setClassValue((Double)pred);
			      String predictedClass=curr.stringValue(curr.classIndex());
			      Node workingNode=workingNodesIt.next();
			      //workingNode.setPredictedTarget(new ContinuousValue(pred));
			      workingNode.setPredictedTarget(new DiscreteValue(predictedClass));
			      ClassifiedCouple cc = new ClassifiedCouple(workingNodes.get(i),pred);
			 
			}
		}
		else{
Iterator<Node> workingNodesIt=workingNodes.iterator();
			
			for (int i = 0; i < this.workingSet.numInstances(); i++) {
			     Instance curr =  this.workingSet.instance(i);
				    //  System.out.println(curr.numClasses());
				      Object pred = trainingSet.instance(0).classValue();
				      curr.setClassValue((Double)pred);
				      String predictedClass=curr.stringValue(curr.classIndex());
				      Node workingNode=workingNodesIt.next();
				      //workingNode.setPredictedTarget(new ContinuousValue(pred));
				      workingNode.setPredictedTarget(new DiscreteValue(predictedClass));
				      ClassifiedCouple cc = new ClassifiedCouple(workingNodes.get(i),pred);
				 
			 	}
		}
	}
	
	protected void learnClassifier() throws Exception {
		// TODO Auto-generated method stub
		singleClassValue=false;
		try{
			super.learnClassifier();
		}
		catch (weka.core.UnsupportedAttributeTypeException e){
			 if(!e.toString().contains("Cannot handle unary class!"))
				 throw e; // propago l'eccezione
			 else
				 //il classifcatore è appreso da un training set con un'unica classe
				singleClassValue=true;
		}
		//System.out.println(myClassifier);
			
	}
	




}
