package group4.waiterapp;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.ebanx.swipebtn.OnStateChangeListener;
import com.ebanx.swipebtn.SwipeButton;

import java.util.List;

import shared.Command;
import shared.Item;

/**
 * @author Pavan Jando
 *
 * A {@link Fragment} to display the details of an order.
 */
public class DetailsFragment extends Fragment {

  /**
   * The status' that the waiter can set an order to.
   */
  private static final String[] STATUS = {"Confirmation Required", "Delivered", "Awaiting payment"};

  private final String TITLE = "Order Details";
  /**
   * Size of text used to display information.
   */
  private final int TEXT_SIZE = 20;
  /**
   * Used to get data from singleton class.
   */
  private Exchange sharedData;
  /**
   * List of items relevant to the current order being displayed.
   */
  private List<Item> food;

  /**
   * Necessary empty constructor.
   */
  public DetailsFragment() {
  }

  /**
   * Gets the index of order.
   * @return index of order. E.g. 0 if order is first order int TitleFragment.
   */
  public int getShownIndex() {
    // Returns the index assigned
    return getArguments().getInt("index", 0);
  }

  /**
   * Creates and returns the view of the order details.
   * @param inflater Layout inflater puts the Fragment on the screen.
   * @param container ViewGroup is the view you want to attach the Fragment to.
   * @param savedInstanceState Bundle stores key value pairs so that data can be saved
   * @return The view that is displayed to the screen.
   */
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           final Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.order_layout, container, false);


    TextView title = view.findViewById(R.id.title);
    title.setText(TITLE);
    sharedData = Exchange.getInstance();

    sharedData.setHelp(getShownIndex(), false);

    TableLayout tl = view.findViewById(R.id.detailsTable);

    // Set the padding to the TextView
    int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
        8, getActivity().getResources().getDisplayMetrics());

    food = sharedData.getOrder(getShownIndex()).getFood();
    //Dynamically add food items according to size of the order
    for (int i = 0; i < food.size(); i++) {

      // Make table row
      TableRow tr = new TableRow(view.getContext());
      tr.setId(100 + i);
      tr.setLayoutParams(new TableLayout.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT));

      // Make Text view to hold the food item name
      TextView item = new TextView(view.getContext());
      item.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1f));
      item.setId(200 + i);
      item.setText(food.get(i).getName());
      item.setTextSize(TEXT_SIZE);
      item.setTextColor(Color.BLACK);
      item.setPadding(padding, padding, padding, padding);
      item.setGravity(Gravity.CENTER);
      //Add text view to the row
      tr.addView(item);

      // Make Text view to hold the price of item
      TextView price = new TextView(view.getContext());
      price.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1f));
      price.setId(300 + i);
      price.setText(String.format("£%.2f", food.get(i).getPrice() * food.get(i).getQuantity()));
      price.setTextSize(TEXT_SIZE);
      price.setTextColor(Color.BLACK);
      price.setPadding(padding, padding, padding, padding);
      price.setGravity(Gravity.CENTER);
      //Add text view to the row
      tr.addView(price);

      TextView quantity = new TextView(view.getContext());
      quantity.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1f));
      quantity.setId(300 + i);
      quantity.setText(String.valueOf(food.get(i).getQuantity()));
      quantity.setTextSize(TEXT_SIZE);
      quantity.setTextColor(Color.BLACK);
      quantity.setPadding(padding, padding, padding, padding);
      quantity.setGravity(Gravity.CENTER);
      //Add text view to the row
      tr.addView(quantity);

      tl.addView(tr, new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
    }

    final Spinner dropDown = view.findViewById(R.id.status);
    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, STATUS);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    dropDown.setAdapter(adapter);

    ArrayAdapter myAdap = (ArrayAdapter) dropDown.getAdapter(); //cast to an ArrayAdapter
    int spinnerPosition = myAdap.getPosition(sharedData.getOrder(getShownIndex()).getStatus());
    dropDown.setSelection(spinnerPosition);


    dropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        sharedData.getOrder(getShownIndex()).setStatus(dropDown.getSelectedItem().toString());
        if (dropDown.getSelectedItem().toString().equals("Delivered")) {
          Log.d("Delivered", "Delivered");
          Command cmd = new Command();
          cmd.setTableNumber(sharedData.getOrder(getShownIndex()).getTableNumber());
          cmd.setCommand("ORDER_DELIVERED");
          cmd.setItems(food);
          sharedData.addCommand(cmd);
          getActivity().onBackPressed();
          sharedData.resetOrder(getShownIndex());
        }
      }

      public void onNothingSelected(AdapterView<?> adapterView) {
      }
    });

    final SwipeButton confirmOrder = view.findViewById(R.id.confirmOrder);
    confirmOrder.setOnStateChangeListener(new OnStateChangeListener() {
      @Override
      public void onStateChange(boolean active) {
        Toast.makeText(getActivity(), "Order sent to Kitchen", Toast.LENGTH_LONG).show();
        Command cmd = new Command(sharedData
            .getOrder(getShownIndex()).getTableNumber(), sharedData.getOrderTime(getShownIndex()));
        cmd.setCommand("CONFIRMED_ORDER");
        cmd.setItems(food);
        sharedData.addCommand(cmd);
        getActivity().onBackPressed();
      }
    });

    Button cancelOrder = view.findViewById(R.id.cancelOrder);
    cancelOrder.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Toast.makeText(getActivity(), "No items to remove", Toast.LENGTH_LONG).show();
        Command cmd = new Command();
        cmd.setTableNumber(sharedData.getOrder(getShownIndex()).getTableNumber());
        cmd.setCommand("CANCEL_ORDER");
        cmd.setItems(food);
        sharedData.addCommand(cmd);
        getActivity().onBackPressed();
        sharedData.resetOrder(getShownIndex());
      }
    });

    Button editOrder = view.findViewById(R.id.editOrder);
    editOrder.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (sharedData.getOrder(getShownIndex()).getFood().size() != 0) {
          EditOrderFragment editOrderFragment = new EditOrderFragment();
          editOrderFragment.setArguments(getActivity().getIntent().getExtras());
          getFragmentManager().beginTransaction()
              .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
              .replace(android.R.id.content, editOrderFragment).addToBackStack(null).commit();
        } else {
          Toast.makeText(getActivity(), "No items to remove", Toast.LENGTH_LONG).show();
        }
      }
    });

    Button addItems = view.findViewById(R.id.addItems);
    addItems.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        AddItemsFragment addItemsFragment = new AddItemsFragment();
        addItemsFragment.setArguments(getActivity().getIntent().getExtras());
        getFragmentManager().beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .replace(android.R.id.content, addItemsFragment).addToBackStack(null).commit();
      }
    });

    return view;
  }
}