import attributes.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Helper {
    private SchedulingPeriod schedulingPeriod;

    public Helper(SchedulingPeriod schedulingPeriod) {
        this.schedulingPeriod = schedulingPeriod;
    }

    public List<DayOffWeekCover> getRequirementList() {
        return schedulingPeriod.getCoverRequirements().stream()
                .map(object -> (DayOffWeekCover) object)
                .collect(Collectors.toList());
    }

    public List<Employee> getEmployeeList() {
        return schedulingPeriod.getEmployees().stream()
                .map(object -> (Employee) object)
                .collect(Collectors.toList());
    }

    public List<Shift> getShiftList() {
        return schedulingPeriod.getShiftTypes().stream()
                .map(object -> (Shift) object)
                .collect(Collectors.toList());
    }

    public int getRequirement(List<DayOffWeekCover> requirementList, String shiftId, int daysAfterStart) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(schedulingPeriod.getStartDate());
        cal.add(Calendar.DATE, daysAfterStart);
        Day weekday = getWeekDay(cal.get(Calendar.DAY_OF_WEEK));
        for (DayOffWeekCover d : requirementList) {
            if (d.getDay().equals(weekday)) {
                for (Cover c : d.getCovers()) {
                    if (c.getShift().equals(shiftId)) {
                        return c.getPreferred();
                    }
                }
            }
        }
        return 0;
    }

    public List<Integer> getReq(int index) {
        List<Integer> req = new ArrayList<>();
        List<DayOffWeekCover> requirementList = getRequirementList();
        List<Shift> shiftList = getShiftList();
        int dhIndex = 99;
        for (int i = 0; i < shiftList.size(); i++) {
            String shiftId = shiftList.get(i).getId();

            if (shiftId.equals("DH")) {
                dhIndex = i;
            } else {
                req.add(getRequirement(requirementList, shiftId, index));
            }
        }
        req.add(getRequirement(requirementList, shiftList.get(dhIndex).getId(), index));
        return req;
    }

    public int getDaysInPeriod() {
        long diff = schedulingPeriod.getEndDate().getTime() - schedulingPeriod.getStartDate().getTime();
        return (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + 1;
    }

    private Day getWeekDay(int weekDay) {
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
