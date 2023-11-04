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
    initVars();
  }

  /**
   * copys file to a new location and replaces the existing one
   *
   * @param source is path of File
   * @param target is new Path to File
   * @return success
   */
  public static boolean copyFile(File source, File target) {
    try {
      Files.copy(source.toPath(), target.toPath(),
          StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException | NullPointerException e) {
      return false;
    }
    return true;
  }

  private static String[] getParagraphs(String s) {
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

  /**
   * Initializes type and name of the file
   */
  private void initVars() {
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
