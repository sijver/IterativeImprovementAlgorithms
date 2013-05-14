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

    /*
    Intended for running of Simulated Annealing algorithm.
        solution - last solution which has been found.
        cpuTimeLimit - algorithm time threshold.
        problemInstance - instance of TSPTW problem.
        bestSolutionEvaluation - needed for measuring of the time when solution not worse than with this value will be found.
        temperature - temperature for SA algorithm.
        bestSolutionFindTime - time when best solution was found (-1 if wasn't found).
     */

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

    /*
    Runs SA algorithm on input data which were set in constructor.
     */
    public void makeSimulatedAnnealing() {
        int stepsWithoutImprovement = 0;

        /*
        Steps on each temperature = n * (n - 1), where n is instance size.
         */
        int instanceSize = solution.getWay().length;
        int stepsOnEachTemperature = (instanceSize - 1) * instanceSize;

        long stepsCounter = 0;

        /*
        Do algorithm and stop if elapsed time is more than limit time or during 5 temperatures solution wasn't changed.
         */
        while (CPUTimeCounter.getCounterResult() < cpuTimeLimit && (stepsWithoutImprovement / stepsOnEachTemperature) < 5) {

            /*
            Get new solution.
             */
            List<ProblemSolution> solutionNeighbours = problemInstance.getAllNeighbours(solution, Neighbourhood.INSERT);
            ProblemSolution newSolution = solutionNeighbours.get(RandomManager.getRandom().nextInt(solutionNeighbours.size()));

            /*
            Replace old solution by the new one if needed conditions are satisfied.
             */
            if (newSolution.getWayCostWithPenalties() < solution.getWayCostWithPenalties() || RandomManager.getRandom().nextDouble() < Math.exp((solution.getWayCostWithPenalties() - newSolution.getWayCostWithPenalties()) / temperature)) {
                solution = newSolution;

                if (solution.getWayCostWithPenalties() <= bestSolutionEvaluation && getBestSolutionFindTime() < 0) {
                    bestSolutionFindTime = CPUTimeCounter.getCounterResult();
                }
                stepsWithoutImprovement = 0;
            } else {
                stepsWithoutImprovement++;
            }

            /*
            Decrease temperature if needed.
             */
            stepsCounter++;
            if (stepsCounter % stepsOnEachTemperature == 0) {
                temperature = temperature * 0.95;
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
}
