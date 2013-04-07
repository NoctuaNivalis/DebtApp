package eu.pinnoo.debtapp.view;

import android.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import static android.content.Context.MODE_PRIVATE;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import com.google.analytics.tracking.android.EasyTracker;
import eu.pinnoo.debtapp.database.DAO;

/**
 *
 * @author see /AUTHORS
 */
public class MainActivity extends Activity {
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(eu.pinnoo.debtapp.R.layout.dashboard_layout);
        
        Button btn_add_debt = (Button) findViewById(eu.pinnoo.debtapp.R.id.btn_add_debt);
        Button btn_split_the_bill = (Button) findViewById(eu.pinnoo.debtapp.R.id.btn_split_the_bill);
        Button btn_user_review = (Button) findViewById(eu.pinnoo.debtapp.R.id.btn_user_review);
        Button btn_add_user = (Button) findViewById(eu.pinnoo.debtapp.R.id.btn_add_user);

        btn_add_debt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AddDebtActivity.class);
                startActivity(i);
            }
        });

        btn_split_the_bill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), SplitthebillActivity.class);
                startActivity(i);
            }
        });

        btn_user_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), UserReviewActivity.class);
                startActivity(i);
            }
        });

        btn_add_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AddUserActivity.class);
                startActivity(i);
            }
        });
        
        closeOrAskPassword();
    }
    
    private void closeOrAskPassword() {
        if (!isNetworkAvailable()) {
            showErrorDialogAndExit();
        } else {
            String password = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("password", "");
            if ("".equals(password)) {
                askForPassword("Password needed!");
            } else {
                DAO.getInstance().getPasswordModel().setPassword(password);
                new MainActivity.VerifyPassword().execute();
            }
        }
    }
    
    
    private void askForPassword(String title) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(title);
        alert.setMessage("Please enter the password of the database.");
        alert.setCancelable(false);
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input.setSelection(input.getText().length());
        alert.setView(input);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                DAO.getInstance().getPasswordModel().setPassword(value);

                getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                        .edit()
                        .putString("password", value)
                        .commit();

                new MainActivity.VerifyPassword().execute();

                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(input.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });
        
        alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                MainActivity.this.finish();
            }
        });
        
        AlertDialog dialog = alert.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        dialog.show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    private void showErrorDialogAndExit() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("No internet connection available!");
        alert.setMessage("No internet connection found. The app will close now.");
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                finish();
            }
        });
        alert.show();
    }
    
    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance().activityStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance().activityStop(this);
    }
    
    private class VerifyPassword extends AsyncTask<Void, Void, Integer> {

        private ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Verifying password...");
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected Integer doInBackground(Void... params) {
            //update ui
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            dialog.dismiss();
            if (!DAO.getInstance().getPasswordModel().passwordCorrect()) {
                askForPassword("Something went wrong!");
            }
            // update ui
            /*if (adapter != null) {
                Spinner spinner1 = (Spinner) findViewById(R.id.main_user_left_spinner);
                Spinner spinner2 = (Spinner) findViewById(R.id.main_user_right_spinner);
                updateSpinnerAdapters(spinner1, spinner2, adapter);
            }*/
        }
    }
}
