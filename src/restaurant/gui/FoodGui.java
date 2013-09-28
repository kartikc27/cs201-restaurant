package restaurant.gui;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.ImageIcon;

import restaurant.CookAgent;

public class FoodGui implements Gui {

	private WaiterGui waiter = null;

	private Graphics2D g = null;

	private int xPos, yPos; 
	private int xDestination, yDestination; 
	private String food;
	private boolean delivered;
	Color foodColor = null;
	private int tableNumber;


	public FoodGui(WaiterGui waiter, String choice, boolean delivered, int x, int y, int t) {
		food = choice;
		this.waiter = waiter;
		this.delivered = delivered;
		xPos = x;
		yPos = y;
		tableNumber = t;
	}

	public void draw(Graphics2D g) {
		this.g = g;
		if (food == "Steak")
			foodColor = new Color (152, 105, 3);
		else if (food == "Pizza")
			foodColor = new Color (231, 76, 60);
		else if (food == "Salad")
			foodColor = new Color (39, 174, 96);
		else if (food == "Chicken")
			foodColor = new Color (243, 156, 18);
		g.setColor(foodColor);
		g.fillRect(xPos, yPos, 17, 17);
		g.setColor(new Color(255,255,255));
		if (!delivered)
		{
			g.drawString("?", xPos+5,  yPos+12);
		}
		else {
			if (food == "Steak")
				g.drawString("S", xPos+5,  yPos+13);
			else if (food == "Pizza")
				g.drawString("P", xPos+5,  yPos+13);
			else if (food == "Salad")
				g.drawString("Sa", xPos+1,  yPos+13);
			else if (food == "Chicken")
				g.drawString("C", xPos+2,  yPos+13);
		}
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


	public void updatePosition() {
		if (xPos < xDestination)
			xPos++;
		else if (xPos > xDestination)
			xPos--;

		if (yPos < yDestination)
			yPos++;
		else if (yPos > yDestination)
			yPos--;

		if (xPos == waiter.locations.get(tableNumber-1).getX()+20 && yPos == waiter.locations.get(tableNumber-1).getY()+20) {
			waiter.agent.msgFoodDelivered();
		}
	}
	
	public void moveWithWaiter()
	{
		xDestination = waiter.locations.get(tableNumber-1).getX() + 20;
		yDestination = waiter.locations.get(tableNumber-1).getY() - 70;
	}


	public void moveToTable() {
		xDestination = waiter.locations.get(tableNumber-1).getX()+20;
		yDestination = waiter.locations.get(tableNumber-1).getY()+20;
	}
}
