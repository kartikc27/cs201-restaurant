package restaurant;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import restaurant.gui.CustomerGui;
import agent.Agent;

/**
 * Restaurant customer agent.
 */
public class CustomerAgent extends Agent {
	private String name;
	private int hungerLevel = 5; // determines length of meal
	private String choice;
	Timer timer = new Timer();
	private CustomerGui customerGui;
	private boolean reorder = false;

	// agent correspondents
	private HostAgent host;
	private WaiterAgent waiter;
	private CashierAgent cashier;

	private Menu myMenu;
	private Check check;

	public enum AgentState
	{DoingNothing, WaitingInRestaurant, BeingSeated, OrderingFood, WaitingForFood, Eating, DoneEating, Leaving, WaitingForCheck};
	private AgentState state = AgentState.DoingNothing;//The start state

	public enum AgentEvent 
	{none, gotHungry, followWaiter, seated, order, foodReceived, foodUnavailable, doneEating, doneLeaving, checkArrived};
	AgentEvent event = AgentEvent.none;

	/**
	 * Constructor for CustomerAgent class
	 *
	 * @param name name of the customer
	 * @param gui  reference to the customergui so the customer can send it messages
	 */
	public CustomerAgent(String name){
		super();
		this.name = name;
	}

	/**
	 * hack to establish connection to Host agent.
	 */
	public void setHost(HostAgent host) {
		this.host = host;
	}

	public void setWaiter(WaiterAgent waiter) {
		this.waiter = waiter;
	}

	public String getCustomerName() {
		return name;
	}
	// Messages

	public void msgGotHungry() {
		print("I'm hungry");
		event = AgentEvent.gotHungry;
		stateChanged();
	}

	public void msgFollowMe(Menu menu) {
		myMenu = menu;
		print("Received msgFollowMe");
		event = AgentEvent.followWaiter;
		stateChanged();
	}

	public void msgWhatWouldYouLike() {
		print ("Received msgWhatWouldYouLike");
		event = AgentEvent.order;
		stateChanged();
	}

	public void msgHereIsYourFood() {
		print ("Received msgHereIsYourFood");
		event = AgentEvent.foodReceived;
		stateChanged();
	}

	public void msgFoodUnavailable() {
		print ("Received msgFoodUnavailable");
		event = AgentEvent.foodUnavailable;
		reorder = true;
		stateChanged();
	}

	public void msgHereIsCheck(Check c) {
		print("Received bill from Waiter");
		check = c;
		event = AgentEvent.checkArrived; 
		stateChanged();
	}

	public void msgAnimationFinishedGoToSeat() {
		//from animation
		event = AgentEvent.seated;
		stateChanged();
	}

	public void msgAnimationFinishedLeaveRestaurant() {
		//from animation
		event = AgentEvent.doneLeaving;
		stateChanged();
	}


	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		//	CustomerAgent is a finite state machine

		if (state == AgentState.DoingNothing && event == AgentEvent.gotHungry ){
			state = AgentState.WaitingInRestaurant;
			GoToRestaurant();
			return true;
		}

		if (state == AgentState.WaitingInRestaurant && event == AgentEvent.followWaiter ){
			state = AgentState.BeingSeated;
			SitDown();
			return true;
		}

		if (state == AgentState.BeingSeated && event == AgentEvent.seated){
			state = AgentState.OrderingFood;
			CallWaiter();
			return true;
		}

		if (state == AgentState.OrderingFood && event == AgentEvent.order){
			state = AgentState.WaitingForFood;
			OrderFood();
			return true;
		}

		if (state == AgentState.WaitingForFood) {

			if (event == AgentEvent.foodReceived){
				state = AgentState.Eating;
				EatFood();
				return true;

			}

			if (event == AgentEvent.foodUnavailable) {
				state = AgentState.OrderingFood;
				event = AgentEvent.order;
				print ("REORDERING");
				//OrderFood();
				return true;
			}

		}


		if (state == AgentState.Eating && event == AgentEvent.doneEating){
			askForCheck();
			state = AgentState.WaitingForCheck;
			return true;
		}

		if ((state == AgentState.WaitingForCheck) && (event == AgentEvent.checkArrived)) {
			payCheckAndLeave();
			state = AgentState.DoingNothing;
			return true;
		}


		return false;
	}

	// Actions

	private void GoToRestaurant() {
		Do("Going to restaurant");
		host.msgIWantFood(this);//send our instance, so he can respond to us
	}

	private void SitDown() {
		Do("Being seated. Going to table");
		customerGui.DoGoToSeat();//hack; only one table
	}

	private void CallWaiter() {
		Do("Calling waiter for food");
		waiter.msgImReadyToOrder(this);
	}

	private void OrderFood() {
		print ("Ordering food");
		/*Random randomGenerator = new Random();
		int randomInt = randomGenerator.nextInt(4);
		choice = myMenu.menuItems[randomInt];
		waiter.msgHereIsMyChoice(choice, this);*/
		if (!reorder) {
			choice = name;
			if (choice.equals("steak"))
				choice = "Steak";
			if (choice.equals("pizza"))
				choice = "Pizza";
			if (choice.equals("salad"))
				choice = "Salad";
			if (choice.equals("chicken"))
				choice = "Chicken";
		}
		else {
			Random randomGenerator = new Random();
			int randomInt = randomGenerator.nextInt(4);
			choice = myMenu.menuItems[randomInt];
		}
		waiter.msgHereIsMyChoice(choice, this);
		Do ("I would like to order " + choice); 
	}

	private void EatFood() {
		Do("Eating Food");
		timer.schedule(new TimerTask() {
			public void run() {
				print("Done eating");
				event = AgentEvent.doneEating;
				stateChanged();
			}
		},
		getHungerLevel() * 1000);
	}

	private void askForCheck() {
		print("Asking for check");
		waiter.msgDoneEating(this); 
	}

	private void payCheckAndLeave() {

		timer.schedule(new TimerTask() {
			public void run() {
				print("Paid check of " + check.price + " for " + choice);
				cashier.msgPayingCheck(check, check.price);  
				LeaveTable();
				print("Leaving the restaurant");

				customerGui.DoExitRestaurant(); //for the animation

			}},
			3000);//how long to wait before running task
	}


	private void LeaveTable() {
		Do("Leaving.");
		waiter.msgLeaving(this);
		customerGui.DoExitRestaurant();
	}

	// Accessors, etc.

	public String getName() {
		return name;
	}

	public int getHungerLevel() {
		return hungerLevel;
	}

	public void setHungerLevel(int hungerLevel) {
		this.hungerLevel = hungerLevel;
		//could be a state change. Maybe you don't
		//need to eat until hunger lever is > 5?
	}

	public String toString() {
		return "customer " + getName();
	}

	public void setCashier(CashierAgent c) {
		cashier = c;
	}

	public void setGui(CustomerGui g) {
		customerGui = g;
	}

	public CustomerGui getGui() {
		return customerGui;
	}


}



