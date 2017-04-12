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

    public List<DayOffWeekCover> getRequirement() {
        return schedulingPeriod.getCoverRequirements().stream()
                .map(object -> (DayOffWeekCover) object)
                .collect(Collectors.toList());
    }

    public List<Employee> getEmployee() {
        return schedulingPeriod.getEmployees().stream()
                .map(object -> (Employee) object)
                .collect(Collectors.toList());
    }

    public List<Shift> getShift() {
        return schedulingPeriod.getShiftTypes().stream()
                .map(object -> (Shift) object)
                .collect(Collectors.toList());
    }

    public int getRequirement(List<DayOffWeekCover> requirementList, String shifType, int i) {
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

    public List<Integer> getReq(int index){
        List<Integer> req = new ArrayList<>();
        List<DayOffWeekCover> requirementList = getRequirement();
        List<Shift> shiftList = getShift();
        int dhIndex = 99;
        for (int i = 0; i < shiftList.size(); i++) {
            String id = shiftList.get(i).getId();

            if (id.equals("DH")) {
                dhIndex = i;
            } else {
                req.add(getRequirement(getRequirement(), id, index));
            }
        }
        req.add(getRequirement(getRequirement(), shiftList.get(dhIndex).getId(), index));
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
