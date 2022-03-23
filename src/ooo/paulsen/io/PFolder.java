package ooo.paulsen.io;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Class for easier file-access and handling filedata
 *
 * @author Paul Seidel
 * @version 1.0.5
 * @since 2019-01-01 : Updated 2022-03-14
 */
public class PFolder {

    public static File createFolder(String path) {
        if (isFile(path))
            return null;
        File f = new File(path);
        if (f.mkdirs())
            return f;
        return null;
    }

    public static boolean isFolder(String path) {
        return (!isFile(path));
    }

    public static boolean isFolder(File f) {
        return ((!isFile(f.getAbsolutePath())));
    }

    public static boolean isFile(String path) {
        if (path == null)
            return false;
        for (int i = path.length() - 1; i >= 0; i--) {
            if (path.charAt(i) == '/' || path.charAt(i) == ((char) 92))
                return false;
            if (path.charAt(i) == '.')
                return true;
        }
        return new File(path).isFile();
    }

    public static String getName(String path) {
        String s = "";
        for (int i = path.length() - 1; i >= 0; i--) {
            if (path.charAt(i) == '/' || path.charAt(i) == ((char) 92))
                break;
            s = path.charAt(i) + s;
        }
        return s;
    }

    public static boolean deleteFolder(String path) {
        if (isFile(path))
            return false;
        File f = new File(path);
        return f.delete();
    }

    /**
     * @param path     of Folder
     * @param fileType of Files (if is null => Filetypes will be ignored)
     * @return
     */
    public static String[] getFiles(String path, String fileType) {
        String[] s = new File(path).list();
        if (s == null || s.length == 0)
            return null;

        ArrayList<String> list = new ArrayList<>();
        for (String temp : s)
            if (isFile(temp))
                if (fileType == null || temp.endsWith(fileType))
                    list.add(path + "/" + temp);

        String sN[] = new String[list.size()];
        for (int i = 0; i < sN.length; i++)
            sN[i] = list.get(i);
        return sN;
    }

    public static String[] getSubFolders(String path) {
        String s[] = new File(path).list();
        if (s == null || s.length == 0)
            return null;

        ArrayList<String> list = new ArrayList<>();
        for (String temp : s)
            if (!isFile(temp))
                list.add(path + "/" + temp);

        String sN[] = new String[list.size()];
        for (int i = 0; i < sN.length; i++) {
            sN[i] = list.get(i);
        }
        return sN;
    }

    /**
     * @param folder Fileobject of folderlocation
     * @return All folders and their childfolders recursively inside of it
     */
    public static ArrayList<File> getAllFoldersOfRoot(File folder) {

        if (folder != null) {
            File[] children = folder.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return PFolder.isFolder(name);
                }
            });
            if (children != null || (children != null && children.length == 0)) {
                ArrayList<File> sum = new ArrayList<>();

                Collections.addAll(sum, children);

                for (File temp : children) {
                    ArrayList<File> tempFiles = getAllFoldersOfRoot(temp); // recursion
                    if (tempFiles != null) {
                        sum.addAll(tempFiles);
                    }
                }
                return sum;
            }
        }
        return null;
    }

}
