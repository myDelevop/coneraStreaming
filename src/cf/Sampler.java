package cf;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Random;

import data.network.DataSetUtility;
import data.network.Network;
import weka.core.Instances;

public class Sampler {
	
	protected double samplePerc=.05;

	private String baseArff;
	private String config;
	
	int n;
	
	
	
	Sampler(String baseArff, String config,float samplePerc, int n){
		this.baseArff=baseArff;
		this.config=config;
		this.samplePerc=samplePerc;
		this.n=n;
	}

	protected int size(Instances data){
		int size=data.numInstances();
	       // randomize data with number generator
		
		System.out.println("Data:"+size);
		
		int end=(int)(size*samplePerc);
		end+=1;
		
		System.out.println("Sampling size:"+end);
		return end;
	}
	
	void sample(){
		String fileTestArffName="Aco2CInput/"+baseArff;
		String fileConfigFileName="Aco2CInput/"+config;
		//Carico network comprensivo di nodi 
		Network n;
		try {
			n = new Network(fileTestArffName, fileConfigFileName);
			DataSetUtility d=new DataSetUtility(n);
			Instances data=d.createLabeleWorkingSet();
			
			
			int end=size(data);

		
			for(int i=0;i<this.n;i++){
				long seed=System.currentTimeMillis();
				Random rand = new Random(seed);   // create seeded number generator
				data.randomize(rand);  
				System.out.println(seed);
				Thread.sleep(10);
				
				try{
				 	BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("Aco2CInput/"+baseArff.replace(".","_")+"Sample"+(i+1)+".txt")));
				 	for(int k=0;k<end;k++)
				 		writer.write((int)data.instance(k).value(0)+"\n");
					writer.close();
					
				}
				catch(IOException e){e.printStackTrace();}
				
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public static void main(String[] args) {
		//movies2.arffTrain_1.arff configMovieEpured.ini .01 5
		Sampler s=new Sampler(args[0], args[1], new Float(args[2]), new Integer(args[3]));
		s.sample();

		
	
	

	}

}
