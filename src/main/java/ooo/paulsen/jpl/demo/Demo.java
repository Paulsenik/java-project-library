package ooo.paulsen.jpl.demo;

import ooo.paulsen.jpl.ui.*;
import ooo.paulsen.jpl.ui.core.PUICanvas;
import ooo.paulsen.jpl.ui.core.PUIFrame;
import ooo.paulsen.jpl.utils.PSystem;
import ooo.paulsen.jpl.io.serial.PSerialConnection;
import ooo.paulsen.jpl.ui.PUIElement.ElementAlignment;
import ooo.paulsen.jpl.utils.PInstance;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

public class Demo {

    public static void main(String[] args) {
        new Demo();
    }

    // PUI-Objects
    PUIFrame f;
    final PUICanvas canvas;
    final PUIMatrix matrix;
    final PUIText darkModeButton;
    PUIList list;
    final PUICheckBox cb;
    PUIRotaryControl rc;
    final PUISlider slider;
    final PUISlider slider2;

    final String frameTitle;

    PSerialConnection usb;
    PInstance p;

    private boolean isDarkMode = true;
    private Color[] otherMode = {Color.white, Color.BLACK, Color.darkGray, Color.lightGray};

    public Demo() {

        try {
            p = new PInstance(8123, () -> {
                System.out.println("Focus that thing! Someone tried to open this exact program once again");
                f.setVisible(true);
                f.setState(JFrame.NORMAL);
                f.toFront();
                f.requestFocus();
            });
        } catch (IOException e) {
            System.out.println("Already runs");
            JOptionPane.showMessageDialog(null, "Port 8123 already taken by another Process", "Instance already running", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }

        frameTitle = "JPL-Demo - " + PSystem.getUserName() + "'s " + PSystem.getOSType() + "-System from " + PSystem.getUserDisplayLocation();

        // initialize frame before creating Elements
        f = new PUIFrame(frameTitle, 600, 600);

        f.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_U) {
                    if (usb != null)
                        usb.disconnect();

                    int index = f.getUserSelection("Select USB", PSerialConnection.getSerialPorts());
                    usb = new PSerialConnection(PSerialConnection.getSerialPorts()[index]);
                    usb.connect();
                    usb.addListener(line -> System.out.println("USB: " + line));
                }
            }
        });

        // Drawing a rectangle in the background
        canvas = new PUICanvas(f, (g, x, y, w, h) -> {
            g.setColor(new Color(100, 100, 100));
            g.fillRoundRect(40, 40, w - 80, h - 80, 20, 20);
        }, -1);

        // Scaling Matrix
        matrix = new PUIMatrix(f, 4, 8);
        // generate Elements fot the Matrix to scale them
        setMatrixElements(false);


        darkModeButton = new PUIText(f, "DARK", 2);
        darkModeButton.addActionListener(that -> {
            if (isDarkMode) {

                //swapping colors
                Color[] temp = otherMode;
                otherMode = PUIElement.default_colors;
                PUIElement.default_colors = temp;

                list.showSlider(false);

                isDarkMode = false;
                darkModeButton.setText("LIGHT");
            } else {

                //swapping colors
                Color[] temp = otherMode;
                otherMode = PUIElement.default_colors;
                PUIElement.default_colors = temp;

                list.showSlider(true);

                isDarkMode = true;
                darkModeButton.setText("DARK");
            }
        });
        // if set to false: when pressed the EventChain doesn't stop => elements on layers behind this Button can be triggered as well
        darkModeButton.doBlockRaycast(false);

        list = new PUIList(f);

        cb = new PUICheckBox(f);
        cb.addActionListener(that -> {
            if (list.getAlignment() == ElementAlignment.VERTICAL) {
                list.setAlignment(ElementAlignment.HORIZONTAL);

                setMatrixElements(true);

                rc.setEnabled(false);
            } else {
                list.setAlignment(ElementAlignment.VERTICAL);

                setMatrixElements(false);

                rc.setEnabled(true);
            }
        });

        rc = new PUIRotaryControl(f, 1);
        rc.addValueUpdateAction(that -> {
            // RotaryControl changes spacing between elements in PUIScrollPanel & PUIMatrix
            int space = (int) (rc.getValue() * 7);
            list.setElementSpacing(space, space, space * 2, space * 2);
            matrix.setElementSpacing(space, space, space, space);
        });

        slider = new PUISlider(f);
        slider.setValue(0.5f);
        slider.setAlignment(ElementAlignment.HORIZONTAL);
        slider.addValueUpdateAction(that -> rc.setValueLength(slider.getValue()));

        slider2 = new PUISlider(f);
        slider2.setAlignment(ElementAlignment.HORIZONTAL);
        slider2.addValueUpdateAction(that -> rc.setValueThickness((int) (360 * slider2.getValue())));

        // add test-Buttons for scrollPanel
        for (int i = 1; i <= 10; i++) {
            PUIText t = new PUIText(f, "" + i);
            t.addActionListener(that -> {

                // automatically centers clicked Element in the UI
                list.center(that);

                that.sendUserInfo("You clicked List-Object: " + ((PUIText) that).getText());
            });
            list.addElement(t);
        }
        // comment out if the size of the element inside the panel should be further limited
//        sp.setElementSpacing(6,0,3,3);

        // prevent different colors when hovering/pressing
        for (PUIElement e : PUIElement.registeredElements) {
            e.doPaintOverOnHover(false);
            e.doPaintOverOnPress(false);
        }

        // initialize updateMethod
        f.setUpdateElements((w, h) -> {

            // Element-Positions can also be defined relative by using width & height variables

            cb.setBounds(w - 150, 50, 100, 100);// relative
            rc.setBounds(w - 150, 200, 100, 100);// relative
            list.setBounds(50, h - 200, 300, 150); // relative

            matrix.setBounds(390, 340, w - 440, h - 390);// relative
        });

        // Set Position of other non-relative Elements
        darkModeButton.setBounds(50, 50, 300, 100);
        slider.setBounds(50, 200, 300, 50);
        slider2.setBounds(50, 251, 300, 50);

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
                    e.addActionListener(that -> that.getFrame().setTitle(((PUIText) that).getText()));
                    matrix.setElement(e, i, j);
                }
        else
            for (int i = 0; i < 4; i++)
                for (int j = 0; j < 8; j++) {
                    PUIElement e = new PUIText(f, i + "," + j);
                    e.addActionListener(that -> that.getFrame().setTitle(((PUIText) that).getText()));
                    matrix.setElement(e, i, j);
                }

        f.updateElements();
    }

}
