package attributes;

import java.util.Date;

public class ShiftOff {
    private int weight;
    private String shiftTypeId;
    private int employeeId;
    private Date date;

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getShiftTypeId() {
        return shiftTypeId;
    }

    public void setShiftTypeId(String shiftTypeId) {
        this.shiftTypeId = shiftTypeId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
