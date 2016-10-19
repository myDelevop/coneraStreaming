package data.schema;

public class WSpeedAttribute extends CollectiveAttribute{
	
	private double wspeed;
	
	public WSpeedAttribute(String name, int index){
		super(name,index);
	}

	public void compute(double m1,double m2){
		if(m2==0)
			if(m1==0)
				wspeed=0;
			else
				wspeed=Double.MAX_VALUE;
		else
		wspeed = m1/m2;
	}
	
	@Override
	public double get() {
		return wspeed;
	}
}
