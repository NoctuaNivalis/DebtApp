package eu.pinnoo.debtapp.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import eu.pinnoo.debtapp.R;
import eu.pinnoo.debtapp.User;
import eu.pinnoo.debtapp.database.DAO;
import java.util.List;

/**
 *
 * @author Wouter Pinnoo <Wouter.Pinnoo@UGent.be>
 */
public class AddUserActivity extends Activity {

    @Override
    public void onCreate(Bundle savendInstanceState) {
        super.onCreate(savendInstanceState);
        setContentView(R.layout.add_user);
        final Button applybutton = (Button) findViewById(R.id.confirmadduser);
        final Button clearbutton = (Button) findViewById(R.id.cancelnewuser);
        final EditText namefield = (EditText) findViewById(R.id.username);
        applybutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String username = namefield.getText().toString();
                if (username.isEmpty()) {
                    return;
                }

                DAO.getInstance().addUser(new User(username));
                clearField();
                refresh();
            }
        });

        clearbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                clearField();
            }
        });
        refresh();
    }

    public String[] getUserList() {
        List<User> list = DAO.getInstance().getUsers();
        String[] userlist = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            userlist[i] = list.get(i).getName();
        }
        return userlist;
    }

    public void clearField() {
        ((EditText) findViewById(R.id.username)).setText("");
    }

    public void refresh() {
        ListView listview = (ListView) findViewById(R.id.userlist);
        listview.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, getUserList()));
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.manage_users_menu, menu);
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
}
