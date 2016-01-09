package Elections;

import Model.*;
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
    //private ScoreVector scores;
    private boolean abstention = false;
    private boolean cost = false;

    public BackInductionElection(PreferenceList pref, VotingOrder order, VotingRule rule, boolean abstention, boolean cost) {
        this(pref, order, rule);
        this.abstention = abstention;
        this.cost = cost;
    }

    public BackInductionElection(PreferenceList pref, VotingOrder order, VotingRule rule, boolean abstention) {
        this(pref, order, rule);
        this.abstention = abstention;
    }

    public BackInductionElection(PreferenceList pref, VotingOrder order, VotingRule rule) {
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
    public int run() throws Exception{

        Tree<ElectionState> root = abstention ? generateGameTreeAbs() : generateGameTree();
        //shallow cloning is fine, voters are immutable objects.
        ArrayList<Voter> revVoters = new ArrayList<>(voters);
        Collections.reverse(revVoters);
        for (int i = 0 ; i < revVoters.size(); i++) {
            Voter v = revVoters.get(i);
            //voter v gets to vote on level revVoters.size() - i
            //we expect this to modify the tree represented by root
            v.chooseWhoToVote(root, revVoters.size() - i, cost);
        }
        // at this point the tree consists of only winners
        ArrayList<Node<ElectionState>> resList = root.getNodesAtLevel(voters.size());
        scores = resList.get(0).getData().getCurrentScores();

        return getUniqueWinner(scores);
    }

    public ArrayList<ElectionState> findNE() throws Exception {
        Tree<ElectionState> root = abstention ? generateGameTreeAbs() : generateGameTree();
        //shallow cloning is fine, voters are immutable objects.
        ArrayList<Voter> revVoters = new ArrayList<>(voters);
        Collections.reverse(revVoters);
        for (int i = 0 ; i < revVoters.size(); i++) {
            Voter v = revVoters.get(i);
            //voter v gets to vote on level revVoters.size() - i
            //we expect this to modify the tree represented by root
            root = v.chooseWhoToVote(root, revVoters.size() - i, cost);
        }
        // at this point the tree consists of only winners
        ArrayList<Node<ElectionState>> resList = root.getNodesAtLevel(voters.size());
        ArrayList<ElectionState> winnerArray = new ArrayList<>();
        for (Node<ElectionState> winnerNode : resList)
            winnerArray.add(winnerNode.getData());

        return winnerArray;
    }

    private Tree<ElectionState> generateGameTree() {
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
                    ScoreVector s = v.voteForPreference(i);
                    // Prepare new score vector
                    s = s.addImmutable(currState.getCurrentScores());
                    //Prepare new votes
                    ArrayList<Integer> newVotes = prepareNewVotes(currState, candidate);
                    //Create new state
                    ElectionState newState = new ElectionState(s,getWinners(s), newVotes ,candidate);
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
    }

    private Tree<ElectionState> generateGameTreeAbs() {
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
                    ScoreVector s = v.voteForPreference(i);
                    // Prepare new score vector
                    s = s.addImmutable(currState.getCurrentScores());
                    //Prepare new votes
                    ArrayList<Integer> newVotes = prepareNewVotes(currState, candidate);
                    //Create new state
                    ElectionState newState = new ElectionState(s,getWinners(s), newVotes ,candidate);
                    //add as the child of currNode
                    Node<ElectionState> child = currNode.addChildWithData(newState);
                    //add this to next Level to be checked by next voter
                    nextLevel.add(child);
                }
                //add abstention
                ElectionState absState = new ElectionState(currState.getCurrentScores(), currState.getCurrentWinners(),
                        prepareNewVotes(currState, 0) ,0);
                Node<ElectionState> child = currNode.addChildWithData(absState);
                nextLevel.add(child);
            }
            currLevel = nextLevel;
        }
        //game tree generated, clean up currLevel just in case, return root
        currLevel = null;
        return new Tree(root);
    }

    private ArrayList<Integer> prepareNewVotes(ElectionState state, int candidate) {
        ArrayList<Integer> newVotes = (ArrayList<Integer>) state.getCurrentVotes().clone();
        newVotes.add(candidate);
        return newVotes;
    }
}
