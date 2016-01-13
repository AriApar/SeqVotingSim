package Testers;

import Elections.*;
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
public class BackInductionVotingTester extends AbstractTester {

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
            ElectionType type = ElectionType.GAMETREE;
            if (args.length > 0) {
                if (args[0].equals("-a")) type = ElectionType.GAMETREEWITHABS;
                else if (args[0].equals("-ac")) {
                    type = ElectionType.GAMETREEWITHCOSTLYABS;
                }
            }

            PreferenceList pref = new PreferenceList(prefList);
            VotingOrder order = new VotingOrder(voters, true);
            VotingRule rule = new PluralityVR(candidates);

            ElectionParameters params = new ElectionParameters(pref, order, rule, type);
            BackInductionElection e = (BackInductionElection) ElectionFactory.create(params);

            ArrayList<ElectionState> winners = e.findNE();

            printResults(order, winners);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void printResults(VotingOrder order, ArrayList<ElectionState> winners) {
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
