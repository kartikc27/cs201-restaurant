package restaurant;

import java.util.HashMap;
import java.util.Map;

/* Check
 * 
 * Class used to transfer information between Cashier, Waiter and Customer
 * Represents the check" given at the end of a dinner
 */

public class Check {
	public CustomerAgent c;
	public int tableNum;
	public String choice;
	public Double price;
	public enum CheckState {UNPAID, PAID, DONE};
	public CheckState state;
	
	protected Map<String, Double> priceMap = new HashMap<String, Double>() { { 
		put ("Steak", 1.99); 
		put ("Chicken", 10.99);
		put ("Salad", 5.99);
		put ("Pizza", 8.99);
	}};

	public Check(CustomerAgent c, int tableNum, String choice)
	{
		this.c = c;
		this.tableNum = tableNum;
		this.choice = choice;
		this.price = priceMap.get(choice);
	}
	
	
}