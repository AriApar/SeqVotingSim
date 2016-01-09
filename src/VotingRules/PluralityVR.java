package VotingRules;

import Model.ElectionState;
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
        res = res.cloneAndSetCandidate(candidateNo, 1);
        return res;
    }

    @Override
    public ScoreVector vote(int candidate) {
        ScoreVector res = new ScoreVector(scoringVector.size());
        res = res.cloneAndSetCandidate(candidate, 1);
        return res;
    }

    @Override
    public ArrayList<Integer> getWinnersOfPrefVectors(ScoreVector s, int numAlternatives) {
        //Gets the winners if each preference got s(i) no of votes
        //preferences in s ordered lexicographically
        ArrayList<Integer> res = new ArrayList<>();
        int block = s.getLength() / numAlternatives;
        int maxVotes = 0;
        int index = 0;
        for (int i = 1; i <= numAlternatives; i++){
            int cVotes = 0;
            for (int j = 0; j < block; j++) {
                cVotes += s.get(index);
                index++;
            }
            if (cVotes > maxVotes) {
                res.clear(); res.add(i); maxVotes = cVotes;
            }
            else if (cVotes != 0 && cVotes == maxVotes) {
                res.add(i);
            }
        }
        return res;
    }

    /*@Override
    public int getWinners(ScoreVector scores) {
        ArrayList<Integer> winners = new ArrayList<>();
        int maxVotes = 0;
        for (int candidate = 1; candidate <= scores.getLength(); candidate++) {
            if (scores.getCandidate(candidate) > maxVotes) {
                winners.clear(); winners.add(candidate); maxVotes = scores.getCandidate(candidate);
            }
            else if (scores.getCandidate(candidate) == maxVotes) {
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
