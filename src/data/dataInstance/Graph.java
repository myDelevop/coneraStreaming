package data.dataInstance;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import collective.COLLECTIVESCHEMA;
import data.schema.CollectiveAttribute;
import data.schema.ContinuousAttribute;
import data.schema.FrequencyAttribute;
import data.schema.MeanAttribute;
import data.schema.Schema;
import data.schema.SpeedAttribute;
import data.schema.StdDevAttribute;
import data.schema.WMeanAttribute;
import data.schema.WSpeedAttribute;
import data.schema.WeightI;
import mining.Predictor;

/**
 * 
 * Classe che modella il grafo
 */
public class Graph implements Iterable<Node>{
	
	/**
	 * Struttura dati del grafo
	 */
	private Map<Node,Set<Edge>> graphStructure;

	
	/**
	 * Lista degli attributi comuni ad ogni nodo del grafo
	 */
	private Schema schema;
	
	/**
	 * Mappa dei vicinati
	 */
	private NeighbourhoodStructure nStructure;
	
	/**
	 * Numero dei  neighbourhoods per ogni nodo
	 */
	private int numberOfNeighourhoods;

	
	private WeightI weight;
	
	
	/**
	 * 
	 * @param schema lista di attributi
	 * 
	 * Costruttore del grafo
	 */
/*	public Graph(Schema schema){
		this.graphStructure = new HashMap<Node,Set<Edge>>();
		
		this.schema = schema;
		this.nStructure = null;
		this.numberOfNeighourhoods = 0;
	}*/
	
	public Graph(){
		this.graphStructure = new HashMap<Node,Set<Edge>>();
		
		this.schema = null;
		this.nStructure = null;
		this.numberOfNeighourhoods = 0;
	}
	/**
	 * 
	 * @param n Nodo da aggiungere al grafo
	 * 
	 * Aggiunge il nodo n al grafo
	 */
	public void addNode(Node n){
		graphStructure.put(n, new TreeSet<Edge>());
	}
	
	/**
	 * 
	 * @param from Nodo di partenza
	 * @param to Nodo destinazione
	 * @param w Peso dell'arco
	 * 
	 * Aggiunge un arco con peso w tra i nodi from e to
	 */
	public void addEdge(Node from, Node to, float w){
		if(graphStructure.containsKey(from) && graphStructure.containsKey(to))
			graphStructure.get(from).add(new Edge(to,w));
	}
	
	/**
	 * 
	 * @return lista degli attributi
	 */
	public Schema getSchema(){
		return schema;
	}
	

	/**
	 * 
	 * @return lista degli attributi
	 */
	public void setSchema(Schema schema){
		this.schema=schema;
	}
	
	/**
	 * 
	 * @param n nodo di cui si vuole conosccere l'insieme di adiacenti
	 * @return l'insieme dei nnodi adiacenti di n
	 */
	public Set<Edge> getAdjacent(Node n){
		Set<Edge> adj = null;
		
		if(graphStructure.containsKey(n))
			adj = graphStructure.get(n);
		return adj;
	}
	
	public Set<Node> nodes() {
		return graphStructure.keySet();
	}

	/**
	 * 
	 * @return il numero di nodi del grafo
	 */
	public int size(){
		return graphStructure.size();
	}
	
	private class NeighbourhoodStructure{
		
		private Map< Node, List<Neighbourhood> > structure;
		
		NeighbourhoodStructure(int rmin, int rmax, int step){
			structure = new HashMap< Node, List<Neighbourhood> >();
			Set<Node> nodes = graphStructure.keySet();
			
			for(Node n : nodes){
				List<Neighbourhood> nhList = new LinkedList<Neighbourhood>();
				//AA: added: per evitare he un nodo sia aggiiunto al suo vicinato
				n.setIsVisited(true); 
				Neighbourhood nh = new Neighbourhood(Graph.this,n,rmin);
				nhList.add(nh);
				for (int i = step;rmin+i<=rmax;i += step){
					nh = new Neighbourhood(Graph.this,nh,step);
					nhList.add(nh);
				}
				
				structure.put(n, nhList);
				//clearVisitingStructure();
				//AA: opero solo sui nodi inclusi in nhList , che sono quelli già visitti
				n.setIsVisited(false);
				clearVisitingStructure(nhList);
			}		
		}
		
