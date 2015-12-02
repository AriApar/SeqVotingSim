package Elections;

import Model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by AriApar on 01/12/2015.
 */
public class DPElection extends Election{
    //Dynamic programming election that follows Algorithm 1 from Stackelberg paper.

    public DPElection(PreferenceList pref, VotingOrder order, VotingRule rule) {
        this.pref = pref;
        this.order = order;
        this.voters = new ArrayList<>();
        for (int voterId : order) {
            this.voters.add(new Voter(voterId, rule, order, pref));
        }
        this.rule = rule;
        scores = new ScoreVector(pref.getNumCandidates());
    }

    @Override
    public int run() throws Exception {
        throw new Exception("YOU WERE SUPPOSED TO USE runNH!");

    }
    public Set<ArrayList<Integer>> runNE() {
        HashMap<ScoreVector, LinkedHashSet<ArrayList<Integer>>> g = new HashMap<>();
        int numAlternatives = pref.getNumCandidates();
        int numAltFactorial = factorial(numAlternatives);
        final ArrayList<ScoreVector> EVector = generateEVectors(numAltFactorial);
        for (int j = numAlternatives +1; j >=1; j--) {
            ArrayList<ScoreVector> states = generatePossibleScoresAtLevel(j, numAltFactorial);
            for (ScoreVector s : states) {
                if (j == numAlternatives + 1) {
                    LinkedHashSet<ArrayList<Integer>> winnerSet = new LinkedHashSet<>();
                    winnerSet.add(rule.getWinnersOfPrefVectors(s));
                    g.put(s, winnerSet);
                }
                else if (j < numAlternatives + 1) {
                    double bestPref = Double.MAX_VALUE;
                    ArrayList<ScoreVector> optimum_e = new ArrayList<>();
                    for (ScoreVector e : EVector) {
                        ScoreVector gSum = s.addImmutable(e);
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
                        if(!g.containsKey(s)) {
                            g_of_sPlusE.addAll(g.get(s));
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

    private ArrayList<ScoreVector> generatePossibleScoresAtLevel(int level, int size) {
        if (level == 1) {
            ArrayList<ScoreVector> s = new ArrayList<>();
            s.add(new ScoreVector(new int[size]));
            return s;
        }
        else {
            ArrayList<ScoreVector> scoreSet = generatePossibleScoresAtLevel(level - 1, size);
            ArrayList<ScoreVector> resSet = new ArrayList<>(scoreSet.size()*size);
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
