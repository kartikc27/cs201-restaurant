package restaurant;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import restaurant.CustomerAgent.AgentEvent;
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
	
	public Semaphore takingBreak = new Semaphore(0,true);

	Timer breakTimer = new Timer();

	boolean readyCustomers = false;
	boolean WantBreak = false;
	public boolean onBreak = false;
	boolean pendingActions = true;
	private CashierAgent cashier;

	enum CustomerState{waiting, seated, readyToOrder, asked, ordered, orderGiven, done, notAvailable, doneEating, eating};

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
		print("Received message sit at table");
		customers.add(new MyCustomer(cust, table, CustomerState.waiting));
		//WantBreak = true;
		stateChanged();
	}

	public void msgImReadyToOrder(CustomerAgent cust) {
		for (MyCustomer mc : customers)
		{
			if (cust.equals(mc.c)) {
				mc.s = CustomerState.readyToOrder;
				stateChanged();
			}

		}
	}

	public void msgHereIsMyChoice(String choice, CustomerAgent c) {
		print ("Received choice of " + choice + " from " + c.getName());
		orderGiven.release();
		for (MyCustomer mc : customers)
		{
			if (c == mc.c){
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

	public void msgDoneEating(CustomerAgent c){
		for(MyCustomer mc:customers){
			if(mc.c.equals(c)){
				mc.s = CustomerState.doneEating;
				stateChanged();
				return;
			}
		}
	}

	public void msgLeaving(CustomerAgent c) {
		for (MyCustomer mc : customers)
		{
			if (c.equals(mc.c)) {
				host.msgTableIsFree(mc.t);
				mc.s = CustomerState.done;
				stateChanged();
			}
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
		print ("Break Accepted");
		onBreak = true;
		stateChanged();
	}

	public void msgBreakDenied() {
		print ("Break Denied");
		onBreak = false;
		stateChanged();
	}

	public void msgWantBreak() {
		WantBreak = true;
		stateChanged();
	}



	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 * @return 
	 */
	protected boolean pickAndExecuteAnAction() {

		if (!onBreak) {
			if(!customers.isEmpty()){
				for (MyCustomer mc : customers) {
					if (mc.s == CustomerState.asked) {
						orderGiven.drainPermits();
						try {
							orderGiven.acquire();
						} catch (InterruptedException e) {
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


				for(MyCustomer c:customers){
					if(c.s == CustomerState.doneEating) {
						prepareCheck(c);
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

				for (MyCustomer mc : customers) {
					if (mc.s == CustomerState.done) {
						waiterGui.DoLeaveCustomer();
					}
				}

				if (readyOrders.size() > 0) {
					TakeFoodToCustomer();
					return true;
				}

				if (WantBreak) {
					for (MyCustomer mc : customers)
					{
						if (mc.s != CustomerState.done) {
							pendingActions = true;
							break;
						}
						else {
							pendingActions = false;
						}
					}

					if (!pendingActions) {
						host.msgIWantABreak(this);
					}
				}
			}
		}
		else if (onBreak) {
			waiterGui.setOffBreak();
			takingBreak.drainPermits();
			try {
				takingBreak.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			onBreak = false;
			WantBreak = false;
			return true;
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
		mc.s = CustomerState.seated;
	}


	private void seatCustomer(MyCustomer c) {
		if (!waiterGui.isHome()) {
			waiterGui.DoLeaveCustomer();
			leftCustomer.drainPermits();
			try {
				leftCustomer.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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
						mc.s = CustomerState.eating;
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

	private void prepareCheck(MyCustomer customer)
	{
		print("Preparing bill for Customer");
		customer.s = CustomerState.done;
		waiterGui.DoClearTable(customer.t);
		Check c = new Check(customer.c, customer.t, customer.choice);
		cashier.msgGiveCheckToCashier(c);
		waiterGui.DoGoToTable(customer.t); 
		atTable.drainPermits();
		try {
			atTable.acquire(); 
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		customer.c.msgHereIsCheck(c);
		stateChanged();
	}

	public void setCashier(CashierAgent cashier){
		this.cashier = cashier;
	}

	public void setGui(WaiterGui gui) {
		waiterGui = gui;
	}

	public WaiterGui getGui() {
		return waiterGui;
	}


}

