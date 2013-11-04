##Restaurant Project Repository

###Instructions

1. Clone repo: git clone git@github.com:usc-csci201-fall2013/restaurant_chillaka.git
2. Open project: File > New > Other > Java Project from Existing Ant Buildfile. Select the build.xml file from the cloned repo
3. Run the project as a Java Application 


TEST SUGGESTION:

1. Run Application
2. Drain inventory
3. Create customer "steak" and select hungry (if customer chooses to leave, repeat until he stays)

This scenario will demonstrate the customer reordering a different food each time, until he realizes all the options are gone. As a result, he leaves.
Also, the cook will have to use each of the markets to restock on supplies. 


NOTE: Customer always has a chance of leaving while waiting, so if it looks like an added customer does not show up, then he/she left! The console will indicate this

##Hacks

The following hacks make it easier to test the code:

1. If the customer's name is "steak", "salad", "chicken", or "pizza" (or with a capital first letter), the customer orders his name
2. If the customer's name is "flake" or "Flake" the customer has no money.
3. Drain button at the top (next to the pause button) to drain the Cook's inventory. Useful in testing the markets. 

Reminder: To slow things down and see them more clearly you can always increase the frameDisplay in animationPanel.java. 

###General Notes

No known errors. Never had a concurrent mod error.

Print statements in the console give a more detailed look at what is going. 

The speed of the animation can be changed by increasing the frameDisplay variable in AnimationPanel.java

The customer waiting area is pretty stupid and was a last minute thing. It only holds two customers before it overlaps.

The cook animations are all there from getting food from the refrigerator to putting it on the grill to plating it

####Host Agent

The host agent "Rami" stays on the screen and does not move. However, the agent plays 
an active role in assigning waiters to customers and managing tables. Rami is also in charge of allowing waiters to go on breaks. 

####Customer Agent

A customer agent is indicated by a green square. A customer can be set to hungry at the point of creation or 
later by clicking his/her respective button and selecting the hungry checkbox. The customer is seated by a waiter,
calls the waiter to give his/her order, receives food from a waiter, eats the food, and leaves the table after informing
a waiter. 

If a customer agent is waiting in a restaurant without a waiter being assigned to him, he/she has a 25% chance of leaving.

Unless the customer agent is named flake, he/she starts off with $100. If a customer runs out of money, he/she steals $100 and pays any previously unpaid check to avoid Rami's wrath.

####Waiter Agent

A waiter agent is displayed with a blue box. Waiters can be created from the waiter creation panel. 
Once they are created, they are responsible for seating the customers, taking their orders, giving orders to the cook, delivering the orders, notifying the cashier of the order, and delivering the check to the customer.
The waiter also notifies the host when a customer is leaving. 

If an order is unavailable, the waiter will notify the customer and allow him/her to order something else.

####Cook Agent

The cook agent is indicated with a Dark Blue square at the top right of the animation panel.
The cook is on screen for purposes of clarity. The cook uses a timer to "cook" food at certain lengths depending
on the type. After cooking the food, it transfers it to the waiter, who then transfers it to the customer. 

The cook maintains his/her personal inventory. If he/she runs out of food, he/she orders from the market. Also, the cook
notifies the waiter if an order is unavailable. The cook has access to 3 markets. If one market cannot satisfy his/her order he/she tries the next market.

####Market Agent

A Market agent represents a market from which a cook can order food. This agent processes one order at a time (which is why booleans were used for a simple solution).
If an order comes in while the market is processing an order, it will reject it and the cook will have to order from another market. 
If a market runs out of food, it does not restock.

####Cashier Agent

The cashier agent receives a Customer's order from the waiter when the customer is finished eating. He/She then computes the check
and passes it back to the waiter to give to the customer. The customer then pays the cashier directly and the cashier processes the payment. 
If the customer does not pay enough the cashier "punishes" the customer but allows him to pay the next time. 

###Food GUI Objects
Food Gui objects are color coded based on the food. Red is Pizza, Brown is Steak, Yellow is Chicken, and Green is Salad. 
While they are being transferred, they have question marks inside them. Once they are delivered, the question mark
is substituted with a letter to indicate the food. 

###Student Information
  + Name: Kartik Chillakanti
  + USC Email: chillaka@usc.edu
  + USC ID: 1094069213
  + Lab: Tuesday 6 PM

###Resources
  + [Restaurant v1](http://www-scf.usc.edu/~csci201/readings/restaurant-v1.html)
  + [Agent Roadmap](http://www-scf.usc.edu/~csci201/readings/agent-roadmap.html)
