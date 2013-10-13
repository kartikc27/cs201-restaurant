package restaurant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import restaurant.Check.CheckState;
import agent.Agent;

public class CashierAgent extends Agent {

	public List<MyCheck> computedChecks = new ArrayList<MyCheck>();
	public List<Check> uncomputedChecks = new ArrayList<Check>();

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

	public Map<String, Double> priceMap = new HashMap<String, Double>() { { 
		put ("Steak", 15.99); 
		put ("Chicken", 10.99);
		put ("Salad", 5.99);
		put ("Pizza", 8.99);
	}};


	public void msgGiveOrderToCashier(String c, int tNum, CustomerAgent cust, WaiterAgent waiter) {
		uncomputedChecks.add(new Check(c, tNum, cust, waiter));
		print ("Received msgGiveOrderToCashier");
		stateChanged();
	}


	public void msgPayingCheck(Check check, Double amountPaid) {
		
		for(MyCheck c:computedChecks) {
			if(c.check.equals(check)) {
				c.amountPaid = amountPaid;
				c.check.state = CheckState.paid;
				break;
			}	
		}
		stateChanged();
	}

	public boolean pickAndExecuteAnAction() {

		for(Check c: uncomputedChecks) {
			if(c.state == CheckState.uncomputed) {
				c.state = CheckState.unpaid;
				computeCheck(c);
				return true;
			}
		}

		for(MyCheck c: computedChecks) {
			if(c.check.state == Check.CheckState.paid) {
				processCheck(c);
				return true;
			}
		}
		return false;

	}


	private void computeCheck(Check c) {
		print ("Computing check");
		computedChecks.add(new MyCheck(c));
		c.price = priceMap.get(c.choice);
		if (c.c.oweMoney) {
			for (MyCheck checks : computedChecks) {
				if ((checks.check.state == CheckState.incomplete) && (checks.check.c.equals(c.c))) {
					c.price = c.price + c.c.check.price;
				}
					
				
			}
		}
		c.w.msgHereIsComputedCheck(c);
	}


	private void processCheck(MyCheck c)
	{
		if (c.check.price <= c.amountPaid) {
			money += c.amountPaid;
			c.check.state = Check.CheckState.done;
			print("Money is now " + money + " with purchase of " + c.amountPaid);
			print("Processing check for Customer");
		}
		else {
			c.check.state = CheckState.incomplete;
			print ("Pay up next time or you will face Rami's Wrath");
			c.check.c.msgPunish();
		}
	}


	public String getName() {
		return name;
	}    
}