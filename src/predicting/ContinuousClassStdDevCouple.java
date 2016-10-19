package predicting;

import data.dataInstance.Neighbourhood;
import data.dataInstance.Node;
import data.schema.ContinuousAttribute;
import data.schema.WeightI;

public class ContinuousClassStdDevCouple extends ClassStdDevCouple {
	
	ContinuousClassStdDevCouple(int i, Node n, Neighbourhood nh, ContinuousAttribute target, WeightI weight) {
		super(i, n, nh, target, weight);
		// TODO Auto-generated constructor stub
	}

	protected Object determineValue(Node to){
		Object predictedClass;
		double continuousTarget=0.0;
		if(to.isSample() || to.isWorkingToSample()){
			continuousTarget=(Double)(to.getTarget().getValue()); // è un double
			//
		}
		else
		{
			continuousTarget=(Double)(to.getPredictedTarget().getValue()); // è un double
		}
		
		//predictedClass=target.discreteValue(continuousTarget);
		predictedClass=ContinuousAttribute.discreteValue(continuousTarget,bins);
		return predictedClass;
	}
	

}
