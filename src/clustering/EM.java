package clustering;

import java.util.GregorianCalendar;
import java.util.TreeSet;

import weka.core.Instance;


/*
class Couple implements Comparable<Couple>{
	Instance i;
	int cluster;
	Couple(Instance i, int cluster ){
		this.i=i;
		this.cluster=cluster;
	}
	@Override
	public int compareTo(Couple o) {
		// TODO Auto-generated method stub
		if(this.cluster<=o.cluster) return -1;
		else return 1;
	}
}
*/
public class EM extends Clustering {

	EM(ClusteringExampleSet data){
		super(data);
	}
	
	@Override
	void compute() throws Exception {
		
		
		
		GregorianCalendar start=new GregorianCalendar();
		weka.clusterers.EM em=new weka.clusterers.EM();
		em.setOptions("-I 100 -N -1 -M 1.0E-6 -S 100".split(" "));
		em.buildClusterer(super.data.getData());
		
		
		GregorianCalendar end=new GregorianCalendar();
		computationtime=end.getTimeInMillis()-start.getTimeInMillis();
		
		//http://weka.sourceforge.net/doc.dev/weka/clusterers/SimpleKMeans.html

		// This array returns the cluster number (starting with 0) for each instance
		// The array has as many elements as the number of instances
		assignments=new int[data.getData().numInstances()];
		
	//	TreeSet<Couple> ts=new TreeSet<Couple>();
		
		for(int i=0;i<data.getData().numInstances();i++){
			Instance inst=data.getData().instance(i);
			assignments[i]=em.clusterInstance(inst);
		//	Instance labeledInstance=data.getLabeledData().instance(i);
		//	ts.add(new Couple(labeledInstance, assignments[i]));
		}
		k=em.numberOfClusters();
		System.out.println("estimated k "+k);
	/*	int i=0;
		for(int clusterNum : assignments) {
		    System.out.printf("Instance %d -> Cluster %d\n", i, clusterNum);
		    i++;
		}*/
	//	for(Couple c: ts){
	//		System.out.println(c.cluster+ " "+c.i.classValue());
	//	}
	}

}
