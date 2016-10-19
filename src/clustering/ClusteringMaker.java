package clustering;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import data.dataInstance.Node;
import data.network.DataSetUtility;
import data.network.Network;
import weka.core.Instances;

class ClusteringExampleSet{
	private Instances data;
	//private Instances labeledData;
	private List<Node> nodes;
	ClusteringExampleSet(Instances data, List<Node> nodes){//{, Instances labeledData){
		this.setData(data);
		this.setNodes(nodes);
	//	this.setLabeledData(labeledData);
	}
	public Instances getData() {
		return data;
	}
	public void setData(Instances data) {
		this.data = data;
	}
	public List<Node> getNodes() {
		return nodes;
	}
	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}
	//public Instances getLabeledData() {
	//	return labeledData;
	//}
	//public void setLabeledData(Instances labeledData) {
	//	this.labeledData = labeledData;
	//}
	
}

public class ClusteringMaker {
	
	private Clustering c;
	
	ClusteringMaker(Clustering c){
		this.c=c;
	}
	
	
	private static ClusteringExampleSet instances(String baseArff, String config)throws Exception{
		String fileTestArffName="Aco2CInput\\"+baseArff;
		String fileConfigFileName="Aco2CInput\\"+config;
		//Carico network comprensivo di nodi 
		Network n = new Network(fileTestArffName, fileConfigFileName);
		DataSetUtility d=new DataSetUtility(n);
		Instances data=d.createClusteringWorkingSet(); // working instance
		
		
		// To check autocorrelation
		
	//	Instances labeledData=d.createLabeleWorkingSet();
		List<Node> nodes=d.getWorkingNodes(); // list working nodes
		return new ClusteringExampleSet(data, nodes);//,labeledData);
			
		
	
	}

	/*
	 * Execute clustering 
	 */
	void compute() throws Exception
	{
		c.compute();
	}

	

	/*
	 * Execute clustering 
	 */
	void save(String fileName) throws IOException,FileNotFoundException
	{
		c.save(fileName);
	}
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		/*if(args.length<=3){
			System.out.println("Wrong parameters! Please specify: String baseArff, String config, String alg, ...");
			return;
		}*/
		
			String baseArff=args[0];
		String config = args[1];
		ClusteringExampleSet cSet=instances(baseArff, config);
		
		
		String type = args[2];
		Clustering c=null;
		
		if(type.toUpperCase().equals("KMEANS")){
		short k=new Short(args[3]); 
		 c=new KMeans(cSet, k);
		}
		else
			if(type.toUpperCase().equals("EM")){
				c=new EM(cSet);
			}
		
		ClusteringMaker cTest=new ClusteringMaker(c);
		cTest.compute();
		cTest.save("Aco2CInput\\clustering\\"+baseArff.replace(".", "_")+".cluster");
		
		
		
		
		
	}

}
