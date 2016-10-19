package data.schema;

import java.util.LinkedList;
import java.util.List;

public class EWDiscretization implements Discretization {

	private double min;
	private double max;
	public EWDiscretization(double min, double max) {
		// TODO Auto-generated constructor stub
		this.min=min;
		this.max=max;
	}
	@Override
	public List<Double> compute(int discretizationN) {
		// TODO Auto-generated method stub
		
	
			List<Double> listSplitPoints =new LinkedList<Double>();
			double step=(double)(max-min)/discretizationN;
			listSplitPoints.add(Double.NEGATIVE_INFINITY);
			//listSplitPoints.add(min);
			double current=min;
			for(int i=1;i<discretizationN ;i++)
			{
			
				current+=step;
				listSplitPoints.add(current);
				
			}
			
			listSplitPoints.add(Double.POSITIVE_INFINITY);
			return listSplitPoints;
		}
		
	

}
