package sampleGenerator;

/**
 * Created by AriApar on 21/04/2016.
 */
public class SampleFileGenerator {

    private static int instancePerConfig = 200;

    public static void main(String[] args) throws InterruptedException{
        Integer[] noVoters = new Integer[]{10, 15, 20, 25, 50, 75, 100, 120, 125, 150, 175, 200};
        Integer[] noCandidates = new Integer[]{2,3,4,5,6,7,8};
        for (Integer candValue : noCandidates) {
            for (Integer voterValue : noVoters){
                SampleFileWriter writer = new SampleFileWriter(voterValue,candValue,"res/PListSamples/");
                for (int i = 0; i < instancePerConfig; i++){
                    writer.writeSampleFile(i);
                    // wait a bit so random generator doesn't return same permutations.
                    Thread.sleep(5);
                }
                System.out.println("Voter size: " + voterValue + ", cand size: " + candValue + " complete.");
            }
        }
    }
}
