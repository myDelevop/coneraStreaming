package clustering2;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import data.dataInstance.Node;

import data.network.Network;
import data.schema.Schema;


public class ClusteringMaker {

	//private List<Node> data;
	//private short k;
	//private Schema schema;
	private PAM pam;
	ClusteringMaker(String baseArff, String config, short k) throws Exception{
		//this.k=k;
		String fileTestArffName="Aco2CInput/"+baseArff;
		String fileConfigFileName="Aco2CInput/"+config;
		//Carico network comprensivo di nodi 
		Network n = new Network(fileTestArffName, fileConfigFileName,true); // aggiorna min e max di attributi numerici in modo da poter fare lo scaling
		Schema schema=n.getGraph().getSchema();
		List<Node>  data=n.getGraph().getWorkingNodes();
		
		
	}
	
	
	ClusteringMaker(String baseArff, String config,  double aPerc, boolean isSwapping, boolean estimateK ) throws Exception{
		//this.k=k;
		String fileTestArffName="Aco2CInput/"+baseArff;
		String fileConfigFileName="Aco2CInput/"+config;
		//Carico network comprensivo di nodi 
		Network n = new Network(fileTestArffName, fileConfigFileName,true); // aggiorna min e max di attributi numerici in modo da poter fare lo scaling
		Schema schema=n.getGraph().getSchema();
		List<Node>  data=n.getGraph().getWorkingNodes();
		
		// K va stiamto in base al numero di esempi attivi (aPerc di umbe rof nodes)
		short k=1;
		int numberOfNodes=n.getGraph().countWorkingNodes();
		 k=(short)(aPerc*numberOfNodes);
		if(k<(aPerc*numberOfNodes))
			k=(short) (k+1); // arrotondo per eccesso
		
		System.out.println("Estimated k="+ k);
		//this.iterations=
		if(!estimateK)
		pam =new PAM(data,schema,k, isSwapping);
		else{
				Double previousSil=-1.0;
			
				for(short h=2;h<=k;h++){
				PAM currentPam =new PAM(data,schema,h,isSwapping);
				double Sil=currentPam.silhoette();
				System.out.println (h+" clusters with Silhouette="+Sil);
				if(Sil>previousSil){
					pam=currentPam;
					previousSil=Sil;
				}
				else
					break;
			}						
		}
	}
	
	private void save (String file) throws FileNotFoundException, IOException{
		pam.save(file);
	}
	
	
	


	public static void main(String args[]) throws Exception{
	
		if(args.length<5){
			System.out.println("Wrong parameters! Please specify: String baseArff, String config, k, isSwapping, isEstimatedk");
			return;
		}
		
			String baseArff=args[0];
		String config = args[1];
		//short k=new Short(args[2]); 
		double a=new Double(args[2]);
		boolean isSwapping=new Boolean(args[3]);
		boolean isEstimatedk=new Boolean(args[4]);
		//ClusteringMaker c=new ClusteringMaker(baseArff, config, k);
		ClusteringMaker c=new ClusteringMaker(baseArff, config, a,isSwapping,isEstimatedk);
		
		
		
		c.save("Aco2CInput/clustering/"+baseArff.replace(".", "_")+".cluster");
		
	}
}
