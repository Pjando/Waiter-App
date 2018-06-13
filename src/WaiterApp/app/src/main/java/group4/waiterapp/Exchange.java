package group4.waiterapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import shared.Command;
import shared.Item;
import shared.Table;
import shared.Type;

/**
 * @author Pavan Jando
 *
 * Singleton class holds all the information obtained from
 * the server and is accessed by most other java classes.
 */
public class Exchange {

  /**
   * Exchange object is returned upon object creation.
   */
  private static Exchange exchange = new Exchange();
  /**
   * Queue for sending commands to the server.
   */
  private BlockingQueue<Command> commandQueue = new LinkedBlockingQueue<>();
  /**
   * List of orders from the server that the waiter is assigned.
   */
  private List<Order> orders = new ArrayList<>();
  /**
   * The menu - list of all the food and drink items.
   */
  private List<Item> menu = new ArrayList<>();
  /**
   * List of food that is received from the server.
   */
  private List<Item> food = new ArrayList<>();
  /**
   * List of drinks received from the server.
   */
  private List<Item> drinks = new ArrayList<>();
  /**
   * List of tables relevant to current waiter.
   */
  private List<Table> tables = new ArrayList<>();
  /**
   * Lists used for testing when not connected to the server.
   */
  private List<Item> temp = new ArrayList<>();
  /**
   * List of items to add to order when not connected to server.
   */
  private List<Item> items = new ArrayList<>();

  /**
   * Constructs the test data for when not connected to server.
   */
  private Exchange() {
    //Test data to use when server is offline.
    Item i1 = new Item(1, "Steak", "It's tasty", Type.MAIN, 12, "N/A", 1.20f);
    Item i2 = new Item(2, "Burger", "It's tasty", Type.MAIN, 12, "N/A", 1.40f);
    i1.setQuantity(3);
    i2.setQuantity(4);
    items.add(i1);
    items.add(i2);

    Random rand = new Random();

    Order o1 = new Order(1, items, String.valueOf(rand.nextInt(8) + 1) + ":05:02", true, "Confirmation Required", true);
    Order o7 = new Order(7, items, String.valueOf(rand.nextInt(8) + 1) + ":05:02", true, "Confirmation Required", true);

    temp = new ArrayList<>();
    Item t1 = new Item(3, "Duck", "It's tasty", Type.MAIN, 15, "N/A", 1.60f);
    Item t2 = new Item(4, "Big Duck", "It's tasty", Type.MAIN, 16, "N/A", 1.80f);
    temp.add(t1);
    temp.add(t2);
    temp.add(i1);
    temp.add(i2);

    //orders.add(o1);   //CHANGE FOR TESTING
    //orders.add(o7);   //
    Collections.sort(orders, new OrderComp());

  }

  /**
   * Singleton class returns the only instantiated Exchange object with all the data.
   *
   * @return Exchange object with all data from the server.
   */
  static Exchange getInstance() {
    if (exchange == null) {
      return new Exchange();
    } else {
      return exchange;
    }
  }

  /**
   * Creates an Order object and add this order to the list of orders.
   *
   * @param table     the table number of the order.
   * @param orderList the list of items associated with the table number.
   * @param time      the time the order was created.
   */
  void setOrder(int table, List<Item> orderList, String time) {
    for (int i = 0; i < orders.size(); i++) {
      if (table == orders.get(i).getTableNumber()) {
        orders.remove(i);
        Order updatedOrder = new Order(table, orderList, time, false, "Confirmation Required", true);
        orders.add(updatedOrder);
      }
    }
  }

  /**
   * Updates the order removing foods with quantity 0.
   * @param index indicates which order needs to updated.
   */
  void updateOrder(int index) {
    getOrder(index).updateFood();
  }

  /**
   * Gets order from the array list according to index.
   *
   * @param index index 0 is the first order, index n is the latest order.
   * @return Order object containing all details of order.
   */
  Order getOrder(int index) {
    return orders.get(index);
  }

  /**
   * Gets the list of all of the orders.
   * @return List of type Order with all the current orders.
   */
  List<Order> getAllOrders() {
    return orders;
  }

  /**
   * Set the list of orders to a new list of orders.
   * @param newList The new list of type Order.
   */
  void setAllOrders(List<Order> newList) {
    orders = newList;
  }

  /**
   * Adds a Command to the command queue to send to the server.
   * @param cmd the command to be sent to the server.
   */
  void addCommand(Command cmd) { //Add a command to send to the server.
    commandQueue.add(cmd);
  }

  /**
   * Gets the queue of commands to be sent to the server.
   * @return Blocking queue of type Command that contains Commands.
   */
  BlockingQueue<Command> getCommandQueue() { //Returns the queue of commands to connection class
    return this.commandQueue;
  }

