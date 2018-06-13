package group4.waiterapp;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * @author Pavan
 * List Fragment that displays the orders to the screen.
 */
public class TitlesFragment extends ListFragment {
  /**
   * Used to get data from singleton class.
   */
  private Exchange sharedData;
  /**
   * Used to adapt list into a viewable state.
   */
  private OrderAdapter stringList;
  /**
   * Handler used to refresh the screen automatically.
   */
  Handler handler = new Handler();
  /**
   * Controls the automatic update runnable, only works on this fragment.
   */
  private Boolean activeFragment;

  /**
   * On resuming the fragment make this active fragment start automatic update.
   */
  @Override
  public void onResume() {
    super.onResume();
    activeFragment = true;
    handler.postDelayed(runnable, 3000);
  }

  /**
   * Make this the active fragment on creation.
   * @param savedInstanceState Bundle stores key value pairs so that data can be saved.
   */
  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    activeFragment = true;
  }

  /**
   * Creates the adapter and sets it in the view. Set up listeners for buttons.
   * @param savedInstanceState Bundle stores key value pairs so that data can be saved.
   */
  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    sharedData = Exchange.getInstance();

    super.onActivityCreated(savedInstanceState);

    // An ArrayAdapter connects the array to our ListView
    // getActivity() returns a Context so we have the resources needed
    // We pass a default list item text view to put the data in and the
    // array
    stringList = new OrderAdapter(
        getActivity(), (ArrayList<Order>) sharedData.getAllOrders());

    // Connect the ListView to our data
    setListAdapter(stringList);

    Button changeMenu = getActivity().findViewById(R.id.change_menu);
    changeMenu.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        // Launch a new Activity to show our DetailsFragment
        Intent intent = new Intent();

        // Define the class Activity to call
        intent.setClass(getActivity(), MenuActivity.class);

        activeFragment = false;

        // Call for the Activity to open
        startActivity(intent);
      }
    });

    Button refresh = getActivity().findViewById(R.id.refresh);
    refresh.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        stringList.notifyDataSetChanged();
      }
    });
  }

  // When a list item is clicked we want to change the order info

  /**
   * When a list item is clicked the details of the order are shown.
   * @param l The relevant list view that has been clicked.
   * @param v The view being interacted with.
   * @param position The integer position of the list view.
   * @param id The id of the list view.
   */
  @Override
  public void onListItemClick(ListView l, View v, int position, long id) {
    if (sharedData.isOrderActive(position)) {
      showDetails(position);
      v.clearAnimation();
    } else {
      Snackbar.make(v, "No order to view", Snackbar.LENGTH_LONG)
          .setAction("Action", null).show();
    }
  }

  /**
   * Starts the details activity, showing the details to the screen.
   * @param index the position of the list view.
   */
  public void showDetails(int index) {
    // Launch a new Activity to show our DetailsFragment
    Intent intent = new Intent();

    // Define the class Activity to call
    intent.setClass(getActivity(), DetailsActivity.class);

    // Pass along the currently selected index assigned to the keyword index
    intent.putExtra("index", index);

    activeFragment = false;
    // Call for the Activity to open
    startActivity(intent);
  }

  /**
   * Runnable which updates the data set of the adapter every 3 seconds.
   */
  private Runnable runnable = new Runnable() {
    @Override
    public void run() {
      stringList.notifyDataSetChanged();
      Log.d("Running", "hello");
      if (activeFragment) {
        handler.postDelayed(runnable, 3000);
      }
    }
  };
}