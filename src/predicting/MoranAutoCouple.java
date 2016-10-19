package predicting;

import data.dataInstance.Edge;
import data.dataInstance.Neighbourhood;
import data.dataInstance.Node;
import data.schema.WeightI;

public class MoranAutoCouple extends ReliabilityCouple {
	private WeightI weight;
	private double sumY;
	private double sumY2;
	private int N;


	MoranAutoCouple(int i, Node n, Neighbourhood nh, WeightI weight, double sumY, double sumY2, int N) {
		super(i, n, nh);
		this.weight=weight;
		this.sumY=sumY;
		this.sumY2=sumY2;
		this.N=N;
	}
	@Override
	void computeReliability() {
		double sum=0;
		double mean=sumY/N;
		for(Edge e:nh){
			Node neighbour=e.getTo();
			double Yj=0;
			if(neighbour.isSample() || neighbour.isWorkingToSample())
				Yj= (double)neighbour.getTarget().getValue();
			else
				Yj = (double)neighbour.getPredictedTarget().getValue();
			double lambdaij=weight.getWeight(e.getDistance());
			sum+=lambdaij*(Yj-mean);
			
		}
		double S2=(sumY2-Math.pow(sumY,2)/N)/N;
		double Yi=0;
		if(n.isSample() || n.isWorkingToSample())
			Yi= (double)n.getTarget().getValue();
		else
			Yi = (double)n.getPredictedTarget().getValue();
		double I= (Yi-mean)/S2*sum;
		reliability=I;

	}
	
	public int compareTo(ReliabilityCouple rc) { // ordina da zero in poi, in manier acrescente
		return this.reliability >= rc.getReliability()? 1 : -1;
	}
}
