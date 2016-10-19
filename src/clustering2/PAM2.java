package clustering2;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import data.dataInstance.Node;
import data.schema.Schema;
import distance.DistanceMatrix;
import sun.util.calendar.Gregorian;





public class PAM2 {


	private List<Node> data;
	private short k;
	private DistanceMatrix d;
	private boolean isSwapping=true;
	//private Schema schema;
	
	private int assignments[];
	
	private long computationTime=0;
	
	private Set<Integer> S=new HashSet<Integer>();
	private List<Boolean> U=new ArrayList<Boolean>();
	
	
	PAM2(List<Node> data, Schema schema, short k, boolean isSwapping){
		this.data=data;
		this.k=k;
		this.isSwapping=isSwapping;
		d =new DistanceMatrix(data,schema);
		
		assignments=new int[data.size()];
		compute();
	}
	
	/*
	 * Step 1: determine the object for which the sum of distances to all other bjects is minimal
	 */
	private int initializeSeed(){
		double sumDistS=Double.MAX_VALUE;
		int rS=-1;
		int r=0;
		for(Node i:data){
			double sumDistColumns=d.sumColumns(r);
			if(sumDistColumns<sumDistS)
			{
				sumDistS=sumDistColumns;
				//s=i;
				rS=r;
			}
			r++;
		}
		return rS;
	}
	/*
	 * Compute dissimilarities D_j between j and the top n cloosest object in S
	 * 
	 * 	 */
	
	private double [] clostestDissimialirty(int rj, Set<Integer> S, int n){
		double Dj[]= new double [n];
		for(int i=0;i<n;i++)
			Dj[i]=Double.MAX_VALUE;
		for(Integer closest:S){
			double currentD=d.get(rj, closest);
			if(currentD<Dj[n-1]){
				// aggiorno 
				Dj[n-1]=currentD;
				// shift elemento D[n-1] a sinistra in modo da mantenere ordinamento
				for(int j=n-2;j>=0;j--){
					if(Dj[j]>Dj[j+1]){
						// swap
						double temp=Dj[j];
						Dj[j]=Dj[j+1];
						Dj[j+1]=temp;
					}
					else break; // terminano shift
				}
			}
		}
		return Dj;
	}
	private void build(){
		
		
		for(int v=0;v<data.size();v++){
			U.add(true);
		}
		
		
		// step 1 : itialize S
		//Node s=null;
		int rS=initializeSeed();
		S.add(rS); //aggiungo s ad S
		U.set(rS, false); //rimuove s da U
		
		//step 2:
		while(S.size()<k){
			int ri=0;
			double maxGi=Double.NEGATIVE_INFINITY;
			int i=-1;
			double gi=0;
			
			for(Boolean ui:U){
				if(ui){ // verifico che sia i in U
					
					// step 3
					int rj=0;
					gi=0;
					for(Boolean uj:U){
						if(rj!=ri && uj)
						{
							// compute dissimilarity D_j between j and the loosest object in S
							double Dj[]=clostestDissimialirty(rj, S,1);
							
							// step 4 if Dj>d(i,j) j will contribute to decisione to select i
							double dji=d.get(rj, ri);
							double Cji=0;
							if((Dj[0]-dji)>0)
								Cji=Dj[0]-dji;
							//step 5: compute gi to evalaute the tota gain obtained by adding i to S
							gi+=Cji;
						}
						rj++;
					} // end for(j ...
					// step 6: choose the object i that maximizes g_i
					if(gi>=maxGi){
						maxGi=gi;
						i=ri;	
					}
				
				}
				ri++;		
			} // end for (ui:U)
			// step 6: S=Su{i}, U=U-{i}
			S.add(i);
			U.set(i, false);
		} // end while
	}
	
	private double computeK(int i, int h, int j){
		double Kjih=0;
		// case 1
		double closest[]=clostestDissimialirty(j, S,2);
		double Dj=closest[0];
		double Ej=closest[1];
		
		double dji=d.get(j, i);
		double djh=d.get(j, h);
		// step a
		if(dji>Dj){
			//case i
			if(djh>=Dj)
				Kjih=0;
			else // case ii
				Kjih=djh-Dj;
			
		}
		else if (dji==Dj){
			
			if(djh<Ej)
				Kjih=djh-Dj;
			else
				Kjih=Ej-Dj;
		}
		else 
			throw new RuntimeException( "Swap exception , step 1 dji<Dj should never occur!");
		return Kjih;
	}
	
