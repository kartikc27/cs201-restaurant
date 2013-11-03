package restaurant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import restaurant.Check.CheckState;
import restaurant.interfaces.Customer;
import restaurant.interfaces.Waiter;
import restaurant.test.mock.EventLog;
import restaurant.test.mock.MockWaiter;
import agent.Agent;

public class CashierAgent extends Agent {

	public List<MyCheck> computedChecks = Collections.synchronizedList(new ArrayList<MyCheck>());
	public List<Check> uncomputedChecks = Collections.synchronizedList(new ArrayList<Check>());
	public List<MyMarketBill> marketbills = Collections.synchronizedList(new ArrayList<MyMarketBill>());

	private String name;
	public Double money;

	public CashierAgent(String name) {
		super();
		this.name = name;
		money = 0.0;
	}

	public class MyCheck {
		public Check check;
		public Double amountPaid;

		public MyCheck(Check check){
			this.check = check;
			this.amountPaid = (double) -1;
		}
	}
	public enum mbState {unpaid, paid};
	public class MyMarketBill {
		public Double price;
		mbState state; 
		MarketAgent market;
		public MyMarketBill(Double p, MarketAgent m){
			price = p;
			market = m;
			state = mbState.unpaid;
		}
	}

	public Map<String, Double> priceMap = new HashMap<String, Double>() { { 
		put ("Steak", 15.99); 
		put ("Chicken", 10.99);
		put ("Salad", 5.99);
		put ("Pizza", 8.99);
	}};
	public EventLog log = new EventLog();


	public void msgGiveOrderToCashier(String c, int tNum, Customer c2, Waiter waiterAgent) {
		uncomputedChecks.add(new Check(c, tNum, c2, waiterAgent));
		print ("Received msgGiveOrderToCashier");
		stateChanged();
	}


	public void msgPayingCheck(Check check, Double amountPaid) {
		synchronized(computedChecks) {
			for(MyCheck c:computedChecks) {
				if(c.check.equals(check)) {
					c.amountPaid = amountPaid;
					c.check.state = CheckState.paid;
					break;
				}	
			}
		}
		stateChanged();
	}

	public void msgHereIsMarketBill(double price, MarketAgent m) {
		marketbills.add(new MyMarketBill(price, m));
		stateChanged();
	}

	public boolean pickAndExecuteAnAction() {
		
		synchronized(marketbills) {
			for (MyMarketBill mb : marketbills) {
				if (mb.state == mbState.unpaid) {
					payMarketBill(mb);
					//mb.state = mbState.paid;
				}
			}
		}
		
		synchronized(uncomputedChecks) {
			for(Check c: uncomputedChecks) {
				if(c.state == CheckState.uncomputed) {
					c.state = CheckState.unpaid;
					computeCheck(c);
					return true;
				}
			}
		}


		synchronized(computedChecks) {
			for(MyCheck c: computedChecks) {
				if(c.check.state == Check.CheckState.paid) {
					processCheck(c);
					return true;
				}
			}
		}
		return false;

	}


	private void computeCheck(Check c) {
		print ("Computing check");
		computedChecks.add(new MyCheck(c));
		c.price = priceMap.get(c.choice);
		if (c.c.oweMoney) {
			synchronized(computedChecks) {
				for (MyCheck checks : computedChecks) {
					if ((checks.check.state == CheckState.incomplete) && (checks.check.c.equals(c.c))) {
						c.price = c.price + c.c.check.price;
					}


				}
			}
		}
		c.w.msgHereIsComputedCheck(c);
	}


	private void processCheck(MyCheck c)
	{
		if (c.check.price <= c.amountPaid) {
			money += c.amountPaid.doubleValue();
			c.check.state = Check.CheckState.done;
			print("Money is now " + money + " with Customer purchase of " + c.amountPaid);
			if (c.check.price < c.amountPaid) {
				double change = c.amountPaid - c.check.price;
				c.check.c.msgHereIsYourChange(change);
			}
			
		}
		else {
			c.check.state = CheckState.incomplete;
			print ("Pay up next time or you will face Rami's Wrath");
			c.check.c.msgPunish();
		}
	}
	
	private void payMarketBill(MyMarketBill bill) {
		
		if (money.compareTo(bill.price) > 0) {
			bill.market.msgHereIsBill(bill.price);
			money -= bill.price;
			print ("Paid market. I now have " + money);
			bill.state = mbState.paid;

		}
		else {
			print ("Could not pay with " + money + ". Will fulfill when I have the money");
			for (MyMarketBill mb : marketbills) {
				if (mb.equals(bill)){
					mb.state = mbState.unpaid;
				}
			}
		}
			
	}


	public String getName() {
		return name;
	}    
}