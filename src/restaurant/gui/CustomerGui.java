package restaurant.gui;

import restaurant.CustomerAgent;
import restaurant.HostAgent;

import java.awt.*;

public class CustomerGui implements Gui{

	private CustomerAgent agent = null;
	private boolean isPresent = false;
	private boolean isHungry = false;

	//private HostAgent host;
	RestaurantGui gui;

	private int xPos, yPos;
	private int xDestination, yDestination;
	private enum Command {noCommand, GoToSeat, LeaveRestaurant};
	private Command command=Command.noCommand;

	 public static final int xTable1 = 200;
	 public static final int yTable1 = 250;
	    
	 public static final int xTable2 = 350;
	 public static final int yTable2 = 250;
	    
	 public static final int xTable3 = 500;
	 public static final int yTable3 = 250;
	 
	 private static int tableNumber = 0;

	public CustomerGui(CustomerAgent c, RestaurantGui gui){ //HostAgent m) {
		agent = c;
		xPos = -40;
		yPos = -40;
		xDestination = -40;
		yDestination = -40;
		//maitreD = m;
		this.gui = gui;
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
            
            if (xPos == xDestination && yPos == yDestination) {
    			if (command==Command.GoToSeat) agent.msgAnimationFinishedGoToSeat();
    			else if (command==Command.LeaveRestaurant) {
    				agent.msgAnimationFinishedLeaveRestaurant();
    				System.out.println("about to call gui.setCustomerEnabled(agent);");
    				isHungry = false;
    				gui.setCustomerEnabled(agent);
    			}
    			command=Command.noCommand;
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
            
            if (xPos == xDestination && yPos == yDestination) {
    			if (command==Command.GoToSeat) agent.msgAnimationFinishedGoToSeat();
    			else if (command==Command.LeaveRestaurant) {
    				agent.msgAnimationFinishedLeaveRestaurant();
    				System.out.println("about to call gui.setCustomerEnabled(agent);");
    				isHungry = false;
    				gui.setCustomerEnabled(agent);
    			}
    			command=Command.noCommand;
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
            
            if (xPos == xDestination && yPos == yDestination) {
    			if (command==Command.GoToSeat) agent.msgAnimationFinishedGoToSeat();
    			else if (command==Command.LeaveRestaurant) {
    				agent.msgAnimationFinishedLeaveRestaurant();
    				System.out.println("about to call gui.setCustomerEnabled(agent);");
    				isHungry = false;
    				gui.setCustomerEnabled(agent);
    			}
    			command=Command.noCommand;
    		}
    	}
		
		/*if (xPos < xDestination)
			xPos++;
		else if (xPos > xDestination)
			xPos--;

		if (yPos < yDestination)
			yPos++;
		else if (yPos > yDestination)
			yPos--;

		if (xPos == xDestination && yPos == yDestination) {
			if (command==Command.GoToSeat) agent.msgAnimationFinishedGoToSeat();
			else if (command==Command.LeaveRestaurant) {
				agent.msgAnimationFinishedLeaveRestaurant();
				System.out.println("about to call gui.setCustomerEnabled(agent);");
				isHungry = false;
				gui.setCustomerEnabled(agent);
			}
			command=Command.noCommand;
		}*/
	}

	public void draw(Graphics2D g) {
		g.setColor(Color.GREEN);
		g.fillRect(xPos, yPos, 20, 20);
	}

	public boolean isPresent() {
		return isPresent;
	}
	public void setHungry() {
		isHungry = true;
		agent.gotHungry();
		setPresent(true);
	}
	public boolean isHungry() {
		return isHungry;
	}

	public void setPresent(boolean p) {
		isPresent = p;
	}

	public void DoGoToSeat(int seatnumber) {//later you will map seatnumber to table coordinates.
		tableNumber = seatnumber;
		if (tableNumber == 1)
		{
			xDestination = xTable1;
			yDestination = yTable1;
		}
		if (tableNumber == 2)
		{
			xDestination = xTable2;
			yDestination = yTable2;
		}
		if (tableNumber == 3)
		{
			xDestination = xTable3;
			yDestination = yTable3;
		}
		
		command = Command.GoToSeat;
	}

	public void DoExitRestaurant() {
		xDestination = -40;
		yDestination = -40;
		command = Command.LeaveRestaurant;
	}
}
