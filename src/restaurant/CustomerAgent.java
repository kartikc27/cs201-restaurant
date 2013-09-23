package restaurant;

import restaurant.gui.CustomerGui;
import restaurant.gui.RestaurantGui;
import agent.Agent;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Restaurant customer agent.
 */
public class CustomerAgent extends Agent {
	private String name;
	private int hungerLevel = 5;        // determines length of meal
	Timer timer = new Timer();
	private CustomerGui customerGui;

	// agent correspondents
	private HostAgent host;
	private WaiterAgent waiter;


	//    private boolean isHungry = false; //hack for gui
	public enum AgentState
	{DoingNothing, WaitingInRestaurant, BeingSeated, Seated, OrderingFood, DoneOrdering, Eating, DoneEating, Leaving};
	private AgentState state = AgentState.DoingNothing;//The start state

	public enum AgentEvent 
	{none, gotHungry, followWaiter, seated, order, foodReceived, doneEating, doneLeaving};
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

	public void msgGotHungry() {//from animation
		print("I'm hungry");
		event = AgentEvent.gotHungry;
		stateChanged();
	}

	public void msgFollowMe() {
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
			System.out.println ("time to follow the waiter!");
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
			state = AgentState.DoneOrdering;
			OrderFood();
			return true;
		}

		if (state == AgentState.DoneOrdering && event == AgentEvent.foodReceived){
			state = AgentState.Eating;
			EatFood();
			return true;
		}

		if (state == AgentState.Eating && event == AgentEvent.doneEating){
			state = AgentState.Leaving;
			LeaveTable();
			return true;
		}

		if (state == AgentState.Leaving && event == AgentEvent.doneLeaving){
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
		customerGui.DoGoToSeat(1);//hack; only one table
		//state = AgentState.OrderingFood;
	}
	
	private void CallWaiter() {
		Do("Calling waiter for food");
		waiter.msgImReadyToOrder(this);
	}
	
	private void OrderFood() {
		String choice = customerGui.receiveOrderFromGui();
		waiter.msgHereIsMyChoice(choice, this);
		Do ("I would like to order" + choice); 
	}

	private void EatFood() {
		Do("Eating Food");
		//This next complicated line creates and starts a timer thread.
		//We schedule a deadline of getHungerLevel()*1000 milliseconds.
		//When that time elapses, it will call back to the run routine
		//located in the anonymous class created right there inline:
		//TimerTask is an interface that we implement right there inline.
		//Since Java does not all us to pass functions, only objects.
		//So, we use Java syntactic mechanism to create an
		//anonymous inner class that has the public method run() in it.
		timer.schedule(new TimerTask() {
			Object cookie = 1;
			public void run() {
				print("Done eating, cookie=" + cookie);
				event = AgentEvent.doneEating;
				//isHungry = false;
				stateChanged();
			}
		},
		5000);//getHungerLevel() * 1000);//how long to wait before running task
	}

	private void LeaveTable() {
		Do("Leaving.");
		waiter.msgDoneEatingAndLeaving(this);
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

	public void setGui(CustomerGui g) {
		customerGui = g;
	}

	public CustomerGui getGui() {
		return customerGui;
	}
}



