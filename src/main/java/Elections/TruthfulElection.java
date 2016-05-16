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
    public ArrayList<ElectionState> findNE()  {
        ScoreVector scores = new ScoreVector(getParams().getPref().getNumCandidates());
        ArrayList<Integer> votes = new ArrayList<>();
        for(Voter v : getVoters()) {
            Vector voteVector = v.vote();
            scores = scores.add(voteVector);
            votes.add(getParams().getRule().getWinnersOfVoteVector(voteVector, getParams()).get(0));
        }
        ArrayList<ElectionState> res = new ArrayList<>();
        res.add(new ElectionState(scores, getWinnersOfScoreVector(scores), votes));
        return res;
    }
}
