package com.paulsen.ui;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;

public class PUIRotaryControl extends PUIElement {

    private ArrayList<Runnable> valueUpdateAction = new ArrayList<Runnable>();

    protected float valueLength = 0.5f;
    protected int rotationArea = 270;
    protected int valueThickness = 2;
    protected float mouseMultiplicator = 0.005f;

    private float value = .5f;
    private Color valueColor = Color.GRAY;
    private Color backgroundColor = Color.LIGHT_GRAY;
    private ElementAlignment alignment = ElementAlignment.VERTICAL;
    private boolean useMouseWheel = true;

    // tempVars
    private int mousePosX, mousePosY;
    private boolean isDragged = false;

    public PUIRotaryControl(PUIFrame f) {
        super(f);
        init();
    }

    public PUIRotaryControl(PUIFrame f, int layer) {
        super(f);
        init();
        setLayer(layer);
    }

    private void init() {
        paintInvoke = new PUIPaintable() {
            @Override
            public void paint(Graphics g, int x, int y, int w, int h) {
                if (w < 0)
                    w = -w;
                if (h < 0)
                    h = -h;

                // BG
                if (PUIElement.darkUIMode && backgroundColor == Color.LIGHT_GRAY)
                    g.setColor(PUIElement.darkBG_1);
                else
                    g.setColor(backgroundColor);
                g.fillOval(x, y, w, h);

                // Outline
                if (PUIElement.darkUIMode)
                    g.setColor(PUIElement.darkOutline);
                else
                    g.setColor(Color.black);
                g.drawOval(x, y, w, h);

                // value-visual
                if (PUIElement.darkUIMode && valueColor == Color.GRAY)
                    g.setColor(PUIElement.darkSelected);
                else
                    g.setColor(valueColor);

                // TODO rewrite
                for (float i = (float) (value - valueThickness * 0.01); i < (float) (value
                        + valueThickness * 0.01); i += (.1f / w)) {
                    Point[] p = getRotPoints(i);

                    g.drawLine(p[0].x, p[0].y, p[1].x, p[1].y);

                }

            }
        };
        hoverOverlay = new PUIPaintable() {
            @Override
            public void paint(Graphics g, int x, int y, int w, int h) {
                g.setColor(new Color(100, 100, 100, 100));
                g.fillOval(x, y, w, h);
            }
        };
        pressOverlay = new PUIPaintable() {
            @Override
            public void paint(Graphics g, int x, int y, int w, int h) {
                g.setColor(new Color(100, 100, 100, 200));
                g.fillOval(x, y, w, h);
            }
        };
        mouseMotionListeners.add(new MouseMotionListener() {
            @Override
            public void mouseMoved(MouseEvent e) {
                isDragged = false;
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (isHovered()) {
                    if (!isDragged) {
                        isDragged = true;
                        mousePosX = e.getX();
                        mousePosY = e.getY();
                    }
                }
                if (isDragged && isCurrentlyPressing()) {
                    if (alignment == ElementAlignment.VERTICAL) {
                        setValue(getValue() - (e.getY() - mousePosY) * mouseMultiplicator);
                    } else if (alignment == ElementAlignment.HORIZONTAL) {
                        setValue(getValue() - (e.getX() - mousePosX) * mouseMultiplicator);
                    }
                    mousePosX = e.getX();
                    mousePosY = e.getY();
                }
            }
        });
        mouseWheelListeners.add(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (useMouseWheel && isHovered()) {
                    setValue((float) (e.getWheelRotation() * 0.1 + getValue()));
                }
            }
        });
    }

    private Point[] getRotPoints(float value) { // 2Points => outer/inner-point
        float rotValue = rotationArea * value + (360 - rotationArea) / 2 + 90;
        float rotH = (float) (Math.sin(Math.toRadians(rotValue)) * w / 2);
        float rotW = (float) (Math.cos(Math.toRadians(rotValue)) * w / 2);
        float nX = x + w / 2 + rotW;
        float nY = y + h / 2 + rotH;
        float innerX = (x + w / 2) - ((x + w / 2) - nX) * (1f - valueLength);
        float innerY = (y + h / 2) - ((y + h / 2) - nY) * (1f - valueLength);

        Point[] p = {new Point((int) nX, (int) nY), new Point((int) innerX, (int) innerY)};
        return p;
    }

    @Override
    public synchronized void setBounds(int x, int y, int w, int h) {
        super.setBounds(x, y, w, w);
    }

    public void setBounds(int x, int y, int s) {
        if (s > 0)
            setBounds(x, y, s, s);
    }

    @Override
    public void setBounds(int w, int h) {
        setBounds(x, y, w, w);
    }

    public void setSize(int s) {
        setBounds(s, s);
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        runAllValueUpdateActions();
        this.value = (value > 1 ? 1 : (value < 0 ? 0 : value));
    }

    public float getValueLength() {
        return valueLength;
    }

    public void setValueLength(float valueLength) {
        this.valueLength = valueLength;
    }

    public int getRotationArea() {
        return rotationArea;
    }

    public void setRotationArea(int rotationArea) {
        this.rotationArea = rotationArea;
    }

    public int getValueThickness() {
        return valueThickness;
    }

    public void setValueThickness(int valueThickness) {
        this.valueThickness = valueThickness;
    }

    public Color getValueColor() {
        return valueColor;
    }

    public void setValueColor(Color valueColor) {
        this.valueColor = valueColor;
    }

    public boolean isMouseWheelUsed() {
        return useMouseWheel;
    }

    public void setUseMouseWheel(boolean useMouseWheel) {
        this.useMouseWheel = useMouseWheel;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public float getMouseMultiplicator() {
        return mouseMultiplicator;
    }

    public void setMouseMultiplicator(float mouseMultiplicator) {
        this.mouseMultiplicator = mouseMultiplicator;
    }

    public ElementAlignment getAlignment() {
        return alignment;
    }

    public void setAlignment(ElementAlignment alignment) {
        this.alignment = alignment;
    }

    public void runAllValueUpdateActions() {
        if (updateFrameOnEvent && frame != null) {
            frame.updateElements();
        }

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

}
