package Model;

/**
 * Created by AriApar on 13/11/2015.
 */
public interface VotingRule {

    public ScoreVector vote(Preferences pref);

    public ScoreVector vote(int candidate);

}
