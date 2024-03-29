package analysisTools;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.MatchResult;

/**
 * Created by AriApar on 05/05/2016.
 */
public class CondorcetWinnerLikelihood {
    public static void main(String[] args) {
        try {
            File[] files = new File(Paths.get("res", "PListSamples").toString()).listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {

                    return (name.contains("3x") || name.contains("4x") || name.contains("5x") || name.contains("6x")
                            || name.contains("7x") || name.contains("8x"));
                }
            });

            int condorcetCount = 0; int condorcetMatchesResult = 0;
            for (File f: files) {
                Path filePath = f.toPath();
                File resFile = new File(Paths.get("/Users/AriApar/Documents",
                        "lazy_wo_cost",
                        "results", f.getName()).toString());
                if (Files.isRegularFile(filePath) && resFile.exists() && !resFile.isDirectory()) {
                    int condorcetWinnerCand = checkForCondorcetWinner(filePath);
                    if (condorcetWinnerCand != 0) {
                        if (mathcesWinnerOfResult(f.getName(), condorcetWinnerCand)) condorcetMatchesResult +=1;
                        condorcetCount +=1;
                    }
                }
            }
            System.out.println("Total used samples with Condorcet winners: " + condorcetCount);
            System.out.println("Samples where condorcet winners are election winners: " + condorcetMatchesResult);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean mathcesWinnerOfResult(String name, int condorcetWinnerCand) {
        boolean res = true;
        try {
            Scanner in = new Scanner(Paths.get("/Users/AriApar/Documents",
                    "lazy_wo_cost",
                    "results", name));
            in.findInLine("This election has (\\d+) Nash equilibria!");
            MatchResult result = in.match();
            for (int i=1; i<=result.groupCount(); i++) {
                in.findWithinHorizon("The winner is candidate\\(s\\) (\\d+)(,\\s*\\d+)*", 0);
                MatchResult winnerResult = in.match();
                //what we want is for the first group to match the parameter passed
                //or the second parameter should include the parameter
                res = res && ((new Integer(winnerResult.group(1)) == condorcetWinnerCand) ||
                        ((winnerResult.group(2)!= null) &&
                                winnerResult.group(2).contains(Integer.toString(condorcetWinnerCand))));
                if (!res)
                    System.out.println(name + ", " + condorcetWinnerCand + ", "
                            + winnerResult.group(1) + ((winnerResult.group(2)!=null) ? winnerResult.group(2) : ""));
            }
            in.close();
        } catch (NoSuchFileException e) {
            System.out.println("This input file has not been tested.");
            res = false;
        } catch (IOException e) {
            e.printStackTrace();
            res = false;
        }
        return res;
    }

    public static int checkForCondorcetWinner(Path p) {
        int res = 0;
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
            in.close();

            //initialize and fill pairwise candidate 2d array
            int[][] pairwiseCand = new int[candidates][candidates];
            for (int candIndex = 0; candIndex < candidates; candIndex++) {
                for (int otherCandIndex = candIndex; otherCandIndex < candidates; otherCandIndex++) {
                    if (otherCandIndex == candIndex) pairwiseCand[candIndex][candIndex] = candIndex;
                    else {
                        int chosenCandIndex = majorityWinner(candIndex, otherCandIndex, prefList);
                        pairwiseCand[candIndex][otherCandIndex] = chosenCandIndex;
                        pairwiseCand[otherCandIndex][candIndex] = chosenCandIndex;
                    }
                }
            }
            //check 2d array
            for (int candNo = 1; candNo <= candidates; candNo++) {
                boolean done = true;
                for (int index = 0; index < candidates; index++) {
                    done = done && (pairwiseCand[candNo-1][index] == candNo-1);
                }
                if (done) {
                    res = candNo;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(p.toString());

        }
        return res;
    }

    private static int majorityWinner(int candIndex, int otherCandIndex, int[][] prefList) {
        int candCount = 0;
        for (int i = 0; i < prefList.length; i++) {
            for (int j = 0; j < prefList[0].length; j++) {
                if (prefList[i][j] == candIndex+1 || prefList[i][j] == otherCandIndex+1) {
                    //This is the first of the two indices I've seen, so increment and break
                    if (prefList[i][j] == candIndex+1) candCount +=1;
                    else if (prefList[i][j] == otherCandIndex+1) candCount -=1;
                    break;
                }
            }
        }
        if (candCount == 0) return -1;
        else if (candCount > 0) return candIndex;
        else return otherCandIndex;
    }
}
