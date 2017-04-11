package attributes;

import java.util.Date;
import java.util.List;

public class SchedulingPeriod {

    private String id;
    private Date startDate;
    private Date endDate;
    private List<Skill> skills;
    private List<Object> shiftTypes;
    private List<Object> patterns;
    private List<Object> contracts;
    private List<Object> employees;
    private List<Object> coverRequirements;
    private List<Object> dayOffRequests;
    private List<Object> shiftOffRequests;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public void setSkills(List<Skill> skills) {
        this.skills = skills;
    }

    public List<Object> getShiftTypes() {
        return shiftTypes;
    }

    public void setShiftTypes(List<Object> shiftTypes) {
        this.shiftTypes = shiftTypes;
    }

    public List<Object> getPatterns() {
        return patterns;
    }

    public void setPatterns(List<Object> patterns) {
        this.patterns = patterns;
    }

    public List<Object> getContracts() {
        return contracts;
    }

    public void setContracts(List<Object> contracts) {
        this.contracts = contracts;
    }

    public List<Object> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Object> employees) {
        this.employees = employees;
    }

    public List<Object> getCoverRequirements() {
        return coverRequirements;
    }

    public void setCoverRequirements(List<Object> coverRequirements) {
        this.coverRequirements = coverRequirements;
    }

    public List<Object> getDayOffRequests() {
        return dayOffRequests;
    }

    public void setDayOffRequests(List<Object> dayOffRequests) {
        this.dayOffRequests = dayOffRequests;
    }

    public List<Object> getShiftOffRequests() {
        return shiftOffRequests;
    }

    public void setShiftOffRequests(List<Object> shiftOffRequests) {
        this.shiftOffRequests = shiftOffRequests;
    }
}
