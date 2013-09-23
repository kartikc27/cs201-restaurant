package restaurant;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import restaurant.CustomerAgent.AgentEvent;
import agent.Agent;

/**
 * Restaurant Host Agent
 */

public class CookAgent extends Agent {
	
	
	enum State {pending, cooking, done, sent};
	String name;
	boolean busy = false;
	
	class CookOrder {
		WaiterAgent waiter;
		String choice;
		int table;
		State state;
		
		
		public CookOrder(WaiterAgent w, String c, int t, State s) {
			waiter = w;
			choice = c;
			table = t;
			state = s;
		}
	}
	
	class Food {
		String choice;
		int cookingTime;
	}
	
	private List<CookOrder> orders = new ArrayList<CookOrder>();
	
	Timer timer;
	Map <String , Food> foods;
	
	public CookAgent(String name) {
		super();
		this.name = name;
	}
	

	@Override
	protected boolean pickAndExecuteAnAction() {
		
		// an incoming order should not come while these orders are being processed. 
		if (!orders.isEmpty())
		{
			
			for (CookOrder o : orders){
				if (o.state == State.done) {
					PlateIt(o);
					break;
				}
				
			
			}
		
			for (CookOrder o : orders){
				if (o.state == State.pending) {
					System.out.println (o.state);
					CookIt(o);
					break;
				}
				
			}
			
			for (CookOrder o : orders){
				if (o.state == State.sent) {
					orders.remove(o);
					break;
				}
				
			}
			
			return true;
		}
		
		
		return false;
	}
	
	
	public void msgHereIsAnOrder(WaiterAgent w, String choice, int table) {
		orders.add(new CookOrder(w, choice, table, State.pending));
		System.out.println (choice + " " + table);
		System.out.println ("I have this many orders" + orders.size());
		stateChanged();
	}
	
	public void FoodDone(CookOrder o) {
		o.state = State.done;
		stateChanged();
		
	}
	
	private void CookIt(CookOrder o){
		System.out.println ("Cooking...");
		o.state = State.cooking;
		print("Done cooking");
		FoodDone(o);
		stateChanged();
	}
	
	private void PlateIt(CookOrder o) {
		o.state = State.sent;
		o.waiter.msgOrderIsReady(o.choice, o.table);
		
	}
}

