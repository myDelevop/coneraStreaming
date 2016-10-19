package self;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import collective.AbstractTopSelector;
import predicting.ReliabilityCouple;


public class RandomSelector extends AbstractTopSelector {

	RandomSelector() {
		super();
		// TODO Auto-generated constructor stub
	}
	


	@Override
	public Set<Integer> compute(int max) {
		// TODO Auto-generated method stub
		
		
		Set<Integer> topReliabilitySet = new TreeSet<Integer>();
		
		ReliabilityCouple [] array=new ReliabilityCouple[1];
		
		array=reliabilitySet.toArray(array);
		List<ReliabilityCouple> l=new LinkedList<ReliabilityCouple>();
		
		l.addAll(reliabilitySet);
		
		Random rnd=new Random();
		rnd.setSeed(System.currentTimeMillis());
		Collections.shuffle(l, rnd);
		int i=0;
		for(ReliabilityCouple r : l){
			topReliabilitySet.add(r.getId());
		//	System.out.println(r.getn().toString());
			i++;
			if(i>=max)break;
		}
	//	System.out.println("---");
		return topReliabilitySet;
	}

}
