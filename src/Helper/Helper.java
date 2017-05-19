package Helper;

import Attributes.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Helper {
    private SchedulingPeriod schedulingPeriod;
    private List<int[][]> roster;

    public Helper(SchedulingPeriod schedulingPeriod, List<int[][]> roster) {
        this.schedulingPeriod = schedulingPeriod;
        this.roster = roster;
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
     * Gibt aus schedulingPeriod die Contract-Liste wieder
     *
     * @return Liste der Mitarbeiten
     */
    public List<Contract> getContractList() {
        return schedulingPeriod.getContracts().stream()
                .map(object -> (Contract) object)
                .collect(Collectors.toList());
    }

    /**
     * Gibt aus schedulingPeriod die UnwandetPatterns-Liste wieder
     *
     * @return Liste der Mitarbeiten
     */
    public List<Pattern> getPatternList() {
        return schedulingPeriod.getPatterns().stream()
                .map(object -> (Pattern) object)
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
     * Gibt aus schedulingPeriod die DayOff-Wunsch-Liste wieder
     *
     * @return Liste der Wünsche
     */
    public List<DayOff> getDayOffRequestList() {
        return schedulingPeriod.getDayOffRequests().stream()
                .map(object -> (DayOff) object)
                .collect(Collectors.toList());
    }

    /**
     * Gibt aus schedulingPeriod die DayOff-Wunsch-Liste wieder
     *
     * @return Liste der Wünsche
     */
    public List<ShiftOff> getShiftOffRequestList() {
        return schedulingPeriod.getShiftOffRequests().stream()
                .map(object -> (ShiftOff) object)
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
    public List<RequirementsForDay> getRequirementsForDay(int daysAfterStart) {
        List<RequirementsForDay> requirements = new ArrayList<>();
        List<Shift> shiftList = getShiftList();
        int dhIndex = 99;
        for (int i = 0; i < shiftList.size(); i++) {
            String shiftId = shiftList.get(i).getId();
            //Wenn DH, dann Index merken
            if (shiftId.equals("DH")) {
                dhIndex = i;
                //ansonsten füge in die req-Liste den Bedarf für den Tag und Schicht hinzu
            } else {
                requirements.add(new RequirementsForDay(getRequirement(shiftId, daysAfterStart), shiftId));
            }
        }
        //füge den Bedarf der Oberschwester an den Listenanfang
        if (dhIndex != 99) {
            requirements.add(0, new RequirementsForDay(getRequirement(shiftList.get(dhIndex).getId(), daysAfterStart), shiftList.get(dhIndex).getId()));
        }
        return requirements;
    }

    /**
     * Gibt eine Liste der Schichten zurück, in der Reihenfolge wie im Roster
     *
     * @return String-Liste von Schichten
     */
    public List<String> getShiftWithIndices() {
        List<Shift> shiftList = getShiftList();
        List<String> shiftWithIndices = new ArrayList<>();
        int dhIndex = 99;
        for (int i = 0; i < shiftList.size(); i++) {
            String shiftId = shiftList.get(i).getId();
            //Wenn DH, dann Index merken
            if (shiftId.equals("DH")) {
                dhIndex = i;
            } else {
                shiftWithIndices.add(shiftId);
            }
        }
        //füge Oberschwester an den Listenanfang
        if (dhIndex != 99) {
            shiftWithIndices.add(0, shiftList.get(dhIndex).getId());
        }
        return shiftWithIndices;
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

    /**
     * Gibt die Anzahl an Tagen zwischen zwei Dati an
     *
     * @param start Startdatum
     * @param end   Enddatum
     * @return Anzahl an Tagen zwischen Start und End
     */
    private int calcDays(Date start, Date end) {
        long diff = end.getTime() - start.getTime();
        return (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + 1;
    }

    /**
     * Zählt die Tage zwischen Anfang und Ende der Periode
     *
     * @return Anzahl der Tage
     */
    public int getDaysInPeriod() {
        return calcDays(schedulingPeriod.getStartDate(), schedulingPeriod.getEndDate());
    }

    /**
     * Zählt die Tage zwischen Anfang und date
     *
     * @param date Datumswert, bis wann gezählt werden soll
     * @return Anzahl der Tage
     */
    public int getDaysFromStart(Date date) {
        return calcDays(schedulingPeriod.getStartDate(), date);
    }

    public List<List<Integer>> getWorkingList() {
        List<List<Integer>> workOnDayPeriode = new ArrayList<>();
        for (int i = 0; i < roster.size(); i++) {
            List<Integer> workOnDayList = new ArrayList<>();
            for (int k = 0; k < getEmployeeList().size(); k++) {
                int workOnDay = 0;
                for (int j = 0; j < roster.get(0).length; j++) {
                    workOnDay += roster.get(i)[j][k];
                }
                workOnDayList.add(workOnDay);
            }
            workOnDayPeriode.add(workOnDayList);
        }
        return workOnDayPeriode;
    }

    /**
     * Gibt den Wochentag des Tages wieder
     *
     * @param day Tag der Periode (von 0 anfangend)
     * @return passenden Wochentag
     */
    public Day getWeekDayOfPeriode(int day) {
        Calendar c = Calendar.getInstance();
        c.setTime(schedulingPeriod.getStartDate());
        c.add(Calendar.DATE, day);
        return getWeekDay(c.get(Calendar.DAY_OF_WEEK));
    }

    /**
     * Gibt die Schicht zurück an dem der Employee für den Tag x arbeitet
     *
     * @param day      Tag
     * @param employee Employee
     * @return Schicht als String
     */
    public String getShiftOfDay(int day, int employee) {
        int[][] currentDay = roster.get(day);
        int shiftId = 99;
        for (int i = 0; i < currentDay.length; i++) {
            if (currentDay[i][employee] == 1) {
                shiftId = i;
            }
        }
        if (shiftId == 99) {
            return "None";
        }
        return getShiftWithIndices().get(shiftId);
    }
}
