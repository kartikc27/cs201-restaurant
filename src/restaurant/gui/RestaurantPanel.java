package restaurant.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import restaurant.CashierAgent;
import restaurant.CookAgent;
import restaurant.CustomerAgent;
import restaurant.HostAgent;
import restaurant.MarketAgent;
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
	private List<MarketAgent> markets = new ArrayList<MarketAgent>();  

	private CookAgent cook = new CookAgent("Sarah"); 
	private CookGui cookGui = new CookGui(cook);
	private CashierAgent cashier = new CashierAgent("Cashier");

	private MarketAgent market1 = new MarketAgent("Market 1", 2, 10, 10, 10, cook, cashier);
	private MarketAgent market2 = new MarketAgent("Market 2", 0, 0, 0, 0, cook, cashier);
	private MarketAgent market3 = new MarketAgent("Market 3", 7, 15, 21, 11, cook, cashier);
	private Vector<CustomerAgent> customers = new Vector<CustomerAgent>();
	private Vector<WaiterAgent> waiters = new Vector<WaiterAgent>();

	int custnum = -1;
	private JPanel restLabel = new JPanel();
	private ListPanel customerPanel = new ListPanel(this, "Customers");
	private WaiterPanel waiterPanel = new WaiterPanel(this, "Waiters");
	private JPanel group = new JPanel();
	private JButton b1;
	private JButton b2;
	private int MaxRestLabelX = 1000;
	private int MaxRestLabelY = 350;

	private JPanel buttonPanel;





	public RestaurantPanel(RestaurantGui gui) {
		this.gui = gui;
		host.setGui(hostGui);


		gui.animationPanel.addGui(hostGui);
		host.startThread();

		markets.add(market1);
		markets.add(market2);
		markets.add(market3);
		cook.addMarkets(markets);

		gui.animationPanel.addGui(cookGui);
		cook.setGui(cookGui);
		cookGui.setAnimationPanel(gui.animationPanel);
		cook.startThread();

		cashier.startThread();



		for (MarketAgent m : markets) {
			m.startThread();
		}

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
		restLabel.setMaximumSize(new Dimension(MaxRestLabelX, MaxRestLabelY));
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
		b2 = new JButton("Drain");
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());

		buttonPanel.add(b1);
		buttonPanel.add(b2);
		restLabel.add(buttonPanel, BorderLayout.EAST);
		b1.addActionListener(this);
		b2.addActionListener(this);


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
		else if (type.equals("Waiters")) {
			for (int i = 0; i < waiters.size(); i++) {
				WaiterAgent temp = waiters.get(i);
				if (temp.getName() == name) {
					gui.updateInfoPanel(temp);
				}
			}
		}
	}

	/**
	 * Adds a customer or waiter to the appropriate list
	 * @param type indicates whether the person is a customer or waiter (later)
	 * @param name name of person
	 */
	public void addPerson(String type, String name) {
		
		if (type.equals("Customers")) {
			custnum++;
			if (custnum == 2)
				custnum = -1;
			CustomerAgent c = new CustomerAgent(name);	
			CustomerGui g = new CustomerGui(c, gui, custnum);
			gui.animationPanel.addGui(g);
			c.setHost(host);
			c.setCashier(cashier);
			c.setGui(g);
			g.setAnimationPanel(gui.animationPanel);
			customers.add(c);
			c.startThread();
		}

		if (type.equals("Waiters")) {
			System.out.println ("adding waiter");
			WaiterAgent w = new WaiterAgent(name);	
			WaiterGui g = new WaiterGui(w, this);
			waiters.add(w);
			w.setGui(g);
			w.setCook(cook);
			w.setHost(host);
			w.setCashier(cashier);
			host.msgWaiterReporting(w);
			g.setAnimationPanel(gui.animationPanel);
			gui.animationPanel.addGui(g);
			w.startThread();
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

	public void markBreak(String name)
	{
		for (int i = 0; i < waiters.size(); i++)
		{
			WaiterAgent temp = waiters.get(i);
			if (temp.getName() == name)
			{
				temp.getGui().setBreak();
			}
		}	
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// pause each customer, waiter, host, and cook
		if (e.getSource() == b1) {
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
		else if (e.getSource() == b2) {
			cook.drainInventory();
		}

	}


}
