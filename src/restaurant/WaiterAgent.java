package restaurant;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import restaurant.CookAgent.CookOrder;
import restaurant.gui.HostGui;
import restaurant.gui.WaiterGui;
import agent.Agent;

/**
 * Restaurant Host Agent
 */


public class WaiterAgent extends Agent {
	
	private String name;
	public Semaphore atTable = new Semaphore(0, true);
	
	enum CustomerState{waiting, seated, readyToOrder, asked, ordered, orderGiven};
	
	private class MyCustomer {
		public MyCustomer(CustomerAgent cust, int table, CustomerState state) {
			c = cust;
			t = table;
			s = state;
			
		}
		CustomerAgent c;
		int t;
		String choice;
		CustomerState s;
	}
	
	class WaiterOrder {
		
		String choice;
		int table;
		
		public WaiterOrder(String c, int t) {
			choice = c;
			table = t;
		}
	}
	
	private List<MyCustomer> customers = new ArrayList<MyCustomer>();
	private List<WaiterOrder> readyOrders = new ArrayList<WaiterOrder>();
	
	public WaiterGui waiterGui = null;
	private CookAgent cook;
	private HostAgent host;
	
	
	
	
	public WaiterAgent(String name) {
		super();
		this.name = name;
	}
	
	public void setCook(CookAgent c) {
		cook = c;
	}
	
	public void setHost(HostAgent h) {
		host = h;
	}


	public String getName() {
		return name;
	}


	public void msgSitAtTable(CustomerAgent cust, int table) {
		customers.add(new MyCustomer(cust, table, CustomerState.waiting));
		
		System.out.println ("Customer State changed to waiting");
		
		stateChanged();
	}
	
	public void msgImReadyToOrder(CustomerAgent cust) {
		for (MyCustomer mc : customers)
		{
			System.out.println ("IM HERE");
			if (cust.getName() == mc.c.getName()) {
				mc.s = CustomerState.readyToOrder;
				System.out.println ("Ready to order");
				stateChanged();
			}
				
		}
	}
	
	public void msgHereIsMyChoice(String choice, CustomerAgent c) {
		System.out.println ("received choice");
		for (MyCustomer mc : customers)
		{
			if (c.getName() == mc.c.getName()) {
				mc.choice = choice;
				mc.s = CustomerState.ordered;
				System.out.println ("state changed to ordered");
				stateChanged();
			}		
		}
	}

	public void msgOrderIsReady(String choice, int table) {
		//System.out.println("received the order from cook");
		readyOrders.add(new WaiterOrder(choice, table));
		stateChanged();
	}

	public void msgDoneEatingAndLeaving(CustomerAgent c) {
		
		for (MyCustomer mc : customers)
		{
			if (c.getName() == mc.c.getName()) {
				host.msgTableIsFree(mc.t);
				customers.remove(mc);
				stateChanged();
				break;
			}
		}
	}
	
	
	public void msgAtTable() { // from animation
		atTable.release();
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 * @return 
	 */
	protected boolean pickAndExecuteAnAction() {
		//System.out.println ("pick and execute an action");
		for (MyCustomer mc : customers) {
			if (mc.s == CustomerState.waiting) {
				System.out.println ("seating customer");
				seatCustomer(mc); // does not ever return
				System.out.println ("done seating");
				return true;
			}
		}
		
		for (MyCustomer mc : customers) {
			if (mc.s == CustomerState.readyToOrder) {
				TakeOrder(mc);
				//System.out.println("Time to take order");
				return true;
			}
		}
		
		for (MyCustomer mc : customers) {
			if (mc.s == CustomerState.ordered) {
				//System.out.println ("state is currently ordererd");
				GiveOrderToCook(mc);
				return true;
			}
		}

		if (readyOrders.size() > 0)
		{
			TakeFoodToCustomer();
			return true;
		}
		
		return false;
	}

	// Actions

	private void seatCustomer(MyCustomer c) {
		c.c.msgFollowMe(/*Menu()*/);
		c.c.setWaiter(this);
		DoSeatCustomer(c.c, c.t);
		System.out.println ("Trying to seat...");
		try {
			atTable.acquire(); // gets put to sleep since its not able to acquire the permit
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		c.s = CustomerState.seated; 
		waiterGui.DoLeaveCustomer();
	}
	
	private void TakeOrder(MyCustomer c){
		//c.s = CustomerState.asked;
		c.c.msgWhatWouldYouLike();
		c.s = CustomerState.asked;
	}
	
	private void GiveOrderToCook(MyCustomer c){
		System.out.println (c.choice);
		System.out.println (c.t);
		cook.msgHereIsAnOrder(this, c.choice, c.t);
		c.s = CustomerState.orderGiven; 
	}
	
	private void TakeFoodToCustomer()
	{
		for (MyCustomer mc : customers) {
			if (readyOrders.size() > 0)
			{
				if (readyOrders.get(0).table == mc.t) {
					mc.c.msgHereIsYourFood();
					readyOrders.remove(0);
				}
			}
		}
		//msgHereIsYourFood();
		
	}
	
	private void DoSeatCustomer(CustomerAgent customer, int tableNumber) {
		//Notice how we print "customer" directly. It's toString method will do it.
		//Same with "table"
		
		print("Seating " + customer + " at " + tableNumber);
		waiterGui.DoBringToTable(customer, tableNumber); 
	}
	
	private void DoGoToCustomer(MyCustomer c) {
		// animation
	}
	
	public void setGui(WaiterGui gui) {
		waiterGui = gui;
	}

	//utilities


	private class Table {
		CustomerAgent occupiedBy;
		int tableNumber;

		Table(int tableNumber) {
			this.tableNumber = tableNumber;
		}

		void setOccupant(CustomerAgent cust) {
			occupiedBy = cust;
		}

		void setUnoccupied() {
			occupiedBy = null;
		}

		CustomerAgent getOccupant() {
			return occupiedBy;
		}

		boolean isOccupied() {
			return occupiedBy != null;
		}

		public String toString() {
			return "table " + tableNumber;
		}
	}
}

