package eu.pinnoo.debtapp.database;

import eu.pinnoo.debtapp.Debt;
import eu.pinnoo.debtapp.DebtComparator;
import eu.pinnoo.debtapp.User;
import eu.pinnoo.debtapp.models.PasswordModel;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
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
        /*if (creditor.getId() == -1) {
            creditor.setId(getUserId(creditor));
        }
        if (debtor.getId() == -1) {
            debtor.setId(getUserId(debtor));
        }

        try {
            Connection conn = db.getConnection();
            PreparedStatement stat = conn.prepareStatement("INSERT INTO Debts(amount, description, creditorid, debtorid) VALUES(?,?,?,?)");
            stat.setDouble(1, debt.getAmount());
            stat.setString(2, debt.getDescription());
            stat.setInt(3, creditor.getId());
            stat.setInt(4, debtor.getId());
            stat.executeUpdate();
            stat.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println(e);
        }*/
    }

    public void updateDebtAmount(Debt debt) {
        /*try {
            Connection conn = db.getConnection();
            PreparedStatement stat = conn.prepareStatement("UPDATE Debts SET amount=? WHERE debtid=?");
            stat.setDouble(1, debt.getAmount());
            stat.setInt(2, debt.getId());
            stat.executeUpdate();
            stat.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println(e);
        }*/
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

    protected boolean payOffDebt(Debt debt) {
        boolean success = false;
        /*try {
            Connection conn = db.getConnection();
            PreparedStatement stat = conn.prepareStatement("DELETE FROM Debts WHERE debtid=?");
            stat.setDouble(1, debt.getId());
            success = stat.executeUpdate() > 0;
            stat.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println(e);
        }*/
        return success;
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

        for (int i = 0; i <= sumindex; i++) {
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
