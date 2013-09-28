package restaurant.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
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
    
    private WaiterAgent waiter = new WaiterAgent("Kartik");
    private WaiterGui waiterGui = new WaiterGui(waiter);
    
    private CookAgent cook = new CookAgent("Sarah"); 
    private CookGui cookGui = new CookGui(cook);

    private Vector<CustomerAgent> customers = new Vector<CustomerAgent>();

    private JPanel restLabel = new JPanel();
    private ListPanel customerPanel = new ListPanel(this, "Customers");
    private JPanel group = new JPanel();
    private JButton b1;
    


   

    public RestaurantPanel(RestaurantGui gui) {
        this.gui = gui;
        host.setGui(hostGui);
        waiter.setGui(waiterGui);
        waiter.setCook(cook);
        waiter.setHost(host);
        waiterGui.setAnimationPanel(gui.animationPanel);
                
        host.addWaiter(waiter);

        gui.animationPanel.addGui(hostGui);
        host.startThread();
        
        gui.animationPanel.addGui(waiterGui);
        waiter.startThread();
        
        gui.animationPanel.addGui(cookGui);
        cook.setGui(cookGui);
        cook.startThread();

        setLayout(new BorderLayout(20, 20));
        group.setLayout(new BorderLayout(10, 10));

        group.add(customerPanel);
        TitledBorder title = BorderFactory.createTitledBorder("Customers");
        title.setTitleJustification(TitledBorder.CENTER);
        group.setBorder(title);
        
        initRestLabel();
        add(restLabel, BorderLayout.NORTH);
        add(group, BorderLayout.CENTER);
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
    		customers.add(c);
    		c.startThread();
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
		waiter.msgPause();
		isPaused = !isPaused;
		if (isPaused) {b1.setText("Unpause");}
		else {b1.setText("Pause");}
		
	}


}
