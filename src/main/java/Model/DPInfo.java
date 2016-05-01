package Model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by AriApar on 21/01/2016.
 */
public class DPInfo implements Serializable {
    private ArrayList<Integer> winners;
    private Vector prefE;

    private int hashCode = 0;

    public DPInfo(ArrayList<Integer> winners, Vector prefVector) {
        this.winners = winners;
        this.prefE = prefVector;
        this.winners.trimToSize();
        //hashCode();
    }

    public ArrayList<Integer> getWinners() {
        return winners;
    }

    public Vector getE() {
        return prefE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DPInfo dpInfo = (DPInfo) o;

        //if (hashCode != dpInfo.hashCode()) return false;
        if (prefE != null ? prefE.equals(dpInfo.prefE) : dpInfo.prefE == null) return false;
        return winners != null ? !winners.equals(dpInfo.winners) : dpInfo.winners != null;

    }

    @Override
    public int hashCode() {
        if (hashCode == 0) {
            int result = winners != null ? winners.hashCode() : 0;
            result = 31 * result + (prefE != null ? prefE.hashCode() : 0);
            hashCode = result;
        }
        return hashCode;
    }
}
