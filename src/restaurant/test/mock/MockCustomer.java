package restaurant.test.mock;


import restaurant.CashierAgent;
import restaurant.Check;
import restaurant.Menu;
import restaurant.WaiterAgent;
import restaurant.gui.CustomerGui;
import restaurant.interfaces.Customer;

/**
 * A sample MockCustomer built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public class MockCustomer extends Mock implements Customer {

	/**
	 * Reference to the Cashier under test that can be set by the unit test.
	 */
	public CashierAgent cashier;
	public Check check1 = null;
	public double money;

	public MockCustomer(String name) {
		super(name);

	}

	public void msgHereIsYourChange(double total) {
		log.add(new LoggedEvent("Received HereIsYourChange from cashier. Change = "+ total));
	}


	public void msgPunish() {
		log.add(new LoggedEvent("Received YouOweUs from cashier."));
	}

	
	public void setCashier(CashierAgent c) {
		cashier = c;
	}

	@Override
	public void msgHereIsCheck(Check c) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgFoodUnavailable() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgFollowMe(Menu menu) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgWhatWouldYouLike() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgHereIsYourFood() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public CustomerGui getGui() {
		// TODO Auto-generated method stub
		return null;
	}

	

}
