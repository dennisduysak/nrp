package Helper;

public class RequirementsForDay {
    private int demand;
    private String shiftID;

    public RequirementsForDay (int demand, String shiftID) {
        this.demand = demand;
        this.shiftID = shiftID;
    }

    public int getDemand() {
        return demand;
    }

    public String getShiftID() {
        return shiftID;
    }

    public void setDemand(int demand) {
        this.demand = demand;
    }

    public void setShiftID(String shiftID) {
        this.shiftID = shiftID;
    }
}
