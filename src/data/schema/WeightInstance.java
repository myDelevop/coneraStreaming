package data.schema;

public class WeightInstance implements WeightI{
	
	int n;
	
	public WeightInstance(int n){
		this.n=n;
	}
	
	@Override
	public float getWeight(float d) {
		
		return (float) (1/(Math.pow(d+1, n)));
		//return d;
	}
}