		Neighbourhood getNeighbourhood(Node n, int index){
			return structure.get(n).get(index-1);
		}
		/*
		private void clearVisitingStructure(){
			for(Node n:graphStructure.keySet())
				n.setIsVisited(false);
		}
		*/
		private void clearVisitingStructure(List<Neighbourhood> nodes){
			Neighbourhood n=nodes.get(nodes.size()-1);
			for(Edge e:n)
				e.getTo().setIsVisited(false);
		}
	}
	
	public void createNeighbourhoodStructure(int rmin,  int rmax, int step){
		numberOfNeighourhoods = ((rmax-rmin)/step)+1;
		nStructure = new NeighbourhoodStructure(rmin,rmax,step);
	//	computeCollectiveValues();
	}
	
	public int getNumberOfNeighbourhoods(){
		return numberOfNeighourhoods;
	}
	
	
	
	public void computeCollectiveValuesOnline(COLLECTIVESCHEMA schema) {
	    
	    switch (schema) {
        case Mean:
            computeMeanCollectiveValues();
            break;

        case Frequency:
            computeFrequencyCollectiveValuesOnline();
            break;
        
        case Average:
            break;

        case GI:
            break;

        case Cluster:
            break;
        
        default:
            // TODO throw new collectiveSchemaNotFoundException
            System.err.println("Use a valid collective schema");
            break;
        }
	    
	    
	    
	    
	}
	/**
	 * 
	 * @param nodeSet nodi dei quali si intendono calcolare i valori collettivi
	 */
	private void computeMeanCollectiveValues(){
		
		MeanAttribute ma = null;
		WMeanAttribute wma = null;
		StdDevAttribute sdev = null;
		SpeedAttribute speed = null;
		WSpeedAttribute wspeed = null;
		
		Iterator<CollectiveAttribute> itc = schema.getCollectiveAttrIterator();
		
		int numAttributes=schema.numberOfCollectiveAttributes()/numberOfNeighourhoods;
	
		//while(itc.hasNext())
		for(	int nA=0;nA<numAttributes;nA++){
			CollectiveAttribute currentCA = itc.next();
			
			if(currentCA instanceof MeanAttribute)
				ma = (MeanAttribute) currentCA;
			else if(currentCA instanceof WMeanAttribute)
				wma = (WMeanAttribute) currentCA;
			else if(currentCA instanceof StdDevAttribute)
				sdev = (StdDevAttribute) currentCA;
			else if(currentCA instanceof SpeedAttribute)
				speed = (SpeedAttribute) currentCA;
			else if(currentCA instanceof WSpeedAttribute)
				wspeed = (WSpeedAttribute)currentCA;
		}
		
		for(Node n : this){

			
			List<ContinuousValue> cv = new LinkedList<ContinuousValue>();
			List<Double> means = new LinkedList<Double>();
			List<Double> wmeans = new LinkedList<Double>();
			
			Neighbourhood nh = nStructure.getNeighbourhood(n,1);
			
			
			try{
			ma.compute(nh);
			cv.add(new ContinuousValue(ma.get()));
			means.add(ma.get());
			}
			catch(NullPointerException e){}
			try{wma.compute(nh);
			cv.add(new ContinuousValue(wma.get()));
			wmeans.add(wma.get());
			}
			catch(NullPointerException e){}
			try{
			sdev.compute(nh,ma.get());
			cv.add(new ContinuousValue(sdev.get()));
			}
			catch(NullPointerException e){
				e.printStackTrace();
			}
			for(int i=2; i<=numberOfNeighourhoods;i++){
				nh = nStructure.getNeighbourhood(n,i);
				try{
				ma.compute(nh, ma);
				cv.add(new ContinuousValue(ma.get()));
				means.add(ma.get());
				}
				catch(NullPointerException e){}
				try{
				wma.compute(nh,wma);
				cv.add(new ContinuousValue(wma.get()));
				wmeans.add(wma.get());
				}
				catch(NullPointerException e){}
				try{sdev.compute(nh,ma.get(),sdev);
				cv.add(new ContinuousValue(sdev.get()));
				}
				catch(NullPointerException e){}
			}
			
			for(int i=1;i<numberOfNeighourhoods;i++){
				try{speed.compute(means.get(i-1), means.get(i));
				cv.add(new ContinuousValue(speed.get()));
				}
				catch(NullPointerException | IndexOutOfBoundsException e){}
				try{
				wspeed.compute(wmeans.get(i-1),wmeans.get(i));
				cv.add(new ContinuousValue(wspeed.get()));
				}
			catch(NullPointerException  | IndexOutOfBoundsException e){}
			}
			
			n.setCollectiveValues(cv);
		}
	}

	
private void computeFrequencyCollectiveValuesOnline(){
		
		FrequencyAttribute fa = null;
		
		
			for(Node n : this){
			//	System.out.println("Node "+ n.getId()+ " Y"+n.getTarget()+ " PY"+n.getPredictedTarget());
		//		nStructure.getNeighbourhood(n,1).print();;
				List<ContinuousValue> cv = new LinkedList<ContinuousValue>();
				Iterator<CollectiveAttribute> itc = schema.getCollectiveAttrIterator();
				
				LinkedList<FrequencyAttribute> previousNeighbourhhodFrequencyAttribute=new LinkedList<FrequencyAttribute>();
				
				Collection<Double> bins=((ContinuousAttribute)schema.getTarget()).discreteBins();
				//initialization Neigh=1
				{
					Neighbourhood nh = nStructure.getNeighbourhood(n,1);
					
					
					Iterator<Double> itDiscreteSplitValue=bins.iterator();
					
					double inf=itDiscreteSplitValue.next();
					while(itDiscreteSplitValue.hasNext()){
						double sup=itDiscreteSplitValue.next();
						CollectiveAttribute currentCA = itc.next(); // current collective atribute
						//devo impostare estermi inf-sup della siscretizazzione
						if(currentCA instanceof FrequencyAttribute){
							fa = (FrequencyAttribute) currentCA;
							fa.setInf(inf);
							fa.setSup(sup);
						}
						try{
						//	System.out.println("Compute "+ fa);
							fa.compute(nh);
							cv.add(new ContinuousValue(fa.get()));
							previousNeighbourhhodFrequencyAttribute.add(fa);
						}
						catch(NullPointerException e){e.printStackTrace();}
						
						inf=sup;
					}
				}
			//	n.setCollectiveValues(cv);
				
			
			   //Neigh=2,3,...
				
				for(int i=2;i<=numberOfNeighourhoods;i++)
				{	
					Neighbourhood nh = nStructure.getNeighbourhood(n,i);
					
										
					Iterator<Double> itDiscreteSplitValue=bins.iterator();
					
					double inf=itDiscreteSplitValue.next();
					int attPosition=-1;
					while(itDiscreteSplitValue.hasNext()){
						attPosition++;
						double sup=itDiscreteSplitValue.next();
						CollectiveAttribute currentCA = itc.next(); // current collective atribute
						//devo impostare estermi inf-sup della siscretizazzione
						if(currentCA instanceof FrequencyAttribute){
							fa = (FrequencyAttribute) currentCA;
							fa.setInf(inf);
							fa.setSup(sup);
						}
						try{
					//		System.out.println("Compute "+ fa);
						//	fa.compute(nh);
						//	System.out.println(fa.get());
							fa.compute(nh,previousNeighbourhhodFrequencyAttribute.get(attPosition));
						//	System.out.println(fa.get());
							cv.add(new ContinuousValue(fa.get()));
							previousNeighbourhhodFrequencyAttribute.set(attPosition, fa); // aggiorno le freqenze precomputate per il prossimo neighbouthood
						}
						catch(NullPointerException e){System.out.println(e);}
						
						inf=sup;
					}
				}
			//	System.out.println(cv);
				n.setCollectiveValues(cv);
			}
				
			
		
	}
	
public void computeCollectiveValuesOffline(COLLECTIVESCHEMA schema){
    
    switch (schema) {
    case Mean:
        computeMeanCollectiveValues();
        break;

    case Frequency:
        computeFrequencyCollectiveValuesOffline();
        break;
    
    case Average:
        break;

    case GI:
        break;

    case Cluster:
        break;
    
    default:
        // TODO throw new collectiveSchemaNotFoundException
        System.err.println("Use a valid collective schema");
        break;
    }
    
}
	
private void computeFrequencyCollectiveValuesOffline(){
	
	FrequencyAttribute fa = null;
	
	
		for(Node n : this){
			List<ContinuousValue> cv = new LinkedList<ContinuousValue>();
			Iterator<CollectiveAttribute> itc = schema.getCollectiveAttrIterator();
			
			LinkedList<FrequencyAttribute> previousNeighbourhhodFrequencyAttribute=new LinkedList<FrequencyAttribute>();
			
			int nAtt=schema.numberOfCollectiveAttributes()/numberOfNeighourhoods;
			
			//initialization Neigh=1
			{
				Neighbourhood nh = nStructure.getNeighbourhood(n,1);
				
				for(int i=0;i<nAtt;i++){
					
					CollectiveAttribute currentCA = itc.next(); // current collective atribute
					//devo impostare estermi inf-sup della siscretizazzione
					if(currentCA instanceof FrequencyAttribute){
						fa = (FrequencyAttribute) currentCA;
					}
					try{
					//	System.out.println("Compute "+ fa);
						fa.compute(nh);
						cv.add(new ContinuousValue(fa.get()));
						previousNeighbourhhodFrequencyAttribute.add(fa);
					}
					catch(NullPointerException e){}
					
				}
			}
			n.setCollectiveValues(cv);
			
		
		   //Neigh=2,3,...
			for(int i=2;i<=numberOfNeighourhoods;i++)
			{	
				Neighbourhood nh = nStructure.getNeighbourhood(n,i);
				
									
				int attPosition=-1;
				for(i=0;i<nAtt;i++){
					
					attPosition++;
					CollectiveAttribute currentCA = itc.next(); // current collective atribute
					//devo impostare estermi inf-sup della siscretizazzione
					if(currentCA instanceof FrequencyAttribute){
						fa = (FrequencyAttribute) currentCA;
					}
					try{
				//		System.out.println("Compute "+ fa);
					//	fa.compute(nh);
					//	System.out.println(fa.get());
						fa.compute(nh,previousNeighbourhhodFrequencyAttribute.get(attPosition));
					//	System.out.println(fa.get());
						cv.add(new ContinuousValue(fa.get()));
						previousNeighbourhhodFrequencyAttribute.set(attPosition, fa); // aggiorno le freqenze precomputate per il prossimo neighbouthood
					}
					catch(NullPointerException e){}
					
				}
			}
			n.setCollectiveValues(cv);
		}
			
		
	
}



/**
	 * 
	 * @param node Nodo del quale si vuole il vicinato
	 * @param index indice del neighbourhood 
	 * @return Neighbourhood del nodo node di indice index
	 */
	public Neighbourhood getNeighbourhood(Node node, int index){
		if(index<1) throw new IllegalArgumentException("index < 1");
		return nStructure.getNeighbourhood(node, index); // n;
	}
	
	
	

	
	/**
	 * 
	 * @return number of nodes having isSample=true (inital working set)
	 */
	public int countWorkingNodes(){
		int ct=0;
		for(Node n:this){
			if(!n.isSample())
				ct++;
		}
		return ct;
	}

	
	/**
	 * 
	 * @return list of working nodes
	 */	
	public List<Node> getWorkingNodes(){
		List<Node> l=new ArrayList<Node>();
		for(Node n:this){
				if(!n.isSample())
					l.add(n);
			}
		return l;
		
	}
	
	
	public List<Node> getTrainingNodes(){
		List<Node> l=new LinkedList<Node>();
		for(Node n:this){
			if(n.isSample() || n.isWorkingToSample())
				l.add(n);
		}
		return l;
		
	}

	/**
	 * Itera sull'insieme dei nodi
	 */
	@Override
	public Iterator<Node> iterator() {
		return graphStructure.keySet().iterator();
	}
	
	public String toString(){
		String str="";
		
		for(Node n : graphStructure.keySet()){
			str+= n + ": "+ graphStructure.get(n) + "\n\n";
		}
		return str;
	}
	
	/*
	 * Restituisce le statistiche sum Y e sum Y^2
	 */
	public double[] getSumsY(){
		double sum[]={0.0,0.0}; //sumY, sumY2
		for(Node n:this){
			double Y;
			if(n.isSample() || n.isWorkingToSample())
				Y= (double)n.getTarget().getValue();
			else
				Y = (double)n.getPredictedTarget().getValue();
			sum[0]+=Y;
			sum[1]+=Math.pow(Y, 2);
		}
		return sum;
	}

	public WeightI getWeight() {
		return weight;
	}

	public void setWeight(WeightI weight) {
		this.weight = weight;
	}
	
	
	public void saveTrainingSample(String fileName) throws IOException,FileNotFoundException{
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "utf-8"));
		for(Node n:this){
			if(n.isSample() || n.isWorkingToSample())
				writer.write(n.getId()+"\n");
		}
		writer.close();
		
	}
}
