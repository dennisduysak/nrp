import attributes.SchedulingPeriod;

import java.util.List;

public class main {
    public static void main(String argv[]) throws Exception {
        //Problem
        XMLParser xmlParser = new XMLParser("medium_hint02");
        SchedulingPeriod schedulingPeriod = xmlParser.parseXML();

        //Initiallösung
        InitialSolution initialSolution = new InitialSolution(schedulingPeriod);
        List<int[][]> initial = initialSolution.createSolution();

        //Initiallösung auf Restriktionen prüfen und bewerten
        Constraint constraint = new Constraint(schedulingPeriod, initial);
        int initialScore = constraint.checkConstraints();

        System.out.println("Strafpunkte der Initiallösung: " + initialScore);
    }
}
