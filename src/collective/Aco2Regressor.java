package collective;


import java.io.BufferedWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.GregorianCalendar;
import java.util.Set;

import mining.CLASSIFIER_TYPE;
import mining.EvaluationMeasure;
import mining.MAE;
import mining.R2;
import mining.RMSE;
import mining.RSE;
import predicting.ClassificationView;
import predicting.ClusterBasedSelector;

import predicting.ClusterPredictionSelector;
import predicting.RELIABILITY_TYPE;
import predicting.ReliabilityCouple;
import predicting.TopSelector;
import setup.LearningSettings;
import weka.core.Instances;
import mining.RAE;
import data.network.DataSetUtility;
import data.network.Network;
import distance.DistanceMap;

public class Aco2Regressor {
	
	protected Network network;
	protected ClassificationView descriptorView;
	protected ClassificationView collectiveView;
	protected LearningSettings cs;
	protected String reportFileName;
	
	protected AbstractTopSelector activeSelector;
	
	protected DistanceMap distances;
		
	public Aco2Regressor(Network n, LearningSettings cs,String sampleFileName, AbstractTopSelector activeSelector, DistanceMap distances) {
		
		
		this(n,cs,sampleFileName,activeSelector);
		this.distances=distances;
	}

	public Aco2Regressor(Network n, LearningSettings cs,String sampleFileName, AbstractTopSelector activeSelector) {
		
		
		network = n;
		this.cs=cs;
		this.reportFileName=sampleFileName+cs.getReliabilityType()+"R2";
		this.activeSelector=activeSelector;
	}
	protected void compute(COLLECTIVESCHEMA schema)throws Exception{

		String pathConfig = "Aco2COutput"+"/"+reportFileName+"/";
		new File(pathConfig.replace(".", "_")).mkdirs();
		String filename = reportFileName;
		
		
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream((pathConfig+filename).replace(".", "_")+"Report.txt"), "utf-8"));
		writer.write("Report:"+filename+"\n");
	
		GregorianCalendar gStart= new GregorianCalendar();
		DataSetUtility d = new DataSetUtility(network);
		
		descriptorView =  new ClassificationView(network,d.createTrainingSet(),d.createNullLlabeledWorkingSet(),d.getWorkingNodes(), distances);
		
		int trainingSize=descriptorView.getTrainingSize();
		int workingSize=network.getGraph().countWorkingNodes();
		
		
		//collectiveView = new ClassificationView(network,d.createCollectiveTrainingSet(),d.createCollectiveWorkingSet(),d.getWorkingNodes());
		
		
		//apprendo classificatore da descrittori e lo uso per predire working set, modifico grafo con queste predizioni
		
		descriptorView.classify(cs.getRegressorType());
		
		GregorianCalendar gEnd= new GregorianCalendar();
		
		// serializzo il classifiactore	
		descriptorView.serializeClassifier(pathConfig+filename+"D0");
		
		
		
		EvaluationMeasure rmse=new RMSE(network);
		EvaluationMeasure mae=new MAE(network);
		EvaluationMeasure r2=new R2(network);
		EvaluationMeasure rae= new RAE(network);
		EvaluationMeasure rse= new RSE(network);
				
		double rmseV=rmse.compute();
		double maeV=mae.compute();
		double r2V=r2.compute();
		double raeV=rae.compute();
		double rseV=rse.compute();
		
		long elapsedTime=gEnd.getTimeInMillis()-gStart.getTimeInMillis();
		String message="***Iteration "+ "0"+"\n"+"Labeled nodes:"+trainingSize+"\n"+"Unlabeled nodes:"+workingSize+"\n"+"rmse:"+rmseV+"\n"+
		"mae:"+maeV+"\n" +"r2V:"+r2V+"\n"+"rae:"+raeV+"\n"+"rse:"+rseV+"\n"+"Elapsed time:"
		+elapsedTime+"\n";
		
		System.out.print(message);
		writer.write(message);
		

		
		
		//for(int i=1;i<=cs.getIterations() && workingSize>0;i++){
		int i=0;
		while(trainingSize <cs.getFinalNumberoOfExamples() && workingSize>0)
		{
			i++;
			// Evaluation phase

			gStart= new GregorianCalendar();
			//aggiorna collective features
			//System.out.println(network.getGraph().getSchema().printCollectiveSchema());
			
			computeCollectiveValues(schema);

			//Costruisco il data set collettivo (training + working)
			collectiveView = new ClassificationView(network,d.createRegressionCollectiveTrainingSet(),d.createRegressionCollectiveWorkingSet(),d.getWorkingNodes(),distances);
			
			//apprendo classificatore collettivo e lo uso per predire working set, modifico grafo con queste predizioni
			collectiveView.classify(cs.getRegressorType());					
		
			
			gEnd=new GregorianCalendar();
			rmse=new RMSE(network);
			mae=new MAE(network);
			r2=new R2(network);
			rae= new RAE(network);
			rse= new RSE(network);
					
			rmseV=rmse.compute();
			maeV=mae.compute();
			r2V=r2.compute();
			raeV=rae.compute();
			rseV=rse.compute();
		
			
			elapsedTime=gEnd.getTimeInMillis()-gStart.getTimeInMillis();
		
			
			message="***Iteration "+i+" COLLECTIVE STEP\n"+"Labeled nodes:"+trainingSize+"\n"+"Unlabeled nodes:"+workingSize+"\n"+"rmse:"+rmseV+"\n"+
					"mae:"+maeV+"\n" +"r2V:"+r2V+"\n"+"rae:"+raeV+"\n"+"rse:"+rseV+"\n"
					+"Elapsed time:"+elapsedTime+"\n";
			
			
			System.out.print(message);
			writer.write(message);
			
			gStart= new GregorianCalendar();
			//aggiorna collective features
					
			// determino affidabilità di previsioni collettive nel grafo
			Set<ReliabilityCouple> reliabilitySet =collectiveView.computeReliability(cs.getReliabilityType(),cs.indexNh, distances!=null);	//2
			
			
			
			// determino i nodi per active learning sulla base di affidabilità di previsioni collettive
			activeSelector.setReliabilitySet(reliabilitySet);
			int topA=cs.getA();
			if(cs.getFinalNumberoOfExamples()-trainingSize <topA)
				topA=cs.getFinalNumberoOfExamples()-trainingSize;
			Set<Integer> topReliabilityDescriptorSet=activeSelector.compute(topA);//ClassificationView.getTopReliabilitySet(cs.a, reliabilitySet);
			
			
			
			
			
			//Sposto nodi attivi da training set a working set di descripto view
			System.out.println("Updating descriptive view ...");
			
			
			descriptorView.modifyTrainingInstances(topReliabilityDescriptorSet);
			
			descriptorView.deleteWorkingInstances(topReliabilityDescriptorSet);
			
			network.updateCollectiveSchema();
			
			//apprendo classificatore da descrittori e lo uso per predire working set, modifico grafo con queste predizioni
			descriptorView.classify(cs.getRegressorType());		
			
			gEnd= new GregorianCalendar();
			
			
			workingSize-=topReliabilityDescriptorSet.size();
			trainingSize+=topReliabilityDescriptorSet.size();
				
			// serializzo il classifiactore	
			descriptorView.serializeClassifier(pathConfig+filename+"D"+(i));
			
			
			// Evalaution phase
			
			
			rmseV=rmse.compute();
			maeV=mae.compute();
			r2V=r2.compute();
			raeV=rae.compute();
			rseV=rse.compute();
			
			elapsedTime=gEnd.getTimeInMillis()-gStart.getTimeInMillis();
			message="***Iteration "+i+" DESCRIPTIVE STEP\n"+"Labeled nodes:"+trainingSize+"\n"+"Unlabeled nodes:"+workingSize+"\n"+"rmse:"+rmseV+"\n"+
					"mae:"+maeV+"\n"+"r2V:"+r2V+"\n"+"rae:"+raeV+"\n"+"rse:"+rseV+"\n"+"Elapsed time:"
					+elapsedTime+"\n";
			
			System.out.print(message);
			writer.write(message);
		
			
			
		}
		writer.close();
	}

	protected void computeCollectiveValues(COLLECTIVESCHEMA schema){
		this.network.getGraph().computeCollectiveValuesOnline(schema);
	}
	
	static public void main(String args[]) throws Exception{
		{			
	
			String data=null;
			String distance=null;
			String sample=null;
			String configData=null;
			String configLearning=null;
			String configLearner=null;
			Boolean clustering=false;
			Boolean distanceWeight=false; 
			int i=0;
			while(i<args.length){
				if(args[i].toLowerCase().equals("-data"))
				{	data=args[i+1];
					i+=2;
				}
				else if(args[i].toLowerCase().equals("-distance"))
					{
					distance=args[i+1];
					i+=2;
					}
				else if(args[i].toLowerCase().equals("-sample"))
				{
					sample=args[i+1];
					i+=2;
					}
				else if(args[i].toLowerCase().equals("-configdata")){
					configData=args[i+1];
					i+=2;
				}
				else if(args[i].toLowerCase().equals("-configlearning")){
					configLearning=args[i+1];
					i+=2;
				}
				else if(args[i].toLowerCase().equals("-configlearner"))
				{	configLearner=args[i+1];
				i+=2;
				}
				else if(args[i].toLowerCase().equals("-clustering"))
				{	clustering=true;
					
					i+=1;
				}
				else if (args[i].toLowerCase().equals("-distanceweight"))
				{
					distanceWeight=true;
					i+=1;
				}
				else throw new RuntimeException("Unsopported input parameter: "+ args[i]+"!");
			}
				
			
				
			if(data==null)
			{
				throw new RuntimeException("Unknown network data!");
			}
			if(distance==null)
			{
				throw new RuntimeException("Unknown network distances!");
			}
			if(configData==null)
			{
				throw new RuntimeException("Unknown configuration of data!");
			}
			if(configLearning==null)
			{
				throw new RuntimeException("Unknown configuration of collective information!");
			}
			if(configLearner==null)
			{
				throw new RuntimeException("Unknown configuration of classification process!");
			}
			
			
			LearningSettings cs = new LearningSettings(configLearner);
	
			
			Aco2Regressor aco2=null;
			
			System.out.println("Running with clustering:"+clustering +"\nRunning with reliability distance weighting:"+distanceWeight);
			
			
			
			Network network=new Network("Aco2CInput/"+data,
					"Aco2CInput/"+distance,					
					"Aco2CInput/"+sample,
					"Aco2CInput/"+configData,
					"Aco2CInput/"+configLearning,
					cs);
			
			
			if(distanceWeight){
				DistanceMap distances=DistanceMap.deserialize("Aco2CInput/distance/"+data.replace(".","_")+".serialized");
				aco2=new Aco2Regressor(network,
					cs,
					sample,
					new ClusterPredictionSelector(clustering),
					distances); //December 2015
			}
			else // no distance map to weight reliability
				aco2=new Aco2Regressor(network,
						cs,
						sample,
						new ClusterPredictionSelector(clustering)
						); //December 2015
				
			
			aco2.compute(cs.getCollectiveSchema());
		
		}
	}
}
