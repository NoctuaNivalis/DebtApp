/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.pinnoo.debtapp;

import java.util.Comparator;

/**
 *
 * @author Wouter Pinnoo <Wouter.Pinnoo@UGent.be>
 */
public class UserComparator implements Comparator<User>{
    
    public int compare(User lhs, User rhs) {
        return lhs.getName().compareTo(rhs.getName());
    }
    
}
