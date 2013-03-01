package eu.pinnoo.debtapp.models;
/**
 *
 * @author Stefaan Vermassen <Stefaan.Vermassen@UGent.be>
 */
public class PasswordModel {
    
    private String password;
    private boolean correct;

    public PasswordModel(){
        correct = true;
    }
    
    public boolean passwordCorrect(){
        return correct;
    }
    
    public void setPasswordCorrect(boolean b){
        correct = b;
    }
    
    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
    
}
