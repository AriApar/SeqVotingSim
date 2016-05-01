package Elections;

import Model.*;
import Model.Vector;
import util.Node;
import util.Tree;

import java.util.*;

/**
 * Created by AriApar on 27/11/2015.
 */
public class BackInductionElection extends Election {

    //private PreferenceList pref;
    //private ArrayList<Voter> voters;
    //private VotingRule rule;
    //private Vector scores;
    private boolean abstention;
    private boolean cost;
    private ArrayList<Voter> voters;

    protected BackInductionElection(ElectionParameters params) {
        setElection(params);
        voters = getVoters();
        abstention = params.canAbstain();
        cost = params.hasCost();
    }

    public ArrayList<ElectionState> findNE() throws Exception {
        Tree<ElectionState> root = generateGameTree();
        //shallow cloning is fine, voters are immutable objects.
        ArrayList<Voter> revVoters = new ArrayList<>(voters);
        Collections.reverse(revVoters);
        for (int i = 0 ; i < revVoters.size(); i++) {
            Voter v = revVoters.get(i);
            //voter v gets to vote on level revVoters.size() - i
            //we expect this to modify the tree represented by root
            root = v.chooseWhoToVote(root, revVoters.size() - i);
        }
        // at this point the tree consists of only winners at bottom level
        return getWinnerStates(root);
    }

    private ArrayList<ElectionState> getWinnerStates(Tree<ElectionState> root) {
        ArrayList<Node<ElectionState>> resList = root.getNodesAtLevel(voters.size()+1);
        ArrayList<ElectionState> winnerArray = new ArrayList<>();
        for (Node<ElectionState> winnerNode : resList)
            winnerArray.add(winnerNode.getData());
        return winnerArray;
    }

/*    private Tree<ElectionState> generateGameTree() {
        int numCandidates = pref.getNumCandidates();
        ElectionState initState = new ElectionState(numCandidates);
        Node<ElectionState> root = new Node<ElectionState>(initState);
        Queue<Node<ElectionState>> currLevel = new LinkedList<>();
        currLevel.add(root);
        for (Voter v : voters) {
            Queue<Node<ElectionState>> nextLevel = new LinkedList<>();
             while (currLevel.peek() != null) {
                 Node<ElectionState> currNode = currLevel.remove();
                 ElectionState currState = currNode.getData();
                //generate all possible states on all possible votes of v
                for (int i = 1; i <= numCandidates; i++) {
                    int candidate = v.getPreference(i);
                    Vector s = v.voteForPreference(i);
                    // Prepare new score vector
                    s = s.add(currState.getCurrentScores());
                    //Prepare new votes
                    ArrayList<Integer> newVotes = prepareNewVotes(currState, candidate);
                    //Create new state
                    ElectionState newState = new ElectionState(s,getWinnersOfScoreVector(s), newVotes ,candidate);
                    //add as the child of currNode
                    Node<ElectionState> child = currNode.addChildWithData(newState);
                    //add this to next Level to be checked by next voter
                    nextLevel.add(child);
                }
            }
            currLevel = nextLevel;
        }
        //game tree generated, clean up currLevel just in case, return root
        currLevel = null;
        return new Tree(root);
    }*/

    private Tree<ElectionState> generateGameTree() {
        int numCandidates = getParams().numberOfCandidates();
        ElectionState initState = new ElectionState(numCandidates);
        Node<ElectionState> root = new Node<ElectionState>(initState);
        Queue<Node<ElectionState>> currLevel = new LinkedList<>();
        currLevel.add(root);
        for (Voter v : voters) {
            currLevel = generateNextLevel(currLevel, v);
        }
        //game tree generated, clean up currLevel just in case, return root
        currLevel = null;
        return new Tree(root);
    }

    private Queue<Node<ElectionState>> generateNextLevel(Queue<Node<ElectionState>> currLevel, Voter v) {
        Queue<Node<ElectionState>> nextLevel = new LinkedList<>();
        while (currLevel.peek() != null) {
            Node<ElectionState> currNode = currLevel.remove();
            //generate all possible states on all possible votes of v
            generateStateForEachCand(v, nextLevel, currNode);
            //add abstention
            if(abstention) {
                generateAbstentionState(nextLevel, currNode);
            }
        }
        currLevel = nextLevel;
        return currLevel;
    }

    private void generateAbstentionState(Queue<Node<ElectionState>> nextLevel, Node<ElectionState> currNode) {
        ElectionState currState = currNode.getData();
        ElectionState absState = new ElectionState(currState.getCurrentScores(), currState.getCurrentWinners(),
                prepareNewVotes(currState, 0));
        Node<ElectionState> child = currNode.addChildWithData(absState);
        nextLevel.add(child);
    }

    private void generateStateForEachCand(Voter v, Queue<Node<ElectionState>> nextLevel, Node<ElectionState> currNode) {
        ElectionState currState = currNode.getData();
        for (int i = 1; i <= getParams().numberOfCandidates(); i++) {
            //int candidate = v.getPreference(i);
            Vector vote = v.vote(i);
            // Prepare new score vector
            ScoreVector s = currState.getCurrentScores().add(vote);
            //Prepare new votes
            ArrayList<Integer> newVotes = prepareNewVotes(currState, i);
            //Create new state
            ElectionState newState = new ElectionState(s, getWinnersOfScoreVector(s), newVotes);
            //add as the child of currNode
            Node<ElectionState> child = currNode.addChildWithData(newState);
            //add this to next Level to be checked by next voter
            nextLevel.add(child);
        }
    }

    private ArrayList<Integer> prepareNewVotes(ElectionState state, int candidate) {
        ArrayList<Integer> newVotes = (ArrayList<Integer>) state.getCurrentVotes().clone();
        newVotes.add(candidate);
        return newVotes;
    }
}
