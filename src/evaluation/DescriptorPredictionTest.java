package evaluation;

import java.io.BufferedWriter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.GregorianCalendar;

import data.network.DataSetUtility;
import data.network.Network;
import mining.Predictor;
import mining.EvaluationMeasure;
import mining.MAE;
import mining.R2;
import mining.RAE;
import mining.RMSE;
import mining.RSE;
import setup.LearningSettings;

public class DescriptorPredictionTest {
	
	private String expName;
	private String configFileName;
	private String testArffName;
	private String reportFile;
	
	//private int it;
	DescriptorPredictionTest(String expName,   String configFileName, String testArffName, String reportFile){
		this.expName=expName;
		
		this.configFileName=configFileName;
		this.testArffName=testArffName;
		this.reportFile=reportFile;
//		this.it=it;
	
		
	}

	
	private void test()throws IOException{
		String pathClassifier = "Aco2COutput/"+expName+"/"+expName;
		String pathTestData = "Aco2CInput/";
		
		
		
		
	
	//	BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pathClassifier+"TestReport"+expName+"a"+a+"_it"+it+".csv"), "utf-8"));
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("Aco2COutput/"+expName+"/"+reportFile), "utf-8"));
	
		writer.write(
				"Iteration;elapsedTime;rmse;mae;r2;rae;rse\n"						
				);
		for(int i=0;i<=Integer.MAX_VALUE;i++){

			String fileName=pathClassifier+"D"+i;
			try {
				GregorianCalendar startTime=new GregorianCalendar();
				weka.classifiers.Classifier classifier=null;
				try{
					classifier=Predictor.load(fileName);
				}
				catch(Exception e){
					break;
				}
				String fileTestArffName=pathTestData+testArffName;
				String fileConfigFileName=pathTestData+configFileName;
				//Carico network comprensivo di nodi 
				
				Network n = new Network(fileTestArffName, fileConfigFileName);
				DataSetUtility d=new DataSetUtility(n);
						
				ClassifierTest testC=new ClassifierTest(classifier, d.createNullLlabeledWorkingSet(), d.getWorkingNodes());
				testC.classify();
				GregorianCalendar endTime=new GregorianCalendar();
				long elapsedTime=endTime.getTimeInMillis()-startTime.getTimeInMillis();
				
				// Evaluation phase
				
				EvaluationMeasure rmse=new RMSE(n);
				EvaluationMeasure mae=new MAE(n);
				EvaluationMeasure r2=new R2(n);
				EvaluationMeasure rae= new RAE(n);
				EvaluationMeasure rse= new RSE(n);
						
				double rmseV=rmse.compute();
				double maeV=mae.compute();
				double r2V=r2.compute();
				double raeV=rae.compute();
				double rseV=rse.compute();
				
				String message=""+i+";"+elapsedTime+";"+ rmseV+";"+maeV+";"+r2V+";"+raeV+";"+rseV+";";
						
				//System.out.println(message);
				message=message.replace(".",",");
				writer.write(message+"\n");
			} catch (ClassNotFoundException | IOException e) {
				System.out.println(e.toString());
			}
			catch (Exception e) {
				System.out.println(e.toString());
			}
		}
		
		writer.close();
			
	}
	
	public static void main(String args[]){
		//movies2.arffTrain_1.arffSample1.txtSELFa3_it10 10 configMovieEpured.ini  movies2.arffTest_1.arff SELFSTDEVREPDiscF10STDEV.csv
		if(args.length==4){
			DescriptorPredictionTest pTest=new  DescriptorPredictionTest(args[0],  args[1],args[2], args[3]);
			try {
				pTest.test();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
		else System.out.println("Wrong parameter. \nPlease specify: \nString expName (arff training file),  \nint a (number of active examples per iteration), \nint it (maximum number of iterations), \nString configFileName (arff config file name),\nString testArffName (arff test file\n ");
	}
}
