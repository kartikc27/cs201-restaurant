package restaurant;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import restaurant.CookAgent.CookOrder;
import restaurant.gui.CustomerGui;
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
	public Semaphore atCook = new Semaphore(0, true);
	public Semaphore orderGiven = new Semaphore(0, true);
	public Semaphore serveFood = new Semaphore(0, true);

	boolean readyCustomers = false;
	boolean WantBreak = false;
	public boolean onBreak = false;
	boolean pendingActions = true;

	enum CustomerState{waiting, seated, readyToOrder, asked, ordered, orderGiven, done, notAvailable};

	private class MyCustomer {
		public MyCustomer(CustomerAgent customer, int table, CustomerState state) {
			c = customer;
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
		//WantBreak = true;
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
		orderGiven.release();
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
			}
			break;
		}
	}

	public void msgImOutOfFood(int table) {
		for (MyCustomer mc : customers)
		{
			if (mc.t == table) {
				mc.s = CustomerState.notAvailable; 
				stateChanged();
			}
		}
	}


	// GUI synchronization semaphores

	public void msgAtTable() { 
		atTable.release();
		stateChanged();
	}

	public void msgLeftCustomer() {
		leftCustomer.release();
		stateChanged();
	}

	public void msgAtCook() {
		atCook.release();
		stateChanged();
	}

	public void msgFoodDelivered() {
		serveFood.release();
		stateChanged();
	}

	public void msgBreakApproved() { 
		System.out.println ("Break Accepted");
		onBreak = true;
	}

	public void msgBreakDenied() {
		System.out.println ("Break Denied");
		onBreak = false;
	}
	
	public void msgWantBreak() {
		WantBreak = true;
	}



	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 * @return 
	 */
	protected boolean pickAndExecuteAnAction() {

		if (!onBreak) {
			// agent sleeps until order is given
			for (MyCustomer mc : customers) {
				if (mc.s == CustomerState.asked) {
					try {
						orderGiven.acquire();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return true;
				}
			}

			for (MyCustomer mc : customers) {
				if (mc.s == CustomerState.ordered) {
					GiveOrderToCook(mc);
					return true;
				}

			}

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
				if (mc.s == CustomerState.notAvailable) {
					TellCustomerFoodUnavailable(mc);
					return true;
				}
			}

			if (readyOrders.size() > 0) {
				TakeFoodToCustomer();
				return true;
			}

			for (MyCustomer mc : customers)
			{
				if (mc.s != CustomerState.done) {
					pendingActions = true;
					break;
				}
				else {
					pendingActions = false;
					break;
				}
			}
			
			if ((!pendingActions) && (WantBreak)) {
				host.msgIWantABreak(this);
			}
		}
		return false;
	}

	// Actions

	private void TellCustomerFoodUnavailable(MyCustomer mc) {
		waiterGui.DoGoToTable(mc.t); 
		atTable.drainPermits();
		try {
			atTable.acquire(); 
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mc.c.msgFoodUnavailable();
		mc.s = CustomerState.done;
	}


	private void seatCustomer(MyCustomer c) {
		DoSeatCustomer(c.c, c.t);
		c.c.msgFollowMe(new Menu());
		c.c.setWaiter(this);
		atTable.drainPermits();
		try {
			atTable.acquire(); 
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		c.s = CustomerState.seated; 
		host.msgCustomerSeated();

		readyCustomers = false;

		for (MyCustomer mc : customers) {
			if (mc.s == CustomerState.readyToOrder) {
				readyCustomers = true;
			}
		}

		if ((!readyCustomers) && (readyOrders.size() == 0))
		{
			waiterGui.DoLeaveCustomer();
			leftCustomer.drainPermits();
			try {
				leftCustomer.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void TakeOrder(MyCustomer c){
		print("Taking the order of " + c.c + " at " + c.t);
		waiterGui.DoGoToTable(c.t); 
		try {
			atTable.acquire(); 
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		c.c.msgWhatWouldYouLike();
		c.s = CustomerState.asked;

	}


	private void GiveOrderToCook(MyCustomer c){
		c.s = CustomerState.orderGiven;
		atCook.drainPermits();
		waiterGui.DoGoToCook();		
		try {
			atCook.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cook.msgHereIsAnOrder(this, c.choice, c.t);
	}


	private void TakeFoodToCustomer()
	{
		for (MyCustomer mc : customers) {
			if (!readyOrders.isEmpty())
			{
				if (mc.s != CustomerState.done)
				{
					if (readyOrders.get(0).table == mc.t) {
						waiterGui.DoGoToCook();		
						atCook.drainPermits();
						try {
							atCook.acquire();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						waiterGui.procureFood(mc.choice, mc.t);
						waiterGui.DoGoToTable(mc.t); 
						atTable.drainPermits();
						try {
							atTable.acquire(); 
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						serveFood.drainPermits();
						waiterGui.DoDeliverFood(mc.t, mc.choice, mc.c.getGui());
						try {
							serveFood.acquire(); 
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						mc.c.msgHereIsYourFood();
						readyOrders.remove(0);
						mc.s = CustomerState.done;
						waiterGui.DoLeaveCustomer();

					}
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


	public void setGui(WaiterGui gui) {
		waiterGui = gui;
	}

	public WaiterGui getGui() {
		return waiterGui;
	}


}

