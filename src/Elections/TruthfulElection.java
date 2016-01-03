package Elections;

import Model.*;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by AriApar on 25/11/2015.
 */
public class TruthfulElection extends Election {

    //private PreferenceList pref;
    //private ArrayList<Voter> voters;
    //private VotingRule rule;
    //private ScoreVector scores;

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
            scores = scores.addImmutable(v.vote());
        }

        return getUniqueWinner();
    }
}
