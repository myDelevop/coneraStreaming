package mining;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import data.dataInstance.ContinuousValue;
import data.dataInstance.Node;
import weka.classifiers.Evaluation;
import weka.core.Instance;
import weka.core.Instances;

public abstract class Predictor implements Iterable<Node>{
	protected Instances trainingSet;
	protected Instances workingSet;
	protected List<Node> workingNodes;
	protected weka.classifiers.Classifier myClassifier;
	
	
	//private Map<Node,ClassifiedCouple> workingPredictionMap=new HashMap<Node,ClassifiedCouple>();
	
	Predictor(Instances trainingSet,Instances workingSet, List<Node> workingN){
		this.trainingSet = trainingSet;
		this.workingSet = workingSet;
		this.workingNodes = workingN;
		this.myClassifier = null;
	//	this.workingPredictionMap=new HashMap<Node,ClassifiedCouple>();
	}
	/**
	 * 
	 * @param myClassifier
	 * @param workingSet
	 * @param workingN
	 * 
	 * for the testing phase
	 */
	

	
	protected void learnClassifier() throws Exception {
		// TODO Auto-generated method stub
			this.myClassifier.buildClassifier(this.trainingSet);
		//System.out.println(myClassifier);
			
	}
	
	protected abstract void classify() throws Exception;


	
	public Iterator<Node>iterator(){
		return workingNodes.iterator();
	}
	public void serialize(String filename) throws IOException {
		filename=filename.replace(".","_");
		FileOutputStream outFile = new FileOutputStream(filename+".classifier");
		ObjectOutputStream outStream = new ObjectOutputStream(outFile);
		outStream.writeObject(myClassifier);
		outStream.close();
	}
	
	static public weka.classifiers.Classifier load(String fileName) throws IOException,ClassNotFoundException {
		weka.classifiers.Classifier classifier;
		FileInputStream inFile= new FileInputStream(fileName+".classifier");
		ObjectInputStream inStream =new ObjectInputStream(inFile);
		
		classifier=(weka.classifiers.Classifier)(inStream.readObject());
		inStream.close();
		
		
		return classifier;
	}
	abstract protected void setConfig() throws Exception;
	
	
	
	protected void mining() throws Exception{
		//train
				learnClassifier();
				
			//	System.out.println(this.myClassifier);
				
				//predict
				classify();
				
				/*Evaluation eval = new Evaluation(trainingSet); 
				eval.evaluateModel(myClassifier, trainingSet); 
				System.out.println(eval.toSummaryString());*/
	}
	
}
