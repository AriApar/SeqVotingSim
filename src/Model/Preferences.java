package Model;

/**
 * Created by AriApar on 13/11/2015.
 */
public class Preferences {

    private int[] pref;
    private int alternatives;

    public Preferences(int[] pref) {
        this.pref = pref;
        this.alternatives = pref.length;
    }

    public int getNthPreference(int n) {
        return pref[n-1];
    }

    public int length() { return pref.length; }
}
