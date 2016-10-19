package predicting;


import data.dataInstance.Neighbourhood;
import data.dataInstance.Node;

public abstract class ReliabilityCouple implements Comparable<ReliabilityCouple>{

	private int iExample;
	protected Neighbourhood nh;
	protected double reliability;
	protected Node n;

	
	ReliabilityCouple(int i,Node n,Neighbourhood nh){
		this.iExample = i;
		this.nh =  nh;
		this.n=n;
		this.reliability  =  0;
	
	}
	
	public int getId(){
		return iExample;
	}
	
	public double getReliability(){
		return reliability;
	}
	
	
	void  defaultReliability(){
		reliability=Double.MAX_VALUE;
	}
	
	public Node getn(){
		return n;
	}
	abstract void computeReliability();
	
	@Override
	public int compareTo(ReliabilityCouple rc) {
		return this.reliability <= rc.getReliability()? 1 : -1;
	}
	
	public String toString(){
		return "Row "+iExample+" Node "+n+" Reliability "+reliability+ "\n";
	}
}
