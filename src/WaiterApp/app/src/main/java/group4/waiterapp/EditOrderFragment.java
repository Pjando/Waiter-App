package group4.waiterapp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import shared.Item;


/**
 * @author Pavan Jando
 *
 * A {@link Fragment} that displays the order with dropdown lists to edit the QUANTITIES.
 */
public class EditOrderFragment extends android.app.Fragment {

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
   * The different quantities that can be set to an item.
   */
  private  final String[] QUANTITIES = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
  /**
   * Spinner object that handles drop down lists.
   */
  private Spinner dropDown;
  /**
   * A list of spinners.
   */
  private List<Spinner> spinners;
  /**
   * String to set the title of the toolbar.
   */
  private final String TITLE = "Edit Order";

  /**
   * Necessary empty constructor.
   */
  public EditOrderFragment() {
  }

  /**
   * Gets the index of order.
   * @return index of order. E.g. 0 if order is first order int TitleFragment.
   */
  private int getShownIndex() {
    return getArguments().getInt("index", 0);
  }

  /**
   * Creates and shows the order details with drop down lists to change quantities.
   * @param inflater Layout inflater puts the Fragment on the screen.
   * @param container ViewGroup is the view you want to attach the Fragment to.
   * @param savedInstanceState Bundle stores key value pairs so that data can be saved.
   * @return The view that is displayed to the screen.
   */
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.edit_order, container, false);

    sharedData = Exchange.getInstance();
    food = sharedData.getOrder(getShownIndex()).getFood();
    spinners = new ArrayList<>();

    TextView title = view.findViewById(R.id.title);
    title.setText(TITLE);
    TableLayout tl = view.findViewById(R.id.detailsTable);
    // Set the padding to the TextView
    int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
        8, getActivity().getResources().getDisplayMetrics());

    //Dynamically add food items according to size of the order
    for (int i = 0; i < food.size(); i++) {
      // Make table row
      TableRow tr = new TableRow(view.getContext());
      tr.setId(100 + i);
      tr.setLayoutParams(new TableLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT));

      // Make Text view to hold the food item name
      TextView item = new TextView(view.getContext());
      item.setLayoutParams(new TableRow.LayoutParams(0, LayoutParams.MATCH_PARENT, 1f));
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
      price.setLayoutParams(new TableRow.LayoutParams(0, LayoutParams.MATCH_PARENT, 1f));
      price.setId(300 + i);
      price.setText(String.format("£%.2f", food.get(i).getPrice() * food.get(i).getQuantity()));
      price.setTextSize(TEXT_SIZE);
      price.setTextColor(Color.BLACK);
      price.setPadding(padding, padding, padding, padding);
      price.setGravity(Gravity.CENTER);
      //Add text view to the row
      tr.addView(price);

      //Drop down list to change the quantity of food item
      dropDown = new Spinner(view.getContext());
      dropDown.setLayoutParams(new TableRow.LayoutParams(0, LayoutParams.MATCH_PARENT, 1f));
      dropDown.setGravity(Gravity.CENTER);
      dropDown.setId(400 + i);

      ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, QUANTITIES);
      adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
      dropDown.setAdapter(adapter);

      ArrayAdapter myAdap = (ArrayAdapter) dropDown.getAdapter(); //cast to an ArrayAdapter
      int spinnerPosition = myAdap.getPosition(String.valueOf(food.get(i).getQuantity()));
      dropDown.setSelection(spinnerPosition);
      spinners.add(dropDown);
      //Add drop down to row
      tr.addView(dropDown);
      //Add row to table
      tl.addView(tr, new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    }

    FloatingActionButton confirmEdit = view.findViewById(R.id.confirmEdit);
    confirmEdit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Snackbar.make(view, "Order updated", Snackbar.LENGTH_LONG)
            .setAction("Action", null).show();

        for (int i = 0; i < spinners.size(); i++) {
          String text = spinners.get(i).getSelectedItem().toString();
          sharedData.getOrder(getShownIndex()).getFood().get(i).setQuantity(Integer.valueOf(text));
        }
        sharedData.updateOrder(getShownIndex());
        getActivity().onBackPressed();
      }
    });

    return view;
  }
}
