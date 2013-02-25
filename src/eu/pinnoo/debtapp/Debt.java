package eu.pinnoo.debtapp;

/**
 *
 * @author Wouter Pinnoo <Wouter.Pinnoo@UGent.be>
 */
public class Debt {
    
    private int id;
    private double amount;
    private User creditor;
    private User debtor;
    
    public Debt(int id, double amount, User creditor, User debtor){
        this.id = id;
        this.amount = amount;
        this.creditor = creditor;
        this.debtor = debtor;
    }
    
    public Debt(double amount, User creditor, User debtor){
        this(-1, amount, creditor, debtor);
    }
    
    public int getId(){
        return id;
    }
    
    public double getAmount(){
        return amount;
    }
    
    public User getCreditor(){
        return creditor;
    }
    
    public User getDebtor(){
        return debtor;
    }
}
