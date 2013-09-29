##Restaurant Project Repository

###Instructions

1. Run the Java Application
2. Enter Customer and Waiter information. Nothing will show on the Animation Panel unless at least 1 waiter and 1 hungry 
customer exists
3. Add Customers and Waiters 

###General Notes

No known errors...

####Host Agent

The host agent "Rami" stays on the screen and does not move. However, the agent plays 
an active role in assigning waiters to customers and managing tables.

####Customer Agent

A customer agent is indicated by a green square. A customer can be set to hungry at the point of creation or 
later by clicking his/her respective button and selecting the hungry checkbox.

####Waiter Agent

A waiter agent is displayed with a blue box. Waiters can be created from the waiter creation panel. 
Once they are created, they are responsible for seating the customers, taking their orders, giving orders to the cook, and delivering the orders. 
The waiter also notifies the host when a customer is leaving. 

####Cook Agent

The cook agent is indicated with a Dark Blue square at the top right of the animation panel.
The cook is on screen for purposes of clarity. The cook uses a timer to "cook" food at certain lengths depending
on the type. After cooking the food, it transfers it to the waiter, who then transfers it to the customer. 

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
