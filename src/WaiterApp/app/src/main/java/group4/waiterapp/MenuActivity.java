package group4.waiterapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import shared.Command;

/**
 * @author Pavan Jando
 *
 * The activity that handles the Menu fragment.
 */
public class MenuActivity extends AppCompatActivity {
  /**
   * Creates the menu fragment on creation of the activity.
   * @param savedInstanceState Bundle stores key value pairs so that data can be saved.
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Check if we have any order data saved
    if (savedInstanceState == null) {

      // If not then create the DetailsFragment
      MenuFragment menu = new MenuFragment();

      // Get the Bundle of key value pairs and assign them to the DetailsFragment
      menu.setArguments(getIntent().getExtras());

      // Add the details Fragment
      getFragmentManager().beginTransaction().add(android.R.id.content, menu).commit();
    }
  }
  /**
   * Indicates that when the back button is pressed, to load the previous fragment.
   */
  @Override
  public void onBackPressed() {
    int count = getFragmentManager().getBackStackEntryCount();

    if (count == 0) {
      super.onBackPressed();
      //additional code
    } else {
      getFragmentManager().popBackStack();
    }
  }

  /**
   * When we leave the activity the it is paused, on pause send the new menu to the server.
   */
  @Override
  protected void onPause() {
    super.onPause();
    Exchange sharedData = Exchange.getInstance();
    Command cmd = new Command();
    cmd.setCommand("SET_MENU");
    cmd.setItems(sharedData.getMenu());
    try {
      sharedData.addCommand(cmd);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
