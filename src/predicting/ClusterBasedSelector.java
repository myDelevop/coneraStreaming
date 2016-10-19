package predicting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

import collective.AbstractTopSelector;
import data.dataInstance.Cluster;


class ReliableCluster extends TreeSet<ReliabilityCouple> implements Comparable<ReliableCluster>{

	@Override
	public int compareTo(ReliableCluster o) {
		// TODO Auto-generated method stub
		int sizeDiff=o.size()-this.size();
		if(sizeDiff<=0)
			return -1;
		else return 1;
			
	}
	
}

public class ClusterBasedSelector extends AbstractTopSelector {
	

	ClusterBasedSelector(Set<ReliabilityCouple> reliabilitySet){
		super(reliabilitySet);
	
	}
	

	public ClusterBasedSelector(){
		
	
	}
	
	

	@Override
	public
	Set<Integer> compute(int max) throws Exception{
		// TODO Auto-generated method stub
		Set<Integer> ts=new TreeSet<Integer>();;
		
		//(1) se il numero di esempi selezionabile è minore di max, li seleziono tutti
		if(reliabilitySet.size()<=max){
			for(ReliabilityCouple r : reliabilitySet)
				ts.add(r.getId());
			return ts;
		}
		
		
		//(2) raggruppo gli esempi selezionabili in cluster
		
		HashMap<Cluster,ReliableCluster> map=new HashMap<Cluster,ReliableCluster>();
		
		
		for(ReliabilityCouple r:reliabilitySet){
			try{
				map.get(r.n.getCluster()).add(r);	
			}
			catch (NullPointerException e){
				ReliableCluster relSet =new ReliableCluster();
				relSet.add(r);
				map.put(r.n.getCluster(), relSet);
			}
		}
	//	System.out.println(map);		// 3) se il numero di cluster è uguale a max, sceglierò un esempio da ogni cluster
		if(map.size()==max){ 
			//an active example per cluster
			for(TreeSet<ReliabilityCouple> relSet:map.values())
				ts.add(relSet.first().getId());
			return ts;
		}
		
		// 4) se il numero di cluster è minore max, sceglierò gli esempi proporzionalmente alla cardinalità dei cluster
	    {
			//select according to cluster cardinality
			TreeSet<ReliableCluster> clusterSet=new TreeSet<ReliableCluster>(map.values());
			
			//4.1 determino numero di esempi da selezionare per cluster (almeno 1) 
			ArrayList<Integer> selectionSize=new ArrayList<>();
			int selected=0;
			for(ReliableCluster c: clusterSet)
			{
				int cardinalityPerc=(int)(((double)c.size()/reliabilitySet.size())*max);
				if(cardinalityPerc==0)
					cardinalityPerc=1;
				
				selectionSize.add(cardinalityPerc);
				selected+=cardinalityPerc;
				
			}
			int i=0;
			
			while(selected>max)
			{
				selectionSize.set(i, selectionSize.get(i)-1);
				selected --;
				i++;
			}
			i=0;
			while(selected<max)
			{
				selectionSize.set(i, selectionSize.get(i)+1);
				selected ++;
				i++;
			}
			i=0;
			
			
			/*int tot=0;
			for(int v:selectionSize)
				tot+=v;
			if(tot!=max) throw new RuntimeException("Error in the active selection procedure!");
			*/
			//4.2 determino gli esempi attivi da ogni cluster 
			for(ReliableCluster c: clusterSet) //cluster ordinati per cardinalità (ordinamento decrescente)
			{
				int k=0;
				Iterator<ReliabilityCouple> it=c.iterator();
				while(it.hasNext() && k<selectionSize.get(i)){
					ReliabilityCouple r=it.next();
					ts.add(r.getId());
					k++;
					//System.out.println("Selected Y" +r.n.getTarget()+ " PY"+r.n.getPredictedTarget());
				}
				//System.out.println("---");
				i++;
			}
			
		}
		return ts;
	}

}
