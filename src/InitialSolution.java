import attributes.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class InitialSolution {
    private static SchedulingPeriod schedulingPeriod;
    private Helper helper;
    private List<int[][]> solutionMatrix = new ArrayList<>();

    InitialSolution(SchedulingPeriod schedulingPeriod) {
        this.schedulingPeriod = schedulingPeriod;
        helper = new Helper(this.schedulingPeriod);
    }

    public List<int[][]> createSolution() {
        List<DayOffWeekCover> requirementList = helper.getRequirement();
        List<Employee> employeeList = helper.getEmployee();
        List<Shift> shiftList = helper.getShift();

        int employeeSize = employeeList.size();
        int shiftTypeSize = shiftList.size();
        int nurseCounter = 0;

        for (int i = 0; i < helper.getDaysInPeriod(); i++) {
            int[][] day = new int[shiftTypeSize][employeeSize];
            List<Integer> req = helper.getReq(i);
            int size = req.size() - 1;

            for (int j = 0; j < 999; j++) {
                if (nurseCounter > employeeSize - 1) {
                    nurseCounter = 0;
                }
                    Employee employee = employeeList.get(nurseCounter); //ich weiß nicht, ob "List" hier richtig ist
                    //nurseCounter soll sagen, an welcher Stelle in der employeeList die nurse rausgesucht werden soll
                    int dhReq = req.get(0);
                if (size == 0 && dhReq == 0) {
                    break;
                }
                    if ((employee.getSkills().contains(Skill.HEAD_NURSE) && dhReq > 0) && (day[0][employee.getId()] != 1)) {
                        // ^ das soll gucken, ob die Nurse den Skill hat und an diesem Tag noch nicht arbeitet!
                        day[0][employee.getId()] = 1;
                        dhReq--;
                        req.set(0, dhReq);
                    } else if (day[size][employee.getId()] != 1) { //überprüfen, dass die Nurse an diesem Tag noch nicht arbeitet
                        int reqShift = req.get(size);
                        if (reqShift > 0) {
                            day[size][employee.getId()] = 1;
                            reqShift--;
                            req.set(size, reqShift);
                        } else {
                            size--;
                            nurseCounter--;
                        }
                    }
                    nurseCounter++;
            }
            solutionMatrix.add(day);
        }
        return solutionMatrix;
    }
}
