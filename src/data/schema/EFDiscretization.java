package data.schema;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Discretize;

public class EFDiscretization implements Discretization {

	private Instances domain;
	
	

	
	public EFDiscretization(Collection<Double> domain) {
		// TODO Auto-generated constructor stub
		FastVector attributes = new FastVector();
		attributes.addElement(new weka.core.Attribute("TARGET",0));
		this.domain = new Instances("EWSet",attributes,0); //creo il trainingSet vuoto
		for(double v:domain){
			Instance i=new Instance(attributes.size());
			i.setDataset(this.domain);
			i.setValue(0, v);
			this.domain.add(i);
		}
		
	
	
	}
	@Override
	public List<Double> compute(int discretizationN) throws Exception{
		// TODO Auto-generated method stub
		
		Discretize filter = new Discretize();
	    filter.setInputFormat(domain);

	    filter.setOptions(new String("-F -B "+discretizationN+" -M -1.0 -R first-last").split(" "));
	    Filter.useFilter(domain, filter);
	    double cuts[]=filter.getCutPoints(0);
	    List<Double> listSplitPoints =new LinkedList<Double>();
		listSplitPoints.add(Double.NEGATIVE_INFINITY);
		for(double d:cuts)
			listSplitPoints.add(d);
		listSplitPoints.add(Double.POSITIVE_INFINITY);
		return listSplitPoints;

	}

}
