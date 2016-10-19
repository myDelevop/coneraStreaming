package predicting;

import java.util.List;

import data.dataInstance.Edge;
import data.dataInstance.Neighbourhood;
import data.dataInstance.Node;

public class StdDevCouple extends ReliabilityCouple{
	
	StdDevCouple(int i, Node n ,  Neighbourhood nh) {
		super(i, n,nh);
		
	}

	@Override
	void computeReliability() {

		double sum=0;
		double sum2=0;
		for(Edge e : this.nh){
			Node to = e.getTo();
			if(to.isSample() || to.isWorkingToSample()){
				sum += (double)to.getTarget().getValue();
				sum2+=Math.pow((double)to.getTarget().getValue(),2);
			}
			else {
				sum += (double)to.getPredictedTarget().getValue();
				sum2 += Math.pow((double)to.getPredictedTarget().getValue(), 2);
			}
		}
		int n=nh.size();
		
		this.reliability = Math.sqrt( sum2/n -Math.pow(sum/n, 2));
		
		
	}
	
}
