package ooo.paulsen.ui;

import ooo.paulsen.ui.core.*;

import javax.management.InvalidAttributeValueException;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;

public class PUIElement extends PUICanvas { // PaulsenUserInterfaceIntegratedElement

    // Static
    public static volatile boolean useGBC = false; // signals PUICore to use System.gbc()
    public static volatile CopyOnWriteArrayList<PUIElement> registeredElements = new CopyOnWriteArrayList<PUIElement>();
    public static volatile Color[] default_colors = new Color[]{
            new Color(47, 47, 47), // BG
            new Color(196, 196, 196), // Text
            new Color(57, 57, 57), // BG_accent
            new Color(81, 81, 81) // Text_accent
    };

    public enum ElementAlignment {
        HORIZONTAL, VERTICAL
    }

    protected int x = 0, y = 0, w = 0, h = 0, arcWidth = 15, arcHeight = 15;
    protected volatile Color colors[] = new Color[0];
    protected PUIPaintable hoverOverlay, pressOverlay;
    protected PUIFrame frame;
    protected PUICore core;
    protected CopyOnWriteArrayList<PUIAction> actions = new CopyOnWriteArrayList<>();
    protected CopyOnWriteArrayList<MouseListener> mouseListeners = new CopyOnWriteArrayList<>();
    protected CopyOnWriteArrayList<MouseMotionListener> mouseMotionListeners = new CopyOnWriteArrayList<>();
    protected CopyOnWriteArrayList<MouseWheelListener> mouseWheelListeners = new CopyOnWriteArrayList<>();
    protected Object metaData;
    protected boolean repaintFrameOnEvent = true, paintOverOnHover = true, paintOverOnPress = true;
    protected boolean blockRaycast = true;
    private boolean enabled = true;
    private int interactionLayer = 0;
    // TEMP-vars
    // pressed -> is pressed on Screen
    // isCurrentlyPressing -> is pressing on Element
    protected boolean hovered = false, pressed = false, isCurrentlyPressing = false;

    public PUIElement(PUIFrame f) {
        super(f, null);
        this.frame = f;
        init();
        initCore();
    }

    public PUIElement(PUIFrame f, int layer) {
        super(f, null, layer);
        this.frame = f;
        init();
        initCore();
    }

