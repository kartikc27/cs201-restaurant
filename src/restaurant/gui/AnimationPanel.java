package restaurant.gui;

// ADD THE TABLE LOCATIONS HERE. HOST GUI GETS LOCATIONS FROM THE ANIMATION PANEL. MAINTAIN LIST OF TABLES HERE

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

import restaurant.gui.WaiterGui;

public class AnimationPanel extends JPanel implements ActionListener {

   
   
    private Image bufferImage;
    private Dimension bufferSize;
    private int frameDisplay = 10;

    private List<Gui> guis = new ArrayList<Gui>();

    public AnimationPanel() {
    	setBackground(Color.WHITE);
    	//setSize(WINDOWX, WINDOWY);
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
        Color tableColor = new Color(243, 156, 18);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    	
        //Clear the screen by painting a rectangle the size of the frame
        g2.setColor(getBackground());
        g2.fillRect(0, 0, screenSize.width/2, screenSize.height );
        
        for (int i = 0; i < WaiterGui.locations.size(); i++) {
        	g2.setColor(tableColor);
            g2.fillRect(WaiterGui.locations.get(i).getX(), WaiterGui.locations.get(i).getY(), 75, 75);
        }
        
       
        for(Gui gui : guis) {
            if (gui.isPresent()) {
                gui.updatePosition();
            }
        }

        for(Gui gui : guis) {
            if (gui.isPresent()) {
                gui.draw(g2);
            }
        }
    }

    public void addGui(CustomerGui gui) {
        guis.add(gui);
    }

    public void addGui(HostGui gui) {
        guis.add(gui);
    }
    
    public void addGui(WaiterGui gui) {
    	guis.add(gui);
    }
    
    //public void addTable
    // pass table location information to the HostGui?
}
