import Attributes.SchedulingPeriod;
import Helper.*;

import java.io.FileWriter;
import java.util.Arrays;
import java.util.List;

public class main {
    public static void main(String argv[]) throws Exception {
        //CSV-Erstellung
        String csvFile = "./out/output.csv";
        FileWriter writer = new FileWriter(csvFile);
        CSVUtils.writeLine(writer, Arrays.asList("Dateiname",
                "StartTemperatur",
                "CoolingRate",
                "IterationsAnzahl",
                "Score"));

        String fileName = "long01";
        final int[] startTempConst = {10000, 5000, 1000, 500, 100, 10};
        double[] coolingRateConst = {10, 5, 1, 0.5, 0.1};
        final double iterations = 10;
        double[] coolingRateFrequency = {1, 0.5, 0.1, 0.05, 0.01};
        double[] startTempFrequency = {1000, 500, 100, 50, 10, 1};
        int[] startTemp = new int[startTempConst.length];
        double[] coolingRate = new double[coolingRateConst.length];


        XMLParser xmlParser = new XMLParser(fileName);
        SchedulingPeriod schedulingPeriod = xmlParser.parseXML();

        //Initiallösung
        InitialSolution initialSolution = new InitialSolution(schedulingPeriod);
        List<int[][]> initialRoster = initialSolution.createSolution();

        //Initiallösung auf Restriktionen prüfen und bewerten
        Constraint constraint = new Constraint(schedulingPeriod, initialRoster);
        Solution initial = new Solution(initialRoster, constraint.calcRosterScore());

        for (int i = 0; i < startTempConst.length; i++) {
            for (int j = 0; j < coolingRateConst.length; j++) {
                System.arraycopy(coolingRateConst, 0, coolingRate, 0, coolingRateConst.length);
                System.arraycopy(startTempConst, 0, startTemp, 0, startTempConst.length);

                //Veränderung der CoolingRate bei 10 Iterationen
                for (int k = 0; k < iterations; k++) {
                    System.out.println("A Starttemp: " + startTemp[i] + " CoolinRate: " + coolingRate[j]);
                    int numberOfIterations = (int) (startTemp[i] / coolingRate[j]);
                    double averageScore = getAverageScore(schedulingPeriod, initial, startTemp[i], coolingRate[j]);

                    CSVUtils.writeLine(writer, Arrays.asList(fileName,
                            String.valueOf(startTemp[i]),
                            String.valueOf(coolingRate[j]),
                            String.valueOf(numberOfIterations),
                            String.valueOf(averageScore)));

                    coolingRate[j] = (double) Math.round((coolingRate[j] - coolingRateFrequency[j]) * 1000) / 1000.0;
                }

                //Veränderung der StartTemperatur bei 10 Iterationen
                System.arraycopy(coolingRateConst, 0, coolingRate, 0, coolingRateConst.length);
                System.arraycopy(startTempConst, 0, startTemp, 0, startTempConst.length);
                for (int k = 0; k < iterations; k++) {
                    System.out.println("B Starttemp: " + startTemp[i] + " CoolinRate: " + coolingRate[j]);
                    int numberOfIterations = (int) (startTemp[i] / coolingRate[j]);
                    double averageScore = getAverageScore(schedulingPeriod, initial, startTemp[i], coolingRate[j]);

                    CSVUtils.writeLine(writer, Arrays.asList(fileName,
                            String.valueOf(startTemp[i]),
                            String.valueOf(coolingRate[j]),
                            String.valueOf(numberOfIterations),
                            String.valueOf(averageScore)));

                    startTemp[i] -= startTempFrequency[i];
                }
                System.out.println();
            }
        }
        writer.flush();
        writer.close();
    }

    private static double getAverageScore(SchedulingPeriod schedulingPeriod, Solution initial,
                                          double startingTemperature, double coolingRate) throws Exception {
        int repeat = 5;
        int[] scoreArray = new int[repeat];
        for (int i = 0; i < repeat; i++) {
            SimulatedAnnealing sa = new SimulatedAnnealing(initial, schedulingPeriod);
            Solution betterSolution = sa.doSimulatedAnnealing(startingTemperature, coolingRate);
            scoreArray[i] = betterSolution.getScore();
        }
        int sum = 0;
        for (int anAverageScore : scoreArray) {
            sum += anAverageScore;
        }
        return sum / scoreArray.length;
    }
}