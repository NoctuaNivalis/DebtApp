package eu.pinnoo.debtapp.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
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
import java.util.ArrayList;
import java.util.List;
import eu.pinnoo.debtapp.models.UserModel;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends Activity {

    private UserModel usermodel;
    private PasswordModel passwordmodel;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        passwordmodel = new PasswordModel();
        askForPassword();
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

    private void UpdateItemsInUserSpinners() {
        //To do
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
                UpdateItemsInUserSpinners();   
            }
        });
        alert.show();
    }
}
