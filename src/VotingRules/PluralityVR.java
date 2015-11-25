package VotingRules;

import Model.Preferences;
import Model.VotingRule;

import java.util.ArrayList;
import java.util.Random;


/**
 * Created by AriApar on 25/11/2015.
 *
 * This class models plurality voting with a coin flip for ties.
 */
public class PluralityVR implements VotingRule {

    private int[] scores;
    private int[] scoringVector;

    public PluralityVR(int candidates) {
        scores = new int[candidates];
        scoringVector = new int[candidates];
        scoringVector[0] = 1;
    }

    @Override
    public void vote(Preferences pref) {
        for (int rank = 1; rank <= pref.length(); rank++ ) {
            int candidateNo = pref.getNthPreference(rank);
            updateScore(candidateNo, rank);
        }
    }

    @Override
    public int getWinner() {
        ArrayList<Integer> winners = new ArrayList<>();
        int maxVotes = 0;
        for (int candidate = 1; candidate <= scores.length; candidate++) {
            if (scores[candidate-1] > maxVotes) {
                winners.clear(); winners.add(candidate); maxVotes = scores[candidate-1];
            }
            else if (scores[candidate-1] == maxVotes) {
                winners.add(candidate);
            }
        }
        if (winners.size() == 1) return winners.get(0);
        else {
            //tie-breaker via Random
            Random random = new Random();
            int winner = random.nextInt(winners.size());
            return winners.get(winner);
        }
    }

    public int[] getScores() {
        return scores.clone();
    }


    private void updateScore(int candidate, int rank) {
        assert ((rank != 0) || (candidate != 0));
        if (scoringVector[rank-1] != 0)
            scores[candidate-1] +=  scoringVector[rank-1];

    }
}
