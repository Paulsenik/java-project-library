package ooo.paulsen.audiocontroller.ui.core;

import ooo.paulsen.audiocontroller.ui.PUIElement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

public class PUIFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private int maxRepaintRate;

    // registered Elements for managing drawing elements
    private final CopyOnWriteArrayList<PUICanvas> elements = new CopyOnWriteArrayList<>();

    private volatile PUIUpdatable updateElements;
    private volatile PUIPaintable backgroundPaint; // called by canvas
    private volatile Component canvas;

    private int w = 100, h = 100;
    private String displayName = "PUIFrame"; // only for init
    private boolean hasInit = false, continuousDraw = false, hasToRepaint = false;
    private int minUpdateDelay = 0;

    // draw-time
    private long lastRepaint_Start, lastRepaint_End;
    private long deltaTime; // delta-Frame-Time in millis
    private long minDeltaTime = Long.MAX_VALUE, maxDeltaTime = Long.MIN_VALUE;

    public PUIFrame() {
        super();
        constructorInit(true);
    }

    public PUIFrame(boolean showFrame) {
        super();
        constructorInit(showFrame);
    }

    public PUIFrame(String displayName) {
        super();
        this.displayName = displayName;
        constructorInit(true);
    }

    public PUIFrame(String displayName, boolean showFrame) {
        super();
        this.displayName = displayName;
        constructorInit(showFrame);
    }

    public PUIFrame(int width, int height) {
        super();
        this.w = width;
        this.h = height;
        constructorInit(true);
    }

    public PUIFrame(int width, int height, boolean showFrame) {
        super();
        this.w = width;
        this.h = height;
        constructorInit(showFrame);
    }

    public PUIFrame(String displayName, int width, int height) {
        super();
        this.displayName = displayName;
        this.w = width;
        this.h = height;
        constructorInit(true);
    }

    public PUIFrame(String displayName, int width, int height, boolean showFrame){
        super();
        this.displayName = displayName;
        this.w = width;
        this.h = height;
        constructorInit(showFrame);
    }

    private void constructorInit(boolean showFrame) {
        setTitle(displayName);
        setSize(w, h);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initWindowListener();
        initCanvas();
        initTimer();

        setMaxRepaintRate(30); // set initial value

        hasInit = true;

        repaint();
        setVisible(showFrame);
    }

    private void initWindowListener() {
        addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                repaint();
            }

            @Override
            public void windowLostFocus(WindowEvent e) {

            }
        });
    }

    private void initCanvas() {
        canvas = new JLabel() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g2) {

                if (!hasInit) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ignored) {
                    }
                    repaint();
                }

                Graphics2D g = ((Graphics2D) g2);

                lastRepaint_Start = System.currentTimeMillis();

                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g.setColor(Color.DARK_GRAY);
                g.fillRect(0, 0, getWidth(), getHeight());

                if (backgroundPaint != null)
                    backgroundPaint.paint(g, 0, 0, w, h);

                // Prevent Concurrent-Modification-Errors
                ArrayList<PUICanvas> tempEl = new ArrayList<>(elements);
                // Paints Elements
                for (PUICanvas el : tempEl) {
                    if (el == null)
                        continue;

                    if (!(el instanceof PUIElement))
                        g.setColor(Color.black);
                    if (el.isVisible()) {
                        el.draw(g);
                    }
                }

                lastRepaint_End = System.currentTimeMillis();
                deltaTime = lastRepaint_End - lastRepaint_Start;

                minDeltaTime = Math.min(deltaTime, minDeltaTime);
                maxDeltaTime = Math.max(deltaTime, maxDeltaTime);

                // finished repaint
                hasToRepaint = false;
            }
        };

        add(canvas);
    }

    private void initTimer() {
        new Thread(() -> {
            while (true) {

                // Draw as fast as possible
                if (continuousDraw) {
                    canvas.repaint();

                    // Draw only to the max-Repaint-Rate
                } else if (lastRepaint_End + minUpdateDelay < System.currentTimeMillis()) {

                    if (hasToRepaint && isPaintable()) {
                        if (canvas != null) {
                            canvas.repaint();
                        }
                    } else {

                        // wait for first repaint()-call
                        try {
                            Thread.sleep(minUpdateDelay);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        }).start();

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (w != canvas.getWidth() || h != canvas.getHeight()) {
                    w = canvas.getWidth();
                    h = canvas.getHeight();
                    updateElements();
                }
            }
        }, 100, 10);
    }

    public boolean isPaintable(){
        return getState() == Frame.NORMAL && isVisible();
    }

    public void updateElements() {
        if (updateElements != null) {
            updateElements.update(w, h);
            repaint();
        }
    }

    public void setUpdateElements(PUIUpdatable r) {
        updateElements = r;
    }

    public void setBackgroud(PUIPaintable p) {
        backgroundPaint = p;
    }

    public int w() {
        return w;
    }

    public int h() {
        return h;
    }

    public Component c() {
        return canvas;
    }

    public Component canvas() {
        return canvas;
    }

    public boolean hasInit() {
        return hasInit;
    }

    public synchronized void add(PUICanvas element) {
        if (element != null && !elements.contains(element)) {
            elements.add(element);
            rearrangeElements();
        }
    }

    public synchronized void remove(PUICanvas element) {
        if (element != null) {
            elements.remove(element);
            rearrangeElements();
        }
    }

    public synchronized void remove(PUIElement element) {
        if (element != null) {
            elements.remove(element);
            element.release();
            rearrangeElements();
        }
    }

    public void repaint() {
        hasToRepaint = true;
    }

    public long getMinDeltaTime() {
        return minDeltaTime;
    }

    public long getMaxDeltaTime() {
        return maxDeltaTime;
    }

    public long getDeltaTime() {
        return deltaTime;
    }

    public int getMaxRepaintRate() {
        return maxRepaintRate;
    }

    /**
     * @param maxRepaintRate Values from 1-1000
     */
    public void setMaxRepaintRate(int maxRepaintRate) {
        if (maxRepaintRate < 1 || maxRepaintRate > 1000)
            return;
        this.maxRepaintRate = maxRepaintRate;
        minUpdateDelay = 1000 / maxRepaintRate;
    }

    public boolean isContinuousDraw() {
        return continuousDraw;
    }

    public void setContinuousDraw(boolean continuousUpdate) {
        this.continuousDraw = continuousUpdate;
    }

    /**
     * sets Draw-Order
     */
    public void rearrangeElements() {
        Comparator<PUICanvas> comp = (o1, o2) -> {
            if (o1.getDrawLayer() < o2.getDrawLayer())
                return -1;
            if (o1.getDrawLayer() > o2.getDrawLayer())
                return 1;

            // same layer but different time
            if (o1.getCreationID() < o2.getCreationID())
                return 1;
            return -1;
        };
        elements.sort(comp);
    }

    public String getUserInput(String message, String initialValue) {
        return JOptionPane.showInputDialog(canvas, message, initialValue);
    }

    public void sendUserError(String message) {
        JOptionPane.showMessageDialog(canvas, message, "ERROR", JOptionPane.ERROR_MESSAGE);
    }

    public void sendUserWarning(String message) {
        JOptionPane.showMessageDialog(canvas, message, "WARNING", JOptionPane.WARNING_MESSAGE);
    }

    public void sendUserInfo(String message) {
        JOptionPane.showMessageDialog(canvas, message, "INFO", JOptionPane.INFORMATION_MESSAGE);
    }

    public boolean getUserConfirm(String message, String title) {
        return JOptionPane.showConfirmDialog(canvas, message, title, JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION;
    }

    /**
     * Creates a popup-window which lets u choose one of the Options
     *
     * @param title         displayed Toptext
     * @param comboBoxInput String-Array of Options
     * @return index from 0 to comboBoxInput.length. <b>If -1:</b> no valid Option was selected
     */
    public int getUserSelection(String title, String[] comboBoxInput) {
        JComboBox<String> box = new JComboBox<>(comboBoxInput);
        JOptionPane.showMessageDialog(canvas, box, title, JOptionPane.QUESTION_MESSAGE);
        return box.getSelectedIndex();
    }

    /**
     * Creates a popup-window which lets u choose one of the Options
     *
     * @param comboBoxInput String-ArrayList of Options
     * @return index from 0 to comboBoxInput.length
     */
    public int getUserSelection(String title, ArrayList<String> comboBoxInput) {
        String[] s = new String[comboBoxInput.size()];
        for (int i = 0; i < s.length; i++)
            s[i] = comboBoxInput.get(i);
        return getUserSelection(title, s);
    }

}
