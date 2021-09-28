package com.paulsen.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

public class PUICheckBox extends PUIElement {

	public boolean activated = false;

	public PUICheckBox(Component l) {
		super(l);
		addActionListener(new PUIAction() {
			@Override
			public void run(PUIElement that) {
				activated = !activated;
			}
		});
	}

	@Override
	public void draw(Graphics g) {
		super.draw(g);
		
		if (!activated)
			return;
		
		if (PUIElement.darkUIMode)
			g.setColor(PUIElement.darkSelected);
		else
			g.setColor(Color.green);
		g.fillOval(x + w / 10, y + h / 10, w - w / 5, h - h / 5);
	}

}
