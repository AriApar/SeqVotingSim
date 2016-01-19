package Testers;

import Elections.ElectionState;
import Model.VotingOrder;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by AriApar on 13/01/2016.
 */
public class AbstractTester {

    public static File getFile(String fileName) {

        //Get file from resources folder

        File file = new File("res/PlistExamples/" + fileName);
        return file;
    }

    public static void printResults(VotingOrder order, ArrayList<ElectionState> winners) {
        System.out.println("This election has " + winners.size() +
                " Nash equilibria!");
        Iterator<ElectionState> it = winners.iterator();
        for (int i = 1; i<= winners.size(); i++) {
            System.out.println("Nash Equilibrium " + i + ":");
            System.out.print("The winner is candidate(s) ");
            ElectionState wins = it.next();
            //Print winners
            ArrayList<Integer> elected = wins.getCurrentWinners();
            for (int j = 0; j < elected.size() - 1; j++) System.out.print(elected.get(j) + ", ");
            System.out.println(elected.get(elected.size() -1));
            //Print vote distribution
            System.out.println("Vote Distribution: " + wins.getCurrentScores().toString());
            //Print votes cast by each voter
            System.out.println("Votes Cast (in order): ");
            Iterator<Integer> iter = wins.getCurrentVotes().iterator();
            for (Integer v : order) {
                System.out.println("Voter " + v + ": Candidate " +  iter.next());
            }
        }
    }
}
