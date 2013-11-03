package restaurant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import restaurant.CookAgent.Food;
import restaurant.gui.CookGui;
import agent.Agent;

/**
 * Restaurant Host Agent
 */

public class CookAgent extends Agent {

	private Map<String, Food> foodMap = Collections.synchronizedMap(new HashMap<String, Food>());
	private List<MarketOrder> incompleteOrders = Collections.synchronizedList(new ArrayList<MarketOrder>());

	enum State {pending, cooking, done, sent};
	String name;
	public CookGui cookGui = null;
	Timer timer = new Timer();
	private int marketNum = 0;

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

	class MarketOrder {
		String type;
		int amount;
		boolean done = false;
		public Object state;

		public MarketOrder(String t, int a) {
			type = t;
			amount = a;
		}
	}

	class Food {
		int amount;
		int cookingTime;
		boolean orderPending = false;
		public Food (int a, int c) {
			amount = a;
			cookingTime = c;
		}
	}

	private List<CookOrder> orders = Collections.synchronizedList(new ArrayList<CookOrder>());
	private List<MarketAgent> markets = Collections.synchronizedList(new ArrayList<MarketAgent>()); 

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

		if (!incompleteOrders.isEmpty())	{
			synchronized (incompleteOrders) {
				for (MarketOrder o : incompleteOrders) {
					if (!o.done)
						markets.get(marketNum).msgHereIsMarketOrder(o.type, o.amount);	
				}
			}
		}

		synchronized (orders) {
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

			synchronized(foodMap) {
				for (Map.Entry<String, Food> entry : foodMap.entrySet()) {
					if ((entry.getValue().amount <= 0) && (!entry.getValue().orderPending)){
						orderFromMarket(entry.getKey());
						entry.getValue().orderPending = true;
						return true;

					}
				}
			}

		}

		return false;
	}


	public void msgHereIsAnOrder(WaiterAgent w, String choice, int table) {
		orders.add(new CookOrder(w, choice, table, State.pending));
		System.out.println ("Cook: received order of " + choice);
		stateChanged();
	}

	public void msgOrderFulfilled(String type, int amount) {
		foodMap.get(type).amount += amount;
		foodMap.get(type).orderPending = false;

		synchronized(incompleteOrders) {
			for (MarketOrder o : incompleteOrders) {
				if ((o.type == type) && (o.done == false)) {
					o.done = true;
				}
			}
		}
		synchronized(foodMap){
			for (Map.Entry<String, Food> entry : foodMap.entrySet()) {
				System.out.println(entry.getKey() + " " + entry.getValue().amount);
			}
		}
		stateChanged();
	}

	public void msgOrderPartiallyFulfilled(String type, int amount, int amountunfulfilled) {
		foodMap.get(type).amount += amount;
		print ("Order of " + type + " partially fulfilled");
		synchronized(foodMap){
			for (Map.Entry<String, Food> entry : foodMap.entrySet()) {
				System.out.println(entry.getKey() + " " + entry.getValue().amount);
			}
		}
		incompleteOrders.add(new MarketOrder(type, amountunfulfilled));
		marketNum++;
		if (marketNum > 2)
			marketNum = 0;
		stateChanged();
	}

	public void msgOrderUnfulfilled() {
		marketNum++;
		if (marketNum > 2)
			marketNum = 0;
		stateChanged();
	}


	private void CookIt(CookOrder o){
		if (foodMap.get(o.choice).amount > 0) {
			foodMap.get(o.choice).amount--; 
			CookFood(o.choice);
		}
		else {
			System.out.println("I'm out of food!");
			o.waiter.msgImOutOfFood(o.table);
			orders.remove(o);
			orderFromMarket(o.choice);
			foodMap.get(o.choice).orderPending = true;
		}
	}

	private void PlateIt(CookOrder o) {
		o.waiter.msgOrderIsReady(o.choice, o.table); 
		orders.remove(o);
	}

	private void CookFood(final String choice) {
		timer.schedule(new TimerTask() {
			public void run() {
				print("Done cooking " + choice );
				markFoodDone(choice);
			}
		},
		foodMap.get(choice).cookingTime*1000);
	}

	private void orderFromMarket(String type) {
		print ("Attempting to order " + type);
		markets.get(marketNum).msgHereIsMarketOrder(type, 5);
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

	public void addMarkets(List<MarketAgent> markets) {
		this.markets = markets;
	}

	public void drainInventory() {
		synchronized(foodMap) {
			for (Map.Entry<String, Food> entry : foodMap.entrySet()) {
				entry.getValue().amount = 0;
			}
		}
		synchronized(foodMap) {
			for (Map.Entry<String, Food> entry : foodMap.entrySet()) {
				System.out.println(entry.getKey() + " " + entry.getValue().amount);
			}
		}
	}

}

