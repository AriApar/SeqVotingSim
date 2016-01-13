package Elections;

import Model.*;

import java.util.*;

/**
 * Created by AriApar on 01/12/2015.
 */
public class DPElection extends Election{
    //Dynamic programming election that follows Algorithm 1 from Stackelberg paper.
    private boolean abstention;
    private boolean cost;
    private ArrayList<Voter> voters;

    protected DPElection(ElectionParameters params) {
        setElection(params);
        abstention = params.canAbstain();
        cost = params.hasCost();
        voters = getVoters();
    }

    /*public Set<ArrayList<Integer>> findNE() throws Exception{

        HashMap<ScoreVector, LinkedHashSet<ArrayList<Integer>>> g = new HashMap<>();
        int numAlternatives = getParams().numberOfCandidates();
        int numVoters = voters.size();
        int numAltFactorial = factorial(numAlternatives);


        final ArrayList<ScoreVector> EVector = generateEVectors(numAltFactorial);
        for (int j = numVoters +1; j >=1; j--) {
            Set<ScoreVector> states = generatePossibleScoresAtLevel(j, numAltFactorial);
            for (ScoreVector s : states) {
                if (j == numVoters + 1) {

                    LinkedHashSet<ArrayList<Integer>> winnerSet = new LinkedHashSet<>();
                    winnerSet.add(getParams().getRule().getWinnersOfPrefVectors(s, getParams()));
                    g.put(s, winnerSet);
                }
                else {

                    double bestPref = Double.MAX_VALUE;
                    ArrayList<ScoreVector> optimum_e = new ArrayList<>();
                    for (ScoreVector e : EVector) {
                        ScoreVector gSum = s.addImmutable(e);
                        //System.out.println(gSum);
                        ArrayList<Integer> cWinners = g.get(gSum).iterator().next();
                        //because we only need the rank of the current winners, getting only one is fine as
                        //all winners will have same rank if they're all optimal
                        double cPref = voters.get(j-1).getCombinedPreferenceForCandidates(cWinners);
                        int comparison = Double.compare(cPref, bestPref);
                        if(comparison == 0) {
                            // add this to current set of optimum e
                            optimum_e.add(e);
                        }
                        else if (comparison < 0) {
                            // new util less, trash old optimum, add this to new
                            optimum_e.clear(); optimum_e.add(e); bestPref = cPref;
                        }
                    }
                    for (ScoreVector e : optimum_e) {
                        ScoreVector sPlusE = s.addImmutable(e);
                        LinkedHashSet<ArrayList<Integer>> g_of_sPlusE = g.get(sPlusE);
                        if(g.containsKey(s)) {
                            //System.out.println(s.toString());
                            for (ArrayList<Integer> item : g.get(s)) g_of_sPlusE.add(item);
                            g.put(s, g_of_sPlusE);
                        }
                        else {
                            g.put(s, g_of_sPlusE);
                        }
                    }
                }
            }
        }
        return g.get(generateZeroVector(numAltFactorial));
    }*/

    public Set<ArrayList<Integer>> findNEs() throws Exception{
        HashMap<ScoreVector, LinkedHashSet<ArrayList<Integer>>> g = new HashMap<>();
        int numAlternatives = getParams().numberOfCandidates();
        int numVoters = voters.size();
        int numAltFactorial = factorial(numAlternatives);
        //add abstention possibility
        if (abstention) numAltFactorial += 1;
        final ArrayList<ScoreVector> EVector =  generateEVectors(numAltFactorial);
        for (int j = numVoters +1; j >=1; j--) {
            Set<ScoreVector> states = generatePossibleScoresAtLevel(j, numAltFactorial);
            for (ScoreVector s : states) {
                if (j == numVoters + 1) {

                    getWinnersBaseCase(g, s);
                }
                else {

                    getWinnersElseCase(g, EVector, j, s);
                }
            }
        }

        return g.get(generateZeroVector(numAltFactorial));
    }

