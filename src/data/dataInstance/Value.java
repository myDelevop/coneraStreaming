package data.dataInstance;

import java.io.Serializable;

/**
 * 
 * classe astratta che modella un generico value 
 *
 */
public abstract class Value implements Serializable{

	private Object value;
	
	/**
	 * 
	 * @param value
	 *
	 * inizializza il valore di value con il parametro passato come argomento al costruttore  
	 */
	Value(Object value) {
		this.value  = value;
	}
	
	/**
	 * 
	 * @return valore di value
	 * restituisce il membro value
	 */
	public Object getValue(){
		return this.value;
	}
	
	public String toString(){
		return value.toString();
	}
	
	public int hashCode(){
		return value.hashCode();
	}
	
	public boolean equals(Object o){
		return value.equals(((Value)o).value);
	}
}
