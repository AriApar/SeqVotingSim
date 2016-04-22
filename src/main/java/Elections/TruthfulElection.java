package Elections;

import Model.*;

/**
 * Created by AriApar on 25/11/2015.
 */
public class TruthfulElection extends Election {

    //private PreferenceList pref;
    //private ArrayList<Voter> voters;
    //private VotingRule rule;
    //private ScoreVector scores;

    protected TruthfulElection(ElectionParameters params) {
        setElection(params);
        //scores = new ScoreVector(pref.getNumCandidates());
    }

    public ScoreVector run() {
        ScoreVector scores = new ScoreVector(getParams().getPref().getNumCandidates());
        for(Voter v : getVoters()) {
            scores = scores.addImmutable(v.vote());
        }
        return scores;
    }
}
