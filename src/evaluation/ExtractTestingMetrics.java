package evaluation;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class ExtractTestingMetrics {
	
	
	
	private List<Double> extract(int columnNumber, String fileName) throws IOException{
		List<Double> results=new ArrayList<Double>();
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String line=br.readLine();
		line=br.readLine();
		while(line!=null){
			String s[]=line.split(";");
			double v=new Double(s[columnNumber].replace(",","."));
			results.add(v);
			line=br.readLine();
			
			
		}
		br.close();
		return results;
	}

	
	public static void main(String args[]) throws IOException{
		String path="D:/hardisk/Documenti/Papers/Annalisa/Articoli/Da accettare/ActiveSemisupervisedCollectiveRegrssion/software/AC2R/esperimento/Aco2COutput/";
		String systems[]={"CO","COCR","SELF"};
		int trainSize=1;
		int sampleSize=1;
		String expName="movies2.arff";
		
		// "D:/hardisk/Documenti/Papers/Annalisa/Articoli/Da accettare/ActiveSemisupervisedCollectiveRegrssion/software/AC2R/esperimento/Aco2COutput/" "CO;COCR;SELF" 1 5 "movies2.arff" 3 10
		ExtractTestingMetrics m=new ExtractTestingMetrics();
		path="Aco2COutput/"; //(args[0]);
		systems=(args[0].split("_"));
		trainSize=((new Integer(args[1])));
		sampleSize=(new Integer(args[2]));
		expName=(args[3]);
	//	int a=new Integer(args[5]);
	//	int it=new Integer(args[6]);
		
		String columns[]={"Iteration","elapsedTime","rmse","mae","r2","rae","rse"};
	//	for(int column=1;column<columns.length;column++){
		for(int column=1;column<=2;column++){
			
			BufferedWriter writerMean = new BufferedWriter(new OutputStreamWriter(new FileOutputStream((path+expName+columns[column].toUpperCase()).replace(".", "_")+"TestMean.csv"), "utf-8"));
			BufferedWriter writerStdev = new BufferedWriter(new OutputStreamWriter(new FileOutputStream((path+expName+columns[column].toUpperCase()).replace(".","_")+"TestStdev.csv"), "utf-8"));
			
			for(String system:systems){
				List<Double> rSystemSum=new ArrayList<Double>();
				List<Double> rSystemSum2=new ArrayList<Double>();
			/*	
				for(int i=0;i<it;i++){
					rSystemSum.add(0.0);
					rSystemSum2.add(0.0);
				}*/
				int c=0;
				for(int train=1;train<=trainSize;train++){
					for(int sample=1;sample<=sampleSize;sample++)
					{
							//movies2.arffTrain_1.arffSample3.txtSELFa3_it10
							String fileName=expName+"Train_"+train+".arffSample"+sample+".txt"+system; //+"a"+a+"_it"+it;
							fileName=fileName+"/"+system+"Sample"+sample;
							fileName=path+fileName;
							fileName=fileName.replace(".", "_")+".csv";
							List<Double> rSample=m.extract(column, fileName);
							for(int i=0;i<rSample.size();i++){
								try{
									rSystemSum.set(i, rSystemSum.get(i)+rSample.get(i));
									rSystemSum2.set(i, rSystemSum2.get(i)+Math.pow(rSample.get(i),2));
								}
								catch(Exception e){
									rSystemSum.add(i, rSample.get(i));
									rSystemSum2.add(i, Math.pow(rSample.get(i),2));
								}
							}
							c++;
					}
				}
				for(int i=0;i<rSystemSum.size();i++){
					rSystemSum.set(i, rSystemSum.get(i)/c); // media
				}
	
				List<Double> rSystemDev=new ArrayList<Double>();
				for(int i=0;i<rSystemSum.size();i++){
					double stdev=rSystemSum2.get(i)-c*Math.pow(rSystemSum.get(i), 2);
					stdev/=c;
					stdev=Math.sqrt(stdev);
					rSystemDev.add(stdev);
				}
				
				// saving mean stdev per system, column
				writerMean.write(system+"Mean");
				for(double d:rSystemSum)
					writerMean.write((";"+d).replace(".", ","));
				writerMean.write("\n");
				writerStdev.write(system+"StDev");
				for(double d:rSystemDev)
					writerStdev.write((";"+d).replace(".", ","));
				writerStdev.write("\n");
			}
			writerMean.close();
			writerStdev.close();
		}
	}


	
}
