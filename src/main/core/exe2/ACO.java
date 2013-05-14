package main.core.exe2;

import main.core.Neighbourhood;
import main.core.PivotingRule;
import main.core.ProblemInstance;
import main.core.ProblemSolution;
import main.utils.CPUTimeCounter;
import main.utils.RandomManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 */
public class ACO {

    /*
    Intended for running of Ant colony optimization algorithm.
        pheromoneTrails - the matrix of pheromones (between each pair of nodes in TSPTW instance).
        heuristicValues - the matrix of values of closeness between each pair of nodes in TSPTW (computed as 1/distance).
        instanceSize - size of TSPTW instance.
        solution - last solution which has been found.
        cpuTimeLimit - algorithm time threshold.
        problemInstance - instance of TSPTW problem.
        bestSolutionEvaluation - needed for measuring of the time when solution not worse than with this value will be found.
        bestSolutionFindTime - time when best solution was found (-1 if wasn't found).
        ants - array of 10 ants (internal class - Ant).
        ALPHA = 2 and BETA = 2 - values of powers in formula for computing of probabilities of choosing next vertex by ant.
     */

    private double[][] pheromoneTrails;
    private double[][] heuristicValues;
    private int instanceSize;

    private ProblemSolution solution;
    private long cpuTimeLimit;
    private ProblemInstance problemInstance;
    private int bestSolutionEvaluation;

    private long bestSolutionFindTime;

    private Ant[] ants = new Ant[10];

    private static final int ALPHA = 2;
    private static final int BETA = 2;

    public ACO(ProblemSolution solution, long cpuTimeLimit, ProblemInstance problemInstance, int bestSolutionEvaluation) {
        this.solution = solution;
        this.cpuTimeLimit = cpuTimeLimit;
        this.problemInstance = problemInstance;
        this.bestSolutionEvaluation = bestSolutionEvaluation;
        this.instanceSize = solution.getWay().length;
        bestSolutionFindTime = -1;

        /*
        Pheromones initialization.
         */
        pheromoneTrails = new double[instanceSize][instanceSize];
        for (int i = 0; i < instanceSize; i++) {
            for (int j = i + 1; j < instanceSize; j++) {
                pheromoneTrails[i][j] = 0.001;
                pheromoneTrails[j][i] = pheromoneTrails[i][j];
            }
        }

        /*
        Computing of heuristic values. If distance between nodes is equal to 0 than heuristic value = 10.
         */
        int[][] matrixOfDistances = problemInstance.getMatrixOfDistances();

        heuristicValues = new double[instanceSize][instanceSize];
        for (int i = 0; i < instanceSize; i++) {
            for (int j = 0; j < instanceSize; j++) {
                if (matrixOfDistances[i][j] != 0) {
                    heuristicValues[i][j] = 1.0 / matrixOfDistances[i][j];
                } else {
                    heuristicValues[i][j] = 10;
                }
            }
        }

        /*
        Initialization of array of 10 ants.
         */
        for (int i = 0; i < ants.length; i++) {
            ants[i] = new Ant();
        }
    }

