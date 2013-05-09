package main.core;

import main.utils.RandomManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 */
public class ProblemInstance {

    /*
    Intended for describing of TSPTW instance and different methods of II for finding of solutions.
        matrixOfDistances - distances between all the node pairs.
        windowOpenTime - vector of windows' open time values.
        windowCloseTime - vector of windows' close time values.
        instanceSize - number of nodes.
     */

    private int[][] matrixOfDistances;
    private int[] windowOpenTime;
    private int[] windowCloseTime;
    private int instanceSize;

    public ProblemInstance(int[][] matrixOfDistances, int[] windowOpenTime, int[] windowCloseTime) {
        this.matrixOfDistances = matrixOfDistances;
        this.windowOpenTime = windowOpenTime;
        this.windowCloseTime = windowCloseTime;
        instanceSize = matrixOfDistances.length;
    }

    /*
    Generates TSPTW solution using RandomManager.
     */
    public ProblemSolution generateInitialSolution() {
        List<Integer> initialWay = new ArrayList<Integer>(instanceSize);
        for (int i = 1; i < instanceSize; i++) {
            initialWay.add(i);
        }
        Collections.shuffle(initialWay, RandomManager.getRandom());

        int[] initialWayArray = new int[instanceSize];
        for (int i = 1; i < instanceSize; i++) {
            initialWayArray[i] = initialWay.get(i - 1);
        }

        return createSolution(initialWayArray);
    }

    /*
    Solution creation (method-factory). Returns the solution with computed way cost.
     */
    public ProblemSolution createSolution(int[] way) {
        ProblemSolution newSolution = new ProblemSolution(way.clone());

        int wayCostWithoutPenalties = 0;
        int penaltiesNumber = 0;

        for (int i = 0; i < instanceSize - 1; i++) {
            wayCostWithoutPenalties += matrixOfDistances[way[i]][way[i + 1]];
            if (wayCostWithoutPenalties < windowOpenTime[way[i + 1]]) {
                wayCostWithoutPenalties = windowOpenTime[way[i + 1]];
            } else if (wayCostWithoutPenalties > windowCloseTime[way[i + 1]]) {
                penaltiesNumber++;
            }
        }
        wayCostWithoutPenalties += matrixOfDistances[way[instanceSize - 1]][way[0]];
        if (wayCostWithoutPenalties < windowOpenTime[way[0]]) {
            wayCostWithoutPenalties = windowOpenTime[way[0]];
        } else if (wayCostWithoutPenalties > windowCloseTime[way[0]]) {
            penaltiesNumber++;
        }

        newSolution.setNumberOfPenalties(penaltiesNumber);
        newSolution.setWayCostWithoutPenalties(wayCostWithoutPenalties);

        return newSolution;
    }

    /*
    Implementation of the II for any neighbourhood. One iteration.
     */
    public ProblemSolution improveSolutionOnce(ProblemSolution initialSolution, Neighbourhood neighbourhood, PivotingRule pivotingRule) {
        ProblemSolution bestSolution = createSolution(initialSolution.getWay());

        switch (neighbourhood) {
            case TRANSPOSE:
                for (int i = 0; i < instanceSize - 1; i++) {
                    int[] newSolutionWay = initialSolution.getWay().clone();
                    int var = newSolutionWay[i];
                    newSolutionWay[i] = newSolutionWay[i + 1];
                    newSolutionWay[i + 1] = var;

                    ProblemSolution newSolution = createSolution(newSolutionWay);
                    if (newSolution.getWayCostWithPenalties() < bestSolution.getWayCostWithPenalties() && newSolutionWay[0] == 0) {
                        bestSolution = newSolution;
                        if (pivotingRule == PivotingRule.FIRST_IMPROVEMENT) {
                            return bestSolution;
                        }
                    }
                }
                break;
            case EXCHANGE:
                for (int i = 0; i < instanceSize; i++) {
                    for (int j = i + 1; j < instanceSize; j++) {
                        int[] newSolutionWay = initialSolution.getWay().clone();
                        int var = newSolutionWay[i];
                        newSolutionWay[i] = newSolutionWay[j];
                        newSolutionWay[j] = var;

                        ProblemSolution newSolution = createSolution(newSolutionWay);
                        if (newSolution.getWayCostWithPenalties() < bestSolution.getWayCostWithPenalties() && newSolutionWay[0] == 0) {
                            bestSolution = newSolution;
                            if (pivotingRule == PivotingRule.FIRST_IMPROVEMENT) {
                                return bestSolution;
                            }
                        }
                    }
                }
                break;
            case INSERT:
                for (int i = 0; i < instanceSize; i++) {
                    for (int j = 0; j < instanceSize; j++) {
                        if (i != j && i != j + 1) {
                            int[] newSolutionWay = initialSolution.getWay().clone();
                            int var = newSolutionWay[i];
                            if (i < j) {
                                for (int k = i; k < j; k++) {
                                    newSolutionWay[k] = newSolutionWay[k + 1];
                                }
                                newSolutionWay[j] = var;
                            } else {
                                for (int k = i; k > j + 1; k--) {
                                    newSolutionWay[k] = newSolutionWay[k - 1];
                                }
                                newSolutionWay[j + 1] = var;
                            }

                            ProblemSolution newSolution = createSolution(newSolutionWay);
                            if (newSolution.getWayCostWithPenalties() < bestSolution.getWayCostWithPenalties() && newSolutionWay[0] == 0) {
                                bestSolution = newSolution;
                                if (pivotingRule == PivotingRule.FIRST_IMPROVEMENT) {
                                    return bestSolution;
                                }
                            }
                        }
                    }
                }
                break;
        }

        return bestSolution;
    }

