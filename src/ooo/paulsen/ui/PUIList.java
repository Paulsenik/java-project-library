package ooo.paulsen.ui;

import ooo.paulsen.ui.core.PUIAction;
import ooo.paulsen.ui.core.PUIFrame;

import java.awt.*;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.concurrent.CopyOnWriteArrayList;

public class PUIList extends PUIElement {

    private CopyOnWriteArrayList<PUIAction> valueUpdateAction = new CopyOnWriteArrayList<>();

    private volatile CopyOnWriteArrayList<PUIElement> elements = new CopyOnWriteArrayList<>();
    private boolean fixedElements = true, useMouseWheel = true, showSlider = true;
    private int sliderWidth = 70;
    private int elementSpace_Left = 0, elementSpace_Right = 0, elementSpace_Top = 0, elementSpace_Bottom = 0;

    private ElementAlignment alignment = ElementAlignment.VERTICAL;

    // only used if fixedElements=true
    private int showedElements = 5, showIndex = 0;

    private PUISlider slider;

    public PUIList(PUIFrame f) {
        super(f);

        init();
    }

    public PUIList(PUIFrame f, int layer) {
        super(f);

        init();
        setLayer(layer);
    }

    private void init() {
        slider = new PUISlider(frame);
        registeredElements.remove(slider);
        doPaintOverOnHover(false);
        doPaintOverOnPress(false);
        slider.useMouseWheel(false);
        slider.setAlignment(ElementAlignment.VERTICAL);
        slider.doPaintOverOnHover(false);
        slider.doPaintOverOnPress(false);
        mouseWheelListeners.add(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                try {
                    if (!isEnabled())
                        return;

                    hovered = contains(e.getPoint());
                    if (useMouseWheel && isHovered()) {
                        if (showedElements < elements.size()) {
                            float value = (float) 1 / (elements.size() - showedElements);
                            slider.setValue((float) (e.getWheelRotation() * (value) + slider.getValue()));

                            // Check if elements in list are hovered over
                            for (PUIElement elem : elements)
                                for (MouseMotionListener ml : elem.getMouseMotionListeners())
                                    ml.mouseMoved(e);
                        }
                    }
                } catch (ConcurrentModificationException e2) {
                }
            }
        });
        slider.addValueUpdateAction(new PUIAction() {
            @Override
            public void run(PUIElement that) {
                if (!isEnabled())
                    return;

                updateElements();
                runAllValueUpdateActions();
            }
        });
    }

    @Override
    public void setLayer(int layer) {
        super.setLayer(layer);
        slider.setLayer(layer);
        for (PUIElement e : elements)
            e.setLayer(layer);
    }

    public synchronized void updateElements() {
        try {
            for (PUIElement e : elements) {
                e.setBounds(0, 0, 0, 0);
                e.setEnabled(false);
            }

            int maxShowIndex = elements.size() - showedElements;
            int nShowIndex = (int) (slider.getValue() * maxShowIndex);
            showIndex = (nShowIndex < 0 ? 0 : nShowIndex);

            if (showSlider)
                if (!elements.isEmpty()) {
                    if (alignment == ElementAlignment.VERTICAL) {
                        slider.setSliderSize((int) ((float) showedElements
                                / (elements.size() > showedElements ? elements.size() : showedElements) * h));
                    } else if (alignment == ElementAlignment.HORIZONTAL) {
                        slider.setSliderSize((int) ((float) showedElements
                                / (elements.size() > showedElements ? elements.size() : showedElements) * w));
                    }
                }

            float eHeight = (float) h / showedElements;
            float eWidth = (float) w / showedElements;

            if (fixedElements) { // snap to grid
                for (int i = 0; i < (elements.size() < showedElements ? elements.size() : showedElements)
                        && (elements.size() > i + showIndex); i++) {

                    PUIElement e = elements.get(i + showIndex);
                    if (alignment == ElementAlignment.VERTICAL) {

                        // Vertical
                        e.setBounds(x + elementSpace_Left, (int) (y + eHeight * i) + elementSpace_Top, w - (showSlider ? sliderWidth : 0) - elementSpace_Left - elementSpace_Right, (int) eHeight - elementSpace_Top - elementSpace_Bottom);

                    } else if (alignment == ElementAlignment.HORIZONTAL) {

                        // Horizontal
                        e.setBounds((int) (x + eWidth * i + elementSpace_Left), y + elementSpace_Top, (int) eWidth - elementSpace_Left - elementSpace_Right, h - (showSlider ? sliderWidth : 0) - elementSpace_Top - elementSpace_Bottom);

                    }
                    if (isEnabled())
                        elements.get(i + showIndex).setEnabled(true);
                }
            } else { // TODO Feature

            }
        } catch (IndexOutOfBoundsException | ConcurrentModificationException e) {
            System.err.println("try new upd");
//			updateElements();
            e.printStackTrace();
        }
    }

    /**
     * Finds the given Element
     *
     * @param element
     * @return values from 0.0 to 1.0 depending o the location of the searched Element
     */
    public float find(PUIElement element) {
        if (element != null)
            for (int i = 0; i < elements.size(); i++)
                if (elements.get(i) == element)
                    return Math.min((Math.max(i - showedElements / 2, 0f)) / (elements.size() - showedElements), 1f);

        return -1; // PUIElement could not be found
    }

    /**
     * Automatically centers Element in the UI
     *
     * @param elementIndex to center
     */
    public void center(int elementIndex) {
        center(elements.get(elementIndex));
    }

    /**
     * Automatically centers Element in the UI
     *
     * @param element to center
     */
    public void center(PUIElement element) {
        if (element != null)
            setSliderValue(find(element));
    }

    public void setElementSpacing(int left, int right, int top, int bottom) {
        elementSpace_Left = Math.max(left, 0);
        elementSpace_Right = Math.max(right, 0);
        elementSpace_Top = Math.max(top, 0);
        elementSpace_Bottom = Math.max(bottom, 0);
        updateElements();
    }

    public boolean isFixedElements() {
        return fixedElements;
    }

    // TODO Feature
