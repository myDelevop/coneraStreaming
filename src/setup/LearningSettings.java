package setup;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import collective.COLLECTIVESCHEMA;
import data.schema.DISCRETIZATIONTYPE;
import mining.CLASSIFIER_TYPE;
import predicting.RELIABILITY_TYPE;

public class LearningSettings {
	private CLASSIFIER_TYPE regressorType;
	private CLASSIFIER_TYPE classifierType;
	private RELIABILITY_TYPE reliabilityType;
	private COLLECTIVESCHEMA collectiveSchema;
	private int discreteIntervalNumber =5;
	private DISCRETIZATIONTYPE discretizationAlgorithm=DISCRETIZATIONTYPE.EW;
	private int a = 0;
	//private int iterations = 0;
	public int indexNh = 0;
	private double aPerc=0; // 0%..100%
	private double maxTrainSizePerc=0;// 0%..100%
	private int finalNumberOfExamples=0; 
	
	public LearningSettings(String cotrainingSettingsFilename) throws IOException{
		BufferedReader br=null;
		
		try {
			br = new BufferedReader(new FileReader(cotrainingSettingsFilename));
		
			String line = br.readLine();
			while (line != null){
				String[] lineElements = line.split(" ");
				//if(lineElements[0].equals("@a")){
				//	setA(Integer.parseInt(lineElements[1]));
				//}
				 if(lineElements[0].equals("@aPerc")){
						aPerc=new Double(lineElements[1]);
				 }
				//else if(lineElements[0].equals("@iterations")){
				//	setIterations(Integer.parseInt(lineElements[1]));
				//}
				 else if (lineElements[0].equals("@maxTrainSizePerc")){
						maxTrainSizePerc=new Double(lineElements[1]);
				 }
				 else if(lineElements[0].equals("@nh")){
					indexNh = Integer.parseInt(lineElements[1]);
				}else if(lineElements[0].equals("@reliability")){
					setReliabilityType(RELIABILITY_TYPE.valueOf(lineElements[1]));
				}else if(lineElements[0].equals("@regressor")){
					setRegressorType(CLASSIFIER_TYPE.valueOf(lineElements[1]));
				}
				else if(lineElements[0].equals("@classifier")){
					setClassifierType(CLASSIFIER_TYPE.valueOf(lineElements[1]));
				}
				else if(lineElements[0].equals("@schema")){
					setCollectiveSchema(COLLECTIVESCHEMA.valueOf(lineElements[1]));
					if(getCollectiveSchema()==COLLECTIVESCHEMA.Frequency){
						setDiscretizationAlgorithm(DISCRETIZATIONTYPE.valueOf(lineElements[2]));
						setDiscreteIntervalNumber(new Integer(lineElements[3]));
						
					}
				}
				line = br.readLine();
			}
		}catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
		//if(getA()<1 || getIterations() <1 || indexNh<1){
		if(maxTrainSizePerc<=0 || maxTrainSizePerc>1 || aPerc<=0 || aPerc >=1|| indexNh<1){
			System.err.println("Il file cotraining_settings.ini contiene valori errati");
			System.exit(1);
		}
	}

	public int getA() {
		return a;
	}

	public void setA(int a) {
		this.a = a;
	}

	/*public int getIterations() {
		return iterations;
	}

	public void setIterations(int iterations) {
		this.iterations = iterations;
	}*/

	public RELIABILITY_TYPE getReliabilityType() {
		return reliabilityType;
	}

	public void setReliabilityType(RELIABILITY_TYPE reliabilityType) {
		this.reliabilityType = reliabilityType;
	}

	public CLASSIFIER_TYPE getRegressorType() {
		return regressorType;
	}

	public void setRegressorType(CLASSIFIER_TYPE classifierType) {
		this.regressorType = classifierType;
	}

	public COLLECTIVESCHEMA getCollectiveSchema() {
		return collectiveSchema;
	}

	public void setCollectiveSchema(COLLECTIVESCHEMA collectiveSchema) {
		this.collectiveSchema = collectiveSchema;
	}

	public CLASSIFIER_TYPE getClassifierType() {
		return classifierType;
	}

	public void setClassifierType(CLASSIFIER_TYPE classifierType) {
		this.classifierType = classifierType;
	}

	public int getDiscreteIntervalNumber() {
		return discreteIntervalNumber;
	}

	public void setDiscreteIntervalNumber(int discreteIntervalNumber) {
		this.discreteIntervalNumber = discreteIntervalNumber;
	}

	public DISCRETIZATIONTYPE getDiscretizationAlgorithm() {
		return discretizationAlgorithm;
	}

	public void setDiscretizationAlgorithm(DISCRETIZATIONTYPE discretizationAlgorithm) {
		this.discretizationAlgorithm = discretizationAlgorithm;
	}
	
	public void update(int numberOfNodes){
		a=(int)(aPerc*numberOfNodes);
		if(a<(aPerc*numberOfNodes))
			a=a+1; // arrotondo per eccesso
		finalNumberOfExamples=(int)(numberOfNodes*maxTrainSizePerc);
		if(finalNumberOfExamples < numberOfNodes*maxTrainSizePerc)
			finalNumberOfExamples+=1;// arrotondo per eccesso
		//this.iterations=
	}
	
	
	public int getFinalNumberoOfExamples(){
		return finalNumberOfExamples;
	}
}

