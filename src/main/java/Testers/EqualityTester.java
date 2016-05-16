package Testers;

import Elections.*;
import Model.PreferenceList;
import Model.VotingOrder;
import Model.VotingRule;
import VotingRules.PluralityVR;
import me.ariapar.Processor.SampleFileProcessor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by AriApar on 25/04/2016.
 */
public class EqualityTester extends AbstractTester{

    public static void main(String[] args) throws Exception {
        //ExecutorService threadPool = Executors.newFixedThreadPool(2);



        File[] files = new File(Paths.get("res", "PListSamples").toString())
                .listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        //return name.contains("2x10S") || name.contains("3x10S");
                        //return name.contains("4x10S") || name.contains("5x10S");
                        //return name.contains("4x10Sample120");
                        return name.equals("3x10Sample2");
                    }
                });

        Arrays.sort(files);

        for (File file : files) {
            Scanner in = new Scanner(file);
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

            ElectionParameters dpParams = new ElectionParameters(pref, order, rule, ElectionType.DPWITHCOSTLYABS);
            ElectionParameters treeParams = new ElectionParameters(pref, order, rule, ElectionType.GAMETREEWITHCOSTLYABS);

            Election dpE = ElectionFactory.create(dpParams);
            Election treeE = ElectionFactory.create(treeParams);


            ArrayList<ElectionState> dpWinners = dpE.findNE();
            ArrayList<ElectionState> treeWinners = treeE.findNE();
            boolean result = compareResults(dpWinners, treeWinners);
            if (result) {
                System.out.println(file.getName() + " matches.");
                printResults(order, dpWinners);
            }
            else {
                System.out.println(file.getName() + " DOES NOT MATCH");
                printResults(order, dpWinners);
                printResults(order, treeWinners);
                break;
            }
        }

    }

    private static boolean compareResults(ArrayList<ElectionState> winners1, ArrayList<ElectionState> winners2) {
        winners2.removeAll(winners1);
        return winners2.size() == 0;
    }
}
