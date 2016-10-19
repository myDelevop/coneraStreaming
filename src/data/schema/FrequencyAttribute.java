package data.schema;

import java.util.Iterator;

import data.dataInstance.Edge;
import data.dataInstance.Neighbourhood;
import data.dataInstance.Node;

public class FrequencyAttribute extends CollectiveAttribute {
	
	
	private double inf= Double.NEGATIVE_INFINITY;
	private double sup= Double.POSITIVE_INFINITY;
	
	private double countValues=0;

	private double nSize=0;
	
	private WeightI wI;

	public FrequencyAttribute(String name, int index,WeightI wI) {
		super(name, index);
		this.wI=wI;
	}


	@Override
	public double get() {
		
		if(countValues==0) return 0;
		
		return countValues/nSize;
	
	}
	
	public String getName(){
		return super.getName(); //+inf+"_"+sup;
	}
	
	
	public boolean filter(double value){
		return (value>inf && value <=sup);
	}
	
	public void setInf(double inf){
		this.inf=inf;
		
	}
	public void setSup(double sup){
		this.sup=sup;
		
	}
	
	private void update(Neighbourhood nh){

		for(Edge e:nh){
			
			
			Node to = e.getTo();
		//	System.out.println("Y"+to.getTarget()+ " PY"+to.getPredictedTarget());
			double w = wI.getWeight(e.getDistance());
			double y=0.0;
			if(!(to.isSample() || to.isWorkingToSample()))
				y=(double)to.getPredictedTarget().getValue();
			else
				y=(double)to.getTarget().getValue();
			boolean fY=this.filter(y);
			if(fY){
				countValues+=w;
	
			}
			nSize+=w;

		}
	}
	public void compute (Neighbourhood nh){
		countValues=0;
		nSize=0;
	
		update(nh);
		
		
	}
	
	public void compute(Neighbourhood nh, FrequencyAttribute f){
		countValues=f.countValues;
		nSize=f.nSize;
		update(nh);
		Iterator<Edge> it=nh.externalIterator();
		while(it.hasNext()){
			Edge e=it.next();
			Node to = e.getTo();
			
			double w = wI.getWeight(e.getDistance());
			double y=0.0;
			if(!(to.isSample() || to.isWorkingToSample()))
				y=(double)to.getPredictedTarget().getValue();
			else
				y=(double)to.getTarget().getValue();
			boolean fY=this.filter(y);
			if(fY)
				countValues+=w;
			nSize+=w;
		}
		
	}

	
	public String toString()
	{
		return getName();//"C"+c+"\tn"+n+"\twC"+countValues+"\twN"+nSize+"\tv"+((double)c/n)+"\twv"+get();
	}
}
