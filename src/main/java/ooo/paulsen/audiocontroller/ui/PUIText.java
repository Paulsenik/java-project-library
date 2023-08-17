package ooo.paulsen.audiocontroller.ui;

import ooo.paulsen.audiocontroller.ui.core.PUIFrame;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

public class PUIText extends PUIElement {

    /**
     * Factor, how much space is left between the characters according to the size
     */
    public static final float characterSpacingFactor = 0.8f;
    public static final String FONT_NORMAL = "Arial", FONT_SELECT = "Consolas";

    public String normalFont = FONT_NORMAL;
    /**
     * Has to be Monospace
     */
    public String selectFont = FONT_SELECT;

    // Higly suggested Monospaced Fonts => fonts that have the same width
    public static final String[] selectableFonts = {"Consolas", "Courier New", "Lucida Console", "MingLiU-ExtB",
            "MingLiU_HKSCS-ExtB", "MS Gothic", "NSimSun", "SimSun", "SimSun-ExtB"};

    private final ArrayList<Runnable> markerUpdateActions = new ArrayList<>();
    private String text;
    private boolean isTextCropped = false;
    private int markerA = 0, markerB = 0;
    private boolean selectable = false, centered = false;

    private boolean hasPressedFirst = false;

    public PUIText(PUIFrame l, String text) {
        super(l);
        constructor();
        setText(text);
    }

    public PUIText(PUIFrame l) {
        super(l);
        constructor();
    }

    public PUIText(PUIFrame l, String text, int layer) {
        super(l);
        constructor();
        setText(text);
        setLayer(layer);
    }

    public PUIText(PUIFrame l, int layer) {
        super(l);
        constructor();
        setLayer(layer);
    }

    private void constructor() {
        mouseListeners.add(new MouseListener() {
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
        mouseMotionListeners.add(new MouseMotionListener() {
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
            drawSelected(g);
        } else { // Normal Text Display with Clipping Area

            g.setColor(getTextColor());
            g.setFont(new Font(normalFont, Font.PLAIN, (int) (h * 0.8)));
            g.drawString(text, x + h/10, (int) (y + h * 0.8));
        }
    }

    protected void drawSelected(Graphics g) {
        g.setColor(color(3));
        int tempX, tempW;
        if (markerA > markerB) {
            tempX = (int) (x + markerB * h * characterSpacingFactor - h / 10);
            tempW = (int) ((markerA - markerB) * h * characterSpacingFactor + (tempX < x ? 0 : h / 10));
        } else {
            tempX = (int) (x + markerA * h * characterSpacingFactor - h / 10);
            tempW = (int) ((markerB - markerA) * h * characterSpacingFactor + (tempX < x ? 0 : h / 10));
        }
        tempW = (tempX + tempW > x + w ? (x + w - tempX) : tempW);
        g.fillRect((Math.max(tempX, x)) + 1, y + 1, tempW - 1, h - 1);

        isTextCropped = false;
        g.setColor(getTextColor());

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
    }

    /**
     * @param fontHeight => height of text
     * @return calculated minimumWidth for the text to be displayed completely
     */
    public static int getTextWidth(int fontHeight, int stringLength) {
        return (int) (fontHeight * stringLength * characterSpacingFactor);
    }

    public static int getTextHeight(int width, int stringLength) {
        return (int) (width / stringLength / characterSpacingFactor);
    }

    @Override
    public void draw(Graphics2D g) {
        if (g == null || !isEnabled())
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

    /**
     * Sets an installed <b>Monospace-Font</b> as the font that is displayed if the PUIText is <b>selectable</b>
     *
     * @param selectFont Should be a Monospace-Font!!! Due to pixel-alignment with the selected Area (relative to font-size)
     */
    public void setSelectFont(String selectFont) {
        this.selectFont = selectFont;
    }

    public String getSelectedText() {
        StringBuilder s = new StringBuilder();
        for (int i = (Math.min(markerA, markerB)); i < (Math.max(markerA, markerB)); i++)
            s.append(text.charAt(i));
        return s.toString();
    }

    /**
     * @return Text without the selected part
     */
    public String getTextWithoutSelected() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < text.length(); i++)
            if (!(i >= (Math.min(markerA, markerB)) && i < (Math.max(markerA, markerB))))
                s.append(text.charAt(i));
        return s.toString();
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
        this.markerA = selectStart > text.length() ? text.length() : (Math.max(selectStart, 0));
        if (prev != markerA)
            runAllMarkerUpdateActions();
    }

    public int getMarkerB() {
        return markerB;
    }

    public void setMarkerB(int selectEnd) {
        int prev = markerB;
        this.markerB = selectEnd > text.length() ? text.length() : (Math.max(selectEnd, 0));
        if (prev != markerB)
            runAllMarkerUpdateActions();
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
