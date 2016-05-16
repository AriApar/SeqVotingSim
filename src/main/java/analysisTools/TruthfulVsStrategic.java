package analysisTools;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.MatchResult;

/**
 * Created by AriApar on 08/05/2016.
 */
public class TruthfulVsStrategic {
    public static void main(String[] args) throws Exception {
        Integer[] noVoters = new Integer[]{10, 15, 20, 25, 50, 75, 100, 120, 125, 150, 175, 200};
        for (int candCount = 2; candCount < 9; candCount++) {
            for (Integer noVoter : noVoters) {
                final int candNo = candCount;
                File[] strategicFiles = new File(Paths.get("/Users/AriApar/Documents"
                        ,"lazy_wo_cost"
                        ,"results").toString())
                        .listFiles(new FilenameFilter() {
                            @Override
                            public boolean accept(File dir, String name) {
                                return (name.contains(candNo + "x" + noVoter + "Sample"));
                            }
                        });

                Arrays.sort(strategicFiles);

                long diffVote = 0;
                long votercount = 0;
                for (int index = 0; index < strategicFiles.length; index++) {
                    File strategicFile = strategicFiles[index];
                    File truthfulFile = new File(Paths.get("truthful", "results", strategicFile.getName()).toString());
                    Path strategicFilePath = strategicFile.toPath();
                    Path truthfulFilePath = truthfulFile.toPath();

                    if (Files.isRegularFile(strategicFilePath) && Files.isRegularFile(truthfulFilePath)) {
                        ArrayList<Integer> differingVoteCounts =
                                countDifferingVotes(strategicFile, truthfulFile, noVoter);
                        ArrayList<Integer> absCount = readAbstentions(strategicFile);
                        for (int i = 0; i < differingVoteCounts.size(); i++) {
                            Integer differingVoteCount = differingVoteCounts.get(i);
                            Integer actualVoteCount = noVoter - absCount.get(i);
                            votercount += actualVoteCount;
                            diffVote += differingVoteCount;
                        }
                    }
                }

                long overlapcount = votercount - diffVote;
                System.out.println("Stats for candidate size " + candCount + ", voter size: " + noVoter);
                System.out.println("Number of overlapping votes: " + overlapcount + " out of " + votercount);
                System.out.println("Percentage of voters: " + (overlapcount * 100.0 / votercount));
                System.out.println();
            }
        }
    }

    private static ArrayList<Integer> countDifferingVotes(File strategicFile, File truthfulFile, int noVoter) {
        ArrayList<Integer> res = new ArrayList<>();
        try {
            Scanner inS = new Scanner(strategicFile);
            inS.findInLine("This election has (\\d+) Nash equilibria!");
            MatchResult result = inS.match();
            for (int i=1; i<=result.groupCount(); i++) {
                int diffCount = 0;
                Scanner inT = new Scanner(truthfulFile);
                for (int j = 0; j < noVoter; j++) {
                    inS.findWithinHorizon("Voter (\\d+): Candidate (\\d+)", 0);
                    inT.findWithinHorizon("Voter (\\d+): Candidate (\\d+)", 0);
                    MatchResult resultStrategic = inS.match();
                    MatchResult resultTruthful = inT.match();
                    if (!resultStrategic.group(1).equals(resultTruthful.group(1)))
                        throw new RuntimeException("Voter IDs don't match, perhaps read the wrong file?");
                    else if (!resultStrategic.group(2).equals("0")
                            && !resultStrategic.group(2).equals(resultTruthful.group(2)))
                        diffCount +=1;
                }
                res.add(diffCount);
                inT.close();
            }
            inS.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static ArrayList<Integer> readAbstentions(File file) {
        return ExpectedAbstention.readAbstentions(file);
    }
}