    private void init() {
        registeredElements.add(this);
        mouseMotionListeners.add(new MouseMotionListener() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (!enabled) return;
                hovered = contains(e.getPoint());
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (!enabled) return;
                Point p = e.getPoint();
                hovered = contains(e.getPoint());

            }
        });
        mouseListeners.add(new MouseListener() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (enabled) hovered = contains(e.getPoint());
                pressed = false;
                isCurrentlyPressing = false;
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (!enabled) return;
                if (!pressed && hovered) {
                    Point p = e.getPoint();
                    if (p != null && getBounds().contains(p)) {
                        isCurrentlyPressing = true;
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

        paint = new PUIPaintable() {
            @Override
            public void paint(Graphics2D g, int x, int y, int w, int h) {
                g.setColor(getBackgroundColor());
                g.fillRoundRect(x, y, w, h, arcWidth, arcHeight);

                g.setColor(color(2));
                g.drawRoundRect(x, y, w, h, arcWidth, arcHeight);
            }
        };
        hoverOverlay = new PUIPaintable() {
            @Override
            public void paint(Graphics2D g, int x, int y, int w, int h) {
                g.setColor(new Color(100, 100, 100, 100));
                g.fillRoundRect(x, y, w, h, arcWidth, arcHeight);
            }
        };
        pressOverlay = new PUIPaintable() {
            @Override
            public void paint(Graphics2D g, int x, int y, int w, int h) {
                g.setColor(new Color(100, 100, 100, 200));
                g.fillRoundRect(x, y, w, h, arcWidth, arcHeight);
            }
        };
    }

    private void initCore() {
        if (frame == null) {
            return;
        }
        core = PUICore.getCore(frame);

        // No core found
        if (core == null) {
            try {
                // create new core
                core = PUICore.getInstance(frame);
            } catch (InvalidAttributeValueException e1) {
                e1.printStackTrace();
                return;
            }
        }

        if (core != null) {
            core.addElement(this);
        }
    }

    @Override
    public synchronized void draw(Graphics2D g) {
        if (g == null || !isEnabled()) return;

        if (paint != null) {
            paint.paint(g, x, y, w, h);
        }
        if (hovered) {
            if (hovered && !pressed && paintOverOnHover) {
                if (hoverOverlay != null) hoverOverlay.paint(g, x, y, w, h);
            } else if (pressed && paintOverOnPress) {
                if (pressOverlay != null) pressOverlay.paint(g, x, y, w, h);
            }
        }
    }

    /**
     * Is used by the Eventsystem to determine if a position of the screen is part of this element
     * <p>
     * Is useful to overwrite if a new Element, based on PUIElement, is created that is not rectangular (e.g Circle, Polygon).
     * So that the EventSystem knows how to handle incomming MouseClicks on this Element
     *
     * @param p
     * @return TRUE if p in Frame-Space is part of the element and FALSE if not.
     */
    public boolean contains(Point p) {
        return getBounds().contains(p);
    }

    public ArrayList<PUIAction> getActionListeners() {
        return new ArrayList<>(actions);
    }

    public void runAllActions() {

        if (actions != null) for (PUIAction r : actions)
            if (r != null) r.run(this);

        if (repaintFrameOnEvent && frame != null) {
            frame.repaint();
        }
    }

    public void refreshMouseInteraction() {
        hovered = false;
        pressed = false;
        isCurrentlyPressing = false;
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

    public void doUpdateFrameOnEvent(boolean updateFrameOnEvent) {
        this.repaintFrameOnEvent = updateFrameOnEvent;
    }

    public boolean isUpdateFrameOnEvent() {
        return repaintFrameOnEvent;
    }

    public boolean isPressed() {
        return pressed;
    }

    public boolean isCurrentlyPressing() {
        return isCurrentlyPressing;
    }

    public boolean isEnabled() {
        return enabled;
    }

    /**
     * If enabled is set FALSE: The Event-/Listening-System ignores this Element and also doesn't draw it.
     *
     * @param enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean blocksRaycast() {
        return blockRaycast;
    }

    /**
     * Releases element from core & register-list
     */
    public void release() {
        core.removeElement(this);
        registeredElements.remove(this);
        useGBC = true;
    }

    /**
     * @param doesBlockRaycast if set to false: when pressed => the eventchain doesnt stop => elements on layers behind this Button can be triggered as well
     */
    public void doBlockRaycast(boolean doesBlockRaycast) {
        this.blockRaycast = doesBlockRaycast;
    }

    @Override
    public void setDrawLayer(int drawLayer) {
        this.drawLayer = drawLayer;
        frame.rearrangeElements();
    }

    public int getInteractionLayer() {
        return interactionLayer;
    }

    public void setInteractionLayer(int interactionLayer) {
        this.interactionLayer = interactionLayer;
        core.rearrangeElements();
    }

    public void setLayer(int l) {
        drawLayer = l;
        interactionLayer = l;
        if (core != null) core.rearrangeElements();
        if (frame != null) frame.rearrangeElements();
    }

    public ArrayList<MouseMotionListener> getMouseMotionListeners() {
        return new ArrayList<>(mouseMotionListeners);
    }

    public ArrayList<MouseWheelListener> getMouseWheelListeners() {
        return new ArrayList<>(mouseWheelListeners);
    }

    public ArrayList<MouseListener> getMouseListeners() {
        return new ArrayList<>(mouseListeners);
    }

    public Color getBackgroundColor() {
        return color(0);
    }

    public Color getTextColor() {
        return color(1);
    }

    public void setBackgroundColor(Color backgroundColor) {
        setColor(0, backgroundColor);
    }

    public Color color(int index) {
        if (colors.length > index) {
            return colors[index];
        }
        if (default_colors.length > index) {
            return default_colors[index];
        }
        return null;
    }

    public void setColor(int index, Color color) {
        if (colors.length > index) {
            colors[index] = color;
        } else {
            colors = Arrays.copyOf(colors, index + 1);
            colors[index] = color;
        }
    }

    /**
     * @return Default-Color of the library - Null if the Could does not exist
     */
    public static Color getDefaultColor(int index) {
        if (default_colors.length > index) {
            return default_colors[index];
        }
        return null;
    }

    /*
    Sets Default-Color of the library
     */
    public static void setDefaultColor(int index, Color color) {
        if (default_colors.length > index) {
            default_colors[index] = color;
        } else {
            default_colors = Arrays.copyOf(default_colors, index + 1);
            default_colors[index] = color;
        }
    }

    public Object getMetadata() {
        return metaData;
    }

    public void setMetadata(Object o) {
        metaData = o;
    }

    public String getUserInput(String message, String initialValue) {
        return frame.getUserInput(message, initialValue);
    }

    public void sendUserError(String message) {
        frame.sendUserError(message);
    }

    public void sendUserWarning(String message) {
        frame.sendUserWarning(message);
    }

    public void sendUserInfo(String message) {
        frame.sendUserInfo(message);
    }

    public boolean getUserConfirm(String message, String title) {
        return frame.getUserConfirm(message, title);
    }

    /**
     * Creates a popup-window which lets u choose one of the Options
     *
     * @param title
     * @param comboBoxInput String-Array of Options
     * @return index from 0 to comboBoxInput.length
     */
    public int getUserSelection(String title, String comboBoxInput[]) {
        return frame.getUserSelection(title, comboBoxInput);
    }

    /**
     * Creates a popup-window which lets u choose one of the Options
     *
     * @param title
     * @param comboBoxInput String-ArrayList of Options
     * @return index from 0 to comboBoxInput.length
     */
    public int getUserSelection(String title, ArrayList<String> comboBoxInput) {
        return frame.getUserSelection(title, comboBoxInput);
    }

}
