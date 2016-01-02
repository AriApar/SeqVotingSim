package Model;

import util.Node;
import util.Tree;

import java.util.ArrayList;
import java.util.EmptyStackException;

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
        return pref.length() - rank;
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

    public void chooseWhoToVote(Tree<ElectionState> root, int nthToVote) throws Exception {
        int level = nthToVote -1;
        ArrayList<Node<ElectionState>> currLevel = root.getNodesAtLevel(level);
        for (Node<ElectionState> currNode : currLevel) {
            keepBestChild(currNode);
        }
    }

    private void keepBestChild(Node<ElectionState> node) throws Exception {
        ArrayList<Node<ElectionState>> children = node.getChildren();
        Node<ElectionState> bestChild = null;
        for (Node<ElectionState> child : children) {
            //go to the end of its branch to see what the result is
            while (child.hasChild()) {
                ArrayList<Node<ElectionState>> chList = child.getChildren();
                assert chList.size() == 1;
                child = chList.get(0);
            }
            //check if this child is better than current best
            //if so, remove old best from children list, make this the best
            //otherwise, remove child from children list
            if (bestChild == null || isBetter(child, bestChild)) {
                if (bestChild != null) node.removeChild(bestChild);
                bestChild = child;
            }
            else {
                node.removeChild(child);
            }
        }
    }

    private boolean isBetter(Node<ElectionState> candidate, Node<ElectionState> currBest) throws Exception {
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

        return Double.compare(cSum, bSum) > 0;
        //TODO: THIS CAUSES THE CODE TO RETURN ONLY ONE SG-P NASH EQ, FIX THIS
    }



}
