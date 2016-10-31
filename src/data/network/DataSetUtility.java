package data.network;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import data.dataInstance.ContinuousValue;
import data.dataInstance.Graph;
import data.dataInstance.Node;
import data.dataInstance.Value;
import data.schema.Attribute;
import data.schema.CollectiveAttribute;
import data.schema.ContinuousAttribute;
import data.schema.DiscreteAttribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class DataSetUtility {
	
	private Graph g;
	
	/**
	 * Lista dei nodi di working
	 */
	private List<Node> workingNodes;	
	
	public DataSetUtility(Network network){
		this.g = network.getGraph();
		
		this.workingNodes = new LinkedList<Node>();
		for(Node n : g.nodes()){
			if(!n.isSample())
				workingNodes.add(n);
		}
	}
	
	/**
	 * 
	 * @return FastVector contenente gli attributi e classe per trainingSet e workingSet
	 */
	private FastVector createRegressionHeader(){
		//creo  l'header di per l'instanziazione dell'Instances
		FastVector attributes = new FastVector();
		int index = 0;
		
		//aggiungo gli attributi validi di schema
		Iterator<Attribute> it = g.getSchema().getIteratorExplanatory();
		while(it.hasNext()){
			Attribute current = it.next();
			weka.core.Attribute wa = null;
			if(current instanceof ContinuousAttribute)
				wa = new weka.core.Attribute(current.getName(),current.getIndexValue());
			else {
				Set<String> discreteValues = ((DiscreteAttribute) current).getValues();
				FastVector fv = new FastVector(discreteValues.size());
				
				for(String s : discreteValues){
					fv.addElement(s);
				}
				
				wa = new weka.core.Attribute(current.getName(),fv,current.getIndexValue());
			}
			attributes.addElement(wa);
			index++;
		}
		
		//aggiungo in coda l'attributo target
		attributes.addElement(new weka.core.Attribute(g.getSchema().getTarget().getName(),index));
		
		return attributes;
	}
	
	
	/**
	 * 
	 * @return FastVector contenente gli attributi, classe e id per trainingSet e workingSet
	 */
	private FastVector createRegressionHeaderWithId(){
		//creo  l'header di per l'instanziazione dell'Instances
		FastVector attributes = new FastVector();
		int index = 0;
		
		//aggiungo node id
		Attribute nodeId=g.getSchema().getId();
		weka.core.Attribute wa = null;
		if(nodeId instanceof ContinuousAttribute)
			wa = new weka.core.Attribute(nodeId.getName(),nodeId.getIndexValue());
		else {
			Set<String> discreteValues = ((DiscreteAttribute) nodeId).getValues();
			FastVector fv = new FastVector(discreteValues.size());
			
			for(String s : discreteValues){
				fv.addElement(s);
			}
		
			wa = new weka.core.Attribute(nodeId.getName(),fv,nodeId.getIndexValue());
		}	
		attributes.addElement(wa);
		index++;
		
		//aggiungo gli attributi validi di schema
		Iterator<Attribute> it = g.getSchema().getIteratorExplanatory();
		while(it.hasNext()){
			Attribute current = it.next();
			//weka.core.Attribute wa = null;
			if(current instanceof ContinuousAttribute)
				wa = new weka.core.Attribute(current.getName(),current.getIndexValue());
			else {
				Set<String> discreteValues = ((DiscreteAttribute) current).getValues();
				FastVector fv = new FastVector(discreteValues.size());
				
				for(String s : discreteValues){
					fv.addElement(s);
				}
				
				wa = new weka.core.Attribute(current.getName(),fv,current.getIndexValue());
			}
			attributes.addElement(wa);
			index++;
		}
		
		//aggiungo in coda l'attributo target
		attributes.addElement(new weka.core.Attribute(g.getSchema().getTarget().getName(),index));
		
		return attributes;
	}
	
	
	
	/**
	 * 
	 * @return FastVector contenente gli attributi (solo descrittori) per trainingSet e workingSet
	 */
	private FastVector createClusteringHeader(){
		//creo  l'header di per l'instanziazione dell'Instances
		FastVector attributes = new FastVector();
		int index = 0;
		
		//aggiungo gli attributi validi di schema
		Iterator<Attribute> it = g.getSchema().getIteratorExplanatory();
		while(it.hasNext()){
			Attribute current = it.next();
			weka.core.Attribute wa = null;
			if(current instanceof ContinuousAttribute)
				wa = new weka.core.Attribute(current.getName(),current.getIndexValue());
			else {
				Set<String> discreteValues = ((DiscreteAttribute) current).getValues();
				FastVector fv = new FastVector(discreteValues.size());
				
				for(String s : discreteValues){
					fv.addElement(s);
				}
				
				wa = new weka.core.Attribute(current.getName(),fv,current.getIndexValue());
			}
			attributes.addElement(wa);
			index++;
		}
		
		
		return attributes;
	}
	/**
	 * 
	 * @return Instances contenente i nodi sample del network
	 */
	public Instances createTrainingSet(){
		//this.trainingNodes = new LinkedList<Node>();
		
		FastVector fv = createRegressionHeader();
		Instances trainingSet = new Instances("trainingSet",fv,0); //creo il trainingSet vuoto
		trainingSet.setClassIndex(fv.indexOf(fv.lastElement())); //imposto l'attributo da classificare
		
		//aggiungo ogni nodo sample al trainingSet
		Instance instance = null;
		
		for(Node n : g.nodes()){
			if(n.isSample() || n.isWorkingToSample()){
				//trainingNodes.add(n);
				
				instance = new Instance(fv.size());
				instance.setDataset(trainingSet);
				
				//instance.setValue(0, (int)(n.getId().getValue()));
				
				//int i=1;
				int j;
				for(j=0;j<n.getValues().size();j++){
					Value v = n.getValue(j);
					
					if(v instanceof ContinuousValue)
						instance.setValue(j, (double)v.getValue());
					else instance.setValue(j, (String)v.getValue());
					//i++;
				}
				instance.setValue(j, (double)n.getTarget().getValue());
				trainingSet.add(instance);
			//	System.out.println("node:" +n.getId() +"Y:"+instance.classValue());
			}
		}
		return trainingSet;
	}
	
	
	public Instances createTrainingSetwithPredictedWorkingSet(){
		//this.trainingNodes = new LinkedList<Node>();
		
		FastVector fv = createRegressionHeader();
		Instances trainingSet = new Instances("trainingSet",fv,0); //creo il trainingSet vuoto
		trainingSet.setClassIndex(fv.indexOf(fv.lastElement())); //imposto l'attributo da classificare
		
		//aggiungo ogni nodo sample al trainingSet
		Instance instance = null;
		
		for(Node n : g.nodes()){
			if(n.isSample() || n.isWorkingToSample()){
				//trainingNodes.add(n);
				
				instance = new Instance(fv.size());
				instance.setDataset(trainingSet);
				
				//instance.setValue(0, (int)(n.getId().getValue()));
				
				//int i=1;
				int j;
				for(j=0;j<n.getValues().size();j++){
					Value v = n.getValue(j);
					
					if(v instanceof ContinuousValue)
						instance.setValue(j, (double)v.getValue());
					else instance.setValue(j, (String)v.getValue());
					//i++;
				}
				instance.setValue(j, (double)n.getTarget().getValue());
				trainingSet.add(instance);
			//	System.out.println("node:" +n.getId() +"Y:"+instance.classValue());
			}
			else
			{
			
			instance = new Instance(fv.size());
			instance.setDataset(trainingSet);
			
			int j;
			for(j=0;j<n.getValues().size();j++){
				Value v = n.getValue(j);
				
				if(v instanceof ContinuousValue)
					instance.setValue(j, (double)v.getValue());
				else instance.setValue(j, (String)v.getValue());
			}
			instance.setValue(j, (double)n.getPredictedTarget().getValue());
			trainingSet.add(instance);
		}
	}

		return trainingSet;
	}
	
	
	/**
	 * 
	 * @return Instances contenente i nodi non sample del network
	 */
	public Instances createNullLlabeledWorkingSet(){
		FastVector fv = createRegressionHeader();
		Instances workingSet = new Instances("workingSet",fv,0);//creo workingSet vuoto
		
		//aggiungo ogni nodo non sample al trainingSet
		Instance instance = null;
		
		for(Node n : g.nodes()){
			if(!n.isSample() &&  !n.isWorkingToSample()){
				instance = new Instance(fv.size());
				instance.setDataset(workingSet);
				
				//instance.setValue(0, (int)(n.getId().getValue()));
				
				//int i=1;
				int j;
				for(j=0;j<n.getValues().size();j++){
					Value v = n.getValue(j);
					
					if(v instanceof ContinuousValue)
						instance.setValue(j, (double)v.getValue());
					else instance.setValue(j, (String)v.getValue());
					//i++;
				}
					
				workingSet.add(instance);
			}
		}
		
		return workingSet;
	}
	
	
	/**
	 * 
	 * @return Instances contenente i nodi non sample del network
	 */
	public Instances createClusteringWorkingSet(){
		FastVector fv = createClusteringHeader();
		Instances workingSet = new Instances("workingSet",fv,0);//creo workingSet vuoto
		
		//aggiungo ogni nodo non sample al trainingSet
		Instance instance = null;
		
		for(Node n : g.nodes()){
			//if(!n.isSample() &&  !n.isWorkingToSample())
			{
				instance = new Instance(fv.size());
				instance.setDataset(workingSet);
				
				//instance.setValue(0, (int)(n.getId().getValue()));
				
				//int i=1;
				int j;
				for(j=0;j<n.getValues().size();j++){
					Value v = n.getValue(j);
					
					if(v instanceof ContinuousValue)
						instance.setValue(j, (double)v.getValue());
					else instance.setValue(j, (String)v.getValue());
					//i++;
				}
					
				workingSet.add(instance);
			}
		}
		
		return workingSet;
	}
	
	/**
	 * 
	 * @return Instances contenente i nodi del working with labels and node id
	 */
	public Instances createLabeleWorkingSet(){
		FastVector fv = createRegressionHeaderWithId();
		Instances workingSet = new Instances("dataSet",fv,0);//creo workingSet vuoto
		
		workingSet.setClassIndex(fv.indexOf(fv.lastElement())); //imposto l'attributo da classificare
		
		
		//aggiungo ogni nodo non sample al trainingSet
		Instance instance = null;
		
		for(Node n : g.nodes()){
			if(!n.isSample()){
				instance = new Instance(fv.size());
				instance.setDataset(workingSet);
				
				//instance.setValue(0, (int)(n.getId().getValue()));
				
				//int i=1;
				if(n.getId().getValue() instanceof String)
				instance.setValue(0,(String)n.getId().getValue());
				else
					instance.setValue(0,(int)n.getId().getValue());
				
				int j;
				
				for(j=0;j<n.getValues().size();j++){
					Value v = n.getValue(j);
					
					if(v instanceof ContinuousValue)
						instance.setValue(j+1, (double)v.getValue());
					else instance.setValue(j+1, (String)v.getValue());
					//i++;
				}
				instance.setValue(j+1, (double)n.getTarget().getValue());
				workingSet.add(instance);
				
				
			}
		}
		
		return workingSet;
	}
	
	
	/**
	 * 
	 * @return Instances contenente i nodi del working without labels and node id
	 */
	public Instances createUnLabeleWorkingSet(){
		FastVector fv = createRegressionHeaderWithId();
		Instances workingSet = new Instances("dataSet",fv,0);//creo workingSet vuoto
		
		workingSet.setClassIndex(fv.indexOf(fv.lastElement())); //imposto l'attributo da classificare
		
		
		//aggiungo ogni nodo non sample al trainingSet
		Instance instance = null;
		
		for(Node n : g.nodes()){
			if(!n.isSample()){
				instance = new Instance(fv.size());
				instance.setDataset(workingSet);
				
				//instance.setValue(0, (int)(n.getId().getValue()));
				
				//int i=1;
				if(n.getId().getValue() instanceof String)
				instance.setValue(0,(String)n.getId().getValue());
				else
					instance.setValue(0,(int)n.getId().getValue());
				
				int j;
				
				for(j=0;j<n.getValues().size();j++){
					Value v = n.getValue(j);
					
					if(v instanceof ContinuousValue)
						instance.setValue(j+1, (double)v.getValue());
					else instance.setValue(j+1, (String)v.getValue());
					//i++;
				}
				instance.setValue(j+1, (double)n.getTarget().getValue());
				workingSet.add(instance);
				
				
			}
		}
		
		return workingSet;
	}
	
	/**
	 * 
	 * @return FastVector contenente gli attributi per trainingSet e workingSet di una vista definita per un problema di regressione
	 */
	private FastVector createRegressionCollectiveHeader() {
        //creo  l'header di per l'instanziazione dell'Instances
        FastVector attributes = new FastVector();
        int index = 0;
        
        //aggiungo gli attributi collettivi e descrittivi di schema 
        Iterator<Attribute> itDescriptive = g.getSchema().getIteratorExplanatory();
        while(itDescriptive.hasNext()) {
            Attribute current = itDescriptive.next();
            weka.core.Attribute wa = null;
            if (current instanceof ContinuousAttribute)
                wa = new weka.core.Attribute(current.getName(), current.getIndexValue());
            else {
                Set<String> discreteValues = ((DiscreteAttribute) current).getValues();
                FastVector fv = new FastVector(discreteValues.size());
                for (String s : discreteValues) {
                    fv.addElement(s);
                }

                wa = new weka.core.Attribute(current.getName(), fv, current.getIndexValue());
            }
            attributes.addElement(wa);
            index++;
        }
        
        Iterator<CollectiveAttribute> itCollective = g.getSchema().getCollectiveAttrIterator();
        while(itCollective.hasNext()){
            CollectiveAttribute current = itCollective.next();
            attributes.addElement(new weka.core.Attribute(current.getName(),current.getIndexValue()));
            index++;
        }
        
        //aggiungo in coda l'attributo target
        attributes.addElement(new weka.core.Attribute(g.getSchema().getTarget().getName(),index));
        
        return attributes;
    }
	
	
	/**
	 * 
	 * @return FastVector contenente gli attributi per trainingSet e workingSet di una vista definita 
	 * con attibuti descrittori + attributi collettivi per un problema di regressione 
	 */
	private FastVector createExtendedRegressionCollectiveHeader(){
		//creo  l'header di per l'instanziazione dell'Instances
		FastVector attributes = new FastVector();
		int index =0;

		
		//aggiungo gli attributi desrittori di schema
		Iterator<Attribute> itD = g.getSchema().getIteratorExplanatory();
		while(itD.hasNext()){
			Attribute current = itD.next();
			weka.core.Attribute wa=null;
			
			if(current instanceof ContinuousAttribute)
				wa = new weka.core.Attribute(current.getName(),index);
			else {
				Set<String> discreteValues = ((DiscreteAttribute) current).getValues();
				FastVector fv = new FastVector(discreteValues.size());
				
				for(String s : discreteValues){
					fv.addElement(s);
				}
				
				wa = new weka.core.Attribute(current.getName(),index);
			}
					
			
			attributes.addElement(wa);
			index++;
		
		}
		
		//aggiungo gli attributi collective di schema
		Iterator<CollectiveAttribute> itC = g.getSchema().getCollectiveAttrIterator();
		while(itC.hasNext()){
			CollectiveAttribute current = itC.next();
			attributes.addElement(new weka.core.Attribute(current.getName(),index));
			index++;
	
		}
		
		//aggiungo in coda l'attributo target
		attributes.addElement(new weka.core.Attribute(g.getSchema().getTarget().getName()));
		
		return attributes;
	}
	/**
	 * 
	 * @return FastVector contenente gli attributi per trainingSet e workingSet di una vista definita per un problema di regressione
	 */
	private FastVector createClassificationCollectiveHeader(Set<String> classValues){
		//creo  l'header di per l'instanziazione dell'Instances
		FastVector attributes = new FastVector();
		int index = 0;
		
		//aggiungo gli attributi collective di schema
		Iterator<CollectiveAttribute> it = g.getSchema().getCollectiveAttrIterator();
		while(it.hasNext()){
			CollectiveAttribute current = it.next();
			attributes.addElement(new weka.core.Attribute(current.getName(),current.getIndexValue()));
			//attributes.addElement(new weka.core.Attribute("COLLECTIVE"+index,current.getIndexValue()));
			index++;
		}
		
		
		FastVector nominalValues=new FastVector();//=((ContinuousAttribute)(g.getSchema().getTarget())).discreteDomain();
		for(String s:classValues)
			nominalValues.addElement(s);
		
		//aggiungo in coda l'attributo target discreto
		attributes.addElement(new weka.core.Attribute(g.getSchema().getTarget().getName(),nominalValues,index));
		
		return attributes;
	}
	

	/**
	 * 
	 * @return FastVector contenente gli attributi per trainingSet e workingSet di una vista definita per un problema di classificazione 
	 * contenente attributi discreti e attributi colelttivi
	 */
	private FastVector createExtendedClassificationCollectiveHeader(Set<String> classValues){
		//creo  l'header di per l'instanziazione dell'Instances
		FastVector attributes = new FastVector();
		int index = 0;
		
		
		//aggiungo gli attributi desrittori di schema
		Iterator<Attribute> itD = g.getSchema().getIteratorExplanatory();
		while(itD.hasNext()){
			Attribute current = itD.next();
			weka.core.Attribute wa=null;
			
			if(current instanceof ContinuousAttribute)
				wa = new weka.core.Attribute(current.getName(),index);
			else {
				Set<String> discreteValues = ((DiscreteAttribute) current).getValues();
				FastVector fv = new FastVector(discreteValues.size());
				
				for(String s : discreteValues){
					fv.addElement(s);
				}
				
				wa = new weka.core.Attribute(current.getName(),index);
			}
					
			
			attributes.addElement(wa);
			index++;
		
		}
		
		//aggiungo gli attributi collective di schema
		Iterator<CollectiveAttribute> it = g.getSchema().getCollectiveAttrIterator();
		while(it.hasNext()){
			CollectiveAttribute current = it.next();
			attributes.addElement(new weka.core.Attribute(current.getName(),index));
			//attributes.addElement(new weka.core.Attribute("COLLECTIVE"+index,current.getIndexValue()));
			index++;
		}
		
		
		FastVector nominalValues=new FastVector();//=((ContinuousAttribute)(g.getSchema().getTarget())).discreteDomain();
		for(String s:classValues)
			nominalValues.addElement(s);
		
		//aggiungo in coda l'attributo target discreto
		attributes.addElement(new weka.core.Attribute(g.getSchema().getTarget().getName(),nominalValues,index));
		
		return attributes;
	}
	/**
	 * 
	 * @return Instances contenente gli elementi collective dei nodi sample del network per uan vista definita con un attributo target per la regressione
	 */
	public Instances createRegressionCollectiveTrainingSet() {
		
		FastVector fv = createRegressionCollectiveHeader();
		Instances trainingSet = new Instances("collectiveTrainingSet",fv,0); //creo il trainingSet vuoto
		trainingSet.setClassIndex(fv.indexOf(fv.lastElement())); //imposto l'attributo da classificare
		
		//aggiungo ogni nodo sample al trainingSet
		// valori descrittivi + collettivi
		Instance instance = null;
		
		for(Node n : g.nodes()) {
			if(n.isSample() || n.isWorkingToSample()) {
				instance = new Instance(fv.size());
				instance.setDataset(trainingSet);
				
				int i;
				int j = 0; // count all attributes (descr + coll)
				for(i=0;i<n.getValues().size();i++) { // descriptive attributes 
					Value v = n.getValue(i);
					
					if(v instanceof ContinuousValue)
                        instance.setValue(i, (double)v.getValue());
                    else 
                        instance.setValue(i, (String)v.getValue());                 
                    j++;
				}
				for(i=0;i<n.getCollectiveValues().size();i++) { // collective attributes
                    Value v = n.getCollectiveValue(i); 
                    instance.setValue(j, (double)v.getValue());
                    j++;
                }
				instance.setValue(j, (double)n.getTarget().getValue());
				trainingSet.add(instance);
				
				
			} // end if	
			//November 25: added to have trainign set that encloses the entir network)
		/*	else 
			{
				instance = new Instance(fv.size());
				instance.setDataset(trainingSet);
				
				int j;
				for(j=0;j<n.getCollectiveValues().size();j++){
					Value v = n.getCollectiveValue(j);
					instance.setValue(j, (double)v.getValue());
				}
				instance.setValue(j, (double)n.getPredictedTarget().getValue());
				trainingSet.add(instance);
			}
			*/	
		}
		return trainingSet;
	}
	
	/**
	 * 
	 * @return Instances contenente gli elementi descrittivi + gli elementi collective dei nodi sample del network per una vista definita con un attributo target per la regressione
	 */
	public Instances createExtendedRegressionCollectiveTrainingSet(){
		
		FastVector fv = createExtendedRegressionCollectiveHeader();
		Instances trainingSet = new Instances("collectiveTrainingSet",fv,0); //creo il trainingSet vuoto
		trainingSet.setClassIndex(fv.indexOf(fv.lastElement())); //imposto l'attributo da classificare
		
		//aggiungo ogni nodo sample al trainingSet
		Instance instance = null;
		
		for(Node n : g.nodes()){
			if(n.isSample() || n.isWorkingToSample()){
				instance = new Instance(fv.size());
				instance.setDataset(trainingSet);
				
				int j;
				for(j=0;j<n.getValues().size();j++){
					Value v = n.getValue(j);
					if(v instanceof ContinuousValue)
						instance.setValue(j, (double)v.getValue());
					else
						instance.setValue(j, (String)v.getValue());
				}
				
				int prefixSize=j;
				for(j=0;j<n.getCollectiveValues().size();j++){
					Value v = n.getCollectiveValue(j);
					if(v instanceof ContinuousValue)
						instance.setValue(j+prefixSize, (double)v.getValue());
					else
						instance.setValue(j+prefixSize, (String)v.getValue());
				}
				instance.setValue(prefixSize+j, (double)n.getTarget().getValue());
				trainingSet.add(instance);
			}
		}
		return trainingSet;
	}


	/**
	 * 
	 * @return Instances contenente gli elementi collective dei nodi sample del network per una vista definita con un attributo target per la classificazione
	 */
	public Instances createClassificationCollectiveTrainingSet(){
		
		
		
		List<Instance> list=new LinkedList<Instance>();
		//aggiungo ogni nodo sample al trainingSet
		Instance instance = null;
		Set<String> classValues=new TreeSet<String>();
		
		int instanceSize=g.getSchema().numberOfCollectiveAttributes()+1;
		
		Collection<Double> discreteBins=g.getSchema().getTarget().discreteBins();
		for(Node n : g.nodes()){
			if(n.isSample() || n.isWorkingToSample()){
				instance = new Instance(instanceSize);
				//instance.setDataset(trainingSet);
				
				int j;
				for(j=0;j<n.getCollectiveValues().size();j++){
					Value v = n.getCollectiveValue(j);
					instance.setValue(j, (double)v.getValue());
				}
				// imposto la classe
				//String discreteValue=((ContinuousAttribute)(g.getSchema().getTarget())).discreteValue((double)n.getTarget().getValue());
				String discreteValue=ContinuousAttribute.discreteValue((double)n.getTarget().getValue(), discreteBins);
						
				//instance.setValue(j, discreteValue);
				classValues.add(discreteValue);
				// aggiungo l'istanza al trainignset
				//trainingSet.add(instance);
				list.add(instance);
			}
		}

		FastVector fv = createClassificationCollectiveHeader(classValues);
		Instances trainingSet = new Instances("collectiveTrainingSet",fv,0); //creo il trainingSet vuoto
		trainingSet.setClassIndex(fv.indexOf(fv.lastElement())); //imposto l'attributo da classificare

		
		Iterator<Instance> itInstance=list.iterator();
		//for(Instance i:list)
		int j=instanceSize-1;
		for(Node n : g.nodes()){
			if(n.isSample() || n.isWorkingToSample()){
				Instance i=itInstance.next();
				i.setDataset(trainingSet);
				String discreteValue=((ContinuousAttribute)(g.getSchema().getTarget())).discreteValue((double)n.getTarget().getValue());
				i.setValue(j, discreteValue);
				
				trainingSet.add(i);
			}
		}
		return trainingSet;
	}
	
	
	/**
	 * 
	 * @return Instances contenente gli elementi descrittivi+collective dei nodi sample del network per una vista definita con un attributo target per la classificazione
	 */
	public Instances createExtendedClassificationCollectiveTrainingSet(){
		
		
		
		List<Instance> list=new LinkedList<Instance>();
		//aggiungo ogni nodo sample al trainingSet
		Instance instance = null;
		Set<String> classValues=new TreeSet<String>();
		
		int instanceSize=g.getSchema().numberOfValidAttributes()+g.getSchema().numberOfCollectiveAttributes()+1;
		
		Collection<Double> discreteBins=g.getSchema().getTarget().discreteBins();
		for(Node n : g.nodes()){
			if(n.isSample() || n.isWorkingToSample()){
				instance = new Instance(instanceSize);
				//instance.setDataset(trainingSet);
				
				int j;
				
				
			
				for(j=0;j<n.getValues().size();j++){
					Value v = n.getValue(j);
					if(v instanceof ContinuousValue)
						instance.setValue(j, (double)v.getValue());
					else
						instance.setValue(j, (String)v.getValue());
				}
				
				int prefix=j;
				
				for(j=0;j<n.getCollectiveValues().size();j++){
					Value v = n.getCollectiveValue(j);
					instance.setValue(prefix+j, (double)v.getValue());
				}
				// imposto la classe
				//String discreteValue=((ContinuousAttribute)(g.getSchema().getTarget())).discreteValue((double)n.getTarget().getValue());
				String discreteValue=ContinuousAttribute.discreteValue((double)n.getTarget().getValue(), discreteBins);
						
				//instance.setValue(j, discreteValue);
				classValues.add(discreteValue);
				// aggiungo l'istanza al trainignset
				//trainingSet.add(instance);
				list.add(instance);
			}
		}

		FastVector fv = createExtendedClassificationCollectiveHeader(classValues);
		Instances trainingSet = new Instances("collectiveTrainingSet",fv,0); //creo il trainingSet vuoto
		trainingSet.setClassIndex(fv.indexOf(fv.lastElement())); //imposto l'attributo da classificare

		
		Iterator<Instance> itInstance=list.iterator();
		//for(Instance i:list)
		int j=instanceSize-1;
		for(Node n : g.nodes()){
			if(n.isSample() || n.isWorkingToSample()){
				Instance i=itInstance.next();
				i.setDataset(trainingSet);
				String discreteValue=((ContinuousAttribute)(g.getSchema().getTarget())).discreteValue((double)n.getTarget().getValue());
				i.setValue(j, discreteValue);
				
				trainingSet.add(i);
			}
		}
		return trainingSet;
	}
	
	
	/**
	 * 
	 * @return Instances contenente gli elementi collective dei nodi non sample del network
	 */
	public Instances createRegressionCollectiveWorkingSet() {
	    FastVector fv = createRegressionCollectiveHeader();
        Instances workingSet = new Instances("collectiveWorkingSet",fv,0);//creo workingSet vuoto
        
        // aggiungo ogni nodo non sample al trainingSet
        // valori descrittivi + collettivi
        Instance instance = null;
        
        for(Node n : g.nodes()){
            if(!n.isSample() && !n.isWorkingToSample()){    
                instance = new Instance(fv.size());
                instance.setDataset(workingSet);

                int i;
                int j=0; //count all attributes
                for(i=0;i<n.getValues().size();i++){
                    Value v = n.getValue(j);
                    if(v instanceof ContinuousValue)
                        instance.setValue(i, (double)v.getValue());
                    else 
                        instance.setValue(i, (String)v.getValue());                 
                    j++;
                }
                for(i=0; i<n.getCollectiveValues().size(); i++) {
                    Value v = n.getCollectiveValue(i);
                    instance.setValue(j, (double)v.getValue());
                    j++;
                }
                workingSet.add(instance);
            }
        }
        return workingSet;
	}
	
	
	/**
	 * 
	 * @return Instances contenente gli elementi descrittivi+collective dei nodi non sample del network
	 */
	public Instances createExtendedRegressionCollectiveWorkingSet(){
		FastVector fv = createExtendedRegressionCollectiveHeader();
		Instances workingSet = new Instances("collectiveWorkingSet",fv,0);//creo workingSet vuoto
		
		//aggiungo ogni nodo non sample al trainingSet
		Instance instance = null;
		
		for(Node n : g.nodes()){
			if(!n.isSample() && !n.isWorkingToSample()){	
				instance = new Instance(fv.size());
				instance.setDataset(workingSet);
				int j;
				
			
				for(j=0;j<n.getValues().size();j++){
					Value v = n.getValue(j);
					if(v instanceof ContinuousValue)
						instance.setValue(j, (double)v.getValue());
					else
						instance.setValue(j, (String)v.getValue());
				}
				
				int prefixSize=j;
				for(j=0;j<n.getCollectiveValues().size();j++){
					Value v = n.getCollectiveValue(j);
					if(v instanceof ContinuousValue)
						instance.setValue(j+prefixSize, (double)v.getValue());
					else
						instance.setValue(j+prefixSize, (String)v.getValue());
				}
				
				workingSet.add(instance);
			}
		}
		return workingSet;
	}
	
	
	/**
	 * 
	 * @return Instances contenente gli elementi collective dei nodi non sample del network per una vista di classificazione
	 */
	public Instances createClassificationCollectiveWorkingSet(FastVector header){
		//FastVector fv = createClassificationCollectiveHeader();
		Instances workingSet = new Instances("collectiveWorkingSet",header,0);//creo workingSet vuoto
		workingSet.setClassIndex(header.indexOf(header.lastElement())); //imposto l'attributo da classificare

		//aggiungo ogni nodo non sample al trainingSet
		Instance instance = null;
		
		for(Node n : g.nodes()){
			if(!n.isSample() && !n.isWorkingToSample()){	
				instance = new Instance(header.size());
				instance.setDataset(workingSet);
				int j;
				for(j=0;j<n.getCollectiveValues().size();j++){
					Value v = n.getCollectiveValue(j);
					instance.setValue(j, (double)v.getValue());
				}
				workingSet.add(instance);
			}
		}
		
			return workingSet;
		
	}

	/**
	 * 
	 * @return Instances contenente gli elementi descrittivi+collective dei nodi non sample del network per una vista di classificazione
	 */
	public Instances createExtendedClassificationCollectiveWorkingSet(FastVector header){
		//FastVector fv = createClassificationCollectiveHeader();
		Instances workingSet = new Instances("collectiveWorkingSet",header,0);//creo workingSet vuoto
		workingSet.setClassIndex(header.indexOf(header.lastElement())); //imposto l'attributo da classificare

		//aggiungo ogni nodo non sample al trainingSet
		Instance instance = null;
		
		for(Node n : g.nodes()){
			if(!n.isSample() && !n.isWorkingToSample()){	
				instance = new Instance(header.size());
				instance.setDataset(workingSet);
				int j;

				for(j=0;j<n.getValues().size();j++){
					Value v = n.getValue(j);
					if(v instanceof ContinuousValue)
						instance.setValue(j, (double)v.getValue());
					else
						instance.setValue(j, (String)v.getValue());
				}
				int prefixSize=j;
				
				for(j=0;j<n.getCollectiveValues().size();j++){
					Value v = n.getCollectiveValue(j);
					instance.setValue(prefixSize+j, (double)v.getValue());
				}
				workingSet.add(instance);
			}
		}
		
			return workingSet;
		
	}
	/**
	 * 
	 * @return Instances contenente gli elementi collective dei nodi sample del network
	 */
	Instances createClusteringSet(){
		
		FastVector fv = createRegressionCollectiveHeader();
		Instances trainingSet = new Instances("collectiveTrainingSet",fv,0); //creo il trainingSet vuoto
		trainingSet.setClassIndex(fv.indexOf(fv.lastElement())); //imposto l'attributo da classificare
		
		//aggiungo ogni nodo sample al trainingSet
		Instance instance = null;
		
		for(Node n : g.nodes()){
			//if(n.isSample() || n.isWorkingToSample())
			{
				instance = new Instance(fv.size());
				instance.setDataset(trainingSet);
				
				int j;
				for(j=0;j<n.getCollectiveValues().size();j++){
					Value v = n.getCollectiveValue(j);
					instance.setValue(j, (double)v.getValue());
				}
				instance.setValue(j, (double)n.getTarget().getValue());
				trainingSet.add(instance);
			}
		}
		return trainingSet;
	}
	
	   /**
     * 
     * @return Instances contenente i nodi sample del network
     */
    public Instances createClusteringData(){
        //this.trainingNodes = new LinkedList<Node>();
        
        FastVector fv = createRegressionHeaderWithId();
        Instances clusteringSet = new Instances("clusteringSet",fv,0); //creo il trainingSet vuoto
//        clusteringSet.setClassIndex(fv.indexOf(fv.lastElement())); //imposto l'attributo da classificare
        
        //aggiungo ogni nodo sample al trainingSet
        Instance instance = null;
        
        for(Node n : g.nodes()){
                //trainingNodes.add(n);
                
                instance = new Instance(fv.size());
                instance.setDataset(clusteringSet);
                
                //instance.setValue(0, (int)(n.getId().getValue()));
                //int i=1;
                if(n.getId().getValue() instanceof String)
                instance.setValue(0,(String)n.getId().getValue());
                else
                    instance.setValue(0,(int)n.getId().getValue());

                //int i=1;
                int j;
                for(j=0;j<n.getValues().size();j++){
                    Value v = n.getValue(j);
                    
                    if(v instanceof ContinuousValue)
                        instance.setValue(j+1, (double)v.getValue());
                    else 
                        instance.setValue(j+1, (String)v.getValue());
                    //i++;
                }
                if(n.getPreviousPredictedTarget() == null)
                    instance.setValue(j+1, (double)n.getTarget().getValue());
                else 
                    instance.setValue(j+1, (double)n.getPreviousPredictedTarget().getValue());

                clusteringSet.add(instance);
            //  System.out.println("node:" +n.getId() +"Y:"+instance.classValue());
            
        }
        return clusteringSet;
    }
	/**
	 * 
	 * @return lista dei nodi di working
	 */
	public List<Node> getWorkingNodes() {
		return this.workingNodes;
	}
}
