package de.paulsenik.jpl.io;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Class for easier file-access and handling file data
 *
 * @since 2019-01-01
 */

public class PFile extends File {

  private String name;
  private String type;

  private PFile() {
    super("");
  }

  public PFile(String path) {
    super(path);

    setNameAndType();
  }

  public static void main(String[] args) {
    System.out.println(new PFile("test.txt").getName());
    System.out.println(new PFile("test.txt").getType());
    System.out.println(new PFile("").getName());
    System.out.println(new PFile("").getType());
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
          String nnP = remove(nPath,
              nPath.length() - (addon.length() + PFile.getFileType(nPath).length()) - 2,
              nPath.length() + PFile.getFileType(nPath).length());
          nPath = nnP + "." + PFile.getFileType(nPath);
        }
        nPath = new PFile(nPath).getParent() + "/" + new PFile(nPath).getName() + addon + copyCount
            + '.'
            + PFile.getFileType(nPath);
        copyCount++;
      }
    }

    try {
      Files.copy(new File(file).toPath(), new File(nPath).toPath(),
          StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException | NullPointerException e) {
      return false;
    }

    return true;
  }

  private static String remove(String s, int a, int b) {
    return s.substring(0, a) + s.substring(b);
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
    for (int i = 0; i < out.size(); i++) {
      array[i] = out.get(i);
    }
    return array;
  }

  private void setNameAndType() {
    StringBuilder s = new StringBuilder();
    for (int i = getPath().length() - 1; i >= 0; i--) {
      if (getPath().charAt(i) == File.separatorChar) {
        break;
      } else if (getPath().charAt(i) == '.') {
        if (!s.isEmpty()) {
          type = s.toString();
          s = new StringBuilder();
        }
      } else {
        s.insert(0, getPath().charAt(i));
      }
    }
    if (!s.isEmpty()) {
      name = s.toString();
    }
  }

  /**
   * E.g. "/home/user/test.txt".getName() -> "test"
   *
   * @return Only the Filename without the filetype or folder-location
   */
  @Override
  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }

  /**
   * @return Array of Strings that were seperated with SPACE and not with lines
   */
  public synchronized String[] getParagraphs() {
    return getParagraphs(getFileAsString());
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
      for (int i = 0; i < out.size(); i++) {
        array[i] = out.get(i);
      }
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
        scn = new Scanner(this);
        while (scn.hasNextLine()) {
          s.append(scn.nextLine());
        }
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
        scn = new Scanner(this);
        while (scn.hasNextLine()) {
          lines.add(scn.nextLine());
        }
        scn.close();
      } catch (FileNotFoundException ignored) {
      }
    }

    String[] l = new String[lines.size()];
    for (int i = 0; i < lines.size(); i++) {
      l[i] = lines.get(i);
    }
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
      fileWriter = new FileWriter(getPath());
      PrintWriter printWriter = new PrintWriter(fileWriter);
      printWriter.print(content);
      printWriter.close();
    } catch (IOException ignored) {
    }
  }

}
