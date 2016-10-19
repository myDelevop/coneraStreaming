package data.schema;

import java.util.Iterator;

import data.dataInstance.Edge;
import data.dataInstance.Neighbourhood;
import data.dataInstance.Node;

public class MeanAttribute extends CollectiveAttribute{
	
	private double sum;
	private int n;
	
	public MeanAttribute(String name, int index){
		super(name,index);
	}
	
	public void compute(Neighbourhood nh){
		sum = 0;
		n = 0;
		for(Edge e : nh){
			Node to = e.getTo();
			if(!(to.isSample() || to.isWorkingToSample()))
				sum +=(double)to.getPredictedTarget().getValue();
			else 
				sum +=(double)to.getTarget().getValue();
		}
		n = nh.size();
	}
	
	public void compute(Neighbourhood nh, MeanAttribute m){
		double currentSum = 0;
		//for(Edge e:nh)
		//AA: devo considerare solo i nodi sul confine
		Iterator<Edge> it=nh.externalIterator();
		while(it.hasNext())
		{
			Edge e=it.next();
			Node to = e.getTo();
			if(!(to.isSample() || to.isWorkingToSample()))
				currentSum +=(double)to.getPredictedTarget().getValue();
			else 
				currentSum +=(double)to.getTarget().getValue();
		}
		
		sum = currentSum + m.sum;
		n = nh.size();
	}

	@Override
	public double get() {
		if(n==0) return 0;
		return sum/n;
	}
}