	private double computeT(int i, int h){
		double Tih=0.0;
		int j=0;
		for(Boolean uj:U){
			if(uj){
				
				double kjih=computeK(i,h,j);
				Tih+=kjih;
			}
			j++;
		}
		return Tih;
	}
	private boolean swap(){
		
		int selectedI=-1;
		int selectedH=-1;
		double minTih=Double.MAX_VALUE;
		for(Integer i:S)
		{
			int h=0;
			for(Boolean uh:U){
				if(uh){
					//swap i and h
				//	S.remove(i);
				//	S.add(h);
					U.set(h, false);
					U.set(i, true);
					// evaluate swap (i,h)
					double Tih=computeT(i,h);
					// selected i,h that minimizes Tih
					if(Tih<minTih){
						selectedI=i;
						selectedH=h;
						minTih=Tih;
					}
					// restore i h
				//	S.remove(h);
				//	S.add(i);
					U.set(h, true);
					U.set(i, false);
					
				}
				
				h++;
			}
		}
		if(minTih<0) // swap is carried on
		{
			S.remove(selectedI);
			S.add(selectedH);
			U.set(selectedH, false);
			U.set(selectedI, true);
			return true;
		}
		else return false;
	}
	
	
	
	private double computeObjectiveFunction(Set<Integer> S){
		double o=0;
		int i=0;
		for(Boolean ui:U){
			if(ui){
				double minD=Double.MAX_VALUE;
				for(int j:S){
					double dij=d.get(i, j);
					if(dij<minD){
						minD=dij;
					}
				}
				o+=minD;
			}
			i++;
		}
		return o;
	
	}
	
	private boolean swap2(){
		
		double currentO=computeObjectiveFunction(S);
		
		double minO=Double.MAX_VALUE;
		int swapI=-1;
		int swaph=-1;
		for(Integer i:S){
			int h=0;
			for(Boolean uh:U){
				if(uh){
					//swap
					Set<Integer> newS=new HashSet<>();
					newS.addAll(S);
					
					newS.remove(i);
					newS.add(h);
					U.set(h, false);
					U.set(i, true);
					
					double o=computeObjectiveFunction(newS);
					if(o<minO){
						minO=o;
						swapI=i;
						swaph=h;
					}
					U.set(h, true);
					U.set(i, false);
					
				}
				h++;
			}
		}
		if(minO<currentO){
			S.remove(swapI);
			S.add(swaph);
			U.set(swaph, false);
			U.set(swapI, true);
			return true;
		}else return false;
	}
	private void compute(){
		GregorianCalendar start=new GregorianCalendar();
		System.out.print("Building ...");
			
		build();
		System.out.println(S);
		//double silhouette=silhoette();
		//System.out.println( " Silhouette="+silhouette);
		
		if(isSwapping){
			boolean isSwapped=true;
			int numSwap=1;
			while(isSwapped){
				System.out.print("Swapping "+numSwap+ " ...");
				// A comparative study of k-means and PAM Algorithms using Leukemia datasets
				isSwapped=swap();
				System.out.println(S);
				//silhouette=silhoette();
				//System.out.println( " Silhouette="+silhouette);
				
				numSwap++;
				}
		}
		GregorianCalendar end=new GregorianCalendar();
		computationTime=end.getTimeInMillis()-start.getTimeInMillis();
		updateAssigments();
		System.out.println("Silhouette "+silhoette());

/*		int i=0;
		for(int clusterNum : assignments) {
		    System.out.printf("Instance %d -> Cluster %d\n", i, clusterNum);
		    i++;
		}
	*/
	}
	
	
	private void updateAssigments(){
		for(int i=0;i<data.size();i++){
			double minD=Double.MAX_VALUE;
			int minJ=-1;
			int clusterId=0;
			for(int j:S){
				double dij=d.get(i, j);
				if(dij<minD){
					minD=dij;
					minJ=clusterId;
				}
				clusterId++;
			}
			assignments[i]=(minJ);
			
		}
	}
	
	 double silhoette (){
		double s=0;
		//updateAssigments();
		for(int i=0;i<data.size();i++){
			double si=1;
			Map<Integer,Double> mapDist=new HashMap<Integer,Double>();
			for(int c=0;c<k;c++)
				mapDist.put(c,0.0);
			int clusterIdI=assignments[i];
			for(int j=0;j<assignments.length;j++){
				int clusterIdJ=assignments[j];
				mapDist.put(clusterIdJ,mapDist.get(clusterIdJ)+d.get(i, j));
			}
			double ai=mapDist.get(clusterIdI);
			double bi=Double.MAX_VALUE;
			for(Integer c:mapDist.keySet()){
				if(c!=clusterIdI){
					double currentb=mapDist.get(c);
					if(currentb<bi)
						bi=currentb;
				}
			}
			if(ai>bi)
				si=(bi-ai)/ai;
			else if(ai<bi)si=(bi-ai)/bi;
			s+=si;
		}
		s/=data.size();	
	
		return s;
	}
	
	
	void save(String fileName) throws IOException, FileNotFoundException{
		int i=0;
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName)));
		writer.write("#Computation Time:"+computationTime+ " msecs\n");
		writer.write("#Number of clusters:"+k+ "\n");
		for(Node n:data){
			writer.write(n.getId()+","+assignments[i++]+"\n");
		}
		writer.close();
	}
		
		
}


