package data.schema;

import java.util.Iterator;

import java.util.LinkedList;
import java.util.Collection;

import weka.core.FastVector;

/**
 * 
 *  Classe che estende la classe Attribute e modella un attributo numerico rappresentandone il dominio [min,max] 
 *
 */
public class ContinuousAttribute extends Attribute{

	/**
	 * estremo superiore dell'intervallo
	 */
	private double max; 
	
	/**
	 * estremo inferiore dell'intervallo
	 */
	private double min ;
	
	private Collection<Double> domain=new LinkedList<Double>();
	private int discretizationN=0;
	
	private DISCRETIZATIONTYPE discretizationAlgorithm;
	
	/**
	 * 
	 * @param name nome dell’attributo
	 * @param index identificativo numerico dell'attributo 
	 *
	 * invoca il costruttore della classe madre e avvalora i membri
	 */
	
	

	public ContinuousAttribute(String name, int index) {
		super(name, index);
		this.max = Double.NEGATIVE_INFINITY;
		this.min = Double.POSITIVE_INFINITY;
	}
	
	
	

	/**
	 * 
	 * @param min estremo inferiore
	 * 
	 * avvalora min
	 */
	public void setMin(double min){
		this.min = min;
	}

	
	/**
	 * 
	 * @param min estremo inferiore
	 * 
	 * aggiorna min
	 */
	public void updateMin(double min){
		if(min<this.min) this.min = min;
	}
	
	/**
	 * 
	 * @param max estremo superiore
	 * 
	 * avvalora max
	 */
	public void setMax(double max){
		this.max = max;
	}

	/**
	 * 
	 * @param max estremo superiore
	 * 
	 * aggiorna max
	 */
	public void updateMax(double max){
		
		if(max>this.max) this.max = max;
	}	
	
	

	/**
	 * 
	 * @param v nuovo valore da aggiungere al dominio
	 * 
	 * aggiorna domain
	 */
	public void updateDomain(double v){
		domain.add(v);
	}	
	/**
	 * 
	 * @param max estremo superiore
	 * 
	 * avvalora max
	 */
	public void setdiscretizationN(int discretizationN){
		this.discretizationN = discretizationN;
	}
	
	/**
	 * 
	 * @return estremo inferiore dell'intervallo 
	 */
	public double getMin(){
		return this.min;
	}
	
	/**
	 * 
	 * @return estremo superiore dell'intervallo 
	 */
	public double getMax(){
		return this.max;
	}
	
	public String toString(){
		//return this.getName()+ "["+ this.getIndexSchema()+" "+ this.getIndexValue()+"]["+this.isNodeId()+" "+this.isTarget()+" "+this.isNeglected()+"]";
		return super.toString();
	}



	/*
	 * (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 * Return iterator discre values by equal width discretization
	 */
	public Collection<Double> discreteBins() {
		// TODO Auto-generated method stub
		
		Collection<Double> listSplitPoints=null;
		Discretization d=null;
		int num= discretizationN;
		switch(discretizationAlgorithm){
			case EW:
				d=new EWDiscretization(min, max);
				break;
			case EF:
				
			    if(num>=domain.size() || num<=1)
			    	
			    	num=(int)Math.sqrt(domain.size()); //stima automatica discretizzazione
			   	if(num<2)
			    	num=2;
			   
				d=new EFDiscretization(domain);
				break;
			default :
				new RuntimeException("No valid discretization algorithm!");
		}
		
		try{
			if(min==max) // un solo valore distinto nel dominio
			{
				listSplitPoints=new LinkedList<Double>();
				listSplitPoints.add(Double.NEGATIVE_INFINITY);
				listSplitPoints.add(min);
				listSplitPoints.add(Double.POSITIVE_INFINITY);
			}else
			listSplitPoints=d.compute(num);
		}
		catch (Exception e){e.printStackTrace();};
		return listSplitPoints;

	}
	
	
	/*
	 * Restituisce il valore discreto associato al valroe reale sulla base della discretizzazione equalwidth
	 */
	public String discreteValue(double continuousValue) throws NullValueException{
		String discrete="";
		Iterator <Double> itDiscreteSplitPoint=discreteBins().iterator();
		double inf=itDiscreteSplitPoint.next();
		//int l=1;
		while(itDiscreteSplitPoint.hasNext()){
			double sup=itDiscreteSplitPoint.next();
			if(continuousValue>inf && continuousValue <=sup)
			{
				//String value="C"+l;
				String value=inf+"__"+sup;
				value=value.replace(".", "_");
				return value;
			}
			inf=sup;
			//l++;
		}
		throw new NullValueException();
	}

	
	static public String discreteValue(double continuousValue, Collection<Double> discreteBins) throws NullValueException{
		String discrete="";
		Iterator <Double> itDiscreteSplitPoint=discreteBins.iterator();
		double inf=itDiscreteSplitPoint.next();
		//int l=1;
		while(itDiscreteSplitPoint.hasNext()){
			double sup=itDiscreteSplitPoint.next();
			if(continuousValue>inf && continuousValue <=sup)
			{
				//String value="C"+l;
				String value=inf+"__"+sup;
				value=value.replace(".", "_");
				return value;
			}
			inf=sup;
			//l++;
		}
		throw new NullValueException();
	}
	/*
	 * Restituisce il dominio discreto associato alla discretizzazione della variabile corrente
	 */
	public FastVector discreteDomain()
	{
		FastVector nominalValues = new FastVector();
		Iterator <Double> itDiscreteSplitPoint=discreteBins().iterator();
		double inf=itDiscreteSplitPoint.next();
		//int l=1;
		while(itDiscreteSplitPoint.hasNext()){
			double sup=itDiscreteSplitPoint.next();
			//String value="C"+l++;;
			String value=inf+"__"+sup;
			value=value.replace(".", "_");
			nominalValues.addElement(value);
			inf=sup;
		}
		return nominalValues;
	}



	public DISCRETIZATIONTYPE getDiscretizationAlgorithm() {
		return discretizationAlgorithm;
	}



	public void setDiscretizationAlgorithm(DISCRETIZATIONTYPE discretizationAlgorithm) {
		this.discretizationAlgorithm = discretizationAlgorithm;
	}
	
	
	public double getScaled(double v){
		return (v-min)/(max-min);
	}
}
