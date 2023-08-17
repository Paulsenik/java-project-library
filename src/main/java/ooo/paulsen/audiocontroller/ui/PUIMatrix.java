package ooo.paulsen.audiocontroller.ui;

import ooo.paulsen.audiocontroller.ui.core.PUIFrame;

import java.awt.*;

/**
 * Automatically scales and positions its Elements
 * Can have an 2DArray of Elements that are then transformed into a matrix-pattern
 */
public class PUIMatrix extends PUIElement {


    protected final int columns;
    protected final int rows;
    protected volatile PUIElement[][] elements;
    private int elementSpace_Left = 0, elementSpace_Right = 0, elementSpace_Top = 0, elementSpace_Bottom = 0;

    public PUIMatrix(PUIFrame f, int columns, int rows) {
        super(f);
        this.columns = columns;
        this.rows = rows;
        elements = new PUIElement[columns][rows];
        doBlockRaycast(false);
    }

    public PUIMatrix(PUIFrame f, int columns, int rows, int layer) {
        super(f, layer);
        this.columns = columns;
        this.rows = rows;
        elements = new PUIElement[columns][rows];
        doBlockRaycast(false);
    }

    @Override
    public synchronized void setBounds(int x, int y, int w, int h) {
        super.setBounds(x, y, w, h);
        updateElements();
    }

    @Override
    public synchronized void draw(Graphics2D g) {
//        super.draw(g);
        if (elements != null)
            for (PUIElement[] element : elements)
                for (int j = 0; j < element.length; j++)
                    if (element[j] != null)
                        element[j].draw(g);
    }

    private void updateElements() {

        float eW = w / columns;
        float eH = h / rows;

        synchronized (elements) {
            for (int i = 0; i < elements.length; i++)
                for (int j = 0; j < elements[i].length; j++)
                    if (elements[i][j] != null)
                        elements[i][j].setBounds((int) (eW * i + x) + elementSpace_Left, (int) (eH * j + y) + elementSpace_Top, (int) (eW - elementSpace_Left - elementSpace_Right), (int) (eH - elementSpace_Top - elementSpace_Bottom));
            frame.repaint();
        }
    }

    /**
     * removes all PUIElements from the frame and clears the Element-Matrix
     */
    public synchronized void reset() {
        synchronized (elements) {
            for (PUIElement[] E : elements)
                for (PUIElement e : E)
                    frame.remove(e);

            elements = new PUIElement[columns][rows];
            frame.repaint();
        }
    }

    public synchronized boolean setElement(PUIElement e, int column, int row) {
        if (columns > column && column >= 0)
            if (rows > row && row >= 0) {
                synchronized (elements) {
                    elements[column][row] = e;
                    updateElements();
                    return true;
                }
            }
        return false;
    }

    public PUIElement get(int column, int row) {
        if (columns > column && column >= 0)
            if (rows > row && row >= 0)
                return elements[column][row];
        return null;
    }

    public void setElementSpacing(int left, int right, int top, int bottom) {
        elementSpace_Left = Math.max(left, 0);
        elementSpace_Right = Math.max(right, 0);
        elementSpace_Top = Math.max(top, 0);
        elementSpace_Bottom = Math.max(bottom, 0);
        updateElements();
    }

    public int getColumns() {
        return columns;
    }

    public int getRows() {
        return rows;
    }

}
