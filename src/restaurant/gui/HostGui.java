package restaurant.gui;


import restaurant.CustomerAgent;
import restaurant.HostAgent;

import java.awt.*;

public class HostGui implements Gui {

    private HostAgent agent = null;

    private int xPos = -20, yPos = -20;//default waiter position
    private int xDestination = -20, yDestination = -20;//default start position

    public static final int xTable1 = 200;
    public static final int yTable1 = 250;
    
    public static final int xTable2 = 350;
    public static final int yTable2 = 250;
    
    public static final int xTable3 = 500;
    public static final int yTable3 = 250;
    
    private int tableNumber = 0;
    
    public boolean headingBack = false;
    

    public HostGui(HostAgent agent) {
        this.agent = agent;
    }

    public void updatePosition() {
    	
    	if (tableNumber == 1){
    		
    		if (xPos < xDestination)
                xPos++;
            else if (xPos > xDestination)
                xPos--;

            if (yPos < yDestination)
                yPos++;
            else if (yPos > yDestination)
                yPos--;
            
            
            if (xPos == xDestination && yPos == yDestination
            		& (xDestination == xTable1 + 20) & (yDestination == yTable1 - 20)) {
               agent.msgAtTable();
            }
            
    	}
    	if (tableNumber == 2){
    		
    		if (xPos < xDestination)
                xPos+=2;
            else if (xPos > xDestination)
                xPos--;

            if (yPos < yDestination)
                yPos++;
            else if (yPos > yDestination)
                yPos--;
            
            
            if (xPos == xDestination && yPos == yDestination
            		& (xDestination == xTable2 + 20) & (yDestination == yTable2 - 20)) {
               agent.msgAtTable();
            }
            
    	}
    	
    	if (tableNumber == 3){
    		
    		if (xPos < xDestination)
                xPos+=2.7;
            else if (xPos > xDestination)
                xPos--;

            if (yPos < yDestination)
                yPos++;
            else if (yPos > yDestination)
                yPos--;
            
            
            if (xPos == xDestination && yPos == yDestination
            		& (xDestination == xTable3 + 20) & (yDestination == yTable3 - 20)) {
               agent.msgAtTable();
            }
            
    	}


        /*if (xPos < xDestination)
            xPos++;
        else if (xPos > xDestination)
            xPos--;

        if (yPos < yDestination)
            yPos++;
        else if (yPos > yDestination)
            yPos--;*/
        
        if ((xPos < 0) && (yPos < 0))
        {
        	if (headingBack)
        	{
        		headingBack = false;
        		xPos = -20;
        		yPos = -20;
        	}
        }
        	
       
        
        /*if (tableNumber == 1)
    	{
        	if (xPos == xDestination && yPos == yDestination
            		& (xDestination == xTable1 + 20) & (yDestination == yTable1 - 20)) {
               agent.msgAtTable();
            }
    	}
    	else if (tableNumber == 2)
    	{
    		if (xPos == xDestination && yPos == yDestination
            		& (xDestination == xTable2 + 20) & (yDestination == yTable2 - 20)) {
               agent.msgAtTable();
            }	
    	}
    	else if (tableNumber == 3)
    	{
    		if (xPos == xDestination && yPos == yDestination
            		& (xDestination == xTable3 + 20) & (yDestination == yTable3 - 20)) {
               agent.msgAtTable();
            }
    	}*/

        
        
        
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.MAGENTA);
        g.fillRect(xPos, yPos, 20, 20);
    }

    public boolean isPresent() {
        return true;
    }

    public void DoBringToTable(CustomerAgent customer, int tNum) {
    	tableNumber = tNum;
    	if (headingBack == false)
    	{
    	if (tableNumber == 1)
    	{
    		xDestination = xTable1 + 20;
            yDestination = yTable1 - 20;
    	}
    	else if (tableNumber == 2)
    	{
    		xDestination = xTable2 + 20;
            yDestination = yTable2 - 20;
    	}
    	else if (tableNumber == 3)
    	{
    		xDestination = xTable3 + 20;
            yDestination = yTable3 - 20;
    	}
    	}
    }

    public void DoLeaveCustomer() {
    	headingBack = true;
        xDestination = -20;
        yDestination = -20;
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
}
