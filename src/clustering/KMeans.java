package clustering;

import java.util.GregorianCalendar;

import weka.clusterers.SimpleKMeans;
import weka.core.Instances;

public class KMeans extends Clustering {
	
	
	KMeans(ClusteringExampleSet data, int k){
		super(data);
		this.k=k;

	}
	@Override
	void compute() throws Exception {
		
		GregorianCalendar start=new GregorianCalendar();
		SimpleKMeans kmeans=new SimpleKMeans();
		kmeans.setSeed((int)(new GregorianCalendar().getTimeInMillis()));
		// This is the important parameter to set
		kmeans.setPreserveInstancesOrder(true);
	    kmeans.setNumClusters(k);
		kmeans.buildClusterer(super.data.getData());
		
		
		GregorianCalendar end=new GregorianCalendar();
		computationtime=end.getTimeInMillis()-start.getTimeInMillis();
		
		//http://weka.sourceforge.net/doc.dev/weka/clusterers/SimpleKMeans.html

		// This array returns the cluster number (starting with 0) for each instance
		// The array has as many elements as the number of instances
		assignments= kmeans.getAssignments();

		int i=0;
		for(int clusterNum : assignments) {
		    System.out.printf("Instance %d -> Cluster %d\n", i, clusterNum);
		    i++;
		}
	}

}
