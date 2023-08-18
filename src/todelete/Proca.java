package toDelete2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apiacoa.graph.Graph;
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

import weka.core.Debug.Random;

public class Proca {

    public static void main(String[] args) throws IOException {
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
        Graph theGraph = Graph.readEdgeList(new NodeFactory(), graphReader);
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

}
