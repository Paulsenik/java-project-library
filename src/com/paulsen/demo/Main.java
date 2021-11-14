package com.paulsen.demo;

import com.paulsen.ui.*;
import com.paulsen.ui.PUIElement.ElementAlignment;

import java.awt.*;

public class Main {

    public Main() {

        // initialize variables before using them in update/paint
        PUIFrame f = new PUIFrame("Project-Library Demo", 600, 600);


        // Drawing a rectangle in the background
        PUICanvas canvas = new PUICanvas(f, new PUIPaintable() {
            @Override
            public void paint(Graphics g, int x, int y, int w, int h) {
                g.setColor(new Color(100, 100, 100));
                g.fillRoundRect(40, 40, w - 80, h - 80, 20, 20);
            }
        }, -1);


        PUIText darkmodeButton = new PUIText(f, "LIGHT", 2);
        darkmodeButton.addActionListener(new PUIAction() {
            @Override
            public void run(PUIElement that) {
                PUIElement.darkUIMode = !PUIElement.darkUIMode;
                if (PUIElement.darkUIMode) {
                    darkmodeButton.setText("DARK");
                } else {
                    darkmodeButton.setText("LIGHT");
                }
            }
        });
        // if set to false: when pressed the eventchain doesnt stop => elements on layers behind this Button can be triggered as well
        darkmodeButton.setRaycastable(false);

        PUIScrollPanel sp = new PUIScrollPanel(f);

        PUICheckBox cb = new PUICheckBox(f);
        cb.addActionListener(new PUIAction() {
            @Override
            public void run(PUIElement that) {
                if (sp.getAlignment() == ElementAlignment.VERTICAL)
                    sp.setAlignment(ElementAlignment.HORIZONTAL);
                else
                    sp.setAlignment(ElementAlignment.VERTICAL);
            }
        });

        PUIRotaryControl rc = new PUIRotaryControl(f, 1);

        PUISlider slider = new PUISlider(f);
        slider.setValue(0.5f);
        slider.setAlignment(ElementAlignment.HORIZONTAL);
        slider.addValueUpdateAction(new Runnable() {
            @Override
            public void run() {
                rc.setValueLength(slider.getValue());
            }
        });

        PUISlider slider2 = new PUISlider(f);
        slider2.setAlignment(ElementAlignment.HORIZONTAL);
        slider2.addValueUpdateAction(new Runnable() {
            @Override
            public void run() {
                rc.setValueThickness((int) (67 * slider2.getValue()));
            }
        });

        // add test-Buttons for scrollpanel
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
            }
        });

        // Set Position of other non-relative Elements
        darkmodeButton.setBounds(50, 50, 300, 100);
        slider.setBounds(50, 200, 300, 50);
        slider2.setBounds(50, 250, 300, 50);

        f.updateElements();
    }

    public static void main(String[] args) {
        new Main();
    }

}
