package Elections;

import Model.*;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by AriApar on 25/11/2015.
 */
public class TruthfulElection extends Election {

    private PreferenceList pref;
    private ArrayList<Voter> voters;
    private VotingRule rule;
    private ScoreVector scores;

    public TruthfulElection(PreferenceList pref, VotingOrder order, VotingRule rule) {
        this.pref = pref;
        this.voters = new ArrayList<>();
        for (int voterId : order) {
            this.voters.add(new Voter(voterId, rule, order, pref));
        }
        this.rule = rule;
        scores = new ScoreVector(pref.getNumCandidates());
    }

    public int run() {
        for(Voter v : voters) {
            scores.add(v.vote());
        }

        return getWinner();
    }

    private int getWinner() {
        ArrayList<Integer> winners = new ArrayList<>();
        int maxVotes = 0;
        for (int candidate = 1; candidate <= pref.getNumCandidates(); candidate++) {
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
    }

    public int getScore(int candidate) {
        return scores.getCandidate(candidate);
    }
}
