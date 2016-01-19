package Elections;

import Model.*;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by AriApar on 01/12/2015.
 */
public class DPElection extends Election{
    //Dynamic programming election that follows Algorithm 1 from Stackelberg paper.
    private boolean abstention;
    private boolean cost;
    private ArrayList<Voter> voters;
    private final double COST_OF_VOTING = 0.01D;

    private class DPInfo {
        private ArrayList<Integer> winners;
        private ScoreVector prefE;

        public DPInfo(ArrayList<Integer> winners, ScoreVector prefVector) {
            this.winners = winners;
            this.prefE = prefVector;
        }

        public ArrayList<Integer> getWinners() {
            return winners;
        }

        public ScoreVector getE() {
            return prefE;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            DPInfo dpInfo = (DPInfo) o;

            if (winners != null ? !winners.equals(dpInfo.winners) : dpInfo.winners != null) return false;
            return prefE != null ? prefE.equals(dpInfo.prefE) : dpInfo.prefE == null;

        }

        @Override
        public int hashCode() {
            int result = winners != null ? winners.hashCode() : 0;
            result = 31 * result + (prefE != null ? prefE.hashCode() : 0);
            return result;
        }
    }

    private class Triple<X, Y, Z> {
        public final X first;
        public final Y second;
        public final Z third;
        public Triple(X x, Y y, Z z) {
            this.first = x;
            this.second = y;
            third = z;
        }
    }

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

    /*public Set<ArrayList<Integer>> findNEs() throws Exception{
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
    }*/

    public ArrayList<ElectionState> findNE() throws Exception{
        HashMap<ScoreVector, Set<DPInfo>> g = new HashMap<>();
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

        return generateWinnerStates(g, g.get(generateZeroVector(numAltFactorial)), numAlternatives, numAltFactorial);
    }

    private ArrayList<ElectionState> generateWinnerStates(HashMap<ScoreVector, Set<DPInfo>> g,
                                                          Set<DPInfo> dpInfos, int numAlternatives,
                                                          int numAltFactorial) throws Exception {
        //todo: UGLY AS HELL
        Queue<Triple<ElectionState, DPInfo, ScoreVector>> q = new LinkedList<>();
        Set<ElectionState> res = new HashSet<>();
        for (DPInfo item : dpInfos) {
            ScoreVector key = generateZeroVector(numAltFactorial);
            ElectionState initState = new ElectionState(numAlternatives);
            q.add(new Triple(initState, item, key));

        }
        //Initial queue done, now start processing it
        while (!q.isEmpty()) {
            Triple<ElectionState, DPInfo, ScoreVector> tuple = q.remove();
            ElectionState state = tuple.first;
            DPInfo info = tuple.second;
            ScoreVector key = tuple.third;

            if (info.getE() != null) {
                ScoreVector e = info.getE();

                ArrayList<Integer> candArray = getParams().getRule().getWinnersOfPrefVectors(e, getParams());
                ElectionState newState = null;
                if (candArray.size() == 0) {
                    //abstention
                    newState = prepNewState(state, 0);
                }
                else {
                    int candidate = candArray.get(0);
                    newState = prepNewState(state, candidate);
                }

                key = key.addImmutable(e);
                Set<DPInfo> newInfos = g.get(key);

                for (DPInfo item : newInfos) {
                    q.add(new Triple<>(newState, item, key));
                }
            }
            else {
                // key null, put the resulting state in the return array
                if (Arrays.equals(state.getCurrentWinners().toArray(), info.getWinners().toArray()))
                    res.add(state);
                else throw new Exception("winner states from mapping not consistent with generated electionstate");
            }
        }
        return new ArrayList<>(res);
    }

    private ElectionState prepNewState(ElectionState state, int candidate) {
        //Get old electionstate values to generate new one
        //ScoreVector
        ScoreVector vector = state.getCurrentScores();
        ArrayList<Integer> votes = (ArrayList<Integer>) state.getCurrentVotes().clone();
        votes.add(candidate);

        if (candidate == 0) {
            //abstention
            return new ElectionState(vector, state.getCurrentWinners(), votes, candidate);
        }
        else {
            //add vote to scorevector
            vector = vector.cloneAndSetCandidate(candidate, vector.getCandidate(candidate) + 1);
            return new ElectionState(vector, getWinners(vector), votes, candidate);
        }
    }

