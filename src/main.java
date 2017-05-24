import Attributes.SchedulingPeriod;
import Helper.*;

import java.util.List;

public class main {
    public static void main(String argv[]) throws Exception {
        //Problem
        XMLParser xmlParser = new XMLParser("toy1");
        SchedulingPeriod schedulingPeriod = xmlParser.parseXML();

        //Initiallösung
        InitialSolution initialSolution = new InitialSolution(schedulingPeriod);
        List<int[][]> initialRoster = initialSolution.createSolution();

        //Initiallösung auf Restriktionen prüfen und bewerten
        Constraint constraint = new Constraint(schedulingPeriod, initialRoster);
        Solution initial = new Solution(initialRoster, constraint.calcRosterScore());

        SimulatedAnnealing sa = new SimulatedAnnealing(initial, schedulingPeriod);
        double startingTemperature = 1000;
        double coolingRate = 3;
        Solution betterSolution = sa.doSimulatedAnnealing(startingTemperature, coolingRate);


        System.out.println("\n------------------------------------\n");
        System.out.println("Strafpunkte der Initiallösung: " + initial.getScore());
        System.out.println("Strafpunkte der Verbesserung: " + betterSolution.getScore());
    }
}
