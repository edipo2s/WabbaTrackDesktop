package com.ediposouza;

import java.io.File;
import java.io.IOException;

/**
 * Created by Edipo on 06/05/2017.
 */
public class Updater {

    private static String FILE_NAME = "WabbaTrack.exe";
    private static String UPD_FILE_NAME = "lastVersion.exe";

    public static void main(String[] args) {
        System.out.println("Updating...");
        new File(FILE_NAME).delete();
        new File(UPD_FILE_NAME).renameTo(new File(FILE_NAME));
        try {
            Runtime.getRuntime().exec("cmd /c start /min " + FILE_NAME);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

}
