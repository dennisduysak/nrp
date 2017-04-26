package Helper;

import java.util.List;

public class Solution {
    private List<int[][]>  roster;
    private int score;
    public Solution(List<int[][]> roster, int score) {
        this.roster = roster;
        this.score = score;
    }

    public List<int[][]> getRoster() {
        return roster;
    }

    public int getScore() {
        return score;
    }
}
