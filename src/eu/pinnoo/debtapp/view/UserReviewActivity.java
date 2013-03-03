package eu.pinnoo.debtapp.view;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Spinner;
import android.widget.TextView;
import eu.pinnoo.debtapp.R;

/**
 *
 * @author Wouter Pinnoo <Wouter.Pinnoo@UGent.be>
 */
public class UserReviewActivity extends Activity{

    @Override
    public void onCreate(Bundle savendInstanceState){
        super.onCreate(savendInstanceState);
        setContentView(R.layout.user_review);
        final TextView payerlabel = (TextView) findViewById(R.id.userlabel);
        final Spinner spinner = (Spinner) findViewById(R.id.userspinner);
        
    }
}
