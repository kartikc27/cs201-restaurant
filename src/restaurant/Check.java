package restaurant;

import java.util.HashMap;
import java.util.Map;


public class Check {
	public CustomerAgent c;
	public WaiterAgent w;
	public int tableNum;
	public String choice;
	public Double price;
	public enum CheckState {uncomputed, unpaid, delivered, paid, incomplete, done};
	public CheckState state;
	
	

	public Check(String choice, int tableNum, CustomerAgent c, WaiterAgent w)
	{	
		this.w = w;
		this.c = c;
		this.tableNum = tableNum;
		this.choice = choice;
		state = CheckState.uncomputed;
	}
	
	
}