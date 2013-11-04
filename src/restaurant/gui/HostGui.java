package restaurant.gui;


import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.ImageIcon;

import restaurant.HostAgent;

public class HostGui implements Gui {

    private HostAgent agent = null;

    private int xPos = 365, yPos = 800;//default waiter position
            

    public HostGui(HostAgent agent) {
        this.agent = agent;
    }

    public void draw(Graphics2D g) {
    	Image icon = new ImageIcon("../restaurant_chillaka/res/Rami.png").getImage();
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
