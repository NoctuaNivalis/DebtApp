package eu.pinnoo.debtapp.view;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import eu.pinnoo.debtapp.R;

public class MainActivity extends Activity {

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        addTableRow(10, "pizza");
    }
    
    private void addTableRow(int amount, String description){
        TableLayout tl = (TableLayout) findViewById(R.id.main_table);
        TableRow entry = new TableRow(this);entry.setBackgroundColor(Color.GRAY);
        TextView label_amount = new TextView(this);
        label_amount.setId(20);
        label_amount.setText(amount+"");
        label_amount.setTextColor(Color.WHITE);
        label_amount.setPadding(5, 5, 5, 5);
        entry.addView(label_amount);
        TextView label_description = new TextView(this);
        label_description.setId(21);
        label_description.setText(description); 
        label_description.setTextColor(Color.WHITE); 
        label_description.setPadding(5, 5, 5, 5);
        entry.addView(label_description);
        tl.addView(entry);
    }
}
