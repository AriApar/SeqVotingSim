package Model;

import Elections.ElectionParameters;

import java.util.ArrayList;

/**
 * Created by AriApar on 13/11/2015.
 */
public interface VotingRule {

    //Truthful
    public ScoreVector voteTruthful(Preferences pref);

    public ScoreVector vote(int candidate);

    public ArrayList<Integer> getWinners(ScoreVector s);

    ArrayList<Integer> getWinnersOfPrefVectors(ScoreVector s, ElectionParameters params);

}
