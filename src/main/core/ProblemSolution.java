package main.core;

import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 */
public class ProblemSolution {

    private List<Integer> way;

    private int numberOfPenalties;

    private static final int PENALTY_COST = 10000;

    private int wayCostWithoutPenalties;

    public ProblemSolution(List<Integer> way) {
        this.way = way;
    }

    public int getWayCostWithoutPenalties() {
        return wayCostWithoutPenalties;
    }

    public int getWayCostWithPenalties() {
        return wayCostWithoutPenalties + numberOfPenalties * PENALTY_COST;
    }

    public List<Integer> getWay() {
        return way;
    }

    public void setNumberOfPenalties(int numberOfPenalties) {
        this.numberOfPenalties = numberOfPenalties;
    }

    public void setWayCostWithoutPenalties(int wayCostWithoutPenalties) {
        this.wayCostWithoutPenalties = wayCostWithoutPenalties;
    }

    public int getNumberOfPenalties() {
        return numberOfPenalties;
    }


    public double getPenalisedRPD(int bestSolutionCost){
        return (double) (getWayCostWithPenalties() - bestSolutionCost) / bestSolutionCost * 100;
    }

    @Override
    public String toString() {
        return "Way: " + Arrays.toString(way.toArray());
    }
}
