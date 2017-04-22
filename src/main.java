import attributes.SchedulingPeriod;

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
        int initialScore = constraint.checkConstraints();

        Solution initial = new Solution(initialRoster, initialScore);

        SimulatedAnnealing sa = new SimulatedAnnealing(initial, schedulingPeriod);
        int startingTemperature = 100;
        int coolingRate = 10;
        Solution betterSolution = sa.doAlg(startingTemperature, coolingRate);

        System.out.println("Strafpunkte der Initiallösung: " + initialScore);
    }
}
