package Testers;

import Elections.BackInductionElection;
import Elections.TruthfulElection;
import Model.*;
import VotingRules.PluralityVR;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Created by AriApar on 30/11/2015.
 */
public class BackInductionVotingTester {

    public static void main(String[] args) {
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

            Election e = new BackInductionElection(pref, order, rule);

            int winner = e.run();
            System.out.println("The winner is candidate " + winner +
                    " with " + e.getScore(winner) + " votes!");
            System.out.println("Distribution of Votes:");
            for (int i = 1; i <= pref.getNumCandidates(); i++) {
                System.out.println("Candidate " + i + ": " + e.getScores().getCandidate(i) + " votes.");
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