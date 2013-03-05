package eu.pinnoo.debtapp.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
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
import eu.pinnoo.debtapp.Debt;
import eu.pinnoo.debtapp.R;
import eu.pinnoo.debtapp.User;
import eu.pinnoo.debtapp.database.DAO;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author see /AUTHORS
 */
public class UserReviewActivity extends Activity {

    private List<User> userlist;

    @Override
    public void onCreate(Bundle savendInstanceState) {
        super.onCreate(savendInstanceState);
        setContentView(R.layout.user_review);
        final Spinner spinner = (Spinner) findViewById(R.id.review_userspinner);
        userlist = DAO.getInstance().getUsers();
        if (userlist == null) {
            DAO.getInstance().getPasswordModel().setPasswordCorrect(false);
        } else {
            DAO.getInstance().getPasswordModel().setPasswordCorrect(true);
            UserArrayAdapter adapter = new UserArrayAdapter(this, userlist, android.R.layout.simple_list_item_1);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                refresh();
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void refresh() {
        TextView totalamount = (TextView) findViewById(R.id.review_total_amount);
        totalamount.setTextColor(Color.GREEN);
        totalamount.setText("0");
        TableLayout table = (TableLayout) findViewById(R.id.review_table);
        final Spinner spinner = (Spinner) findViewById(R.id.review_userspinner);
        table.removeViews(1, table.getChildCount() - 1);
        User user = (User) spinner.getSelectedItem();
        if (user == null) {
            return;
        }
        int amount = 0;
        int rowNumber = 0;
        Iterator<User> userIt = userlist.iterator();
        while (userIt.hasNext()) {
            User user2 = userIt.next();
            List<Debt> debts = DAO.getInstance().getDebts(user, user2);

            if (debts != null && !debts.isEmpty()) {
                Iterator<Debt> it = debts.iterator();
                while (it.hasNext()) {
                    Debt d = it.next();
                    addTableRow(d.getAmount(), d.getDescription(), rowNumber, Color.GREEN, user2);
                    amount += d.getAmount();
                    rowNumber++;
                }
            } else {
                totalamount.setText("0");
                totalamount.setTextColor(Color.GREEN);
            }

            List<Debt> credits = DAO.getInstance().getDebts(user2, user);

            if (credits != null && !credits.isEmpty()) {
                Iterator<Debt> it2 = credits.iterator();
                while (it2.hasNext()) {
                    Debt d = it2.next();
                    addTableRow(d.getAmount(), d.getDescription(), rowNumber, Color.RED, user2);
                    amount -= d.getAmount();
                    rowNumber++;
                }
            } else {
                totalamount.setText("0");
                totalamount.setTextColor(Color.GREEN);
            }
        }
        if (amount >= 0) {
            totalamount.setTextColor(Color.GREEN);
        } else {
            totalamount.setTextColor(Color.RED);
        }
        totalamount.setText(Math.abs(((double) amount) / 100) + "");
    }

    private void addTableRow(int amount, String description, int rowNumber, int color, User u) {
        LayoutInflater inflater = getLayoutInflater();
        TableLayout tl = (TableLayout) findViewById(R.id.review_table);
        TableRow tr = (TableRow) inflater.inflate(R.layout.review_table_row, tl, false);
        TextView label_amount = (TextView) tr.findViewById(R.id.review_amount);
        label_amount.setText(((double) amount) / 100 + "");
        label_amount.setPadding(0, 5, 5, 5);
        TextView label_description = (TextView) tr.findViewById(R.id.review_description);
        label_description.setText(description);
        label_description.setPadding(5, 5, 5, 5);
        TextView label_user = (TextView) tr.findViewById(R.id.review_user);
        label_user.setText(u.getName());
        label_user.setPadding(0, 5, 5, 5);
        if (rowNumber % 2 == 0) {
            tr.setBackgroundColor(Color.GRAY);
            label_amount.setTextColor(color);
            label_description.setTextColor(Color.BLACK);
            label_user.setTextColor(Color.BLACK);
        } else {
            tr.setBackgroundColor(Color.LTGRAY);
            label_amount.setTextColor(color);
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
                refresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
