package group4.waiterapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import shared.Command;

/**
 * @author Pavan Jando
 *
 * The initial activity after logging in that initiates the Title Fragment.
 */
public class FragmentLayout extends AppCompatActivity {

  private Exchange sharedData;

  /**
   * Sets the content view to the xml layout fragment layout,
   * which initiates the fragment layout xml.
   * @param savedInstanceState Bundle stores key value pairs so that data can be saved.
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.fragment_layout);
  }

  /**
   * States that on back press in titles fragment that the login activity is restarted.
   */
  @Override
  public void onBackPressed() {
    sharedData = Exchange.getInstance();
    Log.d("Disconnect", "DISCONNECTED");
    try {
      Connection.sendToServer("DISCONNECTED:waiter:");
    } catch (Exception e) {
      e.printStackTrace();
    }
    sharedData.reset();
    Intent myIntent = new Intent(FragmentLayout.this, LoginActivity.class);
    FragmentLayout.this.startActivity(myIntent);
  }
}

