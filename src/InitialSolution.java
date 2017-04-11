import attributes.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class InitialSolution {
    private static SchedulingPeriod schedulingPeriod;
    private List<int[][]> solutionMatrix = new ArrayList<>();

    InitialSolution(SchedulingPeriod schedulingPeriod) {
        this.schedulingPeriod = schedulingPeriod;
    }

    public List<int[][]> createSolution() {
        //BedarfsListe
        List<DayOffWeekCover> requirementList = schedulingPeriod.getCoverRequirements().stream()
                .map(object -> (DayOffWeekCover) object)
                .collect(Collectors.toList());
        //MitarbeiterListe
        List<Employee> employeeList = schedulingPeriod.getEmployees().stream()
                .map(object -> (Employee) object)
                .collect(Collectors.toList());
        //Liste der Schichten
        List<Shift> shiftList = schedulingPeriod.getShiftTypes().stream()
                .map(object -> (Shift) object)
                .collect(Collectors.toList());

        int employeeSize = schedulingPeriod.getEmployees().size();
        int shiftTypeSize = schedulingPeriod.getShiftTypes().size();
        long diff = schedulingPeriod.getEndDate().getTime() - schedulingPeriod.getStartDate().getTime();
        int daysInPeriod = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + 1;

        int nurseCounter = 0;
        //alle Tage
        for (int i = 0; i < daysInPeriod; i++) {









            int[][] day = new int[shiftTypeSize][employeeSize];
            List<String> ids = new ArrayList<>();
            List<Integer> req = new ArrayList<>();
            int dhIndex = 99;
            for (int j = 0; j < shiftList.size(); j++) {
                String id = shiftList.get(j).getId();
                if (id.equals("DH")) {
                    dhIndex = j;
                } else {
                    ids.add(id);
                    req.add(getRequirement(requirementList, id, i));
                }
            }
            ids.add(0, shiftList.get(dhIndex).getId());
            req.add(0, getRequirement(requirementList, shiftList.get(dhIndex).getId(), i));
            int size = req.size() - 1;
            for (int j = 0; j < 999; j++) {
                if (nurseCounter > employeeSize-1) {
                    nurseCounter = 0;
                }
                Employee employee = employeeList.get(nurseCounter); //ich weiß nicht, ob "List" hier richtig ist
                //nurseCounter soll sagen, an welcher Stelle in der employeeList die nurse rausgesucht werden soll
                int dhReq = req.get(0);


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
                        if (size == 0) {
                            break;
                        }
                    }
                }
                nurseCounter ++;
            }
            solutionMatrix.add(day);
        }
        return solutionMatrix;
    }

    public List<int[][]> createSolution2() {
        //BedarfsListe
        List<DayOffWeekCover> requirementList = schedulingPeriod.getCoverRequirements().stream()
                .map(object -> (DayOffWeekCover) object)
                .collect(Collectors.toList());
        //MitarbeiterListe
        List<Employee> employeeList = schedulingPeriod.getEmployees().stream()
                .map(object -> (Employee) object)
                .collect(Collectors.toList());
        //Liste der Schichten
        List<Shift> shiftList = schedulingPeriod.getShiftTypes().stream()
                .map(object -> (Shift) object)
                .collect(Collectors.toList());

        int employeeSize = schedulingPeriod.getEmployees().size();
        int shiftTypeSize = schedulingPeriod.getShiftTypes().size();
        long diff = schedulingPeriod.getEndDate().getTime() - schedulingPeriod.getStartDate().getTime();
        int daysInPeriod = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + 1;

        //alle Tage
        for (int i = 0; i < daysInPeriod; i++) {
            int[][] day = new int[shiftTypeSize][employeeSize];
            List<String> ids = new ArrayList<>();
            List<Integer> req = new ArrayList<>();
            int dhIndex = 99;
            for (int j = 0; j < shiftList.size(); j++) {
                String id = shiftList.get(j).getId();
                if (id.equals("DH")) {
                    dhIndex = j;
                } else {
                    ids.add(id);
                    req.add(getRequirement(requirementList, id, i));
                }
            }
            ids.add(0, shiftList.get(dhIndex).getId());
            req.add(0, getRequirement(requirementList, shiftList.get(dhIndex).getId(), i));
            int size = req.size() - 1;
            for (Employee e : employeeList) {
                int dhReq = req.get(0);
                if (e.getSkills().contains(Skill.HEAD_NURSE) && dhReq > 0) {
                    day[0][e.getId()] = 1;
                    dhReq--;
                    req.set(0, dhReq);
                } else {
                    int reqShift = req.get(size);
                    if (reqShift > 0) {
                        day[size][e.getId()] = 1;
                        reqShift--;
                        req.set(size, reqShift);
                    } else {
                        size--;
                        if (size == 0) {
                            break;
                        }
                    }
                }
            }
            solutionMatrix.add(day);
        }
        return solutionMatrix;
    }

    private static int getRequirement(List<DayOffWeekCover> requirementList, String shifType, int i) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(schedulingPeriod.getStartDate());
        cal.add(Calendar.DATE, i);
        Day weekday = getWeekDay(cal.get(Calendar.DAY_OF_WEEK));
        for (DayOffWeekCover d : requirementList) {
            if (d.getDay().equals(weekday)) {
                for (Cover c : d.getCovers()) {
                    if (c.getShift().equals(shifType)) {
                        return c.getPreferred();
                    }
                }
            }
        }
        return 0;
    }

    private static Day getWeekDay(int weekDay) {
        switch (weekDay) {
            case 1:
                return Day.Sunday;
            case 2:
                return Day.Monday;
            case 3:
                return Day.Tuesday;
            case 4:
                return Day.Wednesday;
            case 5:
                return Day.Thursday;
            case 6:
                return Day.Friday;
            case 7:
                return Day.Saturday;
        }
        return null;
    }
}
