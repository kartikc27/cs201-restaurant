package restaurant.gui;

//Waiter GUI agent has a location map. Host tells Waiter agent which table to go to.
//Waiter GUI agent then looks at location map, finds the table number and finds the coordinates.
//Waiter GUI agent then tells the Customer GUI agent where to go


import restaurant.CustomerAgent;
import restaurant.WaiterAgent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

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

	protected WaiterAgent agent = null;
	private FoodGui food = null;
	


	private int xPos = -30, yPos = -30;//default waiter position
	int xDestination = -30;//default start position

	int yDestination = -30;

	private int tableNumber = 0;
	public boolean headingBack = false;
	private AnimationPanel animationPanel = null;


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
				agent.msgLeftCustomer();
				headingBack = false;
				xPos = -30;
				yPos = -30;
			}
		}    

		if ((xPos == 540) && (yPos == 140) && (xDestination == 540) && (yDestination == 140)){ //hack
			agent.msgAtCook();
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
		if ((xPos != -30) && (yPos != -30)) {
			DoLeaveCustomer();
			try {
				agent.leftCustomer.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println ("left the customer, now time to take order");
		}
		tableNumber = tNum;
		xDestination = locations.get(tableNumber-1).getX() + 20;
		yDestination = locations.get(tableNumber-1).y - 20;
		CustGui.setDestination(xDestination, yDestination);
	}

	public void DoGoToTable(int tNum) {
		tableNumber = tNum;
		xDestination = locations.get(tableNumber-1).getX() + 20;
		yDestination = locations.get(tableNumber-1).getY() - 70;

	}
	public void DoGoToCook()
	{
		xDestination = 540; // hack
		yDestination = 140; // hack
	}

	public void DoLeaveCustomer() {
		headingBack = true;
		xDestination = -30;
		yDestination = -30;
	}
	
	public void procureFood(String choice, int t) {
		food = new FoodGui(this, choice, false, xPos, yPos, t);
		animationPanel.addGui(food);
		food.moveWithWaiter();
	}

	public int getXPos() {
		return xPos;
	}

	public int getYPos() {
		return yPos;
	}

	public void setAnimationPanel(AnimationPanel ap) {
		animationPanel = ap;
	}

	public void DoDeliverFood(int t, String choice, CustomerGui custGui) {
		animationPanel.removeGui(food);
		food = new FoodGui(this, choice, true, xPos, yPos, t);
		animationPanel.addGui(food);
		food.moveToTable();
		custGui.TakeFood(food);
	}

}
