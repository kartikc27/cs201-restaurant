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
	public Semaphore leftCustomer = new Semaphore(0, true);
	public Semaphore takeOrder = new Semaphore(0, true);
	
	boolean readyCustomers = false;
	
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
		
		stateChanged();
	}
	
	public void msgImReadyToOrder(CustomerAgent cust) {
		for (MyCustomer mc : customers)
		{
			if (cust.getName() == mc.c.getName()) {
				mc.s = CustomerState.readyToOrder;
				stateChanged();
			}
				
		}
	}
	
	public void msgHereIsMyChoice(String choice, CustomerAgent c) {
		for (MyCustomer mc : customers)
		{
			if (c.getName() == mc.c.getName()) {
				mc.choice = choice;
				mc.s = CustomerState.ordered;
				stateChanged();
			}		
		}
		
	}

	public void msgOrderIsReady(String choice, int table) {
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
	
	
	public void msgAtTable() { 
		atTable.release();
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 * @return 
	 */
	protected boolean pickAndExecuteAnAction() {
		for (MyCustomer mc : customers) {
			if (mc.s == CustomerState.waiting) {
				seatCustomer(mc); 
				return true;
			}
		}
		
		for (MyCustomer mc : customers) {
			if (mc.s == CustomerState.readyToOrder) {
				TakeOrder(mc);
				return true;
			}
		}
		
		for (MyCustomer mc : customers) {
			if (mc.s == CustomerState.ordered) {
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
		c.c.msgFollowMe(new Menu());
		c.c.setWaiter(this);
		DoSeatCustomer(c.c, c.t);
		try {
			atTable.acquire(); 
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		c.s = CustomerState.seated; 
		
		readyCustomers = false;
		
		for (MyCustomer mc : customers) {
			if (mc.s == CustomerState.readyToOrder) {
				readyCustomers = true;
			}
		}
		
		if (!readyCustomers)
		{
			waiterGui.DoLeaveCustomer();
		
		try {
			leftCustomer.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	}
	
	private void TakeOrder(MyCustomer c){
		DoTakeOrder(c.c, c.t);
		c.c.msgWhatWouldYouLike();
		c.s = CustomerState.asked;
		
	}
	
	

	private void GiveOrderToCook(MyCustomer c){
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
		
	}
	
	private void DoSeatCustomer(CustomerAgent customer, int tableNumber) {
		//Notice how we print "customer" directly. It's toString method will do it.
		//Same with "table"
		print("Seating " + customer + " at " + tableNumber);
		waiterGui.DoBringToTable(customer.getGui(), tableNumber); 
	}
	
	private void DoTakeOrder(CustomerAgent customer, int tableNumber) {
		print("Taking the order of " + customer + " at " + tableNumber);
		waiterGui.DoGoToTable(customer.getGui(), tableNumber); 
		try {
			atTable.acquire(); 
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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

