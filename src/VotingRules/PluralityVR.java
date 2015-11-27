package VotingRules;

import Model.Preferences;
import Model.ScoreVector;
import Model.VotingRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;


/**
 * Created by AriApar on 25/11/2015.
 *
 * This class models plurality voting with a coin flip for ties.
 */
public class PluralityVR implements VotingRule {

    //private ScoreVector scores;
    private ArrayList<Integer> scoringVector;

    public PluralityVR(int candidates) {
        //scores = new ScoreVector(candidates);
        scoringVector = new ArrayList<>(candidates);
        for (int i = 0; i < candidates; i++) scoringVector.add(0);
        scoringVector.set(0,1);
    }

    @Override
    public ScoreVector vote(Preferences pref) {
        ScoreVector res = new ScoreVector(pref.length());
        int candidateNo = pref.getNthPreference(1);
        res.setCandidate(candidateNo, 1);
        return res;
    }

    @Override
    public ScoreVector vote(int candidate) {
        ScoreVector res = new ScoreVector(scoringVector.size());
        res.setCandidate(candidate, 1);
        return res;
    }

    /*@Override
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
    }*/
}