//	public void setFixedElements(boolean fixedElements) {
//		this.fixedElements = fixedElements;
//	}

    public void showSlider(boolean value) {
        if (showSlider != value) {
            showSlider = value;
            slider.setEnabled(value);
            updateElements();
        }
    }

    public boolean doesShowSlider() {
        return showSlider;
    }

    public boolean isUseMouseWheel() {
        return useMouseWheel;
    }

    public void setUseMouseWheel(boolean useMouseWheel) {
        this.useMouseWheel = useMouseWheel;
    }

    public void addAllElements(ArrayList<PUIElement> elements) {
        for (PUIElement e : elements)
            addElement(e);
    }

    public void addElement(PUIElement element) {
        elements.add(element);
        element.doPaintOverOnHover(false);
        element.doPaintOverOnPress(false);
        element.setLayer(getInteractionLayer());
        registeredElements.remove(element);
        updateElements();
    }

    public void removeElement(PUIElement element) {
        elements.remove(element);
        updateElements();
    }

    public void swapElements(int e1, int e2) {
        if (e1 >= elements.size() || e1 < 0)
            return;
        if (e2 >= elements.size() || e2 < 0)
            return;

        if (elements.get(e1) != null && elements.get(e2) != null) {
            Collections.swap(elements, e1, e2);
            updateElements();
        }
    }

    /**
     * removes all Elements from Core, Frame and clears its own List
     */
    public void clearElements() {
        for (PUIElement e : elements) {
            e.release();
            frame.remove(e);
        }
        elements.clear();
        updateElements();
    }

    public ArrayList<PUIElement> getElements() {
        return new ArrayList<>(elements);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        slider.setEnabled(enabled);
        for (PUIElement e : elements)
            e.setEnabled(enabled);
    }

    @Override
    public void draw(Graphics2D g) {
        if (!isEnabled())
            return;

        try {
            super.draw(g);
            if (slider != null)
                slider.draw(g);

            if (elements != null)
                for (PUIElement e : elements)
                    if (e != null && e.isEnabled())
                        e.draw(g);
        } catch (ConcurrentModificationException e) {
            draw(g);
        }
    }

    public void setSliderSize(int size) {
        slider.setSliderSize(size);
        setBounds(x, y, w, h);
    }

    @Override
    public void setBounds(int x, int y, int w, int h) {
        super.setBounds(x, y, w, h);

        if (alignment == ElementAlignment.VERTICAL) {
            slider.setBounds(x + w - sliderWidth, y, sliderWidth, h);
        } else if (alignment == ElementAlignment.HORIZONTAL) {
            slider.setBounds(x, y + h - sliderWidth, w, sliderWidth);
        }
        updateElements();
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
    public void release() {
        super.release();
        clearElements();
        frame.remove(slider);
    }

    public void runAllValueUpdateActions() {
        if (valueUpdateAction != null)
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

    public int getSliderWidth() {
        return sliderWidth;
    }

    public void setSliderWidth(int sliderWidth) {
        this.sliderWidth = sliderWidth;
        updateElements();
    }

    public float getSliderValue() {
        return slider.getValue();
    }

    public void setSliderValue(float value) {
        slider.setValue(value);
    }

    public int getShowedElements() {
        return showedElements;
    }

    public void setShowedElements(int showedElements) {
        if (showedElements < 1)
            return;
        this.showedElements = showedElements;
        updateElements();
    }

    public ElementAlignment getAlignment() {
        return alignment;
    }

    public void setAlignment(ElementAlignment alignment) {
        this.alignment = alignment;
        if (alignment == ElementAlignment.HORIZONTAL)
            slider.setAlignment(ElementAlignment.HORIZONTAL);
        if (alignment == ElementAlignment.VERTICAL)
            slider.setAlignment(ElementAlignment.VERTICAL);
        updateElements();
    }

}
