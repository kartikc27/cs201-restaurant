package restaurant;

import java.util.ArrayList;
import java.util.HashMap;
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
	private Map<String, Food> foodMap = new HashMap();


	enum State {pending, cooking, done, sent};
	String name;
	boolean busy = false;
	public CookGui cookGui = null;
	Timer timer = new Timer();

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
		int amount;
		int cookingTime;
		public Food (int a, int c) {
			amount = a;
			cookingTime = c;
		}
	}

	private List<CookOrder> orders = new ArrayList<CookOrder>();

	Map <String , Food> foods;

	public CookAgent(String name) {
		super();
		
		Food steak = new Food(1, 5);
		Food salad = new Food(1, 2);
		Food pizza = new Food(1, 4);
		Food chicken = new Food(1, 3);
		this.name = name;
		foodMap.put("Steak", steak); 
		foodMap.put("Salad", salad);  
		foodMap.put("Pizza", pizza);  
		foodMap.put("Chicken", chicken);
	}

	public void setGui(CookGui gui) {
		cookGui = gui;
	}


	@Override
	protected boolean pickAndExecuteAnAction() {
		if (!orders.isEmpty())
		{
			for (CookOrder o : orders){
				if (o.state == State.done) {
					PlateIt(o);	
				}
				break;
			}
			for (CookOrder o : orders){
				if (o.state == State.pending) {
					CookIt(o);
					o.state = State.cooking;
				}
				break;
			}
			return true;
		}
		return false;
	}

	public void msgHereIsAnOrder(WaiterAgent w, String choice, int table) {
		orders.add(new CookOrder(w, choice, table, State.pending));
		System.out.println ("Cook: received order of " + choice);
		stateChanged();
	}


	private void CookIt(CookOrder o){
		foodMap.get(o.choice).amount--; 
		if (foodMap.get(o.choice).amount >= 0) {
			CookFood(o.choice);
		}
		else {
			System.out.println("IM OUT OF FOOD");
			o.waiter.msgImOutOfFood(o.table);
			orders.remove(o);
		}
	}

	private void PlateIt(CookOrder o) {
		o.waiter.msgOrderIsReady(o.choice, o.table); 
		orders.remove(o);
	}

	//receives message twice...
	private void CookFood(final String choice) {
		timer.schedule(new TimerTask() {
			public void run() {
				print("Done cooking " + choice );
				markFoodDone(choice);
			}
		},
		foodMap.get(choice).cookingTime*1000);
	}

	public void markFoodDone(String choice) {
		for (CookOrder o : orders){
			if (o.choice == choice) {
				o.state = State.done;
			}
			break;
		}
		stateChanged();
	}

}

