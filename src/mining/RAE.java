package mining;
import data.dataInstance.Node;
import data.network.Network;
//RelativeAbsoluteError
public class RAE extends EvaluationMeasure {
	
	public RAE(Network n)
	{
		 super(n);
	}

	@Override
	public double compute() {
		double sum=0;
		int ct=0;

		for(Node node: n.getGraph())
		{
			if(!node.isSample()){
				double real=(double)node.getTarget().getValue();
				sum+=real;
				ct++;
			}
		}
		double avg=sum/ct;
			
		

		sum=0;
		double den=0;

		for(Node node: n.getGraph())
		{
			if(!node.isSample() && ! node.isWorkingToSample()){
				double real=(double)node.getTarget().getValue();
				double pred=(double)node.getPredictedTarget().getValue();
				sum+=Math.abs(real-pred);
				den+=Math.abs(avg-real);
				
		}
		
			
		}
		return (sum/den);
		
		
	
	}

}
