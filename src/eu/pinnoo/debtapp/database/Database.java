package eu.pinnoo.debtapp.database;

import eu.pinnoo.debtapp.models.PasswordModel;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

/**
 *
 * @author see /AUTHORS
 */
public class Database {

    private static class Pair implements NameValuePair {

        protected String name;
        protected String value;

        private Pair(String pname, String pvalue) {
            name = pname;
            value = pvalue;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }
    }

    public static JSONArray sendRequest(String stmt, PasswordModel pmodel) {
        ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add(new Pair("password", pmodel.getPassword()));
        pairs.add(new Pair("stmt", stmt));

        InputStream inp = null;

        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost("http://pinnoo.eu:9093/debtdb/sql.php");
        try {
            post.setEntity(new UrlEncodedFormEntity(pairs));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        HttpResponse response = null;
        try {
            response = client.execute(post);
            HttpEntity entity = response.getEntity();
            inp = entity.getContent();
        } catch (IOException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (IllegalStateException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

        String result = "";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inp, "iso-8859-1"), 8);
            StringBuilder builder = new StringBuilder();
            builder.append(reader.readLine()).append("\n");

            String line = "";
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
            inp.close();
            result = builder.toString();

        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException e) {
            System.err.println(e);
        }
        JSONArray arr = null;
        try {
            if (result.isEmpty() || result.equals("null\n")) {
                return null;
            } else {
                arr = new JSONArray(result);
            }
        } catch (JSONException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return arr;
        }
    }
}
