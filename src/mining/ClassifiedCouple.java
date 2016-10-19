package mining;

import data.dataInstance.Node;

public class ClassifiedCouple implements Comparable<ClassifiedCouple>{
	private Node n;
	private Object value;
	
	ClassifiedCouple(Node n, Object v){
		this.n = n;
		this.value = v;
	}
	
	Object getValue(){
		return value;
	}
	
	Node getNode(){
		return n;
	}
	public int compareTo(ClassifiedCouple o) {
		return n.compareTo(o.n);
	}
	
	public String toString(){
		return n.toString() +" :"+value;
	}
}
