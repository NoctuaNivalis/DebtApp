package eu.pinnoo.debtapp;

import java.util.Comparator;

/**
 *
 * @author see /AUTHORS
 */
public class UserComparator implements Comparator<User> {

    public int compare(User lhs, User rhs) {
        return lhs.getName().compareTo(rhs.getName());
    }
}
