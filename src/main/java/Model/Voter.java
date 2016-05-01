package Model;

import Elections.ElectionParameters;
import Elections.ElectionState;
import util.Node;
import util.Tree;

import java.util.ArrayList;

/**
 * Created by AriApar on 26/11/2015.
 */
public class Voter {

    private ElectionParameters params;
    private int voterId; //1..N
    private final double COST_OF_VOTING = 5D;

    public Voter(int voterId, ElectionParameters params) {
        this.voterId = voterId;
        this.params = params;
    }

    //Truthful vote
    public Vector vote() {
        return getRule().voteTruthful(getPrefList().getPreferencesForVoter(voterId));
    }

    public Vector vote(int candidate) {
        return getRule().vote(candidate);
    }

    public double getCombinedUtilityForCandidates(ArrayList<Integer> candidates) throws Exception {
        if (candidates.size() == 0) return Double.MIN_VALUE;
        double res = 0D;
        for (Integer c : candidates) {
            res += (double) getUtilityForCandidate(c);
        }
        res = res / (double) candidates.size();
        return res;
    }

    public Tree<ElectionState> chooseWhoToVote(Tree<ElectionState> root, int level) throws Exception {
        ArrayList<Node<ElectionState>> currLevel = root.getNodesAtLevel(level);
        for (Node<ElectionState> currNode : currLevel) {
            ArrayList<Node<ElectionState>> toRemove = keepBestChild(currNode);
            currNode.removeChildren(toRemove);
        }
        return new Tree(root.getRoot());
    }

    public int compareOverallUtilityForWinnersToCurrentBest(ArrayList<Integer> winners,
                                                         double cost, double bestUtil)  throws Exception
    {
        double cPref = getCombinedUtilityForCandidates(winners);
        cPref = cPref - cost;
        return Double.compare(cPref, bestUtil);
    }

    public int compareIndUtilityForVoteToCurrentBest(Vector vote, double bestUtil) throws Exception {
        double indUtil = getIndUtilityForVote(vote);
        return Double.compare(indUtil, bestUtil);
    }

    public double getIndUtilityForVote(Vector vote) throws Exception {
        ArrayList<Integer> voteCast = params.getRule().getWinnersOfVoteVector(vote, params);
        double indUtil = (voteCast.size() == 0) ? 0D :
                getCombinedUtilityForCandidates(voteCast);
        return indUtil;
    }

    private int getUtilityForCandidate(int candidate) throws Exception {
        Preferences pref = getPrefList().getPreferencesForVoter(voterId);
        //laziness assumption: if abstaining gives you the same utility as voting, abstain.
        if (candidate == 0) throw new Exception("Trying to get utility for cand 0, most likely" +
                "the costly voting is broken.");
        int rank = pref.getPreferenceOfCandidate(candidate);
        return (pref.length() - rank + 1) * 20;
    }

    private ArrayList<Node<ElectionState>> keepBestChild(Node<ElectionState> node) throws Exception {
        ArrayList<Node<ElectionState>> children = node.getChildren();
        ArrayList<Node<ElectionState>> toRemove = new ArrayList<>();
        //Node<ElectionState> bestLeaf = null;
        double bestUtil = -1D; boolean first = true;
        Node<ElectionState> bestChild = null;

        for (Node<ElectionState> child : children) {
            //check if this child is better than current best
            //if so, remove old best from children list, make this the best
            //otherwise, remove child from children list
            double newUtil = calculateUtil(child);
            // isBetter returns the new utility if positive
            if (first ||  compare(newUtil, bestUtil) > 0) {
                //if we're first, set first to false
                //otherwise, add previous best to removal array
                if (!first)
                    toRemove.add(bestChild);
                else
                    first = false;
                //now set the new child to be best
                bestChild = child; bestUtil = newUtil;
            }
            else if (compare(newUtil, bestUtil) == 0) {
                bestChild = compareSameUtilChildren(toRemove, bestChild, child);
            }
            else {
                //worse, remove child
                toRemove.add(child);
            }
        }
        return toRemove;
    }

    private Node<ElectionState> compareSameUtilChildren(ArrayList<Node<ElectionState>> toRemove, Node<ElectionState> bestChild, Node<ElectionState> child) throws Exception {
        //same rank child
        //keep the one which you like the most
        //if one of the options is strategically abstaining, it wins
        int cCand = child.getData().getLastVoteCast();
        int bCand = bestChild.getData().getLastVoteCast();
        //if the best is abstaining, child cannot improve on that by lazy voter assumption
        //otherwise if current is abstaining, or current cand is better than best so far
        //swap current and best
        //else, child is worse again, so remove it.
        if (bCand == 0) toRemove.add(child);
        else if (cCand == 0 || getUtilityForCandidate(cCand) > getUtilityForCandidate(bCand)) {
            toRemove.add(bestChild); bestChild = child;
        }
        else {
            toRemove.add(child);
        }
        //if no difference in between the two candidates, leave them be
        return bestChild;
    }

    //returns 0 if same, the new best util if better, <0 if worse
    private double calculateUtil(Node<ElectionState> candidate) throws Exception {
        //get the vote cast, we need to use it for costly voting
        int voteCast = candidate.getData().getLastVoteCast();
        //go to the end of its branch to see what the result is
        ArrayList<Integer> cWinners = getWinnersOfBranch(candidate);

        // we calculate the avg utility we get from the winners.
        Double cSum = getCombinedUtilityForCandidates(cWinners);
        //cost of voting
        if (params.hasCost() && voteCast != 0) cSum -= COST_OF_VOTING;

        return cSum;
    }

    private int compare(double newUtil, double currUtil) {
        return Double.compare(newUtil, currUtil);
    }

    private ArrayList<Integer> getWinnersOfBranch(Node<ElectionState> candidate) {
        Node<ElectionState> cLeaf = candidate;
        while (cLeaf.hasChild()) {
            ArrayList<Node<ElectionState>> chList = cLeaf.getChildren();
            //assert (chList.size() == 10);
            cLeaf = chList.get(0);
        }

        return cLeaf.getData().getCurrentWinners();
    }

    private VotingRule getRule() {
        return params.getRule();
    }

    private PreferenceList getPrefList() {
        return params.getPref();
    }

}