package analysisTools;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
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
                    "results").toString()).listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return (name.contains("2x") || name.contains("3x") || name.contains("4x") ||
                            name.contains("5x") || name.contains("6x") || name.contains("7x") || name.contains("8x"));
                }
            });

            for (File f: files) {
                Path filePath = f.toPath();
                File resFile = new File(Paths.get("/Users/AriApar/Documents",
                        "lazy_wo_cost", "results", f.getName()).toString());
                if (Files.isRegularFile(filePath) && resFile.exists() && !resFile.isDirectory()) {
                    if (!matchesWinners(f, resFile))
                        System.out.println("Winners for " + f.getName() + " doesn't match");
                }
            }
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
            for (int i=1; i<=result.groupCount(); i++) {
                in1.findWithinHorizon("The winner is candidate\\(s\\) (\\w*)", 0);
                MatchResult winnerResult1 = in1.match();
                in2.findWithinHorizon("The winner is candidate\\(s\\) (\\w*)", 0);
                MatchResult winnerResult2 = in2.match();
                res = res && (winnerResult1.group(1).equals(winnerResult2.group(1)));
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
