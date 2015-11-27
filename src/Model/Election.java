package Model;

/**
 * Created by AriApar on 25/11/2015.
 */
public abstract class Election {

    private PreferenceList pref;
    private VotingOrder order;
    private VotingRule rule;

    private ScoreVector scores;

    public abstract int run();

    public abstract int getScore(int candidate);

}