    /*
    Runs ACO algorithm on input data which were set in constructor.
     */
    public void makeACO() {
        /*
        Do algorithm and stop if elapsed time is more than limit time.
         */
        while (CPUTimeCounter.getCounterResult() < cpuTimeLimit) {
            /*
            Each ant generates solution.
             */
            ProblemSolution[] problemSolutions = new ProblemSolution[ants.length];

            for (int i = 0; i < ants.length; i++) {
                problemSolutions[i] = ants[i].generateSolution();
            }

            /*
            Updating of pheromones. Replace old solution by the new one if new one is better.
             */
            for (int i = 0; i < pheromoneTrails.length; i++) {
                for (int j = i + 1; j < pheromoneTrails.length; j++) {
                    pheromoneTrails[i][j] = pheromoneTrails[i][j] * (1.0 - 0.0001);
                    pheromoneTrails[j][i] = pheromoneTrails[i][j];
                }
            }

            for (int i = 0; i < ants.length; i++) {
                if (problemSolutions[i].getWayCostWithPenalties() < solution.getWayCostWithPenalties()) {
                    solution = problemSolutions[i];

                    if (solution.getWayCostWithPenalties() <= bestSolutionEvaluation && getBestSolutionFindTime() < 0) {
                        bestSolutionFindTime = CPUTimeCounter.getCounterResult();
                    }
                }

                int[] solutionWay = problemSolutions[i].getWay();
                for (int j = 0; j < instanceSize - 1; j++) {
                    pheromoneTrails[solutionWay[j]][solutionWay[j + 1]] += 1 / problemSolutions[i].getWayCostWithPenalties();
                    pheromoneTrails[solutionWay[j + 1]][solutionWay[j]] = pheromoneTrails[solutionWay[j]][solutionWay[j + 1]];
                }
                pheromoneTrails[solutionWay[instanceSize - 1]][solutionWay[0]] += 1 / problemSolutions[i].getWayCostWithPenalties();
                pheromoneTrails[solutionWay[0]][solutionWay[instanceSize - 1]] = pheromoneTrails[solutionWay[instanceSize - 1]][solutionWay[0]];
            }
        }
    }

    /*
    Returns time when best solution has been obtained.
     */
    public long getBestSolutionFindTime() {
        return bestSolutionFindTime;
    }

    /*
    Returns solution.
     */
    public ProblemSolution getSolution() {
        return solution;
    }


    private class Ant {

        /*
        Intended for generating of solutions of TSPTW instance for Ant colony optimization algorithm.
         */

        /*
        Generates solution.
         */
        public ProblemSolution generateSolution() {
            /*
            Set of vertices which have not been used yet in solution.
             */
            Set<Integer> notUsedVertices = new HashSet<Integer>();
            for (int i = 1; i < instanceSize; i++) {
                notUsedVertices.add(i);
            }

            List<Integer> solutionWay = new ArrayList<Integer>();
            solutionWay.add(0);

            /*
            Add new vertex to the solution way while all vertices not used.
             */
            while (notUsedVertices.size() > 0) {
                double[] probabilities = new double[notUsedVertices.size()];
                double probabilitiesSum = 0;

                Object[] notUsedVerticesArray = notUsedVertices.toArray();

                /*
                Compute probabilities vector for choosing of next vertex.
                 */
                for (int i = 0; i < notUsedVertices.size(); i++) {
                    probabilities[i] = Math.pow(pheromoneTrails[solutionWay.get(solutionWay.size() - 1)][(Integer) notUsedVerticesArray[i]], ALPHA) * Math.pow(heuristicValues[solutionWay.get(solutionWay.size() - 1)][(Integer) notUsedVerticesArray[i]], BETA);

                    probabilitiesSum += probabilities[i];
                }
                double randomValue = RandomManager.getRandom().nextDouble();

                double currentProbability = 0;

                /*
                Choose vertex randomly using probabilities vector.
                 */
                for (int i = 0; i < notUsedVertices.size(); i++) {
                    probabilities[i] = probabilities[i] / probabilitiesSum;
                    currentProbability += probabilities[i];
                    if (currentProbability >= randomValue) {
                        solutionWay.add((Integer) notUsedVerticesArray[i]);
                        notUsedVertices.remove(solutionWay.get(solutionWay.size() - 1));
                        break;
                    }
                }
            }

            int[] solutionWayArray = new int[instanceSize];
            for (int i = 0; i < instanceSize; i++) {
                solutionWayArray[i] = solutionWay.get(i);
            }

            /*
            Improve generated solution using iterative improvement with first pivoting rule and insert-neighbourhood.
             */
            return problemInstance.improveSolutionToOptimum(problemInstance.createSolution(solutionWayArray), Neighbourhood.INSERT, PivotingRule.FIRST_IMPROVEMENT);
        }

    }

}
