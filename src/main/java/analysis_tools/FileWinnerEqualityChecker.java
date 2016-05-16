package analysis_tools;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.regex.MatchResult;

/**
 * Created by AriApar on 10/05/2016.
 */
public class FileWinnerEqualityChecker {
    public static void main(String[] args) {
        try {

            File[] files = new File(Paths.get("/Users/AriApar/Dropbox",
                    //"truthful",
                    "results").toString()).listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {

                    return //(name.contains("8x"));

                            (name.contains("2x") || name.contains("3x") || name.contains("4x") || name.contains("5x") || name.contains("6x")
                                    || name.contains("7x") || name.contains("8x"));
                }
            });

            for (File f: files) {
                Path filePath = f.toPath();
                File resFile = new File(Paths.get("/Users/AriApar/Documents",
                        //"truthful",
                        "lazy_wo_cost", "results", f.getName()).toString());
                if (Files.isRegularFile(filePath) && resFile.exists() && !resFile.isDirectory()) {
                    if (!matchesWinners(f, resFile)) System.out.println("Winners for " + f.getName() + " doesn't match");
                }
            }
            //System.out.println("Total used samples with Condorcet winners: " + condorcetCount);
            //System.out.println("Samples where condorcet winners are definite election winners: " + condorcetMatchesResult);
            /*Files.walk(Paths.get("res", "PListSamples")).forEach(filePath -> {
                //System.out.println(filePath.getFileName().toString());
                if (Files.isRegularFile(filePath) && !filePath.getFileName().toString().equals(".DS_Store")) {
                    runSampleFromPath(filePath, args);
                    System.out.println("File " + filePath.getFileName().toString() + " done.");
                }
            });*/
            //long lEndTime = System.currentTimeMillis();
            //long difference = lEndTime - lStartTime;

            //System.out.println("Elapsed milliseconds: " + difference);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean matchesWinners(File f1, File f2) {
        boolean res = true;
        try {
            Scanner in1 = new Scanner(f1);
            Scanner in2 = new Scanner(f2);

            in1.findInLine("This election has (\\d+) Nash equilibria!");
            MatchResult result = in1.match();
            //System.out.println(result.groupCount());
            for (int i=1; i<=result.groupCount(); i++) {

                in1.findWithinHorizon("The winner is candidate\\(s\\) (\\w*)", 0);
                MatchResult winnerResult1 = in1.match();
                in2.findWithinHorizon("The winner is candidate\\(s\\) (\\w*)", 0);
                MatchResult winnerResult2 = in2.match();
                //System.out.println(winnerResult.groupCount());
                //what we want is for the first group to match the parameter passed
                //or the second parameter should include the parameter
                //res = res && (winnerResult.group(2)== null) && (new Integer(winnerResult.group(1)) == condorcetWinnerCand);
                res = res && (winnerResult1.group(1).equals(winnerResult2.group(1)));
                //if (!res) System.out.println(winnerResult.group(1) + "-" + ((winnerResult.group(2)!=null) ? winnerResult.group(2) : "asd"));
            }
            in1.close();
            in2.close();
        } catch (IOException e) {
            e.printStackTrace();
            res = false;
        }
        return res;
    }
}
