package VotingRules;

import Elections.ElectionParameters;
import Model.Preferences;
import Model.ScoreVector;
import Model.VotingRule;
import gnu.trove.set.hash.THashSet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


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
    public ScoreVector voteTruthful(Preferences pref) {
        /*ScoreVector res = new ScoreVector(pref.length());
        int candidateNo = pref.getNthPreference(1);
        res = res.cloneAndSetCandidate(candidateNo, 1);
        return res;*/
        return vote(pref.getNthPreference(1));
    }

    @Override
    public ScoreVector vote(int candidate) {
        ScoreVector res = new ScoreVector(scoringVector.size());
        res = res.cloneAndSetCandidate(candidate, 1);
        return res;
    }

    @Override
    public ArrayList<Integer> getWinnersOfPrefVectors(ScoreVector s, ElectionParameters params) {
        //Gets the winners if each preference got s(i) no of votes
        //preferences in s ordered lexicographically
        //if abstention is possible, there is an abstention vector at the end of scorevectors
        //no abstention
        if (!params.canAbstain()) {
            return calcWinnersOfPrefVectors(s, params.numberOfCandidates(), 0);
        }
        else {
            return calcWinnersOfPrefVectors(s, params.numberOfCandidates(), 1);
        }
    }

    @Override
    public ScoreVector compilationFunction(ScoreVector state, ScoreVector vote, ElectionParameters params) {
        //preferences in vote ordered lexicographically
        //if abstention is possible, there is an abstention vector at the end of scorevectors
        int altCount = params.numberOfCandidates();
        boolean abstain = params.canAbstain();
        int absCounter = abstain ? 1 : 0;
        ScoreVector res = null;
        int block = (vote.getLength() - absCounter) / altCount;
        for (int cNo = 0; cNo < altCount; cNo++) {
            for(int index = 0; index < block; index++) {
                int vectorIndex = cNo*block + index;

                if (vote.get(vectorIndex) == 1) {
                    int oldValue = state.getCandidate(cNo + 1);
                    res = state.cloneAndSetCandidate(cNo + 1, oldValue + 1 );
                }
            }
        }
        // if abstention is possible, res will still be null, so need to deal with that
        if (abstain && res == null) {
            int absIndex = state.getLength() - 1;
            int oldValue = state.get(absIndex);
            res = state.cloneAndSet(absIndex, oldValue + 1 );
        }

        return res;
    }

    @Override
    public Set<ScoreVector> generateStatesForLevel(int level, ElectionParameters params) {

        return generatePossibleScoresAtLevel(level, getCompilationStateSize(params));
    }

    @Override
    public int getCompilationStateSize(ElectionParameters params) {
        int altCount = params.numberOfCandidates();
        boolean abstain = params.canAbstain();
        int absCounter = abstain ? 1 : 0;
        return altCount + absCounter;
    }

    @Override
    public ArrayList<ScoreVector> generateEVectors(ElectionParameters params) {
        int altCount = params.numberOfCandidates();
        boolean abstain = params.canAbstain();
        int absCounter = abstain ? 1 : 0;

        int eSize = factorial(altCount) + absCounter;
        int block = (eSize - absCounter) / altCount;

        ArrayList<ScoreVector> res = new ArrayList<>();
        ScoreVector zeroVector = new ScoreVector(eSize);

        for (int j = 0; j < eSize; j++) {
            //Only set e to 1 once for each voter (once per altCount elements)
            if (j % block == 0) {
                // if abstention is false, then this will return true once per each
                // candidate
                // if true, then it will return true once per each cand.
                // and also once at the end, representing abstention
                ScoreVector e = zeroVector.cloneAndSet(j, 1);
                res.add(e);
            }
        }
        return res;
    }

    private Set<ScoreVector> generatePossibleScoresAtLevel(int level, int size) {
        assert (level >= 1);
        Set<ScoreVector> scores = new THashSet<ScoreVector>();
        scores.add(new ScoreVector(size));
        for (int i = 2; i <=level; i++) {
            Set<ScoreVector> nextScores = new THashSet<>(scores.size()*size*2);
            for (ScoreVector s : scores) {
                for (int j = 0; j < size; j++) {
                    nextScores.add(s.cloneAndSet(j, s.get(j) + 1));
                }
            }
            scores = nextScores;
            System.out.println("Generated scores for level " + i);
        }
        return scores;
    }

    private ArrayList<Integer> calcWinnersOfPrefVectors(ScoreVector s, int numAlternatives, int absCounter) {
        ArrayList<Integer> res = new ArrayList<>();
        int block = (s.getLength() - absCounter)/ numAlternatives;
        int maxVotes = 0;
        int index = 0;
        for (int i = 1; i <= numAlternatives; i++) {
            int cVotes = 0;
            for (int j = 0; j < block; j++) {
                cVotes += s.get(index);
                index++;
            }
            if (cVotes > maxVotes) {
                res.clear();
                res.add(i);
                maxVotes = cVotes;
            } else if (cVotes != 0 && cVotes == maxVotes) {
                res.add(i);
            }
        }
        return res;
    }

    public ArrayList<Integer> getWinners(ScoreVector scores) {
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
        return winners;
    }

    private int factorial(int n) {
        int fact = 1;
        for (int j = 1; j <=n; j++) fact *= j;
        return fact;
    }

}
