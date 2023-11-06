package de.paulsenik.jpl.io;

import java.io.File;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

public class PFileTest {

  private static final String TEST_STRING = "Just Another Text\n[µ] are unusual characters\n\n123\n\n      heyho";

  @Test
  public void test_emptyPath() {
    PFile f = new PFile("");
    Assert.assertNull(f.getName());
    Assert.assertNull(f.getType());
  }

  @Test
  public void test_normalPath() {
    PFile f = new PFile("test123.txt");
    Assert.assertEquals(f.getName(), "test123");
    Assert.assertEquals(f.getType(), "txt");
  }

  @Test
  public void test_noTypePath() {
    PFile f = new PFile("test123");
    Assert.assertEquals(f.getName(), "test123");
    Assert.assertNull(f.getType());
  }

  @Test
  public void test_longPath() {
    PFile f = new PFile(
        File.separator + "just" + File.separator + "another" + File.separator + "file.txt");
    Assert.assertEquals(f.getName(), "file");
    Assert.assertEquals(f.getType(), "txt");
  }

  @Test
  public void test_write() throws IOException {
    PFile f = new PFile("test123.txt");
    try {
      f.writeFile(TEST_STRING);
      Assert.assertEquals(f.getFileAsString(), TEST_STRING);
    } catch (IOException e) {
      f.delete();
      throw e;
    }
    Assert.assertTrue(f.delete());
  }

  @Test
  public void test_copyFile() {
    // TODO
  }

  @Test
  public void test_getParagraphs() {
    // TODO
  }

  @Test
  public void test_getAllParagraphs() {
    // TODO
  }

  @Test
  public void test_getFileAsString() {
    // TODO
  }

  @Test
  public void test_getLines() {
    // TODO
  }

}
