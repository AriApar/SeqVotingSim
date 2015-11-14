package Model;

/**
 * Created by AriApar on 13/11/2015.
 */
public interface VotingRule {

    int[] vote(Preferences pref);

    int getWinner();
}
