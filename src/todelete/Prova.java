package todelete;

import java.io.BufferedReader;
import java.io.FileReader;

import weka.clusterers.ClusterEvaluation;
import weka.clusterers.EM;
import weka.core.Instance;
import weka.core.Instances;

public class Prova {

    public Prova() {
    }

    
    public static void main(String[] args) {
        EM em = new EM();
        try {
            BufferedReader reader = new BufferedReader(
                    new FileReader("C:/Users/rock_/git/conera/src/todelete/clusterData.arff"));
            Instances data = new Instances(reader);
            
            String[] options = "-t C:/Users/rock_/git/conera/src/todelete/clusterData.arff -I 100 -N -1 -M 1.0E-6 -S 100".split(" "); 
            em.setOptions(options);
            em.buildClusterer(data);
            
            int assignements[] = new int[data.numInstances()];
            
            for(int i=0; i<data.numInstances(); i++) {
                Instance inst = data.instance(i);
                assignements[i] = em.clusterInstance(inst);
            }
            
            System.out.println("\n");
            System.out.println("\n");
            System.out.println(ClusterEvaluation.evaluateClusterer(em, options));
            System.out.println("\n");
            System.out.println("\n");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
