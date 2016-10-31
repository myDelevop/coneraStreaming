package data.dataInstance;

import java.io.Serializable;
import java.util.List;



/**
 * 
 * classe che modellla un nodo dell'arco
 *
 */
public class Node implements Comparable<Node>,Serializable{
	
	/**
	 * identificativo del nodo
	 */
	private Value id;
	
	/**
	 * lista dei valori contenuti nel nodo
	 */
	private List<Value> values;
	
	/**
	 * valore target
	 */
	private ContinuousValue target;
	
	/**
	 * valore predetto del target
	 */
	private Value predictedTarget;
	
	private Value previousPredictedTarget;
	
	/**
	 * stabilisce se il nodo è di training
	 */
	private boolean isSample;

	
	
	/**
	 * stabilisce se il nodo è di working, ma è stata acquisita la sua etichetta
	 */
	private boolean isWorkingToSample=false;
	
	/**
	 * lista di valori collettivi del nodo
	 */
	private List<ContinuousValue> collectiveValues;
	
	
	private boolean isVisited = false;
	
	private Cluster cluster=null;
	
	private Cluster descriptiveCluster = null;
	
	private Cluster collectiveCluster = null;
//	
	/**
	 * 
	 * @param id identificativo
	 * @param values lista di valori
	 * @param target valore target
	 * 
	 * Costruttore del nodo
	 */
	public Node(Value id, List<Value> values, ContinuousValue target){
		this.id=id;
		this.values=values;
		this.target=target;
		this.predictedTarget=null;
		this.isSample=false;
	}

	
	public Node(Value id, List<Value> values, ContinuousValue target, Cluster cluster){
		this(id,values,target);
		this.cluster=cluster;
	}
	
	/**
	 * 
	 * @param predictedTarget target predetto
	 * 
	 * avvalora predictedTarget;
	 */
	public void setPredictedTarget(Value predictedTarget){
		this.predictedTarget=predictedTarget;
	}
	
	/**
	 * 
	 * @param i 
	 * 
	 * avvalora isSample
	 */
	public void setIsSample(boolean i){
		this.isSample=i;
	}
	
	/**
	 * 
	 * @return  isSample
	 */
	public boolean isSample(){
		return isSample;
	}
	
	/**
	 * 
	 * @return identificativo del nodo
	 */
	public Value getId(){
		return id;
	}
	
	/**
	 * 
	 * @return lista di valori del nodo
	 */
	public List<Value> getValues(){
		return values;
	}
	
	/**
	 * 
	 * @param i indice
	 * @return il valore alla posizione i
	 */
	public Value getValue(int i){
		return values.get(i);
	}
	
	/**
	 * @param cv valori collettivi da aggiungere al nodo
	 */
	public void setCollectiveValues(List<ContinuousValue> cv){
		this.collectiveValues = cv;
	}
	
	/**
	 * 
	 * @return lista di valori collettivi del nodo
	 */
	public List<ContinuousValue> getCollectiveValues(){
		return collectiveValues;
	}
	
	/**
	 * 
	 * @param i indice
	 * @return il valore alla posizione i
	 */
	public Value getCollectiveValue(int i){
		return collectiveValues.get(i);
	}
	
	/**
	 * 
	 * @return valore target
	 */
	public ContinuousValue getTarget(){
		return target;
	}
	
	/**
	 * 
	 * @return valore target predetto 
	 */
	public Value getPredictedTarget(){
		return predictedTarget;
	}

	/**
	 * confronta due nodi in base all'identificativo
	 */
	@Override
	public int compareTo(Node n) {
		Integer d = (int)this.id.getValue();
		return d.compareTo((int)n.getId().getValue());
		//return (this.id.getValue().equals(n.getId().getValue()))? 0:((int)this.id.getValue() < (int)n.getId().getValue()?-1:1);
	}
	
	public String toString(){
		return Integer.toString((int)id.getValue()) +"\ty"+target.getValue()+"\tpY"+predictedTarget+
				"\tppY"+previousPredictedTarget;
	
	}
	
	public int hashCode(){
		return id.hashCode();
	}
	
	public boolean equals(Object o){
		return id.equals(((Node)o).id);
	}
	
	void setIsVisited(boolean isVisited){
		this.isVisited = isVisited;
	}

	boolean isVisited() {
		return this.isVisited;
	}

	public boolean isWorkingToSample() {
		return isWorkingToSample;
	}

	public void setWorkingToSample(boolean isWorkingToSample) {
		this.isWorkingToSample = isWorkingToSample;
	}


	public Cluster getCluster() {
		return cluster;
	}


	public void setCluster(Cluster c) {
		// TODO Auto-generated method stub
		this.cluster=c;
		
	}


	public Value getPreviousPredictedTarget() {
		return previousPredictedTarget;
	}


	public void setPreviousPredictedTarget(Value previousPredictedTarget) {
		this.previousPredictedTarget = previousPredictedTarget;
	}


    public Cluster getDescriptiveCluster() {
        return descriptiveCluster;
    }


    public void setDescriptiveCluster(Cluster descriptiveCluster) {
        this.descriptiveCluster = descriptiveCluster;
    }


    public Cluster getCollectiveCluster() {
        return collectiveCluster;
    }


    public void setCollectiveCluster(Cluster colleptiveCluster) {
        this.collectiveCluster = colleptiveCluster;
    }


	
}
