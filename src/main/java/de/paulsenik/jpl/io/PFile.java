package de.paulsenik.jpl.io;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

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
    return Arrays.stream(s.split("[ \\t]"))
        .filter(x -> (!"".equals(x)))
        .toArray(String[]::new);
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
  public synchronized String[] getParagraphs() throws IOException {
    return getParagraphs(getFileAsString());
  }

  /**
   * @return Array of Strings that were seperated with SPACE and LINES
   */
  public synchronized String[] getAllParagraphs() throws IOException {
    String s = getFileAsString();
    return Arrays.stream(s.split("[ \\n\\r\\t]"))
        .filter(x -> (!"".equals(x)))
        .toArray(String[]::new);
  }

  /**
   * @return File as one String
   */
  public synchronized String getFileAsString() throws FileNotFoundException, IOException {
    StringBuilder s = new StringBuilder();
    BufferedReader reader = new BufferedReader(
        new InputStreamReader(
            new FileInputStream(this),
            StandardCharsets.UTF_8));
    int c;
    while ((c = reader.read()) != -1) {
      char character = (char) c;
      s.append(character);
    }
    return s.toString();
  }

  /**
   * @return Array of Strings that were seperated with LINES and not with space
   */
  public synchronized String[] getLines() throws IOException {
    String s = getFileAsString();
    return Arrays.stream(s.split("[\\n]"))
        .filter(x -> (!"".equals(x)))
        .toArray(String[]::new);
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
