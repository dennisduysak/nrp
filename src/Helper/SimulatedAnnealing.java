package Helper;

import Attributes.SchedulingPeriod;

import java.util.List;
import java.util.Random;

public class SimulatedAnnealing {
    private Solution initialSolution;
    private SchedulingPeriod schedulingPeriod;

    public SimulatedAnnealing(Solution initialSolution, SchedulingPeriod schedulingPeriod) {
        this.initialSolution = initialSolution;
        this.schedulingPeriod = schedulingPeriod;
    }

    public Solution doSimulatedAnnealing(double startingTemperature, double coolingRate) throws Exception {
        int numberOfIterations = (int) (startingTemperature / coolingRate);
        for (int i = 0; i < numberOfIterations; i++) {
            int score = initialSolution.getScore();
            Solution newSolution = mutatedSolution(initialSolution.getRoster());
            double newScore = newSolution.getScore();
            if (newScore < score) {
                double e = Math.exp((newScore - score) / i);
                if (e > Math.random()) {
                    initialSolution = newSolution;
                }
            } else {
                initialSolution = newSolution;
            }
        }
        return initialSolution;
    }

    /**
     * Vertauscht zufällig zwei Schichten innerhalb einer Periode
     *
     * @return neuen zufälligen Dienstplan
     */
    private Solution mutatedSolution(List<int[][]> oldRoster) throws Exception {
        int daySize = oldRoster.size();
        int shiftSize = oldRoster.get(0).length;
        int employeeSize = oldRoster.get(0)[0].length;
        Random random = new Random();

        List<int[][]> newRoster = oldRoster;
        Constraint constraint = new Constraint(schedulingPeriod, newRoster);

        while (!constraint.checkHardConst()) {
            int randomDay1 = random.nextInt(daySize);
            int randomDay2 = random.nextInt(daySize);
            int shift1 = random.nextInt(shiftSize);
            int shift2 = random.nextInt(shiftSize);
            int employee1 = random.nextInt(employeeSize);
            int employee2 = random.nextInt(employeeSize);

            while (randomDay1 == randomDay2 && shift1 == shift2) {
                shift1 = random.nextInt(shiftSize);
            }

            // Tausche für Tag 1 Schicht 1 Einsatz von Nurse 1 mit Nurse 2
            int[][] i = newRoster.get(randomDay1);
            i[shift1][employee1] = newRoster.get(randomDay1)[shift1][employee2];
            i[shift1][employee2] = newRoster.get(randomDay1)[shift1][employee1];
            newRoster.set(randomDay1, i);

            // Tausche für Tag 2 Schicht 2 Einsatz von Nurse 1 mit Nurse 2
            i = newRoster.get(randomDay2);
            i[shift2][employee1] = newRoster.get(randomDay2)[shift2][employee2];
            i[shift2][employee2] = newRoster.get(randomDay2)[shift2][employee1];
            newRoster.set(randomDay2, i);

            constraint = new Constraint(schedulingPeriod, newRoster);
        }

        return new Solution(newRoster, constraint.calcRosterScore());
    }
}
