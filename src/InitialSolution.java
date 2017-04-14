import attributes.*;

import java.util.ArrayList;
import java.util.List;

public class InitialSolution {
    private static SchedulingPeriod schedulingPeriod;
    private Helper helper;

    InitialSolution(SchedulingPeriod schedulingPeriod) {
        this.schedulingPeriod = schedulingPeriod;
        helper = new Helper(this.schedulingPeriod);
    }

    public List<int[][]> createSolution() {
        List<int[][]> solutionMatrix = new ArrayList<>();
        List<Employee> employeeList = helper.getEmployeeList();
        List<Shift> shiftList = helper.getShiftList();

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
                    Employee employee = employeeList.get(nurseCounter);
                    int dhReq = req.get(0);
                if (size == 0 && dhReq == 0) {
                    break;
                }
                    //Wenn Oberschwester und der Bedarf an Oberschwestern an Tag x nicht gedeckt ist => Zuteilung
                    if ((employee.getSkills().contains(Skill.HEAD_NURSE) && dhReq > 0) && (day[0][employee.getId()] != 1)) {
                        day[0][employee.getId()] = 1;
                        dhReq--;
                        req.set(0, dhReq);
                     //Wenn keine Oberschwester => Zuteilung um den Bedarf zu decken
                    } else if (day[size][employee.getId()] != 1) {
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
