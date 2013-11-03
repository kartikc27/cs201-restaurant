package restaurant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import agent.Agent;


public class MarketAgent extends Agent {

	private CookAgent cook;
	Timer timer = new Timer();
	class MyFood {
		String type;
		int amount;
		public MyFood (String t, int a) {
			type = t;
			amount = a;
		}
	}
	private Map<String, Integer> inventory = new HashMap<String, Integer>();
	MyFood order;

	private String name;
	private boolean busy = false;
	private boolean orderPending = false;

	public MarketAgent(String name, int numPizza, int numSalad, int numSteak, int numChicken, CookAgent cook) {
		super();
		this.name = name;
		this.cook = cook;
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
			return true;
		}
		return false;
	}

	private void completeOrder() {
		if (order.amount < inventory.get(order.type)) {
			timer.schedule(new TimerTask() {
				public void run() {  
					cook.msgOrderFulfilled(order.type, order.amount);
					print("Fulfilled order of " + order.type);
					orderPending = false;
					busy = false;
					
				}},
				10000);
			
		}
		else {
			cook.msgOrderUnfulfilled();
		}
	}

	public String getName() {
		return name;
	}



}


