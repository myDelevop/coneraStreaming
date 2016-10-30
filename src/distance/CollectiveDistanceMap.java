package distance;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import data.dataInstance.Edge;
import data.dataInstance.Graph;
import data.dataInstance.Node;
import data.network.Network;





class NodeWeight implements Comparable<NodeWeight>{
private Node n;
private double cost;
public NodeWeight(Node n, double cost) {
	// TODO Auto-generated constructor stub
	this.setN(n);
	this.cost=cost;
}
public double getW() {
	return cost;
}
public void setW(double w) {
	this.cost = w;
}

public boolean equals(Object o){
	return n.equals(((NodeWeight)o).getN());
}
@Override
public int compareTo(NodeWeight arg0) {
	// TODO Auto-generated method stub
	if (this.cost==arg0.cost) 
		return 0;
	else if (this.cost<arg0.cost) return -1;
	else return +1;
}
public Node getN() {
	return n;
}
public void setN(Node n) {
	this.n = n;
}
public String toString(){
	return ""+n.getId()+"("+cost+")";
}
}

class NodeWeightComparator implements Comparator<NodeWeight>{

	@Override
	public int compare(NodeWeight o1, NodeWeight o2) {
		// TODO Auto-generated method stub
		if(o1.getW()== o2.getW()) 
			return 0;
		if(o1.getW()<o2.getW()) return -1;
		return +1;
	}
}




public class CollectiveDistanceMap extends DistanceMap implements Serializable{
	
    // contiene le distanze su ogni coppia di nodi considerando i cammini minimi (Dijsktra)
    private List<ArrayList<Double>> shortestDistances = new ArrayList<ArrayList<Double>>();

	// inner class
	class Dijkstra {
	
	private Node source;
	private List<Node> nodes;
	private 	ArrayList<Double> dist;
	private ArrayList<ArrayList<Double>> adjacency;
	
	public Dijkstra(Node source, List<Node> nodes, ArrayList<ArrayList<Double>> adjacency) {

		this.source=source;
		this.nodes=nodes;
		dist=new ArrayList<Double>(nodes.size());
		this.adjacency=adjacency;
		
		for(Node n:nodes)
		{
			if(n.equals(source))
			{	dist.add(0.0);
				
			}
			else
			{
				dist.add(Double.MAX_VALUE);
				
			}
			
		
		}

		dijkstra();
	}
	
	ArrayList<Double> getDistances(){
		return dist;
	}
	private void evaluateNeighbours(Node evaluationNode,Set<Node> settled, PriorityQueue<NodeWeight> priorityQueue){
		
		int evaluationNodeIndex=distanceMap.get(evaluationNode);
		
		int i=0;
		double edgeDistance=Double.NEGATIVE_INFINITY;
		double newDistance=Double.NEGATIVE_INFINITY;
		for(Node destination:nodes){
			if(!settled.contains(destination)){
				if(adjacency.get(evaluationNodeIndex).get(i)!=Double.MAX_VALUE){
					edgeDistance=adjacency.get(evaluationNodeIndex).get(i);
					newDistance=dist.get(evaluationNodeIndex)+edgeDistance;
					if(newDistance <dist.get(i)){
						dist.set(i, newDistance);
					
				//	if(priorityQueue.contains(new NodeWeight(destination, dist.get(i))))
				//		System.out.println("stop");
					priorityQueue.add(new NodeWeight(destination, dist.get(i)));
				//	System.out.println("***"+priorityQueue.size());
					}
				}
			}
			i++;
		}
	}
	

		private void dijkstra( ){
		
		
		// initialize

		
		HashSet<Node> settled = new HashSet<Node>();
		
		PriorityQueue<NodeWeight> priorityQueue=new PriorityQueue<NodeWeight>(nodes.size(), new NodeWeightComparator());
		priorityQueue.add(new NodeWeight(source,0.0));
		
		// measure
		while(!priorityQueue.isEmpty()){
			//System.out.println("Queue size:"+priorityQueue.size());
			NodeWeight evaluationNode=priorityQueue.remove();
			settled.add(evaluationNode.getN());
			evaluateNeighbours(evaluationNode.getN(), settled, priorityQueue);
		}
		
	}
	
}

	public CollectiveDistanceMap(Graph graph) {
		super(graph.getWorkingNodes()); // alloca la memoria per le strutture dati
		initialize(graph);
	}
	
	

	protected void initialize(Graph graph)
	{
		
		
		//Dijkstra
		List<Node> nodes=graph.getWorkingNodes();
		
		// create adjacency matrix
		
		ArrayList<ArrayList<Double>> adjacency=new ArrayList<ArrayList<Double>>();
		for(Node n:nodes)
		{
			int nI=distanceMap.get(n);
			ArrayList<Double> row=new ArrayList<Double>();
			for(int i=0;i<nodes.size();i++)
				row.add(Double.MAX_VALUE);
			row.set(nI,0.0);
			Set<Edge> destinations=graph.getAdjacent(n);
			for(Edge e:destinations)
			{
				Node destination=e.getTo();
				double cost=e.getDistance();
				int destinationI=distanceMap.get(destination);
				row.set(destinationI, cost);
				
			}
			adjacency.add(row);
		
		}
		
		
		//
		Iterator<Node> it=nodes.iterator();
		int r=0;
		
		double minW=Double.MAX_VALUE;
		double maxW=Double.NEGATIVE_INFINITY;
		while(it.hasNext()){
			Node n=it.next();
			System.out.println("Processing node "+n.getId());
			


			Dijkstra dijkstra = this.new Dijkstra(n, nodes, adjacency);
			List<Double> W=dijkstra.getDistances();
			//System.out.println(W);
			
			// weight min/max
			
			for(double v:W)
				if(v!=Double.MAX_VALUE)
				{
					if(v<minW)
						minW=v;
					if(v>maxW)
						maxW=v;
				}
			System.out.println(W);
			for (int c=0;c<=r;c++){
				mat.get(r).add(W.get(c));
				
			}
			r++;
		}
		
		shortestDistances = mat;
		
		// mat normalization
		for(r=0;r<mat.size();r++){
			ArrayList<Double> row=mat.get(r);
			for(int c=0;c<row.size();c++)
				if(row.get(c)==Double.MAX_VALUE)
					row.set(c, 1.0);
				else
					row.set(c, (row.get(c)-minW)/(maxW-minW));
		}
		
		
	}
	
public List<ArrayList<Double>> getShortestDistances() {
        return shortestDistances;
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
		
		
		Network n = new Network(fileTestArffName, fileEdgeFileName, fileConfigFileName,fileNhSettingFileName); 
		
		List<Node>  data=n.getGraph().getWorkingNodes();
		long start=System.currentTimeMillis();
		
		CollectiveDistanceMap d=new CollectiveDistanceMap(n.getGraph());
		long end=System.currentTimeMillis();
		
		d.save("Aco2CInput/distance/"+baseArff.replace(".", "_")+".csv",(end-start));
		d.serialize("Aco2CInput/distance/"+baseArff.replace(".", "_")+".serialized");
		
	}

}
