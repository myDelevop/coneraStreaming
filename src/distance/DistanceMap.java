package distance;

import java.io.FileInputStream;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import data.dataInstance.Node;
import data.network.Network;
import data.schema.Schema;


public class DistanceMap  extends DistanceMatrix implements Serializable, Iterable<Node>{
	
	
	protected Map<Node,Integer> distanceMap=new HashMap<Node,Integer>();
	
	



	
	
	 DistanceMap(List<Node> data){
		super(data);
		int row=0;
		for(Node n:data)
			distanceMap.put(n,row++);
	}
	public DistanceMap(List<Node> data,  Schema schema){
		
		super(data,schema);
		int row=0;
		for(Node n:data)
			distanceMap.put(n,row++);
		
	
	}
	
	public int getIndex(Node n){
		return distanceMap.get(n); // restiruisce l'indice di riga colonna nella matrice  
	}
	
public static void main(String args[]) throws Exception{
		
		if(args.length !=2){
			System.out.println("Wrong parameters! Please specify: String baseArff, String config");
			return;
		}
		
		String baseArff=args[0];
		String config = args[1];

		String fileTestArffName="Aco2CInput/"+baseArff;
		String fileConfigFileName="Aco2CInput/"+config;
		//Carico network comprensivo di nodi 
		Network n = new Network(fileTestArffName, fileConfigFileName,true); // aggiorna min e max di attributi numerici in modo da poter fare lo scaling
		Schema schema=n.getGraph().getSchema();
		List<Node>  data=n.getGraph().getWorkingNodes();
		long start=System.currentTimeMillis();
		
		DistanceMap d=new DistanceMap(data,schema);
		long end =System.currentTimeMillis();
		
		System.out.println("Learning time:"+(end-start)+ " msecs" );
		
		
		d.save("Aco2CInput/distance/"+baseArff.replace(".", "_")+".csv",(end-start));
		d.serialize("Aco2CInput/distance/"+baseArff.replace(".", "_")+".serialized");
		
	}
	

public static DistanceMap deserialize(String fileName) throws FileNotFoundException,IOException, ClassNotFoundException {
	// TODO Auto-generated constructor stub
	ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName));
	DistanceMap mat=(DistanceMap) (in.readObject());
	in.close();
	return mat;
	
}

@Override
public Iterator<Node> iterator() {
	// TODO Auto-generated method stub
	return distanceMap.keySet().iterator();
}

}
