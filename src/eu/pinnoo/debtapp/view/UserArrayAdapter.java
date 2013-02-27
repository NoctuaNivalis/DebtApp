/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.pinnoo.debtapp.view;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import eu.pinnoo.debtapp.User;
import java.util.List;
import eu.pinnoo.debtapp.R;

/**
 *
 * @author Stefaan Vermassen <Stefaan.Vermassen@UGent.be>
 */
public class UserArrayAdapter extends ArrayAdapter<User> {

    private Activity activity;
    private List<User> users;

    public UserArrayAdapter(Activity activity, List<User> users) {
        super(activity, android.R.layout.simple_list_item_1, users);
        this.activity = activity;
        this.users = users;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView v = (TextView) super.getView(position, convertView, parent);
        if (v == null) {
            v = new TextView(activity);
        }
        v.setTextColor(Color.BLACK);
        v.setText(users.get(position).getName());
        return v;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView v = (TextView) super.getView(position, convertView, parent);
        if (v == null) {
            v = new TextView(activity);
        }
        try{
            v.setText(users.get(position).getName());
        } catch (NullPointerException e){
            v.setText("Loading");
        }
        return v;
    }
}