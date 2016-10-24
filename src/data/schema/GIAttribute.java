package data.schema;

import java.util.Iterator;

import data.dataInstance.Edge;
import data.dataInstance.Neighbourhood;
import data.dataInstance.Node;

public class GIAttribute extends CollectiveAttribute {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    
    private WeightI wI;
    
    private double sum_wx = 0;
    private double sum_w = 0;
    private double sum_wSquare = 0;
    private double sum_x = 0;
    private double sum_xSquare = 0;
    private double average = 0;

    private double n=0;
    
    public GIAttribute(String name, int index, WeightI wI) {
        super(name, index);
        this.wI = wI;
    }

    @Override
    public double get() {
        
        double S = Math.sqrt((sum_xSquare/n)-(Math.pow(average, 2)));
        double average = sum_x/n;
        
        double numerator = sum_wx - average*sum_w;
        double denominator = S * Math.sqrt((n*sum_wSquare - Math.pow(sum_w, 2))/(n-1));

        return numerator/denominator;

    }

    private void update(Neighbourhood nh) {
        
        n = nh.size();
        
        for(Edge e:nh) {
            Node to = e.getTo();
            
            //  System.out.println("Y"+to.getTarget()+ " PY"+to.getPredictedTarget());
            double w = wI.getWeight(e.getDistance());
            double y = 0;
  
            if (to.isSample() || to.isWorkingToSample()) {
                y = (double) to.getTarget().getValue();
                sum_wx += (w*y);
                sum_w += w;
                sum_wSquare += (Math.pow(w,2));
                sum_x += y;
                sum_xSquare += (Math.pow(y,2));
            } // IF WE WOULD ALSO CONSIDER UNLABELED NODES: 
            /*else { 
                  ...
            }*/

        }
        
    }
    
    public void compute(Neighbourhood nh) {
        sum_wx = 0;
        sum_w = 0;
        sum_wSquare = 0;
        sum_x = 0;
        sum_xSquare = 0;
        average = 0;

        n = nh.size();

        update(nh);
    }
    
    
    public void compute(Neighbourhood nh, GIAttribute a) {
        sum_wx = a.sum_wx;
        sum_w = a.sum_w;
        sum_wSquare = a.sum_wSquare;
        sum_x = a.sum_x;
        sum_xSquare = a.sum_xSquare;
        average = a.average;

        n = nh.size();
        //update(nh);

        Iterator<Edge> it = nh.externalIterator();
        while(it.hasNext()) {
            Edge e = it.next();
            Node to = e.getTo();
            //  System.out.println("Y"+to.getTarget()+ " PY"+to.getPredictedTarget());
            double w = wI.getWeight(e.getDistance());
            double y = 0;
            if (to.isSample() || to.isWorkingToSample()) {
                y = (double) to.getTarget().getValue();
                sum_wx += (w*y);
                sum_w += w;
                sum_wSquare += (Math.pow(w,2));
                sum_x += y;
                sum_xSquare += (Math.pow(y,2));
            } // IF WE WOULD ALSO CONSIDER UNLABELED NODES: 
            /*else { 
            ...
          }*/ 
        }
    }
    
    public String getName(){
        return super.getName();
    }

    
    public String toString() {
        return getName();
    }
}