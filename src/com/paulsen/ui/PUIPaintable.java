package com.paulsen.ui;

import java.awt.*;

public interface PUIPaintable {
	/**
	 * 
	 * @param g grahpics to draw on
	 * @param x-coordinate
	 * @param y-coordinate
	 * @param w width
	 * @param h height
	 */
	public void paint(Graphics2D g, int x, int y, int w, int h);
}
