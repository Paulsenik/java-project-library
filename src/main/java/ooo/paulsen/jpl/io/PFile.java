package ooo.paulsen.jpl.io;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Class for easier file-access and handling filedata
 *
 * @author Paul Seidel
 * @version 1.0.5
 * @since 2019-01-01 : Updated 2022-03-14
 */

public class PFile {

    /**
     * @return Filename without ending and folders, where its located
     */
    public static String getName(String path) {
        StringBuilder s = new StringBuilder();
        boolean hasBeenType = false;
        for (int i = path.length() - 1; i >= 0; i--) {
            if (hasBeenType) {
                if (path.charAt(i) == '/' || path.charAt(i) == ((char) 92)) {
                    return s.toString();
                } else {
                    s.insert(0, path.charAt(i));
                }
            } else {
                if (path.charAt(i) == '.')
                    hasBeenType = true;
            }
        }
        return s.toString();
    }

    public static String getNameAndType(String path) {
        StringBuilder s = new StringBuilder();
        for (int i = path.length() - 1; i >= 0; i--) {
            if (path.charAt(i) == '/' || path.charAt(i) == ((char) 92)) {
                return s.toString();
            } else {
                s.insert(0, path.charAt(i));
            }
        }
        return s.toString();
    }

    public static String getParentFolder(String path) {
        return new File(path).getParent();
    }

    public static boolean deleteFile(String path) {
        return new File(path).delete();
    }

    /**
     * copys file if directory is a new one and renames it
     *
     * @param file   is path of File
     * @param target is new Path to File
     * @return success
     */
    public static boolean copyFile(String file, String target, boolean replaceIfExists) {

        String nPath = target;

        if (!replaceIfExists) {
            String addon = " - copy"; // l=8
            int copyCount = 0;
            while (nPath != null && new File(nPath).exists()) {
                if (copyCount > 0) { // remove " - copy0"
                    String nnP = remove(nPath, nPath.length() - (addon.length() + PFile.getFileType(nPath).length()) - 2,
                            nPath.length() + PFile.getFileType(nPath).length());
                    nPath = nnP + "." + PFile.getFileType(nPath);
                }
                nPath = PFile.getParentFolder(nPath) + "/" + PFile.getName(nPath) + addon + copyCount + '.'
                        + PFile.getFileType(nPath);
                copyCount++;
            }
        }

        try {
            Files.copy(new File(file).toPath(), new File(nPath).toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException | NullPointerException e) {
            System.err.println("[PFile] :: copyFile :: an error occured trying to copy from " + file + " to " + target);
            return false;
        }

//		System.out.println("[PFile] :: copyFile :: from " + file + " to " + target);

        return true;
    }

    private static String remove(String s, int a, int b) {
        StringBuilder sN = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (!(i >= a && i < b))
                sN.append(s.charAt(i));
        }
        return sN.toString();
    }

    /**
     * @param file path
     * @return filetype (e.g. "txt" "png" "wav" ...)
     */
    public static String getFileType(String file) {
        StringBuilder fileType = new StringBuilder();
        for (int i = file.length() - 1; i >= 0; i--) {
            if (file.charAt(i) == '.') {
                return fileType.toString();
            } else {
                fileType.insert(0, file.charAt(i));
            }
        }
        return null;
    }

    private final String path;

    public PFile(String path) {
        this.path = path;
        File file = new File(path);

        if (!file.exists()) {
            try {
                file.createNewFile();
//				System.out.println("[PFile] :: " + getName(path) + "." + getFileType(path) + " created");
            } catch (IOException e) {
            }
        }
    }

    /**
     * @return Array of Strings that were seperated with SPACE and not with lines
     */
    public synchronized String[] getParagraphs() {
        return getParagraphs(getFileAsString());
    }

    public static String[] getParagraphs(String s) {
        ArrayList<String> out = new ArrayList<>();

        StringBuilder lastword = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (!(s.charAt(i) == ' ')) {
                lastword.append(s.charAt(i));
            } else if (!lastword.toString().trim().isEmpty()) {
                out.add(lastword.toString());
                lastword = new StringBuilder();
            } else {
                lastword = new StringBuilder();
            }
        }
        if (lastword.length() != 0) {
            out.add(lastword.toString());
        }
        String[] array = new String[out.size()];
        for (int i = 0; i < out.size(); i++)
            array[i] = out.get(i);
        return array;
    }

    /**
     * @return Array of Strings that were seperated with SPACE and LINES
     */
    public synchronized String[] getAllParagraphs() {
        if (exists()) {
            String[] s = getLines();
            ArrayList<String> out = new ArrayList<>();

            for (String line : s) {
                StringBuilder lastword = new StringBuilder();
                for (int i = 0; i < line.length(); i++) {
                    if (!(line.charAt(i) == ' ')) {
                        lastword.append(line.charAt(i));
                    } else {
                        out.add(lastword.toString());
                        lastword = new StringBuilder();
                    }
                }
                if (lastword.length() != 0) {
                    out.add(lastword.toString());
                }
            }

            String[] array = new String[out.size()];
            for (int i = 0; i < out.size(); i++)
                array[i] = out.get(i);
            return array;
        }
        return null;
    }

    /**
     * @return File as one String
     */
    public synchronized String getFileAsString() {
        StringBuilder s = new StringBuilder();
        if (exists()) {
            Scanner scn;
            try {
                scn = new Scanner(new File(path));
                while (scn.hasNextLine())
                    s.append(scn.nextLine());
                scn.close();
            } catch (FileNotFoundException ignored) {
            }
        }
        return s.toString();
    }

    /**
     * @return Array of Strings that were seperated with LINES and not with space
     */
    public synchronized String[] getLines() {
        ArrayList<String> lines = new ArrayList<>();

        if (exists()) {
            Scanner scn;
            try {
                scn = new Scanner(new File(path));
                while (scn.hasNextLine())
                    lines.add(scn.nextLine());
                scn.close();
            } catch (FileNotFoundException ignored) {
            }
        }

        String[] l = new String[lines.size()];
        for (int i = 0; i < lines.size(); i++)
            l[i] = lines.get(i);
        return l;
    }

    /**
     * Overwrites file with inhalt
     *
     * @param content contains whole fileContent (including linebreaks)
     */
    public synchronized void writeFile(String content) {
        try {
            FileWriter fileWriter;
            fileWriter = new FileWriter(path);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.print(content);
            printWriter.close();
        } catch (IOException ignored) {
        }
    }

    public String getAbsolutePath() {
        return new File(path).getAbsolutePath();
    }

    public synchronized boolean exists() {
        return new File(path).exists();
    }

    public synchronized boolean delete() {
        return new File(path).delete();
    }

}
