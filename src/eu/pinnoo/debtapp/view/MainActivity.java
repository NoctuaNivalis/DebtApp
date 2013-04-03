package eu.pinnoo.debtapp.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.google.analytics.tracking.android.EasyTracker;
import eu.pinnoo.debtapp.Debt;
import eu.pinnoo.debtapp.DecimalDigitsInputFilter;
import eu.pinnoo.debtapp.R;
import eu.pinnoo.debtapp.User;
import eu.pinnoo.debtapp.database.DAO;
import eu.pinnoo.debtapp.models.UserModel;
import static eu.pinnoo.debtapp.models.UserModel.DIRECTION.EAST;
import static eu.pinnoo.debtapp.models.UserModel.DIRECTION.WEST;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author see /AUTHORS
 */
public class MainActivity extends Activity {

    private UserModel usermodel;
    private DAO dao;
    private UserArrayAdapter adapter;
    private boolean activityStarted = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        dao = DAO.getInstance();
        checkNetworkConnection();
        usermodel = new UserModel(this);
        EditText amountEditText = (EditText) findViewById(R.id.main_amount);
        amountEditText.setFilters(new InputFilter[]{new DecimalDigitsInputFilter()});

        final Spinner spinner1 = (Spinner) findViewById(R.id.main_user_left_spinner);
        final Spinner spinner2 = (Spinner) findViewById(R.id.main_user_right_spinner);

