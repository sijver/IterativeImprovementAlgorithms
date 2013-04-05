package main.core;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 */
public class ProblemSolution {

    /*
    Intended for describing of the solution for TSPTW.
        way - the way of the traveling salesman. Order of nodes visited.
     */

    private int[] way;

    private int numberOfPenalties;

    private static final int PENALTY_COST = 10000;

    private int wayCostWithoutPenalties;

    public ProblemSolution(int[] way) {
        this.way = way;
    }

    public int getWayCostWithPenalties() {
        return wayCostWithoutPenalties + numberOfPenalties * PENALTY_COST;
    }

    public int[] getWay() {
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


    public double getPenalisedRPD(int bestSolutionCost) {
        return (double) (getWayCostWithPenalties() - bestSolutionCost) / bestSolutionCost * 100;
    }

    @Override
    public String toString() {
        return "Way: " + Arrays.toString(way);
    }
}
