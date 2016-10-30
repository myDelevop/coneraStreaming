package data.network;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeSet;

import clusteringCollectiveSchema.Clustering;
import clusteringCollectiveSchema.ColleptiveClustering;
import clusteringCollectiveSchema.DescriptiveClustering;
import collective.COLLECTIVESCHEMA;
import data.dataInstance.Cluster;
import data.dataInstance.ContinuousValue;
import data.dataInstance.DiscreteValue;
import data.dataInstance.Graph;
import data.dataInstance.Node;
import data.dataInstance.Value;
import data.schema.Attribute;
import data.schema.AverageAttribute;
import data.schema.CollectiveAttribute;
import data.schema.ContinuousAttribute;
import data.schema.DISCRETIZATIONTYPE;
import data.schema.DiscreteAttribute;
import data.schema.FrequencyAttribute;
import data.schema.GIAttribute;
import data.schema.MeanAttribute;
import data.schema.Schema;
import data.schema.SpeedAttribute;
import data.schema.StdDevAttribute;
import data.schema.WMeanAttribute;
import data.schema.WSpeedAttribute;
import data.schema.WeightI;
import data.schema.WeightInstance;
import setup.LearningSettings;
import weka.core.Instances;

public class Network{
	
    private Network n;
	private Graph network;
	
	private LearningSettings l;
	
	private NetworkSettings ns;
	
	
	
