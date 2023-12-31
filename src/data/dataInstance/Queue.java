package data.dataInstance;

public class Queue {

	private Record begin = null;

	private Record end = null;
	
	private class Record {
		
 		 Object elem;

 		 Record next;

		 Record(Object e) {
			this.elem = e; 
			this.next = null;
		}
		 
		 public String toString(){
			 return elem.toString();
		 }
	}
	
	 public boolean isEmpty() {
		return this.begin == null;
	}

	 public void enqueue(Object e) {
		if (this.isEmpty())
			this.begin = this.end = new Record(e);
		else {
			this.end.next = new Record(e);
			this.end = this.end.next;
		}
	}

	 public Object first(){
		return this.begin.elem;
	}

	 public void dequeue(){
		if(this.begin==this.end){
			if(this.begin==null)
			System.out.println("The queue is empty!");
			else
				this.begin=this.end=null;
		}
		else{
			begin=begin.next;
		}
		
	}

	public String toString(){
		String s = "[";
		Record a = begin;
		while(a!=null){
			s+=a+", ";
			a = a.next;
		}
		
		s+="]";
		return s;
	}
}