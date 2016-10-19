package data.schema;

import java.io.Serializable;

/**
 * Classe astratta che modella un generico attributo discreto o continuo
 *
 */
public abstract class Attribute implements Serializable{

	/**
	 * nome dell'attributo
	 */
	private String name;
	
	/**
	 * identificativo numerico dell'attributo 
	 */
	private int indexSchema;
	
	/**
	 * identificativo numerico dell'attributo all'interno di values di Node
	 */
	private int indexValue;
	
	/**
	 * indica se l'attributo è da trascurare
	 */
	private boolean isNeglected;
	
	/**
	 * indica se è l'attributo Id
	 */
	private boolean isNodeId;
	
	/**
	 * indica se è l'attributo target
	 */
	private boolean isTarget;
	  
	/**
	 * 
	 * @param name nome dell'attributo
	 * @param index identificativo numerico dell'attributo
	 * 
	 * inizializza i valori dei membri
	 */
	public Attribute(String name, int  index){
		this.name = name;
		this.indexSchema = index;
		this.indexValue = 0;
		this.isNeglected  = false;
		this.isNodeId = false;
		this.isTarget = false;
	}
	
	/**
	 * 
	 * @return restituisce il nome dell'attributo 
	 */
	public String getName(){
		return this.name;
	}
	
	/**
	 * 
	 * @return  restituisce l'identificativo numerico dell'attributo 
	 */
	public int getIndexSchema(){
		return this.indexSchema;
	}
	
	/**
	 * 
	 * @param i indice dell'attributo in values
	 * avvalora indexValue
	 */
	public void setIndexValue(int i){
		this.indexValue = i;
	}
	
	/**
	 * 
	 * @return l'indice dell'attributo in values
	 */
	public int getIndexValue(){
		return this.indexValue;
	}
	
	/**
	 * 
	 * @param n
	 * avvalora isNeglected
	 */
	public void setIsNeglected(boolean n){
		this.isNeglected = n;
	}
	
	/**
	 * 
	 * @return valore dell'attributo isNegleted
	 */
	boolean isNeglected(){
		return this.isNeglected;
	}
	
	/**
	 * 
	 * @param n
	 * avvalora isTarget
	 */
	public void setIsTarget(boolean n){
		this.isTarget = n;
	}

	/**
	 * 
	 * @return isTarget
	 */
	boolean isTarget(){
		return this.isTarget;
	}
	
	/**
	 * 
	 * @param n
	 * avvalora isNodeId
	 */
	public void setIsNodeId(boolean n){
		this.isNodeId = n;
	}
	
	/**
	 * 
	 * @return isNodeId
	 */
	boolean isNodeId(){
		return this.isNodeId;
	}
	
	public String toString(){
		return name;
	}
	
}
