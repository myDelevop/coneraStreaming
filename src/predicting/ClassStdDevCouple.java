package predicting;


import java.util.Collection;

import data.dataInstance.Edge;
import data.dataInstance.Neighbourhood;
import data.dataInstance.Node;
import data.schema.ContinuousAttribute;
import data.schema.WeightI;

public class ClassStdDevCouple extends ReliabilityCouple {
	
	protected ContinuousAttribute target;
	protected WeightI weight;
	protected Collection<Double> bins;
	
	

	ClassStdDevCouple(int i, Node n, Neighbourhood nh, ContinuousAttribute target, WeightI weight) {
		super(i, n, nh);
		this.target=target;
		this.weight=weight;
		bins=target.discreteBins();
	}

	
	protected Object determineValue(Node to){
		Object predictedClass;
	
		if(to.isSample() || to.isWorkingToSample()){
			double continuousTarget=(Double)(to.getTarget().getValue()); // è un double
			predictedClass=ContinuousAttribute.discreteValue(continuousTarget,bins);
			
			//predictedClass=target.discreteValue(continuousTarget);
		}
		else
		{
			predictedClass=to.getPredictedTarget().getValue(); // dovrebbe essere una stringa
			//predictedClass=ContinuousAttribute.discreteValue((double)predictedClass,bins);
		}
		
		return predictedClass;
	}
	
	
	@Override
	void computeReliability() {
		// TODO Auto-generated method stub
		Object nClass=determineValue(n); //n.getPredictedTarget().getValue(); // dovrebbe essere una stringa
		
		
		
		//		System.out.println("Evaluate "+ n.getId()+ " with " +nh.size()+ "neighbours");
		double countErrors=0;
		double size=0;
		for(Edge e: super.nh){
			Node to = e.getTo();
			double w=weight.getWeight(e.getDistance());
			size+=w;
			Object predictedClass=determineValue(to);
			/*if(to.isSample() || to.isWorkingToSample()){
				double continuousTarget=(Double)(to.getTarget().getValue()); // è un double
				predictedClass=target.discreteValue(continuousTarget);
			}
			else
			{
				predictedClass=to.getPredictedTarget().getValue(); // dovrebbe essere una stringa
			}*/
		//	System.out.println(nClass + "\t" +predictedClass);
			if(!nClass.toString().contains("_")) throw new RuntimeException("Error in generiting the class!");
			if(!predictedClass.toString().contains("_")) throw new RuntimeException("Error in generiting the class!");
			
			if(!nClass.equals(predictedClass))
				countErrors+=w;
		}
		this.reliability=countErrors/size;

	}

}
