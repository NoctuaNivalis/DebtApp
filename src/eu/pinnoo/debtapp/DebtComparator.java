package eu.pinnoo.debtapp;

import java.util.Comparator;

/**
 *
 * @author see /AUTHORS
 */
public class DebtComparator implements Comparator<Debt> {

    public int compare(Debt lhs, Debt rhs) {
        return lhs.getAmount() - rhs.getAmount();
    }
}
