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

    public GIAttribute(String name, int index, WeightI wI) {
        super(name, index);
        this.wI = wI;
    }

    @Override
    public double get() {
        return 0d;
        //  if(weightedNsize==0) return 0;

        //        return weightedNsize/nSize;
    }

    private void update(Neighbourhood nh) {
        
        for(Edge e:nh) {

        }
        
    }
    
    public void compute(Neighbourhood nh) {

    }
    
    
    public void compute(Neighbourhood nh, AverageAttribute a) {

    }
    
    public String getName(){
        return super.getName();
    }

    
    public String toString() {
        return getName();
    }
}