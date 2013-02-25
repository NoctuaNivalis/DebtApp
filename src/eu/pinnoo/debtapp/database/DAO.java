package eu.pinnoo.debtapp.database;

import eu.pinnoo.debtapp.Debt;
import eu.pinnoo.debtapp.User;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
            PreparedStatement stat = db.getConnection().prepareStatement("SELECT * FROM Debts WHERE creditorid=? AND debtorid=?");
            stat.setInt(1, getUserId(creditor));
            stat.setInt(2, getUserId(debtor));
            ResultSet rs = stat.executeQuery();
            while(rs.next()){
                list.add(new Debt(rs.getInt("debtid"), rs.getDouble("amount"), rs.getString("description"), creditor, debtor));
            }
            rs.close();
            stat.close();
        } catch (SQLException ex) {
        }
        return list;
    }
    
    public int getUserId(User user){
        if(user.getId() != -1) {
            return user.getId();
        }
        int id = -1;
        try {
            PreparedStatement stat = db.getConnection().prepareStatement("SELECT id FROM User WHERE name=?");
            stat.setString(1, user.getName());
            ResultSet rs = stat.executeQuery();
            if(rs.next()){
                id = rs.getInt("id");
            }
            rs.close();
            stat.close();
        } catch (SQLException ex) {
        }
        return id;
    }
}
