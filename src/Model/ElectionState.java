package Model;

import java.util.ArrayList;

/**
 * Created by AriApar on 26/11/2015.
 */
public class ElectionState {
    private ScoreVector currentScores;
    private ArrayList<Integer> currentWinners;
    private int voteCast; // vote cast by the last voter

    public ElectionState(int numCandidates) {
        currentScores = new ScoreVector(numCandidates);
        currentWinners = new ArrayList<>();
        voteCast = 0;
    }

    public ElectionState(int[] currentScores, ArrayList<Integer> currentWinners, int voteCast) {
        this.currentScores = new ScoreVector(currentScores);
        this.currentWinners = currentWinners;
        this.voteCast = voteCast;
    }

    public ElectionState(ScoreVector currentScores, ArrayList<Integer> currentWinners, int voteCast) {
        this.currentScores = currentScores;
        this.currentWinners = currentWinners;
        this.voteCast = voteCast;
    }

    public ScoreVector getCurrentScores() {
        return currentScores;
    }

    public ArrayList<Integer> getCurrentWinners() {
        return currentWinners;
    }

    public int getVoteCast() {
        return voteCast;
    }
}
