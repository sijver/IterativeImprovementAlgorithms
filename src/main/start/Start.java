package main.start;

import main.core.ProblemInstance;
import main.core.ProblemSolution;
import main.utils.CPUTimeCounter;
import main.utils.RandomManager;
import main.utils.Reader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 */
public class Start {

    public static void main(String[] args) {
        /*
        Parsing of console command.
         */
        String algorithm = "simple";


        /*
        Generating of random seeds with initial seed 1.
         */
        RandomManager.setRandomSeed(1);
        List<Long> randomSeeds = new ArrayList<Long>(100);
        for (int i = 0; i < 100; i++) {
            randomSeeds.add(RandomManager.getRandom().nextLong());
        }
        System.out.println("Seeds used: " + Arrays.toString(randomSeeds.toArray()));

        /*
        Main variables initialization.
         */
        String[] instanceFiles = new String[]{"tsptw_instances/n80w20.001.txt", "tsptw_instances/n80w20.002.txt",
                "tsptw_instances/n80w20.003.txt", "tsptw_instances/n80w20.004.txt", "tsptw_instances/n80w20.005.txt",
                "tsptw_instances/n80w200.001.txt", "tsptw_instances/n80w200.002.txt", "tsptw_instances/n80w200.003.txt",
                "tsptw_instances/n80w200.004.txt", "tsptw_instances/n80w200.005.txt"};
        int[] instanceBest = new int[]{616, 737, 667, 615, 748, 491, 488, 466, 526, 440};
        long totalCPUTime;
        int totalPenalisedRPD;
        int infeasibleNum;
        ProblemInstance problemInstance;
        ProblemSolution problemSolution;
        long[] allCpuTime = new long[100];
        double[] allPenalisedRPD = new double[100];

        /*
        Running of II algorithm 100 times for each instance. Getting of average pRPD and CPU-time.
         */
        for (String instanceFile : instanceFiles) {
            problemInstance = Reader.readProblemInstanceFromFile(instanceFile);
            totalCPUTime = 0;
            totalPenalisedRPD = 0;
            infeasibleNum = 0;
            for (int i = 0; i < randomSeeds.size(); i++) {
                RandomManager.setRandomSeed(randomSeeds.get(i));
                problemSolution = problemInstance.generateInitialSolution();
                CPUTimeCounter.startCounter();
                if (algorithm.equals("simple")) {
                    problemSolution = problemInstance.improveSolutionToOptimum(problemSolution, neighbourhood, pivotingRule);
                } else if (algorithm.equals("VND")) {
                    problemSolution = problemInstance.improveSolutionVND(problemSolution, neighbourhoodsOrder);
                } else {
                    problemSolution = problemInstance.improveSolutionPipedVND(problemSolution, neighbourhoodsOrder);
                }
                /*
                Count CPU-time and pRPD.
                 */
                allCpuTime[i] = CPUTimeCounter.getCounterResult();
                totalCPUTime += allCpuTime[i];
                allPenalisedRPD[i] = problemSolution.getPenalisedRPD(instanceBest[i]);
                totalPenalisedRPD += allPenalisedRPD[i];
                /*
                Count infeasible solutions.
                 */
                if(problemSolution.getNumberOfPenalties() > 0){
                    infeasibleNum++;
                }

                /*
                Additional information output
                 */
//                System.out.println(problemSolution.toString());
//                System.out.println("Cost: " + problemSolution.getWayCostWithPenalties());

            }
            /*
            Statistical output
             */
            System.out.println("Instance: " + instanceFile);
            System.out.println("   Infeasible: " + (double) infeasibleNum / 100);
            System.out.println("   Mean penalised RPD: " + totalPenalisedRPD / 100);
            System.out.println("   Mean CPU time: " + totalCPUTime / 100);
            System.out.println("      All penalised RPDs: " + Arrays.toString(allPenalisedRPD));
            System.out.println("      All cpu-time: " + Arrays.toString(allCpuTime));
        }
    }

    public static void errorExit(){

    }

}
