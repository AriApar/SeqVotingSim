package me.ariapar.Processor;

import Elections.*;
import Model.PreferenceList;
import Model.VotingOrder;
import Model.VotingRule;
import VotingRules.PluralityVR;

import java.io.BufferedWriter;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

/**
 * Created by AriApar on 22/04/2016.
 */
public class SampleFileProcessor implements Runnable {
    private Path p;
    private String[] args;

    public SampleFileProcessor(Path p, String[] args) {
        this.p = p;
        this.args = args;
    }

    @Override
    public void run() {
        try {
            Scanner in = new Scanner(p);
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

            long startTime = System.nanoTime();
            ArrayList<ElectionState> winners = e.findNE();
            long endTime = System.nanoTime();
            saveResultsToFile(order, winners, (endTime - startTime), p);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(p.toString());
        }
    }

    public static void saveResultsToFile(VotingOrder order, ArrayList<ElectionState> winners,
                                         long time, Path path) {
        //make the directory if needed
        String directoryPath = "results/";
        File f = new File(directoryPath);
        f.mkdirs();
        //get file path
        Path p = Paths.get(directoryPath, path.getFileName().toString());
        try {
            BufferedWriter writer = Files.newBufferedWriter(p, StandardCharsets.UTF_8);
            writer.write("This election has " + winners.size() +
                    " Nash equilibria!");
            writer.newLine();
            Iterator<ElectionState> it = winners.iterator();
            for (int i = 1; i<= winners.size(); i++) {
                writer.write("Nash Equilibrium " + i + ":");
                writer.newLine();
                writer.write("The winner is candidate(s) ");
                ElectionState wins = it.next();
                //Print winners
                ArrayList<Integer> elected = wins.getCurrentWinners();
                //System.out.println(elected.toString());

                for (int j = 0; j < elected.size() - 1; j++) writer.write(elected.get(j) + ", ");
                writer.write(elected.get(elected.size() -1).toString());
                writer.newLine();
                //Print vote distribution
                writer.write("Vote Distribution: " + wins.getCurrentScores().toString());
                writer.newLine();
                ArrayList<Integer> votes = wins.getCurrentVotes();
                writer.write("Abstentions: " +  (votes.size() - wins.getCurrentScores().getSum()));
                writer.newLine();
                //Print votes cast by each voter
                writer.write("Votes Cast (in order): ");
                writer.newLine();
                Iterator<Integer> iter = wins.getCurrentVotes().iterator();
                for (Integer v : order) {
                    writer.write("Voter " + v + ": Candidate " +  iter.next());
                    writer.newLine();
                }

            }
            writer.write("Time taken: " + time + " nanoseconds");
            writer.close();
            System.out.println("File " + path.getFileName().toString() + " done.");
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
}
