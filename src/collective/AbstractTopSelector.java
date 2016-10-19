package collective;

import java.util.Set;

import predicting.ReliabilityCouple;

public abstract class AbstractTopSelector {

	protected Set<ReliabilityCouple> reliabilitySet;
	//protected int max;
	
	protected AbstractTopSelector(Set<ReliabilityCouple> reliabilitySet){//, int max){
		this.reliabilitySet=reliabilitySet;
		//this.max=max;
	}
	
	
	protected AbstractTopSelector(){//int max){
		//this.max=max;
	}	
	public abstract Set<Integer> compute(int max) throws Exception;
	
	public void setReliabilitySet(Set<ReliabilityCouple> reliabilitySet){
		this.reliabilitySet=reliabilitySet;
	}
}
