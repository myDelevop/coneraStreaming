package cf;




import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Random;

import data.network.DataSetUtility;
import data.network.Network;
import weka.core.Instances;

public class kFoldCV {
	
	private String baseArff;
	private String config;
	private int k=10;
	
	public kFoldCV(String baseArff, String config, int k) {
		this.baseArff=baseArff;
		this.config=config;
		this.k=k;
	}

	
	void cv(){
		
		String fileTestArffName="Aco2CInput/"+baseArff;
		String fileConfigFileName="Aco2CInput/"+config;
		//Carico network comprensivo di nodi 
		Network n;
		try {
			n = new Network(fileTestArffName, fileConfigFileName);
			DataSetUtility d=new DataSetUtility(n);
			Instances data=d.createLabeleWorkingSet();
			
			
			Random rand = new Random(System.currentTimeMillis());   // create seeded number generator
			data.randomize(rand);         // randomize data with number generator
			
			System.out.println("Data:"+data.numInstances());
			
		
			for(int i=0;i<k;i++){
				
				try{
					Instances trainData = data.trainCV(k,i);
				 	Instances testData = data.testCV(k,i);
				 	System.out.println("Fold "+ (i+1)+ ": training ("+trainData.numInstances()+") + testing ("+testData.numInstances()+")");
				 	BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("Aco2CInput/"+baseArff.replace(".", "_")+"Train_"+(i+1)+".arff")));
					writer.write(trainData.toString());
				//	System.out.println(trainData);
				//	System.out.println(testData);
					writer.close();
					writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("Aco2CInput/"+baseArff.replace(".", "_")+"Test_"+(i+1)+".arff")));
					writer.write(testData.toString());
					writer.close();
				}
				catch(IOException e){e.printStackTrace();}
				
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	

	}
	
	public static void main (String args[]){
		//movies2.arff configMovie.ini 5
		if(args.length!=3){
			System.out.println("Wrong parameters! Please specify: String baseArff, String config, int k");
			return;
		}
		String baseArff=args[0];
		String config = args[1];
		int k= new Integer(args[2]);
		kFoldCV cv=new kFoldCV(baseArff, config, k);
		cv.cv();
	}
	

}
