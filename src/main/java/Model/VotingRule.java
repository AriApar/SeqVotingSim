package Model;

import Elections.ElectionParameters;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AriApar on 13/11/2015.
 */
public interface VotingRule {
    //Should return a vote vector
    Vector voteTruthful(Preferences pref);
    //Should return a vote vector
    Vector vote(int candidate);
    //Returns winner(s) of the given score vector
    ArrayList<Integer> getWinnersOfScoreVector(ScoreVector s, ElectionParameters params);
    //Returns winner(s) of a given vote vector
    ArrayList<Integer> getWinnersOfVoteVector(Vector s, ElectionParameters params);
    //Returns winner(s) of a state vector
    ArrayList<Integer> getWinnersOfStateVector(Vector s, ElectionParameters params);
    //Returns the new state once the new vote is applied to the old state
    Vector compilationFunction(Vector state, Vector vote, ElectionParameters params);
    //Returns a list of vectors representing the states for level i,
    //where level = count of votes cast + 1 (therefore 1-indexed).
    List<Vector> generateStatesForLevel(int level, ElectionParameters params);
    //Returns the length of the state vectors used
    int getCompilationStateSize(ElectionParameters params);
    //Returns the vectors that represent each possible vote a voter could cast
    ArrayList<Vector> generateEVectors(ElectionParameters params);
}
