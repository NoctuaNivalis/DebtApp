package eu.pinnoo.debtapp.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import eu.pinnoo.debtapp.Debt;
import eu.pinnoo.debtapp.R;
import eu.pinnoo.debtapp.User;
import eu.pinnoo.debtapp.database.DAO;
import java.util.List;

/**
 *
 * @author Eveline Hoogstoel <eveline.hoogstoel@ugent.be>
 */
public class SplitthebillActivity extends Activity {

    private List<User> userlist;
    private Spinner spinner;

    @Override
    public void onCreate(Bundle savendInstanceState) {
        super.onCreate(savendInstanceState);
        setContentView(R.layout.split_the_bill);
        final TextView payerlabel = (TextView) findViewById(R.id.payer);
        payerlabel.setText("Payer"); //of in xml omdat dit toch niet verandert?
        final TextView debtorslabel = (TextView) findViewById(R.id.debtors);
        debtorslabel.setText("Debtors:");

        spinner = (Spinner) findViewById(R.id.spinner3);
        updateSpinnerItems();

        final ListView lv = (ListView) findViewById(R.id.debtorslist);
        lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, android.R.id.text1, getUserList()));
        
        final Button okButton = (Button) findViewById(R.id.okbutton);
        okButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                boolean valid = true;
                double tmpamount = 0;
                try {
                    tmpamount = Double.parseDouble((((EditText) findViewById(R.id.stb_amount)).getText().toString()));
                } catch (NumberFormatException e) {
                    return;
                }

                final String description = ((EditText) findViewById(R.id.stb_desc)).getText().toString();

                final double amount = tmpamount;

                if (amount == 0 || description == null) {
                    return;
                }

                if (description.isEmpty()) {
                    AlertDialog.Builder alt_bld = new AlertDialog.Builder(SplitthebillActivity.this);
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
                User payer = (User) spinner.getSelectedItem();
                
                User[] users = null;//TODO
                splitTheBill(new Debt(amount, description), payer, users);
                
                refresh();

                clearFields();

                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });
    }
    
    public void updateSpinnerItems(){
        
        userlist = DAO.getInstance().getUsers();

        UserArrayAdapter adapter = new UserArrayAdapter(this, userlist);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
    
    public void clearFields(){
        ((EditText) findViewById(R.id.stb_desc)).setText("");
        ((EditText) findViewById(R.id.stb_amount)).setText("");
    }

    public String[] getUserList() {
        String[] list = new String[userlist.size()];
        for (int i = 0; i < userlist.size(); i++) {
            list[i] = userlist.get(i).getName();
        }
        return list;
    }

    public void splitTheBill(Debt debt, User payer, User[] users) {
        int debtors = users.length;
        Debt splitted = new Debt(debt.getAmount() / debtors, debt.getDescription());
        for (int i = 0; i < users.length; i++) {
            if (!users[i].equals(payer)) {
                DAO.getInstance().addDebt(payer,users[i],splitted);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.splitthebill_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        Intent intent;
        switch (item.getItemId()) {
            case R.id.refresh:
                refresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void refresh() {
        updateSpinnerItems();
        clearFields();
    }
}
