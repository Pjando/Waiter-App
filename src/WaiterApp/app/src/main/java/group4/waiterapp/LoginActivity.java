package group4.waiterapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import shared.Command;

import static java.lang.Thread.sleep;

/**
 * @author Pavan Jando
 *
 * A login screen that offers login via username/password.
 */
public class LoginActivity extends AppCompatActivity {

  /**
   * A dummy authentication store containing known user names and passwords.
   */
  private static final String[] DUMMY_CREDENTIALS = new String[]{
      "t:p", "Sean:password"
  };
  /**
   * Keep track of the login task to ensure we can cancel it if requested.
   */
  private UserLoginTask mAuthTask = null;

  /**
   * Text view used to enter username.
   */
  private AutoCompleteTextView mUsernameView;
  /**
   * Edit text view used to type password.
   */
  private EditText mPasswordView;
  private View mProgressView;
  private View mLoginFormView;

  /**
   * Connection object to connect to server.
   */
  private Connection con;
  /**
   * Used to get data from singleton class.
   */
  private Exchange sharedData;

  /**
   *  Creates connection object and starts the threads to connect to server.
   *  Initial commands get tables and show menu to get tables and the menu,
   * @param savedInstanceState Bundle stores key value pairs so that data can be saved.
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    if (!Connection.connectionMade) {
      Log.d("Hello", "Hello");
      con = new Connection();
      con.threadListen();
      try {
        //sleep(30000);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    Command cmd = new Command();
    Command cmd2 = new Command();
    cmd.setCommand("GET_TABLES");
    cmd.setTableNumber(1);
    cmd2.setCommand("SHOW_MENU_WAITER");
    cmd2.setTableNumber(1);
    sharedData = Exchange.getInstance();
    try {
      sharedData.addCommand(cmd);
      sleep(1000);
      sharedData.addCommand(cmd2);
      sleep(1000);
    } catch (Exception e) {
      e.printStackTrace();
    }

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    // Set up the login form.
    mUsernameView = findViewById(R.id.username);

    mPasswordView = findViewById(R.id.password);
    mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
        if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
          attemptLogin();
          return true;
        }
        return false;
      }
    });

    Button mUsernameSignInButton = findViewById(R.id.username_sign_in_button);
    mUsernameSignInButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        attemptLogin();
      }
    });

    mLoginFormView = findViewById(R.id.login_form);
    mProgressView = findViewById(R.id.login_progress);
  }

  /**
   * Attempts to sign in or register the account specified by the login form.
   * If there are form errors (invalid username, missing fields, etc.), the
   * errors are presented and no actual login attempt is made.
   */
  private void attemptLogin() {
    if (mAuthTask != null) {
      return;
    }

    // Reset errors.
    mUsernameView.setError(null);
    mPasswordView.setError(null);

    // Store values at the time of the login attempt.
    String username = mUsernameView.getText().toString();
    String password = mPasswordView.getText().toString();

    boolean cancel = false;
    View focusView = null;

    // Check for a valid password, if the user entered one.
    if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
      mPasswordView.setError(getString(R.string.error_invalid_password));
      focusView = mPasswordView;
      cancel = true;
    }

    // Check for a valid username.
    if (TextUtils.isEmpty(username)) {
      mUsernameView.setError(getString(R.string.error_field_required));
      focusView = mUsernameView;
      cancel = true;
    } else if (!isUsernameValid(username)) {
      mUsernameView.setError(getString(R.string.error_invalid_username));
      focusView = mUsernameView;
      cancel = true;
    }

    if (cancel) {
      // There was an error; don't attempt login and focus the first
      // form field with an error.
      focusView.requestFocus();
    } else {
      // Show a progress spinner, and kick off a background task to
      // perform the user login attempt.
      showProgress(true);
      mAuthTask = new UserLoginTask(username, password);
      mAuthTask.execute((Void) null);
    }
  }

  /**
   * Checks whether the username entered is valid.
   * @param username String username entered by user.
   * @return True if valid, false if not valid.
   */
  private boolean isUsernameValid(String username) {
    return true;
  }

  /**
   * Checks whether the password entered is valid.
   * @param password String password entered by user.
   * @return True if password is longer than length 0.
   */
  private boolean isPasswordValid(String password) {
    return password.length() > 0;
  }

  /**
   * Shows the progress UI and hides the login form.
   */
  @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
  private void showProgress(final boolean show) {
    // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
    // for very easy animations. If available, use these APIs to fade-in
    // the progress spinner.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
      int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

      mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
      mLoginFormView.animate().setDuration(shortAnimTime).alpha(
          show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
          mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
      });

      mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
      mProgressView.animate().setDuration(shortAnimTime).alpha(
          show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
          mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
      });
    } else {
      // The ViewPropertyAnimator APIs are not available, so simply show
      // and hide the relevant UI components.
      mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
      mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
    }
  }

  /**
   * Represents an asynchronous login/registration task used to authenticate
   * the user.
   */
  public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

    private final String mUsername;
    private final String mPassword;

    UserLoginTask(String username, String password) {
      mUsername = username;
      mPassword = password;
    }

    @Override
    protected Boolean doInBackground(Void... params) {

      for (String credential : DUMMY_CREDENTIALS) {
        String[] pieces = credential.split(":");
        if (pieces[0].equals(mUsername)) {
          // Account exists, return true if the password matches.
          return pieces[1].equals(mPassword);
        }
      }
      return false;
    }

    /**
     * Executed when the activity is ending. Starts the Fragment layout activity.
     * @param success whether the login details where correct or not.
     */
    @Override
    protected void onPostExecute(final Boolean success) {
      mAuthTask = null;
      showProgress(false);
      if (success) {
        sharedData.defaultTables();
        finish();
        Intent myIntent = new Intent(LoginActivity.this, FragmentLayout.class);
        LoginActivity.this.startActivity(myIntent);
      } else {
        mPasswordView.setError(getString(R.string.error_incorrect_password));
        mPasswordView.requestFocus();
      }
    }

    @Override
    protected void onCancelled() {
      mAuthTask = null;
      showProgress(false);
    }
  }
}

