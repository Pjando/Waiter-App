package group4.waiterapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import shared.Command;

import static java.lang.Thread.sleep;

/**
 * @author Pavan Jando
 *
 * A fragment representing a list of Items.
 */
public class MenuFragment extends android.app.Fragment {

  /**
   * The tag used to get the column count.
   */
  private static final String ARG_COLUMN_COUNT = "column-count";
  /**
   * Indicates how many columns should be displayed.
   */
  private int mColumnCount = 1;
  /**
   * Used to get data from singleton class.
   */
  private Exchange sharedData;

  /**
   * Mandatory empty constructor for the fragment manager to instantiate the
   * fragment (e.g. upon screen orientation changes).
   */
  public MenuFragment() {
  }

  /**
   * On creation of the fragment a command is sent to the server for the menu.
   * @param savedInstanceState Bundle stores key value pairs so that data can be saved.
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

    if (getArguments() != null) {
      mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
    }
  }

  /**
   * A view of the menu with toggle buttons to set which items are available.
   * @param inflater Layout inflater puts the Fragment on the screen.
   * @param container ViewGroup is the view you want to attach the Fragment to
   * @param savedInstanceState Bundle stores key value pairs so that data can be saved
   * @return The view that is displayed to the screen.
   */
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.view_menu_list, container, false);

    // Set the adapter
    if (view instanceof RecyclerView) {
      Context context = view.getContext();
      RecyclerView recyclerView = (RecyclerView) view;
      if (mColumnCount <= 1) {
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
      } else {
        recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
      }
      recyclerView.setAdapter(new MyItemRecyclerViewAdapter(sharedData.getMenu(), true));
    }
    return view;
  }
}
