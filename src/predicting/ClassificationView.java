package predicting;

import java.io.IOException;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import data.dataInstance.Graph;
import data.dataInstance.Neighbourhood;
import data.dataInstance.Node;
import data.network.Network;
import data.schema.ContinuousAttribute;
import data.schema.DISCRETIZATIONTYPE;
import distance.DistanceMap;
import mining.CLASSIFIER_TYPE;
import mining.LOGRegClassifier;
import mining.Predictor;

import mining.SMORegressor;
import mining.M5PRegressor;
import mining.NaiveClassifier;
import mining.SVMClassifier;
import weka.core.Instance;
import weka.core.Instances;

public class ClassificationView {
	private Instances trainingSet;
	private Instances workingSet;
	private Graph graph;

	private List<Node> workingNodes;



	private Predictor c = null;
	
	private DistanceMap distances;
	

	
	// for learning phase

	public ClassificationView(Network network,Instances ts, Instances ws, List<Node> wn) throws Exception{
		graph = network.getGraph();
		trainingSet = ts;
		workingSet = ws;
		workingNodes = wn;
	
		
	}
	public ClassificationView(Network network,Instances ts, Instances ws, List<Node> wn, DistanceMap d) throws Exception{
		this(network,ts,ws,wn);
		distances=d;
		
	}
	
	public void classify(CLASSIFIER_TYPE classifierType) throws Exception{
		if(trainingSet==null) throw new Exception("No training set is defined!");;
		
		//System.out.println(trainingSet);
	
		switch (classifierType) {
    	case M5P:
    		System.out.println("Learning from "+trainingSet.numInstances() + " training examples with "+ workingSet.numInstances() + " working examples of " +workingNodes.size()+ " nodes!");
    		c = new M5PRegressor(trainingSet,workingSet,workingNodes); ///1.1
    		
    		break;    	
    	case SMOReg:
    		System.out.println("Learning from "+trainingSet.numInstances() + " training examples with "+ workingSet.numInstances() + " working examples of " +workingNodes.size()+ " nodes!");
    		c = new SMORegressor(trainingSet,workingSet,workingNodes); ///1.1
    		break;
    	case SVM:			
			System.out.println("Learning from "+trainingSet.numInstances() + " training examples with "+ workingSet.numInstances() + " working examples of " +workingNodes.size()+ " nodes!");
    		c = new SVMClassifier(trainingSet,workingSet,workingNodes); ///1.1
    		break;
    	case NB:			
			System.out.println("Learning from "+trainingSet.numInstances() + " training examples with "+ workingSet.numInstances() + " working examples of " +workingNodes.size()+ " nodes!");
    		c = new NaiveClassifier(trainingSet,workingSet,workingNodes); ///1.1//new SVMClassifier(trainingSet,workingSet,workingNodes); ///1.1
    		break;
    	case LOGReg:
    		System.out.println("Learning from "+trainingSet.numInstances() + " training examples with "+ workingSet.numInstances() + " working examples of " +workingNodes.size()+ " nodes!");
    		c = new LOGRegClassifier(trainingSet,workingSet,workingNodes); ///1.1//new SVMClassifier(trainingSet,workingSet,workingNodes); ///1.1
    		break;
		}
	
	
	}
	
	 public Set<ReliabilityCouple>  computeReliability(RELIABILITY_TYPE rt, int indexNh, boolean isDistanceWeight){
		 Set<ReliabilityCouple> reliabilitySet = new TreeSet<ReliabilityCouple>();
		int i=0;
		double sum[]=null;
		if(rt==RELIABILITY_TYPE.GO || rt==RELIABILITY_TYPE.MORAN)
			sum=graph.getSumsY();
		for(Node n:workingNodes)
		{
				Neighbourhood nh = graph.getNeighbourhood(n,indexNh);
				ReliabilityCouple r = null;			
	
				//to check ideal case
				if(!rt.equals(RELIABILITY_TYPE.NONE))
				
					//
					//December 2015
					if(isDistanceWeight)
						r=new CollectiveDescriptiveDALCouple(i, n, nh, graph.getSchema().getTarget(),graph.getWeight(),distances, graph.getTrainingNodes());
					else
						r=new CollectiveDescriptiveCouple(i, n, nh, graph.getSchema().getTarget(),graph.getWeight());
				
				else
					r=new DefaultReliabilityCouple(i, n, nh);
				r.computeReliability();
				reliabilitySet.add(r);
		/*
				
				if(nh.size()>0)
				{
					r.computeReliability();
					reliabilitySet.add(r);
				}
				else 
				{
					r.defaultReliability();
					reliabilitySet.add(r);
				}*/
			
			i++;
		}
		return reliabilitySet;
	}

	public void addTrainingInstances(Set<Integer> indexes){
		//aggiunge a trainingSet gli elementi di indice idexes di workingSet
		for(Integer i : indexes){
			
			//November 25, rimosso perchè tanto ricreo training set
			/*Instance inst = workingSet.instance(i);
			inst.setValue(trainingSet.numAttributes()-1, (double)workingNodes.get(i).getTarget().getValue());
			trainingSet.add(inst);
			*/
			Node n=workingNodes.get(i);
			
			n.setWorkingToSample(true);
			ContinuousAttribute target=((ContinuousAttribute)(graph.getSchema().getTarget()));
			{
				target.updateMin((double)(n.getTarget().getValue()));
				target.updateMax((double)(n.getTarget().getValue()));
			}
			if(target.getDiscretizationAlgorithm().equals(DISCRETIZATIONTYPE.EF)){
					target.updateDomain((double)n.getTarget().getValue());
				}
				
			n.setPredictedTarget(n.getTarget());
			
			
		}
	}
	
	
	public void modifyTrainingInstances(Set<Integer> indexes){
		//aggiunge a trainingSet gli elementi di indice idexes di workingSet
		for(Integer i : indexes){
			
			Instance inst = workingSet.instance(i);
			inst.setValue(trainingSet.numAttributes()-1, (double)workingNodes.get(i).getTarget().getValue());
			trainingSet.add(inst);
			
			Node n=workingNodes.get(i);
			
			n.setWorkingToSample(true);
			ContinuousAttribute target=((ContinuousAttribute)(graph.getSchema().getTarget()));
			{
				target.updateMin((double)(n.getTarget().getValue()));
				target.updateMax((double)(n.getTarget().getValue()));
			}
			if(target.getDiscretizationAlgorithm().equals(DISCRETIZATIONTYPE.EF)){
					target.updateDomain((double)n.getTarget().getValue());
				}
			//System.out.println("Y:"+n.getTarget()+"CY"+n.getPredictedTarget()+"DY"+n.getPreviousPredictedTarget());
			n.setPredictedTarget(n.getTarget());
			
			
		}
	}
		

	
	public void deleteWorkingInstances(Set<Integer> indexes){
		//aggiunge a trainingSet gli elementi di indice idexes di workingSet
		
		int removed=0;
		for(Integer i : indexes){
			workingSet.delete(i-removed);
			workingNodes.remove(i-removed);
			removed++;
			
		}
		
		
	}
	


	
	public void serializeClassifier(String filename) throws IOException {
		c.serialize(filename);
	}
	
	public int getTrainingSize(){
		return trainingSet.numInstances();
	}
	
	public void setTrainingSet(Instances train){
		trainingSet=train;
	}
}
