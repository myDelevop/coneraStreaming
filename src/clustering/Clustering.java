package clustering;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import data.dataInstance.Node;

public abstract class Clustering {
	protected ClusteringExampleSet data;
	protected int[] assignments ;
	
	protected long computationtime=0;
	protected int k=0;
	 
	
	Clustering(ClusteringExampleSet data){
		this.data=data;
	}
	
	
	abstract void compute() throws Exception;
	
	
	void save(String fileName) throws IOException, FileNotFoundException{
		int i=0;
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName)));
		writer.write("#Computation Time:"+computationtime+ " msecs\n");
		writer.write("#Number of clusters:"+k+ "\n");
		for(Node n:data.getNodes()){
			writer.write(n.getId()+","+assignments[i++]+"\n");
		}
		writer.close();
		
		
	}
	

}
