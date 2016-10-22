package data.schema;

import java.util.Iterator;

import data.dataInstance.Edge;
import data.dataInstance.Neighbourhood;
import data.dataInstance.Node;

public class AverageAttribute extends CollectiveAttribute {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    
    private double nSize = 0; // Σ(w)
    private double weightedNsize = 0; // Σ(w*yj)  
    
    
    private WeightI wI;

    public AverageAttribute(String name, int index, WeightI wI) {
        super(name, index);
        this.wI = wI;
    }

    @Override
    public double get() {
        if(weightedNsize==0) return 0;

        return weightedNsize/nSize;
    }

    private void update(Neighbourhood nh) {
        
        for(Edge e:nh) {
            
            Node to = e.getTo();
            //  System.out.println("Y"+to.getTarget()+ " PY"+to.getPredictedTarget());
            double w = wI.getWeight(e.getDistance());
            double y = 0;
            if (to.isSample() || to.isWorkingToSample()) {
                y = (double) to.getTarget().getValue();
                nSize = nSize + w;
                weightedNsize = weightedNsize + (w*y); 
            } // IF WE WOULD ALSO CONSIDER UNLABELED NODES: 
            /*else { 
                y = (double) to.getPredictedTarget().getValue();
                nSize = nSize + w;
                weightedNsize = weightedNsize + (w*y); 
            }*/

        }
        
    }
    
    public void compute(Neighbourhood nh) {
        nSize = 0;
        weightedNsize = 0;
        
        update(nh);
    }
    
    
    public void compute(Neighbourhood nh, AverageAttribute a) {
        nSize = a.nSize;
        weightedNsize = a.weightedNsize;
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
                nSize = nSize + w;
                weightedNsize = weightedNsize + (w*y); 
            } // IF WE WOULD ALSO CONSIDER UNLABELED NODES: 
            /*else { 
                y = (double) to.getPredictedTarget().getValue();
                nSize = nSize + w;
                weightedNsize = weightedNsize + (w*y); 
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
