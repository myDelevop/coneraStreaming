package predicting;
import java.util.GregorianCalendar;

import java.util.Set;

import data.dataInstance.Cluster;

import predicting.ReliabilityCouple;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class ClusterPredictionSelector extends ClusterBasedSelector {

	private boolean isClustering=false;
	private boolean isDistanceWeight=false;
	
	public ClusterPredictionSelector(boolean isClustering){
		this.isClustering=isClustering;
		
	}
	
	private void clustering() throws Exception{
		// create instances
		FastVector attributes = new FastVector();

		weka.core.Attribute wa = new weka.core.Attribute("PY",0);
		attributes.addElement(wa);
		wa = new weka.core.Attribute("PPY",0);
		attributes.addElement(wa);
		Instances instances = new Instances("data",attributes,0); //creo il trainingSet vuoto
		for(ReliabilityCouple r:this.reliabilitySet){
			Instance instance= new Instance(attributes.size());
			instance.setDataset(instances);
			//Predizione collettiva
			instance.setValue(0, (Double)(r.getn().getPredictedTarget().getValue()));
			//Predizione descrittiva
			instance.setValue(1, (Double)(r.getn().getPreviousPredictedTarget().getValue()));
			
			instances.add(instance);
		}
		
		// EM
		GregorianCalendar start=new GregorianCalendar();
		weka.clusterers.EM em=new weka.clusterers.EM();
		em.setOptions("-I 100 -N -1 -M 1.0E-6 -S 100".split(" "));
		em.buildClusterer(instances);
		
		Cluster clusters[]=new Cluster[em.numberOfClusters()];
		for(int c=0;c<clusters.length;c++)
			clusters[c]=new Cluster(c);
		
        // apply clustering to working nodes
        int i=0;
		
		for(ReliabilityCouple r:this.reliabilitySet){
			Instance instance=instances.instance(i);
			int cluster=em.clusterInstance(instance);
			r.n.setCluster(clusters[cluster]);
			clusters[cluster].add(r.n);
			i++;
		}
	
		
	}
	@Override
	public
	Set<Integer> compute(int max) throws Exception{
		
		
		if(isClustering){
			System.out.println ("Clustering ...");
			clustering();
		}
		else{
		// no clustering
			Cluster c=new Cluster(0);
			for(ReliabilityCouple r:this.reliabilitySet){
				r.n.setCluster(c);
			
				
			}
		}
		return super.compute(max);
	}

}
