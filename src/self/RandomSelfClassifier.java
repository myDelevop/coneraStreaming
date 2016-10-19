package self;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.GregorianCalendar;
import java.util.Set;

import collective.AbstractTopSelector;
import data.network.DataSetUtility;
import data.network.Network;
import mining.EvaluationMeasure;
import mining.MAE;
import mining.R2;
import mining.RAE;
import mining.RMSE;
import mining.RSE;
import predicting.ClassificationView;
import predicting.ClusterBasedSelector;
import predicting.RELIABILITY_TYPE;
import predicting.ReliabilityCouple;
import predicting.TopSelector;
import setup.LearningSettings;
import weka.core.Instances;

public class RandomSelfClassifier {

	
	private Network network;
	private ClassificationView descriptorView;

	private LearningSettings cs;
	private String reportFileName;
	
	private AbstractTopSelector activeSelector;
	
		
	public RandomSelfClassifier(Network n, LearningSettings cs,String sampleFileName, AbstractTopSelector activeSelector) {
		
		
		network = n;
		this.cs=cs;
		this.reportFileName=sampleFileName+cs.getReliabilityType()+"R";
		this.activeSelector=activeSelector;
		
	}
	public void compute()throws Exception{

		String pathConfig = "Aco2COutput"+"/"+reportFileName+"/";
		new File(pathConfig.replace(".", "_")).mkdirs();
		String filename = reportFileName; //+"a"+cs.getA()+"_it"+cs.getIterations();
		
		
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream((pathConfig+filename).replace(".", "_")+"Report.txt"), "utf-8"));
		writer.write("Report:"+filename+"\n");
	
		GregorianCalendar gStart= new GregorianCalendar();
		DataSetUtility d = new DataSetUtility(network);
		
		descriptorView =  new ClassificationView(network,d.createTrainingSet(),d.createNullLlabeledWorkingSet(),d.getWorkingNodes());
		
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
		"mae:"+maeV+"\n"+"r2V:"+r2V+"\n"+"rae:"+raeV+"\n"+"rse:"+rseV+"\n"+"Elapsed time:"+elapsedTime+"\n";
		
		System.out.print(message);
		writer.write(message);
		

		int i=0;
		
		//for(int i=1;i<=cs.getIterations() && workingSize>0;i++)
		while(trainingSize <cs.getFinalNumberoOfExamples() && workingSize>0)
		{
			i++;
			// Evaluation phase

						gStart= new GregorianCalendar();
			//aggiorna collective features
					
			// determino affidabilità di previsioni collettive nel grafo
			Set<ReliabilityCouple> reliabilitySet =descriptorView.computeReliability(cs.getReliabilityType(),cs.indexNh,false);	//2
			
			// determino i nodi per acctive learning sulla base di affidabilità di previsioni collettive
			activeSelector.setReliabilitySet(reliabilitySet);
			
			int topA=cs.getA();
			if(cs.getFinalNumberoOfExamples()-trainingSize <topA)
				topA=cs.getFinalNumberoOfExamples()-trainingSize;
			Set<Integer> topReliabilityDescriptorSet=activeSelector.compute(topA);//ClassificationView.getTopReliabilitySet(cs.a, reliabilitySet);
			
			//Sposto nodi attivi da training set a working set di descripto view
			System.out.println("Updating descriptive view ...");
			descriptorView.modifyTrainingInstances(topReliabilityDescriptorSet);	
			

			descriptorView.deleteWorkingInstances(topReliabilityDescriptorSet);
			
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
					"mae:"+maeV+"\n"+"r2V:"+r2V+"\n"+"rae:"+raeV+"\n"+"rse:"+rseV+"\n"+"Elapsed time:"+elapsedTime+"\n";
			
			System.out.print(message);
			writer.write(message);
		
			
			
		}
		writer.close();
		
		// salvo il training sample creato in maniera random
		//
		String trainingfileName=(pathConfig+reportFileName+"Sample").replace(".","_")+".txt";
		network.saveTrainingSample(trainingfileName);
	}

	
	

	static public void main(String args[]) throws Exception{
		{			
		
			String data=null;
			String distance=null;
			String sample=null;
			String configData=null;
			String configLearning=null;
			String configLearner=null;
			
			int i=0;
			while(i<args.length){
				if(args[i].toLowerCase().equals("-data"))
					data=args[i+1];
				else if(args[i].toLowerCase().equals("-distance"))
					distance=args[i+1];
				else if(args[i].toLowerCase().equals("-sample"))
					sample=args[i+1];
				else if(args[i].toLowerCase().equals("-configdata"))
					configData=args[i+1];
				else if(args[i].toLowerCase().equals("-configlearning"))
					configLearning=args[i+1];
				else if(args[i].toLowerCase().equals("-configlearner"))
					configLearner=args[i+1];
				i+=2;
				
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
			
			RandomSelfClassifier aco2=null;
			aco2=new RandomSelfClassifier(new Network("Aco2CInput/"+data,
						"Aco2CInput/"+distance,
						"Aco2CInput/"+sample,
						"Aco2CInput/"+configData,
						"Aco2CInput/"+configLearning,
						cs),
						cs,sample,new RandomSelector());
			
			aco2.compute();
		
		}
	}

}
