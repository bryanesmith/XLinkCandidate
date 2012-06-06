/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.proteomecommons.xlinkcandidate.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.Writer;

/**
 *
 * @author besmit
 */
public class IOUtil {
    /**
     * <p>Recursively deletes all the files in the directory. If fails, throws RuntimeException.</p>
     * @param dir
     * @return The directory passed as a parameter.
     */
    public static File recursiveDelete(File dir) {
        if (dir != null && dir.exists()) {
            if (dir.isDirectory()) {
                for (String fname : dir.list()) {
                    File ff = new File(dir, fname);
                    recursiveDelete(ff);
                }
            }
            if (!dir.delete()) {
                if (dir.exists()) {
                    throw new RuntimeException("Can't delete " + dir);
                }
            }
        }

        return dir;
    }

    /**
     * <p>A helper method that trys really hard to make sure a file is deleted. If the file can't be deleted, it is reduced to a size of 1. This method is particuarlly helpful for the Knoppix LiveCDs and making sure temporary files are deleted. If they aren't they'll quickly saturate the ramdisk and cause "Out of disk space" errors.</p>
     * @param file The file to delete.
     */
    public static void safeDelete(File file) {
        if (file == null) {
            return;
        }
        if (!file.delete() && file.exists() && !file.isDirectory()) {
            FileOutputStream temp = null;
            try {
                temp = new FileOutputStream(file);
                temp.write('0');
            } catch (Exception e) {
            } finally {
                safeClose(temp);
            }
        }
    }

    /**
     * <p>A helper method to safely close the given OutputStream object via invoking flush() followed by close(). Any exceptions thorwn are discarded silently, including NullPointerExceptions that may be thrown if the reference is null.</p>
     * @param out OuputStream to safely close.
     */
    public static final void safeClose(OutputStream out) {
        if (out != null) {
            try {
                out.flush();
            } catch (Exception e) {
            }
            try {
                out.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * <p>A helper method to safely close the given FileWriter object via invoking flush() followed by close(). Any exceptions thorwn are discarded silently, including NullPointerExceptions that may be thrown if the reference is null.</p>
     * @param out OuputStream to safely close.
     */
    public static final void safeClose(Writer out) {
        if (out != null) {
            try {
                out.flush();
            } catch (Exception e) {
            }
            try {
                out.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * <p>Safely closes an InputStream by invoking the close() method. Any errors thrown are silently discard, and no NullPointerException is thrown if the InputStream reference passed is null.</p>
     * @param in InpuStream to safely close.
     */
    public static final void safeClose(InputStream in) {
        if (in != null) {
            try {
                in.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * <p>Safely close off resources used by RandomAccessFile.</p>
     * @param ras
     */
    public static final void safeClose(RandomAccessFile ras) {
        if (ras != null) {
            try {
                ras.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * <p>Safely close off resources used by any Reader.</p>
     * @param in
     */
    public static void safeClose(Reader in) {
        if (in != null) {
            try {
                in.close();
            } catch (Exception e) {
            }
        }
    }
}
