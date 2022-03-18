package ooo.paulsen.demo;

import ooo.paulsen.ui.*;
import ooo.paulsen.ui.PUIElement.ElementAlignment;

import java.awt.*;

public class Demo {

    public static void main(String[] args) {
        new Demo();
    }

    // PUI-Objects
    PUIFrame f;
    PUICanvas canvas;
    PUIMatrix matrix;
    PUIText darkmodeButton;
    PUIScrollPanel sp;
    PUICheckBox cb;
    PUIRotaryControl rc;
    PUISlider slider;
    PUISlider slider2;

    public Demo() {

        // initialize frame before creating Elements
        f = new PUIFrame("Project-Library Demo", 600, 600);


        // Drawing a rectangle in the background
        canvas = new PUICanvas(f, new PUIPaintable() {
            @Override
            public void paint(Graphics2D g, int x, int y, int w, int h) {
                g.setColor(new Color(100, 100, 100));
                g.fillRoundRect(40, 40, w - 80, h - 80, 20, 20);
            }
        }, -1);

        // Scaling Matrix
        matrix = new PUIMatrix(f, 4, 8);
        // generate Elements fot the Matrix to scale them
        setMatrixElements(false);


        darkmodeButton = new PUIText(f, "LIGHT", 2);
        darkmodeButton.addActionListener(new PUIAction() {
            @Override
            public void run(PUIElement that) {
                PUIElement.darkUIMode = !PUIElement.darkUIMode;
                if (PUIElement.darkUIMode) {
                    darkmodeButton.setText("DARK");
                    sp.setEnabled(false); // set any Element as disabled -> No more Interaction and no Visuals with this Element
                } else {
                    darkmodeButton.setText("LIGHT");
                    sp.setEnabled(true);
                }
            }
        });
        // if set to false: when pressed the eventchain doesnt stop => elements on layers behind this Button can be triggered as well
        darkmodeButton.doBlockRaycast(false);

        sp = new PUIScrollPanel(f);

        cb = new PUICheckBox(f);
        cb.addActionListener(new PUIAction() {
            @Override
            public void run(PUIElement that) {
                if (sp.getAlignment() == ElementAlignment.VERTICAL) {
                    sp.setAlignment(ElementAlignment.HORIZONTAL);

                    setMatrixElements(true);

                    rc.setEnabled(false);
                } else {
                    sp.setAlignment(ElementAlignment.VERTICAL);

                    setMatrixElements(false);

                    rc.setEnabled(true);
                }
            }
        });

        rc = new PUIRotaryControl(f, 1);
        rc.addValueUpdateAction(new Runnable() {
            @Override
            public void run() {
                // RotaryControl changes spacing between elements in PUIScrollPanel & PUIMatrix
                int space = (int) (rc.getValue() * 7);
                sp.setElementSpacing(space, space, space * 2, space * 2);
                matrix.setElementSpacing(space, space, space, space);
            }
        });

        slider = new PUISlider(f);
        slider.setValue(0.5f);
        slider.setAlignment(ElementAlignment.HORIZONTAL);
        slider.addValueUpdateAction(new Runnable() {
            @Override
            public void run() {
                rc.setValueLength(slider.getValue());
            }
        });

        slider2 = new PUISlider(f);
        slider2.setAlignment(ElementAlignment.HORIZONTAL);
        slider2.addValueUpdateAction(new Runnable() {
            @Override
            public void run() {
                rc.setValueThickness((int) (360 * slider2.getValue()));
            }
        });

        // add test-Buttons for scrollPanel
        for (int i = 1; i <= 10; i++) {
            PUIText t = new PUIText(f, "" + i);
            t.addActionListener(new PUIAction() {
                @Override
                public void run(PUIElement that) {
                    f.setTitle(((PUIText) that).getText());

                    // automatically centers clicked Element in the UI
                    sp.center(that);
                }
            });
            sp.addElement(t);
        }
        // comment out if the size of the element inside of the panel should be further limited
//        sp.setElementSpacing(6,0,3,3);

        // prevent different colors when hovering/pressing
        for (PUIElement e : PUIElement.registeredElements) {
            e.doPaintOverOnHover(false);
            e.doPaintOverOnPress(false);
        }

        f.setUpdateElements(new PUIUpdatable() { // initialize updateMethod
            @Override
            public void update(int w, int h) {

                // Element-Positions can also be defined relative by using width & height variables

                cb.setBounds(w - 150, 50, 100, 100);// relative
                rc.setBounds(w - 150, 200, 100, 100);// relative
                sp.setBounds(50, h - 200, 300, 150); // relative

                matrix.setBounds(390, 340, w - 440, h - 390);// relative
            }
        });

        // Set Position of other non-relative Elements
        darkmodeButton.setBounds(50, 50, 300, 100);
        slider.setBounds(50, 200, 300, 50);
        slider2.setBounds(50, 250, 300, 50);

        f.updateElements();

    }

    public void setMatrixElements(boolean genHalf) {
        for (int i = 0; i < matrix.getColumns(); i++) {
            for (int j = 0; j < matrix.getRows(); j++) {
                f.remove(matrix.get(i, j));
            }
        }
        matrix.reset();
        if (genHalf)
            for (int i = 0; i < 4; i++)
                for (int j = (i % 2 == 0 ? 0 : 1); j < 8; j += 2) {
                    PUIElement e = new PUIText(f, i + "," + j);
                    e.addActionListener(new PUIAction() {
                        @Override
                        public void run(PUIElement that) {
                            that.getFrame().setTitle(((PUIText) that).getText());
                        }
                    });
                    matrix.setElement(e, i, j);
                }
        else
            for (int i = 0; i < 4; i++)
                for (int j = 0; j < 8; j++) {
                    PUIElement e = new PUIText(f, i + "," + j);
                    e.addActionListener(new PUIAction() {
                        @Override
                        public void run(PUIElement that) {
                            that.getFrame().setTitle(((PUIText) that).getText());
                        }
                    });
                    matrix.setElement(e, i, j);
                }

        // Checking MemoryOptimizations
//        System.out.println("Core-Elements: " + PUICore.getCore(f).getElements().size());
//        System.out.println("Registered-Elements: " + PUIElement.registeredElements.size());
        f.updateElements();
    }

}
