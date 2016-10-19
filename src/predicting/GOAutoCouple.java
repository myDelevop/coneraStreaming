package predicting;

import data.dataInstance.Edge;
import data.dataInstance.Neighbourhood;
import data.dataInstance.Node;
import data.schema.WeightI;

public class GOAutoCouple extends ReliabilityCouple {
	
	private WeightI weight;
	private double sumY;
	private double sumY2;
	private int N;

	GOAutoCouple(int i, Node n, Neighbourhood nh, WeightI weight, double sumY, double sumY2, int N) {
		super(i, n, nh);
		this.weight=weight;
		this.sumY=sumY;
		this.sumY2=sumY2;
		this.N=N;
	}

	@Override
	void computeReliability() {
		// TODO Auto-generated method stub
		
	
		double sumLambdaij=0.0;
		double sumLamdajYj=0.0;
		double sumLambdaij2=0.0;

	
	
		for(Edge e:nh){
			Node neighbour=e.getTo();
			double Yj=0;
			if(neighbour.isSample() || neighbour.isWorkingToSample())
				Yj= (double)neighbour.getTarget().getValue();
			else
				Yj = (double)neighbour.getPredictedTarget().getValue();
			double lambdaij=weight.getWeight(e.getDistance());
			sumLambdaij+=lambdaij;
			sumLamdajYj+=Yj*lambdaij;	
			sumLambdaij2+=Math.pow(lambdaij, 2);
		}
		double num=sumLamdajYj-(sumY/N)*sumLambdaij;
		double S2=(sumY2-Math.pow(sumY,2)/N)/N;
		double den=Math.sqrt(S2/(N-1)*(N*sumLambdaij2- Math.pow(sumLambdaij, 2)));
		Double Gi=num/den;
		if(Gi.equals((Double.NaN)) || Gi.equals(Double.NEGATIVE_INFINITY) || Gi.equals(Double.POSITIVE_INFINITY))
				Gi=1000000.0;
//		
		reliability=Math.abs(Gi);

	}
	
	public int compareTo(ReliabilityCouple rc) { // ordina da zero in poi, in manier acrescente
		return this.reliability >= rc.getReliability()? 1 : -1;
	}

}
