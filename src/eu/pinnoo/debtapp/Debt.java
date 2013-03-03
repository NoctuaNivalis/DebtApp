package eu.pinnoo.debtapp;

/**
 *
 * @author Wouter Pinnoo <Wouter.Pinnoo@UGent.be>
 */
public class Debt {
    
    private int id;
    private double amount;
    private String description;
    private User creditor;
    private User debtor;
    
    public Debt(int id, double amount, String description, User creditor, User debtor){
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.creditor = creditor;
        this.debtor = debtor;
    }
    public Debt(double amount, String description){
        this.amount = amount;
        this.description = description;
    }
    
    public Debt(double amount, String description, User creditor, User debtor){
        this(-1, amount, description, creditor, debtor);
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
    
    public String getDescription(){
        return description;
    }
    
    public void setAmount(double amount){
        this.amount = amount;
    }
}
