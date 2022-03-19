package ooo.paulsen.ui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

public class PUIFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private int maxFrameRate = 30;

    // registered Elements for managing drawing elements
    private volatile CopyOnWriteArrayList<PUICanvas> elements = new CopyOnWriteArrayList<>();

    private volatile PUIUpdatable updateElements;
    private volatile PUIPaintable backgroundPaint; // called by canvas
    private volatile Component canvas;

    private int w = 100, h = 100;
    private String displayName = "PUIFrame"; // only for init
    private boolean hasInit = false, continuousDraw = false;
    private int minUpdateDelay = 0;

    public PUIFrame() {
        super();
        constructorInit();
    }

    public PUIFrame(String displayName){
        super();
        this.displayName = displayName;
        constructorInit();
    }

    public PUIFrame(int width, int height) {
        super();
        this.w = width;
        this.h = height;
        constructorInit();
    }

    public PUIFrame(String displayName, int width, int height) {
        super();
        this.displayName = displayName;
        this.w = width;
        this.h = height;
        constructorInit();
    }

    private void constructorInit() {
        setTitle(displayName);
        setSize(w, h);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initCanvas();
        initTimer();

        setVisible(true);

        hasInit = true;
    }

    private void initCanvas() {
        canvas = new JLabel() {
            private static final long serialVersionUID = 1L;

            protected void paintComponent(Graphics g2) {
                if (!hasInit) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                    }
                    repaint();
                }

                Graphics2D g = ((Graphics2D) g2);

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
            }
        };

        add(canvas);
    }

    private void initTimer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (continuousDraw) {
                        try {
                            Thread.sleep(minUpdateDelay);
                        } catch (InterruptedException e) {
                        }
                        repaint();
                    } else {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
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
        if (canvas != null) {
            canvas.repaint();
        }
    }

    public int getMaxFrameRate() {
        return maxFrameRate;
    }

    /**
     * @param maxFrameRate Values from 1-1000
     */
    public void setMaxFrameRate(int maxFrameRate) {
        if (maxFrameRate < 1 || maxFrameRate > 1000)
            return;
        this.maxFrameRate = maxFrameRate;
        minUpdateDelay = 1000 / maxFrameRate;
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
        Comparator<PUICanvas> comp = new Comparator<>() {
            @Override
            public int compare(PUICanvas o1, PUICanvas o2) {
                if (o1.getDrawLayer() < o2.getDrawLayer())
                    return -1;
                if (o1.getDrawLayer() > o2.getDrawLayer())
                    return 1;

                // same layer but different time
                if (o1.getCreationID() < o2.getCreationID())
                    return 1;
                return -1;
            }
        };
        elements.sort(comp);

//        System.out.println("draw Order:");
//        for (PUIElement e : elements) {
//            System.out.println(" " + e.getInteractionLayer());
//        }
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
     * @param title displayed Toptext
     * @param comboBoxInput String-Array of Options
     * @return index from 0 to comboBoxInput.length. <b>If -1:</b> no valid Option was selected
     */
    public int getUserSelection(String title, String comboBoxInput[]) {
        JComboBox<String> box = new JComboBox<>(comboBoxInput);
        JOptionPane.showMessageDialog(canvas, box, title, JOptionPane.QUESTION_MESSAGE);
        return box.getSelectedIndex();
    }

    /**
     * Creates a popup-window which lets u choose one of the Options
     *
     * @param title
     * @param comboBoxInput String-ArrayList of Options
     * @return index from 0 to comboBoxInput.length
     */
    public int getUserSelection(String title, ArrayList<String> comboBoxInput) {
        String s[] = new String[comboBoxInput.size()];
        for (int i = 0; i < s.length; i++)
            s[i] = comboBoxInput.get(i);
        return getUserSelection(title, s);
    }

}
