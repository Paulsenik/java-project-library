package com.paulsen.ui;

import java.awt.Graphics;

public interface PUIPaintable {
	/**
	 * 
	 * @param grahpics to draw on
	 * @param x-coordinate
	 * @param y-coordinate
	 * @param width
	 * @param height
	 */
	public void paint(Graphics g, int x, int y, int w, int h);
}
