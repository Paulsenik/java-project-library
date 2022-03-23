package ooo.paulsen.ui;

import ooo.paulsen.ui.core.PUIAction;
import ooo.paulsen.ui.core.PUIFrame;

import java.awt.*;

public class PUICheckBox extends PUIElement {

    public boolean activated = false;

    public PUICheckBox(PUIFrame l, int layer) {
        super(l);
        addActionListener(new PUIAction() {
            @Override
            public void run(PUIElement that) {
                activated = !activated;
            }
        });
        setLayer(layer);
    }

    public PUICheckBox(PUIFrame l) {
        super(l);
        addActionListener(new PUIAction() {
            @Override
            public void run(PUIElement that) {
                activated = !activated;
            }
        });
    }

    @Override
    public void draw(Graphics2D g) {
        super.draw(g);

        if (!activated)
            return;

        if (darkUIMode)
            g.setColor(darkSelected);
        else
            g.setColor(Color.green);
        g.fillOval(x + w / 10, y + h / 10, w - w / 5, h - h / 5);
    }

}
