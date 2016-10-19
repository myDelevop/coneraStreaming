package distance;

import java.util.List;

import data.dataInstance.Graph;
import data.dataInstance.Node;
import data.network.Network;
import data.schema.Schema;

public class DescriptiveAndCollectiveDistanceMap extends CollectiveDistanceMap {

	
	public DescriptiveAndCollectiveDistanceMap(Graph graph,List<Node> data,  Schema schema) {
		super(graph);
		System.out.println("Computing descriptive distances!");
		DistanceMap descriptiveDistanceMap=new DistanceMap(data,schema);
		
		// crea la matrice che media distanze collettive e distanze descrittive
		for(int i=0;i<mat.size();i++){
			List<Double> row=mat.get(i);
			for(int j=0;j<row.size();j++)
				row.set(j, (row.get(j)+descriptiveDistanceMap.get(i, j))/2);
		}
	}

	
public static void main(String args[]) throws Exception{
		
		if(args.length !=4){
			System.out.println("Wrong parameters! Please specify: String baseArff, String config");
			return;
		}
		
		String baseArff=args[0];
		String config = args[1];
		String edge=args[2];
		String nhsetting=args[3];

		String fileTestArffName="Aco2CInput/"+baseArff;
		String fileConfigFileName="Aco2CInput/"+config;
		String fileEdgeFileName="Aco2CInput/"+edge;
		String fileNhSettingFileName="Aco2CInput/"+nhsetting;
		
		
		Network n = new Network(fileTestArffName, fileEdgeFileName, fileConfigFileName,fileNhSettingFileName,true); 
		
		List<Node>  data=n.getGraph().getWorkingNodes();
		Schema schema=n.getGraph().getSchema();
		
		long start=System.currentTimeMillis();
		
		DescriptiveAndCollectiveDistanceMap d=new DescriptiveAndCollectiveDistanceMap(n.getGraph(),data,schema);
		long end=System.currentTimeMillis();
		
		d.save("Aco2CInput/distance/"+baseArff.replace(".", "_")+".csv",(end-start));
		d.serialize("Aco2CInput/distance/"+baseArff.replace(".", "_")+".serialized");
		
	}
}
