import Attributes.SchedulingPeriod;
import Helper.*;

import java.io.FileWriter;
import java.util.Arrays;
import java.util.List;

public class main {
    public static void main(String argv[]) throws Exception {
        //CSV-Erstellung
        String csvFile = "/Users/dennis/IdeaProjects/nrp/out/output.csv";
        FileWriter writer = new FileWriter(csvFile);
        CSVUtils.writeLine(writer, Arrays.asList("Dateiname",
                "StartTemperatur",
                "CoolingRate",
                "AnzahlDerIterationen",
                "AverageScore"));

        //Problem
        String[] fileNames = {"long01",
          /*      "long_hidden01",
                "long_hint01",
                "long_late01",
                "medium01",
                "medium_hidden01",
                "medium_hint01",
                "medium_late01",
                "sprint01",
                "sprint_hidden01",
                "sprint_hint01",
                "sprint_late01",*/
                "toy1"};
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
            double startTemp = 1000;
            double coolingRate = 3;
            for (int i = 0; i < 10; i++) {
                int numberOfIterations = (int) (startTemp / coolingRate);
                double averageScore = getAverageScore(schedulingPeriod, initial, startTemp, coolingRate);

                CSVUtils.writeLine(writer, Arrays.asList(fileName, String.valueOf(startTemp),
                        String.valueOf(coolingRate), String.valueOf(numberOfIterations), String.valueOf(averageScore)));

                coolingRate += 5;
            }
            CSVUtils.writeLine(writer, Arrays.asList());

            //Veränderung der StartTemperatur bei 10 Iterationen
            startTemp = 1000;
            coolingRate = 3;
            for (int i = 0; i < 10; i++) {
                int numberOfIterations = (int) (startTemp / coolingRate);
                double averageScore = getAverageScore(schedulingPeriod, initial, startTemp, coolingRate);

                CSVUtils.writeLine(writer, Arrays.asList(fileName, String.valueOf(startTemp),
                        String.valueOf(coolingRate), String.valueOf(numberOfIterations), String.valueOf(averageScore)));

                startTemp -= 10;
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
