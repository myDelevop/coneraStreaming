package mining;

import data.dataInstance.Node;
import data.network.Network;

public class RMSE extends EvaluationMeasure {

	public RMSE(Network n) {
		super(n);
		// TODO Auto-generated constructor stub
	}

	@Override
	public double compute() {
		// TODO Auto-generated method stub
		double sum=0;
		int ct=0;

		for(Node node: n.getGraph())
		{
			if(!node.isSample()){ 
				ct++; //anche nodi attivi partecipano al clacolo dello rmse anche se per loro l'errore è zero
				double real=(double)node.getTarget().getValue();
				double pred=(double)node.getPredictedTarget().getValue();
				sum+=Math.pow(real-pred, 2);
				
			}
		}
		return Math.sqrt(sum/ct);
	}

}
