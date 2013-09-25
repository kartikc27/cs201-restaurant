package restaurant;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Semaphore;

import restaurant.WaiterAgent.CustomerState;
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
	
	
	public boolean isFree = false;

	public HostGui hostGui = null;
	
	private class MyWaiter {
		public MyWaiter(WaiterAgent w, int nTables) {
			waiter = w;
			numTables = nTables; 
		}
		WaiterAgent waiter;
		int numTables;
	}
	
	private List<MyWaiter> waiters = new ArrayList<MyWaiter>();

	public HostAgent(String name) {
		super();
		this.name = name;
		// make some tables
		tables = new ArrayList<Table>(NTABLES);
		for (int ix = 1; ix <= NTABLES; ix++) {
			tables.add(new Table(ix));//how you add to a collections
		}
	}

	/*public String getMaitreDName() {
		return name;
	}*/

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
	
	public void msgTableIsFree(int table) {
		for (Table tbl : tables) {
			if (tbl.tableNumber == table) {
				tbl.setUnoccupied();
				stateChanged();
			}
		}
	}

	/*public void msgLeavingTable(CustomerAgent cust) {
		for (Table table : tables) {
			if (table.getOccupant() == cust) {
				print(cust + " leaving " + table);
				table.setUnoccupied();
				stateChanged();
			}
		}
	}

	public void msgAtTable() {//from animation
		//print("msgAtTable() called");
		s.release();// = true;
		stateChanged();
	}*/

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		/* Think of this next rule as:
            Does there exist a table and customer,
            so that table is unoccupied and customer is waiting.
            If so seat him at the table.
		 */
		
		for (Table table : tables) {
			if (!table.isOccupied()) {
				if (!waitingCustomers.isEmpty()) {
					int minTables = waiters.get(0).numTables;
					int WaiterWithMinTables = 0;
					int i = 0;
					for (MyWaiter mw : waiters) {
						i++;
						if (mw.numTables < minTables) {
							minTables = mw.numTables;
							WaiterWithMinTables = i;
						}
					}
					
					tellWaiterToSeatCustomer(waitingCustomers.get(0), table, waiters.get(WaiterWithMinTables).waiter);
					//waitingCustomers.get(0).setWaiter(waiters.get(WaiterWithMinTables).waiter);
					//tellWaiterToSeatCustomer(mw, table, waiters.get(WaiterWithMinTables).waiter);
					// get removes the first customer in waitingCustomers
					//waitingCustomers.get(0).setWaiter(waiters.get(WaiterWithMinTables).waiter);
					
					return true;
				}
			}
		}
		

		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions

	private void tellWaiterToSeatCustomer(CustomerAgent customer, Table table, WaiterAgent waiter) {
		/*while (hostGui.headingBack)
		{
			try {
				Thread.sleep(500);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}*/
		
		waiter.msgSitAtTable(customer, table.tableNumber);
		table.setOccupant(customer);
		waitingCustomers.remove(customer);
		table.setOccupant(customer);
		
	}

	// The animation DoXYZ() routines
	/*private void DoSeatCustomer(CustomerAgent customer, Table table) {
		//Notice how we print "customer" directly. It's toString method will do it.
		//Same with "table"
		print("Seating " + customer + " at " + table);
		
		hostGui.DoBringToTable(customer, table.tableNumber); 
		isFree = false;

	}*/

	//utilities

	public void setGui(HostGui gui) {
		hostGui = gui;
	}

	public HostGui getGui() {
		return hostGui;
	}
	
	public void addWaiter(WaiterAgent w) {
		waiters.add((new MyWaiter(w, 0)));
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
}

