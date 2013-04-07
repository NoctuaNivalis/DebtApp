package eu.pinnoo.debtapp.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
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
public class AddDebtActivity extends Activity {

    private UserModel usermodel;
    private DAO dao;
    private UserArrayAdapter adapter;
    private boolean activityStarted = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_debt);
        dao = DAO.getInstance();
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
                    AlertDialog.Builder alt_bld = new AlertDialog.Builder(AddDebtActivity.this);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_user_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.refresh:
                updateItemsInUserSpinners();
                updateSpinnerAdapters((Spinner) findViewById(R.id.main_user_left_spinner), (Spinner) findViewById(R.id.main_user_right_spinner), adapter);
                refresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class ApplyAction extends AsyncTask<Void, Void, Integer> {

        private ProgressDialog dialog = new ProgressDialog(AddDebtActivity.this);
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

        private ProgressDialog dialog = new ProgressDialog(AddDebtActivity.this);
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
                ((TextView) AddDebtActivity.this.findViewById(R.id.main_total)).setText("0");
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
            TextView totalamount = (TextView) AddDebtActivity.this.findViewById(R.id.main_total);
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
