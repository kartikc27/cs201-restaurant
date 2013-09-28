package restaurant;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import restaurant.gui.CookGui;
import agent.Agent;

/**
 * Restaurant Host Agent
 */

public class CookAgent extends Agent {


	enum State {pending, cooking, done, sent};
	String name;
	boolean busy = false;
	public CookGui cookGui = null;
	Timer timer1 = new Timer();

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

	public void setGui(CookGui gui) {
		cookGui = gui;
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
		System.out.println ("Received order of " + choice);
		stateChanged();
	}

	public void FoodDone(String choice) {
		for (CookOrder o : orders){
			if (o.choice == choice) {
				o.state = State.done;
			}
			break;
		}
		stateChanged();
	}

	private void CookIt(CookOrder o){
		o.state = State.cooking;
		CookFood(o.choice);
		System.out.println("Done cooking " + o.choice);
		//FoodDone();
		stateChanged();
	}

	private void PlateIt(CookOrder o) {
		o.state = State.sent;
		o.waiter.msgOrderIsReady(o.choice, o.table); 
	}


	private void CookFood(final String choice) {
		Do("Cooking Food");
		//This next complicated line creates and starts a timer thread.
		//We schedule a deadline of getHungerLevel()*1000 milliseconds.
		//When that time elapses, it will call back to the run routine
		//located in the anonymous class created right there inline:
		//TimerTask is an interface that we implement right there inline.
		//Since Java does not all us to pass functions, only objects.
		//So, we use Java syntactic mechanism to create an
		//anonymous inner class that has the public method run() in it.
		timer1.schedule(new TimerTask() {
			public void run() {
				print("Done cooking " + choice );
				FoodDone(choice);
			}
		},
		1000);
	}
}

