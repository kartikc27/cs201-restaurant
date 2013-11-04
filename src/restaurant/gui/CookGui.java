package restaurant.gui;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.ImageIcon;

import restaurant.CookAgent;
import restaurant.gui.WaiterGui.Location;

public class CookGui implements Gui {

	private CookAgent agent = null;
	//public CookFoodGui food = null;


	public static List<CookFoodGui> foods = Collections.synchronizedList(new ArrayList<CookFoodGui>());


	private Graphics2D g = null;

	private int xPos = 575, yPos = 135; // cook position
	int xDestination = 575;//default start position
	int yDestination = 135;

	private AnimationPanel animationPanel = null;



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

	public static List<Location> grills = new ArrayList<Location>();
	public static List<Location> plates = new ArrayList<Location>();

	public CookGui(CookAgent agent) {
		this.agent = agent;
		int n = 510;
		for (int i = 0; i < 3; i++) {
			grills.add(new Location(n, 50));
			n += 55;
		}
		n = 510;
		for (int i = 0; i < 3; i++) {
			plates.add(new Location(n, 200));
			n += 55;
		}
	}




	public void draw(Graphics2D g) {
		this.g = g;
		Color customerColor = new Color (52, 73, 94);
		g.setColor(customerColor);
		g.fillRect(xPos, yPos, 30, 30);
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

	public void DoGoToFridge() {
		xDestination = 460;
		yDestination = 135;
	}

	public void procureFood(String choice, int orderNumber) {
		CookFoodGui food = new CookFoodGui(this, choice, xPos, yPos, orderNumber);
		foods.add(food);
		animationPanel.addGui(food);
	}

	public void DoGoToGrill(int grillNum, int orderNum) {
		xDestination = grills.get(0).x + 9;
		yDestination = 100;
		synchronized(foods) {
			for (CookFoodGui f : foods) {
				if (f.orderNumber == orderNum) {
					f.moveWithCookToGrill(grillNum);
				}
			}
		}
	}

	public void DoGoToGrill2(int grillNum) {
		xDestination = grills.get(grillNum).x + 9;
		yDestination = 100;
	}

	public void DoGoToPlate(int plateNum, int orderNum) {
		xDestination = plates.get(plateNum).x + 9;
		yDestination = 170;
		synchronized (foods){
			for (CookFoodGui f : foods) {
				if (f.orderNumber == orderNum) {
					f.moveWithCookToPlate(plateNum);
				}
			}
		}
	}

	public void DoGoHome() {
		xDestination = 575;
		yDestination = 135;
	}

	public void removeFood(int orderNum) {
		synchronized(foods){
			for (CookFoodGui f : foods) {
				if (f.orderNumber == orderNum) {
					f.visible = false;
				}
			}
		}
	}




	@Override
	public void updatePosition() {
		if (xPos < xDestination)
			xPos++;
		else if (xPos > xDestination)
			xPos--;

		if (yPos < yDestination)
			yPos++;
		else if (yPos > yDestination)
			yPos--;

		if ((xPos == xDestination) && (yPos == yDestination) && (xDestination == 460) && (yDestination == 135)) {
			agent.msgAtFridge();
		}

		if ((xPos == xDestination) && (yPos == yDestination) && (xDestination == grills.get(0).getX() + 9) && (yDestination == 100)) {
			agent.msgAtGrill();
		}

		if ((xPos == xDestination) && (yPos == yDestination) && (xDestination == grills.get(1).getX() + 9) && (yDestination == 100)) {
			agent.msgAtGrill();
		}

		if ((xPos == xDestination) && (yPos == yDestination) && (xDestination == grills.get(2).getX() + 9) && (yDestination == 100)) {
			agent.msgAtGrill();
		}

		if ((xPos == xDestination) && (yPos == yDestination) && (xDestination == plates.get(0).getX() + 9) && (yDestination == 170)) {
			agent.msgAtPlate();
		}
		if ((xPos == xDestination) && (yPos == yDestination) && (xDestination == plates.get(1).getX() + 9) && (yDestination == 170)) {
			agent.msgAtPlate();
		}
		if ((xPos == xDestination) && (yPos == yDestination) && (xDestination == plates.get(2).getX() + 9) && (yDestination == 170)) {
			agent.msgAtPlate();
		}

		if ((xPos == xDestination) && (yPos == yDestination) && (xDestination == 575) && (yDestination == 135)) {
			agent.msgAtHome();
		}
	}

	public void setAnimationPanel(AnimationPanel ap) {
		animationPanel = ap;
	}
}
