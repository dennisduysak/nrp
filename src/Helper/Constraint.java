package Helper;

import Attributes.Contract;
import Attributes.Employee;
import Attributes.SchedulingPeriod;
import Attributes.Skill;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Constraint {
    private SchedulingPeriod schedulingPeriod;
    private Helper helper;
    private List<int[][]> roster;

    public Constraint(SchedulingPeriod schedulingPeriod, List<int[][]> roster) {
        this.schedulingPeriod = schedulingPeriod;
        helper = new Helper(this.schedulingPeriod);
        this.roster = roster;
    }

    /**
     * Prüft die roster auf harte Restriktionen
     *
     * @return true, wenn die harten Restriktionen erfüllt wurden, false sonst
     */
    public boolean checkHardConst() {
        List<Employee> employeeList = helper.getEmployeeList();

        //Bedarf der Schichten erfüllt?
        for (int i = 0; i < roster.size(); i++) {
            for (int j = 0; j < roster.get(0).length; j++) {
                List<RequirementsForDay> requirementsForDay = helper.getRequirementsForDay(i);
                int demandOnShift = 0;
                for (int k = 0; k < employeeList.size(); k++) {
                    demandOnShift += roster.get(i)[j][k];
                    //Wenn Employee HeadNurse sein sollte, aber keine ist
                    if (requirementsForDay.get(j).getShiftID().equals("DH")) {
                        if (!employeeList.get(k).getSkills().contains(Skill.HEAD_NURSE)) {
                            if (roster.get(i)[j][k] == 1) {
                                return false;
                            }
                        }
                    }
                }
                //Wenn Bedarf der Schicht ungleich der Vorgabe ist
                if (demandOnShift != requirementsForDay.get(j).getDemand()) {
                    return false;
                }
            }
        }
        //Ein Employee nur eine Schicht am Tag?
        for (int i = 0; i < roster.size(); i++) {
            for (int k = 0; k < employeeList.size(); k++) {
                int employeeWorkAtOneDay = 0;
                for (int j = 0; j < roster.get(0).length; j++) {
                    employeeWorkAtOneDay += roster.get(i)[j][k];
                }
                if (employeeWorkAtOneDay > 1) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Prüft die weichen Restriktionen
     *
     * @return Strafpunkte
     * @throws Exception wenn harte Restriktionen nicht erfüllt wurden
     */
    public int calcRosterScore() throws Exception {
        int penaltyPoints = 0;
        if (!checkHardConst()) {
            throw new Exception("Verstoß gegen harte Restriktion...");
        }
        penaltyPoints += checkNumbAssigment();

        return penaltyPoints;
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
        for (int[][] aSolution : roster) {
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
            if (c.getMaxNumAssignments_on() == 1) {
                if (maxDaysOfWork < daysOfWork) {
                    diffDays += (daysOfWork - maxDaysOfWork) * c.getMaxNumAssignments_weight();
                }
            }
            if (c.getMinNumAssignments_on() == 1) {
                if (minDaysOfWork > daysOfWork) {
                    diffDays += (minDaysOfWork - daysOfWork) * c.getMinNumAssignments_weight();
                }
            }
            numbOfDiffDays.add(diffDays);
        }
        //summierte Liste
        return numbOfDiffDays.stream().mapToInt(Integer::intValue).sum();
    }
}
