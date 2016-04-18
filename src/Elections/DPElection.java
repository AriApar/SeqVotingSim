package Elections;

import Model.*;

import java.io.*;
import java.util.*;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

/**
 * Created by AriApar on 01/12/2015.
 */
public class DPElection extends Election{
    //Dynamic programming election that follows Algorithm 1 from Stackelberg paper.
    private boolean abstention;
    private boolean cost;
    private ArrayList<Voter> voters;
    private final double COST_OF_VOTING = 5D;

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

    private String getFileName(int stageNo) {
        return "tmp/map_Stage_" + stageNo + ".ser";
    }

    private boolean saveMapForStage(Map<ScoreVector, Set<DPInfo>> map, int stageNo ) {
        try
        {
            File f = new File(getFileName(stageNo));
            FileOutputStream fileOut = new FileOutputStream(f);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(map);
            out.close();
            fileOut.close();
            System.out.println("Serialized data is saved in /tmp/map_Stage_" + stageNo + ".ser");
        }
        catch(IOException i)
        {
            i.printStackTrace();
            return false;
        }
        return true;
    }

    private Map<ScoreVector, Set<DPInfo>> getMapForStage(int stageNo) {
        try
        {
            File f = new File(getFileName(stageNo));
            FileInputStream fileIn = new FileInputStream(f);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            Map<ScoreVector, Set<DPInfo>> map = (Map<ScoreVector, Set<DPInfo>>) in.readObject();
            in.close();
            fileIn.close();
            System.out.println("Read map no " + stageNo + " from file");
            return map;
        }
        catch(IOException i)
        {
            i.printStackTrace();
            return null;
        }
        catch(ClassNotFoundException c)
        {
            System.out.println("Map class not found");
            c.printStackTrace();
            return null;
        }
    }

    public ArrayList<ElectionState> findNE() throws Exception{
        int numVoters = voters.size();

        int numAlternatives = getParams().numberOfCandidates();
        int numAltFactorial = factorial(numAlternatives);
        //add abstention possibility
        if (abstention) numAltFactorial += 1;

        //Map<ScoreVector, Set<DPInfo>> g = new THashMap<>();
        Map<ScoreVector, Set<DPInfo>> gMap = new THashMap<>();

        final ArrayList<ScoreVector> EVector =  getParams().getRule().generateEVectors(getParams()); //generateEVectors(numAltFactorial);
        Set<ScoreVector> states = null;
        for (int j = numVoters +1; j >=1; j--) {
            //Set<ScoreVector> states = generatePossibleScoresAtLevel(j, numAltFactorial);
            if (j == numVoters+1)
                states = getParams().getRule().generateStatesForLevel(j, getParams());
            else {
                states = getParams().getRule().generateStatesForLevel(j, getParams());
                //states = shrinkStatesBy1(states);
                System.out.println("Generated states for level " + j);
            }
            Map<ScoreVector, Set<DPInfo>> g = new THashMap<>(states.size());
            for (ScoreVector s : states) {

                if (j == numVoters + 1) {
                    getWinnersBaseCase(g, s);

                }
                else {
                    getWinnersElseCase(g, gMap, EVector, j, s);
                }
            }

            if (j != numVoters + 1) {
                writeToFileAndClear(gMap, j);
            }
            gMap.putAll(g);

        }

        //return generateWinnerStates(g, g.get(generateZeroVector(numAltFactorial)), numAlternatives, numAltFactorial);
        //return generateWinnerStates(gMap.get(generateZeroVector(numAltFactorial)), numAlternatives, numAltFactorial);
        int stateSize = getParams().getRule().getCompilationStateSize(getParams());
        return generateWinnerStates(gMap.get(generateZeroVector(stateSize)), numAlternatives, stateSize);
    }

    private Set<ScoreVector> shrinkStatesBy1(Set<ScoreVector> states) {
        Set<ScoreVector> res = new THashSet<>(states.size());
        for (ScoreVector state : states) {
            for (int i = 0; i < state.getLength(); i++) {
                int value = state.get(i);
                if (value != 0) res.add(state.cloneAndSet(i, value - 1));
            }
        }
        return res;
    }

