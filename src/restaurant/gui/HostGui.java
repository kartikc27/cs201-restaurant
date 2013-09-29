package restaurant.gui;


import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.ImageIcon;

import restaurant.HostAgent;

public class HostGui implements Gui {

    private HostAgent agent = null;

    private int xPos = 365, yPos = 100;//default waiter position
            

    public HostGui(HostAgent agent) {
        this.agent = agent;
    }
    
    // retrieve table location information from the animation panel
    // pass the table location information to the Customer GUI (the customer cannot access the info, but the GUI can)
    // pass the table location information to the WAITER Gui


    public void draw(Graphics2D g) {
    	Image icon = new ImageIcon("res/Rami.png").getImage();
        g.drawImage(icon, xPos, yPos, null);
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
