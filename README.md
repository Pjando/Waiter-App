# Waiter-App
This repository contains only code for the waiter application.

#### Principle of operation:

All of these apps are connected with one server/machine that "will be locally placed in the restaurant".
When the server runs, all of the apps look for the server and connect to it. The server will talk with the database and all the
data that will be sent from the devices will be put into the database. For each server request, the server will send certain information back to each device.

#### Server:

Server is local (in local network), so there is no need for an internet connection!. Only the sever has permission to "talk with" the database straight away. Server connects customer, waiter, kitchen apps.

#### Client app:

The client will interact with this application with tablets at the table, provided by the restaurant.

Client has opportunity to:
+ see the menu with pictures
+ make an order
+ call waiter
+ track his/her meals

#### Kitchen app:

Kitchen automatically receives orders and places them in sections according to type of meal. Kitchen should change status of order to "Cooking" when preparing the meal and "Ready" when the waiter needs to collect the meal.

#### Waitor app:

The waiter app is the main source of communication betweent the kitchen and the client. The waiter can confirm customer orders and in doing so can edit, add or delete items from the order. The waiter can also set what is available on the menu taking items off due to lack of stock.
