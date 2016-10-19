package mining;

import data.dataInstance.Node;
import data.network.Network;


public class R2 extends EvaluationMeasure {

	public R2(Network n) {
		super(n);
		// TODO Auto-generated constructor stub
	}

	@Override
	public double compute() {
		// TODO Auto-generated method stub
		double sumY=0, sumY2=0,sumPredY2=0,sumYPredY=0;
		int ct=0;

		for(Node node: n.getGraph())
		{
			if(!node.isSample()){
				double real=(double)node.getTarget().getValue();
				double pred=(double)node.getPredictedTarget().getValue();
				sumY+=real;
				sumY2+=Math.pow(real, 2);
				sumPredY2+=Math.pow(pred, 2);
				sumYPredY+=pred*real;
				ct++;
		}
		
			
		}
		double rss=sumY2+sumPredY2-2*sumYPredY;
		double tss=sumY2-Math.pow(sumY, 2)/ct;
		return 1.0-(rss/tss);
	}
}