import attributes.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HardRest {
    private SchedulingPeriod schedulingPeriod;
    private Helper helper;

    HardRest(SchedulingPeriod schedulingPeriod){
        this.schedulingPeriod = schedulingPeriod;
        helper = new Helper(this.schedulingPeriod);
    }

    //TODO kommentieren
    //TODO refactorn!!
    public boolean checkHardRest(List<int[][]> initial) {
        int employeeWorkAtOneDay = 0;
        List<Shift> shiftList = helper.getShift();
        for (int i = 0; i < initial.size(); i++) {
            List<Integer> req = helper.getReq(i);
            int employeeSize = helper.getEmployee().size();
            for (int j = 0; j < employeeSize; j++) {
                int[][] shiftList2 = initial.get(i);
                for (int k = 0; k < shiftList.size(); k++) {
                    int demand = 0;
                    for (int l = 0; l < employeeSize; l++) {
                        demand += shiftList2[k][l];
                    }
                    if (demand > req.get(k)){
                        return false;
                    } else if (demand < req.get(k)){
                        return false;
                    }
                    int[][] d = initial.get(i);
                    employeeWorkAtOneDay += d[k][j];
                }
                if (employeeWorkAtOneDay > 1) {
                    System.out.print("Employee: " + j + " arbeitet mehr als eine Schicht am Tag: " + i+1);
                    return false;
                }
                employeeWorkAtOneDay = 0;
            }
        }
        return true;
    }
}
