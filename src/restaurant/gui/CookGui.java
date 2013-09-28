package restaurant.gui;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.ImageIcon;

import restaurant.CookAgent;

public class CookGui implements Gui {

    private CookAgent agent = null;
    
    private Graphics2D g = null;

    private int xPos = 570, yPos = 110; // cook position
            

    public CookGui(CookAgent agent) {
		this.agent = agent;
	}

    
    // retrieve table location information from the animation panel
    // pass the table location information to the Customer GUI (the customer cannot access the info, but the GUI can)
    // pass the table location information to the WAITER Gui


    public void draw(Graphics2D g) {
    	this.g = g;
    	Color customerColor = new Color (52, 73, 94);
		g.setColor(customerColor);
		g.fillRect(xPos, yPos, 30, 30);
    }

    public boolean isPresent() {
        return true;
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }


	@Override
	public void updatePosition() {
		// TODO Auto-generated method stub
		
	}
}
