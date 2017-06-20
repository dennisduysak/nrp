package Helper;

import Attributes.Employee;
import Attributes.SchedulingPeriod;
import Attributes.Skill;

import java.util.List;
import java.util.Random;

public class SimulatedAnnealing {
    private Solution initialSolution;
    private SchedulingPeriod schedulingPeriod;
    private Helper helper;

    public SimulatedAnnealing(Solution initialSolution, SchedulingPeriod schedulingPeriod) {
        this.initialSolution = initialSolution;
        this.schedulingPeriod = schedulingPeriod;
        helper = new Helper(this.schedulingPeriod,null);
    }

    /**
     * Führt den Simulated Annealin Algorithmus aus
     *
     * @param startingTemperature StartTemperatur
     * @param coolingRate         Abkühlungsrate
     * @return neue "verbesserte" Solution
     * @throws Exception wenn harte Restriktionen nicht erfüllt wurden
     */
    public Solution doSimulatedAnnealing(double startingTemperature, double coolingRate) throws Exception {
        int numberOfIterations = (int) (startingTemperature / coolingRate);
        Solution bestSolution = initialSolution;
        for (int i = 0; i < numberOfIterations; i++) {
            int score = initialSolution.getScore();
            Solution newSolution = mutatedSolution(initialSolution.getRoster());
            //System.out.println("\t" + i + 1 + ".Iteration - Score: " + newSolution.getScore());
            double newScore = newSolution.getScore();
            if (newScore > score) {
                double scoreDiff = score - newScore;
                double temperature = startingTemperature - (i * coolingRate);
                double e = Math.exp(scoreDiff / temperature);
                if (e > Math.random()) {
                    initialSolution = newSolution;
                    if (initialSolution.getScore() < bestSolution.getScore()) {
                        bestSolution = initialSolution;
                    }
                }
            } else {
                initialSolution = newSolution;
                if (initialSolution.getScore() < bestSolution.getScore()) {
                    bestSolution = initialSolution;
                }
            }
        }
        return bestSolution;
    }

    /**
     * Vertauscht zufällig zwei Schichten innerhalb einer Periode unter Berücksichtigung der harten Restriktionen
     *
     * @return neuen zufälligen Dienstplan
     */
    private Solution mutatedSolution(List<int[][]> roster) throws Exception {
        int daySize = roster.size();
        int shiftSize = roster.get(0).length;
        int employeeSize = roster.get(0)[0].length;
        Random random = new Random();
        Constraint constraint = null;

        boolean isConditionsFulfilled = false;
        while (!isConditionsFulfilled) {
            int randomDay = random.nextInt(daySize);
            int employee1 = random.nextInt(employeeSize);
            int employee2 = random.nextInt(employeeSize);
            while (employee1 == employee2) {
                employee1 = random.nextInt(employeeSize);
            }
            int[][] currentDay = roster.get(randomDay);

            int indexOfDhShift = helper.getShiftWithIndices().indexOf("DH");
            List<Employee> employeeList = helper.getEmployeeList();
            boolean isEmployee1HeadNurse = employeeList.get(employee1).getSkills().contains(Skill.HEAD_NURSE);
            boolean isEmployee2HeadNurse = employeeList.get(employee2).getSkills().contains(Skill.HEAD_NURSE);
            //wenn es keine DH Schicht gibt (z.B. toy1), wird indexOfDhShift = -1     UND
            //wenn employee1 eine DH Schicht hat UND employee2 keine Headnurse ist    ODER
            //wenn employee2 eine DH Schicht hat UND employee1 keine Headnurse ist
            if ((indexOfDhShift > -1) && ((currentDay[indexOfDhShift][employee1] == 1 && !isEmployee2HeadNurse) ||
                    (currentDay[indexOfDhShift][employee2] == 1 && !isEmployee1HeadNurse))) {
                isConditionsFulfilled = false;
            } else {
                // Tausche alle Dienste von employee1 mit employee2 für den Tag:randomDay (über alle Schichten)
                for (int i = 0; i < shiftSize; i++) {
                    int temp = currentDay[i][employee1];
                    currentDay[i][employee1] = currentDay[i][employee2];
                    currentDay[i][employee2] = temp;
                }
                roster.set(randomDay, currentDay);
                constraint = new Constraint(schedulingPeriod, roster);

                isConditionsFulfilled = constraint.checkHardConst();
            }
        }
        return new Solution(roster, constraint.calcRosterScore());
    }
}
