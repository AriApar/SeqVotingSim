package Model;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.set.TIntSet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by AriApar on 26/11/2015.
 *
 * Immutable ScoreVector
 *
 */
public class ScoreVector implements Serializable {

    /*private int[] scores;
    //private TIntIntHashMap scores;
    //private int numCandidates;

    public ScoreVector(int numCandidates) {
        this.scores = new int[numCandidates];
        //this.numCandidates = numCandidates;
    }

    public ScoreVector(int[] scores) {
        this.scores = scores;
        //this.numCandidates = scores.size();
    }

    *//*public void add(ScoreVector voteVector) {
        assert voteVector.getLength() == scores.length;
        for (int i = 0; i < scores.length; i++) {
            scores[i] += voteVector.get(i);
        }
    }*//*

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

    *//*public void set(int index, int value) {
        assert index >= 0 && index < scores.length;
        scores[index] = value;
    }*//*

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ScoreVector that = (ScoreVector) o;

        return Arrays.equals(scores, that.scores);

    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(scores);
    }

    public ScoreVector cloneAndSetCandidate(int candidate, int value) {
        assert candidate > 0 && candidate <= scores.length;
        return cloneAndSet(candidate-1, value);
    }

    @Override
    public String toString() {
        return Arrays.toString(scores);
    }

    public int getSum() {
        int sum = 0;
        for(int i = 0; i< scores.length; i++) sum += scores[i];
        return sum;
    }*/

    //private int[] scores;
    private TIntIntMap scores;
    private int numCandidates;

    public ScoreVector(int numCandidates) {
        this.scores = new TIntIntHashMap();
        this.numCandidates = numCandidates;
    }

    public ScoreVector(TIntIntMap map, int numCandidates) {
        this.scores = map;
        this.numCandidates = numCandidates;
    }

    public ScoreVector addImmutable(ScoreVector voteVector) {
        assert (voteVector.getLength() == getLength());
        TIntIntMap resMap = new TIntIntHashMap(voteVector.getRepresentation());
        int[] keys = scores.keys();
        for (int i = 0; i < scores.size() ; i++) {
            int key = keys[i];
            int value = scores.get(key);
            int newValue = resMap.adjustOrPutValue(key, value, value);
            // if new value is 0 remove from map!
            if(newValue == 0) resMap.remove(i);
        }
        return new ScoreVector(resMap, getLength());
    }

    public int getLength() {
        return numCandidates;
    }

    public int get(int i) {
        assert (i >= 0 && i < getLength());
        return scores.get(i);
    }

    public int getCandidate(int candidate) {
        assert candidate > 0 && candidate <= getLength();
        return get(candidate - 1);
    }

    /*public void set(int index, int value) {
        assert index >= 0 && index < scores.length;
        scores[index] = value;
    }*/

    public ScoreVector cloneAndSet(int index, int value) {
        assert (index >= 0 && index < getLength());
        TIntIntMap resMap = new TIntIntHashMap(scores);
        if(value == 0) resMap.remove(index);
        else resMap.put(index, value);

        return new ScoreVector(resMap, getLength());
    }

    public TIntIntMap getRepresentation() {
        return new TIntIntHashMap(scores);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ScoreVector that = (ScoreVector) o;

        if (numCandidates != that.numCandidates) return false;
        return scores != null ?
                Arrays.equals(scores.keys(), that.scores.keys()) && Arrays.equals(scores.values(), that.scores.values()) :
                that.scores == null;

    }

    @Override
    public int hashCode() {
        int result = scores != null ? scores.hashCode() : 0;
        result = 31 * result + numCandidates;
        return result;
    }

    public ScoreVector cloneAndSetCandidate(int candidate, int value) {
        assert candidate > 0 && candidate <= getLength();
        return cloneAndSet(candidate-1, value);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("[");
        for (int i = 0; i < getLength() - 1; i++) {
            str.append(scores.get(i) + ", ");
        }
        str.append(scores.get(getLength() -1) + "]");
        return str.toString();
    }

    public int getSum() {
        int sum = 0;
        int[] values = scores.values();
        for(int i = 0; i< values.length; i++) sum += values[i];
        return sum;
    }

}
