package restaurant.gui;

import restaurant.CustomerAgent;
import restaurant.HostAgent;

import java.awt.*;

public class CustomerGui implements Gui{

	private CustomerAgent agent = null;
	private boolean isPresent = false;
	private boolean isHungry = false;
	private FoodGui food = null;
	private AnimationPanel animationPanel = null;


	//private HostAgent host;
	RestaurantGui gui;

	private int xPos, yPos;
	private int xDestination, yDestination;
	private int tempDestinationX, tempDestinationY;
	private enum Command {noCommand, GoToSeat, LeaveRestaurant};
	private Command command=Command.noCommand;
	private int home = 0;

	private static int tableNumber = 0;

	public CustomerGui(CustomerAgent c, RestaurantGui gui, int custnum){ //HostAgent m) {
		agent = c;
		xPos = home+(custnum*30);
		yPos = home;
		xDestination = xPos;
		yDestination = home;

		this.gui = gui;
	}

	public void setDestination(int x, int y) {
		xDestination = x;
		yDestination = y;
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

		if (xPos == xDestination && yPos == yDestination) {
			if (command==Command.GoToSeat) agent.msgAnimationFinishedGoToSeat();
			else if (command==Command.LeaveRestaurant) {
				agent.msgAnimationFinishedLeaveRestaurant();
				//System.out.println("about to call gui.setCustomerEnabled(agent);");
				isHungry = false;
				gui.setCustomerEnabled(agent);
			}
			command=Command.noCommand;
		}
	}

	public void draw(Graphics2D g) {
		Color customerColor = new Color (46, 204, 113);
		g.setColor(customerColor);
		g.fillRect(xPos, yPos, 30, 30);
	}

	public boolean isPresent() {
		return isPresent;
	}
	public void setHungry() {
		isHungry = true;
		agent.msgGotHungry();
		setPresent(true);
	}
	public boolean isHungry() {
		return isHungry;
	}

	public void setPresent(boolean p) {
		isPresent = p;
	}

	public void DoGoToSeat() {
		command = Command.GoToSeat;
		xDestination = tempDestinationX;
		yDestination = tempDestinationY;
	}

	public void DoExitRestaurant() {
		xDestination = -70;
		yDestination = -70;
		command = Command.LeaveRestaurant;
	}

	public void setAnimationPanel(AnimationPanel ap) {
		animationPanel = ap;
	}

	public void setSuggestedDestination(int x, int y) {
		tempDestinationX = x;
		tempDestinationY = y;
		
	}


}
