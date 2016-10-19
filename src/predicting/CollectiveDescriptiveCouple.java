package predicting;


import data.dataInstance.Neighbourhood;
import data.dataInstance.Node;
import data.schema.ContinuousAttribute;
import data.schema.WeightI;

public class CollectiveDescriptiveCouple extends ContinuousClassStdDevCouple {

	CollectiveDescriptiveCouple(int i, Node n, Neighbourhood nh, ContinuousAttribute target, WeightI weight) {
		super(i, n, nh, target, weight);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	void computeReliability() {
		// TODO Auto-generated method stub
	
		
		//this.reliability=Math.abs(((Double)n.getTarget().getValue())-((Double)n.getPredictedTarget().getValue()));
		this.reliability=Math.abs(((Double)n.getPreviousPredictedTarget().getValue())-((Double)n.getPredictedTarget().getValue()));

	}

}
