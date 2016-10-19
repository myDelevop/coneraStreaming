package data.dataInstance;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

public class Neighbourhood implements Iterable<Edge>{
	
	private Neighbourhood internal;
	private Map<Node,Edge> neighbourhood;
	private int r;
	
	Neighbourhood(Graph graph, Node n, int rmin){
		internal = null;
		neighbourhood = new HashMap<Node,Edge>();
		r = 1;
		
		Queue inputQ = new Queue();
		for(Edge e:graph.getAdjacent(n)){
			inputQ.enqueue(e);
			neighbourhood.put(e.getTo(), e);
		}
		setVisitingStructure();
		
		for(int i=2;i<=rmin;i++){
			r++;
			Queue outputQ = new Queue();
			while(!inputQ.isEmpty()){
				Edge currEdge = (Edge)inputQ.first();
				inputQ.dequeue();
				double d = currEdge.getDistance();
				
				for(Edge adjacent:graph.getAdjacent(currEdge.getTo())){
					Node to = adjacent.getTo();
					if(!to.isVisited()){
						Edge adjClone = (Edge) adjacent.clone();
						adjClone.addDistance(d);
						
						Edge e = (Edge) neighbourhood.get(to);
						if(e == null){
							outputQ.enqueue(adjClone);
							neighbourhood.put(to, adjClone);
						}
						else{
							if(e.getDistance()>adjClone.getDistance()){
								outputQ.enqueue(adjClone);
								neighbourhood.put(to, adjClone);
							}
						}
					}
				}
			}
			inputQ = outputQ;
		}
		
		if(rmin!=1)
			setVisitingStructure();
	}
	
	Neighbourhood(Graph graph, Neighbourhood nh, int step){
		internal = nh;
		neighbourhood = new HashMap<Node,Edge>();
		r = internal.r+1;
		
		Queue inputQ = new Queue();
		Collection<Edge> edges = internal.neighbourhood.values();
		for(Edge currEdge: edges){
			double d = currEdge.getDistance();
			for(Edge adjacent:graph.getAdjacent(currEdge.getTo())){
				Node to = adjacent.getTo();
				if(!to.isVisited()){			
					Edge adjClone = (Edge) adjacent.clone();
					adjClone.addDistance(d);
					
					Edge e = (Edge) neighbourhood.get(to);
					if(e == null){
						inputQ.enqueue(adjClone);
						neighbourhood.put(to, adjClone);
					}
					else{
						if(e.getDistance()>adjClone.getDistance()){
							inputQ.enqueue(adjClone);
							neighbourhood.put(to, adjClone);
						}
					}
				}
			}
		}
		
		setVisitingStructure();
		
		for(int i=2;i<=step;i++){
			r++;
			Queue outputQ = new Queue();
			while(!inputQ.isEmpty()){
				Edge currEdge = (Edge)inputQ.first();
				inputQ.dequeue();
				double d = currEdge.getDistance();
				
				for(Edge adjacent:graph.getAdjacent(currEdge.getTo())){
					Node to = adjacent.getTo();
					if(!to.isVisited()){
						Edge adjClone = (Edge) adjacent.clone();
						adjClone.addDistance(d);
						
						Edge e = (Edge) neighbourhood.get(to);
						if(e == null){
							outputQ.enqueue(adjClone);
							neighbourhood.put(to, adjClone);
						}
						else{
							if(e.getDistance()>adjClone.getDistance()){
								outputQ.enqueue(adjClone);
								neighbourhood.put(to, adjClone);
							}
						}
					}
				}
			}
			inputQ = outputQ;
		}
		
		if(step!=1)
			setVisitingStructure();
	}

	private void setVisitingStructure(){
		for(Node n : neighbourhood.keySet())
			n.setIsVisited(true);
	}
	
	@Override
	public Iterator<Edge> iterator() { 
		if(internal != null) return new NeighbourhoodIterator<Edge>(internal.iterator(),neighbourhood.values().iterator());
			else return neighbourhood.values().iterator();
	}

	
	/**
	 * Questo metodo itera sui nodi esterni del vicinato ; scludenti quelli inclusi nella zona interna
	 * @return
	 */
	public Iterator<Edge> externalIterator() { 
		return neighbourhood.values().iterator();
	}
	
	
	public String toString(){
		String s = "[";
		Iterator<Edge> it = this.iterator();
		while(it.hasNext()){
			s += it.next();
			if(it.hasNext())
				s+=", ";
		}
		s+="]";
		return s;
	}

	public int size() {
		if(internal == null) return neighbourhood.size();
		else return internal.size()+neighbourhood.size();
	}

	public int getRadius() {
		return r;
	}
	
	
	public void print(){
		TreeSet<Edge> ed=new TreeSet(neighbourhood.values());
		for(Edge e:ed){
				
			Node to = e.getTo();
			System.out.println(to.getId() + " Y"+to.getTarget()+ " PY"+to.getPredictedTarget()+ "d "+ e.getDistance());
		}
		
	}
}
