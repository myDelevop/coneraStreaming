package data.schema;

import java.util.Set;

/**
 *  Classe che estende la classe Attribute e modella un attributo discreto rappresentando l’insieme di valori distinti del relativo dominio. 
 *
 */
public class DiscreteAttribute extends Attribute{

	/**
	 * Set di String, una per ciascun valore discreto 
	 */
	private Set<String> values;	
	
	/**
	 * 
	 * @param name nome simbolico dell'attributo
	 * @param index identificativo numerico dell'attributo
	 * @param values valori discreti che ne costituiscono il dominio 
	 * 
	 * invoca il costruttore della classe madre e  avvalora il set values con i valori discreti in input.
	 */
	public DiscreteAttribute(String name, int index, Set<String> values) {
		super(name, index);
		this.values = values;
	}
	
	/**
	 * 
	 * @return  restituisce la cardinalità del membro values 
	 */
	int getNumberOfDistinctValues(){
		return values.size();
	}
	
	/**
	 * 
	 * @return  insieme dei valori discreti dell'attributo 
	 */
	public Set<String> getValues(){
		return this.values;
	}
	
	public String toString(){
		return this.getName()+ "["+ this.getIndexSchema()+" "+ this.getIndexValue()+"]["+this.isNodeId()+" "+this.isTarget()+" "+this.isNeglected();
	}
	
}
