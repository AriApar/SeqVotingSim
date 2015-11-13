package Model;

import java.util.*;

/**
 * Created by AriApar on 13/11/2015.
 *
 * This class encapsulates preference lists of all voters.
 *
 *
 */
public class PreferenceList {

    private int alternatives;
    private int voters;
    private int[][] preferenceList;

    /**
     * Creates an empty PreferenceList with a given number of voters and alternatives.
     * @param voters        number of voters
     * @param alternatives  number of alternatives
     */
    public PreferenceList(int voters, int alternatives) {
        this.alternatives = alternatives;
        this.voters = voters;
        preferenceList = new int[voters][alternatives];
    }
    
    public PreferenceList(int[][] preferenceList) {
        this.alternatives = preferenceList[0].length;
        this.voters = preferenceList.length;
        this.preferenceList = preferenceList;
    }

    public int[] getPreferencesForVoter(int voterId) {
        return preferenceList[voterId-1];
    }

    public int getNthPreferenceOfVoter(int preference, int voterId){
        return preferenceList[voterId-1][preference-1];
    }
}
