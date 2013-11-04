package restaurant.gui;

//Waiter GUI agent has a location map. Host tells Waiter agent which table to go to.
//Waiter GUI agent then looks at location map, finds the table number and finds the coordinates.
//Waiter GUI agent then tells the Customer GUI agent where to go


import restaurant.CustomerAgent;
import restaurant.WaiterAgent;
import restaurant.gui.CookGui.Location;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import javax.swing.ImageIcon;

public class WaiterGui implements Gui {

	public static class Location {
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
	public static List<FoodGui> foodItems = new ArrayList<FoodGui>();
	public static List<Location> plates = new ArrayList<Location>();

	protected WaiterAgent agent = null;
	private FoodGui food = null;
	private int CookX = 475;
	private int CookY = 250;
	private int plateNum = 0;



	private int xPos = -30, yPos = -30;//default waiter position
	int xDestination = -30;//default start position

	int yDestination = -30;

	private int tableNumber = 0;
	public boolean headingBack = false;
	private AnimationPanel animationPanel = null;
	Timer breakTimer = new Timer();
	private RestaurantPanel restPanel;

	public WaiterGui(WaiterAgent agent, RestaurantPanel rp) {
		restPanel = rp;
		this.agent = agent;
		int n = 150;
		for (int i = 0; i < 3; i++) {
			locations.add(new Location(n, 550));
			n += 200;
		}
		n = 510;
		for (int i = 0; i < 3; i++) {
			plates.add(new Location(n, 200));
			n += 55;
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


		if ((xPos == -30) && (yPos == -30))
		{
			if (headingBack)
			{
				agent.msgLeftCustomer();
				headingBack = false;
			}
		}    

		if (plateNum == -1) {
			if ((xPos == CookX) && (yPos == CookY) && (xDestination == CookX) && (yDestination == CookY)){ //hack
				agent.msgAtCook();
			}
		}

		if ((plateNum >= 0) && (plateNum < 3)){
			if ((xPos == plates.get(plateNum).getX()+9) && (yPos == plates.get(plateNum).getY()+40) && (xDestination == plates.get(plateNum).getX()+9) && (yDestination == plates.get(plateNum).getY()+40)){ 
				agent.msgAtPlate();
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
		CustGui.setSuggestedDestination(xDestination, yDestination);
	}

	public void DoGoToTable(int tNum) {
		tableNumber = tNum;
		xDestination = locations.get(tableNumber-1).getX() + 20;
		yDestination = locations.get(tableNumber-1).getY() - 70;

	}
	public void DoGoToCook(int plateNum)
	{	
		this.plateNum = plateNum;
		if (plateNum == -1) {
			xDestination = CookX; 
			yDestination = CookY; 
		}
		else {
			xDestination = plates.get(plateNum).getX()+9;
			yDestination = plates.get(plateNum).getY()+40;
		}

	}

	public void DoLeaveCustomer() {
		headingBack = true;
		xDestination = -30;
		yDestination = -30;
	}

	public void procureFood(String choice, int t) {
		food = new FoodGui(this, choice, false, xPos, yPos, t);
		foodItems.add(food);
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
		food.moveToTable();
	}

	public void setBreak() {
		agent.msgWantBreak();
	}
	public boolean IsOnBreak() {
		return agent.onBreak;
	}
	public void setOffBreak() {
		breakTimer.schedule(new TimerTask() {
			public void run() {
				System.out.println(agent.getName() + ": break is over");
				agent.takingBreak.release();
				agent.offBreak();
				restPanel.showInfo("Waiters", agent.getName());
			}
		},
		15000);
	}
	public void DoClearTable(int t) {
		for (FoodGui f : foodItems) {
			if (f.tableNumber == t) {
				f.visible = false;
			}
			//break;
		}
	}
	public boolean isHome() {
		if ((xPos == -30) && (yPos == -30)) {
			return true;
		}
		return false;
	}


}
