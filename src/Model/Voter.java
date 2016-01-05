package Model;

import util.Node;
import util.Tree;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Iterator;

/**
 * Created by AriApar on 26/11/2015.
 */
public class Voter {

    private VotingRule rule;
    private int voterId; //1..N
    private VotingOrder order;
    private PreferenceList preferenceList;
    private final double COST_OF_VOTING = 0.1D;

    public Voter(int voterId, VotingRule rule, VotingOrder order, PreferenceList preferenceList) {
        this.rule = rule;
        this.voterId = voterId;
        this.order = order;
        this.preferenceList = preferenceList;
    }

    //Truthful vote
    public ScoreVector vote() {
        return rule.vote(preferenceList.getPreferencesForVoter(voterId));
    }

    public ScoreVector vote(int candidate) {
        return rule.vote(candidate);
    }

    public ScoreVector voteForPreference(int pref) {
        return rule.vote(preferenceList.getNthPreferenceOfVoter(pref, voterId));
    }

    public int getPreference(int preference) {
        return preferenceList.getNthPreferenceOfVoter(preference, voterId);
    }

    public int getUtilityForCandidate(int candidate) throws Exception {
        Preferences pref = preferenceList.getPreferencesForVoter(voterId);
        //laziness assumption: if abstaining gives you the same utility as voting, abstain.
        //if (candidate == 0) return 0;//pref.length();
        if (candidate == 0) throw new Exception("Trying ot get utility for cand 0, most likely" +
                "the costly voting is broken.");
        int rank = pref.getPreferenceOfCandidate(candidate);
        return (pref.length() - rank + 1);
    }

    public double getCombinedPreferenceForCandidates(ArrayList<Integer> candidates) throws Exception {
        assert (candidates.size() != 0);
        double res = 0D;
        Preferences pref = preferenceList.getPreferencesForVoter(voterId);
        for (Integer c : candidates) {
            res += (double) pref.getPreferenceOfCandidate(c);
        }
        res = res / (double) candidates.size();
        return res;
    }

    public Tree<ElectionState> chooseWhoToVote(Tree<ElectionState> root, int nthToVote, boolean cost) throws Exception {
        int level = nthToVote -1;
        ArrayList<Node<ElectionState>> currLevel = root.getNodesAtLevel(level);
        for (Node<ElectionState> currNode : currLevel) {
            ArrayList<Node<ElectionState>> toRemove = keepBestChild(currNode, cost);
            currNode.removeChildren(toRemove);
            System.out.print("");
        }
        return new Tree(root.getRoot());
    }

    private ArrayList<Node<ElectionState>> keepBestChild(Node<ElectionState> node, boolean cost) throws Exception {
        ArrayList<Node<ElectionState>> children = node.getChildren();
        ArrayList<Node<ElectionState>> toRemove = new ArrayList<>();
        //Node<ElectionState> bestLeaf = null;
        double bestUtil = -1D;
        Node<ElectionState> bestChild = null;

        for (Node<ElectionState> child : children) {

            //check if this child is better than current best
            //if so, remove old best from children list, make this the best
            //otherwise, remove child from children list
            double util = isBetter(child, bestUtil, cost);
            // isBetter returns the new utility if positive
            if (bestUtil == -1 ||  util > 0) {
                if (bestUtil != -1) toRemove.add(bestChild);//node.removeChild(bestChild);
                bestChild = child; bestUtil = util;
            }
            else if (util == 0) {
                //same rank child
                //keep the one which you like the most
                //if one of the options is strategically abstaining, it wins
                int cCand = child.getData().getVoteCast();
                int bCand = bestChild.getData().getVoteCast();
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
            }
            else {
                //worse, remove child
                toRemove.add(child); //node.removeChild(child);
            }
        }
        return toRemove;
    }

    //returns 0 if same, the new best util if better, <0 if worse
    private double isBetter(Node<ElectionState> candidate, double currUtil, boolean cost) throws Exception {
        //get the vote cast, we need to use it for costly voting
        int voteCast = candidate.getData().getVoteCast();
        //go to the end of its branch to see what the result is
        Node<ElectionState> cLeaf = candidate;
        while (cLeaf.hasChild()) {
            ArrayList<Node<ElectionState>> chList = cLeaf.getChildren();
            //assert (chList.size() == 10);
            cLeaf = chList.get(0);
        }

        ArrayList<Integer> cWinners = candidate.getData().getCurrentWinners();

        if (cWinners.size() == 0) return -1;
        //if current winner empty, return neg to keep best winner.

        // we calculate the avg utility we get from the winners.
        //1st pref gets candidate-1 points, 2nd gets candidate-2 etc...
        //TODO: MAKE THE UTILITIES A PART OF ELECTION STATE NODES
        Double cSum = 0D; Double bSum = 0D;
        for (Integer cand : cWinners) {
            cSum += getUtilityForCandidate(cand);
        }
        //cost of voting
        if (cost && voteCast != 0) cSum -= COST_OF_VOTING;

        cSum = cSum / (double) (cWinners.size());

        int compare = Double.compare(cSum, currUtil);
        return (compare <= 0) ? compare : cSum;
    }



}
