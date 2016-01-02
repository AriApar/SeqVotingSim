package Model;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by AriApar on 13/11/2015.
 */
public class Preferences {

    private ArrayList<Integer> pref;
    private int alternatives;

    public Preferences(int[] preferences) {
        this.pref = new ArrayList<>();
        for (int i : preferences) pref.add(i);
        this.alternatives = preferences.length;
    }

    public int getNthPreference(int n) {
        return pref.get(n-1);
    }

    public int length() { return pref.size(); }

    public int getPreferenceOfCandidate(int candidate) throws Exception {
        int index = 0;
        while (index < alternatives && pref.get(index) != candidate ) index++;
        if (index == alternatives) throw new Exception("candidateID given to getPrefOfCand not in Preferences");
        return index + 1;
    }
}
