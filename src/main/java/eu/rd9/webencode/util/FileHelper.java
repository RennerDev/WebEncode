package eu.rd9.webencode.util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by dominicrenner on 18.12.16.
 */
public class FileHelper {

    public static boolean isCompletelyWritten(File file) {
        RandomAccessFile stream = null;
        try {
            stream = new RandomAccessFile(file, "rw");
            return true;
        } catch (Exception e) {
            System.err.println("Skipping file " + file.getName() + " for this iteration due it's not completely written");
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    System.err.println("Exception during closing file " + file.getName());
                }
            }
        }
        return false;
    }

}
