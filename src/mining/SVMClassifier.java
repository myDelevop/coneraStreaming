package mining;

import java.util.List;

import data.dataInstance.Node;
import weka.classifiers.meta.MultiClassClassifier;
import weka.classifiers.functions.supportVector.PolyKernel;
import weka.classifiers.functions.supportVector.RBFKernel;
import weka.classifiers.functions.SMO;
import weka.core.Instance;
import weka.core.Instances;

public class SVMClassifier extends Classifier {
	
	private double C=64; //(int)(this.getTraining().numInstances()/ni);;
	private double gamma=0.125;
	private int nFolds=5; // for evalautions

  /*
   * Set-up C and Gamma
   */
	private 	void gridsearchGC() throws Exception{
			
			
			double []cvalues={1,2,4, 8,16,32,64,128,256};
			double[] gammaValuese={Math.pow(2, -5),Math.pow(2, -4),Math.pow(2, -3),Math.pow(2, -2),Math.pow(2, -1),1,Math.pow(2, 1),Math.pow(2, 2),Math.pow(2, 3),Math.pow(2, 4),Math.pow(2, 5)};
			//double[] gammaValuese={Math.pow(2, -3),Math.pow(2, -2),Math.pow(2, -1),1, Math.pow(2, 1)};
			
			Instances training =new Instances(this.trainingSet); // in questo modo la procedura stratify
			// cambiera l'ordine nel training locale, lasciando inaltrato l'oridine in this.training
			
			
			int tempNFolds=nFolds;
			if(nFolds>training.numInstances())
				nFolds=training.numInstances();
			
			
			if(nFolds==1) // non si può stimare set di parametri con un traing set di un solo esempio
				return;
			 training.stratify(nFolds);
			 double bestGamma=0;
			 double bestC=0;
			 double bestAccuracy=Double.NEGATIVE_INFINITY;
			 for(double C: cvalues){
				 double bestAccuracyC=Double.NEGATIVE_INFINITY;
				 double bestGammaC=0;
				 double bestCC=0;
			 for(double gamma:gammaValuese){
				 double avgFoldsAccuracy=0;
				 for(int i=0;i<nFolds;i++){
					 Instances trainData = training.trainCV(nFolds,i);
					 Instances testData = training.testCV(nFolds,i);
					 weka.classifiers.Classifier svm=learnSVMClassifier((int)C, gamma, trainData);
					 double testAccuracy=0;
					 for (int j=0; j<testData.numInstances(); j++) {
						  	Instance instance = testData.instance(j);
				           
				            double predValue=svm.classifyInstance(instance);
				            double trueValue=instance.classValue();
				            if(predValue==trueValue) testAccuracy++;
					 }
					 testAccuracy/=testData.numInstances();
					 avgFoldsAccuracy+=testAccuracy;				 
					 
				 }
				 avgFoldsAccuracy/=nFolds;
			//	 System.out.println("Gamma:"+gamma+ " C:"+C+" accuracy:"+avgFoldsAccuracy);
				 if(avgFoldsAccuracy>bestAccuracyC){
					 bestAccuracyC=avgFoldsAccuracy;
					 bestGammaC=gamma;
					 bestCC=C;
				 }
				 else {
					 break;
					 } // si ferma al primo migliore che trova
			 }
			 if(bestAccuracyC>bestAccuracy){
				 bestAccuracy=bestAccuracyC;
				 bestGamma=bestGammaC;
				 bestC=bestCC;
			 }
			 else{break;
				 } // si ferma al primo migliore che trova
			 
			}
			
			C=bestC;
			gamma=bestGamma;
			//System.out.println("BestGamma:"+gamma+ " BestC:"+C +" with accuracy "+bestAccuracy);
			nFolds=tempNFolds;
		 }

	 
	 /**
	  * Learn a multiclass SVM 
	  * @param c
	  * @param gamma
	  * @param train
	  * @param isLogistic
	  * @return
	  */
	 private MultiClassClassifier learnSVMClassifier(int c, double gamma, Instances train)throws Exception{
		
		   MultiClassClassifier svm=  new weka.classifiers.meta.MultiClassClassifier(); 
		    svm.setOptions("-M 0 -R 2.0 -S 1".split(" "));
		 
		 	
			weka.classifiers.functions.SMO smo=new SMO();
		
			smo.setOptions(("weka.classifiers.functions.SMO -C "+ c+ " -L 0.001 -P 1.0E-12 -N 0 -V -1 -W 1").split(" ") );
			
			RBFKernel kernel=new RBFKernel();
					kernel.setOptions(("-C 250007"+" -G "+ gamma).split(" "));
			
			//PolyKernel kernel=new PolyKernel();			
			//kernel.setOptions(("-C 250007"+" -E "+ gamma).split(" "));
			
			
				
			smo.setKernel(kernel);
			

		
			//smo.setBuildLogisticModels(true);
/*			smo.setNumFolds(nFolds);
	*/	
			
			svm.setClassifier(smo); 
	
			
		//	System.out.print("Learning SVM with gamma:"+gamma+ " C:"+C + " from "+ train.numInstances()+ " training istances ... ");

		//	long start = System.currentTimeMillis();
		
			svm.buildClassifier(train);
			//System.out.println("done!");
			//long end=  System.currentTimeMillis();
			//System.out.println("Classifier mined into "+ (end-start)+ " milliseconds!");
			return svm;
			
	 }

	public SVMClassifier(Instances trainingSet, Instances workingSet, List<Node> workingNodes) throws Exception{
		super(trainingSet,workingSet,workingNodes);
		
		
	    myClassifier=  new weka.classifiers.meta.MultiClassClassifier(); 
	    setConfig();
	    mining();
	}

	protected void setConfig() throws Exception{
		
		gridsearchGC();
		myClassifier.setOptions("-M 0 -R 2.0 -S 1".split(" "));
	 
	 	
		SMO smo=new SMO();
	
		smo.setOptions(("weka.classifiers.functions.SMO -C "+ C+ " -L 0.001 -P 1.0E-12 -N 0 -V -1 -W 1").split(" ") );
		
		RBFKernel kernel=new RBFKernel();
		kernel.setOptions(("-C 250007"+" -G "+ gamma).split(" "));
	//	PolyKernel kernel=new PolyKernel();			
	//	kernel.setOptions(("-C 250007"+" -E "+ gamma).split(" "));
		
		smo.setKernel(kernel);
		
		//smo.setBuildLogisticModels(true);
		
		((weka.classifiers.meta.MultiClassClassifier)myClassifier).setClassifier(smo); 
		
	}
	
}
