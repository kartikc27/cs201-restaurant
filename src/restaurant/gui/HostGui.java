package restaurant.gui;


import restaurant.CustomerAgent;
import restaurant.HostAgent;
import restaurant.WaiterAgent;

import java.awt.*;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;

public class HostGui implements Gui {

    private HostAgent agent = null;

    private int xPos = 360, yPos = 100;//default waiter position
            

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
