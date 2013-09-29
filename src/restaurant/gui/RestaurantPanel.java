package restaurant.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import restaurant.CookAgent;
import restaurant.CustomerAgent;
import restaurant.HostAgent;
import restaurant.WaiterAgent;

/**
 * Panel in frame that contains all the restaurant information,
 * including host, cook, waiters, and customers.
 */
public class RestaurantPanel extends JPanel implements ActionListener {

    //Host, cook, waiters and customers
    private HostAgent host = new HostAgent("Rami");
    private HostGui hostGui = new HostGui(host);
    boolean isPaused = false;
    
    private RestaurantGui gui; //reference to main gui
    
    //private WaiterAgent waiter = new WaiterAgent("Kartik");
    //private WaiterGui waiterGui = new WaiterGui(waiter);
    
    private CookAgent cook = new CookAgent("Sarah"); 
    private CookGui cookGui = new CookGui(cook);

    private Vector<CustomerAgent> customers = new Vector<CustomerAgent>();
    private Vector<WaiterAgent> waiters = new Vector<WaiterAgent>();


    private JPanel restLabel = new JPanel();
    private ListPanel customerPanel = new ListPanel(this, "Customers");
    private WaiterPanel waiterPanel = new WaiterPanel(this, "Waiters");
    private JPanel group = new JPanel();
    private JButton b1;
    


   

    public RestaurantPanel(RestaurantGui gui) {
        this.gui = gui;
        host.setGui(hostGui);
        //waiter.setGui(waiterGui);
        //waiter.setCook(cook);
        //waiter.setHost(host);
        //waiterGui.setAnimationPanel(gui.animationPanel);
                
        //host.addWaiter(waiter);

        gui.animationPanel.addGui(hostGui);
        host.startThread();
        
        //gui.animationPanel.addGui(waiterGui);
        //waiter.startThread();
        
        gui.animationPanel.addGui(cookGui);
        cook.setGui(cookGui);
        cook.startThread();

        //setLayout(new BorderLayout(20, 20));
        //group.setLayout(new BorderLayout(10, 10));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));

        group.add(customerPanel);
        TitledBorder title = BorderFactory.createTitledBorder("Customers");
        title.setTitleJustification(TitledBorder.CENTER);
        customerPanel.setBorder(title);
        group.add(Box.createRigidArea(new Dimension(0, 25)));
        group.add(waiterPanel);
        TitledBorder title2 = BorderFactory.createTitledBorder("Waiters");
        title2.setTitleJustification(TitledBorder.CENTER);
        waiterPanel.setBorder(title2);
        
        
        
        initRestLabel();
        //restLabel.setPreferredSize(new Dimension (190, 100));
        restLabel.setMaximumSize(new Dimension(1000, 350));
        add(restLabel/*, BorderLayout.NORTH*/);
        add(Box.createRigidArea(new Dimension(0, 25)));
        add(group/*, BorderLayout.CENTER*/);
    }

    /**
     * Sets up the restaurant label that includes the menu,
     * and host and cook information
     */
    private void initRestLabel() {
        JLabel label = new JLabel();
        //restLabel.setLayout(new BoxLayout((Container)restLabel, BoxLayout.Y_AXIS));
        restLabel.setLayout(new BorderLayout());
        
        label.setText(
                "<html><h3><u>Tonight's Staff</u></h3><table><tr><td>Host:</td><td>" + host.getName() + "</td></tr></table><h3><u> Menu</u></h3><table><tr><td>Steak</td><td>$15.99</td></tr><tr><td>Chicken</td><td>$10.99</td></tr><tr><td>Salad</td><td>$5.99</td></tr><tr><td>Pizza</td><td>$8.99</td></tr></table><br></html>");

        restLabel.setBorder(BorderFactory.createTitledBorder( ""));
        restLabel.add(label, BorderLayout.CENTER);
        restLabel.add(new JLabel("               "), BorderLayout.EAST);
        restLabel.add(new JLabel("               "), BorderLayout.WEST);
        
        b1 = new JButton("Pause");
        
        restLabel.add(b1, BorderLayout.EAST);
        b1.addActionListener(this);
        
       
    }

    /**
     * When a customer or waiter is clicked, this function calls
     * updatedInfoPanel() from the main gui so that person's information
     * will be shown
     *
     * @param type indicates whether the person is a customer or waiter
     * @param name name of person
     */
    public void showInfo(String type, String name) {

        if (type.equals("Customers")) {

            for (int i = 0; i < customers.size(); i++) {
                CustomerAgent temp = customers.get(i);
                if (temp.getName() == name)
                    gui.updateInfoPanel(temp);
            }
        }
    }

    /**
     * Adds a customer or waiter to the appropriate list
     *
     * @param type indicates whether the person is a customer or waiter (later)
     * @param name name of person
     */
    public void addPerson(String type, String name) {

    	if (type.equals("Customers")) {
    		CustomerAgent c = new CustomerAgent(name);	
    		CustomerGui g = new CustomerGui(c, gui);

    		gui.animationPanel.addGui(g);
    		c.setHost(host);
    		c.setGui(g);
    		g.setAnimationPanel(gui.animationPanel);
    		customers.add(c);
    		c.startThread();
    	}
    	
    	if (type.equals("Waiters")) {
    		System.out.println ("adding waiter");
    		WaiterAgent w = new WaiterAgent(name);	
    		WaiterGui g = new WaiterGui(w);
    		w.setGui(g);
    		w.setCook(cook);
    		w.setHost(host);
    		host.addWaiter(w);
    		g.setAnimationPanel(gui.animationPanel);
    		gui.animationPanel.addGui(g);
    		w.startThread();
    		
    		 //gui.animationPanel.addGui(waiterGui);
            //waiter.startThread();
    		
    		
    	
    	}
    }
    
    public void markHungry(String name)
    {
    	for (int i = 0; i < customers.size(); i++)
    	{
    		 CustomerAgent temp = customers.get(i);
    		 if (temp.getName() == name)
    		 {
    			  temp.getGui().setHungry();
    		 }
    	}	
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		// pause each customer, waiter, host, and cook
		for (CustomerAgent c : customers) {
			c.msgPause();
		}
		host.msgPause();
		for (WaiterAgent w : waiters) {
			w.msgPause();
		}
		isPaused = !isPaused;
		if (isPaused) {b1.setText("Unpause");}
		else {b1.setText("Pause");}
		
	}


}
