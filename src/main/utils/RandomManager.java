package main.utils;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 */
public class RandomManager {

    private static Random random;

    public static Random getRandom() {
        return random;
    }

    public static void setRandomSeed(long randomSeed) {
        RandomManager.random = new Random(randomSeed);
    }
}