  /**
   * Set the status of an order to the status received from the server. E.g. Received, cooking, ready.
   * @param cmd A command sent from the server containing the new status.
   */
  void setTableStatus(Command cmd) {
    for (Order o : orders) {
      if (o.getTableNumber() == cmd.getTableNumber()) {
        o.setStatus(cmd.getStatus().substring(0, 1).toUpperCase() + cmd.getStatus().substring(1));
      }
    }
  }

  /**
   * Gets the time the order was made.
   * @param index references the order needed from the list of orders.
   * @return String time in format HH:MM:SS.
   */
  String getOrderTime(int index) {
    return orders.get(index).getTimeOfOrder();
  }

  /**
   * Sets whether a table needs help from the waiter.
   * @param index references the order needed from the list of orders.
   * @param helpMe Used to set whether a table needs help (true) or not (false).
   */
  void setHelp(int index, Boolean helpMe) {
    if (helpMe) {
      for (Order o : orders) {
        if (o.getTableNumber() == index) {
          o.setHelp(helpMe);
        }
      }
    } else {
      orders.get(index).setHelp(helpMe);
    }
  }

  /**
   * Gets whether table needs help or not.
   * @param index references the order needed from the list of orders.
   * @return Boolean value true representing needs help, false otherwise.
   */
  Boolean getHelp(int index) {
    return orders.get(index).getHelp();
  }

  /**
   * Gets the menu.
   * @return List of type item with all food and drinks.
   */
  List<Item> getMenu() { //CHANGE FOR TESTING
    Collections.sort(temp, new MenuComp());
    return menu;
  }

  /**
   * Sorts the menu into food and drinks and sets this list to the menu list.
   * @param menu Lists of items retrieved from the server.
   */
  void setMenu(List<Item> menu) {
    drinks.clear();
    food.clear();
    this.menu.clear();

    for (Item i : menu) {
      if (i.getType() == Type.ALCOHOL || i.getType() == Type.JUICE || i.getType() == Type.SOFT) {
        drinks.add(i);
      } else {
        food.add(i);
      }
    }
    Collections.sort(drinks, new MenuComp());
    Collections.sort(food, new MenuComp());

    this.menu.addAll(food);
    this.menu.addAll(drinks);
  }

  /**
   * Sets the list of tables from the server.
   * @param tables List of type Table from the server.
   */
  void setTables(List<Table> tables) {
    this.tables = tables;
  }

  /**
   * Creates a list of empty orders based on tables retrieved from the server.
   */
  void defaultTables() {
    for (Table t : tables) {
      Order o = new Order(t.getTableNumber(), null, "No Order", false, "No Order", false);
      orders.add(o);
    }
  }

  /**
   * Gets whether an order is active order e.g. not and empty order.
   * @param index references the order needed from the list of orders.
   * @return Boolean true if order is active, false otherwise.
   */
  Boolean isOrderActive(int index) {
    return orders.get(index).isActive();
  }

  /**
   * Adds new items to an order.
   * @param index references the order needed from the list of orders.
   * @param additions List of type Item of all the new items to be added.
   */
  void addNewItems(int index, List<Item> additions) {
    orders.get(index).addFood(additions);
  }

  /**
   * Sets the relevant order to true if they have paid, false otherwise.
   * @param table table number to get order
   * @param paid boolean true if they have paid, false otherwise.
   */
  void setPaid(int table, boolean paid) {
    for (Order o : orders) {
      if (o.getTableNumber() == table) {
        o.setPayment(paid);
      }
    }
  }

  /**
   * Gets whether a table has paid or not
   * @param index references the order.
   * @return Boolean true if order is paid, false otherwise.
   */
  Boolean getPaid(int index) {
    return orders.get(index).getPayment();
  }

  /**
   * Resets the list of orders.
   */
  void reset() {
    orders.clear();
  }

  /**
   * Resets order to default state
   * @param index references order in list
   */
  void resetOrder(int index) {
    Order o = orders.get(index);
    orders.remove(index);
    orders.add(new Order(o.getTableNumber(), null, "No Order", false, "No Order", false));
  }
}

/**
 * Comparator that sorts the orders.
 */
class OrderComp implements Comparator<Order> {
  /**
   * Sorts the orders putting the earliest orders first.
   * @param order1 order to be compared.
   * @param order2 order to be compared
   * @return 1 if order 1 is greater, -1 id order 1 is less, 0 if equal.
   */
  @Override
  public int compare(Order order1, Order order2) {
    return order1.compareTo(order2);
  }
}

/**
 * Comparator that sorts Item's by id.
 */
class MenuComp implements  Comparator<Item> {
  /**
   * Sorts the items putting the lowest id first.
   * @param i1 item to be compared.
   * @param i2 item to be compared.
   * @return 1 if item 1 id is greater, -1 if item 1 id is lower, 0 if equal.
   */
  @Override
  public  int compare(Item i1, Item i2) {
    if (i1.getID() > i2.getID()) {
      return 1;
    } else if (i1.getID() < i2.getID()) {
      return -1;
    }
    return 0;
  }
}