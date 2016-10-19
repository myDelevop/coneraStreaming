package data.schema;

public abstract class CollectiveAttribute extends ContinuousAttribute{
	
	public CollectiveAttribute(String name, int index) {
		super(name, index);
	}
	

	abstract public double get();
	
	
	
}
