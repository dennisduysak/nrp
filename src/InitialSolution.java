import attributes.Employee;
import attributes.SchedulingPeriod;
import attributes.Shift;
import attributes.Skill;

import java.util.ArrayList;
import java.util.List;

public class InitialSolution {
    private static SchedulingPeriod schedulingPeriod;
    private Helper helper;

    InitialSolution(SchedulingPeriod schedulingPeriod) {
        this.schedulingPeriod = schedulingPeriod;
        helper = new Helper(this.schedulingPeriod);
    }

    /**
     * Entwirft eine Initiallösung, bei dem nur harte Restriktionen berücksichtigt werden.
     *
     * @return solutionMatrix   Liste von zweidimensionalen int-Arrays (pro Tag einen Eintrag in der Liste)
     * int-Array: Tabelle der Schichten pro Mitarbeiter (1, arbeitet in Schicht x, 0 nicht)
     */
    public List<int[][]> createSolution() {
        List<int[][]> solutionMatrix = new ArrayList<>();
        List<Employee> employeeList = helper.getEmployeeList();
        List<Shift> shiftList = helper.getShiftList();

        int employeeSize = employeeList.size();
        int shiftTypeSize = shiftList.size();
        int nurseCounter = 0;

        for (int i = 0; i < helper.getDaysInPeriod(); i++) {
            int[][] day = new int[shiftTypeSize][employeeSize];
            List<Integer> requirementsForDay = helper.getRequirementsForDay(i);
            int size = requirementsForDay.size() - 1;

            for (int j = 0; j < 999; j++) {
                if (nurseCounter > employeeSize - 1) {
                    nurseCounter = 0;
                }
                Employee employee = employeeList.get(nurseCounter);
                int dhReq = requirementsForDay.get(0);
                if (size == 0 && dhReq == 0) {
                    break;
                }
                //Wenn Oberschwester und der Bedarf an Oberschwestern an Tag x nicht gedeckt ist => Zuteilung
                if ((employee.getSkills().contains(Skill.HEAD_NURSE) && dhReq > 0) && (day[0][employee.getId()] != 1)) {
                    day[0][employee.getId()] = 1;
                    dhReq--;
                    requirementsForDay.set(0, dhReq);
                    //Wenn keine Oberschwester => Zuteilung um den Bedarf zu decken
                } else if (day[size][employee.getId()] != 1) {
                    int reqShift = requirementsForDay.get(size);
                    if (reqShift > 0) {
                        day[size][employee.getId()] = 1;
                        reqShift--;
                        requirementsForDay.set(size, reqShift);
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
