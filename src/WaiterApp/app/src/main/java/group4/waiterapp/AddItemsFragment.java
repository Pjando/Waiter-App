package group4.waiterapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import shared.Command;
import shared.Item;

import static java.lang.Thread.sleep;

/**
 * @author Pavan Jando
 *
 * A {@link Fragment} to display the menu when adding items to an order.
 */
public class AddItemsFragment extends android.app.Fragment {

  /**
   * Used to get data from singleton class.
   */
  private Exchange sharedData;
  /**
   * Adapter used to format the list items and attach to the view.
   */
  private MyItemRecyclerViewAdapter adapter;

  /**
   * Necessary empty constructor.
   */
  public AddItemsFragment() {
  }

  /**
   * Gets the index of order.
   * @return index of order. E.g. 0 if order is first order int TitleFragment.
   */
  public int getShownIndex() {
    return getArguments().getInt("index", 0);
  }

  /**
   * Code that is executed usually data related before the view is created.
   * Command is sent for the menu and initialises adapter the the menu.
   * @param savedInstanceState the previous state of the fragment.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Command cmd = new Command();
    cmd.setCommand("SHOW_MENU_WAITER");
    cmd.setTableNumber(1);
    sharedData = Exchange.getInstance();
    try {
      sharedData.addCommand(cmd);
      sleep(250);
    } catch (Exception e) {
      e.printStackTrace();
    }
    List<Item> temp = new ArrayList<>();
    temp.addAll(sharedData.getMenu());
    temp.removeAll(sharedData.getOrder(getShownIndex()).getFood());
    Log.d("menus", sharedData.getMenu().toString());
    adapter = new MyItemRecyclerViewAdapter(temp, false);
  }

  /**
   * Creates the view to be displayed on screen.
   * Creates a recycler view which has the list of items.
   * Listener for updating the order.
   * @param inflater used to inflate a layout xml.
   * @param container the container for the layout.
   * @param savedInstanceState the previous state of the fragment
   * @return the view to be displayed to the screen
   */
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.add_items_list, container, false);
    TextView title = view.findViewById(R.id.title);
    title.setText("Add to Order");

    // Set the adapter
    RecyclerView recyclerView = view.findViewById(R.id.list);
    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    recyclerView.setAdapter(adapter);

    FloatingActionButton confirmAdditions = view.findViewById(R.id.confirmAdditions);
    confirmAdditions.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Snackbar.make(view, "Order updated", Snackbar.LENGTH_LONG)
            .setAction("Action", null).show();
        sharedData.addNewItems(getShownIndex(), adapter.getAdditions());
        getActivity().onBackPressed();
      }
    });

    return view;
  }
}
