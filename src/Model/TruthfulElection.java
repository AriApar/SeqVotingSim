package Model;

/**
 * Created by AriApar on 25/11/2015.
 */
public class TruthfulElection implements Election {

    private PreferenceList pref;
    private VotingOrder order;
    private VotingRule rule;

    public TruthfulElection(PreferenceList pref, VotingOrder order, VotingRule rule) {
        this.pref = pref;
        this.order = order;
        this.rule = rule;
    }

    @Override
    public int run() {
        for(int voter : order) {
            rule.vote(pref.getPreferencesForVoter(voter));
        }

        return rule.getWinner();
    }

    @Override
    public int getScore(int candidate) {
        return rule.getScore(candidate);
    }
}
