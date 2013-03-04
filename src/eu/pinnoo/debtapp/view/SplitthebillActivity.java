package eu.pinnoo.debtapp.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
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

    @Override
    public void onCreate(Bundle savendInstanceState) {
        super.onCreate(savendInstanceState);
        setContentView(R.layout.split_the_bill);
        final TextView payerlabel = (TextView) findViewById(R.id.payer);
        payerlabel.setText("Payer"); //of in xml omdat dit toch niet verandert?
        final TextView debtorslabel = (TextView) findViewById(R.id.debtors);
        debtorslabel.setText("Debtors:");

        final Spinner spinner = (Spinner) findViewById(R.id.spinner3);
        userlist = DAO.getInstance().getUsers();

        UserArrayAdapter adapter = new UserArrayAdapter(this, userlist);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        final ListView lv = (ListView) findViewById(R.id.debtorslist);
        lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, android.R.id.text1, getUserList()));
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
        Debt splited = new Debt(debt.getAmount() / debtors, debt.getDescription());
        for (int i = 0; i < users.length; i++) {
            if (!users[i].equals(payer)) {
                //toevoegen aan DB (TODO)
                //dao.addDebt(payer,users[i],splited);
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
        // TODO: everything that needs to refresh when there's an update in the DB, must be refreshed here
    }
}
