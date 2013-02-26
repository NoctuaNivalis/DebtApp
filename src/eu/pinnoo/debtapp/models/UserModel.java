package eu.pinnoo.debtapp.models;

import eu.pinnoo.debtapp.User;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Wouter Pinnoo <Wouter.Pinnoo@UGent.be>
 */
public class UserModel {
    
    private List<User> list;
    
    public void setUsers(List<User> list){
        this.list = list;
    }
    
    public List<String> getUsernames(){
        List<String> usernames = new ArrayList<String>();
        Iterator<User> it = list.iterator();
        while(it.hasNext()){
            usernames.add(it.next().getName());
        }
        return usernames;
    }
}
