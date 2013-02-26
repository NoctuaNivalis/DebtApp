package eu.pinnoo.debtapp.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import eu.pinnoo.debtapp.Debt;
import eu.pinnoo.debtapp.R;
import eu.pinnoo.debtapp.User;
import eu.pinnoo.debtapp.database.DAO;
import eu.pinnoo.debtapp.models.PasswordModel;
import eu.pinnoo.debtapp.models.UserModel;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends Activity {

    private UserModel usermodel;
    private PasswordModel passwordmodel;
    private DAO dao;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        passwordmodel = new PasswordModel();
        dao = new DAO(passwordmodel);
        askForPassword();
        
        final Button refreshbutton = (Button) findViewById(R.id.undo);
        refreshbutton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                User debtor = (User) ((Spinner) findViewById(R.id.spinner1)).getSelectedItem();
                User creditor = (User) ((Spinner) findViewById(R.id.spinner2)).getSelectedItem();
                if(debtor == null || creditor == null) return;
                List<Debt> debts = dao.getDebts(creditor, debtor);
                Iterator<Debt> it = debts.iterator();
                while(it.hasNext()){
                    Debt d = it.next();
                    addTableRow(d.getAmount(), d.getDescription());
                }
            }
        });
        
        addTableRow(10, "pizza");
    }

    private void addTableRow(double amount, String description) {
        LayoutInflater inflater = getLayoutInflater();
        TableLayout tl = (TableLayout) findViewById(R.id.main_table);
        TableRow tr = (TableRow) inflater.inflate(R.layout.table_row, tl, false);
        tr.setBackgroundColor(Color.GRAY);
        TextView label_amount = (TextView) tr.findViewById(R.id.amount);
        label_amount.setText(amount + "");
        label_amount.setPadding(5, 5, 5, 5);
        label_amount.setTextColor(Color.RED);
        TextView label_description = (TextView) tr.findViewById(R.id.description);;
        label_description.setText(description);
        label_description.setPadding(5, 5, 5, 5);
        label_description.setTextColor(Color.WHITE);
        tl.addView(tr);
    }

    private void updateItemsInUserSpinners() {
        Spinner spinner1 = (Spinner) findViewById(R.id.spinner1);
        Spinner spinner2 = (Spinner) findViewById(R.id.spinner2);
        List<User> userlist = dao.getUsers();
        UserArrayAdapter adapter = new UserArrayAdapter(this, userlist);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter);
        spinner2.setAdapter(adapter);
    }

    private void askForPassword() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Password needed!");
        alert.setMessage("Please enter the password of the database.");
        final EditText input = new EditText(this);
        alert.setView(input);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                passwordmodel.setPassword(value);
                updateItemsInUserSpinners();
            }
        });
        alert.show();
    }
}
