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
  public void test_multipleTypePath() {
    PFile f = new PFile("test123.rc.old.md.txt");
    Assert.assertEquals(f.getName(), "test123.rc.old.md");
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
  public void test_writeRead() throws IOException {
    PFile f = new PFile("test123.txt");
    try {
      f.writeFile(TEST_STRING);
      Assert.assertEquals(f.getFileAsString(), TEST_STRING);
    } catch (IOException e) {
      Assert.assertTrue(f.delete());
      throw e;
    }
    Assert.assertTrue(f.delete());
  }

  @Test
  public void test_copyFile() {
    PFile f2 = new PFile("temp123-copy.txt");
    PFile f = new PFile("test123.txt");
    f.writeFile("test");
    Assert.assertTrue(PFile.copyFile(f, f2));
    Assert.assertTrue(f.delete());
    Assert.assertTrue(f2.delete());
  }

  @Test
  public void test_getParagraphs() throws IOException {
    PFile f = new PFile("asdf");
    f.writeFile("asdf jklö\n\t   g\n h");
    Assert.assertArrayEquals(new String[]{"asdf", "jklö\n", "g\n", "h"}, f.getParagraphs());
    Assert.assertTrue(f.delete());
  }

  @Test
  public void test_getAllParagraphs() throws IOException {
    PFile f = new PFile("asdf");
    f.writeFile("asdf jklö\n\t   g\n   h");
    Assert.assertArrayEquals(new String[]{"asdf", "jklö", "g", "h"}, f.getAllParagraphs());
    Assert.assertTrue(f.delete());
  }

  @Test
  public void test_getLines() throws IOException {
    PFile f = new PFile("asdf");
    f.writeFile("asdf jklö\n\t   g\n   h");
    Assert.assertArrayEquals(new String[]{"asdf jklö", "\t   g", "   h"}, f.getLines());
    Assert.assertTrue(f.delete());
  }

}
