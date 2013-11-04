package restaurant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import restaurant.CookAgent.Food;
import restaurant.gui.CookGui;
import agent.Agent;

/**
 * Restaurant Host Agent
 */

public class CookAgent extends Agent {

	private Map<String, Food> foodMap = Collections.synchronizedMap(new HashMap<String, Food>());
	private List<MarketOrder> incompleteOrders = Collections.synchronizedList(new ArrayList<MarketOrder>());
	enum State {pending, cooking, done, plated, sent};
	String name;
	public CookGui cookGui = null;
	Timer timer = new Timer();
	private int marketNum = 0;

	int plateNum = 0;
	int orderNumber = 0;
	private Semaphore atFridge = new Semaphore(0, true);
	private Semaphore atGrill = new Semaphore(0, true);
	private Semaphore atPlate = new Semaphore(0, true);
	private Semaphore platingFood = new Semaphore(0, true);
	private Semaphore atHome = new Semaphore(0,true);
	private Semaphore askWaiter = new Semaphore(0,true);





	class CookOrder {
		WaiterAgent waiter;
		String choice;
		int table;
		State state;
		int grill;
		int plate;
		int orderNum;



		public CookOrder(WaiterAgent w, String c, int t, State s) {
			waiter = w;
			choice = c;
			table = t;
			state = s;
			orderNum = orderNumber;
			grill = orderNumber%3;
			plate = grill;
		}
	}

	enum MarketOrderState {pending, ordering, done};

	class MarketOrder {
		String type;
		int amount;

		public MarketOrderState state;

		public MarketOrder(String t, int a) {
			type = t;
			amount = a;
			state = MarketOrderState.pending;
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


		synchronized (incompleteOrders) {
			for (MarketOrder o : incompleteOrders) {
				if (o.state == MarketOrderState.pending) {
					print ("Ordering more of partially completed order");
					o.state = MarketOrderState.ordering;
					markets.get(marketNum).msgHereIsMarketOrder(o.type, o.amount);	
				}
			}
		}


		synchronized (orders) {
			if (!orders.isEmpty())
			{
				for (CookOrder o : orders){
					if (o.state == State.done) {
						o.state = State.plated;
						print ("Plating " + o.choice + " from " + o.grill + " to " + o.plate);
						PlateIt(o);	
						return true;

					}
				}
			}
		}

		synchronized (orders) {
			if (!orders.isEmpty()) {
				for (CookOrder o : orders){
					if (o.state == State.pending) {
						print ("Cooking " + o.choice + " on " + o.grill);
						CookIt(o);

						o.state = State.cooking;
						return true;
					}
				}
			}
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

		return false;
	}


	public void msgHereIsAnOrder(WaiterAgent w, String choice, int table) {
		orders.add(new CookOrder(w, choice, table, State.pending));
		orderNumber++;
		System.out.println ("Cook: received order of " + choice + " " + orders.size());
		stateChanged();
	}

	public void msgOrderFulfilled(String type, int amount) {
		foodMap.get(type).amount += amount;
		foodMap.get(type).orderPending = false;

		synchronized(incompleteOrders) {
			for (MarketOrder o : incompleteOrders) {
				if ((o.type == type) && (o.state == MarketOrderState.ordering)) {
					o.state = MarketOrderState.done;
					foodMap.get(o.type).orderPending = true;
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

	public void msgOrderUnfulfilled(String type) {
		synchronized (incompleteOrders) {
			for (MarketOrder m : incompleteOrders) {
				if ((m.type.equals(type)) && (m.state == MarketOrderState.ordering)) {
					m.state = MarketOrderState.pending;
				}
			}
		}
		marketNum++;
		if (marketNum > 2)
			marketNum = 0;
		stateChanged();
	}


	private void CookIt(CookOrder o){
		if (foodMap.get(o.choice).amount > 0) {
			foodMap.get(o.choice).amount--; 
			CookFood(o);
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
		PlateFood(o);
		platingFood.drainPermits();
		try {
			platingFood.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		o.waiter.msgOrderIsReady(o.choice, o.table, o.plate); 
		print ("SENT THIS MESSAGE");
		o.state = State.sent;
	}

	private void PlateFood(CookOrder o) {
		cookGui.DoGoToGrill2(o.grill);
		print ("GOING TO GRILL " + o.grill);
		atGrill.drainPermits();
		try {
			atGrill.acquire(); 
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		cookGui.DoGoToPlate(o.plate, o.orderNum);
		atPlate.drainPermits();
		try {
			atPlate.acquire(); 
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		print ("done plating " + o.choice);

	}

	private void CookFood(final CookOrder o) {

		DoGetIngredients();

		cookGui.procureFood(o.choice, o.orderNum);

		cookGui.DoGoToGrill(o.grill, o.orderNum);

		atGrill.drainPermits();
		try {
			atGrill.acquire(); 
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		cookGui.DoGoHome();
		atHome.drainPermits();
		try {
			atHome.acquire(); 
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		timer.schedule(new TimerTask() {
			public void run() {
				print("Done cooking " + o.choice );
				markFoodDone(o);
			}
		},
		foodMap.get(o.choice).cookingTime*1000);
	}

	private void orderFromMarket(String type) {
		print ("Attempting to order " + type);
		markets.get(marketNum).msgHereIsMarketOrder(type, 5);
	}


	public void markFoodDone(CookOrder or) {
		synchronized(orders) {
			for (CookOrder o : orders){
				if (o.orderNum == or.orderNum) {
					o.state = State.done;
				}
			}
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

	// GUI Stuff

	private void DoGetIngredients() {
		cookGui.DoGoToFridge(); 
		atFridge.drainPermits();
		try {
			atFridge.acquire(); 
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public void msgAtFridge() {
		atFridge.release();
		stateChanged();
	}

	public void msgAtGrill() {
		atGrill.release();
		stateChanged();
	}

	public void msgAtHome() {
		atHome.release();
		stateChanged();
	}

	public void msgAtPlate() {
		atPlate.release();
		askWaiter.release();
		platingFood.release();
		stateChanged();
	}

	public void msgImTakingTheFood(String choice, int t) {
		for (CookOrder o : orders) {
			if (o.state == State.sent)  {
				cookGui.removeFood(o.orderNum);
			}
		}
		stateChanged();
	}


}

