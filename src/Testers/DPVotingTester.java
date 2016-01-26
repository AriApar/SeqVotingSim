package Testers;

import Elections.*;
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
public class DPVotingTester extends AbstractTester {

    public static void main(String[] args) throws Exception{
        try {
            Scanner in = new Scanner(getFile("5x5Sample"));
            int voters = in.nextInt();
            int candidates = in.nextInt();
            int[][] prefList = new int[voters][candidates];
            for (int i = 0; i < voters; i++) {
                for (int j = 0; j < candidates; j++) {
                    prefList[i][j] = in.nextInt();
                }
            }
            ElectionType type = ElectionType.DP;
            if (args.length > 0) {
                if (args[0].equals("-a")) type = ElectionType.DPWITHABS;
                else if (args[0].equals("-ac")) {
                    type = ElectionType.DPWITHCOSTLYABS;
                }
            }

            PreferenceList pref = new PreferenceList(prefList);
            VotingOrder order = new VotingOrder(voters, true);
            VotingRule rule = new PluralityVR(candidates);

            ElectionParameters params = new ElectionParameters(pref, order, rule, type);
            DPElection e = (DPElection) ElectionFactory.create(params);

            ArrayList<ElectionState> winners = e.findNE();

            printResults(order, winners);

            /*Set<ArrayList<Integer>> winners = e.findNEs();
            System.out.println("This election has " + winners.size() +
                    " Nash equilibria!");
            Iterator<ArrayList<Integer>> it = winners.iterator();
            for (int i = 1; i<= winners.size(); i++) {
                System.out.println("Nash Equilibrium " + i + ":");
                System.out.print("The winner is candidate(s) ");
                ArrayList<Integer> wins = it.next();
                for (int j = 0; j < wins.size(); j++) System.out.print(wins.get(j) + ", ");

            }*/

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
