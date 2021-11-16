package com.paulsen.ui;

import javax.management.InvalidAttributeValueException;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

public final class PUICore {

    private static CopyOnWriteArrayList<PUICore> registeredCores = new CopyOnWriteArrayList<>();
    private PUIFrame f;
    // registered Elements for managing inputs from user
    private volatile CopyOnWriteArrayList<PUIElement> elements = new CopyOnWriteArrayList<>();

    public PUICore(PUIFrame f) throws InvalidAttributeValueException {
        for (PUICore core : registeredCores)
            if (core.f == f)
                throw new InvalidAttributeValueException("This Component has already been registered!");

        registeredCores.add(this);
        this.f = f;
        init();
    }

    /**
     * @return PUICore that has component <code>c</code> or <code>null</code> if
     * core with given component doesn't exist
     */
    public static PUICore getCore(PUIFrame f) {
        for (PUICore core : registeredCores)
            if (core.f == f)
                return core;
        return null;
    }

    private void init() {
        System.out.println("init PUICore");

        f.c().addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
                int firstHitLayer = -1; //2D-Raycast from top to bottom
                for (PUIElement elem : elements)
                    if ((firstHitLayer == -1 || firstHitLayer == elem.getInteractionLayer()) && elem.contains(e.getPoint())) {
                        if (elem.blocksRaycast()) {
                            firstHitLayer = elem.getInteractionLayer();
                        }
                        for (MouseListener listener : elem.getMouseListeners())
                            listener.mousePressed(e);
                    } else if ((firstHitLayer != -1 && firstHitLayer != elem.getInteractionLayer())) {
                        return; // break from loop because 2DRaycast has been triggered and the layers behind are not reachable
                    }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                for (PUIElement elem : elements)
                    for (MouseListener listener : elem.getMouseListeners())
                        listener.mouseReleased(e);
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        f.c().addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                for (PUIElement elem : elements)
                    for (MouseMotionListener listener : elem.getMouseMotionListeners())
                        listener.mouseDragged(e);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                for (PUIElement elem : elements)
                    for (MouseMotionListener listener : elem.getMouseMotionListeners())
                        listener.mouseMoved(e);
            }
        });
        f.c().addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                int firstHitLayer = -1; //2D-Raycast from top to bottom
                for (PUIElement elem : elements)
                    if ((firstHitLayer == -1 || firstHitLayer == elem.getInteractionLayer()) && elem.contains(e.getPoint())) {
                        if (elem.blocksRaycast())
                            firstHitLayer = elem.getInteractionLayer();
                        for (MouseWheelListener listener : elem.getMouseWheelListeners())
                            listener.mouseWheelMoved(e);
                    } else if ((firstHitLayer != -1 && firstHitLayer != elem.getInteractionLayer())) {
                        return; // break from loop because 2DRaycast has been triggered and the layers behind are not reachable
                    }
            }
        });

        // init java-GBC
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (PUIElement.useGBC) {
                    System.gc();
                    PUIElement.useGBC = false;
                }
            }
        }, 0, 10000);
    }

    public void addElement(PUIElement e) {
        if (e != null && !elements.contains(e)) {
            elements.add(e);
            rearrangeElements();
        }
    }

    public void removeElement(PUIElement e) {
        if (e != null) {
            elements.remove(e);
            rearrangeElements();
        }
    }

    /**
     * Sorts Elements by interactionlayer
     */
    public void rearrangeElements() {
        Comparator<PUIElement> comp = new Comparator<PUIElement>() {
            @Override
            public int compare(PUIElement o1, PUIElement o2) {
                if (o1.getInteractionLayer() > o2.getInteractionLayer())
                    return -1;
                if (o1.getInteractionLayer() < o2.getInteractionLayer())
                    return 1;

                // same layer but different time
                if (o1.getCreationID() < o2.getCreationID())
                    return -1;
                return 1;
            }
        };
        elements.sort(comp);

//        System.out.println("layer:");
//        for (PUIElement e : elements) {
//            System.out.println(" " + e.getInteractionLayer());
//        }
    }

    public ArrayList<PUIElement> getElements() {
        return new ArrayList<>(elements);
    }

}
