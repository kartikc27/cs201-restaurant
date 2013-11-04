package restaurant.test;

import restaurant.CashierAgent.mbState;
import restaurant.Check;
import restaurant.CashierAgent;
import restaurant.Check;
import restaurant.CookAgent;
import restaurant.MarketAgent;
import restaurant.Check.CheckState;
import restaurant.CustomerAgent;
import restaurant.WaiterAgent;
import restaurant.interfaces.Customer;
import restaurant.interfaces.Waiter;
import restaurant.test.mock.MockCustomer;
import restaurant.test.mock.MockMarket;
import restaurant.test.mock.MockWaiter;
//import restaurant.test.mock.MockWaiter;
import junit.framework.*;

/**
 * 
 * This class is a JUnit test class to unit test the CashierAgent's basic interaction
 * with waiters, customers, and the host.
 * It is provided as an example to students in CS201 for their unit testing lab.
 *
 * @author Monroe Ekilah
 */
public class CashierTest extends TestCase
{
	CookAgent cook;
	//these are instantiated for each test separately via the setUp() method.
	CashierAgent cashier;
	MockWaiter waiter;
	MockCustomer customer;
	MockCustomer flake;
	MockMarket market1 = new MockMarket("Market 1", 10, 10, 10, 10, cook, cashier);
	MockMarket market2 = new MockMarket("Market2", 2, 5, 3, 1, cook, cashier);

