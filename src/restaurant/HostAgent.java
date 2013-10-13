package restaurant;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Semaphore;

import restaurant.gui.HostGui;
import agent.Agent;

/**
 * Restaurant Host Agent
 */
//We only have 2 types of agents in this prototype. A customer and an agent that
//does all the rest. Rather than calling the other agent a waiter, we called him
//the HostAgent. A Host is the manager of a restaurant who sees that all
//is proceeded as he wishes.
public class HostAgent extends Agent {
	static final int NTABLES = 3;//a global for the number of tables.
	//Notice that we implement waitingCustomers using ArrayList, but type it
	//with List semantics.
	public List<CustomerAgent> waitingCustomers = new ArrayList<CustomerAgent>();
	public Collection<Table> tables;
	//note that tables is typed with Collection semantics.
	//Later we will see how it is implemented

	private String name;

	public HostGui hostGui = null;

	private Semaphore seatCustomer = new Semaphore(0, true);

	private class MyWaiter {
		public MyWaiter(WaiterAgent w, int nTables) {
			waiter = w;
			numTables = nTables; 
			onBreak = false;
		}
		WaiterAgent waiter;
		int numTables;
		boolean onBreak;

	}

	private List<MyWaiter> waiters = new ArrayList<MyWaiter>();

	public HostAgent(String name) {
		super();
		this.name = name;
		tables = new ArrayList<Table>(NTABLES);
		for (int ix = 1; ix <= NTABLES; ix++) {
			tables.add(new Table(ix));//how you add to a collections
		}
	}

	public String getName() {
		return name;
	}

	public List getWaitingCustomers() {
		return waitingCustomers;
	}

	public Collection getTables() {
		return tables;
	}

	// Messages

	public void msgIWantFood(CustomerAgent cust) {
		waitingCustomers.add(cust);
		stateChanged();
	}

	public void msgWaiterReporting(WaiterAgent w) {
		waiters.add((new MyWaiter(w, 0)));
		stateChanged();
	}
	public void msgTableIsFree(int table) {
		print ("Received msgTableIsFree");
		for (Table tbl : tables) {
			if (tbl.tableNumber == table) {
				tbl.setUnoccupied();
				stateChanged();
			}
		}
	}

	public void msgIWantABreak(WaiterAgent w)
	{
		if (waiters.size() > 1) {
			for (MyWaiter mw : waiters) {
				if (mw.waiter.equals(w)){
					mw.onBreak = true;
					mw.waiter.msgBreakApproved();
					stateChanged();
					break;
				}
			}
		}
		else {
			w.msgBreakDenied();
			stateChanged();
		}
	}

	public void msgLeaving(CustomerAgent c) {
		waitingCustomers.remove(c);
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		if (!waiters.isEmpty())
		{
			for (Table table : tables) {
				if (!table.isOccupied()) {
					if (waitingCustomers.size() > 0) {
						int i = 0;
						for (MyWaiter m : waiters) {
							if (m.onBreak)
								i++;
						}
						int minTables = waiters.get(i).numTables;
						int WaiterWithMinTables = i;
						int j = 0;
						for (MyWaiter mw : waiters) {
							if (!mw.onBreak)
							{
								if (mw.numTables < minTables) {
									minTables = mw.numTables;
									WaiterWithMinTables = j;
								}
							}
							j++;
						}
						waiters.get(WaiterWithMinTables).numTables++;
						if (waitingCustomers.size() > 0) {
							if (waitingCustomers.contains(waitingCustomers.get(0))) {
								tellWaiterToSeatCustomer(waitingCustomers.get(0), table, waiters.get(WaiterWithMinTables).waiter);
							}
							try {
								seatCustomer.acquire();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	// Actions

	private void tellWaiterToSeatCustomer(CustomerAgent customer, Table table, WaiterAgent waiter) {
		waiter.msgSitAtTable(customer, table.tableNumber);
		table.setOccupant(customer);
		waitingCustomers.remove(customer);
	}

	//utilities

	public void setGui(HostGui gui) {
		hostGui = gui;
	}

	public HostGui getGui() {
		return hostGui;
	}


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

	public void msgCustomerSeated() {
		seatCustomer.release();
	}

}

