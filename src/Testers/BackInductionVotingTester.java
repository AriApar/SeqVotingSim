package Testers;

import Elections.BackInductionElection;
import Elections.TruthfulElection;
import Model.*;
import VotingRules.PluralityVR;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

/**
 * Created by AriApar on 30/11/2015.
 */
public class BackInductionVotingTester {

    public static void main(String[] args) throws Exception{
        try {
            Scanner in = new Scanner(getFile("SmallPListSample"));
            int voters = in.nextInt();
            int candidates = in.nextInt();
            int[][] prefList = new int[voters][candidates];
            for (int i = 0; i < voters; i++) {
                for (int j = 0; j < candidates; j++) {
                    prefList[i][j] = in.nextInt();
                }
            }
            boolean abs = false; boolean cost = false;
            if (args.length > 0) {
                if (args[0].equals("-a")) abs = true;
                else if (args[0].equals("-ac")) {
                    abs = true;
                    cost = true;
                }
            }

            PreferenceList pref = new PreferenceList(prefList);
            VotingOrder order = new VotingOrder(voters, true);
            VotingRule rule = new PluralityVR(candidates);

            BackInductionElection e = new BackInductionElection(pref, order, rule, abs, cost);

            ArrayList<ElectionState> winners = e.findNE();
            System.out.println("This election has " + winners.size() +
                    " Nash equilibria!");
            Iterator<ElectionState> it = winners.iterator();
            for (int i = 1; i<= winners.size(); i++) {
                System.out.println("Nash Equilibrium " + i + ":");
                System.out.print("The winner is candidate(s) ");
                ElectionState wins = it.next();
                ArrayList<Integer> elected = wins.getCurrentWinners();
                for (int j = 0; j < elected.size(); j++) System.out.println(elected.get(j) + ", ");
                System.out.println("Vote Distribution: " + wins.getCurrentScores().toString());
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static File getFile(String fileName) {

        //Get file from resources folder

        File file = new File("res/PlistExamples/" + fileName);
        return file;
    }
}
