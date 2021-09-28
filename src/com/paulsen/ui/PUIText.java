package com.paulsen.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

public class PUIText extends PUIElement {

	/**
	 * Factor, how much space is left between the characters according to the size
	 */
	public static final float characterSpacingFactor = 0.8f;
	public String normalFont = "Arial";
	public String selectFont = "Consolas";

	// Higly suggested Monospaced Fonts => fonts that have the same width
	public static final String[] selectableFonts = { "Consolas", "Courier New", "Lucida Console", "MingLiU-ExtB",
			"MingLiU_HKSCS-ExtB", "MS Gothic", "NSimSun", "SimSun", "SimSun-ExtB" };

	private ArrayList<Runnable> markerUpdateActions = new ArrayList<Runnable>();
	private Color textColor = Color.black, selectedColor = Color.orange;
	private String text;
	private boolean isTextCropped = false;
	private int markerA = 0, markerB = 0;
	private boolean selectable = false, centered = false;

	private boolean hasPressedFirst = false;

	public PUIText(Component l, String text) {
		super(l);
		constructor(l);
		setText(text);
	}

	public PUIText(Component l) {
		super(l);
		constructor(l);
	}

	private void constructor(Component l) {
		l.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (!selectable || !isEnabled())
					return;
				hasPressedFirst = false;

				if (!isHovered())
					return;

				int mx = (int) e.getPoint().getX();
				float nEnd = (mx - x) / (characterSpacingFactor * h);
				setMarkerB((int) (nEnd + 0.5));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				if (!selectable || !isEnabled() || !isHovered())
					return;

				if (isPressed()) {
					int mx = (int) e.getPoint().getX();
					if (!hasPressedFirst) {
						hasPressedFirst = true;
						float nStart = (mx - x) / (characterSpacingFactor * h);
						setMarkerA((int) (nStart + 0.5));
						float nEnd = (mx - x) / (characterSpacingFactor * h);
						setMarkerB((int) (nEnd + 0.5));
					}
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});
		l.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseMoved(MouseEvent e) {
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				if (!selectable || !isEnabled() || !isPressed())
					return;

				int mx = (int) e.getPoint().getX();
				float nEnd = (mx - x) / (characterSpacingFactor * h);
				setMarkerB((int) (nEnd + 0.5));
			}
		});
	}

	private void drawText(Graphics g) {
		if (text == null || text.isEmpty())
			return;

		if (selectable) {
			g.setColor(selectedColor);
			int tempX = 0, tempW = 0;
			if (markerA > markerB) {
				tempX = (int) (x + markerB * h * characterSpacingFactor - h / 10);
				tempW = (int) ((markerA - markerB) * h * characterSpacingFactor + (tempX < x ? 0 : h / 10));
			} else {
				tempX = (int) (x + markerA * h * characterSpacingFactor - h / 10);
				tempW = (int) ((markerB - markerA) * h * characterSpacingFactor + (tempX < x ? 0 : h / 10));
			}
			tempW = (tempX + tempW > x + w ? (x + w - tempX) : tempW);
			g.fillRect((tempX < x ? x : tempX) + 1, y + 1, tempW - 1, h - 1);

			isTextCropped = false;
			if (PUIElement.darkUIMode && textColor == Color.black)
				g.setColor(PUIElement.darkText);// old:new Color(160, 160, 160)
			else
				g.setColor(textColor);
			g.setFont(new Font(selectFont, Font.PLAIN, h));
			for (int i = 0; i < text.length(); i++) {
				// prevent overflow
				if (h * (i + 1) * characterSpacingFactor > w) {
					isTextCropped = true;
					break;
				}

				g.drawString(String.valueOf(text.charAt(i)), (int) (x + h * i * characterSpacingFactor),
						(int) (y + h * 0.85));
			}
		} else { // Normal Text Display with Clipping Area
			if (PUIElement.darkUIMode && textColor == Color.black)
				g.setColor(PUIElement.darkText);
			else
				g.setColor(textColor);
			g.setFont(new Font(normalFont, Font.PLAIN, h));
			g.drawString(text, x, (int) (y + h * 0.85));
		}
	}

	/**
	 * @param height of text
	 * @return calculated minimumWidth for the text to be displayed completely
	 */
	public static int getTextWidth(int fontHeight, int stringLength) {
		return (int) (fontHeight * stringLength * characterSpacingFactor);
	}

	public static int getTextHeight(int width, int stringLength) {
		return (int) (width / stringLength / characterSpacingFactor);
	}

	@Override
	public void draw(Graphics g) {
		if (g == null)
			return;
		super.draw(g);
		g.setClip(x, y, w, h);
		drawText(g);
		g.setClip(0, 0, 100000, 100000);
	}

	public String getFont() {
		return normalFont;
	}

	public void setFont(String normalFont) {
		this.normalFont = normalFont;
	}

	public String getSelectFont() {
		return selectFont;
	}

	public void setSelectFont(String selectFont) {
		this.selectFont = selectFont;
	}

	public String getSelectedText() {
		String s = "";
		for (int i = (markerA < markerB ? markerA : markerB); i < (markerA < markerB ? markerB : markerA); i++)
			s += text.charAt(i);
		return s;
	}

	/**
	 * 
	 * @return Text without the selected part
	 */
	public String getTextWithoutSelected() {
		String s = "";
		for (int i = 0; i < text.length(); i++)
			if (!(i >= (markerA < markerB ? markerA : markerB) && i < (markerA < markerB ? markerB : markerA)))
				s += text.charAt(i);
		return s;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
		setMarkerA(markerA);
		setMarkerB(markerB);
	}

	public int getMarkerA() {
		return markerA;
	}

	public void setMarkerA(int selectStart) {
		int prev = markerA;
		this.markerA = selectStart > text.length() ? text.length() : (selectStart < 0 ? 0 : selectStart);
		if (prev != markerA)
			runAllMarkerUpdateActions();
	}

	public int getMarkerB() {
		return markerB;
	}

	public void setMarkerB(int selectEnd) {
		int prev = markerB;
		this.markerB = selectEnd > text.length() ? text.length() : (selectEnd < 0 ? 0 : selectEnd);
		if (prev != markerB)
			runAllMarkerUpdateActions();
	}

	public Color getTextColor() {
		return textColor;
	}

	public void setTextColor(Color textColor) {
		this.textColor = textColor;
	}

	public boolean isSelectable() {
		return selectable;
	}

	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
	}

	public boolean isCentered() {
		return centered;
	}

	public boolean isTextCropped() {
		return isTextCropped;
	}

	public void setCentered(boolean centered) {
		this.centered = centered;
	}

	public void runAllMarkerUpdateActions() {
		if (markerUpdateActions != null)
			for (Runnable r : markerUpdateActions)
				if (r != null)
					r.run();
	}

	public ArrayList<Runnable> getMarkerUpdateActions() {
		return markerUpdateActions;
	}

	public void addMarkerUpdateAction(Runnable markerUpdateAction) {
		markerUpdateActions.add(markerUpdateAction);
	}

	public void removeMarkerUpdateAction(Runnable markerUpdateAction) {
		markerUpdateActions.remove(markerUpdateAction);
	}
}