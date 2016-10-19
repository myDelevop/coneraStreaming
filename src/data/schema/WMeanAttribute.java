package data.schema;

import java.util.Iterator;

import data.dataInstance.Edge;
import data.dataInstance.Neighbourhood;
import data.dataInstance.Node;

public class WMeanAttribute extends CollectiveAttribute{

	private double sum =0;
	private float wSum =0;

	private WeightI wI;
	
	public WMeanAttribute(String name, int index, WeightI w){
		super(name,index);
		this.wI = w;
	}
	
	public void compute(Neighbourhood nh){
		sum=0;
		wSum=0;
		for(Edge e:nh){
			Node to = e.getTo();
			float d = e.getDistance();
			float w=wI.getWeight(d);
			if(!(to.isSample() || to.isWorkingToSample()))
				sum +=(double)to.getPredictedTarget().getValue()*w;
			else
				sum +=(double)to.getTarget().getValue()*w;
			wSum += w;
		}
	}
	
	public void compute(Neighbourhood nh, WMeanAttribute wm){
		double currentSum =0;
		float currentWSum =0;
		
		//for(Edge e:nh)
		//AA: devo considerare solo i nodi sul confine
		Iterator<Edge> it=nh.externalIterator();
		while(it.hasNext())
		{
			Edge e=it.next();
			
			Node to = e.getTo();
			double w = wI.getWeight(e.getDistance());
			if(!(to.isSample() || to.isWorkingToSample()))
				currentSum +=(double)to.getPredictedTarget().getValue()*w;
			else
				currentSum +=(double)to.getTarget().getValue()*w;
			currentWSum += w;
		}
		
		sum = currentSum + wm.sum;
		wSum = currentWSum + wm.wSum;
	}

	@Override
	public double get() {
		if(wSum==0) 
			if (sum==0)return 0;
			else return Double.MAX_VALUE;
		return sum/wSum;
	}
}
