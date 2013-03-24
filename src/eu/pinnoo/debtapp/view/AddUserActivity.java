package eu.pinnoo.debtapp.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import com.google.analytics.tracking.android.EasyTracker;
import eu.pinnoo.debtapp.R;
import eu.pinnoo.debtapp.User;
import eu.pinnoo.debtapp.database.DAO;
import java.util.List;

/**
 *
 * @author see /AUTHORS
 */
public class AddUserActivity extends Activity {

    @Override
    public void onCreate(Bundle savendInstanceState) {
        super.onCreate(savendInstanceState);
        setContentView(R.layout.add_user);
        final Button applybutton = (Button) findViewById(R.id.add_user_applybutton);
        final Button clearbutton = (Button) findViewById(R.id.add_user_clearbutton);
        final EditText namefield = (EditText) findViewById(R.id.add_user_username);
        applybutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String username = namefield.getText().toString();
                if (username.isEmpty()) {
                    return;
                }

                new AddUserAction(username).execute();
            }
        });

        clearbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                clearField();
            }
        });
        refresh();
    }
    
    @Override
    public void onStart(){
        EasyTracker.getInstance().activityStart(this);
    }
    
    @Override
    public void onStop(){
        EasyTracker.getInstance().activityStop(this);
    }

    public void clearField() {
        ((EditText) findViewById(R.id.add_user_username)).setText("");
    }

    public void refresh() {
        new LoadUsers().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.manage_users_menu, menu);
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

    private class AddUserAction extends AsyncTask<Void, Void, Integer> {

        private ProgressDialog dialog = new ProgressDialog(AddUserActivity.this);
        private String username;

        public AddUserAction(String username) {
            this.username = username;
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Applying...");
            dialog.show();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            DAO.getInstance().addUser(new User(username));
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            dialog.dismiss();
            clearField();
            refresh();
        }
    }

    private class LoadUsers extends AsyncTask<Void, Void, Integer> {

        private ProgressDialog dialog = new ProgressDialog(AddUserActivity.this);
        private List<User> list;

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Loading users...");
            dialog.show();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            list = DAO.getInstance().getUsers();
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            dialog.dismiss();
            String[] userlist = new String[list.size()];
            int i = 0;
            int j = 0;
            while (i < list.size()) {
                if(!list.get(i).getName().equals("Select")){
                   userlist[j++] = list.get(i).getName(); 
                }
                i++;
            }
            ListView listview = (ListView) findViewById(R.id.add_user_userlist);
            listview.setAdapter(new ArrayAdapter<String>(AddUserActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, userlist));
        }
    }
}
