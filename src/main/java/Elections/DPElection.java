package Elections;

import Model.*;

import java.io.*;
import java.util.*;

import Model.Vector;
import com.google.common.math.IntMath;
import it.unimi.dsi.fastutil.io.BinIO;
import it.unimi.dsi.fastutil.objects.*;

/**
 * Created by AriApar on 01/12/2015.
 */
public class DPElection extends Election{
    //Dynamic programming election that follows Algorithm 1 from Stackelberg paper.
    private boolean abstention;
    private boolean cost;
    private ArrayList<Voter> voters;
    private final double COST_OF_VOTING = 5D;
    //private Map<Vector, List<DPInfo>>[] mapArr;

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
        //mapArr = (Map<Vector, List<DPInfo>>[]) Array.newInstance(Object2ObjectOpenHashMap.class, voters.size() +1);// Object2ObjectOpenHashMap<Vector, Set<DPInfo>>[voters.size() +1];
    }

    /*public Set<ArrayList<Integer>> findNE() throws Exception{

        HashMap<Vector, LinkedHashSet<ArrayList<Integer>>> g = new HashMap<>();
        int numAlternatives = getParams().numberOfCandidates();
        int numVoters = voters.size();
        int numAltFactorial = factorial(numAlternatives);


        final ArrayList<Vector> EVector = generateEVectors(numAltFactorial);
        for (int j = numVoters +1; j >=1; j--) {
            Set<Vector> states = generatePossibleScoresAtLevel(j, numAltFactorial);
            for (Vector s : states) {
                if (j == numVoters + 1) {

                    LinkedHashSet<ArrayList<Integer>> winnerSet = new LinkedHashSet<>();
                    winnerSet.add(getParams().getRule().getWinnersOfVoteVector(s, getParams()));
                    g.put(s, winnerSet);
                }
                else {

                    double bestPref = Double.MAX_VALUE;
                    ArrayList<Vector> optimum_e = new ArrayList<>();
                    for (Vector e : EVector) {
                        Vector gSum = s.addImmutable(e);
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
                    for (Vector e : optimum_e) {
                        Vector sPlusE = s.addImmutable(e);
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
        HashMap<Vector, LinkedHashSet<ArrayList<Integer>>> g = new HashMap<>();
        int numAlternatives = getParams().numberOfCandidates();
        int numVoters = voters.size();
        int numAltFactorial = factorial(numAlternatives);
        //add abstention possibility
        if (abstention) numAltFactorial += 1;
        final ArrayList<Vector> EVector =  generateEVectors(numAltFactorial);
        for (int j = numVoters +1; j >=1; j--) {
            Set<Vector> states = generatePossibleScoresAtLevel(j, numAltFactorial);
            for (Vector s : states) {
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
        return "tmp" + Thread.currentThread().getId() + "/map_Stage_" + stageNo + ".ser";
    }

    private boolean saveMapForStage(Map<Vector, List<DPInfo>> map, int stageNo) {
        //mapArr[stageNo] = map;
        try
        {
            File f = new File(getFileName(stageNo));
            f.getParentFile().mkdirs();
            BinIO.storeObject(map, f);
            /*FileOutputStream fileOut = new FileOutputStream(f);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(map);
            out.close();
            fileOut.close();*/
            //System.out.println("Serialized data is saved in /tmp/map_Stage_" + stageNo + ".ser");
        }
        catch(IOException i)
        {
            i.printStackTrace();
            return false;
        }
        return true;
    }

    private Map<Vector, List<DPInfo>> getMapForStage(int stageNo) {
        //return mapArr[stageNo];
        try
        {
            File f = new File(getFileName(stageNo));
            /*FileInputStream fileIn = new FileInputStream(f);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            Map<Vector, Set<DPInfo>> map = (Map<Vector, Set<DPInfo>>) in.readObject();
            in.close();
            fileIn.close();*/
            Map<Vector, List<DPInfo>> map = (Map<Vector, List<DPInfo>>) BinIO.loadObject(f);
            //System.out.println("Read map no " + stageNo + " from file");
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
        VotingRule votingRule = getParams().getRule();


        int numAlternatives = getParams().numberOfCandidates();
        //stars and bars calculation
        int numBoxes = abstention ? numAlternatives + 1 : numAlternatives;

        //add abstention possibility


        //Map<Vector, Set<DPInfo>> g = new THashMap<>();
        Object2ObjectOpenHashMap<Vector, List<DPInfo>> gMap = new Object2ObjectOpenHashMap<>(IntMath.binomial(numVoters + numBoxes - 2, numBoxes -1));
        //gMap.setAutoCompactionFactor(0.5f);


        final ArrayList<Vector> EVector =  votingRule.generateEVectors(getParams()); //generateEVectors(numAltFactorial);
        List<Vector> states = null;
        for (int j = numVoters +1; j >=1; j--) {
            states = votingRule.generateStatesForLevel(j, getParams());
            //System.out.println("Generated states for level " + j);
            /*if (j == numVoters+1)
                states = getParams().getRule().generateStatesForLevel(j, getParams());
            else {
                //states = getParams().getRule().generateStatesForLevel(j, getParams());
                states = shrinkStatesBy1(states);
                System.out.println("Generated states for level " + j);
            }*/
            Object2ObjectOpenHashMap<Vector, List<DPInfo>> g = new Object2ObjectOpenHashMap<>(IntMath.binomial(j + numBoxes - 2, numBoxes -1));
            if (j == numVoters + 1) {
                for (Vector s : states) {
                    getWinnersBaseCase(g, s);
                }
            }
            //g.setAutoCompactionFactor(0.5f);
            else {
                for (Vector s : states) {
                    getWinnersElseCase(g, gMap, EVector, j, s);
                }
                writeToFileAndClear(gMap, j);
            }
            gMap = g;
            //gMap.putAll(g);
            gMap.trim();

        }

        //return generateWinnerStates(g, g.get(generateZeroVector(numAltFactorial)), numAlternatives, numAltFactorial);
        //return generateWinnerStates(gMap.get(generateZeroVector(numAltFactorial)), numAlternatives, numAltFactorial);
        int stateSize = votingRule.getCompilationStateSize(getParams());
        return generateWinnerStates(gMap.get(generateZeroVector(stateSize)), numAlternatives, stateSize);
    }

    private Set<Vector> shrinkStatesBy1(Set<Vector> states) {
        Set<Vector> res = new ObjectOpenHashSet<>(states.size());
        for (Vector state : states) {
            for (int i = 0; i < state.getLength(); i++) {
                int value = state.get(i);
                if (value != 0) res.add(state.cloneAndSet(i, value - 1));
            }
        }
        return res;
    }

    private ArrayList<ElectionState> generateWinnerStates(List<DPInfo> dpInfos, int numAlternatives, int numAltFactorial) throws Exception{
        Queue<Triple<ElectionState, DPInfo, Vector>> q = new ArrayDeque<>();
        //Set<ElectionState> res = new ObjectOpenHashSet<>(dpInfos.size() * 2);
        ArrayList<ElectionState> res = new ArrayList<>();
        for (DPInfo item : dpInfos) {
            Vector key = generateZeroVector(numAltFactorial);
            ElectionState initState = new ElectionState(numAlternatives);
            q.add(new Triple(initState, item, key));
            //System.out.println(initState.getCurrentVotes().toString());
        }
        Map<Vector, List<DPInfo>> currMap = new Object2ObjectOpenHashMap<>();
        int prevSum = -1; //level counter, so we don't reload the same level map
        //Initial queue done, now start processing it
        while (!q.isEmpty()) {
            Triple<ElectionState, DPInfo, Vector> tuple = q.remove();
            ElectionState state = tuple.first;
            DPInfo info = tuple.second;
            Vector key = tuple.third;

            if (info.getE() != null) {
                Vector e = info.getE();

                ArrayList<Integer> candArray = getParams().getRule().getWinnersOfVoteVector(e, getParams());
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
                List<DPInfo> newInfos = currMap.get(key);

                for (DPInfo item : newInfos) {
                    q.add(new Triple<>(newState, item, key));
                }
            }
            else {
                // key null, put the resulting state in the return array
                //if (Arrays.equals(state.getCurrentWinners().toArray(), info.getWinnersOfScoreVector().toArray()))
                    res.add(state);
                //else throw new Exception("winner states from mapping not consistent with generated electionstate");
            }
        }
        //return new ArrayList<>(res);
        return res;
    }

    private void writeToFileAndClear(Map<Vector, List<DPInfo>> g, int j) {
        //Writes g to file and deletes it from the array
        saveMapForStage(g, j);
        //g.clear();
    }

    /*private ArrayList<ElectionState> generateWinnerStates(Map<Vector, Set<DPInfo>> g,
                                                          Set<DPInfo> dpInfos, int numAlternatives,
                                                          int numAltFactorial) throws Exception {
        Queue<Triple<ElectionState, DPInfo, Vector>> q = new ArrayList<>();
        Set<ElectionState> res = new HashSet<>();
        for (DPInfo item : dpInfos) {
            Vector key = generateZeroVector(numAltFactorial);
            ElectionState initState = new ElectionState(numAlternatives);
            q.add(new Triple(initState, item, key));

        }
        //Initial queue done, now start processing it
        while (!q.isEmpty()) {
            Triple<ElectionState, DPInfo, Vector> tuple = q.remove();
            ElectionState state = tuple.first;
            DPInfo info = tuple.second;
            Vector key = tuple.third;

            if (info.getE() != null) {
                Vector e = info.getE();

                ArrayList<Integer> candArray = getParams().getRule().getWinnersOfVoteVector(e, getParams());
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
                if (Arrays.equals(state.getCurrentWinners().toArray(), info.getWinnersOfScoreVector().toArray()))
                    res.add(state);
                else throw new Exception("winner states from mapping not consistent with generated electionstate");
            }
        }
        return new ArrayList<>(res);
    }*/

    private ElectionState prepNewState(ElectionState state, int candidate) {
        //Get old electionstate values to generate new one
        //Vector
        ScoreVector vector = state.getCurrentScores();
        ArrayList<Integer> votes = (ArrayList<Integer>) state.getCurrentVotes();//.clone();
        votes.add(candidate);

        if (candidate == 0) {
            //abstention
            return new ElectionState(vector, state.getCurrentWinners(), votes);
        }
        else {
            //add vote to scorevector
            vector = vector.cloneAndSetCandidate(candidate, vector.getCandidate(candidate) + 1);
            return new ElectionState(vector, getWinnersOfScoreVector(vector), votes);
        }
    }

    private void getWinnersElseCase(Map<Vector, List<DPInfo>> g,
                                    Map<Vector, List<DPInfo>> gLookup,
                                    ArrayList<Vector> EVector,
                                    int j, Vector s) throws Exception {
        double bestPref = Double.MIN_VALUE;
        double bestUtil = Double.MIN_VALUE;
        ArrayList<Vector> optimum_e = new ArrayList<>();
        boolean hasCost = getParams().hasCost();

        for (Vector e : EVector) {
            boolean abs = (abstention && e.get(e.getLength()-1) == 1);

            double cost = (!abs && hasCost) ? COST_OF_VOTING : 0D;
            ArrayList<Integer> voteCast = getParams().getRule().getWinnersOfVoteVector(e, getParams());
            //todo: change back depending on edith
            double indUtil = (voteCast.size() == 0) ? 0D :
                    voters.get(j-1).getCombinedUtilityForCandidates(voteCast) / 10000D;

            //Vector gSum = s.addImmutable(e);
            Vector gSum = getParams().getRule().compilationFunction(s, e, getParams());

            List<DPInfo> cStates = gLookup.get(gSum);
            ArrayList<Integer> cWinners = cStates.get(0).getWinners();
            //because we only need the rank of the current winners, getting only one is fine as
            //all winners will have same rank if they're all optimal

            double cPref = voters.get(j-1).getCombinedUtilityForCandidates(cWinners);
            cPref = cPref - cost;
            //double totalVoteCost = (abstention) ? getNonAbstentionCount(s) * COST_OF_VOTING : 0D;
            int comparison = Double.compare(cPref, bestPref);

            if(comparison == 0) {
                // add this to current set of optimum e, if individual preference is also same
                int utilComparison = Double.compare(indUtil, bestUtil);
                if (utilComparison == 0) optimum_e.add(e);
                // if not, if new indUtil more, make this the new optimum
                else if (utilComparison > 0) {
                    optimum_e.clear(); optimum_e.add(e); bestUtil = indUtil;
                }
            }
            else if (comparison > 0) {
                // new util more, trash old optimum, add this to new
                //optimum_e = new ArrayList<>(); optimum_e.add(e); bestPref = cPref;
                optimum_e.clear(); optimum_e.add(e); bestPref = cPref; bestUtil = indUtil;
            }
        }


        updateMappingWithOptima(g, gLookup, s, optimum_e);
    }

    private int getNonAbstentionCount(Vector s) {
        int sum = 0;
        for (int i = 0; i < s.getLength() -1; i++) {
            sum += s.get(i);
        }
        return sum;
    }
    private void updateMappingWithOptima(Map<Vector, List<DPInfo>> g,
                                         Map<Vector, List<DPInfo>> gLookup,
                                         Vector s, ArrayList<Vector> optimum_e) {
        Set<Vector> seen = new ObjectOpenHashSet<>();
        for (Vector e : optimum_e) {
            Vector sPlusE = getParams().getRule().compilationFunction(s, e, getParams());
            if (!seen.contains(sPlusE)) {
                seen.add(sPlusE);
                List<DPInfo> g_of_sPlusE = gLookup.get(sPlusE);
                //prepare new DPInfo's
                g_of_sPlusE = prepNewInfos(g_of_sPlusE, e);
                //g.get(s).addAll(g_of_sPlusE);
                if (g.containsKey(s)) {
                    // todo: optimize this
                    //for (DPInfo item : g.get(s)) g_of_sPlusE.add(item);
                    g_of_sPlusE.addAll(g.get(s));
                    g.put(s, g_of_sPlusE);
                }
                else {
                    g.put(s, g_of_sPlusE);
                }
            }
        }
    }

    private List<DPInfo> prepNewInfos(List<DPInfo> g_of_sPlusE, Vector e) {
        //Same winners, new e for profiling later on
        /*Set<DPInfo> res = g_of_sPlusE.stream()
                                .map(dpInfo -> new DPInfo(dpInfo.getWinnersOfScoreVector(), e))
                                .collect(Collectors.toCollection(ObjectOpenHashSet<DPInfo>::new));*/
        List<DPInfo> res = new ArrayList<>();
        for (DPInfo item : g_of_sPlusE) {
            res.add(new DPInfo(item.getWinners(), e));
        }
        return res;
    }

    private void getWinnersBaseCase(Map<Vector, List<DPInfo>> g, Vector s) {
        List<DPInfo> res = new ArrayList<>();
        ArrayList<Integer> winners = getParams().getRule().getWinnersOfStateVector(s, getParams());
        res.add(new DPInfo(winners, null));
        g.put(s, res);
    }

    /*private void getWinnersElseCase(HashMap<Vector, LinkedHashSet<ArrayList<Integer>>> g,
                                    ArrayList<Vector> EVector,
                                    int j, Vector s) throws Exception {
        double bestPref = Double.MAX_VALUE;
        ArrayList<Vector> optimum_e = new ArrayList<>();

        for (Vector e : EVector) {
            boolean abs = (abstention && e.get(e.getLength()-1) == 1);
            double cost = ((abs && getParams().hasCost()) ? 0.01D : 0D);
            Vector gSum = s.addImmutable(e);

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

    /*private void updateMappingWithOptima(HashMap<Vector, LinkedHashSet<ArrayList<Integer>>> g, Vector s, ArrayList<Vector> optimum_e) {
        for (Vector e : optimum_e) {
            Vector sPlusE = s.addImmutable(e);
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

    /*private void getWinnersBaseCase(HashMap<Vector, LinkedHashSet<ArrayList<Integer>>> g, Vector s) {
        LinkedHashSet<ArrayList<Integer>> winnerSet = new LinkedHashSet<>();
        winnerSet.add(getParams().getRule().getWinnersOfVoteVector(s, getParams()));
        g.put(s, winnerSet);
    }*/

    private ArrayList<Vector> generateEVectors(int size) {
        ArrayList<Vector> res = new ArrayList<>(size);
        Vector zeroVector = generateZeroVector(size);
        for (int j = 0; j < size; j++) {
            Vector e = zeroVector.cloneAndSet(j, 1);
            res.add(e);
        }
        return res;
    }


/*

    private Set<Vector> generatePossibleScoresAtLevel(int level, int size) {
        if (level == 1) {
            Set<Vector> s = new HashSet<>();
            s.add(new Vector(new int[size]));
            return s;
        }
        else {
            Set<Vector> scoreSet = generatePossibleScoresAtLevel(level - 1, size);
            Set<Vector> resSet = new HashSet<>(scoreSet.size()*size);
            for (Vector s : scoreSet) {
                for (int i = 0; i < size; i++) {
                    resSet.add(s.cloneAndSet(i, s.get(i) + 1));
                }
            }
            return resSet;
        }
    }
*/

    private Vector generateZeroVector(int size) {
        return new Vector(size);
    }

    private int factorial(int n) {
        int fact = 1;
        for (int j = 1; j <=n; j++) fact *= j;
        return fact;
    }
}
