package Model;

import java.util.ArrayList;

/**
 * Created by AriApar on 26/11/2015.
 */
public class ElectionState {
    private ScoreVector currentScores;
    private ArrayList<Integer> currentWinners;
    private ArrayList<Integer> currentVotes;
    private int voteCast; // vote cast by the last voter

    public ElectionState(int numCandidates) {
        currentScores = new ScoreVector(numCandidates);
        currentWinners = new ArrayList<>();
        currentVotes = new ArrayList<>();
        voteCast = 0;
    }

    public ElectionState(ScoreVector currentScores, ArrayList<Integer> currentWinners,
                         ArrayList<Integer> currentVotes, int voteCast) {
        this.currentScores = currentScores;
        this.currentWinners = currentWinners;
        this.currentVotes = currentVotes;
        this.voteCast = voteCast;
    }

    public ScoreVector getCurrentScores() {
        return currentScores;
    }

    public ArrayList<Integer> getCurrentWinners() {
        return currentWinners;
    }

    public ArrayList<Integer> getCurrentVotes() { return currentVotes;}

    public int getVoteCast() {
        return voteCast;
    }
}
