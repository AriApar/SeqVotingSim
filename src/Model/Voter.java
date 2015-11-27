package Model;

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

}
