package eu.pinnoo.debtapp.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 *
 * @author Wouter Pinnoo <Wouter.Pinnoo@UGent.be>
 */
public class Database {

    private Properties dbProperties;

    public Database() {
        InputStream inp = null;
        try {
            inp = new FileInputStream("database/database.properties");
            dbProperties = new Properties();
            dbProperties.load(inp);
            inp.close();
        } catch (IOException e) {
            System.err.println(e);
        }
        try {
            Class.forName(dbProperties.getProperty("driver"));
        } catch (ClassNotFoundException e) {
            System.err.println(e);
        }
    }

    public Connection getConnection() {
        Connection c = null;
        try {
            DriverManager.getConnection(dbProperties.getProperty("url"),
                    dbProperties.getProperty("user"),
                    dbProperties.getProperty("password"));
        } catch (SQLException e) {
            return null;
        }
        return c;
    }
}
