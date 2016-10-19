package predicting;

import java.util.Set;
import java.util.TreeSet;

import collective.AbstractTopSelector;

public class TopSelector extends AbstractTopSelector {

	
	TopSelector(Set<ReliabilityCouple> reliabilitySet){
		super(reliabilitySet);
	}
	
	public TopSelector(){
		
	}
	
	
	
	public
	Set<Integer> compute(int max) {
		
		Set<Integer> topReliabilitySet = new TreeSet<Integer>();
		
		int i =0;
		for(ReliabilityCouple r : reliabilitySet){
			topReliabilitySet.add(r.getId());
			i++;
			if(i>=max)break;
			
		}
		
		
		return topReliabilitySet;
	
	}
		
		/*

	public	Set<Integer> compute(int max) {
		
		Set<Integer> topReliabilitySet = new TreeSet<Integer>();
		
		if(max>=reliabilitySet.size()){
			for(ReliabilityCouple r : reliabilitySet)
				topReliabilitySet.add(r.getId());
				return topReliabilitySet;
		}
		// determina top size, botom size
		int sizeTop=max/2;
		if (sizeTop==0)
			sizeTop=1;
		int sizeBottom=0;
		if(sizeTop<max)
			sizeBottom=max-sizeTop;
		
		Object [] rArray=reliabilitySet.toArray();
		
		for(int i=0;i<sizeTop;i++){
			topReliabilitySet.add(((ReliabilityCouple)rArray[i]).getId());
			
		}
		
		for(int i=rArray.length-1;i>=rArray.length-1-sizeBottom+1;i--){
			topReliabilitySet.add(((ReliabilityCouple)rArray[i]).getId());
			
		}
		
		return topReliabilitySet;
	}
*/
}
