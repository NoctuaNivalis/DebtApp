package eu.pinnoo.debtapp.models;

import android.app.Activity;
import android.content.Context;
import android.widget.Spinner;
import eu.pinnoo.debtapp.R;
import eu.pinnoo.debtapp.User;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Wouter Pinnoo <Wouter.Pinnoo@UGent.be>
 */
public class UserModel {
    
    public enum DIRECTION{
        EAST, WEST;
    }
            
    private List<User> list;
    private DIRECTION curDir;
    private Activity act;
    
    public UserModel(Activity act){
        curDir = DIRECTION.EAST;
        this.act = act;
    }
    
    public void switchDir(){
        curDir = curDir.equals(DIRECTION.WEST) ? DIRECTION.EAST : DIRECTION.WEST;
    }
    
    public User getCreditor(){
        switch(curDir){
            case EAST:
                return (User) ((Spinner) act.findViewById(R.id.spinner1)).getSelectedItem();
            case WEST:
                return (User) ((Spinner) act.findViewById(R.id.spinner2)).getSelectedItem();
        }
        return null;
    }
    
    public User getDebtor(){
        switch(curDir){
            case EAST:
                return (User) ((Spinner) act.findViewById(R.id.spinner2)).getSelectedItem();
            case WEST:
                return (User) ((Spinner) act.findViewById(R.id.spinner1)).getSelectedItem();
        }
        return null;
    }
    
    public DIRECTION getCurDir(){
        return curDir;
    }
}
