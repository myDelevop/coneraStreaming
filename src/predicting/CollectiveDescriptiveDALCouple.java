package predicting;

import java.util.List;

import data.dataInstance.Neighbourhood;
import data.dataInstance.Node;
import data.schema.ContinuousAttribute;
import data.schema.WeightI;
import distance.DistanceMap;

public class CollectiveDescriptiveDALCouple extends CollectiveDescriptiveCouple {

	//private double dalReliability=0;
	
	private DistanceMap distances;
	
	private List<Node> trainignNodes;
	
	CollectiveDescriptiveDALCouple(int i, Node n, Neighbourhood nh, ContinuousAttribute target, WeightI weight, DistanceMap distances, List<Node> trainingNodes) {
		super(i, n, nh, target, weight);
		this.distances=distances;
		this.trainignNodes=trainingNodes;
		
	}

	@Override
	void computeReliability() {
		super.computeReliability(); // collective -descriptive
		//
		determineDalReliability();
	}
	
	private void determineDalReliability(){
			//determine closest training example of n
		int r=distances.getIndex(n);
		
		double minDist=Double.MAX_VALUE;
		for(Node node:trainignNodes)
		{
			
			//
			{
				int c=distances.getIndex(node);
				double d=distances.get(r, c);
				if(d<minDist)
					minDist=d;
			}
		}
		
		// ho trovato la closest training distance; considero pi� papabile per active labeling
		// l'esempio con pi� alta closest distance da un esempio di training
		// dato l'attributo reliability � ordinato in maniera decrescente
		double dalReliability=minDist;
		
		// preferiro esempi con pi� alta distanza minima da un training example e maggiore variabilit� tra etichetta descrittiva e etichetta collettiva
		reliability*=dalReliability;
		
	}

}
