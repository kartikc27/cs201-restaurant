package restaurant.gui;


import restaurant.CustomerAgent;
import restaurant.HostAgent;
import restaurant.WaiterAgent;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class HostGui implements Gui {

    private HostAgent agent = null;

    private int xPos = 50, yPos = 50;//default waiter position
    //private int xDestination = 50, yDestination = 50;//default start position
    
    // Map out each table with a class Location (check if there is a pre-existing location class)
    
    /*public static final int xTable1 = 200;
    public static final int yTable1 = 250;
    
    public static final int xTable2 = 350;
    public static final int yTable2 = 250;
    
    public static final int xTable3 = 500;
    public static final int yTable3 = 250;*/
    
    
            

    public HostGui(HostAgent agent) {
        this.agent = agent;
    }
    
    // retrieve table location information from the animation panel
    // pass the table location information to the Customer GUI (the customer cannot access the info, but the GUI can)
    // pass the table location information to the WAITER Gui


    public void draw(Graphics2D g) {
        g.setColor(Color.MAGENTA);
        g.fillRect(xPos, yPos, 20, 20);
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
