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

	

	boolean oweMoney = false;
	Check check = null;


	public abstract void msgHereIsYourChange(double total);

	public abstract void msgPunish();

	public abstract void msgHereIsCheck(Check c);

	public abstract void msgFoodUnavailable();

	public abstract void msgFollowMe(Menu menu);

	public abstract void msgWhatWouldYouLike();

	public abstract void msgHereIsYourFood();

	public abstract CustomerGui getGui();



}