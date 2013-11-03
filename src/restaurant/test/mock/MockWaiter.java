package restaurant.test.mock;


import restaurant.CashierAgent;
import restaurant.Check;
import restaurant.interfaces.Customer;
import restaurant.interfaces.Waiter;

/**
 * A sample MockCustomer built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public class MockWaiter extends Mock implements Waiter {

	/**
	 * Reference to the Cashier under test that can be set by the unit test.
	 */
	public CashierAgent cashier;

	public MockWaiter(String name) {
		super(name);

	}
	
	public void msgHereIsComputedcheck(Check c) {
		log.add(new LoggedEvent("Received msgHereIsComputedcheck from cashier"));
	}

	@Override
	public void msgHereIsComputedCheck(Check c) {
		// TODO Auto-generated method stub
		
	}
	
	

	

}
