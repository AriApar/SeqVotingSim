package Model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by AriApar on 21/01/2016.
 */
public class DPInfo implements Serializable {
    private ArrayList<Integer> winners;
    private ScoreVector prefE;

    public DPInfo(ArrayList<Integer> winners, ScoreVector prefVector) {
        this.winners = winners;
        this.prefE = prefVector;
        this.winners.trimToSize();
    }

    public ArrayList<Integer> getWinners() {
        return winners;
    }

    public ScoreVector getE() {
        return prefE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DPInfo dpInfo = (DPInfo) o;

        if (winners != null ? !winners.equals(dpInfo.winners) : dpInfo.winners != null) return false;
        return prefE != null ? prefE.equals(dpInfo.prefE) : dpInfo.prefE == null;

    }

    @Override
    public int hashCode() {
        int result = winners != null ? winners.hashCode() : 0;
        result = 31 * result + (prefE != null ? prefE.hashCode() : 0);
        return result;
    }
}
