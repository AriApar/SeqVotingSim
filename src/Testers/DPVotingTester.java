package Testers;

import Elections.BackInductionElection;
import Elections.DPElection;
import Model.*;
import VotingRules.PluralityVR;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

/**
 * Created by AriApar on 02/12/2015.
 */
public class DPVotingTester {

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

            PreferenceList pref = new PreferenceList(prefList);
            VotingOrder order = new VotingOrder(voters, true);
            VotingRule rule = new PluralityVR(candidates);

            DPElection e = new DPElection(pref, order, rule);

            Set<ArrayList<Integer>> winners = e.findNEAbs();
            System.out.println("This election has " + winners.size() +
                    " Nash equilibria!");
            Iterator<ArrayList<Integer>> it = winners.iterator();
            for (int i = 1; i<= winners.size(); i++) {
                System.out.println("Nash Equilibrium " + i + ":");
                System.out.print("The winner is candidate(s) ");
                ArrayList<Integer> wins = it.next();
                for (int j = 0; j < wins.size(); j++) System.out.print(wins.get(j) + ", ");
                //System.out.println("Vote Distribution: " + wins.getCurrentScores().toString());
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