	Check check1;
	Check check2;


	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception{
		super.setUp();		
		cashier = new CashierAgent("cashier");		
		customer = new MockCustomer("mockcustomer");
		flake = new MockCustomer("flake");
		flake.money = 0;
		customer.setCashier(cashier);
		waiter = new MockWaiter("mockwaiter");

		check1 = new Check("Steak", 1, customer, waiter);
	}	
	/**
	 * This tests the cashier under very simple terms: one customer is ready to pay the exact bill.
	 */
	public void testOneNormalCustomerScenario()
	{
		System.out.println("Test 1: testOneCustomer");

		// check postconditions for step 1 and preconditions for step 2
		assertEquals("Cashier should have $0", cashier.money, 0.0);
		assertEquals("Cashier checks should be empty", cashier.uncomputedChecks.size(), 0);
		assertEquals("Cashier checks should be empty", cashier.computedChecks.size(), 0);
		assertFalse("Cashier's scheduler should have returned false (no actions to do on a bill from a waiter), but didn't.", cashier.pickAndExecuteAnAction());
		assertEquals(
				"MockWaiter should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		assertEquals(
				"MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());


		// Check if cashier receives the check
		cashier.msgGiveOrderToCashier("Steak", 1, customer, waiter);
		assertEquals("Uncomputed Checks should contain one check", cashier.uncomputedChecks.size(), 1);
		assertEquals("Computed Checks should contain 0 checks", cashier.computedChecks.size(), 0);

		// Check if cashier computes the check
		cashier.pickAndExecuteAnAction();
		assertEquals("Uncomputed Checks should contain one check", cashier.uncomputedChecks.size(), 1);
		assertEquals("Computed Checks should contain 1 check", cashier.computedChecks.size(), 1);
		assertEquals("The computed check's price is correct", cashier.computedChecks.get(0).check.price, cashier.priceMap.get(check1.choice));
		assertEquals("Waiter log should have one: " + waiter.log.toString(), 1, waiter.log.size());
		assertEquals("Customer log should have zero: " + customer.log.toString(), 0, customer.log.size());

		// Check if cashier processes the check

		//check created by customer
		check1 = new Check("Steak", 1, customer, waiter);
		check1.price = 15.99;
		cashier.msgPayingCheck(check1, check1.price);
		assertEquals("Uncomputed Checks should contain one check", cashier.uncomputedChecks.size(), 1);
		assertEquals("Computed Checks should contain 1 check", cashier.computedChecks.size(), 1);
		assertEquals("Customer log should have zero: " + customer.log.toString(), 0, customer.log.size());
		assertEquals("Waiter log should have one: " + waiter.log.toString(), 1, waiter.log.size());


		assertFalse("Cashier's scheduler should have returned false (no actions left to do), but didn't.", 
				cashier.pickAndExecuteAnAction());
	}

	public void testFlake()
	{
		System.out.println("Test 2: Test Flake");

		// check postconditions for step 1 and preconditions for step 2
		assertEquals("Cashier should have $0", cashier.money, 0.0);
		assertEquals("Cashier checks should be empty", cashier.uncomputedChecks.size(), 0);
		assertEquals("Cashier checks should be empty", cashier.computedChecks.size(), 0);
		assertFalse("Cashier's scheduler should have returned false (no actions to do on a bill from a waiter), but didn't.", cashier.pickAndExecuteAnAction());
		assertEquals(
				"MockWaiter should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		assertEquals(
				"MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		assertEquals("Cashier should have $0", cashier.money, 0.0);

		assertEquals("Customer should have $0", flake.money, 0.0);

		// Check if cashier receives the check
		cashier.msgGiveOrderToCashier("Steak", 1, customer, waiter);
		assertEquals("Uncomputed Checks should contain one check", cashier.uncomputedChecks.size(), 1);
		assertEquals("Computed Checks should contain 0 checks", cashier.computedChecks.size(), 0);

		// Check if cashier computes the check
		cashier.pickAndExecuteAnAction();
		assertEquals("Uncomputed Checks should contain one check", cashier.uncomputedChecks.size(), 1);
		assertEquals("Computed Checks should contain 1 check", cashier.computedChecks.size(), 1);
		assertEquals("The computed check's price is correct", cashier.computedChecks.get(0).check.price, cashier.priceMap.get(check1.choice));
		assertEquals("Waiter log should have one: " + waiter.log.toString(), 1, waiter.log.size());
		assertEquals("Customer log should have zero: " + customer.log.toString(), 0, customer.log.size());

		//check created by customer
		Check check1 = new Check("Steak", 1, customer, waiter);
		check1.price = 15.99;
		//cashier.msgPayingCheck(check1, check1.price);
		assertEquals("Uncomputed Checks should contain one check", cashier.uncomputedChecks.size(), 1);
		assertEquals("Computed Checks should contain 1 check", cashier.computedChecks.size(), 1);
		assertEquals("Customer log should have zero: " + customer.log.toString(), 0, customer.log.size());
		assertEquals("Waiter log should have one: " + waiter.log.toString(), 1, waiter.log.size());
	
		// cashier receives payment
		cashier.msgPayingCheck(check1, check1.price);
		assertEquals("Uncomputed Checks should contain one check", cashier.uncomputedChecks.size(), 1);
		assertEquals("Computed Checks should contain 1 checks", cashier.computedChecks.size(), 1);
		
		//cashier processes check
		cashier.pickAndExecuteAnAction();
		assertEquals("Cashier money should be 0 since cust is a flake: ", cashier.money, 0.0);
		
	}
	
	public void testMarket1()
	{
		System.out.println("Test 3: Test Cashier/Market, one order fulfilled, bill paid in full");
		cashier.money = 100.0;
		// check postconditions for step 1 and preconditions for step 2
		assertEquals("Cashier should have $100", cashier.money, 100.0);
		assertEquals("Cashier should have 0 market bills", cashier.marketbills.size(), 0);
		
		cashier.msgHereIsMarketBill(10.99, market1);
		assertEquals("Cashier should have 1 market bill", cashier.marketbills.size(), 1);
		cashier.marketbills.get(0).market = market1;
		System.out.println(cashier.marketbills.get(0).market);
		
		cashier.pickAndExecuteAnAction();
		assertEquals("Market log should have one: " + market1.log.toString(), 1, market1.log.size());
		System.out.println("Cashier has " + cashier.money + " left");
		assertTrue("Marketbills should contain a bill with the state paid. It doesn't.",
                cashier.marketbills.get(0).state == mbState.paid);
		
		
	}
	
	public void testMarket2()
	{
		System.out.println("Test 4: Test Cashier/Market, one order fulfilled by 2 markets");

		cashier.money = 100.0;
		// check postconditions for step 1 and preconditions for step 2
		assertEquals("Cashier should have $100", cashier.money, 100.0);
		assertEquals("Cashier should have 0 market bills", cashier.marketbills.size(), 0);
		
		cashier.msgHereIsMarketBill(10.99, market1);
		assertEquals("Cashier should have 1 market bill", cashier.marketbills.size(), 1);
		cashier.marketbills.get(0).market = market1;
		System.out.println(cashier.marketbills.get(0).market);
		
		cashier.pickAndExecuteAnAction();
		assertEquals("Market log should have one: " + market1.log.toString(), 1, market1.log.size());
		System.out.println("Cashier has " + cashier.money + " left");
		assertTrue("Marketbills should contain a bill with the state paid. It doesn't.",
                cashier.marketbills.get(0).state == mbState.paid);
		
		cashier.msgHereIsMarketBill(5.99, market2);
		assertEquals("Cashier should have 2 market bills", cashier.marketbills.size(), 2);
		assertTrue("Marketbills should contain a bill with the state unpaid. It doesn't.",
                cashier.marketbills.get(1).state == mbState.unpaid);
		cashier.marketbills.get(1).market = market2;
		System.out.println(cashier.marketbills.get(1).market);
		
		cashier.pickAndExecuteAnAction();
		assertEquals("Market log should have one: " + market2.log.toString(), 1, market2.log.size());
		System.out.println("Cashier has " + cashier.money.doubleValue() + " left");
		assertTrue("Marketbills should contain a bill with the state paid. It doesn't.",
                cashier.marketbills.get(0).state == mbState.paid);
		
	}
	
	
	public void testMarket3()
	{
		System.out.println("Test 5: Test Cashier/Market: Cashier does not have enough cash");

		cashier.money = 0.0;
		// check postconditions for step 1 and preconditions for step 2
		assertEquals("Cashier should have $0", cashier.money, 0.0);
		assertEquals("Cashier should have 0 market bills", cashier.marketbills.size(), 0);
		
		cashier.msgHereIsMarketBill(10.99, market1);
		assertEquals("Cashier should have 1 market bill", cashier.marketbills.size(), 1);
		cashier.marketbills.get(0).market = market1;
		System.out.println(cashier.marketbills.get(0).market);
		
		cashier.pickAndExecuteAnAction();
		assertEquals("Market log should have 0: " + market1.log.toString(), 0, market1.log.size());
		System.out.println("Cashier has " + cashier.money + " left");
		assertTrue("Marketbills should contain a bill with the state upaid. It doesn't.",
                cashier.marketbills.get(0).state == mbState.unpaid);
		assertEquals("The unpaid bill should be 10.99 ", cashier.marketbills.get(0).price, 10.99);
		
	}
	
	public void testMarket4()
	{
		System.out.println("Test 6: Test Cashier/Market: Cashier repays Market when possible");

		cashier.money = 0.0;
		// check postconditions for step 1 and preconditions for step 2
		assertEquals("Cashier should have $0", cashier.money, 0.0);
		assertEquals("Cashier should have 0 market bills", cashier.marketbills.size(), 0);
		
		cashier.msgHereIsMarketBill(10.99, market1);
		assertEquals("Cashier should have 1 market bill", cashier.marketbills.size(), 1);
		cashier.marketbills.get(0).market = market1;
		System.out.println(cashier.marketbills.get(0).market);
		
		cashier.pickAndExecuteAnAction();
		assertEquals("Market log should have 0: " + market1.log.toString(), 0, market1.log.size());
		System.out.println("Cashier has " + cashier.money + " left");
		assertTrue("Marketbills should contain a bill with the state upaid. It doesn't.",
                cashier.marketbills.get(0).state == mbState.unpaid);
		assertEquals("The unpaid bill should be 10.99 ", cashier.marketbills.get(0).price, 10.99);
		
		cashier.money += 15.99;
		cashier.pickAndExecuteAnAction();
		assertEquals("Market log should have 0: " + market1.log.toString(), 1, market1.log.size());
		System.out.println("Cashier has " + cashier.money + " left");
		assertTrue("Marketbills should contain a bill with the state paid. It doesn't.",
                cashier.marketbills.get(0).state == mbState.paid);
		assertEquals("The unpaid bill should be 10.99 ", cashier.marketbills.get(0).price, 10.99);
		
		
	}


}