    private void getWinnersElseCase(HashMap<ScoreVector, Set<DPInfo>> g,
                                    ArrayList<ScoreVector> EVector,
                                    int j, ScoreVector s) throws Exception {
        double bestPref = Double.MIN_VALUE;
        ArrayList<ScoreVector> optimum_e = new ArrayList<>();

        for (ScoreVector e : EVector) {
            boolean abs = (abstention && e.get(e.getLength()-1) == 1);
            double cost = (!abs && getParams().hasCost()) ? COST_OF_VOTING : 0D;
            ArrayList<Integer> voteCast = getParams().getRule().getWinnersOfPrefVectors(e, getParams());
            //todo: change back depending on edith
            double indUtil = (voteCast.size() == 0) ? 0D :
                    voters.get(j-1).getCombinedUtilityForCandidates(voteCast) * 0.00001D;

            ScoreVector gSum = s.addImmutable(e);

            Set<DPInfo> cStates = g.get(gSum);
            ArrayList<Integer> cWinners = cStates.iterator().next().getWinners();
            //because we only need the rank of the current winners, getting only one is fine as
            //all winners will have same rank if they're all optimal

            double cPref = voters.get(j-1).getCombinedUtilityForCandidates(cWinners);
            cPref = cPref - cost;
            //double totalVoteCost = (abstention) ? getNonAbstentionCount(s) * COST_OF_VOTING : 0D;
            //todo: remove indUtil depending on edith
            int comparison = Double.compare(cPref //+ indUtil
                    , bestPref);
            if(comparison == 0) {
                // add this to current set of optimum e
                optimum_e.add(e);
            }
            else if (comparison > 0) {
                // new util more, trash old optimum, add this to new
                optimum_e.clear(); optimum_e.add(e); bestPref = cPref;
            }
        }

        updateMappingWithOptima(g, s, optimum_e);
    }

    private int getNonAbstentionCount(ScoreVector s) {
        int sum = 0;
        for (int i = 0; i < s.getLength() -1; i++) {
            sum += s.get(i);
        }
        return sum;
    }
    private void updateMappingWithOptima(HashMap<ScoreVector, Set<DPInfo>> g, ScoreVector s, ArrayList<ScoreVector> optimum_e) {
        for (ScoreVector e : optimum_e) {
            ScoreVector sPlusE = s.addImmutable(e);
            Set<DPInfo> g_of_sPlusE = g.get(sPlusE);
            //prepare new DPInfo's
            g_of_sPlusE = prepNewInfos(g_of_sPlusE, e);
            if(g.containsKey(s)) {

                for (DPInfo item : g.get(s)) g_of_sPlusE.add(item);
                g.put(s, g_of_sPlusE);
            }
            else {
                g.put(s, g_of_sPlusE);
            }
        }
    }

    private Set<DPInfo> prepNewInfos(Set<DPInfo> g_of_sPlusE, ScoreVector e) {
        //Same winners, new e for profiling later on
        Set<DPInfo> res = new HashSet<>();
        for (DPInfo item : g_of_sPlusE) {
            res.add(new DPInfo(item.getWinners(), e));
        }
        return res;
    }

    private void getWinnersBaseCase(HashMap<ScoreVector, Set<DPInfo>> g, ScoreVector s) {
        Set<DPInfo> res = new HashSet<>();
        ArrayList<Integer> winners = getParams().getRule().getWinnersOfPrefVectors(s, getParams());
        res.add(new DPInfo(winners, null));
        g.put(s, res);
    }

    /*private void getWinnersElseCase(HashMap<ScoreVector, LinkedHashSet<ArrayList<Integer>>> g,
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
    }*/

    /*private void updateMappingWithOptima(HashMap<ScoreVector, LinkedHashSet<ArrayList<Integer>>> g, ScoreVector s, ArrayList<ScoreVector> optimum_e) {
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
    }*/

    /*private void getWinnersBaseCase(HashMap<ScoreVector, LinkedHashSet<ArrayList<Integer>>> g, ScoreVector s) {
        LinkedHashSet<ArrayList<Integer>> winnerSet = new LinkedHashSet<>();
        winnerSet.add(getParams().getRule().getWinnersOfPrefVectors(s, getParams()));
        g.put(s, winnerSet);
    }*/

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
