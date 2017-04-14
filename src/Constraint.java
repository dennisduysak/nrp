import attributes.Contract;
import attributes.Employee;
import attributes.SchedulingPeriod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Constraint {
    private SchedulingPeriod schedulingPeriod;
    private Helper helper;
    private List<int[][]> solution;

    public Constraint(SchedulingPeriod schedulingPeriod, List<int[][]> solution) {
        this.schedulingPeriod = schedulingPeriod;
        helper = new Helper(this.schedulingPeriod);
        this.solution = solution;
    }

    /**
     * Prüft die solution auf harte Restriktionen
     *
     * @return true, wenn die harten Restriktionen erfüllt wurden, false sonst
     */
    private boolean checkHardConst() {
        int employeeWorkAtOneDay = 0;
        for (int i = 0; i < solution.size(); i++) {
            List<Integer> requirementsForDay = helper.getRequirementsForDay(i);
            int employeeSize = helper.getEmployeeList().size();
            for (int j = 0; j < employeeSize; j++) {
                int[][] shiftList = solution.get(i);
                for (int k = 0; k < helper.getShiftList().size(); k++) {
                    int demand = 0;
                    for (int l = 0; l < employeeSize; l++) {
                        demand += shiftList[k][l];
                    }
                    if (demand > requirementsForDay.get(k)) {
                        return false;
                    } else if (demand < requirementsForDay.get(k)) {
                        return false;
                    }
                    int[][] d = solution.get(i);
                    employeeWorkAtOneDay += d[k][j];
                }
                if (employeeWorkAtOneDay > 1) {
                    return false;
                }
                employeeWorkAtOneDay = 0;
            }
        }
        return true;
    }

    /**
     * Prüft die weichen Restriktionen
     *
     * @return Strafpunkte
     * @throws Exception    wenn harte Restriktionen nicht erfüllt wurden
     */
    public int checkConstraints() throws Exception {
        int strafpunkte = 0;
        if (!checkHardConst()) {
            throw new Exception("Verstoß gegen harte Restriktion...");
        }

        strafpunkte += checkNumbAssigment();


        return strafpunkte;
    }

    /**
     * Prüft, ob die minimale und maximale Anzahl an Diensten in einer Periode, festgelegt im Vertrag, über-
     * bzw. unterschritten sind.
     *
     * @return Differenz in Tagen bei Über- und Unterschreitung (einen Tag zu vie/wenig => Strafpunkt)
     */
    private int checkNumbAssigment() {

        //List: numbOfShiftInPeriod Number of Workingdays per Nurse per Period
        int employeeSize = helper.getEmployeeList().size();
        int shiftSize = helper.getShiftList().size();
        List<Integer> numbOfShiftInPeriod = new ArrayList<>(Collections.nCopies(employeeSize, 0));
        for (int[][] aSolution : solution) {
            for (int k = 0; k < shiftSize; k++) {
                for (int l = 0; l < employeeSize; l++) {
                    int temp = numbOfShiftInPeriod.get(l) + aSolution[k][l];
                    numbOfShiftInPeriod.set(l, temp);
                }
            }
        }
        //Liste der Mitarbeiter mit der Differenz (Restriktionsverstoß) TODO: evtl. umbenennen
        List<Integer> numbOfDiffDays = new ArrayList<>();
        for (int i = 0; i < numbOfShiftInPeriod.size(); i++) {
            Employee e = (Employee) schedulingPeriod.getEmployees().get(i);
            Contract c = (Contract) schedulingPeriod.getContracts().get(e.getContractId());

            int diffDays = 0;
            int daysOfWork = numbOfShiftInPeriod.get(i);
            int maxDaysOfWork = c.getMaxNumAssignments();
            int minDaysOfWork = c.getMinNumAssignments();
            if(c.getMaxNumAssignments_on() == 1) {
                if(maxDaysOfWork < daysOfWork) {
                    diffDays += daysOfWork - maxDaysOfWork;
                }
            }
            if(c.getMinNumAssignments_on() == 1) {
                if(minDaysOfWork > daysOfWork) {
                    diffDays += minDaysOfWork - daysOfWork;
                }
            }
            numbOfDiffDays.add(diffDays);
        }
        //summierte Liste
        return numbOfDiffDays.stream().mapToInt(Integer::intValue).sum();
    }
}
