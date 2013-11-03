package restaurant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import agent.Agent;


public class MarketAgent extends Agent {

	private CookAgent cook;
	private CashierAgent cashier;
	Timer timer = new Timer();
	class MyFood {
		String type;
		int amount;
		public MyFood (String t, int a) {
			type = t;
			amount = a;
		}
	}
	
	int amountunfulfilled = 0;
	private Map<String, Integer> inventory = Collections.synchronizedMap (new HashMap<String, Integer>());
	MyFood order;

	private String name;
	private boolean busy = false;
	private boolean orderPending = false;
	
	public Map<String, Double> priceMap = new HashMap<String, Double>() { { 
		put ("Steak", 10.99); 
		put ("Chicken", 5.99);
		put ("Salad", 3.99);
		put ("Pizza", 5.99);
	}};

	public MarketAgent(String name, int numPizza, int numSalad, int numSteak, int numChicken, CookAgent cook, CashierAgent cashier) {
		super();
		this.name = name;
		this.cook = cook;
		this.cashier = cashier;
		inventory.put("Pizza", numPizza);
		inventory.put("Salad", numSalad);
		inventory.put("Steak", numSteak);
		inventory.put("Chicken", numChicken);
	}

	public void msgHereIsMarketOrder(String type, int amt) {

		if(!orderPending) {   
			print ("Received order of " + type);
			order = new MyFood(type, amt);
			orderPending = true;
		}
		else if (orderPending) {
			print ("Please order from another market");
			cook.msgOrderUnfulfilled();

		}
		stateChanged();
	}

	@Override
	protected boolean pickAndExecuteAnAction() {

		if ((orderPending) && (!busy)) {
			print ("im here");
			busy = true;
			completeOrder();
			print ("now im here");
			return true;
		}
		return false;
	}

	private void completeOrder() {
		
		synchronized(inventory)
		{
			if (order.amount < inventory.get(order.type)) {
				timer.schedule(new TimerTask() {
					public void run() {  
						cook.msgOrderFulfilled(order.type, order.amount);
						print("Fulfilled order of " + order.type);
						orderPending = false;
						busy = false;
						cashier.msgHereIsMarketBill(priceMap.get(order.type));

					}},
					10000);

			}

			else if (inventory.get(order.type) > 0) {
					amountunfulfilled = order.amount - inventory.get(order.type);
					timer.schedule(new TimerTask() {
						public void run() { 
							cook.msgOrderPartiallyFulfilled(order.type, order.amount-amountunfulfilled, amountunfulfilled);
							print("Partially fulfilled order of " + order.type);
							orderPending = false;
							busy = false;
						}},
						2000);
			}
			else  {
				cook.msgOrderUnfulfilled();
			}
		}
	}

	public String getName() {
		return name;
	}



}


