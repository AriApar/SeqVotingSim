package Model;

import Elections.Election;
import Elections.ElectionParameters;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by AriApar on 13/11/2015.
 */
public interface VotingRule {

    //Truthful
    ScoreVector voteTruthful(Preferences pref);

    ScoreVector vote(int candidate);

    ArrayList<Integer> getWinners(ScoreVector s);

    ArrayList<Integer> getWinnersOfPrefVectors(ScoreVector s, ElectionParameters params);

    ScoreVector compilationFunction(ScoreVector state, ScoreVector vote, ElectionParameters params);

    Set<ScoreVector> generateStatesForLevel(int level, ElectionParameters params);

    int getCompilationStateSize(ElectionParameters params);

    ArrayList<ScoreVector> generateEVectors(ElectionParameters params);

}
