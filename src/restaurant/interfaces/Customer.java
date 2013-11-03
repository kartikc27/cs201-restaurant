package restaurant.interfaces;

import restaurant.Check;
import restaurant.Menu;
import restaurant.WaiterAgent;
import restaurant.gui.CustomerGui;

/**
 * A sample Customer interface built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public interface Customer {

	Check check = null;
	boolean oweMoney = false;


	public abstract void msgHereIsYourChange(double total);

	public abstract void msgPunish();

	public abstract void msgFoodUnavailable();


	public abstract CustomerGui getGui();

	public abstract void msgHereIsCheck(Check c);

	public abstract void msgHereIsYourFood();

	public abstract void msgWhatWouldYouLike();

	public abstract void setWaiter(WaiterAgent waiterAgent);


	public abstract void msgFollowMe(Menu menu);

}