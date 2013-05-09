package main.core.exe2;

import main.core.Neighbourhood;
import main.core.ProblemInstance;
import main.core.ProblemSolution;
import main.utils.CPUTimeCounter;
import main.utils.RandomManager;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 */
public class SA {

    private ProblemSolution solution;
    private long cpuTimeLimit;
    private ProblemInstance problemInstance;
    private int bestSolutionEvaluation;

    private double temperature;

    private long bestSolutionFindTime;

    public SA(ProblemSolution solution, long cpuTimeLimit, ProblemInstance problemInstance, int bestSolutionEvaluation) {
        this.solution = solution;
        this.cpuTimeLimit = cpuTimeLimit;
        this.problemInstance = problemInstance;
        this.bestSolutionEvaluation = bestSolutionEvaluation;
        temperature = 2;
        bestSolutionFindTime = -1;
    }


    public void makeSimulatedAnnealing() {
        int stepsWithoutImprovement = 0;

        int instanceSize = solution.getWay().length;
        int stepsOnEachTemperature = (instanceSize - 1) * instanceSize;

        long stepsCounter = 0;

        while (CPUTimeCounter.getCounterResult() < cpuTimeLimit && (stepsWithoutImprovement / stepsOnEachTemperature) < 5) {

            List<ProblemSolution> solutionNeighbours = problemInstance.getAllNeighbours(solution, Neighbourhood.INSERT);
            ProblemSolution newSolution = solutionNeighbours.get(RandomManager.getRandom().nextInt(solutionNeighbours.size()));

            if (newSolution.getWayCostWithPenalties() < solution.getWayCostWithPenalties() || RandomManager.getRandom().nextDouble() < Math.exp((solution.getWayCostWithPenalties() - newSolution.getWayCostWithPenalties()) / temperature)) {
                solution = newSolution;

                if (solution.getWayCostWithPenalties() <= bestSolutionEvaluation && getBestSolutionFindTime() < 0) {
                    bestSolutionFindTime = CPUTimeCounter.getCounterResult();
                }
                stepsWithoutImprovement = 0;
            } else {
                stepsWithoutImprovement++;
            }

            stepsCounter++;
            if (stepsCounter % stepsOnEachTemperature == 0) {
                temperature = temperature * 0.95;
            }
        }

    }

    public long getBestSolutionFindTime() {
        return bestSolutionFindTime;
    }

    public ProblemSolution getSolution() {
        return solution;
    }
}
