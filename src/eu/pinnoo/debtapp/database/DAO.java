package eu.pinnoo.debtapp.database;

import eu.pinnoo.debtapp.Debt;
import eu.pinnoo.debtapp.DebtComparator;
import eu.pinnoo.debtapp.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Wouter Pinnoo <Wouter.Pinnoo@UGent.be>
 */
public class DAO {

    private Database db;

    public DAO() {
        db = new Database();
    }

    public List<Debt> getDebts(User creditor, User debtor) {
        List<Debt> list = new ArrayList<Debt>();
        if (creditor == null || debtor == null) {
            return list;
        }
        try {
            Connection conn = db.getConnection();
            PreparedStatement stat = conn.prepareStatement("SELECT * FROM Debts WHERE creditorid=? AND debtorid=?");
            stat.setInt(1, getUserId(creditor));
            stat.setInt(2, getUserId(debtor));
            ResultSet rs = stat.executeQuery();
            while (rs.next()) {
                list.add(new Debt(rs.getInt("debtid"), rs.getDouble("amount"), rs.getString("description"), creditor, debtor));
            }
            rs.close();
            stat.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println(e);
        }
        return list;
    }

    public int getUserId(User user) {
        if (user.getId() != -1) {
            return user.getId();
        }
        int id = -1;
        try {
            Connection conn = db.getConnection();
            PreparedStatement stat = conn.prepareStatement("SELECT id FROM User WHERE name=?");
            stat.setString(1, user.getName());
            ResultSet rs = stat.executeQuery();
            if (rs.next()) {
                id = rs.getInt("id");
            }
            rs.close();
            stat.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println(e);
        }
        return id;
    }

    public void addDebt(User creditor, User debtor, Debt debt) {
        if (creditor.getId() == -1) {
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
        }
    }

    public void updateDebtAmount(Debt debt) {
        try {
            Connection conn = db.getConnection();
            PreparedStatement stat = conn.prepareStatement("UPDATE Debts SET amount=? WHERE debtid=?");
            stat.setDouble(1, debt.getAmount());
            stat.setInt(2, debt.getId());
            stat.executeUpdate();
            stat.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println(e);
        }
    }

    public List<User> getUsers() {
        List<User> users = new ArrayList<User>();
        try {
            Connection conn = db.getConnection();
            PreparedStatement stat = conn.prepareStatement("SELECT * FROM User");
            ResultSet rs = stat.executeQuery();
            while (rs.next()) {
                users.add(new User(rs.getString("name"), rs.getInt("id")));
            }
            rs.close();
            stat.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println(e);
        }
        return users;
    }

    protected boolean payOffDebt(Debt debt) {
        boolean success = false;
        try {
            Connection conn = db.getConnection();
            PreparedStatement stat = conn.prepareStatement("DELETE FROM Debts WHERE debtid=?");
            stat.setDouble(1, debt.getId());
            success = stat.executeUpdate() > 0;
            stat.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println(e);
        }
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
