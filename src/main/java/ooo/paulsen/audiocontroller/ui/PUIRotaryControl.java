package ooo.paulsen.audiocontroller.ui;

import ooo.paulsen.audiocontroller.ui.core.PUIAction;
import ooo.paulsen.audiocontroller.ui.core.PUIFrame;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class PUIRotaryControl extends PUIElement {

    protected float valueLength = 0.5f;
    protected float rotationArea = 270; // in degrees
    protected float valueThickness = 20; // in degrees
    protected float mouseMultiplicator = 0.005f;
    private final CopyOnWriteArrayList<PUIAction> valueUpdateAction = new CopyOnWriteArrayList<>();
    private float value = .5f;
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
        paint = (g, x, y, w, h) -> {
            if (w < 0)
                w = -w;
            if (h < 0)
                h = -h;

            // BG
            g.setColor(getBackgroundColor());
            g.fillOval(x, y, w, h);

            // value-visual
            g.setColor(getTextColor());

            // Value-Visual
            g.fillArc(x, y, w, h, (int) (360 - ((rotationArea * value + (360 - rotationArea) / 2 + 90) + valueThickness / 2)), (int) valueThickness);

            // Overpaint part of ^ , to visualize valueLength
            g.setColor(getBackgroundColor());

            g.fillOval((int) (x + (1.0f - valueLength) * (w / 2)), (int) (y + (1.0f - valueLength) * (h / 2)), (int) (w * (valueLength)), (int) (h * (valueLength)));

            // Outline
            g.setColor(color(2));
            g.drawOval(x, y, w, h);

        };
        hoverOverlay = (g, x, y, w, h) -> {
            g.setColor(new Color(100, 100, 100, 100));
            g.fillOval(x, y, w, h);
        };
        pressOverlay = (g, x, y, w, h) -> {
            g.setColor(new Color(100, 100, 100, 200));
            g.fillOval(x, y, w, h);
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
        mouseWheelListeners.add(e -> {
            if (useMouseWheel && isHovered()) {
                setValue((float) (e.getWheelRotation() * 0.1 + getValue()));
            }
        });
    }

    /**
     * only allows events in a oval area.
     */
    @Override
    public boolean contains(Point p) {
        return new Point(x + w / 2, y + h / 2).distanceSq(p.x, p.y) <= (w / 2) * (w / 2);
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
        this.value = (value > 1 ? 1 : (value < 0 ? 0 : value));
        runAllValueUpdateActions();
    }

    public void setValue_NoUpdate(float value) {
        this.value = (value > 1 ? 1 : (value < 0 ? 0 : value));
    }

    public float getValueLength() {
        return valueLength;
    }

    public void setValueLength(float valueLength) {
        this.valueLength = valueLength;
    }

    public float getRotationArea() {
        return rotationArea;
    }

    public void setRotationArea(float rotationArea) {
        this.rotationArea = rotationArea;
    }

    public float getValueThickness() {
        return valueThickness;
    }

    public void setValueThickness(float valueThickness) {
        this.valueThickness = valueThickness;
    }

    public boolean isMouseWheelUsed() {
        return useMouseWheel;
    }

    public void setUseMouseWheel(boolean useMouseWheel) {
        this.useMouseWheel = useMouseWheel;
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
        if (repaintFrameOnEvent && frame != null) {
            frame.repaint();
        }

        for (PUIAction r : valueUpdateAction)
            if (r != null)
                r.run(this);
    }

    public void addValueUpdateAction(PUIAction r) {
        valueUpdateAction.add(r);
    }

    public void removeValueUpdateAction(PUIAction r) {
        valueUpdateAction.remove(r);
    }

    public ArrayList<PUIAction> getValueUpdateActions() {
        return new ArrayList<>(valueUpdateAction);
    }

}
