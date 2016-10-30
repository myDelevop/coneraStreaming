package clusteringCollectiveSchema;

import data.dataInstance.Cluster;
import data.dataInstance.Graph;
import weka.core.Instances;

public class Clustering {
    
    protected Cluster[] clusters;
    
    protected Graph graph;
    
    protected long computationtime=0;
    protected int k=0;

    
    protected Clustering(Graph graph) {
        this.graph = graph;
    }

    public Cluster[] getClusters() {
        return clusters;
    }
    
}