	public Network(String networkFileName, 
			String edgesFileName, 
			String trainingNodesFileName,
			String configFileName,
			String networkSettingsFilename,
			LearningSettings l) throws Exception/*, UnsupportedEncodingException*/{
		BufferedReader arff=null;
		BufferedReader config=null;
		BufferedReader sample=null; 
		BufferedReader edges=null;
		BufferedReader netw=null;
		this.l=l;
		this.n=this;
		try {
			netw = new BufferedReader(new FileReader(networkSettingsFilename));
			arff = new BufferedReader(new FileReader(networkFileName));
			config = new BufferedReader(new FileReader(configFileName));
			sample = new BufferedReader(new FileReader(trainingNodesFileName));
			edges = new BufferedReader(new FileReader(edgesFileName));
			
			ns = new NetworkSettings(netw);
			//new ArffSchema(arff,ns, l.getCollectiveSchema());
			network=new Graph();
			new ArffSchema(arff); // crea descriptive schema e lo imposta in network 
			network.setWeight(ns.weight);
			new Config(config);
			new ArffData(arff);
			
			new Sample(sample);
            new Edges(edges);

			new ArffCollectiveSchema( ns, l.getCollectiveSchema());// crea collective schema; e lo impost in network

			
		//	DataSetUtility d = new DataSetUtility(this);
			network.createNeighbourhoodStructure(ns.rmin,ns.rmax,ns.step);
			
			
			// update l 
			l.update(network.size());
		
		}catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (arff != null)arff.close();
				if (config != null)config.close();
				if (sample != null)sample.close();
				if (edges != null)edges.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
				
		
	}

	
	public Network(String networkFileName, 
			String edgesFileName, 
			String clusteringFileName,
			String trainingNodesFileName,
			String configFileName,
			String networkSettingsFilename,
			LearningSettings l) throws Exception/*, UnsupportedEncodingException*/{
		this.l=l;
		BufferedReader arff=null;
		BufferedReader config=null;
		BufferedReader clustering=null;
		BufferedReader sample=null; 
		BufferedReader edges=null;
		BufferedReader netw=null;
		
		try {
			netw = new BufferedReader(new FileReader(networkSettingsFilename));
			arff = new BufferedReader(new FileReader(networkFileName));
			clustering = new BufferedReader(new FileReader(clusteringFileName));
			config = new BufferedReader(new FileReader(configFileName));
			sample = new BufferedReader(new FileReader(trainingNodesFileName));
			edges = new BufferedReader(new FileReader(edgesFileName));
			
			ns = new NetworkSettings(netw);
			network=new Graph();
			//new ArffSchema(arff,ns,l.getCollectiveSchema());
			new ArffSchema(arff);
			new Config(config);
			new ArffData(arff);
			network.setWeight(ns.weight);
			
			new Clusterings(clustering);
			new Sample(sample);
			new ArffCollectiveSchema(ns, l.getCollectiveSchema());
			new Edges(edges);

			
		//	DataSetUtility d = new DataSetUtility(this);
			network.createNeighbourhoodStructure(ns.rmin,ns.rmax,ns.step);
			
			l.update(network.size());
		
		}catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (arff != null)arff.close();
				if (config != null)config.close();
				if (clustering != null)clustering.close();
				if (sample != null)sample.close();
				if (edges != null)edges.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
				
		
	}
	
	
	

	
	public Network(String networkFileName, 
			String edgesFileName, 
			String trainingNodesFileName,
			String configFileName,
			String networkSettingsFilename,
			int a) throws Exception/*, UnsupportedEncodingException*/{
		BufferedReader arff=null;
		BufferedReader config=null;
		BufferedReader sample=null; 
		BufferedReader edges=null;
		BufferedReader netw=null;
		
		try {
			netw = new BufferedReader(new FileReader(networkSettingsFilename));
			arff = new BufferedReader(new FileReader(networkFileName));
			config = new BufferedReader(new FileReader(configFileName));
			sample = new BufferedReader(new FileReader(trainingNodesFileName));
			edges = new BufferedReader(new FileReader(edgesFileName));
			
			ns = new NetworkSettings(netw);
			network=new Graph();
			//new ArffSchema(arff,ns);
			new ArffSchema(arff);
			network.setWeight(ns.weight);
			new Config(config);
			new ArffData(arff);
			new Sample(sample);
			new Edges(edges);

			
		//	DataSetUtility d = new DataSetUtility(this);
			network.createNeighbourhoodStructure(ns.rmin,ns.rmax,ns.step);
		
		}catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (arff != null)arff.close();
				if (config != null)config.close();
				if (sample != null)sample.close();
				if (edges != null)edges.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
				
		
	}

	
	public Network(String networkFileName, 
			String edgesFileName, 
			String clusteringFileName,
			String trainingNodesFileName,
			String configFileName,
			String networkSettingsFilename,
			int a) throws Exception/*, UnsupportedEncodingException*/{
		BufferedReader arff=null;
		BufferedReader config=null;
		BufferedReader clustering=null;
		BufferedReader sample=null; 
		BufferedReader edges=null;
		BufferedReader netw=null;
		
		try {
			netw = new BufferedReader(new FileReader(networkSettingsFilename));
			arff = new BufferedReader(new FileReader(networkFileName));
			clustering = new BufferedReader(new FileReader(clusteringFileName));
			config = new BufferedReader(new FileReader(configFileName));
			sample = new BufferedReader(new FileReader(trainingNodesFileName));
			edges = new BufferedReader(new FileReader(edgesFileName));
			
			ns = new NetworkSettings(netw);
			network=new Graph();
			//new ArffSchema(arff,ns);
			new ArffSchema(arff);
			new Config(config);
			new ArffData(arff);
			network.setWeight(ns.weight);
			new Clusterings(clustering);
			new Sample(sample);
			new Edges(edges);
			l.update(network.size());

			
		//	DataSetUtility d = new DataSetUtility(this);
			network.createNeighbourhoodStructure(ns.rmin,ns.rmax,ns.step);
		
		}catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (arff != null)arff.close();
				if (config != null)config.close();
				if (clustering != null)clustering.close();
				if (sample != null)sample.close();
				if (edges != null)edges.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
				
		
	}
	
	public Network(String networkFileName, 
			String configFileName
			) throws Exception/*, UnsupportedEncodingException*/{
		BufferedReader arff=null;
		BufferedReader config=null;
		
		
		try {
			
			arff = new BufferedReader(new FileReader(networkFileName));
			config = new BufferedReader(new FileReader(configFileName));
			network=new Graph();
			new ArffSchema(arff);
			new Config(config);
			new ArffData(arff);
			
			
			
		}catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (arff != null)arff.close();
				if (config != null)config.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
				
		
	}
	
	
	
