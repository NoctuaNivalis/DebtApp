package eu.pinnoo.debtapp.database;

import eu.pinnoo.debtapp.Debt;
import eu.pinnoo.debtapp.DebtComparator;
import eu.pinnoo.debtapp.User;
import eu.pinnoo.debtapp.models.PasswordModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Wouter Pinnoo <Wouter.Pinnoo@UGent.be>
 */
public class DAO {

    private Database db;
    private PasswordModel pmodel;
    private JSONArray jsonarray;

    public DAO(PasswordModel pmodel) {
        this.pmodel = pmodel;
    }

    public List<Debt> getDebts(User creditor, User debtor) {
        String stmt = "SELECT * FROM Debts WHERE creditorid=" + creditor.getId() + " AND debtorid=" + debtor.getId();
        JSONArray array = Database.sendRequest(stmt, pmodel);
        if (array == null) {
            return null;
        }

        List<Debt> debts = new ArrayList<Debt>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = null;
            try {
                obj = array.getJSONObject(i);
                debts.add(new Debt(obj.getInt("debtid"), obj.getDouble("amount"), obj.getString("description"), creditor, debtor));
            } catch (JSONException ex) {
                Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NullPointerException e) {
                System.err.println(e);
            }
        }
        return debts;
    }

    public int getUserId(User user) {
        if (user.getId() != -1) {
            return user.getId();
        }
        int id = -1;

        String stmt = "SELECT id FROM User WHERE name=" + user.getId();
        JSONArray array = Database.sendRequest(stmt, pmodel);
        if (array == null) {
            return id;
        }

        JSONObject obj = null;
        try {
            obj = array.getJSONObject(0);
            id = obj.getInt("id");
        } catch (JSONException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullPointerException e) {
            System.err.println(e);
        }

        return id;
    }

    public void addDebt(User creditor, User debtor, Debt debt) {
        List<Debt> debtsCreditor = getDebts(debtor, creditor);
        if (debtsCreditor == null || debtsCreditor.isEmpty()) {
            addDebtInDatabase(creditor, debtor, debt);
            return;
        }
        Collections.sort(debtsCreditor, new DebtComparator());

        Iterator<Debt> it = debtsCreditor.iterator();
        while (it.hasNext()) {
            Debt d = it.next();
            if(d.getAmount() == debt.getAmount()){
                payOffDebt(d);
                return;
            } else if (d.getAmount() < debt.getAmount()){
                debt.setAmount(debt.getAmount() - d.getAmount());
                payOffDebt(d);
                updateDebtAmount(debt);
            } else {
                d.setAmount(d.getAmount() - debt.getAmount());
                updateDebtAmount(d);
                return;
            }
        }
        if (debt.getAmount() > 0) {
            addDebtInDatabase(creditor, debtor, debt);
        }
    }

    protected void addDebtInDatabase(User creditor, User debtor, Debt debt) {
        if (creditor.getId() == -1) {
            creditor.setId(getUserId(creditor));
        }
        if (debtor.getId() == -1) {
            debtor.setId(getUserId(debtor));
        }
        String stmt = "INSERT INTO Debts(amount, description, creditorid, debtorid) VALUES(" + debt.getAmount() + ", \"" + debt.getDescription() + "\", " + creditor.getId() + ", " + debtor.getId() + ")";
        Database.sendRequest(stmt, pmodel);
    }

    public void updateDebtAmount(Debt debt) {
        String stmt = "UPDATE Debts SET amount=" + debt.getAmount() + " WHERE debtid=" + debt.getId();
        Database.sendRequest(stmt, pmodel);
    }

    public List<User> getUsers() {
        String stmt = "SELECT * FROM User";
        JSONArray array = Database.sendRequest(stmt, pmodel);
        if (array == null) {
            return null;
        }

        List<User> users = new ArrayList<User>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = null;
            try {
                obj = array.getJSONObject(i);
                users.add(new User(obj.getString("name"), obj.getInt("id")));
            } catch (JSONException ex) {
                Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NullPointerException e) {
                System.err.println(e);
            }
        }
        return users;
    }

    protected void payOffDebt(Debt debt) {
        String stmt = "DELETE FROM Debts WHERE debtid=" + debt.getId();
        Database.sendRequest(stmt, pmodel);
    }

    public void payOffDebt(double amount, String description, User creditor, User debtor) {
        List<Debt> debts = getDebts(creditor, debtor);
        Collections.sort(debts, new DebtComparator());

        int index = 0;
        int sum = 0;
        int sumindex = 0;
        while (index < debts.size()) {
            if (amount == debts.get(index).getAmount()) {
                payOffDebt(debts.get(index));
                return;
            } else if (debts.get(index).getAmount() > amount) {
                break;
            } else {
                if (sum + debts.get(index).getAmount() < amount) {
                    sum += debts.get(index).getAmount();
                    sumindex++;
                }
                index++;
            }
        }

        for (int i = 0; i < sumindex; i++) {
            payOffDebt(debts.get(i));
            sum -= debts.get(i).getAmount();
        }

        if (sumindex + 1 < debts.size()) {
            debts.get(sumindex + 1).setAmount(debts.get(sumindex + 1).getAmount() - sum);
            updateDebtAmount(debts.get(sumindex + 1));
        } else {
            addDebt(creditor, debtor, new Debt(sum, description, creditor, debtor));
        }
    }
}
