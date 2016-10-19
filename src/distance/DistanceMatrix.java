package distance;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;


import data.dataInstance.Node;
import data.dataInstance.Value;
import data.network.Network;
import data.schema.Attribute;
import data.schema.ContinuousAttribute;
import data.schema.Schema;


public class DistanceMatrix implements Serializable{
	protected List<ArrayList<Double>> mat=new ArrayList<ArrayList<Double>>();

	
	
	DistanceMatrix(List<Node> data){
		
		for(int i=0;i<data.size();i++)
			mat.add(new ArrayList<Double>());
		
	
	}
	
	public DistanceMatrix(List<Node> data, Schema schema){
		
		for(int i=0;i<data.size();i++)
			mat.add(new ArrayList<Double>());
		
		initialize(data, schema);
	}
	
	protected void initialize(List<Node> data, Schema schema)
	{
		for(int r=0;r<data.size();r++){
			for (int c=0;c<=r;c++){
				mat.get(r).add(distance(data,schema,r,c));
			}
		}
	}
	
	private double distance(List<Node> data, Schema schema, int r, int c){
		double d=0;
		List<Value> xr=data.get(r).getValues();
		List<Value> xc=data.get(c).getValues();
		Iterator<Attribute> itSchema=schema.getIteratorExplanatory();
		Iterator<Value> itXr=xr.iterator();
		Iterator<Value> itXc=xc.iterator();
		
		while(itSchema.hasNext()){
			Attribute a=itSchema.next();
			Value vr=itXr.next();
			Value vc=itXc.next();
			if(a instanceof ContinuousAttribute){
				double scaledR=((ContinuousAttribute)a).getScaled((Double)(vr.getValue()));
				double scaledC=((ContinuousAttribute)a).getScaled((Double)(vc.getValue()));
				d+=Math.pow(scaledR-scaledC, 2);
			}
			else
			{
				if(!vr.equals(vc))
					d+=1;
			}
		}
		
		return Math.sqrt(d/xr.size());
	}
	
	public double get(int r, int c){
		if(r<c)
			return mat.get(c).get(r);
		else return mat.get(r).get(c);
	}
	
	
	public double sumColumns(int r){
		double s=0;
		for(int c=0;c<mat.size();c++)
			s+=get(r,c);
		return s;
	}
	
	
	void save(String fileName, long learningTime) throws FileNotFoundException, IOException{
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName)));
	//	int rowSize=mat.size();
		writer.write("Learning time,"+learningTime+",msecs\n");
		for(ArrayList<Double> row:mat){
			int j=0;
			for(Double v:row){
				writer.write(""+v);
				if(j<row.size()-1)
					writer.write("\t");
				else
					writer.write("\n");
				j++;
				
			}
			
		}
		
		writer.close();
	
		
	}
	
	public static void main(String args[]) throws Exception{
		
		if(args.length !=2){
			System.out.println("Wrong parameters! Please specify: String baseArff, String config");
			return;
		}
		
		String baseArff=args[0];
		String config = args[1];

		String fileTestArffName="Aco2CInput/"+baseArff;
		String fileConfigFileName="Aco2CInput/"+config;
		//Carico network comprensivo di nodi 
		Network n = new Network(fileTestArffName, fileConfigFileName,true); // aggiorna min e max di attributi numerici in modo da poter fare lo scaling
		Schema schema=n.getGraph().getSchema();
		List<Node>  data=n.getGraph().getWorkingNodes();
		
		long start=System.currentTimeMillis();
		DistanceMatrix d=new DistanceMatrix(data,schema);
		long end =System.currentTimeMillis();
	
		System.out.println("Learning time:"+(end-start)+ " msecs" );
		
		d.save("Aco2CInput/distance/"+baseArff.replace(".", "_")+".csv",(end-start));
		d.serialize("Aco2CInput/distance/"+baseArff.replace(".", "_")+".serialized");
		
	}
	
	
	protected void serialize(String fileName) throws IOException, FileNotFoundException{
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName));
		out.writeObject(this);
		out.close();
		
		
	}
	
	static public DistanceMatrix deserialize(String fileName) throws FileNotFoundException,IOException, ClassNotFoundException {
		// TODO Auto-generated constructor stub
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName));
		DistanceMatrix mat=(DistanceMatrix) (in.readObject());
		in.close();
		return mat;
		
	}
}
