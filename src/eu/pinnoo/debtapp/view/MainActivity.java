package eu.pinnoo.debtapp.view;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
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
        //TableLayout t1;
        TableLayout tl = (TableLayout) findViewById(R.id.main_table);
        TableRow tr_head = new TableRow(this);
        tr_head.setId(10);
        tr_head.setBackgroundColor(Color.GRAY);
        tr_head.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));
        TextView label_amount = new TextView(this);
         label_amount.setId(20);
         label_amount.setText("5");
         label_amount.setTextColor(Color.WHITE);
         label_amount.setPadding(5, 5, 5, 5);
         tr_head.addView(label_amount);// add the column to the table row here

         TextView label_description = new TextView(this);
         label_description.setId(21);// define id that must be unique
         label_description.setText("Pizza"); // set the text for the header 
         label_description.setTextColor(Color.WHITE); // set the color
         label_description.setPadding(5, 5, 5, 5); // set the padding (if required)
         tr_head.addView(label_description); // add the column to the table row here
         tl.addView(tr_head, new TableLayout.LayoutParams(
                 LayoutParams.MATCH_PARENT,
                 LayoutParams.WRAP_CONTENT));

    }
}
