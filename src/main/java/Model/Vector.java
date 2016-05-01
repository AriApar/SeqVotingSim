package Model;


import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by AriApar on 26/11/2015.
 *
 * Immutable Vector
 *
 */
public class Vector implements Serializable {

    private int[] scores;
    private int hashCode;
    //private TIntIntHashMap scores;
    //private int numCandidates;

    public Vector(int numCandidates) {
        this.scores = new int[numCandidates];
        //this.numCandidates = numCandidates;
    }

    public Vector(int[] scores) {
        this.scores = scores;
        //this.numCandidates = scores.size();
    }

    /*public Vector(Integer[] arr, int numCandidates) {
        this.scores = new int[numCandidates];
        for (int i =0; i< arr.length; i++){
            if (arr[i] > 0) this.scores[i] = arr[i];
        }
    }*/

    public Vector add(Vector voteVector) {
        assert (voteVector.getLength() == scores.length);
        int[] resArr = new int[scores.length];
        for (int i = 0; i < scores.length; i++) {
            resArr[i] = scores[i] + voteVector.get(i);
        }
        return new Vector(resArr);
    }

    public int getLength() {
        return scores.length;
    }

    public int get(int i) {
        assert (i >= 0 && i < scores.length);
        return scores[i];
    }

    public Vector cloneAndSet(int index, int value) {
        assert (index >= 0 && index < scores.length);
        int[] resArr = scores.clone();
        resArr[index] = value;
        return new Vector(resArr);
    }

    public int[] getScores() {
        return scores;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vector that = (Vector) o;
        //if (hashCode() != that.hashCode()) return false;
        if (hashCode() != that.hashCode()) return false;
        return Arrays.equals(scores, that.scores);

    }

    @Override
    public int hashCode() {
        if (hashCode == 0) {
            hashCode = Arrays.hashCode(scores);
        }
        return hashCode;
    }



    @Override
    public String toString() {
        return Arrays.toString(scores);
    }

    public int getSum() {
        int sum = 0;
        for(int i = 0; i< scores.length; i++) sum += scores[i];
        return sum;
    }

    //private int[] scores;
    /*private Int2IntOpenHashMap scores;
    private int numCandidates;

    private int hashCode = 0;

    public Vector(int numCandidates) {
        this.scores = new Int2IntOpenHashMap(numCandidates);
        scores.defaultReturnValue(0);
        this.numCandidates = numCandidates;
    }

    public Vector(Int2IntOpenHashMap map, int numCandidates) {
        this.scores = (map != null) ? map : new Int2IntOpenHashMap(numCandidates);
        scores.defaultReturnValue(0);
        this.numCandidates = numCandidates;
    }

    public Vector(int[] arr) {
        this.scores = new Int2IntOpenHashMap(arr.length);
        scores.defaultReturnValue(0);
        for (int i =0; i< arr.length; i++){
            if (arr[i] > 0) this.scores.put(i, (int) arr[i]);
        }
        this.numCandidates = arr.length;
        arr = null;
    }

    public Vector add(Vector voteVector) {
        if (voteVector.getLength() != getLength()) throw new AssertionError("vector sizes not equal on add");
        Int2IntOpenHashMap resMap = new Int2IntOpenHashMap(voteVector.getRepresentation());
        //resMap.defaultReturnValue(0);
        ObjectSet<Map.Entry<Integer, Integer>> entrySet = scores.entrySet();
        for (Map.Entry<Integer, Integer> entry : entrySet) {
            int key = entry.getKey();
            int value = entry.getValue();
            int newValue = resMap.addTo(key, value);
            // if new value is 0 remove from map!
            if(resMap.get(key) == 0) resMap.remove(key);
        }
        *//*int[] keys = scores.keySet().toIntArray();
        for (int i = 0; i < scores.size() ; i++) {
            int key = keys[i];
            int value = scores.get(key);
            int newValue = resMap.addTo(key, value);
            // if new value is 0 remove from map!
            if(newValue == 0) resMap.remove(key);
        }*//*
        return new Vector(resMap, getLength());
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

    public Vector cloneAndSet(int index, int value) {
        assert (index >= 0 && index < getLength());
        Int2IntOpenHashMap resMap = new Int2IntOpenHashMap(scores);
        //resMap.defaultReturnValue(0);
        if(value == 0) resMap.remove(index);
        else resMap.put(index, value);

        return new Vector(resMap, getLength());
    }

    public Int2IntOpenHashMap getRepresentation() {
        return scores;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vector that = (Vector) o;

        if (hashCode() != that.hashCode()) return false;
        if (scores.size() != that.scores.size()) return false;
        return  //Arrays.equals(scores.keys(), that.scores.keys()) && Arrays.equals(scores.values(), that.scores.values()) :
                scores.equals(that.scores) ;//&& numCandidates == that.numCandidates :

    }

    @Override
    public int hashCode() {
        if (hashCode == 0) {

            int result = 11;//17 * numCandidates;
            result = scores != null ? result * 31 + scores.hashCode() : result;
            hashCode = result;
        }
        return hashCode;
    }

    public Vector cloneAndSetCandidate(int candidate, int value) {
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
        int[] values = scores.values().toIntArray();
        for(int i = 0; i< values.length; i++) sum += values[i];
        return sum;
    }*/


}
