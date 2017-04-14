import attributes.Contract;
import attributes.Employee;
import attributes.SchedulingPeriod;
import attributes.Shift;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Constraint {
    private SchedulingPeriod schedulingPeriod;
    private Helper helper;
    List<int[][]> solution;

    public Constraint(SchedulingPeriod schedulingPeriod, List<int[][]> solution) {
        this.schedulingPeriod = schedulingPeriod;
        helper = new Helper(this.schedulingPeriod);
        this.solution = solution;
    }

    private boolean checkHardConst() {
        int employeeWorkAtOneDay = 0;
        List<Shift> shiftList = helper.getShiftList();
        for (int i = 0; i < solution.size(); i++) {
            List<Integer> req = helper.getReq(i);
            int employeeSize = helper.getEmployeeList().size();
            for (int j = 0; j < employeeSize; j++) {
                int[][] shiftList2 = solution.get(i);
                for (int k = 0; k < shiftList.size(); k++) {
                    int demand = 0;
                    for (int l = 0; l < employeeSize; l++) {
                        demand += shiftList2[k][l];
                    }
                    if (demand > req.get(k)) {
                        return false;
                    } else if (demand < req.get(k)) {
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

    public int checkConstraints() throws Exception {
        int strafpunkte = 0;
        if (!checkHardConst()) {
            throw new Exception("Verstoß gegen harte Restriktion...");
        }

        strafpunkte += checkNumbAssigment();


        return strafpunkte;
    }

    private int checkNumbAssigment() {

        //List: numbOfShiftInPeriod Number of Workingdays per Nurse per Period
        int employeeSize = helper.getEmployeeList().size();
        int shiftSize = helper.getShiftList().size();
        List<Integer> numbOfShiftInPeriod = new ArrayList<>(Collections.nCopies(employeeSize, 0));
        for (int i = 0; i < solution.size(); i++) {
            for (int k = 0; k < shiftSize; k++) {
                for (int l = 0; l < employeeSize; l++) {
                    int[][] employeeWorkDay = solution.get(i);
                    int temp = numbOfShiftInPeriod.get(l) + employeeWorkDay[k][l];
                    numbOfShiftInPeriod.set(l, temp);
                }
            }
        }

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
            return diffDays;
        }
        return 0;
    }
}
