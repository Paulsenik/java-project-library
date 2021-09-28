package com.paulsen.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;

public class PUIElement { // PaulsenUserInterfaceIntegratedElement

	public static volatile ArrayList<PUIElement> registeredElements = new ArrayList<PUIElement>();
	public static boolean darkUIMode = false;
	public static Color darkBG_1 = new Color(47, 47, 47), darkBG_2 = new Color(57, 57, 57),
			darkOutline = new Color(81, 81, 81), darkText = new Color(235, 235, 235),
			darkSelected = new Color(196, 196, 196);

	public enum ElementAlignment {
		HORIZONTAL, VERTICAL
	}

	protected int x = 0, y = 0, w = 0, h = 0;
	protected Color backgroundColor = Color.LIGHT_GRAY;

	protected PUIPaintable paintInvoke, hoverOverlay, pressOverlay;
	protected Component c;

	protected ArrayList<PUIAction> actions = new ArrayList<>();
	protected Object metaData;

	private boolean hovered = false, pressed = false, paintOverOnHover = true, paintOverOnPress = true, enabled = true;

	public PUIElement(Component l) {
		this.c = l;
		registeredElements.add(this);

		l.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseMoved(MouseEvent e) {
				if (!enabled)
					return;
				Point p = l.getMousePosition();
				hovered = (p != null && getBounds().contains(p));
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				if (!enabled)
					return;
				Point p = l.getMousePosition();
				hovered = (p != null && getBounds().contains(p));

			}
		});
		l.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
				pressed = false;
			}

			@Override
			public void mousePressed(MouseEvent e) {
				if (!enabled)
					return;
				if (!pressed && hovered) {
					Point p = l.getMousePosition();
					if (p != null && getBounds().contains(p)) {
						runAllActions();
					}
					pressed = true;
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

		paintInvoke = new PUIPaintable() {
			@Override
			public void paint(Graphics g, int x, int y, int w, int h) {
				if (darkUIMode && backgroundColor == Color.LIGHT_GRAY) {
					g.setColor(darkBG_1);
					g.fillRect(x, y, w, h);
					g.setColor(darkOutline);
					g.drawRect(x, y, w, h);
				} else {
					g.setColor(backgroundColor);
					g.fillRect(x, y, w, h);
					g.setColor(new Color(50, 50, 50));
					g.drawRect(x, y, w, h);
				}
			}
		};
		hoverOverlay = new PUIPaintable() {
			@Override
			public void paint(Graphics g, int x, int y, int w, int h) {
				g.setColor(new Color(100, 100, 100, 100));
				g.fillRect(x, y, w, h);
			}
		};
		pressOverlay = new PUIPaintable() {
			@Override
			public void paint(Graphics g, int x, int y, int w, int h) {
				g.setColor(new Color(100, 100, 100, 200));
				g.fillRect(x, y, w, h);
			}
		};
	}

	public synchronized void draw(Graphics g) {
		if (g == null)
			return;

		Point p = c.getMousePosition();
		if (p != null && getBounds().contains(p)) {
			hovered = true;
		} else
			hovered = false;

		if (paintInvoke != null) {
			paintInvoke.paint(g, x, y, w, h);
		}
		if (hovered) {
			if (hovered && !pressed && paintOverOnHover) {
				if (hoverOverlay != null)
					hoverOverlay.paint(g, x, y, w, h);
			} else if (pressed && paintOverOnPress) {
				if (pressOverlay != null)
					pressOverlay.paint(g, x, y, w, h);
			}
		}
	}

	public ArrayList<PUIAction> getActionListeners() {
		return actions;
	}

	public void runAllActions() {
		if (actions != null)
			for (PUIAction r : actions)
				if (r != null)
					r.run(this);
	}

	public void removeActionListener(PUIAction action) {
		actions.remove(action);
	}

	public void addActionListener(PUIAction action) {
		actions.add(action);
	}

	public synchronized void setBounds(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}

	public void setPosition(int x, int y) {
		setBounds(x, y, w, h);
	}

	public void setBounds(int w, int h) {
		setBounds(x, y, w, h);
	}

	public Rectangle getBounds() {
		return new Rectangle(x, y, w, h);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getW() {
		return w;
	}

	public int getH() {
		return h;
	}

	public void setDraw(PUIPaintable paintInvoke) {
		this.paintInvoke = paintInvoke;
	}

	public void setHoverOverlay(PUIPaintable paintInvoke) {
		hoverOverlay = paintInvoke;
	}

	public void setPressedOverlay(PUIPaintable paintInvoke) {
		pressOverlay = paintInvoke;
	}

	public boolean isPaintOverOnHover() {
		return paintOverOnHover;
	}

	public void doPaintOverOnHover(boolean paintOverOnHover) {
		this.paintOverOnHover = paintOverOnHover;
	}

	public boolean isPaintOverOnPress() {
		return paintOverOnPress;
	}

	public void doPaintOverOnPress(boolean paintOverOnPress) {
		this.paintOverOnPress = paintOverOnPress;
	}

	public boolean isHovered() {
		return hovered;
	}

	public boolean isPressed() {
		return pressed;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public String getUserInput(String message, String initialValue) {
		return JOptionPane.showInputDialog(c, message, initialValue);
	}

	public void sendUserError(String message) {
		JOptionPane.showMessageDialog(c, message, "ERROR", JOptionPane.ERROR_MESSAGE);
	}

	public void sendUserWarning(String message) {
		JOptionPane.showMessageDialog(c, message, "WARNING", JOptionPane.WARNING_MESSAGE);
	}

	public void sendUserInfo(String message) {
		JOptionPane.showMessageDialog(c, message, "INFO", JOptionPane.INFORMATION_MESSAGE);
	}

	public boolean getUserConfirm(String message, String title) {
		return JOptionPane.showConfirmDialog(c, message, title, JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION;
	}

	/**
	 * Creates a popup-window which lets u choose one of the Options
	 * 
	 * @param parent
	 * @param title
	 * @param comboBoxInput String-Array of Options
	 * @return index from 0 to comboBoxInput.length
	 */
	public int getUserSelection(String title, String comboBoxInput[]) {
		JComboBox<String> box = new JComboBox<>(comboBoxInput);
		JOptionPane.showMessageDialog(c, box, title, JOptionPane.QUESTION_MESSAGE);
		return box.getSelectedIndex();
	}

	public int getUserSelection(String title, ArrayList<String> comboBoxInput) {
		String s[] = new String[comboBoxInput.size()];
		for (int i = 0; i < s.length; i++)
			s[i] = comboBoxInput.get(i);
		return getUserSelection(title, s);
	}

	public void setMetadata(Object o) {
		metaData = o;
	}

	public Object getMetadata() {
		return metaData;
	}

}
