package group4.waiterapp;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import shared.Item;

/**
 * @author Pavan Jando
 *
 * Order class used to store data about one order from the server.
 */
public class Order implements Comparable<Order> {

  /**
   * Table number that an order is linked to.
   */
  private int tableNumber;
  /**
   * The list of items for an order.
   */
  private List<Item> food = new ArrayList<>();
  /**
   * The time the order was made, format HH:MM:SS.
   */
  private String timeOfOrder;
  /**
   * Status of the table. E.g. Confirmation Required, Received, Cooking, Ready.
   */
  private String status;
  /**
   * Whether the table needs help.
   */
  private Boolean needHelp = false;
  /**
   * Whether this is an active order.
   */
  private Boolean active = false;
  /**
   * Whether this order has paid.
   */
  private Boolean payment = false;

  /**
   * Constructs an order with the relevant information.
   * @param table the table number.
   * @param items the list of Items.
   * @param time the time the order was made.
   * @param help Whether the table who made the order needs help.
   * @param orderStatus The current status of the order.
   * @param active Whether the table is an active order, or empty.
   */
  Order(int table, List<Item> items, String time, Boolean help, String orderStatus, Boolean active) {
    tableNumber = table;
    food = items;
    timeOfOrder = time;
    needHelp = help;
    status = orderStatus;
    this.active = active;
  }

  /**
   * Gets the table number of the order.
   * @return
   */
  int getTableNumber() {
    return tableNumber;
  }

  /**
   * Get the list of food for the order.
   * @return List of type Item.
   */
  List<Item> getFood() {
    return food;
  }

  /**
   * Add new food items to the order
   * @param newFood List of new items to be added to order.
   */
  void addFood(List<Item> newFood) {
    food.addAll(newFood);
  }

  /**
   * Updates the food list removing any items with less than 1 quantity.
   */
  void updateFood() {
    for (Iterator<Item> iter = food.iterator(); iter.hasNext(); ) {
      Item element = iter.next();
      if (element.getQuantity() < 1) {
        iter.remove();
      }
    }
  }

  /**
   * Gets the time the order was made.
   * @return String format HH:MM:SS
   */
  String getTimeOfOrder() {
    return timeOfOrder;
  }

  /**
   * Gets whether the table needs help.
   * @return Boolean true if table needs help, false otherwise.
   */
  Boolean getHelp() {
    return needHelp;
  }

  /**
   * Sets whether the table needs help.
   * @param help Boolean sets help.
   */
  void setHelp(Boolean help) {
    needHelp = help;
  }

  /**
   * Get status of the order.
   * @return String of the status E.g. Cooking
   */
  String getStatus() {
    return status;
  }

  /**
   * Set status of the order.
   * @param orderStatus String of the status and sets it.
   */
  void setStatus(String orderStatus) {
    status = orderStatus;
  }

  /**
   * Gets whether the table is active.
   * @return True if active, false otherwise.
   */
  Boolean isActive(){
    return active;
  }

  /**
   * Sets the boolean payment.
   * @param payment True if payment has been made, false otherwise.
   */
  void setPayment(Boolean payment) {
    this.payment = payment;
  }

  /**
   * Gets whether the payment has been made.
   * @return True if payment has been made, false otherwise.
   */
  public Boolean getPayment() {
    return payment;
  }

  /**
   * Compares orders by time putting earlier orders first when the list is sorted.
   */
  @Override
  public int compareTo(@NonNull Order order) {
    if (this.getTimeOfOrder().compareTo(order.getTimeOfOrder()) > 0) {
      return 1;
    } else if (this.getTimeOfOrder().compareTo(order.getTimeOfOrder()) < 0) {
      return -1;
    } else {
      return 0;
    }
  }
}
