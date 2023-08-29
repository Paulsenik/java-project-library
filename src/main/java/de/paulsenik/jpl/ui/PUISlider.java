package de.paulsenik.jpl.ui;

import de.paulsenik.jpl.ui.core.PUIAction;
import de.paulsenik.jpl.ui.core.PUIFrame;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

public class PUISlider extends PUIElement {

    private final ArrayList<PUIAction> valueUpdateAction = new ArrayList<>();

    private PUIElement sliderB;
    private ElementAlignment alignment = ElementAlignment.VERTICAL;
    private float sliderValue = 0.0f; // 0.0f <= value <= 1.0f
    private int sliderSize = 20;
    private boolean useMouseWheel = true, useFixPoints = false;

    private int fixPoints = 5;

    public PUISlider(PUIFrame l) {
        super(l);

        init();
    }

    public PUISlider(PUIFrame l, int layer) {
        super(l);

        init();
        setLayer(layer);
    }

    private void init() {

        sliderB = new PUIElement(frame, getDrawLayer());
        sliderB.setDraw((g, x, y, w, h) -> {
            g.setColor(color(1));
            g.fillRoundRect(x, y, w, h, arcWidth, arcHeight);

            g.setColor(color(3));
            g.drawRoundRect(x, y, w, h, arcWidth, arcHeight);
        });
        registeredElements.remove(sliderB);

        mouseMotionListeners.add(new MouseMotionListener() {
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
        mouseWheelListeners.add(e -> {
            hovered = contains(e.getPoint());
            if (useMouseWheel && isHovered()) {
                setValue((float) (e.getWheelRotation() * 0.1 + getValue()));
            }
        });
    }

    @Override
    public void setLayer(int layer) {
        super.setLayer(layer);
        sliderB.setLayer(layer);
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
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        sliderB.setEnabled(enabled);
    }

    @Override
    public void setBounds(int x, int y, int w, int h) {
        super.setBounds(x, y, w, h);
        if (alignment == ElementAlignment.VERTICAL) {
            sliderB.setBounds(x + 1, (int) (y + (h - sliderSize) * sliderValue) + 1, w - 2, sliderSize - 2);
        } else if (alignment == ElementAlignment.HORIZONTAL) {
            sliderB.setBounds((int) (x + (w - sliderSize) * sliderValue) + 1, y + 1, sliderSize - 2, h - 2);
        }
    }

    @Override
    public void draw(Graphics2D g) {
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
     */
    public void setFixPoints(int fixPoints) {
        // TODO Feature
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

    @Override
    public void release() {
        super.release();
        frame.remove(sliderB);
    }

    public float getValue() {
        return sliderValue;
    }

    public void setValue(float sliderValue) {
        float nSliderValue = sliderValue > 1.0f ? 1.0f : (sliderValue < 0 ? 0 : sliderValue);
        if (useFixPoints) {
            // TODO Feature
        }
        if (nSliderValue != this.sliderValue) {
            this.sliderValue = nSliderValue;
            runAllValueUpdateActions();
            setBounds(x, y, w, h);
        }
    }

    public void runAllValueUpdateActions() {
        for (PUIAction r : valueUpdateAction)
            if (r != null)
                r.run(this);

        if (repaintFrameOnEvent && frame != null)
            frame.updateElements();
    }

    public void addValueUpdateAction(PUIAction r) {
        valueUpdateAction.add(r);
    }

    public void removeValueUpdateAction(Runnable r) {
        valueUpdateAction.remove(r);
    }

    public ArrayList<PUIAction> getValueUpdateActions() {
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