    /*
    Implementation of the II for any neighbourhood to the optimum.
     */
    public ProblemSolution improveSolutionToOptimum(ProblemSolution initialSolution, Neighbourhood neighbourhood, PivotingRule pivotingRule) {
        ProblemSolution bestSolution = initialSolution;
        int lastTimeWayCost;

        do {
            lastTimeWayCost = bestSolution.getWayCostWithPenalties();
            bestSolution = improveSolutionOnce(bestSolution, neighbourhood, pivotingRule);
        } while (bestSolution.getWayCostWithPenalties() < lastTimeWayCost);

        return bestSolution;
    }

    /*
    Implementation of the VND algorithm for any neighbourhoods order.
     */
    public ProblemSolution improveSolutionVND(ProblemSolution initialSolution, List<Neighbourhood> algorithmsExecutionOrder) {
        ProblemSolution bestSolution = initialSolution;

        int executableAlgorithmNow = 0;
        int lastTimeWayCost;

        while (executableAlgorithmNow < algorithmsExecutionOrder.size()) {
            lastTimeWayCost = bestSolution.getWayCostWithPenalties();
            bestSolution = improveSolutionOnce(bestSolution, algorithmsExecutionOrder.get(executableAlgorithmNow), PivotingRule.FIRST_IMPROVEMENT);
            if (bestSolution.getWayCostWithPenalties() < lastTimeWayCost) {
                executableAlgorithmNow = 0;
            } else {
                executableAlgorithmNow++;
            }
        }

        return bestSolution;
    }

    /*
    Implementation of the Piped VND algorithm for any neighbourhoods order.
     */
    public ProblemSolution improveSolutionPipedVND(ProblemSolution initialSolution, List<Neighbourhood> algorithmsExecutionOrder) {
        ProblemSolution bestSolution = initialSolution;

        for (Neighbourhood algorithmToExecute : algorithmsExecutionOrder) {
            bestSolution = improveSolutionToOptimum(bestSolution, algorithmToExecute, PivotingRule.FIRST_IMPROVEMENT);
        }

        return bestSolution;
    }

    /*
    Get all neighbours of solution using certain neighbourhood.
     */
    public List<ProblemSolution> getAllNeighbours(ProblemSolution solution, Neighbourhood neighbourhood) {
        List<ProblemSolution> neighbours = new LinkedList<ProblemSolution>();

        switch (neighbourhood) {
            case TRANSPOSE:
                for (int i = 0; i < instanceSize - 1; i++) {
                    int[] newSolutionWay = solution.getWay().clone();
                    int var = newSolutionWay[i];
                    newSolutionWay[i] = newSolutionWay[i + 1];
                    newSolutionWay[i + 1] = var;

                    if (newSolutionWay[0] == 0) {
                        ProblemSolution newSolution = createSolution(newSolutionWay);
                        neighbours.add(newSolution);
                    }
                }
                break;
            case EXCHANGE:
                for (int i = 0; i < instanceSize; i++) {
                    for (int j = i + 1; j < instanceSize; j++) {
                        int[] newSolutionWay = solution.getWay().clone();
                        int var = newSolutionWay[i];
                        newSolutionWay[i] = newSolutionWay[j];
                        newSolutionWay[j] = var;

                        if (newSolutionWay[0] == 0) {
                            ProblemSolution newSolution = createSolution(newSolutionWay);
                            neighbours.add(newSolution);
                        }
                    }
                }
                break;
            case INSERT:
                for (int i = 0; i < instanceSize; i++) {
                    for (int j = 0; j < instanceSize; j++) {
                        if (i != j && i != j + 1) {
                            int[] newSolutionWay = solution.getWay().clone();
                            int var = newSolutionWay[i];
                            if (i < j) {
                                for (int k = i; k < j; k++) {
                                    newSolutionWay[k] = newSolutionWay[k + 1];
                                }
                                newSolutionWay[j] = var;
                            } else {
                                for (int k = i; k > j + 1; k--) {
                                    newSolutionWay[k] = newSolutionWay[k - 1];
                                }
                                newSolutionWay[j + 1] = var;
                            }

                            if (newSolutionWay[0] == 0) {
                                ProblemSolution newSolution = createSolution(newSolutionWay);
                                neighbours.add(newSolution);
                            }
                        }
                    }
                }
                break;
        }
        return neighbours;
    }

    public int[][] getMatrixOfDistances() {
        return matrixOfDistances;
    }
}
