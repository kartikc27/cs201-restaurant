package restaurant.gui;

//Waiter GUI agent has a location map. Host tells Waiter agent which table to go to.
//Waiter GUI agent then looks at location map, finds the table number and finds the coordinates.
//Waiter GUI agent then tells the Customer GUI agent where to go


import restaurant.CustomerAgent;
import restaurant.WaiterAgent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

public class WaiterGui implements Gui {
	
	class Location {
		public Location (int x, int y) {
			this.setX(x);
			this.y = y;
		}
		public int getX() {
			return x;
		}
		public void setX(int x) {
			this.x = x;
		}
		public int getY() {
			return y;
		}
		public void setY(int y) {
			this.y = y;
		}
		private int x;
		private int y;
	}
	
	public static List<Location> locations = new ArrayList<Location>();

    private WaiterAgent agent = null;
    

    private int xPos = -30, yPos = -30;//default waiter position
    private int xDestination = -30, yDestination = -30;//default start position
    
    private int tableNumber = 0;
    public boolean headingBack = false;
    

    public WaiterGui(WaiterAgent agent) {
        this.agent = agent;
        int n = 150;
        for (int i = 0; i < 3; i++) {
        	locations.add(new Location(n, 550));
        	n += 200;
        }
    }

    public void updatePosition() {
    	
    	if (xPos < xDestination)
    		xPos++;
    	else if (xPos > xDestination)
    		xPos--;
    	
    	if (yPos < yDestination)
            yPos++;
        else if (yPos > yDestination)
            yPos--;
    	
    	// WAITER AGENT IS GOING TO SLEEP. SINCE THE SEMAPHORE IS NOT RELEASED WHEN IT GOES TO TAKE ORDER (YPOS IS DIFF)
    	if (tableNumber > 0) {
    		if (xPos == xDestination && yPos == yDestination
        		& (xDestination == (locations.get(tableNumber - 1).getX() + 20)) & (yDestination == (locations.get(tableNumber - 1).y - 20))) {
    				agent.msgAtTable();
    		}		
        }
    	
    	if (tableNumber > 0) {
    		if (xPos == xDestination && yPos == yDestination
        		& (xDestination == (locations.get(tableNumber - 1).getX() + 20)) & (yDestination == (locations.get(tableNumber - 1).y - 70))) {
    				agent.msgAtTable();
    		}		
        }
    	
        
        if ((xPos < -5) || (yPos < -5))
        {
        	if (headingBack)
        	{
        		agent.leftCustomer.release();
        		headingBack = false;
        		xPos = -30;
        		yPos = -30;
        	}
        }    
    }

    public void draw(Graphics2D g) {
    	Color waiterColor = new Color(41, 128, 185);
        g.setColor(waiterColor);
        g.fillRect(xPos, yPos, 30, 30);
    }

    public boolean isPresent() {
        return true;
    }

    public void DoBringToTable(CustomerGui CustGui, int tNum) {
    	tableNumber = tNum;
    	xDestination = locations.get(tableNumber-1).getX() + 20;
    	yDestination = locations.get(tableNumber-1).y - 20;
    	CustGui.setDestination(xDestination, yDestination);
    }
    
    public void DoGoToTable(CustomerGui CustGui, int tNum) {
    	tableNumber = tNum;
    	xDestination = locations.get(tableNumber-1).getX() + 20;
    	yDestination = locations.get(tableNumber-1).y - 70;
    	
    }

    public void DoLeaveCustomer() {
    	headingBack = true;
        xDestination = -30;
        yDestination = -30;
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }

}
