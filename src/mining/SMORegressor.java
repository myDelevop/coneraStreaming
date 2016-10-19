package mining;

import java.util.List;

import data.dataInstance.Node;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMOreg;
import weka.classifiers.functions.supportVector.RBFKernel;

import weka.core.Instances;

public class SMORegressor extends Regressor {
	private double C=64; //(int)(this.getTraining().numInstances()/ni);;
	private double gamma=0.125;
	private int nFolds=5; // for evalautions
	
	
	 private SMOreg learnSMOReg(double c, double gamma, Instances train)throws Exception{
			
		   	
			SMOreg smo=new SMOreg();
		
			
			smo.setOptions(("-C "+c).split(" "));
			RBFKernel kernel=new RBFKernel();
					kernel.setOptions(("-C 250007"+" -G "+ gamma).split(" "));
			
			//PolyKernel kernel=new PolyKernel();			
			//kernel.setOptions(("-C 250007"+" -E "+ gamma).split(" "));
			
			
				
			smo.setKernel(kernel);
			

			smo.buildClassifier(train);
			
			
			//System.out.println("done!");
			//long end=  System.currentTimeMillis();
			//System.out.println("Classifier mined into "+ (end-start)+ " milliseconds!");
			return smo;
			
	 }
	
	 /*
	   * Set-up C and Gamma
	   */
		private 	void gridsearchGC() throws Exception{
				
				
				double []cvalues=new double[19];
				
				for(int i=-9;i<=9;i++)
				cvalues[i+9]=Math.pow(2, i);
				double[] gammaValuese=new double[15];
				for(int i=-11;i<=3;i++)
				gammaValuese[i+11]=Math.pow(2, i);
				//double[] gammaValuese={Math.pow(2, -3),Math.pow(2, -2),Math.pow(2, -1),1, Math.pow(2, 1)};
				
				Instances training =new Instances(this.trainingSet); // in questo modo la procedura stratify
				// cambiera l'ordine nel training locale, lasciando inaltrato l'oridine in this.training
				
				
				int tempNFolds=nFolds;
				if(nFolds>training.numInstances())
					nFolds=training.numInstances();
				
				 training.stratify(nFolds);
				 double bestGamma=0;
				 double bestC=0;
				 double bestAccuracy=Double.POSITIVE_INFINITY;
				 for(double C: cvalues)
				 {
					 double bestAccuracyC=Double.POSITIVE_INFINITY;
					 double bestGammaC=0;
					 double bestCC=0;
				 for(double gamma:gammaValuese){
					 double avgFoldsAccuracy=0;
					 for(int i=0;i<nFolds;i++){
						 Instances trainData = training.trainCV(nFolds,i);
						 Instances testData = training.testCV(nFolds,i);
						 SMOreg svr=learnSMOReg(C, gamma, trainData);
						 
						 Evaluation eval = new Evaluation(testData); 
						 eval.evaluateModel(svr, testData); 
						 double e= eval.errorRate();
						avgFoldsAccuracy+=e;
						 
					 }
					 avgFoldsAccuracy/=nFolds;
					 //System.out.println("Gamma:"+gamma+ " C:"+C+" accuracy:"+avgFoldsAccuracy);
					 if(avgFoldsAccuracy<=bestAccuracyC){
						 bestAccuracyC=avgFoldsAccuracy;
						 bestGammaC=gamma;
						 bestCC=C;
					 }
					 else {
						// break;
						 } // si ferma al primo migliore che trova
				 }
				 if(bestAccuracyC<=bestAccuracy){
					 bestAccuracy=bestAccuracyC;
					 bestGamma=bestGammaC;
					 bestC=bestCC;
				 }
				 else{//break;
					 } // si ferma al primo migliore che trova
				 
				}
				
				C=bestC;
				gamma=bestGamma;
				System.out.println("BestGamma:"+gamma+ " BestC:"+C +" with accuracy "+bestAccuracy);
				nFolds=tempNFolds;
			 }
	public SMORegressor(Instances trainingSet, Instances workingSet, List<Node> workingN) throws Exception {
		super(trainingSet, workingSet, workingN);
		// TODO Auto-generated constructor stub
		
		
		// copy training set
		//Instances training =new Instances(this.trainingSet); // in questo modo la procedura stratify
		
		
		this.myClassifier= new SMOreg();
		
		
		//System.out.println(trainingSet);
		 setConfig();
		 mining();
		 
	//	System.out.println(this.myClassifier);
		
//		trainingSet.sort(trainingSet.attribute(trainingSet.numAttributes()-2));
		mining();
		
	//	System.out.println(this.myClassifier);
	//	this.trainingSet=training;
		

	}

	@Override
	protected void setConfig() throws Exception {
		gridsearchGC();
		myClassifier.setOptions(("-C "+C).split(" "));
		
		// TODO Auto-generated method stub
		//String svmOptions = "-C 1.0 -N 2 -I \"weka.classifiers.functions.supportVector.RegSMOImproved -L 0.0010 -W 1 -P 1.0E-12 -T 0.0010 -V\" -K \"weka.classifiers.functions.supportVector.PolyKernel -C 250007 -E 1.0\""; 
		String svmOptions = "-C 1.0 -N 2 -I \"weka.classifiers.functions.supportVector.RegSMOImproved -L 0.0010 -W 1 -P 1.0E-12 -T 0.0010 -V\"";
			
	 
	 	
			RBFKernel kernel=new RBFKernel();
		kernel.setOptions(("-C 250007"+" -G "+ gamma).split(" "));
		
		((SMOreg)myClassifier).setKernel(kernel);
		
		//smo.setBuildLogisticModels(true);
		
		

	}

}
