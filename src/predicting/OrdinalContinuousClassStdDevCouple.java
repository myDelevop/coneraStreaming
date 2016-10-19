package predicting;

import java.util.HashMap;
import java.util.Iterator;

import data.dataInstance.Edge;
import data.dataInstance.Neighbourhood;
import data.dataInstance.Node;
import data.schema.ContinuousAttribute;
import data.schema.WeightI;

public class OrdinalContinuousClassStdDevCouple extends ContinuousClassStdDevCouple {
	
	private HashMap<String,Integer> ordinalClasses=new HashMap<String, Integer>();


	OrdinalContinuousClassStdDevCouple(int i, Node n, Neighbourhood nh, ContinuousAttribute target, WeightI weight) {
		super(i, n, nh, target, weight);
		// pupulate ordinalClasses
		Iterator<Double> itBins=bins.iterator();
		double inf=itBins.next();
		int ordinal=1;
		while(itBins.hasNext()){
			double sup=itBins.next();
			String value=inf+"__"+sup;
			value=value.replace(".", "_");
			ordinalClasses.put(value, ordinal++);
			inf=sup;
		}
	}
	

	@Override
	void computeReliability() {
		// TODO Auto-generated method stub
		Object nClass=determineValue(n); //n.getPredictedTarget().getValue(); // dovrebbe essere una stringa

		if(!nClass.toString().contains("_")) throw new RuntimeException("Error in generiting the class!");
		
		int oNClass=ordinalClasses.get(nClass);
//		System.out.println("Evaluate "+ n.getId()+ " with " +nh.size()+ "neighbours");
		double countErrors=0;
		double size=0;
		for(Edge e: super.nh){
			Node to = e.getTo();
			double w=weight.getWeight(e.getDistance());
			size+=w;
			Object predictedClass=determineValue(to);
			
			if(!predictedClass.toString().contains("_")) throw new RuntimeException("Error in generiting the class!");	
			
			int oPredictedClass=ordinalClasses.get(predictedClass);
			countErrors+=w*(Math.abs(oNClass-oPredictedClass));
		}
		this.reliability=countErrors/size;

	}
}
