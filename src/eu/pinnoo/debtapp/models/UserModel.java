package eu.pinnoo.debtapp.models;

import android.app.Activity;
import android.widget.Spinner;
import eu.pinnoo.debtapp.R;
import eu.pinnoo.debtapp.User;
import java.util.List;

/**
 *
 * @author see /AUTHORS
 */
public class UserModel {

    public enum DIRECTION {

        EAST, WEST;
    }
    
    private DIRECTION curDir;
    private Activity act;

    public UserModel(Activity act) {
        curDir = DIRECTION.EAST;
        this.act = act;
    }

    public void switchDir() {
        curDir = curDir.equals(DIRECTION.WEST) ? DIRECTION.EAST : DIRECTION.WEST;
    }

    public User getCreditor() {
        switch (curDir) {
            case EAST:
                return (User) ((Spinner) act.findViewById(R.id.main_user_right_spinner)).getSelectedItem();
            case WEST:
                return (User) ((Spinner) act.findViewById(R.id.main_user_left_spinner)).getSelectedItem();
        }
        return null;
    }

    public User getDebtor() {
        switch (curDir) {
            case EAST:
                return (User) ((Spinner) act.findViewById(R.id.main_user_left_spinner)).getSelectedItem();
            case WEST:
                return (User) ((Spinner) act.findViewById(R.id.main_user_right_spinner)).getSelectedItem();
        }
        return null;
    }

    public DIRECTION getCurDir() {
        return curDir;
    }
}
