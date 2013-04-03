package eu.pinnoo.debtapp.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import com.google.analytics.tracking.android.EasyTracker;
import eu.pinnoo.debtapp.Debt;
import eu.pinnoo.debtapp.DecimalDigitsInputFilter;
import eu.pinnoo.debtapp.R;
import eu.pinnoo.debtapp.User;
import eu.pinnoo.debtapp.database.DAO;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author see /AUTHORS
 */
public class SplitthebillActivity extends Activity {

    private List<User> userlist;
    private Spinner spinner;
    private UserArrayAdapter listadapter;
    private boolean activityStarted = false;

    @Override
    public void onCreate(Bundle savendInstanceState) {
        super.onCreate(savendInstanceState);
        setContentView(R.layout.split_the_bill);
        EditText amountEditText = (EditText) findViewById(R.id.stb_amount);
        amountEditText.setFilters(new InputFilter[]{new DecimalDigitsInputFilter()});
        spinner = (Spinner) findViewById(R.id.stb_payer_spinner);
        final ListView lv = (ListView) findViewById(R.id.stb_debtorslist);
        new LoadUsers().execute();
        lv.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> av, View view, int position, long l) {
                listadapter.toggle(position);
            }
        });
        final Button okButton = (Button) findViewById(R.id.stb_apply);
        okButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int tmpamount = 0;
                try {
                    tmpamount = (int) (100 * Double.parseDouble((((EditText) findViewById(R.id.stb_amount)).getText().toString())));
                } catch (NumberFormatException e) {
                    return;
                }

                final String description = ((EditText) findViewById(R.id.stb_desc)).getText().toString();
                final int amount = tmpamount;
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

            private void apply(int amount, String description) {
                User payer = (User) spinner.getSelectedItem();
                ArrayList<User> selectedusers = new ArrayList<User>();
                UserArrayAdapter userlistadapter = (UserArrayAdapter) lv.getAdapter();
                for (int i = 0; i < userlist.size(); i++) {
                    if (userlistadapter.isChecked(i)) {
                        selectedusers.add((User) lv.getItemAtPosition(i));
                    }
                }
                new SplitTheBillAction(new Debt(amount, description), payer, selectedusers).execute();
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });
    }
    
    @Override
    public void onResume(){
        super.onResume();
        if(!activityStarted){
            return;
        }
        refresh();
    }
    
    @Override
    public void onStart(){
        super.onStart();
        EasyTracker.getInstance().activityStart(this);
    }
    
    @Override
    public void onStop(){
        super.onStop();
        EasyTracker.getInstance().activityStop(this);
    }

    public void clearFields() {
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

    public void splitTheBill(Debt debt, User payer, ArrayList<User> users) {
        int debtors = users.size();
        Debt splitted = new Debt(debt.getAmount() / debtors, debt.getDescription());
        for (int i = 0; i < users.size(); i++) {
            if (!users.get(i).equals(payer)) {
                DAO.getInstance().addDebt(payer, users.get(i), splitted);
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
        switch (item.getItemId()) {
            case R.id.refresh:
                refresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void refresh() {
        new LoadUsers().execute();
        clearFields();
    }

    private class LoadUsers extends AsyncTask<Void, Void, Integer> {

        private ProgressDialog dialog = new ProgressDialog(SplitthebillActivity.this);

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Loading users...");
            dialog.show();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            userlist = DAO.getInstance().getUsers();
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            dialog.dismiss();
            UserArrayAdapter adapter = new UserArrayAdapter(SplitthebillActivity.this, userlist, android.R.layout.simple_list_item_1);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            final ListView lv = (ListView) findViewById(R.id.stb_debtorslist);
            listadapter = new UserArrayAdapter(SplitthebillActivity.this, userlist, android.R.layout.simple_list_item_multiple_choice);
            lv.setAdapter(listadapter);
            lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        }
    }

    private class SplitTheBillAction extends AsyncTask<Void, Void, Integer> {

        private ProgressDialog dialog = new ProgressDialog(SplitthebillActivity.this);
        private Debt debt;
        private User payer;
        private List<User> users;

        public SplitTheBillAction(Debt debt, User payer, ArrayList<User> users) {
            this.debt = debt;
            this.payer = payer;
            this.users = users;
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Applying...");
            dialog.show();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            int debtors = users.size();
            Debt splitted = new Debt(debt.getAmount() / debtors, debt.getDescription());
            for (int i = 0; i < users.size(); i++) {
                if (!users.get(i).equals(payer)) {
                    DAO.getInstance().addDebt(payer, users.get(i), splitted);
                }
            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            dialog.dismiss();
            refresh();
        }
    }
}
