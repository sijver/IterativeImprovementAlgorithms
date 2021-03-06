package main.start;

import main.core.Neighbourhood;
import main.core.PivotingRule;
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
        String algorithm = "single";
        Neighbourhood neighbourhood = null;
        List<Neighbourhood> neighbourhoodsOrder = new ArrayList<Neighbourhood>();
        PivotingRule pivotingRule = null;
        if (args.length != 2) {
            errorExit();
        } else {
            if (args[1].equals("--transpose")) {
                neighbourhood = Neighbourhood.TRANSPOSE;
            } else if (args[1].equals("--insert")) {
                neighbourhood = Neighbourhood.INSERT;
            } else if (args[1].equals("--exchange")) {
                neighbourhood = Neighbourhood.EXCHANGE;
            } else if (args[1].equalsIgnoreCase("--VND-TEI")) {
                neighbourhoodsOrder = Arrays.asList(Neighbourhood.TRANSPOSE, Neighbourhood.EXCHANGE, Neighbourhood.INSERT);
                algorithm = "VND";
            } else if (args[1].equalsIgnoreCase("--VND-TIE")) {
                neighbourhoodsOrder = Arrays.asList(Neighbourhood.TRANSPOSE, Neighbourhood.INSERT, Neighbourhood.EXCHANGE);
                algorithm = "VND";
            } else if (args[1].equalsIgnoreCase("--pipedVND-TEI")) {
                neighbourhoodsOrder = Arrays.asList(Neighbourhood.TRANSPOSE, Neighbourhood.EXCHANGE, Neighbourhood.INSERT);
                algorithm = "PipedVND";
            } else if (args[1].equalsIgnoreCase("--pipedVND-TIE")) {
                neighbourhoodsOrder = Arrays.asList(Neighbourhood.TRANSPOSE, Neighbourhood.INSERT, Neighbourhood.EXCHANGE);
                algorithm = "PipedVND";
            } else {
                errorExit();
            }
            if (args[0].equals("--best")) {
                pivotingRule = PivotingRule.BEST_IMPROVEMENT;
            } else if (args[0].equals("--first")) {
                pivotingRule = PivotingRule.FIRST_IMPROVEMENT;
            } else {
                errorExit();
            }
        }

        /*
        Generating of random seeds with initial seed 1.
         */
        RandomManager.setRandomSeed(1);
        List<Long> randomSeeds = new ArrayList<Long>(100);
        for (int i = 0; i < 100; i++) {
            randomSeeds.add(RandomManager.getRandom().nextLong());
        }
//        System.out.println("Seeds used: " + Arrays.toString(randomSeeds.toArray()));

        /*
        Neighbourhood and pivoting rule output
         */
        System.out.print("Neighbourhood: ");
        if (neighbourhood != null) {
            System.out.println(neighbourhood);
        } else {
            System.out.print(algorithm + " (");
            for (int i = 0; i < neighbourhoodsOrder.size() - 1; i++) {
                System.out.print(neighbourhoodsOrder.get(i) + "-");
            }
            System.out.println(neighbourhoodsOrder.get(neighbourhoodsOrder.size() - 1) + ")");
        }
        System.out.print("Pivoting rule: ");
        if (neighbourhood != null) {
            System.out.println(pivotingRule);
        } else {
            System.out.println(PivotingRule.FIRST_IMPROVEMENT);
        }

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
        for (int i = 0; i < instanceFiles.length; i++) {
            problemInstance = Reader.readProblemInstanceFromFile(instanceFiles[i]);
            totalCPUTime = 0;
            totalPenalisedRPD = 0;
            infeasibleNum = 0;
            for (int j = 0; j < randomSeeds.size(); j++) {
                RandomManager.setRandomSeed(randomSeeds.get(j));
                problemSolution = problemInstance.generateInitialSolution();
                CPUTimeCounter.startCounter();
                if (algorithm.equals("single")) {
                    problemSolution = problemInstance.improveSolutionToOptimum(problemSolution, neighbourhood, pivotingRule);
                } else if (algorithm.equals("VND")) {
                    problemSolution = problemInstance.improveSolutionVND(problemSolution, neighbourhoodsOrder);
                } else {
                    problemSolution = problemInstance.improveSolutionPipedVND(problemSolution, neighbourhoodsOrder);
                }
                /*
                Count CPU-time and pRPD.
                 */
                allCpuTime[j] = CPUTimeCounter.getCounterResult();
                totalCPUTime += allCpuTime[j];
                allPenalisedRPD[j] = problemSolution.getPenalisedRPD(instanceBest[i]);
                totalPenalisedRPD += allPenalisedRPD[j];
                /*
                Count infeasible solutions.
                 */
                if (problemSolution.getNumberOfPenalties() > 0) {
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
            System.out.println("   Instance: " + instanceFiles[i]);
            System.out.println("      Infeasible: " + infeasibleNum);
            System.out.println("      Mean penalised RPD: " + totalPenalisedRPD / 100);
            System.out.println("      Mean CPU time: " + totalCPUTime / 100);
//            System.out.println("         All penalised RPDs: " + Arrays.toString(allPenalisedRPD));
//            System.out.println("         All cpu-time: " + Arrays.toString(allCpuTime));
        }
    }

    /*
    Error of console command input.
     */
    public static void errorExit() {
        System.out.println("Console command is wrong!");
        System.exit(1);
    }

}
