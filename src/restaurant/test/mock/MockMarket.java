package restaurant.test.mock;


import restaurant.CashierAgent;
import restaurant.Check;
import restaurant.CookAgent;
import restaurant.interfaces.Customer;
import restaurant.interfaces.Market;
import restaurant.interfaces.Waiter;

/**
 * A sample MockCustomer built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public class MockMarket extends Mock implements Market {

	/**
	 * Reference to the Cashier under test that can be set by the unit test.
	 */
	public CashierAgent cashier;

	public MockMarket(String name, int i, int j, int k, int l, CookAgent cook, CashierAgent cashier2) {
		super(name);

	}

	@Override
	public void msgHereIsBill(double amountPaid) {
		log.add(new LoggedEvent("Received msgHereIsBill from cashier"));
		
	}
	
	

}