package main.utils;

import main.core.ProblemInstance;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 */
public class Reader {

    /*
    Static reader of TSPTW instance files. Uses the regular expressions for recognizing of separate integers. Returns new TSPTW instance.
     */
    public static ProblemInstance readProblemInstanceFromFile(String filePath) {
        int[][] matrixOfDistances = null;
        int[] windowOpenTime = null;
        int[] windowCloseTime = null;

        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
            String strLine;

            Pattern integerRegExp = Pattern.compile("\\d+");
            Matcher integerRegExpMatcher;

            int numberOfNodes = Integer.parseInt(bufferedReader.readLine());
            matrixOfDistances = new int[numberOfNodes][numberOfNodes];
            windowOpenTime = new int[numberOfNodes];
            windowCloseTime = new int[numberOfNodes];

            int rowReadingNumber = 0;

            while ((strLine = bufferedReader.readLine()) != null) {
                integerRegExpMatcher = integerRegExp.matcher(strLine);

                if (rowReadingNumber < numberOfNodes) {
                    for (int matrixColumn = 0; matrixColumn < numberOfNodes; matrixColumn++) {
                        integerRegExpMatcher.find();
                        matrixOfDistances[rowReadingNumber][matrixColumn] = Integer.parseInt(integerRegExpMatcher.group());
                    }
                } else {
                    if (integerRegExpMatcher.find()) {
                        windowOpenTime[rowReadingNumber - numberOfNodes] = Integer.parseInt(integerRegExpMatcher.group());
                        if (integerRegExpMatcher.find()) {
                            windowCloseTime[rowReadingNumber - numberOfNodes] = Integer.parseInt(integerRegExpMatcher.group());
                        }
                    }
                }
                rowReadingNumber++;
            }
        } catch (Exception e) {
            System.out.println("Instance file reading error!");
            System.exit(1);
        }

        return new ProblemInstance(matrixOfDistances, windowOpenTime, windowCloseTime);
    }

}
