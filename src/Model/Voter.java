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
        int rank = pref.getPreferenceOfCandidate(candidate);
        return (pref.length() - rank);
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

    public Tree<ElectionState> chooseWhoToVote(Tree<ElectionState> root, int nthToVote) throws Exception {
        int level = nthToVote -1;
        ArrayList<Node<ElectionState>> currLevel = root.getNodesAtLevel(level);
        for (Node<ElectionState> currNode : currLevel) {
            ArrayList<Node<ElectionState>> toRemove = keepBestChild(currNode);
            currNode.removeChildren(toRemove);
            System.out.print("");
        }
        return new Tree(root.getRoot());
    }

    private ArrayList<Node<ElectionState>> keepBestChild(Node<ElectionState> node) throws Exception {
        ArrayList<Node<ElectionState>> children = node.getChildren();
        ArrayList<Node<ElectionState>> toRemove = new ArrayList<>();
        Node<ElectionState> bestLeaf = null;
        Node<ElectionState> bestChild = null;
        for (Node<ElectionState> child : children) {
            //go to the end of its branch to see what the result is
            Node<ElectionState> cLeaf = child;
            while (cLeaf.hasChild()) {
                ArrayList<Node<ElectionState>> chList = cLeaf.getChildren();
                //assert (chList.size() == 10);
                cLeaf = chList.get(0);
            }
            //check if this child is better than current best
            //if so, remove old best from children list, make this the best
            //otherwise, remove child from children list
            if (bestLeaf == null || isBetter(cLeaf, bestLeaf) > 0) {
                if (bestLeaf != null) toRemove.add(bestChild);//node.removeChild(bestChild);
                bestChild = child; bestLeaf = cLeaf;
            }
            else if (isBetter(cLeaf, bestLeaf) == 0) {
                //same rank child
                //keep the one which you like the most
                int cCand = child.getData().getVoteCast();
                int bCand = bestChild.getData().getVoteCast();
                if (getUtilityForCandidate(cCand) > getUtilityForCandidate(bCand)) {
                    toRemove.add(bestChild);
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

    //returns 0 if same, >0 if better, <0 if worse
    private int isBetter(Node<ElectionState> candidate, Node<ElectionState> currBest) throws Exception {
        ArrayList<Integer> cWinners = candidate.getData().getCurrentWinners();
        ArrayList<Integer> bWinners = currBest.getData().getCurrentWinners();

        // we calculate the avg utility we get from the winners.
        //1st pref gets candidate-1 points, 2nd gets candidate-2 etc...
        //TODO: MAKE THE UTILITIES A PART OF ELECTION STATE NODES
        Double cSum = 0D; Double bSum = 0D;
        for (Integer cand : cWinners) {
            cSum += getUtilityForCandidate(cand);
        }
        for (Integer cand : bWinners) {
            bSum += getUtilityForCandidate(cand);
        }
        cSum = cSum / (double) (cWinners.size());
        bSum = bSum / (double) (bWinners.size());

        return Double.compare(cSum, bSum);
        //TODO: THIS CAUSES THE CODE TO RETURN ONLY ONE SG-P NASH EQ, FIX THIS
    }



}
