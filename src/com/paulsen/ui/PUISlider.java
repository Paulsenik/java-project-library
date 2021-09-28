package com.paulsen.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;

public class PUISlider extends PUIElement {

	private ArrayList<Runnable> valueUpdateAction = new ArrayList<Runnable>();

	private PUIElement sliderB;
	private ElementAlignment alignment = ElementAlignment.VERTICAL;
	private float sliderValue = 0.0f; // 0.0f <= value <= 1.0f
	private int sliderSize = 20;
	private boolean useMouseWheel = true, useFixPoints = false;

	private int fixPoints = 5;

	public PUISlider(Component l) {
		super(l);

		sliderB = new PUIElement(l);
		sliderB.setDraw(new PUIPaintable() {
			@Override
			public void paint(Graphics g, int x, int y, int w, int h) {
				if (PUIElement.darkUIMode)
					g.setColor(PUIElement.darkBG_2);
				else
					g.setColor(Color.GRAY);
				g.fillRect(x, y, w, h);

				if (PUIElement.darkUIMode)
					g.setColor(darkOutline);
				else
					g.setColor(Color.LIGHT_GRAY);
				g.drawRect(x, y, w, h);
			}
		});
		PUIElement.registeredElements.remove(sliderB);

		l.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseMoved(MouseEvent e) {
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				if (isPressed()) {
					setSliderValue(e.getPoint());
				}
			}
		});
		l.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (useMouseWheel && isHovered()) {
					setValue((float) (e.getWheelRotation() * 0.1 + getValue()));
				}
			}
		});
	}

	private void setSliderValue(Point mousePos) {
		if (alignment == ElementAlignment.VERTICAL) {
			float nValue = ((float) mousePos.y - (y + sliderSize / 2)) / (h - sliderSize);
			setValue(nValue);
		} else if (alignment == ElementAlignment.HORIZONTAL) {
			float nValue = ((float) mousePos.x - (x + sliderSize / 2)) / (w - sliderSize);
			setValue(nValue);
		}
	}

	@Override
	public void setBounds(int x, int y, int w, int h) {
		super.setBounds(x, y, w, h);
		if (alignment == ElementAlignment.VERTICAL) {
			sliderB.setBounds(x, (int) (y + (h - sliderSize) * sliderValue), w, sliderSize);
		} else if (alignment == ElementAlignment.HORIZONTAL) {
			sliderB.setBounds((int) (x + (w - sliderSize) * sliderValue), y, sliderSize, h);
		}
	}

	@Override
	public void draw(Graphics g) {
		super.draw(g);
		sliderB.draw(g);
	}

	public boolean doesUseFixPoints() {
		return useFixPoints;
	}

	public void useFixPoints(boolean useFixPoints) {
		this.useFixPoints = useFixPoints;
	}

	public int getFixPoints() {
		return fixPoints;
	}

	/**
	 * Not USEABLE
	 * 
	 * @param fixPoints
	 */
	public void setFixPoints(int fixPoints) {
		// TODO
		this.fixPoints = fixPoints;
	}

	@Override
	public void setPosition(int x, int y) {
		setBounds(x, y, w, h);
	}

	@Override
	public void setBounds(int w, int h) {
		setBounds(x, y, w, h);
	}

	@Override
	public void doPaintOverOnPress(boolean paintOverOnPress) {
		super.doPaintOverOnPress(paintOverOnPress);
		sliderB.doPaintOverOnPress(paintOverOnPress);
	}

	@Override
	public void doPaintOverOnHover(boolean paintOverOnHover) {
		super.doPaintOverOnHover(paintOverOnHover);
		sliderB.doPaintOverOnHover(paintOverOnHover);
	}

	public float getValue() {
		return sliderValue;
	}

	public void setValue(float sliderValue) {
		float nSliderValue = sliderValue > 1.0f ? 1.0f : (sliderValue < 0 ? 0 : sliderValue);
		if (useFixPoints) {
			// TODO
		}
		if (nSliderValue != this.sliderValue) {
			this.sliderValue = nSliderValue;
			runAllValueUpdateActions();
			setBounds(x, y, w, h);
		}
	}

	public void runAllValueUpdateActions() {
		if (valueUpdateAction != null)
			for (Runnable r : valueUpdateAction)
				if (r != null)
					r.run();
	}

	public void addValueUpdateAction(Runnable r) {
		valueUpdateAction.add(r);
	}

	public void removeValueUpdateAction(Runnable r) {
		valueUpdateAction.remove(r);
	}

	public ArrayList<Runnable> getValueUpdateActions() {
		return valueUpdateAction;
	}

	public ElementAlignment getAlignment() {
		return alignment;
	}

	public void setAlignment(ElementAlignment alignment) {
		if (this.alignment != alignment) {
			this.alignment = alignment;
			setBounds(x, y, w, h); // update Slider
		}
	}

	public int getSliderSize() {
		return sliderSize;
	}

	public void setSliderSize(int sliderSize) {
		if (this.sliderSize != sliderSize) {
			this.sliderSize = sliderSize;
			setBounds(x, y, w, h);
		}
	}

	public boolean isUsingMouseWheel() {
		return useMouseWheel;
	}

	public void useMouseWheel(boolean useWithMouseWheel) {
		this.useMouseWheel = useWithMouseWheel;
	}
}