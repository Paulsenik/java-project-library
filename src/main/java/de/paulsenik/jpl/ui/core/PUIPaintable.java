package de.paulsenik.jpl.ui.core;

import java.awt.Graphics2D;

public interface PUIPaintable {

  /**
   * @param g            grahpics to draw on
   * @param x-coordinate
   * @param y-coordinate
   * @param w            width
   * @param h            height
   */
  void paint(Graphics2D g, int x, int y, int w, int h);
}
