package eu.pinnoo.debtapp.models;

/**
 *
 * @author see /AUTHORS
 */
public class PasswordModel {

    private String password;
    private boolean correct;

    public PasswordModel() {
        correct = true;
    }

    public boolean passwordCorrect() {
        return correct;
    }

    public void setPasswordCorrect(boolean b) {
        correct = b;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
