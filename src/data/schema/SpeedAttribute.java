package data.schema;

public class SpeedAttribute extends CollectiveAttribute{
	
	private double speed;
		
	public SpeedAttribute(String name, int index){
		super(name,index);
	}

	public void compute(double m1,double m2){
		if(m2==0)
			if(m1==0)
				speed=0;
			else
				speed=Double.MAX_VALUE;
		else
		speed = m1/m2;
	}
	
	@Override
	public double get() {
		return speed;
	}
}
