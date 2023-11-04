package de.paulsenik.jpl.io;

import java.io.File;
import org.junit.Assert;
import org.junit.Test;

public class PFileTest {

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


}
