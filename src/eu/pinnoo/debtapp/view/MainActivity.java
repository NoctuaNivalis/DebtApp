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
import eu.pinnoo.debtapp.Debt;
import eu.pinnoo.debtapp.R;
import eu.pinnoo.debtapp.User;
import eu.pinnoo.debtapp.database.DAO;
import eu.pinnoo.debtapp.models.PasswordModel;
import eu.pinnoo.debtapp.models.UserModel;
import static eu.pinnoo.debtapp.models.UserModel.DIRECTION.EAST;
import static eu.pinnoo.debtapp.models.UserModel.DIRECTION.WEST;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends Activity {

    private UserModel usermodel;
    private DAO dao;
    private UserArrayAdapter adapter;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        checkNetworkConnection();
        usermodel = new UserModel(this);
        dao = DAO.getInstance();

        final Spinner spinner1 = (Spinner) findViewById(R.id.spinner1);
        final Spinner spinner2 = (Spinner) findViewById(R.id.spinner2);

        OnItemSelectedListener listener = new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> av, View view, int i, long l) {
                refresh();
            }

            public void onNothingSelected(AdapterView<?> av) {
            }
        };

        spinner1.setOnItemSelectedListener(listener);
        spinner2.setOnItemSelectedListener(listener);

        final TextView debtorlabel = (TextView) findViewById(R.id.debtor);
        final TextView creditorlabel = (TextView) findViewById(R.id.creditor);
        final Button switchbutton = (Button) findViewById(R.id.btnSwitch);
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

        final Button clearbutton = (Button) findViewById(R.id.cancel);
        clearbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                clearFields();
            }
        });

        final Button applybutton = (Button) findViewById(R.id.ok);
        applybutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boolean valid = true;
                double tmpamount = 0;
                try {
                    tmpamount = Double.parseDouble((((EditText) findViewById(R.id.amount_edittext)).getText().toString()));
                } catch (NumberFormatException e) {
                    return;
                }

                final String description = ((EditText) findViewById(R.id.description_edittext)).getText().toString();

                final double amount = tmpamount;

                if (amount == 0 || description == null) {
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

            private void apply(double amount, String description) {
                User debtor = usermodel.getDebtor();
                User creditor = usermodel.getCreditor();

                if (((Switch) findViewById(R.id.switcher)).isChecked()) {
                    dao.payOffDebt(amount, description, creditor, debtor);
                } else {
                    dao.addDebt(creditor, debtor,
                            new Debt(amount, description, creditor, debtor));
                }

                refresh();

                clearFields();

                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });
    }

    private void refresh() {
        TableLayout table = (TableLayout) findViewById(R.id.main_table);
        table.removeViews(1, table.getChildCount() - 1);
        User debtor = usermodel.getDebtor();
        User creditor = usermodel.getCreditor();
        if (debtor == null || creditor == null) {
            return;
        }
        List<Debt> debts = dao.getDebts(creditor, debtor);
        if (debts == null || debts.isEmpty()) {
            ((TextView) this.findViewById(R.id.totalamount)).setText("0");
            return;
        }
        Iterator<Debt> it = debts.iterator();
        int rowNumber = 0;
        double amount = 0;
        while (it.hasNext()) {
            Debt d = it.next();
            addTableRow(d.getAmount(), d.getDescription(), rowNumber);
            amount += d.getAmount();
            rowNumber++;
        }
        TextView totalamount = (TextView) this.findViewById(R.id.totalamount);
        totalamount.setText(amount + "");
    }

    private void clearFields() {
        ((EditText) findViewById(R.id.amount_edittext)).setText("");
        ((EditText) findViewById(R.id.description_edittext)).setText("");
    }

    private void addTableRow(double amount, String description, int rowNumber) {
        LayoutInflater inflater = getLayoutInflater();
        TableLayout tl = (TableLayout) findViewById(R.id.main_table);
        TableRow tr = (TableRow) inflater.inflate(R.layout.table_row, tl, false);
        TextView label_amount = (TextView) tr.findViewById(R.id.amount);
        label_amount.setText(amount + "");
        label_amount.setPadding(1, 5, 5, 5);
        TextView label_description = (TextView) tr.findViewById(R.id.description);;
        label_description.setText(description);
        label_description.setPadding(5, 5, 5, 5);
        //Set colors of background and colors of font sizes
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
        Spinner spinner1 = (Spinner) findViewById(R.id.spinner1);
        Spinner spinner2 = (Spinner) findViewById(R.id.spinner2);
        List<User> userlist = dao.getUsers();
        if (userlist == null) {
            dao.getPasswordModel().setPasswordCorrect(false);
        } else {
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
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input.setSelection(input.getText().length());
        alert.setView(input);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                dao.getPasswordModel().setPassword(value);
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
            askForPassword("Password needed!");
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
        // Handle item selection
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
                updateSpinnerAdapters((Spinner) findViewById(R.id.spinner1), (Spinner) findViewById(R.id.spinner2), adapter); 
                refresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class VerifyPassword extends AsyncTask<Void, Void, Integer> {

        private ProgressDialog Dialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Verifying password...");
            Dialog.show();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            updateItemsInUserSpinners();
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            Dialog.dismiss();
            if (!dao.getPasswordModel().passwordCorrect()) {
                askForPassword("Something went wrong!");
            }
            if (adapter != null) {
                Spinner spinner1 = (Spinner) findViewById(R.id.spinner1);
                Spinner spinner2 = (Spinner) findViewById(R.id.spinner2);
                updateSpinnerAdapters(spinner1, spinner2, adapter);
            }
        }
    }
}
