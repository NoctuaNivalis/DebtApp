package eu.pinnoo.debtapp.view;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Spinner;
import android.widget.TextView;
import eu.pinnoo.debtapp.Debt;
import eu.pinnoo.debtapp.R;
import eu.pinnoo.debtapp.User;

/**
 *
 * @author Eveline Hoogstoel <eveline.hoogstoel@ugent.be>
 */
public class SplitthebillActivity extends Activity{
    
    @Override
    public void onCreate(Bundle savendInstanceState){
        super.onCreate(savendInstanceState);
        setContentView(R.layout.split_the_bill);
        final TextView payerlabel = (TextView) findViewById(R.id.payer);
        payerlabel.setText("Payer"); //of in xml omdat dit toch niet verandert?
        final TextView debtorslabel = (TextView) findViewById(R.id.debtors);
        debtorslabel.setText("Debtors:");
        
        final Spinner spinner = (Spinner) findViewById(R.id.spinner3);
        
    }
    
    public void splitTheBill(Debt debt, User payer, User[] users){
        int debtors = users.length;
        Debt splited = new Debt(debt.getAmount()/debtors, debt.getDescription());
        for(int i=0; i < users.length; i++){
            if(!users[i].equals(payer)){
                //toevoegen aan DB (TODO)
                //dao.addDebt(payer,users[i],splited);
            }
        }
    }

}
