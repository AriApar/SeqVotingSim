package Model;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by AriApar on 26/11/2015.
 *
 * Immutable ScoreVector
 *
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

    /*public void add(ScoreVector voteVector) {
        assert voteVector.getLength() == scores.length;
        for (int i = 0; i < scores.length; i++) {
            scores[i] += voteVector.get(i);
        }
    }*/

    public ScoreVector addImmutable(ScoreVector voteVector) {
        assert (voteVector.getLength() == scores.length);
        int[] resArr = new int[scores.length];
        for (int i = 0; i < scores.length; i++) {
            resArr[i] = scores[i] + voteVector.get(i);
        }
        return new ScoreVector(resArr);
    }

    public int getLength() {
        return scores.length;
    }

    public int get(int i) {
        assert (i >= 0 && i < scores.length);
        return scores[i];
    }

    public int getCandidate(int candidate) {
        assert candidate > 0 && candidate <= scores.length;
        return scores[candidate-1];
    }

    /*public void set(int index, int value) {
        assert index >= 0 && index < scores.length;
        scores[index] = value;
    }*/

    public ScoreVector cloneAndSet(int index, int value) {
        assert (index >= 0 && index < scores.length);
        int[] resArr = scores.clone();
        resArr[index] = value;
        return new ScoreVector(resArr);
    }

    public int[] getRepresentation() {
        return scores.clone();
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(scores) * 31;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ScoreVector)) return false;
        ScoreVector that = (ScoreVector) obj;
        return Arrays.equals(scores, that.scores);
    }

    public ScoreVector cloneAndSetCandidate(int candidate, int value) {
        assert candidate > 0 && candidate <= scores.length;
        return cloneAndSet(candidate-1, value);
    }

    @Override
    public String toString() {
        return Arrays.toString(scores);
    }

}