        OnItemSelectedListener listener = new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> av, View view, int i, long l) {
                if (spinner1.getSelectedItemId() != 0 && spinner2.getSelectedItemId() != 0) {
                    refresh();
                }
            }

            public void onNothingSelected(AdapterView<?> av) {
            }
        };

        spinner1.setOnItemSelectedListener(listener);
        spinner2.setOnItemSelectedListener(listener);

        final TextView debtorlabel = (TextView) findViewById(R.id.main_user_right_label);
        final TextView creditorlabel = (TextView) findViewById(R.id.main_user_left_label);
        final Button switchbutton = (Button) findViewById(R.id.main_switch_button);
        switch (usermodel.getCurDir()) {
            case EAST:
                switchbutton.setBackgroundResource(R.drawable.forward);
                debtorlabel.setText("Creditor");
                creditorlabel.setText("Debtor");
                break;
            case WEST:
                switchbutton.setBackgroundResource(R.drawable.back);
                debtorlabel.setText("Debtor");
                creditorlabel.setText("Creditor");
                break;
        }
        switchbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                usermodel.switchDir();
                switch (usermodel.getCurDir()) {
                    case EAST:
                        switchbutton.setBackgroundResource(R.drawable.forward);
                        debtorlabel.setText("Creditor");
                        creditorlabel.setText("Debtor");
                        break;
                    case WEST:
                        switchbutton.setBackgroundResource(R.drawable.back);
                        debtorlabel.setText("Debtor");
                        creditorlabel.setText("Creditor");
                        break;
                }
                refresh();
            }
        });

        final Button clearbutton = (Button) findViewById(R.id.main_cancel);
        clearbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                clearFields();
            }
        });

        final Button applybutton = (Button) findViewById(R.id.main_apply);
        applybutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int tmpamount = 0;
                try {
                    tmpamount = (int) (100 * Double.parseDouble((((EditText) findViewById(R.id.main_amount)).getText().toString())));
                } catch (NumberFormatException e) {
                    return;
                }

                final String description = ((EditText) findViewById(R.id.main_description)).getText().toString();

                final int amount = tmpamount;

                if (amount == 0 || description == null || spinner1.getSelectedItemId() == 0 || spinner2.getSelectedItemId() == 0) {
                    return;
                }

                if (description.isEmpty()) {
                    AlertDialog.Builder alt_bld = new AlertDialog.Builder(MainActivity.this);
                    alt_bld.setMessage("Proceed without description?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            apply(amount, description);
                        }
                    })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alert = alt_bld.create();
                    alert.setTitle("Empty description!");
                    alert.show();
                } else {
                    apply(amount, description);
                }
            }

            private void apply(int amount, String description) {
                new ApplyAction(amount, description).execute();
            }
        });
        
        activityStarted = true;
    }

    private void refresh() {
        TableLayout table = (TableLayout) findViewById(R.id.main_table);
        table.removeViews(1, table.getChildCount() - 1);
        User debtor = usermodel.getDebtor();
        User creditor = usermodel.getCreditor();
        if (debtor == null || creditor == null) {
            return;
        }
        new LoadDebts(creditor, debtor).execute();
    }

    private void clearFields() {
        ((EditText) findViewById(R.id.main_amount)).setText("");
        ((EditText) findViewById(R.id.main_description)).setText("");
    }

    private void addTableRow(int amount, String description, int rowNumber) {
        LayoutInflater inflater = getLayoutInflater();
        TableLayout tl = (TableLayout) findViewById(R.id.main_table);
        TableRow tr = (TableRow) inflater.inflate(R.layout.main_table_row, tl, false);
        TextView label_amount = (TextView) tr.findViewById(R.id.main_row_amount);
        label_amount.setText(((double) amount) / 100 + "");
        label_amount.setPadding(1, 5, 5, 5);
        TextView label_description = (TextView) tr.findViewById(R.id.main_row_description);
        label_description.setText(description);
        label_description.setPadding(5, 5, 5, 5);

        if (rowNumber % 2 == 0) {
            tr.setBackgroundColor(Color.GRAY);
            label_amount.setTextColor(Color.RED);
            label_description.setTextColor(Color.BLACK);

        } else {
            tr.setBackgroundColor(Color.LTGRAY);
            label_amount.setTextColor(Color.RED);
            label_description.setTextColor(Color.BLACK);
        }

        tl.addView(tr);
    }

    private void updateItemsInUserSpinners() {
        List<User> userlist = dao.getUsers();
        if (userlist == null) {
            dao.getPasswordModel().setPasswordCorrect(false);
        } else {
            userlist.add(0, new User("Select"));
            dao.getPasswordModel().setPasswordCorrect(true);
            adapter = new UserArrayAdapter(this, userlist, android.R.layout.simple_list_item_1);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }
    }

    private void updateSpinnerAdapters(Spinner spinner1, Spinner spinner2, UserArrayAdapter adapter) {
        spinner1.setAdapter(adapter);
        spinner2.setAdapter(adapter);
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
                dao.getPasswordModel().setPassword(value);

                getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                        .edit()
                        .putString("password", value)
                        .commit();

                new VerifyPassword().execute();

                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(input.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });
        AlertDialog dialog = alert.create();
        dialog.setOnShowListener(new OnShowListener() {
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

    private void checkNetworkConnection() {
        if (!isNetworkAvailable()) {
            showErrorDialogAndExit();
        } else {
            String password = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("password", "");
            if ("".equals(password)) {
                askForPassword("Password needed!");
            } else {
                dao.getPasswordModel().setPassword(password);
                new VerifyPassword().execute();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.splitthebill:
                intent = new Intent(this, SplitthebillActivity.class);
                this.startActivity(intent);
                return true;
            case R.id.userreview:
                intent = new Intent(this, UserReviewActivity.class);
                this.startActivity(intent);
                return true;
            case R.id.adduser:
                intent = new Intent(this, AddUserActivity.class);
                this.startActivity(intent);
                return true;
            case R.id.refresh:
                updateItemsInUserSpinners();
                updateSpinnerAdapters((Spinner) findViewById(R.id.main_user_left_spinner), (Spinner) findViewById(R.id.main_user_right_spinner), adapter);
                refresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class VerifyPassword extends AsyncTask<Void, Void, Integer> {

        private ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Verifying password...");
            dialog.show();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            updateItemsInUserSpinners();
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            dialog.dismiss();
            if (!dao.getPasswordModel().passwordCorrect()) {
                askForPassword("Something went wrong!");
            }
            if (adapter != null) {
                Spinner spinner1 = (Spinner) findViewById(R.id.main_user_left_spinner);
                Spinner spinner2 = (Spinner) findViewById(R.id.main_user_right_spinner);
                updateSpinnerAdapters(spinner1, spinner2, adapter);
            }
        }
    }

    private class ApplyAction extends AsyncTask<Void, Void, Integer> {

        private ProgressDialog dialog = new ProgressDialog(MainActivity.this);
        private int amount;
        private String description;

        public ApplyAction(int amount, String description) {
            this.amount = amount;
            this.description = description;
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Applying...");
            dialog.show();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            User debtor = usermodel.getDebtor();
            User creditor = usermodel.getCreditor();

            if (((Switch) findViewById(R.id.main_typeSwitch)).isChecked()) {
                dao.payOffDebt(amount, description, creditor, debtor);
            } else {
                dao.addDebt(creditor, debtor,
                        new Debt(amount, description, creditor, debtor));
            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            dialog.dismiss();
            refresh();
            clearFields();
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private class LoadDebts extends AsyncTask<Void, Void, Integer> {

        private ProgressDialog dialog = new ProgressDialog(MainActivity.this);
        private User creditor;
        private User debtor;
        private List<Debt> debts;

        public LoadDebts(User creditor, User debtor) {
            this.creditor = creditor;
            this.debtor = debtor;
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Loading debts...");
            dialog.show();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            debts = dao.getDebts(creditor, debtor);
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            dialog.dismiss();
            if (debts == null || debts.isEmpty()) {
                ((TextView) MainActivity.this.findViewById(R.id.main_total)).setText("0");
                return;
            }
            Iterator<Debt> it = debts.iterator();
            int rowNumber = 0;
            int amount = 0;
            while (it.hasNext()) {
                Debt d = it.next();
                addTableRow(d.getAmount(), d.getDescription(), rowNumber);
                amount += d.getAmount();
                rowNumber++;
            }
            TextView totalamount = (TextView) MainActivity.this.findViewById(R.id.main_total);
            totalamount.setText(((double) amount) / 100 + "");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!activityStarted){
            return;
        }
        updateItemsInUserSpinners();
        updateSpinnerAdapters((Spinner) findViewById(R.id.main_user_left_spinner), (Spinner) findViewById(R.id.main_user_right_spinner), adapter);
        refresh();
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
}
