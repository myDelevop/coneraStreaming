package clusteringCollectiveSchema;

import java.util.GregorianCalendar;

import data.dataInstance.Cluster;
import data.dataInstance.Graph;
import data.dataInstance.Node;
import weka.core.Instance;
import weka.core.Instances;

public class DescriptiveClustering extends Clustering {

    public DescriptiveClustering(Instances data, Graph graph) throws Exception {
        super(graph);
        
        GregorianCalendar start=new GregorianCalendar();
        weka.clusterers.EM em=new weka.clusterers.EM();
        em.setOptions("-I 100 -N -1 -M 1.0E-6 -S 100".split(" "));
        em.buildClusterer(data);
        
        
        GregorianCalendar end=new GregorianCalendar();
        computationtime=end.getTimeInMillis()-start.getTimeInMillis();

        int[] assignments=new int[data.numInstances()];
        
    //  TreeSet<Couple> ts=new TreeSet<Couple>();
        
        for(int i=0;i<data.numInstances();i++){
            Instance inst=data.instance(i);
            assignments[i]=em.clusterInstance(inst);
        //  Instance labeledInstance=data.getLabeledData().instance(i);
        //  ts.add(new Couple(labeledInstance, assignments[i]));
        }
        k = em.numberOfClusters();
        System.out.println("estimated k "+k);
        
        clusters = new Cluster[em.numberOfClusters()];
        for(int c=0;c<clusters.length;c++) {
            clusters[c]=new Cluster(c);
        }
        
        for(int i=0; i<data.numAttributes(); i++) {
            Instance instance = data.instance(i);
            
            Integer id = (int) instance.value(0);
            Node n = graph.getNodeByID(id);
            int cluster = em.clusterInstance(instance);
            n.setDescriptiveCluster(clusters[cluster]);
            clusters[cluster].add(n);
       }

    }

}
