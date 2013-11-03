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
	double money = 0;
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
	private Map<String, foodData> inventory = Collections.synchronizedMap (new HashMap<String, foodData>());
	MyFood order;

	private String name;
	private boolean busy = false;
	private boolean orderPending = false;
	
	public class foodData {
		int amount;
		double price;
		public foodData(int a, double p) {
			amount = a;
			price = p;
		}
	}
	
	

	public MarketAgent(String name, int numPizza, int numSalad, int numSteak, int numChicken, CookAgent cook, CashierAgent cashier) {
		super();
		this.name = name;
		this.cook = cook;
		this.cashier = cashier;
		foodData chickenData = new foodData(numChicken, 5.99);
		foodData saladData = new foodData(numSalad, 3.99);
		foodData steakData = new foodData(numSteak, 10.99);
		foodData pizzaData = new foodData(numPizza, 5.99);
		inventory.put("Chicken", chickenData);
		inventory.put("Salad", saladData);
		inventory.put("Steak", steakData);
		inventory.put("Pizza", pizzaData);
	}

	public void msgHereIsMarketOrder(String type, int amt) {

		
		if (orderPending) {
			print ("Please order from another market");
			cook.msgOrderUnfulfilled(order.type);

		}
		else if(!orderPending) {   
			print ("Received order of " + type);
			order = new MyFood(type, amt);
			orderPending = true;
		}
		stateChanged();
	}
	
	public void msgHereIsBill(double amountPaid) {
		money += amountPaid;
		print ("Received bill from Cashier. I now have " + money);
	}

	@Override
	protected boolean pickAndExecuteAnAction() {

		if ((orderPending) && (!busy)) {
			busy = true;
			completeOrder();
			return true;
		}
		return false;
	}

	private void completeOrder() {
		
		synchronized(inventory)
		{
			if (order.amount < inventory.get(order.type).price) {
				timer.schedule(new TimerTask() {
					public void run() {  
						cook.msgOrderFulfilled(order.type, order.amount);
						inventory.get(order.type).amount -= order.amount;
						print("Fulfilled order of " + order.type);
						orderPending = false;
						busy = false;
					}},
					100);
				print ("Sending bill to Cashier");
				cashier.msgHereIsMarketBill(inventory.get(order.type).price, this);
			}
			
			else if ((inventory.get(order.type).amount > 0) && (order.amount > inventory.get(order.type).amount)) {
					amountunfulfilled = order.amount - inventory.get(order.type).amount;
					inventory.get(order.type).amount = 0;
					timer.schedule(new TimerTask() {
						public void run() { 
							print("Partially fulfilled order of " + order.type);
							cook.msgOrderPartiallyFulfilled(order.type, order.amount-amountunfulfilled, amountunfulfilled);
							orderPending = false;
							busy = false;
							
							
						}},
						2000);
			}
			else  {
				cook.msgOrderUnfulfilled(order.type);
				busy = false;
				orderPending = false;
			}
		}
	}

	public String getName() {
		return name;
	}



}