	public Network(String networkFileName, 
			String configFileName, boolean scalingNumericValues
			) throws Exception/*, UnsupportedEncodingException*/{
		BufferedReader arff=null;
		BufferedReader config=null;
		
		
		try {
			
			arff = new BufferedReader(new FileReader(networkFileName));
			config = new BufferedReader(new FileReader(configFileName));
			network=new Graph();
			new ArffSchema(arff);
			new Config(config);
			new ArffData(arff,scalingNumericValues);
			
			
			
		}catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (arff != null)arff.close();
				if (config != null)config.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
				
		
	}
	
	
	public Network(String networkFileName, 
			String edgesFileName, 
			String configFileName,
			String networkSettingsFilename 
			//, COLLECTIVESCHEMA schema
			) throws Exception/*, UnsupportedEncodingException*/{
		BufferedReader arff=null;
		BufferedReader config=null;
		
		BufferedReader edges=null;
		BufferedReader netw=null;
		
		try {
			netw = new BufferedReader(new FileReader(networkSettingsFilename));
			arff = new BufferedReader(new FileReader(networkFileName));
			config = new BufferedReader(new FileReader(configFileName));
			edges = new BufferedReader(new FileReader(edgesFileName));
			
			ns = new NetworkSettings(netw);
			network=new Graph();
			//new ArffSchema(arff,ns,schema);
			new ArffSchema(arff);
			network.setWeight(ns.weight);
			new Config(config);
			new ArffData(arff);
			
		
			new Edges(edges);

			
		//	DataSetUtility d = new DataSetUtility(this);
			network.createNeighbourhoodStructure(ns.rmin,ns.rmax,ns.step);
		
		}catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (arff != null)arff.close();
				if (config != null)config.close();
				
				if (edges != null)edges.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
				
		
	}

	public Network(String networkFileName, 
			String edgesFileName, 
			String configFileName,
			String networkSettingsFilename ,
			boolean minmax
			) throws Exception/*, UnsupportedEncodingException*/{
		BufferedReader arff=null;
		BufferedReader config=null;
		
		BufferedReader edges=null;
		BufferedReader netw=null;
		
		try {
			netw = new BufferedReader(new FileReader(networkSettingsFilename));
			arff = new BufferedReader(new FileReader(networkFileName));
			config = new BufferedReader(new FileReader(configFileName));
			edges = new BufferedReader(new FileReader(edgesFileName));
			
			ns = new NetworkSettings(netw);
			network=new Graph();
			//new ArffSchema(arff,ns,schema);
			new ArffSchema(arff);
			network.setWeight(ns.weight);
			new Config(config);
			new ArffData(arff,minmax);
			
		
			new Edges(edges);

			
		//	DataSetUtility d = new DataSetUtility(this);
			network.createNeighbourhoodStructure(ns.rmin,ns.rmax,ns.step);
		
		}catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (arff != null)arff.close();
				if (config != null)config.close();
				
				if (edges != null)edges.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
				
		
	}

	
	
	
	public Graph getGraph(){
		return network;
	}

	private class NetworkSettings{
		
	//	CLASSIFIER_TYPE ct;
		int rmin;
		int rmax;
		int step;
		//int powerWeight;
		WeightI weight;
		
		NetworkSettings(BufferedReader br) throws IOException{
			String line = br.readLine();
			while (line != null){
				String[] lineElements = line.split(" ");
				if(lineElements[0].equals("@rmin")){
					rmin = Integer.parseInt(lineElements[1]);
				}else if(lineElements[0].equals("@rmax")){
					rmax = Integer.parseInt(lineElements[1]);
				}else if(lineElements[0].equals("@step")){
					step = Integer.parseInt(lineElements[1]);
				}else if(lineElements[0].equals("@wpower")){
					int powerWeight = Integer.parseInt(lineElements[1]);
					weight=(new WeightInstance(powerWeight));
				}
				//else if(lineElements[0].equals("@classifier")){
				//	ct = CLASSIFIER_TYPE.valueOf(lineElements[1]);
				//}
				line = br.readLine();
			}
				if(rmin < 1 || 
						rmax < rmin || 
						step < 1 //|| 
						//weight.n < 1 
						){
					System.err.println("Il file network_settings.ini contiene valori errati");
					System.exit(1);
				}
			
		}
	}
	
	
	/**
	 * Aggiorna il collective schema ; ha senso solo un presenza di frequency attribute con equand frequency, altrimenti si mantiene il colelctive schema esistente
	 */
	public void updateCollectiveSchema(){ 
		if (l.getCollectiveSchema().equals(COLLECTIVESCHEMA.Frequency))
		    new ArffCollectiveSchema(ns, l.getCollectiveSchema());
		else if (l.getCollectiveSchema().equals(COLLECTIVESCHEMA.Cluster)) {
            new ArffCollectiveSchema(ns, l.getCollectiveSchema());
		}
	}
	
