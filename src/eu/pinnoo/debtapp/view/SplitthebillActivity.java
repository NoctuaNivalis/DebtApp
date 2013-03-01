package eu.pinnoo.debtapp.view;

import android.app.Activity;
import android.os.Bundle;
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
