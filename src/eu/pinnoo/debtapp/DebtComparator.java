package eu.pinnoo.debtapp;

import java.util.Comparator;

/**
 *
 * @author Wouter Pinnoo <Wouter.Pinnoo@UGent.be>
 */
public class DebtComparator implements Comparator<Debt> {

    public int compare(Debt lhs, Debt rhs) {
        return (int) (lhs.getAmount() - rhs.getAmount());
    }
    
}
