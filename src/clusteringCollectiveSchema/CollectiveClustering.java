package clusteringCollectiveSchema;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apiacoa.graph.HierarchicalGraphPartition;
import org.apiacoa.graph.NodeFactory;
import org.apiacoa.graph.clustering.CallableGraphClustering;
import org.apiacoa.graph.clustering.CoarseningLevel;
import org.apiacoa.graph.clustering.GraphClusteringMethod;
import org.apiacoa.graph.clustering.GraphClusteringParameters;
import org.apiacoa.graph.clustering.RecursiveClustering;
import org.apiacoa.graph.clustering.RecursiveClusteringTermination;
import org.apiacoa.graph.random.RandomGraphGenerator;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import data.dataInstance.Cluster;
import data.dataInstance.Edge;
import data.dataInstance.Graph;
import data.dataInstance.Node;
import weka.core.Debug.Random;

public class CollectiveClustering extends Clustering {
    
    String inputEdges = "apicoa/edges.txt";
    String outputClustering = "apicoa/clusters.txt";
    String modularites = "apicoa/modularites.txt";


    public CollectiveClustering(Graph graph) {
        super(graph);
        
        try {
            createClusters();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        System.out.println("estimated k " + k);

        clusters = new Cluster[k];
        for(int c=0;c<clusters.length;c++) {
            clusters[c]=new Cluster(c);
        }
        
        BufferedReader br = null;
        FileReader fr = null;
        try {
            fr = new FileReader(outputClustering);
            br = new BufferedReader(fr);
           
            String line = br.readLine();
            while (line != null){
                String[] lineElements = line.split("\\s+");
                Integer id = Integer.parseInt(lineElements[0]);
                Node n = graph.getNodeByID(id);
                int cluster = Integer.parseInt(lineElements[1]);
                n.setCollectiveCluster(clusters[cluster]);
                clusters[cluster].add(n);
                
                line = br.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
                if (fr != null)
                    fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
    }

    private void createClusters() throws Exception {
        

        createEdgesFile(inputEdges);

        

        String[] args = ("-graph " + inputEdges + " -mod " + modularites + " -part " 
                + outputClustering + " -random 100 -recursive").split(" ");
        GraphClusteringParameters params = new GraphClusteringParameters();
        CmdLineParser parser = new CmdLineParser(params);
        try {
            parser.parseArgument(args);
        }
        catch (CmdLineException e) {
            System.err.println(e.getMessage());
            System.err.println("java -jar clustering.jar [options...] arguments...");
            parser.printUsage(System.err);
            return;
        }
        if (params.nbRandom > 0 && params.outModularities == null) {
            System.err.println("Missing modularities file needed to save significance analysis results");
            System.err.println("java -jar clustering.jar [options...] arguments...");
            parser.printUsage(System.err);
            return;
        }
        BufferedReader graphReader = params.createGraphReader();
        if (graphReader == null) {
            System.err.println("Not input graph specified");
            parser.printUsage(System.err);
            return;
        }
        org.apiacoa.graph.Graph theGraph = org.apiacoa.graph.Graph
                .readEdgeList(new NodeFactory(), graphReader);
        graphReader.close();
        if (theGraph.asUndirected()) {
            System.err.println("Warning: the original graph appears to be directed");
        }
        if (params.verbosity > 1) {
            System.out.println("#Graph loaded");
        }
        PrintWriter out = params.createPartitionWriter();
        GraphClusteringMethod gcm = params.createGraphClusteringMethod();
        CoarseningLevel cl = gcm.doCluster(theGraph);
        if (params.isRecursive) {
            RecursiveClustering rc = new RecursiveClustering();
            RecursiveClusteringTermination rct = params.createRCT(gcm, new Random());
            HierarchicalGraphPartition hgp = rc.doRecursiveCluster(gcm, cl, rct);
            hgp.renumber();
            hgp.writePartition(theGraph, out);
            this.k = hgp.nbClusters();
        } else {
            cl.getPartition().writePartition(theGraph, out);
        }
        params.closePartitionWriter();
        if (params.outModularities != null) {
            PrintWriter outMod = params.createModularityWriter();
            outMod.println(String.valueOf(cl.modularity()) + "\t" + "Original");
            if (params.nbRandom > 0) {
                if (params.nbThreads > 1) {
                    ExecutorService es = Executors.newFixedThreadPool(params.nbThreads);
                    CallableGraphClustering.graph = theGraph;
                    CallableGraphClustering.params = params;
                    ArrayList<CallableGraphClustering> task = new ArrayList<CallableGraphClustering>(params.nbRandom);
                    int i = 0;
                    while (i < params.nbRandom) {
                        task.add(new CallableGraphClustering(outMod, i));
                        ++i;
                    }
                    try {
                        es.invokeAll(task);
                    }
                    catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    es.shutdown();
                } else {
                    RandomGraphGenerator rgg = params.createRandomGraphGenerator(new Random(), theGraph);
                    int i = 0;
                    while (i < params.nbRandom) {
                        if (params.verbosity > 0) {
                            System.out.println("# random graph n\u00b0" + i);
                        }
                        CoarseningLevel clRandom = gcm.doCluster(rgg.nextGraph());
                        outMod.println(String.valueOf(clRandom.modularity()) + "\t" + "Random");
                        outMod.flush();
                        ++i;
                    }
                }
            }
            outMod.close();
        }

    }
    
    private void createEdgesFile(String inputEdges) {        
        // create input file for apicoa
        Map<Node, Set<Edge>> graphStructure = graph.getGraphStructure();

        File edgesFile = new File(inputEdges);
        FileOutputStream fos=null;
        BufferedWriter bw=null;
        try {
            fos = new FileOutputStream(edgesFile);
            bw = new BufferedWriter(new OutputStreamWriter(fos));
            
            int idFrom;
            int idTo;
            float edge;
            
            bw.flush();

            for (Node n:graphStructure.keySet()) {
                idFrom = (int) n.getId().getValue();
                for(Edge e:graphStructure.get(n)) {
                    idTo = (int) e.getTo().getId().getValue();
                    edge = e.getDistance();
                    bw.flush();
                    bw.write(idFrom + " " + idTo + " " + edge);
                    bw.newLine();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(bw!=null)
                    bw.close();
                if(fos!=null)
                    fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            } 
        }

    }
}