	private class ArffCollectiveSchema{
		ArffCollectiveSchema( NetworkSettings ns, COLLECTIVESCHEMA schema){
			
			int i=0;
			
			List<CollectiveAttribute> collAttributes = new LinkedList<CollectiveAttribute>();
			int nN = ((ns.rmax-ns.rmin)/ns.step)+1;
			int idS=1;
			
			switch (schema) {
			case Mean:
			    for(i=1;i<=nN;i++){
			        collAttributes.add(new MeanAttribute("mean_"+i,idS++));
			        collAttributes.add(new WMeanAttribute("wMean_"+i,idS++,ns.weight));
			        collAttributes.add(new StdDevAttribute("stdDev_"+i,idS++));
			    }
			    if(nN!=1)
			        for(i=1;i<=nN;i++){
			            int j=i+1;
			            if(j<=nN){
			                collAttributes.add(new SpeedAttribute("speed_"+i+"_"+j,idS++));
			                collAttributes.add(new WSpeedAttribute("wSpeed_"+i+"_"+j,idS++));
			            }
			        }

			    break;

			case Frequency:
			    //AA: Lo schema frequenza può essere defiito solo dopo la discretizzazione, quanod èesplicitato in numero di intervalli

			    /*  for(i=1;i<=nN;i++){
                for(int j=1;j<=l.getDiscreteIntervalNumber();j++)
                    collAttributes.add(new FrequencyAttribute("freq_N"+i+"D"+j, idS++,ns.weight));
            }*/


			    // è necessario creare il collective schema perche quando si è chiamatato arffSchema non c'erano le informazioni per farlo
			    Collection<Double> bins=network.getSchema().getTarget().discreteBins();
			    for(i=1;i<=nN;i++){
			        Iterator<Double> it=bins.iterator();
			        double inf=it.next();
			        while (it.hasNext()) {
			            double sup=it.next();
			            String value=inf+"__"+sup;
			            value=value.replace(".", "_");
			            collAttributes.add(new FrequencyAttribute("freq_N"+i+"D"+value, idS++,ns.weight));

			            inf=sup;
			        }
			    }
                break;
			
            case Average:
                for(i=1; i<=nN; i++) {
                    collAttributes.add(new AverageAttribute("Average_N_" + i, idS++, ns.weight));
                }
                break;

            case GI:
                for(i=1; i<=nN; i++) {
                    collAttributes.add(new GIAttribute("GI*_N_" + i, idS++, ns.weight));
                }
                break;

            case Cluster:
                DataSetUtility d = new DataSetUtility(n);

                Instances data = d.createClusteringData(); 
                
                try {
                    // clusterizzo usando gli attributi descrittivi senza considerare la rete
                    getGraph().setfClustering(new DescriptiveClustering(data, network));
                    for(i=1; i<=getGraph().getfClustering().getClusters().length; i++) {
                        // per ogni cluster aggiungo un attributo
                        collAttributes.add(
                                new AverageAttribute("Cluster_N_" + i, idS++, ns.weight));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                
                try {
                    getGraph().setWeightedClustering(new ColleptiveClustering(network));
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            
            default:
                // TODO throw new collectiveSchemaNotFoundException
                System.err.println("Use a valid collective schema");
                break;
			}

			network.getSchema().setCollectiveAttributes(collAttributes);
		}
		
	}
	
	private class ArffSchema{
	/*	ArffSchema(BufferedReader br, NetworkSettings ns, COLLECTIVESCHEMA schema) throws IOException{
			
			List<Attribute> attributes = new LinkedList<Attribute>();
			int i=1;
			String line = br.readLine();
			while (line != null){
				String[] lineElements = line.split(" |\t");
				if(lineElements.length>0)
					if(lineElements[0].equals("@attribute")){
					
						Attribute currentAttribute;
						String name = lineElements[1];
						String type = lineElements[2];
						
						if(type.equals("numeric")){
							currentAttribute = new ContinuousAttribute(name,i);
						}else {
							//parse nominal values
				    		TreeSet<String> discValues = new TreeSet<String>();
				    		Scanner discreteScanner = new Scanner(type);
				    		discreteScanner.useDelimiter("\\{|\\}|,");
				    		
				    		while(discreteScanner.hasNext()){
				    			discValues.add(discreteScanner.next());
				    		}
				    		discreteScanner.close();
				    		currentAttribute = new DiscreteAttribute(name,i,discValues);
						}
						attributes.add(currentAttribute);
						i++;
					} else if (lineElements[0].equals("@data"))
						break;
				line = br.readLine();
			}
			
		//	List<CollectiveAttribute> collAttributes = new LinkedList<CollectiveAttribute>();
		//	int nN = ((ns.rmax-ns.rmin)/ns.step)+1;
		//	int idS=1;
			
			//AA: Lo schema frequenza può essere defiito solo dopo la discretizzazione, quanod èesplicitato in numero di intervalli
			//if(schema.equals(COLLECTIVESCHEMA.Frequency){
			//	for(i=1;i<=nN;i++){
			//		for(int j=1;j<=l.getDiscreteIntervalNumber();j++)
			//			collAttributes.add(new FrequencyAttribute("freq_N"+i+"D"+j, idS++,ns.weight));
			//	}
			//}
			//
		//	if(schema.equals(COLLECTIVESCHEMA.Mean)){
		//	for(i=1;i<=nN;i++){
		//		collAttributes.add(new MeanAttribute("mean_"+i,idS++));
		//		collAttributes.add(new WMeanAttribute("wMean_"+i,idS++,ns.weight));
		//		collAttributes.add(new StdDevAttribute("stdDev_"+i,idS++));
		//	}
		//	if(nN!=1)
		//		for(i=1;i<=nN;i++){
		//			int j=i+1;
		//			if(j<=nN){
		//			collAttributes.add(new SpeedAttribute("speed_"+i+"_"+j,idS++));
		//				collAttributes.add(new WSpeedAttribute("wSpeed_"+i+"_"+j,idS++));
			//		}
		//		}
		//	}
			
			
			
	//		network = new Graph(new Schema(attributes, collAttributes));
		}
	
		
		
	ArffSchema(BufferedReader br, NetworkSettings ns) throws IOException{
			
			List<Attribute> attributes = new LinkedList<Attribute>();
			int i=1;
			String line = br.readLine();
			while (line != null){
				String[] lineElements = line.split(" |\t");
				if(lineElements.length>0)
					if(lineElements[0].equals("@attribute")){
					
						Attribute currentAttribute;
						String name = lineElements[1];
						String type = lineElements[2];
						
						if(type.equals("numeric")){
							currentAttribute = new ContinuousAttribute(name,i);
						}else {
							//parse nominal values
				    		TreeSet<String> discValues = new TreeSet<String>();
				    		Scanner discreteScanner = new Scanner(type);
				    		discreteScanner.useDelimiter("\\{|\\}|,");
				    		
				    		while(discreteScanner.hasNext()){
				    			discValues.add(discreteScanner.next());
				    		}
				    		discreteScanner.close();
				    		currentAttribute = new DiscreteAttribute(name,i,discValues);
						}
						attributes.add(currentAttribute);
						i++;
					} else if (lineElements[0].equals("@data"))
						break;
				line = br.readLine();
			}
			
			List<CollectiveAttribute> collAttributes = new LinkedList<CollectiveAttribute>();

			// il collAttributes è vuoto
			network = new Graph(new Schema(attributes, collAttributes));
		}
	
		*/
		ArffSchema(BufferedReader br) throws IOException{
		
			List<Attribute> attributes = new LinkedList<Attribute>();
			int i=1;
			String line = br.readLine();
			while (line != null){
				String[] lineElements = line.split(" |\t");
				if(lineElements.length>0)
					if(lineElements[0].equals("@attribute")){
					
						Attribute currentAttribute;
						String name = lineElements[1];
						String type = lineElements[2];
						
						if(type.equals("numeric")){
							currentAttribute = new ContinuousAttribute(name,i);
						}else {
							//parse nominal values
				    		TreeSet<String> discValues = new TreeSet<String>();
				    		Scanner discreteScanner = new Scanner(type);
				    		discreteScanner.useDelimiter("\\{|\\}|,");
				    		
				    		while(discreteScanner.hasNext()){
				    			discValues.add(discreteScanner.next());
				    		}
				    		discreteScanner.close();
				    		currentAttribute = new DiscreteAttribute(name,i,discValues);
						}
						attributes.add(currentAttribute);
						i++;
					} else if (lineElements[0].equals("@data"))
						break;
				line = br.readLine();
			}
			
	
		//	network = new Graph(new Schema(attributes));
			network.setSchema(new Schema(attributes));
		}
	}

	
	

	private class Config{
		Config(BufferedReader br) throws IOException{
			
			String line = br.readLine();
			while (line != null){
				String[] lineElements = line.split(" ");
				if(lineElements[0].equals("@idNode")){
					int indexId = Integer.parseInt(lineElements[1]);
					if(indexId==0)indexId++;
					Attribute current = network.getSchema().getAttribute(indexId);
					current.setIsNodeId(true);
					current.setIndexValue(-1);
					network.getSchema().setId(current);
				}else if(lineElements[0].equals("@target")){
					int indexTarget = Integer.parseInt(lineElements[1]);
					Attribute current = network.getSchema().getAttribute(indexTarget);
					try{
						((ContinuousAttribute)current).setdiscretizationN(l.getDiscreteIntervalNumber());
						((ContinuousAttribute)current).setDiscretizationAlgorithm(l.getDiscretizationAlgorithm());
					} catch(NullPointerException e) {//System.out.println("No discretization!");
						}
					
					current.setIsTarget(true);
					current.setIndexValue(-1);
				
					network.getSchema().setTarget((ContinuousAttribute)current);
					
				}else if(lineElements[0].equals("@neglected")){
					String[] neglected = lineElements[1].split(",");
					for(int i=0;i<neglected.length;i++){
						int indexNeglected = Integer.parseInt(neglected[i]);
						Attribute current = network.getSchema().getAttribute(indexNeglected);
						current.setIsNeglected(true);
						current.setIndexValue(-1);
					}
				}
				line = br.readLine();
			}
			
			int j=1;
			for(int i=1;i<network.getSchema().numberOfAttributes()+1;i++){
				if(network.getSchema().getAttribute(i).getIndexValue()!=-1){
					network.getSchema().getAttribute(i).setIndexValue(j);
					j++;
				}
			}
		}
	}
	
	private class ArffData{
		ArffData(BufferedReader br) throws IOException{
			int indexSchemaId=network.getSchema().getId().getIndexSchema()-1;
			int indexSchemaTarget=network.getSchema().getTarget().getIndexSchema()-1;
			String line = br.readLine();
			while (line != null){
				String[] node = line.split(",| |\t");
				
				int idValue = Integer.parseInt(node[indexSchemaId]);
				Value id = new ContinuousValue(idValue);
				//setMinMax(((ContinuousAttribute)network.getSchema().getId()),idValue);
				
				
				//setMinMax(((ContinuousAttribute)network.getSchema().getId()),idValue);
				//if(l.getDiscretizationAlgorithm().equals(DISCRETIZATIONTYPE.EF)) setDomain(((ContinuousAttribute)network.getSchema().getId()),idValue);
			
				
				double targetValue = Double.parseDouble(node[indexSchemaTarget]);
				ContinuousValue target = new ContinuousValue(targetValue);
				//setMinMax(network.getSchema().getTarget(),targetValue);
				//if(l.getDiscretizationAlgorithm().equals(DISCRETIZATIONTYPE.EF)) setDomain(network.getSchema().getTarget(),targetValue);
				
				
				//setMinMax(((ContinuousAttribute)network.getSchema().getTarget()),targetValue);
				
				List<Value> values = new LinkedList<Value>();
				Iterator<Attribute> it = network.getSchema().getIteratorExplanatory();
			//	System.out.println(line);
				while(it.hasNext()){
					Attribute current = it.next();
					
					if(current instanceof ContinuousAttribute){
						double value = Double.parseDouble(node[current.getIndexSchema()-1]);
						
						//setMinMax(((ContinuousAttribute) current),value);
						
						//setMinMax(((ContinuousAttribute) current),value);
						//if(l.getDiscretizationAlgorithm().equals(DISCRETIZATIONTYPE.EF)) setDomain(((ContinuousAttribute) current),value);
					
						
						values.add(current.getIndexValue()-1, new ContinuousValue(value));
						
					}else if (current instanceof DiscreteAttribute){
						values.add(current.getIndexValue()-1, new DiscreteValue(node[current.getIndexSchema()-1]));
					}
				}
				
				network.addNode(new Node(id,values,target));
				line = br.readLine();
			}
		}
		
		/*
		 * Agiornamin e max di ogni attributo
		 * Usato nel package clustering 2, pr poter ffettuare scaling di attributi contiui 
		 */
		ArffData(BufferedReader br, boolean minmax) throws IOException{
			int indexSchemaId=network.getSchema().getId().getIndexSchema()-1;
			int indexSchemaTarget=network.getSchema().getTarget().getIndexSchema()-1;
			String line = br.readLine();
			while (line != null){
				String[] node = line.split(",| |\t");
				
				int idValue = Integer.parseInt(node[indexSchemaId]);
				Value id = new ContinuousValue(idValue);
				setMinMax(((ContinuousAttribute)network.getSchema().getId()),idValue);
				
				
				//setMinMax(((ContinuousAttribute)network.getSchema().getId()),idValue);
				//if(l.getDiscretizationAlgorithm().equals(DISCRETIZATIONTYPE.EF)) setDomain(((ContinuousAttribute)network.getSchema().getId()),idValue);
			
				
				double targetValue = Double.parseDouble(node[indexSchemaTarget]);
				ContinuousValue target = new ContinuousValue(targetValue);
				setMinMax(network.getSchema().getTarget(),targetValue);
				//if(l.getDiscretizationAlgorithm().equals(DISCRETIZATIONTYPE.EF)) setDomain(network.getSchema().getTarget(),targetValue);
				
				
				//setMinMax(((ContinuousAttribute)network.getSchema().getTarget()),targetValue);
				
				List<Value> values = new LinkedList<Value>();
				Iterator<Attribute> it = network.getSchema().getIteratorExplanatory();
				while(it.hasNext()){
					Attribute current = it.next();
					
					if(current instanceof ContinuousAttribute){
						double value = Double.parseDouble(node[current.getIndexSchema()-1]);
						
						setMinMax(((ContinuousAttribute) current),value);
						
						//setMinMax(((ContinuousAttribute) current),value);
						//if(l.getDiscretizationAlgorithm().equals(DISCRETIZATIONTYPE.EF)) setDomain(((ContinuousAttribute) current),value);
					
						
						values.add(current.getIndexValue()-1, new ContinuousValue(value));
						
					}else if (current instanceof DiscreteAttribute){
						values.add(current.getIndexValue()-1, new DiscreteValue(node[current.getIndexSchema()-1]));
					}
				}
				
				network.addNode(new Node(id,values,target));
				line = br.readLine();
			}
		}
		
	}

	private void setMinMax(ContinuousAttribute current, double value){
		if(network.size()==0){
			((ContinuousAttribute) current).setMin(value);
			((ContinuousAttribute) current).setMax(value);
		}else{
			((ContinuousAttribute) current).updateMin(value);
			((ContinuousAttribute) current).updateMax(value);
			}
	}	

	private void setDomain(ContinuousAttribute current, double value){
		
			((ContinuousAttribute) current).updateDomain(value);;
			
	}	
	private class Clusterings{
		Clusterings(BufferedReader br) throws IOException{
			HashMap<Integer,Cluster> clusteredNodes=new HashMap<Integer,Cluster>();
			String line = br.readLine();
			// skip line startig by #
			while(line.charAt(0)=='#'){
				line = br.readLine();
				if(line.indexOf("Number of clusters")!=-1){
					//#Number of clusters:3
					
					int k=new Integer(line.split(":")[1]);
					for(int i=0;i<k;i++)
						clusteredNodes.put(i,new Cluster(i));
				}
				
				
			}
			

			HashMap<Value, Cluster> mapCluster=new HashMap<Value, Cluster>();
			while(line!=null){
				String[] node = line.split(",");
				Value idNode = new ContinuousValue(Integer.parseInt(node[0]));
				int idCluster =  Integer.parseInt(node[1]);
				Cluster c=clusteredNodes.get(idCluster);
				mapCluster.put(idNode,c);
				
				line = br.readLine();
			}
			
			for(Node n:network){
				Cluster c=mapCluster.get(n.getId());
				if(c==null )throw new RuntimeException("Uncorrect Clustering file for node "+n);
				c.add(n);
				n.setCluster(c);
			}
			
			/*
			for (Node n:network){
				String[] node = line.split(",");
				
				Value idNode = new ContinuousValue(Integer.parseInt(node[0]));
				int idCluster =  Integer.parseInt(node[1]);
				if(!n.getId().equals(idNode)) throw new RuntimeException("Uncorrect Clustering file "+n + " "+ idNode);
				Cluster c=clusteredNodes.get(idCluster);
				c.add(n);
				n.setCluster(c);
				line = br.readLine();
			}
			*/
		}
		

	}
	private class Sample{
		Sample(BufferedReader br) throws IOException{
			String line = br.readLine();
			while (line != null){
				Node n = new Node(new ContinuousValue((int)Double.parseDouble(line)),null,null);
				
				Iterator<Node> it = network.iterator();
				while(it.hasNext()){
					Node current = it.next();
					if(current.compareTo(n)==0){
						current.setIsSample(true);
						// Set min max of sampled data
						setMinMax(((ContinuousAttribute)network.getSchema().getTarget()),(double)(current.getTarget().getValue()));
						if(l.getDiscretizationAlgorithm().equals(DISCRETIZATIONTYPE.EF)) setDomain(((ContinuousAttribute)network.getSchema().getTarget()),(double)(current.getTarget().getValue()));
						break;
					}
				}
				line = br.readLine();
			}
		}
	}

	private class Edges{
		Edges(BufferedReader br) throws IOException{
			String line = br.readLine();
			//
			
			Map<Node,Node> nodeStructure=new HashMap<Node,Node>();
			for(Node n:network)
				nodeStructure.put(n,n);
			
			
			while (line != null){
				String[] edge = line.split(",");
				Node from = new Node(new ContinuousValue(Integer.parseInt(edge[0])),null,null);
				Node to = new Node(new ContinuousValue(Integer.parseInt(edge[1])),null,null);
			
				Node f=nodeStructure.get(from);
				if(f!=null){
					Node t=nodeStructure.get(to);
					if(t!=null){
						//if(Float.parseFloat(edge[2])<=2.5)
							network.addEdge(f, t, Float.parseFloat(edge[2]));
					}
				}
				
				line = br.readLine();
			}
			
		}
	}
	
	
	public String toString(){
		return network.toString();// +"\n"+clusteredNodes.toString();
	}
	
	/* 
	 * Esporta gli id dei nodi che attualemte costituiscongo il training set (esteso)
	 */
	public void saveTrainingSample(String fileName) throws IOException,FileNotFoundException{
		getGraph().saveTrainingSample(fileName);
	}

}

