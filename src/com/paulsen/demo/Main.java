package com.paulsen.demo;

import java.awt.Component;
import java.awt.Graphics;

import com.paulsen.ui.*;
import com.paulsen.ui.PUIElement.ElementAlignment;

public class Main {

	public static void main(String[] args) {
		new Main();
	}

	PUIFrame f;

	PUIText darkmodeButton;
	PUICheckBox cb;
	PUIRotaryControl rc;
	PUISlider slider;
	PUIScrollPanel sp;

	public Main() {
		// initialize variables before using them in update/paint
		f = new PUIFrame("Project-Library Demo", 600, 600, new PUIInitializable() {
			@Override
			public void initUI(Component c) {

				darkmodeButton = new PUIText(c, "LIGHT");
				darkmodeButton.addActionListener(new PUIAction() {
					@Override
					public void run(PUIElement that) {
						PUIElement.darkUIMode = !PUIElement.darkUIMode;
						if (PUIElement.darkUIMode) {
							darkmodeButton.setText("DARK");
						} else {
							darkmodeButton.setText("LIGHT");
						}
						f.updateElements();
					}
				});

				Runnable update = new Runnable() {
					@Override
					public void run() {
						f.updateElements();
					}
				};

				cb = new PUICheckBox(c);
				cb.addActionListener(new PUIAction() {
					@Override
					public void run(PUIElement that) {
						f.updateElements();
					}
				});

				rc = new PUIRotaryControl(c);
				rc.addValueUpdateAction(update);

				slider = new PUISlider(c);
				slider.addValueUpdateAction(update);
				slider.setAlignment(ElementAlignment.HORIZONTAL);

				sp = new PUIScrollPanel(c);
				sp.addValueUpdateAction(update);

				// add test-Buttons for scrollpanel
				for (int i = 1; i <= 10; i++)
					sp.addElement(new PUIText(c, "" + i));

				// prevent different colors when hovering/pressing
				for (PUIElement e : PUIElement.registeredElements) {
					e.doPaintOverOnHover(false);
					e.doPaintOverOnPress(false);
				}
			}
		});
		f.setDraw(new PUIPaintable() { // graphics display on Frame
			@Override
			public void paint(Graphics g, int x, int y, int w, int h) {
				darkmodeButton.draw(g);
				cb.draw(g);
				rc.draw(g);
				slider.draw(g);
				sp.draw(g);
			}
		});
		f.setUpdateElements(new PUIUpdatable() { // initialize updateMethod
			@Override
			public void update(int w, int h) {

				/*
				 * Element-Positions can also be defined relative by using width & height
				 * variables
				 */

				darkmodeButton.setBounds(50, 50, 300, 100);
				cb.setBounds(w - 150, 50, 100, 100);// relative
				rc.setBounds(w - 150, 200, 100, 100);// relative
				slider.setBounds(50, 200, 300, 100);
				sp.setBounds(50, h - 200, 300, 150); // relative
			}
		});
		f.updateElements();
	}

}
