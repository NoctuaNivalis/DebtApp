package eu.pinnoo.debtapp.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.google.analytics.tracking.android.EasyTracker;
import eu.pinnoo.debtapp.Debt;
import eu.pinnoo.debtapp.R;
import eu.pinnoo.debtapp.User;
import eu.pinnoo.debtapp.database.DAO;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author see /AUTHORS
 */
public class UserReviewActivity extends Activity {

    private List<User> userlist;
    private UserArrayAdapter adapter;

    @Override
    public void onCreate(Bundle savendInstanceState) {
        super.onCreate(savendInstanceState);
        setContentView(R.layout.user_review);
        initiateSpinner();
        new UsersUpdater().execute();
    }
    
    @Override
    public void onStart(){
        EasyTracker.getInstance().activityStart(this);
    }
    
    @Override
    public void onStop(){
        EasyTracker.getInstance().activityStop(this);
    }

    private void initiateSpinner() {
        final Spinner spinner = (Spinner) findViewById(R.id.review_userspinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (id != 0) {
                    new UserItemsUpdater().execute();
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void updateSpinnerItems() {
        userlist = DAO.getInstance().getUsers();
        if (userlist == null) {
            DAO.getInstance().getPasswordModel().setPasswordCorrect(false);
        } else {
            DAO.getInstance().getPasswordModel().setPasswordCorrect(true);
            userlist.add(0, new User("Select"));
            
            adapter = new UserArrayAdapter(this, userlist, android.R.layout.simple_list_item_1);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }
    }

    private List<ItemTableRow> updateUserItems() {
        final Spinner spinner = (Spinner) findViewById(R.id.review_userspinner);
        User user = (User) spinner.getSelectedItem();
        if (user == null) {
            return null;
        }
        int rowNumber = 0;
        List<ItemTableRow> rows = new ArrayList<ItemTableRow>();
        Iterator<User> userIt = userlist.iterator();
        while (userIt.hasNext()) {
            User user2 = userIt.next();
            List<Debt> debts = DAO.getInstance().getDebts(user, user2);

            if (debts != null && !debts.isEmpty()) {
                Iterator<Debt> it = debts.iterator();
                while (it.hasNext()) {
                    Debt d = it.next();
                    rows.add(new ItemTableRow(d.getAmount(), d.getDescription(), rowNumber, Color.GREEN, user2));
                    rowNumber++;
                }
            }

            List<Debt> credits = DAO.getInstance().getDebts(user2, user);

            if (credits != null && !credits.isEmpty()) {
                Iterator<Debt> it2 = credits.iterator();
                while (it2.hasNext()) {
                    Debt d = it2.next();
                    rows.add(new ItemTableRow(d.getAmount(), d.getDescription(), rowNumber, Color.RED, user2));
                    rowNumber++;
                }
            }
        }
        return rows;
    }

    private void addTableRow(ItemTableRow row) {
        LayoutInflater inflater = getLayoutInflater();
        TableLayout tl = (TableLayout) findViewById(R.id.review_table);
        TableRow tr = (TableRow) inflater.inflate(R.layout.review_table_row, tl, false);
        TextView label_amount = (TextView) tr.findViewById(R.id.review_amount);
        label_amount.setText(((double) row.amount) / 100 + "");
        label_amount.setPadding(0, 5, 5, 5);
        TextView label_description = (TextView) tr.findViewById(R.id.review_description);
        label_description.setText(row.description);
        label_description.setPadding(5, 5, 5, 5);
        TextView label_user = (TextView) tr.findViewById(R.id.review_user);
        label_user.setText(row.u.getName());
        label_user.setPadding(0, 5, 5, 5);
        if (row.rowNumber % 2 == 0) {
            tr.setBackgroundColor(Color.GRAY);
            label_amount.setTextColor(row.color);
            label_description.setTextColor(Color.BLACK);
            label_user.setTextColor(Color.BLACK);
        } else {
            tr.setBackgroundColor(Color.LTGRAY);
            label_amount.setTextColor(row.color);
            label_description.setTextColor(Color.BLACK);
            label_user.setTextColor(Color.BLACK);
        }
        tl.addView(tr);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.user_review_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                new UsersUpdater().execute();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class UsersUpdater extends AsyncTask<Void, Void, Integer> {

        private ProgressDialog dialog = new ProgressDialog(UserReviewActivity.this);

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Loading users...");
            dialog.show();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            updateSpinnerItems();
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            dialog.dismiss();
            final Spinner spinner = (Spinner) findViewById(R.id.review_userspinner);
            spinner.setAdapter(adapter);
        }
    }

    private class UserItemsUpdater extends AsyncTask<Void, Void, Integer> {

        private ProgressDialog dialog = new ProgressDialog(UserReviewActivity.this);
        private List<ItemTableRow> rows;
        private TextView totalamount;

        @Override
        protected void onPreExecute() {
            totalamount = (TextView) findViewById(R.id.review_total_amount);
            totalamount.setTextColor(Color.GREEN);
            totalamount.setText("0");
            TableLayout table = (TableLayout) findViewById(R.id.review_table);
            table.removeViews(1, table.getChildCount() - 1);
            dialog.setMessage("Loading debts...");
            dialog.show();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            rows = updateUserItems();
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            dialog.dismiss();
            int amount = 0;
            for (ItemTableRow item : rows) {
                addTableRow(item);
                switch (item.color) {
                    case Color.GREEN:
                        amount += item.amount;
                        break;
                    case Color.RED:
                        amount -= item.amount;
                        break;
                }
            }
            totalamount.setText(Math.abs(((double) amount) / 100) + "");
            if (amount >= 0) {
                totalamount.setTextColor(Color.GREEN);
            } else {
                totalamount.setTextColor(Color.RED);
            }
        }
    }

    private class ItemTableRow {

        public int amount;
        public String description;
        public int rowNumber;
        public int color;
        public User u;

        public ItemTableRow(int amount, String description, int rowNumber, int color, User u) {
            this.amount = amount;
            this.description = description;
            this.rowNumber = rowNumber;
            this.color = color;
            this.u = u;
        }
    }
}
