package group4.waiterapp;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import shared.Item;

/**
 * @author Pavan
 *
 * Adapter used to take list and customise the appearance for each item.
 */
public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<ViewHolder> {

  /**
   * List of items to be displayed.
   */
  private final List<Item> values;
  /**
   * Used to get data from singleton class.
   */
  private Exchange sharedData;
  /**
   * True if the parent is the menu fragment, false if add items fragment.
   */
  private boolean isMenuFrag;
  /**
   * List of view holders.
   */
  private List<ViewHolder> viewHolders = new ArrayList<>();

  /**
   * Constructor initialises values and sets quantities to 0 by default (for add items fragment)
   * @param items Used to initialise the values list.
   * @param fragment tells adapter which fragment called it.
   */
  public MyItemRecyclerViewAdapter(List<Item> items, boolean fragment) {
    values = items;
    for (Item i : items) {
      i.setQuantity(0);
    }
    isMenuFrag = fragment;
    sharedData = Exchange.getInstance();
  }

  /**
   * Creates view holder and inflates the relevant layout according to MenuFrag.
   * @param parent ViewGroup is the view you want to attach the Fragment to.
   * @param viewType Indicates the type of view.
   * @return Creates new view holder with the view and MenuFrag.
   */
  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view;
    if (isMenuFrag) {
      view = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.view_menu_item, parent, false);
    } else {
      view = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.add_items_text, parent, false);
    }
    return new ViewHolder(view, isMenuFrag);
  }

  /**
   * When a view holder is bound, set the details to show in the holder.
   * @param holder ViewHolder that contains the information of each item.
   * @param position position of the view holder in the list.
   */
  @Override
  public void onBindViewHolder(final ViewHolder holder, final int position) {
    holder.mItem = values.get(position);
    holder.idView.setText(String.valueOf(values.get(position).getID()));
    holder.contentView.setText(values.get(position).getName());

    if (isMenuFrag) {
      if (values.get(0).getStock() > 0) {
        holder.toggleButton.setChecked(true);
      } else {
        holder.toggleButton.setChecked(false);
      }
      holder.toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
          if (b) {
            sharedData.getMenu().get(position).setStock(10);
          } else {
            sharedData.getMenu().get(position).setStock(0);
          }
        }
      });
    } else {
      String stringAmount = "x " + String.valueOf(holder.getAmount());
      holder.quantity.setText(stringAmount);
      holder.minus.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          holder.setAmount(holder.getAmount() - 1);
          notifyDataSetChanged();
        }
      });
      holder.plus.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          holder.setAmount(holder.getAmount() + 1);
          notifyDataSetChanged();
        }
      });
    }
    viewHolders.add(holder);
  }

  /**
   * Get the size of the list.
   * @return int size of the list.
   */
  @Override
  public int getItemCount() {
    return values.size();
  }

  /**
   * Get the list of additions to add to the current order.
   * @return List of type items.
   */
  List<Item> getAdditions() {
    List<Item> newItems = new ArrayList<>();
    for (ViewHolder v : viewHolders) {
      if (v.getAmount() > 0) {
        newItems.add(v.getItem());
      }
    }
    //Remove duplicate items
    Set<Item> hsi = new HashSet<>();

    hsi.addAll(newItems);
    newItems.clear();
    newItems.addAll(hsi);
    Log.d("food", newItems.toString());
    return newItems;
  }
}

/**
 * The custom view holder holding details about each item.
 */
class ViewHolder extends RecyclerView.ViewHolder {

  /**
   * The view that is being used.
    */
  public final View view;
  /**
   * Text view for the id of the item.
   */
  public final TextView idView;
  /**
   * Text view for the name of the food item.
   */
  public final TextView contentView;
  /**
   * Text view for the quantity of the item to be added.
   */
  public final TextView quantity;
  /**
   * Toggle button used to indicate whether the food is available.
   */
  public final ToggleButton toggleButton;
  /**
   * Button to subtract 1 from the quantity.
   */
  public final Button minus;
  /**
   * Button to add 1 to the quantity.
   */
  public final Button plus;
  /**
   * The item that is being displayed.
   */
  public Item mItem;


  /**
   * Constructor inititalise view holder variables according to the fragment caller isMenuFrag.
   * @param view view used to show details to the screen.
   * @param isMenuFrag true if caller is menu fragment, false if add items fragment.
   */
  public ViewHolder(View view, boolean isMenuFrag) {
    super(view);
    this.view = view;
    idView = view.findViewById(R.id.id);
    contentView = view.findViewById(R.id.content);
    if (isMenuFrag) {
      toggleButton = view.findViewById(R.id.availability);
      minus = null;
      plus = null;
      quantity = null;
    } else {
      minus = view.findViewById(R.id.minus);
      plus = view.findViewById(R.id.plus);
      quantity = view.findViewById(R.id.quantity);
      toggleButton = null;
    }
  }

  /**
   * Get the quantity of the item.
   * @return int quanitut of the item.
   */
  int getAmount() {
    return mItem.getQuantity();
  }

  /**
   * Sets the quantity of the item if it is greater than 0.
   * @param amount amount to be added or subtracted.
   */
  void setAmount(int amount) {
    if (mItem.getQuantity() + amount > 0) {
    mItem.setQuantity(amount);
    }
  }

  /**
   * Gets the item and its new quantity.
   * @return Item variable.
   */
  public Item getItem() {
    return mItem;
  }

  @Override
  public String toString() {
    return super.toString() + " '" + contentView.getText() + "'";
  }
}

