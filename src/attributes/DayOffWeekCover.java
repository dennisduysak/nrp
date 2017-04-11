package attributes;

import java.util.Date;
import java.util.List;

public class DayOffWeekCover {
    private Day day;
    private List<Cover> covers;

    public Day getDay() {
        return day;
    }

    public void setDay(Day day) {
        this.day = day;
    }

    public List<Cover> getCovers() {
        return covers;
    }

    public void setCovers(List<Cover> covers) {
        this.covers = covers;
    }
}
