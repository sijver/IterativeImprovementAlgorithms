package main.core;

import main.utils.RandomManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 */
public class ProblemInstance {

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

    public ProblemSolution generateInitialSolution(){
        List<Integer> initialWay = new ArrayList<Integer>(instanceSize);
        for(int i = 0; i < instanceSize; i++){
            initialWay.add(i);
        }
        Collections.shuffle(initialWay, RandomManager.getRandom());

        return createSolution(initialWay);
    }

    public ProblemSolution createSolution(List<Integer> way) {
        ProblemSolution newSolution = new ProblemSolution(way);

        int wayCostWithoutPenalties = 0;
        int penaltiesNumber = 0;

        for (int i = 0; i < instanceSize - 1; i++) {
            wayCostWithoutPenalties += matrixOfDistances[way.get(i)][way.get(i + 1)];
            if (wayCostWithoutPenalties < windowOpenTime[i + 1]) {
                wayCostWithoutPenalties = windowOpenTime[i + 1];
            } else if (wayCostWithoutPenalties > windowCloseTime[i + 1]) {
                penaltiesNumber++;
            }
        }
        wayCostWithoutPenalties += matrixOfDistances[way.get(instanceSize - 1)][way.get(0)];
        if (wayCostWithoutPenalties < windowOpenTime[0]) {
            wayCostWithoutPenalties = windowOpenTime[0];
        } else if (wayCostWithoutPenalties > windowCloseTime[0]) {
            penaltiesNumber++;
        }

        newSolution.setNumberOfPenalties(penaltiesNumber);
        newSolution.setWayCostWithoutPenalties(wayCostWithoutPenalties);

        return newSolution;
    }

    public ProblemSolution improveSolutionOnce(ProblemSolution initialSolution, Neighbourhood neighbourhood, PivotingRule pivotingRule) {
        ProblemSolution bestSolution = initialSolution;

        switch (neighbourhood) {
            case TRANSPOSE:
                for (int i = 0; i < instanceSize - 1; i++) {
                    List<Integer> newSolutionWay = initialSolution.getWay().subList(0, instanceSize);
                    int var = newSolutionWay.get(i);
                    newSolutionWay.set(i, newSolutionWay.get(i + 1));
                    newSolutionWay.set(i + 1, var);

                    ProblemSolution newSolution = createSolution(newSolutionWay);
                    if (newSolution.getWayCostWithPenalties() < bestSolution.getWayCostWithPenalties()) {
                        bestSolution = newSolution;
                        if (pivotingRule == PivotingRule.FIRST_IMPROVEMENT) {
                            break;
                        }
                    }
                }
                break;
            case EXCHANGE:
                for (int i = 0; i < instanceSize; i++) {
                    for (int j = i + 1; j < instanceSize; j++) {
                        List<Integer> newSolutionWay = initialSolution.getWay().subList(0, instanceSize);
                        int var = newSolutionWay.get(i);
                        newSolutionWay.set(i, newSolutionWay.get(j));
                        newSolutionWay.set(j, var);

                        ProblemSolution newSolution = createSolution(newSolutionWay);
                        if (newSolution.getWayCostWithPenalties() < bestSolution.getWayCostWithPenalties()) {
                            bestSolution = newSolution;
                            if (pivotingRule == PivotingRule.FIRST_IMPROVEMENT) {
                                break;
                            }
                        }
                    }
                }
                break;
            case INSERT:
                for (int i = 0; i < instanceSize; i++) {
                    for (int j = 0; j < instanceSize; j++) {
                        if (i != j && i != j + 1) {
                            List<Integer> newSolutionWay = initialSolution.getWay().subList(0, instanceSize);
                            int var = newSolutionWay.get(i);
                            if (i < j) {
                                for (int k = i; k < j; k++) {
                                    newSolutionWay.set(k, newSolutionWay.get(k + 1));
                                }
                                newSolutionWay.set(j, var);
                            } else {
                                for (int k = i; k > j + 1; k--) {
                                    newSolutionWay.set(k, newSolutionWay.get(k - 1));
                                }
                                newSolutionWay.set(j + 1, var);
                            }

                            ProblemSolution newSolution = createSolution(newSolutionWay);
                            if (newSolution.getWayCostWithPenalties() < bestSolution.getWayCostWithPenalties()) {
                                bestSolution = newSolution;
                                if (pivotingRule == PivotingRule.FIRST_IMPROVEMENT) {
                                    break;
                                }
                            }
                        }
                    }
                }
                break;
        }

        return bestSolution;
    }

    public ProblemSolution improveSolutionToOptimum(ProblemSolution initialSolution, Neighbourhood neighbourhood, PivotingRule pivotingRule) {
        ProblemSolution bestSolution = initialSolution;
        int lastTimeWayCost;

        do {
            lastTimeWayCost = bestSolution.getWayCostWithPenalties();
            bestSolution = improveSolutionOnce(bestSolution, neighbourhood, pivotingRule);
        } while (bestSolution.getWayCostWithPenalties() < lastTimeWayCost);

        return bestSolution;
    }

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

    public ProblemSolution improveSolutionPipedVND(ProblemSolution initialSolution, List<Neighbourhood> algorithmsExecutionOrder) {
        ProblemSolution bestSolution = initialSolution;

        for (Neighbourhood algorithmToExecute : algorithmsExecutionOrder) {
            bestSolution = improveSolutionToOptimum(bestSolution, algorithmToExecute, PivotingRule.FIRST_IMPROVEMENT);
        }

        return bestSolution;
    }
}
