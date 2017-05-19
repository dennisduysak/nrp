package Helper;

import Attributes.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Constraint {
    private SchedulingPeriod schedulingPeriod;
    private Helper helper;
    private List<int[][]> roster;

    public Constraint(SchedulingPeriod schedulingPeriod, List<int[][]> roster) {
        this.schedulingPeriod = schedulingPeriod;
        this.roster = roster;
        helper = new Helper(this.schedulingPeriod, this.roster);
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
        int punishmentPoints = 0;
        if (!checkHardConst()) {
            throw new Exception("Verstoß gegen harte Restriktion...");
        }
        punishmentPoints += checkNumbAssigment();

        punishmentPoints += checkMaxConsecutiveWorkingDays();
        punishmentPoints += checkMinConsecutiveWorkingDays();
        punishmentPoints += checkMaxConsecutiveFreeDays();
        punishmentPoints += checkMinConsecutiveFreeDays();
        punishmentPoints += checkWeekendConstraints();
        punishmentPoints += checkCompleteWeekends();
        punishmentPoints += checkIdenticalShiftTypesDuringWeekend();
        punishmentPoints += checkNoNightShiftBeforeFreeWeekend();
        punishmentPoints += checkDayOffRequest();
        punishmentPoints += checkShiftOffRequest();

        punishmentPoints += checkUnwantedPattern();

        return punishmentPoints;
    }

    /**
     * Prüft die aufeinanderfolgende Arbeitstage gegen die im Vertrag vereinbarte maximalgröße.
     * Für jeden Tag extra, wird ein weiterer Strafpunkt verteilt
     *
     * @return Strafpunkt
     */
    private int checkMaxConsecutiveWorkingDays() {
        List<List<Integer>> workOnDayPeriode = helper.getWorkingList();
        List<Employee> employeeList = helper.getEmployeeList();
        List<Contract> contractList = helper.getContractList();
        int punishmentPoints = 0;
        //für alle employee
        for (int j = 0; j < workOnDayPeriode.get(0).size(); j++) {
            int employeeContractId = employeeList.get(j).getContractId();
            Contract currentContract = contractList.get(employeeContractId);
            int maxConsecutiveWorkingDays = currentContract.getMaxConsecutiveWorkingDays();
            //für alle Tage
            for (int i = 0; i < workOnDayPeriode.size(); i++) {
                //wenn ein Arbeitstag
                if (workOnDayPeriode.get(i).get(j) == 1) {
                    if (currentContract.getMaxConsecutiveWorkingDays_on() == 1) {
                        int counter = 0;
                        //wenn aktueller Tag + MaxConDays noch innerhalb der Periode liegt
                        if (workOnDayPeriode.size() > i + currentContract.getMaxConsecutiveWorkingDays()) {
                            //Zähle alle Tage vom aktuellen bis MaxConDays + 1 zusammen
                            for (int k = 0; k < maxConsecutiveWorkingDays + 1; k++) {
                                counter += workOnDayPeriode.get(i + k).get(j);
                            }
                            if (counter == maxConsecutiveWorkingDays + 1) {
                                punishmentPoints += currentContract.getMaxConsecutiveWorkingDays_weight();
                            }
                        }
                    }
                }
            }
        }
        return punishmentPoints;
    }

    /**
     * Prüft die aufeinanderfolgende Arbeitstage gegen die im Vertrag vereinbarte minimalgröße.
     * Bei unterschreitung der minimalgröße => Strafpunkt je unterschrittenem Tag
     *
     * @return Strafpunkt
     */
    private int checkMinConsecutiveWorkingDays() {
        List<List<Integer>> workOnDayPeriode = helper.getWorkingList();
        List<Employee> employeeList = helper.getEmployeeList();
        List<Contract> contractList = helper.getContractList();
        int punishmentPoints = 0;
        //für alle employee
        for (int j = 0; j < employeeList.size(); j++) {
            int employeeContractId = employeeList.get(j).getContractId();
            Contract currentContract = contractList.get(employeeContractId);
            int minConsecutiveWorkingDays = currentContract.getMinConsecutiveWorkingDays();
            //für alle Tage
            for (int i = 0; i < workOnDayPeriode.size(); i++) {
                int counter = 0;

                if (currentContract.getMinConsecutiveWorkingDays_on() == 1) {
                    //Sonderfall: erster Tag in Periode
                    if (i == 0 && workOnDayPeriode.get(i).get(j) == 1) {

                        //Zähle alle Arbeitstage von i bis minConWorkingDays
                        for (int k = 0; k < minConsecutiveWorkingDays; k++) {
                            if (workOnDayPeriode.get(k).get(j) == 1) {
                                counter++;
                            } else {
                                break;
                            }
                        }
                        if (counter < minConsecutiveWorkingDays) {
                            punishmentPoints += currentContract.getMinConsecutiveWorkingDays_weight();
                        }
                    }
                    //wenn Anfang einer konsekutiven Reihe von Arbeitstagen
                    else if (i + 1 < workOnDayPeriode.size() && workOnDayPeriode.get(i).get(j) == 0 &&
                            workOnDayPeriode.get(i + 1).get(j) == 1) {

                        //wenn aktueller Tag + MinConDays noch innerhalb der Periode liegt
                        if (workOnDayPeriode.size() > i + currentContract.getMinConsecutiveWorkingDays()) {
                            //Zähle alle Tage vom morgigen bis MinConDays
                            for (int k = i + 1; k < i + 1 + minConsecutiveWorkingDays; k++) {
                                // Abfrage, ob die Konsekutive Reihe von Arbeitstagen beendet ist
                                if (workOnDayPeriode.get(k).get(j) == 1) {
                                    counter++;
                                } else {
                                    break;
                                }
                            }
                        }
                        // wenn nicht mehr innerhalb der Periode, zähle Arbeitstage bis Ende der Persiode
                        else {
                            //Zähle alle Tage vom morgigen bis Ende der Periode
                            for (int k = i + 1; k < workOnDayPeriode.size(); k++) {
                                // Abfrage, ob die Konsekutive Reihe von Arbeitstagen beendet ist
                                if (workOnDayPeriode.get(k).get(j) == 1) {
                                    counter++;
                                } else {
                                    break;
                                }
                            }
                        }
                        // Strafpunkte je unterschrittenem Tag
                        if (counter < minConsecutiveWorkingDays) {
                            counter = minConsecutiveWorkingDays - counter;
                            punishmentPoints += currentContract.getMinConsecutiveWorkingDays_weight() * counter;
                        }
                    }
                }
            }
        }
        return punishmentPoints;
    }

    /**
     * Prüft die aufeinanderfolgende freie Tage gegen die im Vertrag vereinbarte maximalgröße.
     * Für jeden Tag extra, wird ein weiterer Strafpunkt verteilt
     *
     * @return Strafpunkt
     */
    private int checkMaxConsecutiveFreeDays() {
        List<List<Integer>> workOnDayPeriode = helper.getWorkingList();
        List<Employee> employeeList = helper.getEmployeeList();
        List<Contract> contractList = helper.getContractList();
        int punishmentPoints = 0;
        //für alle employee
        for (int j = 0; j < workOnDayPeriode.get(0).size(); j++) {
            int employeeContractId = employeeList.get(j).getContractId();
            Contract currentContract = contractList.get(employeeContractId);
            int maxConsecutiveFreeDays = currentContract.getMaxConsecutiveFreeDays();
            //für alle Tage
            for (int i = 0; i < workOnDayPeriode.size(); i++) {
                //Wenn KEIN Arbeitstag
                if (workOnDayPeriode.get(i).get(j) == 0) {
                    if (currentContract.getMaxConsecutiveFreeDays_on() == 1) {
                        int counter = 0;
                        //wenn aktueller Tag + MaxConDays noch innerhalb der Periode liegt
                        if (workOnDayPeriode.size() > i + currentContract.getMaxConsecutiveFreeDays()) {
                            //Zähle alle Tage vom aktuellen bis MaxConDays + 1 zusammen
                            for (int k = 0; k < maxConsecutiveFreeDays + 1; k++) {
                                counter += workOnDayPeriode.get(i + k).get(j);
                            }
                            if (counter == 0) {
                                punishmentPoints += currentContract.getMaxConsecutiveFreeDays_weight();
                            }
                        }
                    }
                }
            }
        }
        return punishmentPoints;
    }

    /**
     * Prüft die aufeinanderfolgende freien Arbeitstage gegen die im Vertrag vereinbarte minimalgröße.
     * Bei unterschreitung der minimalgröße => Strafpunkt
     *
     * @return Strafpunkt
     */
    private int checkMinConsecutiveFreeDays() {
        List<List<Integer>> workOnDayPeriode = helper.getWorkingList();
        List<Employee> employeeList = helper.getEmployeeList();
        List<Contract> contractList = helper.getContractList();
        int punishmentPoints = 0;
        //für alle employee
        for (int j = 0; j < workOnDayPeriode.get(0).size(); j++) {
            int employeeContractId = employeeList.get(j).getContractId();
            Contract currentContract = contractList.get(employeeContractId);
            int minConsecutiveFreeDays = currentContract.getMinConsecutiveFreeDays();
            //für alle Tage
            for (int i = 0; i < workOnDayPeriode.size(); i++) {
                int counter = 0;

                if (currentContract.getMinConsecutiveFreeDays_on() == 1) {
                    //Sonderfall: erster Tag in Periode
                    if (i == 0 && workOnDayPeriode.get(i).get(j) == 0) {

                        //Zähle alle nicht Arbeitstage von i bis minConFreeDays
                        for (int k = 0; k < minConsecutiveFreeDays; k++) {
                            if (workOnDayPeriode.get(k).get(j) == 0) {
                                counter += 1;
                            } else {
                                break;
                            }
                        }
                        if (counter < minConsecutiveFreeDays) {
                            counter = minConsecutiveFreeDays - counter;
                            punishmentPoints += currentContract.getMinConsecutiveFreeDays_weight() * counter;
                        }
                    }
                    //wenn Angang einer Arbeitstagreihe
                    else if (i + 1 < workOnDayPeriode.size() && workOnDayPeriode.get(i).get(j) == 1 &&
                            workOnDayPeriode.get(i + 1).get(j) == 0) {

                        //wenn aktueller Tag + MinConDays noch innerhalb der Periode liegt
                        if (workOnDayPeriode.size() > i + currentContract.getMinConsecutiveFreeDays()) {
                            //Zähle alle Tage vom morgigen bis MinConDays
                            for (int k = 0; k < minConsecutiveFreeDays; k++) {
                                if (workOnDayPeriode.get(i + k + 1).get(j) == 0) {
                                    counter += 1;
                                } else {
                                    break;
                                }
                            }
                        }
                        // wenn nicht mehr innerhalb der Periode, zähle freie Tage bis Ende der Persiode
                        else {
                            //Zähle alle Tage vom morgigen bis Ende der Periode
                            for (int k = i + 1; k < workOnDayPeriode.size(); k++) {
                                // Abfrage, ob die Konsekutive Reihe von Arbeitstagen beendet ist
                                if (workOnDayPeriode.get(k).get(j) == 0) {
                                    counter++;
                                } else {
                                    break;
                                }
                            }
                        }
                        if (counter < minConsecutiveFreeDays) {
                            counter = minConsecutiveFreeDays - counter;
                            punishmentPoints += currentContract.getMinConsecutiveFreeDays_weight() * counter;
                        }
                    }
                }
            }
        }
        return punishmentPoints;
    }

    /**
     * Prüft die aufeinanderfolgenden Arbeits-Wochenenden und zählt:
     * 1. um wie viel die Maximalanzahl überschritten wurde
     * 2. um wie viel die Minimalanzahl unterschritten wurde
     * <p>
     * 3. Prüft die Wochenenden in einer Persiode und zält um wie viel die Maximalanzahl überschritten wurde
     *
     * @return Strafpunkte - je überschrittene Einheit
     */
    private int checkWeekendConstraints() {
        List<List<Integer>> workOnDayPeriode = helper.getWorkingList();
        List<Employee> employeeList = helper.getEmployeeList();
        List<Contract> contractList = helper.getContractList();
        int punishmentPoints_maxCon = 0;
        int punishmentPoints_minCon = 0;
        int punishmentPoints_maxWorkingWe = 0;
        //für alle employee
        for (int j = 0; j < workOnDayPeriode.get(0).size(); j++) {
            int contractId = employeeList.get(j).getContractId();
            Contract currentContract = contractList.get(contractId);
            List<Day> weekendDefinition = currentContract.getWeekendDefinition();
            int countConsecutiveWorkAtWeekend = 0;
            int countWorkAtWeekend = 0;
            //für alle Tage
            boolean oldValue = false;
            for (int i = 0; i < workOnDayPeriode.size(); i++) {
                Day currentDay = helper.getWeekDayOfPeriode(i);
                if (weekendDefinition.contains(currentDay)) {
                    int indexOfWeekendDefinition = weekendDefinition.indexOf(currentDay);
                    boolean workAtWeekend = false;
                    if (workOnDayPeriode.size() > i + weekendDefinition.size() - 1) {
                        for (int k = 0; k < weekendDefinition.size() - indexOfWeekendDefinition; k++) {
                            if (workOnDayPeriode.get(i + k).get(j) == 1) {
                                workAtWeekend = true;
                                break;
                            }
                        }
                    } else {
                        for (int k = 0; k < workOnDayPeriode.size() - i; k++) {
                            if (workOnDayPeriode.get(i + k).get(j) == 1) {
                                workAtWeekend = true;
                                break;
                            }
                        }
                    }
                    if (oldValue && workAtWeekend) {
                        countConsecutiveWorkAtWeekend++;
                    }
                    if (workAtWeekend) {
                        countWorkAtWeekend++;
                        i += weekendDefinition.size() - indexOfWeekendDefinition - 1;
                    }
                    oldValue = workAtWeekend;
                }

            }
            int maxConsecutiveWorkingWeekends = currentContract.getMaxConsecutiveWorkingWeekends();
            if (currentContract.getMaxConsecutiveWorkingWeekends_on() == 1 &&
                    countConsecutiveWorkAtWeekend > maxConsecutiveWorkingWeekends) {
                punishmentPoints_maxCon += (countConsecutiveWorkAtWeekend - maxConsecutiveWorkingWeekends) *
                        currentContract.getMaxConsecutiveWorkingWeekends_weight();
            }
            int minConsecutiveWorkingWeekends = currentContract.getMinConsecutiveWorkingWeekends();
            if (currentContract.getMinConsecutiveWorkingWeekends_on() == 1 &&
                    countConsecutiveWorkAtWeekend < minConsecutiveWorkingWeekends) {
                punishmentPoints_minCon += (minConsecutiveWorkingWeekends - countConsecutiveWorkAtWeekend) *
                        currentContract.getMaxConsecutiveWorkingWeekends_weight();
            }
            int maxWorkingWeekendsInFourWeeks = currentContract.getMaxWorkingWeekendsInFourWeeks();
            if (currentContract.getMaxWorkingWeekendsInFourWeeks_on() == 1 &&
                    countWorkAtWeekend > maxWorkingWeekendsInFourWeeks) {
                punishmentPoints_maxWorkingWe += (countWorkAtWeekend - maxWorkingWeekendsInFourWeeks) *
                        currentContract.getMaxWorkingWeekendsInFourWeeks_weight();
            }
        }
        return punishmentPoints_maxCon + punishmentPoints_minCon + punishmentPoints_maxWorkingWe;
    }

    /**
     * !!Achtung: diese Methode ist noch nicht fertig. Ist größtenteils erst nur copy paste
     * Prüft nach, wenn am Wochenende gearbetet wird, dann gleich das ganze Wochenende
     *
     * @return Strafpunkte
     */
    private int checkCompleteWeekends() {
        List<List<Integer>> workOnDayPeriode = helper.getWorkingList();
        List<Employee> employeeList = helper.getEmployeeList();
        List<Contract> contractList = helper.getContractList();
        int punishmentPoints = 0;
        //für alle employee
        for (int j = 0; j < workOnDayPeriode.get(0).size(); j++) {
            int contractId = employeeList.get(j).getContractId();
            Contract currentContract = contractList.get(contractId);
            List<Day> weekendDefinition = currentContract.getWeekendDefinition();
            //für alle Tage
            for (int i = 0; i < workOnDayPeriode.size(); i++) {
                if (currentContract.getMaxWorkingWeekendsInFourWeeks_on() == 1) {
                    Day currentDay = helper.getWeekDayOfPeriode(i);
                    if (weekendDefinition.contains(currentDay)) {
                        int indexOfWeekendDefinition = weekendDefinition.indexOf(currentDay);
                        int workAtWeekend = 0;
                        if (workOnDayPeriode.size() > i + weekendDefinition.size() - 1) {
                            for (int k = indexOfWeekendDefinition; k < weekendDefinition.size(); k++) {
                                if (workOnDayPeriode.get(i + k).get(j) == 1) {
                                    workAtWeekend++;
                                }
                            }
                        } else {
                            for (int k = indexOfWeekendDefinition; k < workOnDayPeriode.size() - i; k++) {
                                if (workOnDayPeriode.get(i + k).get(j) == 1) {
                                    workAtWeekend++;
                                }
                            }
                        }
                    }
                }
            }
        }
        return punishmentPoints;
    }

    /**
     * Prüft für jeden Employee ob die Nachtschicht vor dem Beginn eines Wochenendes gearbeitet wird.
     * Wenn ja erfolgt ein Strafpunkt.
     *
     * @return Strafpunkte
     */
    private int checkNoNightShiftBeforeFreeWeekend() {
        List<List<Integer>> workOnDayPeriode = helper.getWorkingList();
        List<Employee> employeeList = helper.getEmployeeList();
        List<Contract> contractList = helper.getContractList();
        List<String> shiftWithIndices = helper.getShiftWithIndices();
        int punishmentPoints = 0;
        //für alle employee
        for (int j = 0; j < workOnDayPeriode.get(0).size(); j++) {
            int contractId = employeeList.get(j).getContractId();
            Contract currentContract = contractList.get(contractId);
            List<Day> weekendDefinition = currentContract.getWeekendDefinition();
            //für alle Tage
            for (int i = 0; i < workOnDayPeriode.size(); i++) {
                if (currentContract.isNoNightShiftBeforeFreeWeekend()) {
                    Day currentDay = helper.getWeekDayOfPeriode(i);
                    int nightShiftIndex = shiftWithIndices.indexOf("N");
                    if (weekendDefinition.get(0) == currentDay &&
                            workOnDayPeriode.get(i).get(j) == 0 &&
                            i != 0 &&
                            roster.get(i - 1)[nightShiftIndex][j] == 1) {
                        // hier nur Bestrafung, wenn Freitag/Samstag frei?? Was ist, wenn Sonntag gearbeitet wird?
                        punishmentPoints++;
                    }
                }
            }
        }
        return punishmentPoints;
    }

    /**
     * Prüft nach, dass an einem Wochenende die gleiche Schicht gearbeitet wird.
     * Annahme: Der erste Tag des definierten Wochenendes legt den Schichttyp fest, auf welchen die nächsten Tage
     * überprüft werden.
     * Nicht an diesem Tag zu arbeiten, ist auch eine Art Schicht.
     *
     * @return Strafpunkte
     */
    private int checkIdenticalShiftTypesDuringWeekend() {
        List<List<Integer>> workOnDayPeriode = helper.getWorkingList();
        List<Employee> employeeList = helper.getEmployeeList();
        List<Contract> contractList = helper.getContractList();
        int punishmentPoints = 0;
        //für alle employee
        for (int j = 0; j < workOnDayPeriode.get(0).size(); j++) {
            int contractId = employeeList.get(j).getContractId();
            Contract currentContract = contractList.get(contractId);
            List<Day> weekendDefinition = currentContract.getWeekendDefinition();
            //für alle Tage
            for (int i = 0; i < workOnDayPeriode.size(); i++) {
                if (currentContract.isIdenticalShiftTypesDuringWeekend()) {
                    Day currentDay = helper.getWeekDayOfPeriode(i);
                    if (weekendDefinition.contains(currentDay)) {
                        String currentShift = helper.getShiftOfDay(i, j);
                        int indexOfWeekendDefinition = weekendDefinition.indexOf(currentDay);
                        if (workOnDayPeriode.size() > i + weekendDefinition.size() - 1) {
                            for (int k = 0; k < weekendDefinition.size() - indexOfWeekendDefinition; k++) {
                                if (!currentShift.equals(helper.getShiftOfDay(k + i, j))) {
                                    punishmentPoints++;
                                }
                            }
                            i += weekendDefinition.size() - indexOfWeekendDefinition - 1;
                        } else {
                            // Wenn nicht mehr in Periode
                            for (int k = 0; k < workOnDayPeriode.size() - i; k++) {
                                if (workOnDayPeriode.get(i + k).get(j) == 1) {
                                    if (!currentShift.equals(helper.getShiftOfDay(k + i, j))) {
                                        punishmentPoints++;
                                    }
                                }
                            }
                            i += weekendDefinition.size() - indexOfWeekendDefinition - 1;
                        }
                    }
                }
            }
        }
        return punishmentPoints;
    }

    /**
     * Prüft und zählt die Schichten, an dem sich die Employees frei gewünscht haben, sie aber trotzdem arbeiten müssen
     *
     * @return Strafpunkte
     */
    private int checkShiftOffRequest() {
        List<ShiftOff> shiftOff = helper.getShiftOffRequestList();
        List<String> shiftWithIndices = helper.getShiftWithIndices();
        int counter = 0;
        for (ShiftOff s : shiftOff) {
            int dayNumber = helper.getDaysFromStart(s.getDate()) - 1;
            int index = shiftWithIndices.indexOf(s.getShiftTypeId());
            int workShiftToday = roster.get(dayNumber)[index][s.getEmployeeId()];
            if (workShiftToday == 1) {
                counter += s.getWeight();
            }
        }
        return counter;
    }

    /**
     * Prüft und zählt die Tage, an dem sich die employees frei gewünscht haben, sie aber trotzdem arbeiten Müssen
     *
     * @return Strafpunkte
     * @throws Exception wenn harte Restriktionen nicht erfüllt wurden
     */
    private int checkDayOffRequest() throws Exception {
        List<DayOff> dayOff = helper.getDayOffRequestList();
        int counter = 0;
        for (DayOff d : dayOff) {
            int dayNumber = helper.getDaysFromStart(d.getDate()) - 1;
            int worksToday = worksToday(d.getEmployeeId(), dayNumber);
            if (worksToday == 1) {
                counter += d.getWeight();
            }
        }
        return counter;
    }

    /**
     * Gibt an ob ein employee x am Tag y arbeitet.
     *
     * @param employeeId EmployeeID
     * @param dayNumber  Der Tag nach beginn der Periode (Also wenn die Woche am Montag beginnt,
     *                   wäre der erste Mittwoch eine 3)
     * @return 1, wenn employee an dem Tag arbeitet (egal welche Schicht), 0 sonst
     * @throws Exception
     */
    private int worksToday(int employeeId, int dayNumber) throws Exception {
        int[][] j = roster.get(dayNumber);
        int worksToday = 0;
        for (int i = 0; i < roster.get(dayNumber).length; i++) {
            worksToday += j[i][employeeId];
        }
        if (worksToday > 1) {
            throw new Exception("Verstoß gegen harte Restriktion...");
        }
        return worksToday;
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

    /**
     * Prüft die unwantedPatterns
     *
     * @return Strafpunkte
     */
    private int checkUnwantedPattern() {
        List<List<Integer>> workOnDayPeriode = helper.getWorkingList();
        List<Employee> employeeList = helper.getEmployeeList();
        List<Contract> contractList = helper.getContractList();
        List<Pattern> patternList = helper.getPatternList();
        int punishmentPoints = 0;
        //für alle employee
        for (int j = 0; j < employeeList.size(); j++) {
            int employeeContractId = employeeList.get(j).getContractId();
            Contract currentContract = contractList.get(employeeContractId);
            List<Integer> unwantedPatterns = currentContract.getUnwantedPatterns();

            //für alle Tage
            for (int i = 0; i < workOnDayPeriode.size(); i++) {
                String shift = helper.getShiftOfDay(i, j);
                Day day = helper.getWeekDayOfPeriode(i);
                for (int k : unwantedPatterns) {
                    Pattern pattern = patternList.get(k);
                    List<PatternEntry> patternEntry = pattern.getPatternEntryList();
                    PatternEntry trigger = patternEntry.get(0);
                    if (trigger.getShiftType().equals(shift) &&
                            (trigger.getDay() == Day.Any || trigger.getDay() == day)) {
                        for (int l = 1; l < patternEntry.size(); l++) {
                            if (!(i + unwantedPatterns.size() - 1 >= workOnDayPeriode.size())) {
                                PatternEntry currentEntry = patternEntry.get(l);
                                String shift2 = helper.getShiftOfDay(i + l, j);
                                Day day2 = helper.getWeekDayOfPeriode(i + l);
                                if (!((currentEntry.getShiftType().equals("Any") || currentEntry.getShiftType().equals(shift2)) &&
                                        (currentEntry.getDay() == Day.Any || currentEntry.getDay() == day2))) {
                                    punishmentPoints++;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        return punishmentPoints;
    }
}
