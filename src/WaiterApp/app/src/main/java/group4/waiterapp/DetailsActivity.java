package group4.waiterapp;

import android.app.Activity;
import android.os.Bundle;

/**
 * @author Pavan Jando
 *
 * This activity manages the details, add items, and edit order fragments.
 */
public class DetailsActivity extends Activity {
  /**
   * Creates the details fragment on creation of the activity.
   * @param savedInstanceState the previous state of the fragment.
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Check if we have any order data saved
    if (savedInstanceState == null) {

      // If not then create the DetailsFragment
      DetailsFragment details = new DetailsFragment();

      // Get the Bundle of key value pairs and assign them to the DetailsFragment
      details.setArguments(getIntent().getExtras());

      // Add the details Fragment
      getFragmentManager().beginTransaction().add(android.R.id.content, details).commit();
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
}