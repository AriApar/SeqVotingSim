package Model;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by AriApar on 25/11/2015.
 */
public abstract class Election {

    public PreferenceList pref;
    public VotingOrder order;
    public VotingRule rule;
    public ArrayList<Voter> voters;

    public ScoreVector scores;

    public abstract int run();

    public ArrayList<Integer> getWinners(ScoreVector scores) {
        ArrayList<Integer> winners = new ArrayList<>();
        int maxVotes = 0;
        for (int candidate = 1; candidate <= this.pref.getNumCandidates(); candidate++) {
            if (scores.getCandidate(candidate) > maxVotes) {
                winners.clear(); winners.add(candidate); maxVotes = scores.getCandidate(candidate);
            }
            else if (scores.getCandidate(candidate) == maxVotes) {
                winners.add(candidate);
            }
        }
        return winners;
    }

    public int getUniqueWinner(ScoreVector scores) {
        ArrayList<Integer> winners = getWinners(scores);
        if (winners.size() == 1) return winners.get(0);
        else {
            //tie-breaker via Random
            Random random = new Random();
            int winner = random.nextInt(winners.size());
            return winners.get(winner);
        }
    }

    public int getUniqueWinner() {
        return getUniqueWinner(this.scores);
    }

    public int getScore(int candidate) {
        return this.scores.getCandidate(candidate);
    }

    public ScoreVector getScores() {
        return scores;
    }

}
