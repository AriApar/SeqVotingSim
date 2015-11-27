package Model;

import java.util.ArrayList;

/**
 * Created by AriApar on 26/11/2015.
 */
public class ScoreVector {

    private int[] scores;
    //private int numCandidates;

    public ScoreVector(int numCandidates) {
        this.scores = new int[numCandidates];
        //this.numCandidates = numCandidates;
    }

    public ScoreVector(int[] scores) {
        this.scores = scores;
        //this.numCandidates = scores.size();
    }

    public void add(ScoreVector voteVector) {
        assert voteVector.getLength() == scores.length;
        for (int i = 0; i < scores.length; i++) {
            scores[i] += voteVector.get(i);
        }
    }

    public int getLength() {
        return scores.length;
    }

    public int get(int i) {
        assert i >= 0 && i < scores.length;
        return scores[i];
    }

    public int getCandidate(int candidate) {
        assert candidate > 0 && candidate <= scores.length;
        return scores[candidate-1];
    }

    public void set(int index, int value) {
        assert index >= 0 && index < scores.length;
        scores[index] = value;
    }

    public void setCandidate(int candidate, int value) {
        assert candidate > 0 && candidate <= scores.length;
        scores[candidate-1] = value;
    }

}
