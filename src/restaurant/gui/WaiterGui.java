package restaurant.gui;

//Waiter GUI agent has a location map. Host tells Waiter agent which table to go to.
//Waiter GUI agent then looks at location map, finds the table number and finds the coordinates.
//Waiter GUI agent then tells the Customer GUI agent where to go


import restaurant.CustomerAgent;
import restaurant.WaiterAgent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class WaiterGui implements Gui {
	
	private class Location {
		public Location (int x, int y) {
			this.x = x;
			this.y = y;
		}
		private int x;
		private int y;
	}
	
	public List<Location> locations = new ArrayList<Location>();

    private WaiterAgent agent = null;

    private int xPos = 40, yPos = 40;//default waiter position
    private int xDestination = 40, yDestination = 40;//default start position
    
    private int tableNumber = 0;
    public boolean headingBack = false;
    

    public WaiterGui(WaiterAgent agent) {
        this.agent = agent;
        int n = 200;
        for (int i = 0; i < 3; i++) {
        	locations.add(new Location(n, 250));
        	n += 150;
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
    	
    	if (tableNumber > 0) {
    		if (xPos == xDestination && yPos == yDestination
        		& (xDestination == (locations.get(tableNumber - 1).x + 20)) & (yDestination == (locations.get(tableNumber - 1).y - 20))) {
    				agent.msgAtTable();
    		}		
        }
    	
        
        if ((xPos < 0) && (yPos < 0))
        {
        	if (headingBack)
        	{
        		headingBack = false;
        		xPos = -20;
        		yPos = -20;
        	}
        }    
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.ORANGE);
        g.fillRect(xPos, yPos, 20, 20);
    }

    public boolean isPresent() {
        return true;
    }

    public void DoBringToTable(CustomerAgent customer, int tNum) {
    	tableNumber = tNum;
    	
    	xDestination = locations.get(tableNumber-1).x + 20;
    	yDestination = locations.get(tableNumber-1).y - 20;
    }

    public void DoLeaveCustomer() {
    	headingBack = true;
        xDestination = 40;
        yDestination = 40;
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }

}
