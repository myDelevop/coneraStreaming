package data.schema;

import java.util.Iterator;

import data.dataInstance.Edge;
import data.dataInstance.Neighbourhood;
import data.dataInstance.Node;

public class StdDevAttribute extends CollectiveAttribute{
	
	private double sum =0;
	private int n =0;
	private double mean =0;
	
	public StdDevAttribute(String name, int index){
		super(name,index);
	}

	public void compute(Neighbourhood nh, double m){
		sum=0;
		n=0;
		mean=0;
		for(Edge e:nh){
			Node to = e.getTo();
			if(!(to.isSample() || to.isWorkingToSample()))
				sum += Math.pow((double)to.getPredictedTarget().getValue(), 2);
			else
				sum += Math.pow((double)to.getTarget().getValue(), 2);
		}
		n = nh.size();
		mean = Math.pow(m, 2);
	}
	
	public void compute(Neighbourhood nh, double m, StdDevAttribute sd){
		double currentSum =0;
		//for(Edge e:nh)
		//AA: devo considerare solo i nodi sul confine
		Iterator<Edge> it=nh.externalIterator();
		while(it.hasNext())
		{
			Edge e=it.next();
			Node to = e.getTo();
			if(!(to.isSample() || to.isWorkingToSample()))
				currentSum += Math.pow((double)to.getPredictedTarget().getValue(), 2);
			else
				currentSum += Math.pow((double)to.getTarget().getValue(), 2);
		}
		
		sum = currentSum + sd.sum;
		n = nh.size(); // + sd.n;
		mean = Math.pow(m, 2);
	}
	
	@Override
	public double get() {
		if(n==0) return 0;
		return Math.sqrt((sum/n)-mean);
	}
}
