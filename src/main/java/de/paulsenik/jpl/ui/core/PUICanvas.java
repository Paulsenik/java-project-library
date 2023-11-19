package de.paulsenik.jpl.ui.core;

import java.awt.Graphics2D;

public class PUICanvas {

  private final long creationID = System.currentTimeMillis(); // creation time

  protected final PUIFrame frame;
  protected PUIPaintable paint;

  protected boolean visible = true/*, blockRaycast = false*/;

  // other
  protected int drawLayer = 0;

  public PUICanvas(PUIFrame f, PUIPaintable paint) {
    frame = f;
    this.paint = paint;
    init();
  }

  public PUICanvas(PUIFrame f, PUIPaintable paint, int layer) {
    frame = f;
    this.paint = paint;
    drawLayer = layer;
    init();
  }

  private void init() {
    if (frame != null) {
      frame.add(this);
    }
  }

  public synchronized void draw(Graphics2D g) {
    if (g != null && frame != null && isVisible()) {
      paint.paint(g, 0, 0, frame.w(), frame.h());
    }
  }

  public PUIFrame getFrame() {
    return frame;
  }

  public PUIPaintable getDraw() {
    return paint;
  }

  public void setDraw(PUIPaintable paintable) {
    this.paint = paintable;
  }

  public boolean isVisible() {
    return visible;
  }

  public void setVisible(boolean visible) {
    this.visible = visible;
  }

  public int getDrawLayer() {
    return drawLayer;
  }

  public void setDrawLayer(int drawLayer) {
    this.drawLayer = drawLayer;
  }

  public long getCreationID() {
    return creationID;
  }

}
