package de.paulsenik.jpl.io;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Class for easier file-access and handling filedata
 *
 * @since 2019-01-01
 */
public class PFolder {

  private PFile file;

  public PFolder(String path) {
    file = new PFile(path);
    if (!file.isFolder()) {
      throw new IllegalArgumentException("path does not point to a folder!");
    }
  }

  /**
   * @param folder Fileobject of folderlocation
   * @return All folders and their childfolders recursively inside of it
   */
  public static List<File> getAllFoldersOfRoot(File folder) {
    // TODO refactor with List of Strings
    if (folder != null) {
      File[] children = folder.listFiles((dir, name) -> new PFile(name).isFolder());
      if (children != null) {
        ArrayList<File> sum = new ArrayList<>();

        Collections.addAll(sum, children);

        for (File temp : children) {
          List<File> tempFiles = getAllFoldersOfRoot(temp); // recursion
          if (tempFiles != null) {
            sum.addAll(tempFiles);
          }
        }
        return sum;
      }
    }
    return null;
  }

  public List<String> getSubFolders() {
    List<String> list = new ArrayList<>();
    for (String temp : Objects.requireNonNull(file.list())) {
      if (new PFile(temp).isFolder()) {
        list.add(file.getPath() + File.separator + temp);
      }
    }
    return list;
  }

  /**
   * @param fileType of Files (if is null => Filetypes will be ignored)
   */
  public List<String> getFiles(String fileType) {
    List<String> list = new ArrayList<>();
    for (String temp : Objects.requireNonNull(file.list())) {
      if (new PFile(temp).isFile()) {
        if (fileType == null || fileType.isBlank() || temp.endsWith(fileType)) {
          list.add(file.getPath() + File.separator + temp);
        }
      }
    }
    return list;
  }

  public boolean delete() {
    return file.delete();
  }

  public String getName() {
    return file.getName();
  }

  public boolean createFolder() {
    return file.mkdirs();
  }

}
