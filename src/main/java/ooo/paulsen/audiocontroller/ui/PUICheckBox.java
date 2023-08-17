package ooo.paulsen.audiocontroller.ui;

import ooo.paulsen.audiocontroller.ui.core.PUIFrame;

import java.awt.*;

public class PUICheckBox extends PUIElement {

    public boolean activated = false;

    public PUICheckBox(PUIFrame l, int layer) {
        super(l);
        addActionListener(that -> activated = !activated);
        setLayer(layer);
    }

    public PUICheckBox(PUIFrame l) {
        super(l);
        addActionListener(that -> activated = !activated);
    }

    @Override
    public void draw(Graphics2D g) {
        super.draw(g);

        if (!activated)
            return;

        g.setColor(getTextColor());
        g.fillRoundRect(x + w / 10, y + h / 10, w - w / 5, h - h / 5, arcWidth, arcHeight);
    }

}
