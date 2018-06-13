package group4.waiterapp;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.VIBRATOR_SERVICE;

/**
 * @author Pavan
 * Used to adapt list of orders to be shown in the titles fragment.
 */
public class OrderAdapter extends ArrayAdapter<Order> {

  /**
   * The context of the view.
   */
  private Context mContext;
  /**
   * List of orders to be displayed to the screen.
   */
  private List<Order> orderList = new ArrayList<>();
  /**
   * Used to get data from singleton class.
   */
  private Exchange sharedData;

  /**
   * Constructor sets the context, list and initialises sharedData variable.
   * @param context The context of the view.
   * @param list The list of orders to be displayed.
   */
  public OrderAdapter(@NonNull Context context, ArrayList<Order> list) {
    super(context, 0, list);
    mContext = context;
    orderList = list;
    sharedData = Exchange.getInstance();
  }

  /**
   * Used to notify the adapter that the data set has changed and to update the screen.
   */
  @Override
  public void notifyDataSetChanged() {
    super.notifyDataSetChanged();
    orderList = sharedData.getAllOrders();
  }

  /**
   * Creates the view inflating the xml layout and fills the view with data.
   * @param position position in the list of orders.
   * @param convertView view to display to the screen.
   * @param parent ViewGroup is the view you want to attach the Fragment to
   * @return Returns the view with data in it.
   */
  @NonNull
  @Override
  public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    View listItem = convertView;
    if (listItem == null)
      listItem = LayoutInflater.from(mContext).inflate(R.layout.list_order, parent, false);

    TextView time = listItem.findViewById(R.id.time);
    time.setText(orderList.get(position).getTimeOfOrder());

    TextView table = listItem.findViewById(R.id.order);
    String t = "Table " + String.valueOf(orderList.get(position).getTableNumber());
    table.setText(t);

    TextView status = listItem.findViewById(R.id.status);
    status.setText(orderList.get(position).getStatus());

    if (sharedData.getHelp(position)) {
      shakeItBaby();
      final Animation animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
      animation.setDuration(2000); // duration - half a second
      animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
      animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
      animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in
      listItem.startAnimation(animation);
    }

    return listItem;
  }

  /**
   * Vibrates the device when the waiter is notified by the customer.
   */
  private void shakeItBaby() {
    if (Build.VERSION.SDK_INT >= 26) {
      ((Vibrator) mContext.getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE));
    } else {
      ((Vibrator) mContext.getSystemService(VIBRATOR_SERVICE)).vibrate(150);
    }
  }
}
