package Elections;

import Model.*;

import java.util.ArrayList;

/**
 * Created by AriApar on 25/11/2015.
 */
public class TruthfulElection extends Election {

    //private PreferenceList pref;
    //private ArrayList<Voter> voters;
    //private VotingRule rule;
    //private Vector scores;

    protected TruthfulElection(ElectionParameters params) {
        setElection(params);
        //scores = new Vector(pref.getNumCandidates());
    }

    public ScoreVector run() {
        ScoreVector scores = new ScoreVector(getParams().getPref().getNumCandidates());
        for(Voter v : getVoters()) {
            scores = scores.add(v.vote());
        }
        return scores;
    }

    @Override
    public ArrayList<ElectionState> findNE() throws Exception {
        throw new UnsupportedOperationException("TruthfulElection is executed via run() method");
    }
}
