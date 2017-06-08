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

        //Problem
        String[] fileNames = {"toy1",
                "long01",
                "long_hidden01",
                "long_hint01",
                "long_late01",
                "medium01",
                "medium_hidden01",
                "medium_hint01",
                "medium_late01",
                "sprint01",
                "sprint_hidden01",
                "sprint_hint01",
                "sprint_late01"};

        double[] startTemp = {10000, 5000, 1000, 500, 100, 10};
        double[] coolingRate = {10, 5, 1, 0.5, 0.1, 0.01};
        double[] coolingRateFrequency = {1, 0.5, 0.1, 0.05, 0.01, 0.001};
        double[] startTempFrequency = {1000, 500, 100, 50, 10, 1};
        int iterations = 10;

        for (int j = 0; j < 1; j++) {
            for (String fileName : fileNames) {
                XMLParser xmlParser = new XMLParser(fileName);
                SchedulingPeriod schedulingPeriod = xmlParser.parseXML();

                //Initiallösung
                InitialSolution initialSolution = new InitialSolution(schedulingPeriod);
                List<int[][]> initialRoster = initialSolution.createSolution();

                //Initiallösung auf Restriktionen prüfen und bewerten
                Constraint constraint = new Constraint(schedulingPeriod, initialRoster);
                Solution initial = new Solution(initialRoster, constraint.calcRosterScore());

                //Veränderung der CoolingRate bei 10 Iterationen
                for (int i = 0; i < iterations; i++) {
                    int numberOfIterations = (int) (startTemp[j] / coolingRate[j]);
                    double averageScore = getAverageScore(schedulingPeriod, initial, startTemp[j], coolingRate[j]);

                    CSVUtils.writeLine(writer, Arrays.asList(fileName, String.valueOf(startTemp[j]),
                            String.valueOf(coolingRate[j]), String.valueOf(numberOfIterations), String.valueOf(averageScore)));

                    coolingRate[j] -= coolingRateFrequency[j];
                }

                //Veränderung der StartTemperatur bei 10 Iterationen
                startTemp = new double[]{10000, 5000, 1000, 500, 100, 10};
                coolingRate = new double[]{10, 5, 1, 0.5, 0.1, 0.01};
                for (int i = 0; i < iterations; i++) {
                    int numberOfIterations = (int) (startTemp[j] / coolingRate[j]);
                    double averageScore = getAverageScore(schedulingPeriod, initial, startTemp[j], coolingRate[j]);

                    CSVUtils.writeLine(writer, Arrays.asList(fileName, String.valueOf(startTemp[j]),
                            String.valueOf(coolingRate[j]), String.valueOf(numberOfIterations), String.valueOf(averageScore)));

                    startTemp[j] -= startTempFrequency[j];
                }
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