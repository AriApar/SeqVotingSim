package analysisTools;


import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.MatchResult;

/**
 * Created by AriApar on 04/05/2016.
 */
public class ExpectedAbstention {

    public static void main(String[] args) {
        Integer[] noVoters = new Integer[]{10, 15, 20, 25, 50, 75, 100, 120, 125, 150, 175, 200};
        for (int candCount = 2; candCount < 9; candCount++) {
            for (Integer noVoter : noVoters) {
                final int candNo = candCount;
                File[] files = new File(Paths.get("/Users/AriApar/Documents", "lazy_wo_cost", "results").toString())
                        .listFiles(new FilenameFilter() {
                            @Override
                            public boolean accept(File dir, String name) {
                                return (name.contains(candNo + "x" + noVoter + "Sample"));
                            }
                        });

                long abscount = 0;
                long votercount = 0;
                for (File file : files) {
                    Path filePath = file.toPath();
                    if (Files.isRegularFile(filePath)) {
                        ArrayList<Integer> abstNos = readAbstentions(file);
                        for (Integer abstNo : abstNos) {
                            votercount += noVoter;
                            abscount += abstNo;
                        }
                    }
                }

                long votecount = votercount - abscount;
                System.out.println("Stats for candidate size " + candCount + ", voter size: " + noVoter);
                System.out.println("Number of voters voted: " + votecount + " out of " + votercount);
                System.out.println("Percentage of voters: " + (votecount * 100.0 / votercount));
                System.out.println();
            }
        }
    }

    public static ArrayList<Integer> readAbstentions(File file) {
        ArrayList<Integer> res = new ArrayList<>();
        try {
            Scanner in = new Scanner(file);
            in.findInLine("This election has (\\d+) Nash equilibria!");
            MatchResult result = in.match();
            for (int i=1; i<=result.groupCount(); i++) {
                in.findWithinHorizon("Abstentions: (\\d+)", 0);
                result = in.match();
                res.add(new Integer(result.group(i)));
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }
}
