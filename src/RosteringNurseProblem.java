import Attributes.SchedulingPeriod;
import Helper.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class RosteringNurseProblem {
    public static void main(String argv[]) throws Exception {
        //CSV-Erstellung
        FileWriter writer = getFileWriter();

        //Testwerte erstellen
        int[] startTempConst = {/*10000, 5000, 1000,*/ 500, 100, 10};
        int[] startTempFrequency = {/*1000, 500, 100,*/ 50, 10, 1};
        double[] coolingRateConst = {10, 5, 1, 0.5, 0.1};
        double[] coolingRateFrequency = {1, 0.5, 0.1, 0.05, 0.01};
        double iterations = 10;

        int[] startTemp = new int[startTempConst.length];
        double[] coolingRate = new double[coolingRateConst.length];

        String fileName = "long01";
        XMLParser xmlParser = new XMLParser(fileName);
        SchedulingPeriod schedulingPeriod = xmlParser.parseXML();

        for (int i = 0; i < startTempConst.length; i++) {
            for (int j = 0; j < coolingRateConst.length; j++) {
                System.arraycopy(coolingRateConst, 0, coolingRate, 0, coolingRateConst.length);
                System.arraycopy(startTempConst, 0, startTemp, 0, startTempConst.length);

                //Veränderung der CoolingRate bei 10 Iterationen
                for (int k = 0; k < iterations; k++) {
                    System.out.println("A Starttemp: " + startTemp[i] + " CoolinRate: " + coolingRate[j]);
                    int numberOfIterations = (int) (startTemp[i] / coolingRate[j]);
                    double averageScore = getAverageScore(schedulingPeriod, startTemp[i], coolingRate[j]);

                    CSVUtils.writeLine(writer, Arrays.asList(fileName,
                            String.valueOf(startTemp[i]),
                            String.valueOf(coolingRate[j]),
                            String.valueOf(numberOfIterations),
                            String.valueOf(averageScore)));

                    coolingRate[j] = (double) Math.round((coolingRate[j] - coolingRateFrequency[j]) * 1000) / 1000.0;
                }

                //Veränderung der StartTemperatur bei 10 Iterationen
                System.arraycopy(coolingRateConst, 0, coolingRate, 0, coolingRateConst.length);
                for (int k = 0; k < iterations; k++) {
                    System.out.println("B Starttemp: " + startTemp[i] + " CoolingRate: " + coolingRate[j]);
                    int numberOfIterations = (int) (startTemp[i] / coolingRate[j]);
                    double averageScore = getAverageScore(schedulingPeriod, startTemp[i], coolingRate[j]);

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

    private static FileWriter getFileWriter() throws IOException {
        String csvFile = "./out/output.csv";
        FileWriter writer = new FileWriter(csvFile);
        CSVUtils.writeLine(writer, Arrays.asList("Dateiname",
                "StartTemperatur",
                "CoolingRate",
                "IterationsAnzahl",
                "Score"));
        return writer;
    }

    private static Solution getInitialSolution(SchedulingPeriod schedulingPeriod) throws Exception {
        //Initiallösung
        InitialSolution initialSolution = new InitialSolution(schedulingPeriod);
        List<int[][]> initialRoster = initialSolution.createSolution();

        //Initiallösung auf Restriktionen prüfen und bewerten
        Constraint constraint = new Constraint(schedulingPeriod, initialRoster);
        return new Solution(initialRoster, constraint.calcRosterScore());
    }

    private static double getAverageScore(SchedulingPeriod schedulingPeriod, double startingTemperature,
                                          double coolingRate) throws Exception {
        int repeat = 5;
        int[] scoreArray = new int[repeat];
        for (int i = 0; i < repeat; i++) {
            Solution initial = getInitialSolution(schedulingPeriod);
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