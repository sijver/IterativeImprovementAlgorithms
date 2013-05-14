package main.start;

import main.core.ProblemInstance;
import main.core.ProblemSolution;
import main.core.exe2.ACO;
import main.core.exe2.SA;
import main.utils.CPUTimeCounter;
import main.utils.RandomManager;
import main.utils.Reader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 */
public class StartExe2 {

    public static void main(String[] args) {
        /*
        Generating of random seeds with initial seed 1.
         */
        RandomManager.setRandomSeed(1);
        List<Long> randomSeeds = new ArrayList<Long>(25);
        for (int i = 0; i < 25; i++) {
            randomSeeds.add(RandomManager.getRandom().nextLong());
        }
//        System.out.println("Seeds used: " + Arrays.toString(randomSeeds.toArray()));

        /*
        Main variables initialization.
         */
        String[] instanceFiles = new String[]{"tsptw_instances/n80w20.001.txt", "tsptw_instances/n80w20.002.txt",
                "tsptw_instances/n80w20.003.txt", "tsptw_instances/n80w20.004.txt", "tsptw_instances/n80w20.005.txt",
                "tsptw_instances/n80w200.001.txt", "tsptw_instances/n80w200.002.txt", "tsptw_instances/n80w200.003.txt",
                "tsptw_instances/n80w200.004.txt", "tsptw_instances/n80w200.005.txt"};
        int[] instanceBest = new int[]{616, 737, 667, 615, 748, 491, 488, 466, 526, 440};
        /* Max times (in nanoseconds) of VND algorithm on each instance. */
        long[] cpuTimeLimits = new long[]{392917000, 332040000, 445366000, 365437000, 333393000, 426370000, 448095000, 408531000, 375692000, 460746000};
        /* For last 5 instances percent to add for best solution values. For RTD analysis. */
        int[] percentage = new int[]{30, 35, 50, 30, 45};

        long totalCPUTime;
        int totalPenalisedRPD;
        int infeasibleNum;
        ProblemInstance problemInstance;
        ProblemSolution problemSolution;
        long[] allCpuTime = new long[25];
        double[] allPenalisedRPD = new double[25];

        /*
        Running of SA and ACO algorithm 25 times for each instance. Getting of average pRPD and CPU-time.
         */
        for (int i = 0; i < instanceFiles.length; i++) {
            for (int alg = 0; alg < 2; alg++) {
                if (alg == 0) {
                    System.out.println("Simulated annealing:");
                } else {
                    System.out.println("Ant colony optimization:");
                }

                problemInstance = Reader.readProblemInstanceFromFile(instanceFiles[i]);
                totalCPUTime = 0;
                totalPenalisedRPD = 0;
                infeasibleNum = 0;
                for (int j = 0; j < randomSeeds.size(); j++) {
                    RandomManager.setRandomSeed(randomSeeds.get(j));
                    problemSolution = problemInstance.generateInitialSolution();

                    CPUTimeCounter.startCounter();
                    if (alg == 0) {
                        SA sa = new SA(problemSolution, cpuTimeLimits[i] * 300, problemInstance, instanceBest[i]);
                        sa.makeSimulatedAnnealing();
                        problemSolution = sa.getSolution();
                    } else {
                        ACO aco = new ACO(problemSolution, cpuTimeLimits[i] * 300, problemInstance, instanceBest[i]);
                        aco.makeACO();
                        problemSolution = aco.getSolution();
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
                System.out.println("      Mean penalised RPD: " + (double) totalPenalisedRPD / 25);
                System.out.println("      Mean CPU time: " + (double) totalCPUTime / 25 / Math.pow(10, 9) + " sec");
//            System.out.println("         All penalised RPDs: " + Arrays.toString(allPenalisedRPD));
//            System.out.println("         All cpu-time: " + Arrays.toString(allCpuTime));
            }
        }

        /*
        Running of SA and ACO algorithm 25 times for last 5 instances. Getting of times of obtaining of best solution + certain percent.
         */
        for (int i = 5; i < instanceFiles.length; i++) {

            /*
            Algorithm 0 - SA, 1 - ACO.
             */
            for (int alg = 0; alg < 2; alg++) {
                if (alg == 0) {
                    System.out.println("Simulated annealing:");
                } else {
                    System.out.println("Ant colony optimization:");
                }

                problemInstance = Reader.readProblemInstanceFromFile(instanceFiles[i]);

                /*
                List of times when solutions of needed quality were obtained.
                 */
                List<Double> bestSolutionFindTimeList = new ArrayList<Double>();

                /*
                Get solution find time for each seed.
                 */
                for (int j = 0; j < randomSeeds.size(); j++) {
                    RandomManager.setRandomSeed(randomSeeds.get(j));
                    problemSolution = problemInstance.generateInitialSolution();

                    CPUTimeCounter.startCounter();
                    if (alg == 0) {
                        SA sa = new SA(problemSolution, cpuTimeLimits[i] * 1000, problemInstance, (int) ((100.0 + percentage[i - 5]) / 100 * instanceBest[i]));
                        sa.makeSimulatedAnnealing();
                        if (sa.getBestSolutionFindTime() > 0) {
                            bestSolutionFindTimeList.add((double) sa.getBestSolutionFindTime() / Math.pow(10, 9));
                        }
                    } else {
                        ACO aco = new ACO(problemSolution, cpuTimeLimits[i] * 1000, problemInstance, (int) ((100.0 + percentage[i - 5]) / 100 * instanceBest[i]));
                        aco.makeACO();
                        if (aco.getBestSolutionFindTime() > 0) {
                            bestSolutionFindTimeList.add((double) aco.getBestSolutionFindTime() / Math.pow(10, 9));
                        }
                    }
                }
                Collections.sort(bestSolutionFindTimeList);
                /*
                Output of solution finding times.
                 */
                System.out.println("   Instance: " + instanceFiles[i]);
                System.out.println("      Best solution + " + percentage[i - 5] + "% find times: " + Arrays.toString(bestSolutionFindTimeList.toArray()));
            }
        }
    }

}
