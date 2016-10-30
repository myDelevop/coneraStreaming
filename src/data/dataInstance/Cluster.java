package data.dataInstance;

import java.util.HashSet;

public class Cluster extends HashSet<Node> implements Comparable<Cluster> {
    
    private int id = -1;

    public Cluster(int id) {
        this.setId(id);
    }

    public int getId() {
        return id;
    }

    private void setId(int id) {
        this.id = id;
    }

    public int hashCode() {
        return new Integer(id).hashCode();
    }

    public String toString() {
        return "C" + id  + super.toString();
    }

    @Override
    public int compareTo(Cluster o) {
        // TODO Auto-generated method stub
        int compare = this.size() - o.size();
        if (compare <= 0)
            return -1;
        else
            return 1;
    }
}
