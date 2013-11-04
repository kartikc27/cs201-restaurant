package restaurant.gui;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import restaurant.gui.WaiterGui;
import restaurant.gui.WaiterGui.Location;

public class AnimationPanel extends JPanel implements ActionListener {

	private Image bufferImage;
	private Dimension bufferSize;
	private int frameDisplay = 2;

	private List<Gui> guis = Collections.synchronizedList(new ArrayList<Gui>());

	public AnimationPanel() {
		setBackground(Color.WHITE);
		
		setVisible(true);

		bufferSize = this.getSize();

		Timer timer = new Timer(frameDisplay, this );
		timer.start();

	}

	public void actionPerformed(ActionEvent e) {
		repaint();  //Will have paintComponent called
	}

	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		Color tableColor = new Color(149, 165, 166);
		Color grillColor = new Color(241, 196, 15);
		Color platingColor = new Color(26, 188, 156);
		Color fridgeColor = new Color(155, 89, 182);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		//Clear the screen by painting a rectangle the size of the frame
		g2.setColor(getBackground());
		g2.fillRect(0, 0, screenSize.width/2, screenSize.height );
		int n = 150;
		for (int i = 0; i < 3; i++) {
			g2.setColor(tableColor);
			g2.fillRect(n, 550, 75, 75);
			n += 200;
		}
		
		n = 510;
		for (int i = 0; i < 3; i++) {
			g2.setColor(platingColor);
			g2.fillRect(n, 200, 50, 50);
			n += 55;
		}
		
		n = 510;
		for (int i = 0; i < 3; i++) {
			g2.setColor(grillColor);
			g2.fillRect(n, 50, 50, 50);
			n += 55;
		}
		
		g2.setColor(fridgeColor);
		g2.fillRect(370,50, 100, 200);

		synchronized(guis){
			for(Gui gui : guis) {
				if (gui.isPresent()) {
					gui.updatePosition();
				}
			}
		}
		synchronized(guis){
			for(Gui gui : guis) {
				if (gui.isPresent()) {
					gui.draw(g2);
				}
			}
		}
	}

	public void addGui(CustomerGui gui) {
		guis.add(gui);
	}

	public void removeGui(FoodGui gui) {
		guis.remove(gui);
	}

	public void addGui(HostGui gui) {
		guis.add(gui);
	}

	public void addGui(WaiterGui gui) {
		guis.add(gui);
	}

	public void addGui(CookGui gui) {
		guis.add(gui);
	}

	public void addGui(FoodGui gui) {
		guis.add(gui);
	}

	public void addGui(CookFoodGui gui) {
		
		guis.add(gui);
	}


}
