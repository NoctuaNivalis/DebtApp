package eu.pinnoo.debtapp.view;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import eu.pinnoo.debtapp.R;
import eu.pinnoo.debtapp.User;
import eu.pinnoo.debtapp.database.DAO;
import eu.pinnoo.debtapp.models.UserModel;

public class MainActivity extends Activity {
    
    private UserModel usermodel;
    private DAO doa;
    private DAO dao;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        addTableRow(10, "pizza");
        
        usermodel = new UserModel();
        dao = new DAO();
        usermodel.setUsers(dao.getUsers());
        
        final Button b = (Button) findViewById(R.id.undo);
        b.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                User debtor = (User) ((Spinner) findViewById(R.id.planets_spinner)).getSelectedItem();
                User creditor = (User) ((Spinner) findViewById(R.id.planets_spinner2)).getSelectedItem();
            }
        });
    }
    
    private void addTableRow(int amount, String description){
        LayoutInflater inflater = getLayoutInflater();
        TableLayout tl = (TableLayout) findViewById(R.id.main_table);
        TableRow tr = (TableRow)inflater.inflate(R.layout.table_row, tl, false);
        tr.setBackgroundColor(Color.GRAY);
        TextView label_amount = (TextView)tr.findViewById(R.id.amount);
        label_amount.setText(amount+"");
        label_amount.setPadding(5, 5, 5, 5);
        label_amount.setTextColor(Color.RED);
        TextView label_description = (TextView)tr.findViewById(R.id.description);;
        label_description.setText(description); 
        label_description.setPadding(5, 5, 5, 5);
        label_description.setTextColor(Color.WHITE); 
        tl.addView(tr);
    }
    
    private void addItemsOnUserSpinners(){
        //To implement
    }
}
