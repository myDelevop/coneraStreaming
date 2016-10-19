package data.dataInstance;

/**
 * 
 * Classe che modella l'arco
 *
 */
public class Edge implements Comparable<Edge>, Cloneable{

	/**
	 * Nodo di arrivo dell'arco
	 */
	private Node to;
	
	/**
	 * Peso dell'arco
	 */
	private float distance;
	
	/**
	 * 
	 * @param to nodo di arrivo
	 * @param w peso
	 * 
	 * Costruttore
	 */
	Edge(Node to, float w) {
		this.to = to;
		this.distance = w;
	}
	
	/**
	 * 
	 * @return peso dell'arco
	 */
	public float getDistance(){
		return this.distance;
	}
	
	/**
	 * 
	 * @return Nodo di arrivo dell'arco
	 */
	public Node getTo(){
		return this.to;
	}

	
	public	Object clone() {
		Object o = null;
		try {
			o = super.clone();
		} catch(CloneNotSupportedException e) {
			System.err.println("Edge can't clone");
		}
		return o;
	}

	/**
	 * confronta due archi in base al peso
	 */
	@Override
	public int compareTo(Edge e) {
		return this.distance <= e.getDistance()? -1 : 1;
	}
	
	public String toString(){
		return to.getId()+":"+distance;
	}

	void addDistance(double w) {
		this.distance += w;	
	}

}
