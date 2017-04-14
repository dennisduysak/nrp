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

    /**
     * Gibt aus schedulingPeriod die Bedarfs-Liste wieder
     *
     * @return Liste der Bedarfe
     */
    public List<DayOffWeekCover> getRequirementList() {
        return schedulingPeriod.getCoverRequirements().stream()
                .map(object -> (DayOffWeekCover) object)
                .collect(Collectors.toList());
    }

    /**
     * Gibt aus schedulingPeriod die Mitarbeiter-Liste wieder
     *
     * @return Liste der Mitarbeiten
     */
    public List<Employee> getEmployeeList() {
        return schedulingPeriod.getEmployees().stream()
                .map(object -> (Employee) object)
                .collect(Collectors.toList());
    }

    /**
     * Gibt aus schedulingPeriod die Schicht-Liste wieder
     *
     * @return Liste der Schichten
     */
    public List<Shift> getShiftList() {
        return schedulingPeriod.getShiftTypes().stream()
                .map(object -> (Shift) object)
                .collect(Collectors.toList());
    }

    /**
     * Gibt den Bedarf der Schicht an dem bestimmten Wochentag wieder.
     *
     * @param shiftId        SchiftID
     * @param daysAfterStart Anzahl der Tage nach beginn der Periode
     * @return Bedarf
     */
    public int getRequirement(String shiftId, int daysAfterStart) {
        List<DayOffWeekCover> requirementList = getRequirementList();
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

    /**
     * Gibt eine Liste von Integern wieder die den Bedarf der Schichten abhängig vom Wochentag beiinhaltet
     * Der Bedarf der Oberschwestern befindet sich im Listenanfang
     *
     * @param daysAfterStart Anzahl der Tage nach beginn der Periode
     * @return Liste von Integern mit Bedarf
     */
    public List<Integer> getRequirementsForDay(int daysAfterStart) {
        List<Integer> requirements = new ArrayList<>();
        List<Shift> shiftList = getShiftList();
        int dhIndex = 99;
        for (int i = 0; i < shiftList.size(); i++) {
            String shiftId = shiftList.get(i).getId();
            //Wenn DH, dann Index merken
            if (shiftId.equals("DH")) {
                dhIndex = i;
                //ansonsten füge in die req-Liste den Bedarf für den Tag und Schicht hinzu
            } else {
                requirements.add(getRequirement(shiftId, daysAfterStart));
            }
        }
        //füge den Bedarf der Oberschwester an den Listenanfang
        if (dhIndex != 99) {
            requirements.add(0, getRequirement(shiftList.get(dhIndex).getId(), daysAfterStart));
        }
        return requirements;
    }

    /**
     * Zählt die Tage zwischen Anfang und Ende der Periode
     *
     * @return Anzahl an Tagen
     */
    public int getDaysInPeriod() {
        long diff = schedulingPeriod.getEndDate().getTime() - schedulingPeriod.getStartDate().getTime();
        return (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + 1;
    }

    /**
     * Gibt zum passender Indexnummer den korrekten Wochentag aus.
     *
     * @param weekDay indexnummer des Wochentags
     * @return Wochentag der Klasse Day
     */
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
