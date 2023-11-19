package de.paulsenik.jpl.ui.core;

import de.paulsenik.jpl.ui.PUIElement;
import de.paulsenik.jpl.ui.PUIText;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.management.InvalidAttributeValueException;

public final class PUICore {

  private static final CopyOnWriteArrayList<PUICore> registeredCores = new CopyOnWriteArrayList<>();
  private PUIFrame f;
  // registered Elements for managing inputs from user
  private final CopyOnWriteArrayList<PUIElement> elements = new CopyOnWriteArrayList<>();

  private PUICore() {
  }

  private PUICore(PUIFrame f) throws InvalidAttributeValueException {
    for (PUICore core : registeredCores) {
      if (core.f == f) {
        throw new InvalidAttributeValueException("This Component has already been registered!");
      }
    }

    registeredCores.add(this);
    this.f = f;
    init();
  }

  public static PUICore getInstance(PUIFrame f) throws InvalidAttributeValueException {
    return new PUICore(f);
  }

  /**
   * @return PUICore that has component <code>c</code> or <code>null</code> if core with given
   * component doesn't exist
   */
  public static PUICore getCore(PUIFrame f) {
    for (PUICore core : registeredCores) {
      if (core.f == f) {
        return core;
      }
    }
    return null;
  }

  public static void main(String[] args) {
    PUIFrame f = new PUIFrame(600, 400);

    PUIElement e1 = new PUIText(f, -1);
    e1.setBounds(0, 0, 100, 100);
    e1.addActionListener(that -> System.out.println("1"));

    PUIElement e2 = new PUIText(f, -2);
    e2.setBounds(50, 50, 100, 100);
    e2.addActionListener(that -> System.out.println("2"));

    PUIElement e3 = new PUIText(f, -3);
    e3.setBounds(10, 50, 90, 100);
    e3.addActionListener(that -> System.out.println("3"));

    new Timer().scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run() {
        f.repaint();
      }
    }, 0, 100);

  }

  private void init() {
    f.c().addMouseListener(new MouseListener() {
      @Override
      public void mouseClicked(MouseEvent e) {
        Integer firstHitLayer = null; //2D-Raycast from top to bottom
        for (PUIElement elem : elements) {
          if ((firstHitLayer == null || firstHitLayer == elem.getInteractionLayer())
              && elem.contains(e.getPoint()) && elem.isEnabled()) {
            if (elem.blocksRaycast()) {
              firstHitLayer = elem.getInteractionLayer();
            }
            for (MouseListener listener : elem.getMouseListeners()) {
              listener.mouseClicked(e);
            }
          } else if ((firstHitLayer != null && firstHitLayer != elem.getInteractionLayer()
              && elem.isEnabled())) {
            return; // break from loop because 2DRaycast has been triggered and the layers behind are not reachable
          }
        }
      }

      @Override
      public void mousePressed(MouseEvent e) {
        Integer firstHitLayer = null; //2D-Raycast from top to bottom
        for (PUIElement elem : elements) {
          if ((firstHitLayer == null || firstHitLayer == elem.getInteractionLayer())
              && elem.contains(e.getPoint()) && elem.isEnabled()) {
            if (elem.blocksRaycast()) {
              firstHitLayer = elem.getInteractionLayer();
            }
            for (MouseListener listener : elem.getMouseListeners()) {
              listener.mousePressed(e);
            }
          } else if ((firstHitLayer != null && firstHitLayer != elem.getInteractionLayer()
              && elem.isEnabled())) {
            return; // break from loop because 2DRaycast has been triggered and the layers behind are not reachable
          }
        }
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        for (PUIElement elem : elements) {
          if (elem.isEnabled()) {
            for (MouseListener listener : elem.getMouseListeners()) {
              listener.mouseReleased(e);
            }
          }
        }
      }

      @Override
      public void mouseEntered(MouseEvent e) {
        for (PUIElement elem : elements) {
          if (elem.isEnabled()) {
            for (MouseListener listener : elem.getMouseListeners()) {
              listener.mouseEntered(e);
            }
          }
        }
      }

      @Override
      public void mouseExited(MouseEvent e) {
        for (PUIElement elem : elements) {
          if (elem.isEnabled()) {
            for (MouseListener listener : elem.getMouseListeners()) {
              listener.mouseExited(e);
            }
          }
        }
      }
    });
    f.addWindowFocusListener(new WindowFocusListener() {
      @Override
      public void windowGainedFocus(WindowEvent e) {
        for (PUIElement elem : elements) {
          elem.refreshMouseInteraction();
        }
      }

      @Override
      public void windowLostFocus(WindowEvent e) {
      }
    });
    f.c().addMouseMotionListener(new MouseMotionListener() {
      @Override
      public void mouseDragged(MouseEvent e) {
        for (PUIElement elem : elements) {
          if (elem.isEnabled()) {
            for (MouseMotionListener listener : elem.getMouseMotionListeners()) {
              listener.mouseDragged(e);
            }
          }
        }
      }

      @Override
      public void mouseMoved(MouseEvent e) {
        Integer firstHitLayer = null; //2D-Raycast from top to bottom
        for (PUIElement elem : elements) {
          if ((firstHitLayer == null || firstHitLayer == elem.getInteractionLayer())
              && elem.contains(e.getPoint()) && elem.isEnabled()) {
            if (elem.blocksRaycast()) {
              firstHitLayer = elem.getInteractionLayer();
            }
            for (MouseMotionListener listener : elem.getMouseMotionListeners()) {
              listener.mouseMoved(e);
            }
          } else if ((firstHitLayer != null && firstHitLayer != elem.getInteractionLayer()
              && elem.isEnabled())) {
            return; // break from loop because 2DRaycast has been triggered and the layers behind are not reachable
          }
        }
      }
    });
    f.c().addMouseWheelListener(e -> {
      Integer firstHitLayer = null; //2D-Raycast from top to bottom
      for (PUIElement elem : elements) {
        if ((firstHitLayer == null || firstHitLayer == elem.getInteractionLayer()) && elem.contains(
            e.getPoint()) && elem.isEnabled()) {
          if (elem.blocksRaycast()) {
            firstHitLayer = elem.getInteractionLayer();
          }
          for (MouseWheelListener listener : elem.getMouseWheelListeners()) {
            listener.mouseWheelMoved(e);
          }
        } else if ((firstHitLayer != null && firstHitLayer != elem.getInteractionLayer()
            && elem.isEnabled())) {
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
    Comparator<PUIElement> comp = (o1, o2) -> {
      if (o1.getInteractionLayer() > o2.getInteractionLayer()) {
        return -1;
      }
      if (o1.getInteractionLayer() < o2.getInteractionLayer()) {
        return 1;
      }

      // same layer but different time
      if (o1.getCreationID() < o2.getCreationID()) {
        return -1;
      }
      return 1;
    };
    elements.sort(comp);

  }

  public ArrayList<PUIElement> getElements() {
    return new ArrayList<>(elements);
  }

}