    private void getWinnersElseCase(HashMap<ScoreVector, LinkedHashSet<ArrayList<Integer>>> g,
                                    ArrayList<ScoreVector> EVector,
                                    int j, ScoreVector s) throws Exception {
        double bestPref = Double.MAX_VALUE;
        ArrayList<ScoreVector> optimum_e = new ArrayList<>();

        for (ScoreVector e : EVector) {
            boolean abs = (abstention && e.get(e.getLength()-1) == 1);
            double cost = ((abs && getParams().hasCost()) ? 0.01D : 0D);
            ScoreVector gSum = s.addImmutable(e);

            ArrayList<Integer> cWinners = g.get(gSum).iterator().next();
            //because we only need the rank of the current winners, getting only one is fine as
            //all winners will have same rank if they're all optimal

            double cPref = voters.get(j-1).getCombinedPreferenceForCandidates(cWinners);
            int comparison = Double.compare(cPref - cost, bestPref);
            if(comparison == 0) {
                // add this to current set of optimum e
                optimum_e.add(e);
            }
            else if (comparison < 0) {
                // new util less, trash old optimum, add this to new
                optimum_e.clear(); optimum_e.add(e); bestPref = cPref;
            }
        }

        updateMappingWithOptima(g, s, optimum_e);
    }

    private void updateMappingWithOptima(HashMap<ScoreVector, LinkedHashSet<ArrayList<Integer>>> g, ScoreVector s, ArrayList<ScoreVector> optimum_e) {
        for (ScoreVector e : optimum_e) {
            ScoreVector sPlusE = s.addImmutable(e);
            LinkedHashSet<ArrayList<Integer>> g_of_sPlusE = g.get(sPlusE);
            if(g.containsKey(s)) {

                for (ArrayList<Integer> item : g.get(s)) g_of_sPlusE.add(item);
                g.put(s, g_of_sPlusE);
            }
            else {
                g.put(s, g_of_sPlusE);
            }
        }
    }

    private void getWinnersBaseCase(HashMap<ScoreVector, LinkedHashSet<ArrayList<Integer>>> g, ScoreVector s) {
        LinkedHashSet<ArrayList<Integer>> winnerSet = new LinkedHashSet<>();
        winnerSet.add(getParams().getRule().getWinnersOfPrefVectors(s, getParams()));
        g.put(s, winnerSet);
    }

    private ArrayList<ScoreVector> generateEVectors(int size) {
        ArrayList<ScoreVector> res = new ArrayList<>(size);
        ScoreVector zeroVector = generateZeroVector(size);
        for (int j = 0; j < size; j++) {
            ScoreVector e = zeroVector.cloneAndSet(j, 1);
            res.add(e);
        }
        return res;
    }

    /*
    private Set<ScoreVector> generatePossibleScoresAtLevel(int level, int size) {
        assert (level >= 1);
        Set<ScoreVector> scores = new HashSet<ScoreVector>();
        scores.add(new ScoreVector(new int[size]));
        for (int i = 2; i <=level; i++) {
            Set<ScoreVector> nextScores = new HashSet<>(scores.size()*size);
            for (ScoreVector s : scores) {
                for (int j = 0; j < size; j++) {
                    nextScores.add(s.cloneAndSet(j, s.get(j) + 1));
                }
            }
            scores = nextScores;
        }
        return scores;
    }
    */

    private Set<ScoreVector> generatePossibleScoresAtLevel(int level, int size) {
        if (level == 1) {
            Set<ScoreVector> s = new HashSet<>();
            s.add(new ScoreVector(new int[size]));
            return s;
        }
        else {
            Set<ScoreVector> scoreSet = generatePossibleScoresAtLevel(level - 1, size);
            Set<ScoreVector> resSet = new HashSet<>(scoreSet.size()*size);
            for (ScoreVector s : scoreSet) {
                for (int i = 0; i < size; i++) {
                    resSet.add(s.cloneAndSet(i, s.get(i) + 1));
                }
            }
            return resSet;
        }
    }

    private ScoreVector generateZeroVector(int size) {
        return new ScoreVector(size);
    }

    private int factorial(int n) {
        int fact = 1;
        for (int j = 1; j <=n; j++) fact *= j;
        return fact;
    }
}
