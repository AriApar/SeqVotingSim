package Testers;

import java.io.File;

/**
 * Created by AriApar on 13/01/2016.
 */
public class AbstractTester {

    public static File getFile(String fileName) {

        //Get file from resources folder

        File file = new File("res/PlistExamples/" + fileName);
        return file;
    }
}
