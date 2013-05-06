package main.core.exe2;

import main.core.Neighbourhood;
import main.core.PivotingRule;
import main.core.ProblemInstance;
import main.core.ProblemSolution;
import main.utils.CPUTimeCounter;
import main.utils.RandomManager;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 */
public class ACO {

    private double[][] pheromoneTrails;
    private double[][] heuristicValues;
    private int instanceSize;

    private ProblemSolution solution;
    private long cpuTimeLimit;
    private ProblemInstance problemInstance;
    private int bestSolutionEvaluation;

    private long bestSolutionFindTime;

    private Ant[] ants = new Ant[10];

    public ACO(ProblemSolution solution, long cpuTimeLimit, ProblemInstance problemInstance, int bestSolutionEvaluation) {
        this.solution = solution;
        this.cpuTimeLimit = cpuTimeLimit;
        this.problemInstance = problemInstance;
        this.bestSolutionEvaluation = bestSolutionEvaluation;
        this.instanceSize = solution.getWay().length;
        bestSolutionFindTime = -1;

        pheromoneTrails = new double[instanceSize][instanceSize];
        for (int i = 0; i < instanceSize; i++) {
            for (int j = i + 1; j < instanceSize; j++) {
                pheromoneTrails[i][j] = 0.0001;
                pheromoneTrails[j][i] = pheromoneTrails[i][j];
            }
        }

        int[][] matrixOfDistances = problemInstance.getMatrixOfDistances();

        heuristicValues = new double[instanceSize][instanceSize];
        for (int i = 0; i < instanceSize; i++) {
            for (int j = 0; j < instanceSize; j++) {
                if(matrixOfDistances[i][j] != 0){
                    heuristicValues[i][j] = 1.0 / matrixOfDistances[i][j];
                } else {
                    heuristicValues[i][j] = 10;
                }
            }
        }

        for (int i = 0; i < ants.length; i++) {
            ants[i] = new Ant();
        }
    }

    public void makeACO() {
        while (CPUTimeCounter.getCounterResult() < cpuTimeLimit) {
            ProblemSolution[] problemSolutions = new ProblemSolution[ants.length];

            for (int i = 0; i < ants.length; i++) {
                problemSolutions[i] = ants[i].generateSolution();
            }

            for (int i = 0; i < pheromoneTrails.length; i++) {
                for (int j = i + 1; j < pheromoneTrails.length; j++) {
                    pheromoneTrails[i][j] = pheromoneTrails[i][j] * (1.0 - 0.001);
                    pheromoneTrails[j][i] = pheromoneTrails[i][j];
                }
            }

            for (int i = 0; i < ants.length; i++) {
                if (problemSolutions[i].getWayCostWithPenalties() < solution.getWayCostWithPenalties()) {
                    solution = problemSolutions[i];

                    System.out.println(solution.getWayCostWithPenalties());
                    System.out.println(Arrays.toString(solution.getWay()));
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

    public long getBestSolutionFindTime() {
        return bestSolutionFindTime;
    }

    public ProblemSolution getSolution() {
        return solution;
    }


    private class Ant {

        private int alpha = 2;
        private int beta = 2;

        public ProblemSolution generateSolution() {
            Set<Integer> notUsedVertices = new HashSet<Integer>();
            for (int i = 0; i < instanceSize; i++) {
                notUsedVertices.add(i);
            }

            List<Integer> solutionWay = new ArrayList<Integer>();
            solutionWay.add(RandomManager.getRandom().nextInt(instanceSize));
            notUsedVertices.remove(solutionWay.get(0));

            while (notUsedVertices.size() > 0) {
                double[] probabilities = new double[notUsedVertices.size()];
                double probabilitiesSum = 0;

                Object[] notUsedVerticesArray = notUsedVertices.toArray();

                for (int i = 0; i < notUsedVertices.size(); i++) {
                    probabilities[i] = Math.pow(pheromoneTrails[solutionWay.get(solutionWay.size() - 1)][(Integer) notUsedVerticesArray[i]], alpha) * Math.pow(heuristicValues[solutionWay.get(solutionWay.size() - 1)][(Integer) notUsedVerticesArray[i]], beta);

                    probabilitiesSum += probabilities[i];
                }
                double randomValue = RandomManager.getRandom().nextDouble();

                double currentProbability = 0;

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

            return problemInstance.improveSolutionVND(problemInstance.createSolution(solutionWayArray), Arrays.asList(Neighbourhood.TRANSPOSE, Neighbourhood.INSERT, Neighbourhood.EXCHANGE));
//            return problemInstance.improveSolutionToOptimum(problemInstance.createSolution(solutionWayArray), Neighbourhood.INSERT, PivotingRule.FIRST_IMPROVEMENT);
        }

    }

}
