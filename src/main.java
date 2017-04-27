import Helper.*;
import Attributes.SchedulingPeriod;

import java.util.List;

public class main {
    public static void main(String argv[]) throws Exception {
        //Problem
        XMLParser xmlParser = new XMLParser("long01");
        SchedulingPeriod schedulingPeriod = xmlParser.parseXML();

        //Initiallösung
        InitialSolution initialSolution = new InitialSolution(schedulingPeriod);
        List<int[][]> initialRoster = initialSolution.createSolution();

        //Initiallösung auf Restriktionen prüfen und bewerten
        Constraint constraint = new Constraint(schedulingPeriod, initialRoster);
        int initialScore = constraint.calcRosterScore();

        Solution initial = new Solution(initialRoster, initialScore);

        SimulatedAnnealing sa = new SimulatedAnnealing(initial, schedulingPeriod);
        double startingTemperature = 5;
        double coolingRate = 1.3;
        Solution betterSolution = sa.doAlg(startingTemperature, coolingRate);

        System.out.println("Strafpunkte der Initiallösung: " + initialScore);
        System.out.println("Strafpunkte der Verbesserung: " + betterSolution.getScore());
    }
}