    private ArrayList<ElectionState> generateWinnerStates(Set<DPInfo> dpInfos, int numAlternatives, int numAltFactorial) throws Exception{
        Queue<Triple<ElectionState, DPInfo, ScoreVector>> q = new LinkedList<>();
        Set<ElectionState> res = new THashSet<>(dpInfos.size() * 2);
        for (DPInfo item : dpInfos) {
            ScoreVector key = generateZeroVector(numAltFactorial);
            ElectionState initState = new ElectionState(numAlternatives);
            q.add(new Triple(initState, item, key));

        }
        Map<ScoreVector, Set<DPInfo>> currMap = null;
        int prevSum = -1; //level counter, so we don't reload the same level map
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

                key = getParams().getRule().compilationFunction(key, e, getParams());
                //optimisation
                if(key.getSum() != prevSum) {
                    int sum = key.getSum();
                    currMap = getMapForStage(sum);
                    prevSum = sum;
                }
                Set<DPInfo> newInfos = currMap.get(key);

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

    private void writeToFileAndClear(Map<ScoreVector, Set<DPInfo>> g, int j) {
        //Writes g to file and deletes it from the array
        saveMapForStage(g, j);
        g.clear();
    }

    /*private ArrayList<ElectionState> generateWinnerStates(Map<ScoreVector, Set<DPInfo>> g,
                                                          Set<DPInfo> dpInfos, int numAlternatives,
                                                          int numAltFactorial) throws Exception {
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
    }*/

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

    private void getWinnersElseCase(Map<ScoreVector, Set<DPInfo>> g,
                                    Map<ScoreVector, Set<DPInfo>> gLookup,
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
                    voters.get(j-1).getCombinedUtilityForCandidates(voteCast) / 10000D;

            //ScoreVector gSum = s.addImmutable(e);
            ScoreVector gSum = getParams().getRule().compilationFunction(s, e, getParams());

            Set<DPInfo> cStates = gLookup.get(gSum);
            ArrayList<Integer> cWinners = cStates.iterator().next().getWinners();
            //because we only need the rank of the current winners, getting only one is fine as
            //all winners will have same rank if they're all optimal

            double cPref = voters.get(j-1).getCombinedUtilityForCandidates(cWinners);
            cPref = cPref - cost + indUtil;
            //double totalVoteCost = (abstention) ? getNonAbstentionCount(s) * COST_OF_VOTING : 0D;
            int comparison = Double.compare(cPref, bestPref);
            if(comparison == 0) {
                // add this to current set of optimum e
                optimum_e.add(e);
            }
            else if (comparison > 0) {
                // new util more, trash old optimum, add this to new
                optimum_e.clear(); optimum_e.add(e); bestPref = cPref;
            }
        }


        updateMappingWithOptima(g, gLookup, s, optimum_e);
    }

    private int getNonAbstentionCount(ScoreVector s) {
        int sum = 0;
        for (int i = 0; i < s.getLength() -1; i++) {
            sum += s.get(i);
        }
        return sum;
    }
    private void updateMappingWithOptima(Map<ScoreVector, Set<DPInfo>> g, Map<ScoreVector, Set<DPInfo>> gLookup, ScoreVector s, ArrayList<ScoreVector> optimum_e) {
        Set<ScoreVector> seen = new THashSet<>();
        for (ScoreVector e : optimum_e) {
            ScoreVector sPlusE = getParams().getRule().compilationFunction(s, e, getParams());
            if (!seen.contains(sPlusE)) {
                seen.add(sPlusE);
                Set<DPInfo> g_of_sPlusE = gLookup.get(sPlusE);
                //prepare new DPInfo's
                g_of_sPlusE = prepNewInfos(g_of_sPlusE, e);
                if (g.containsKey(s)) {

                    for (DPInfo item : g.get(s)) g_of_sPlusE.add(item);
                    g.put(s, g_of_sPlusE);
                } else {
                    g.put(s, g_of_sPlusE);
                }
            }
        }
    }

    private Set<DPInfo> prepNewInfos(Set<DPInfo> g_of_sPlusE, ScoreVector e) {
        //Same winners, new e for profiling later on
        Set<DPInfo> res = new THashSet<>(g_of_sPlusE.size());
        for (DPInfo item : g_of_sPlusE) {
            res.add(new DPInfo(item.getWinners(), e));
        }
        return res;
    }

    private void getWinnersBaseCase(Map<ScoreVector, Set<DPInfo>> g, ScoreVector s) {
        Set<DPInfo> res = new THashSet<>();
        ArrayList<Integer> winners = getParams().getRule().getWinnersOfEndState(s, getParams());
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
*/

    private ScoreVector generateZeroVector(int size) {
        return new ScoreVector(size);
    }

    private int factorial(int n) {
        int fact = 1;
        for (int j = 1; j <=n; j++) fact *= j;
        return fact;
    }
}
