import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import attributes.*;

public class main {
    //test (bitte löschen)
    public static void main(String argv[]) throws ParseException {
        XMLReader xmlReader = new XMLReader();
        SchedulingPeriod schedulingPeriod = xmlReader.parseXML("long01");
        InitialSolution initialSolution = new InitialSolution(schedulingPeriod);
        List<int[][]> initial = initialSolution.createSolution();
    }
}
