import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

import attributes.*;

public class main {
    public static void main(String argv[]) throws ParseException {
        XMLReader xmlReader = new XMLReader();
        //Problem
        SchedulingPeriod schedulingPeriod = xmlReader.parseXML("long01");
        InitialSolution initialSolution = new InitialSolution(schedulingPeriod);
        //Initiallösung:
        List<int[][]> initial = initialSolution.createSolution();

        HardRest hardRest = new HardRest(schedulingPeriod);
        boolean  b = hardRest.checkHardRest(initial);




    }
}
