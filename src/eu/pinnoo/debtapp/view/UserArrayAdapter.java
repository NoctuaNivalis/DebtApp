/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.pinnoo.debtapp.view;

import android.app.Activity;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import eu.pinnoo.debtapp.User;
import java.util.List;
import eu.pinnoo.debtapp.R;

/**
 *
 * @author Stefaan Vermassen <Stefaan.Vermassen@UGent.be>
 */
public class UserArrayAdapter extends ArrayAdapter<User> implements CompoundButton.OnCheckedChangeListener {

    private Activity activity;
    private List<User> users;
    private SparseBooleanArray mCheckStates;

    public UserArrayAdapter(Activity activity, List<User> users, int type) {
        super(activity, type, users);
        this.activity = activity;
        this.users = users;
        mCheckStates = new SparseBooleanArray(users.size());
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView v = (TextView) super.getView(position, convertView, parent);
        if (v == null) {
            v = new TextView(activity);
        }
        v.setTextColor(Color.WHITE);
        v.setText(users.get(position).getName());
        return v;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView v = (TextView) super.getView(position, convertView, parent);
        if (v == null) {
            v = new TextView(activity);
        }
        try {
            v.setText(users.get(position).getName());
        } catch (NullPointerException e) {
            v.setText("Loading");
        }
        return v;
    }

    public boolean isChecked(int position) {
        return mCheckStates.get(position, false);
    }

    public void setChecked(int position, boolean isChecked) {
        mCheckStates.put(position, isChecked);
        notifyDataSetChanged();
    }

    public void toggle(int position) {
        setChecked(position, !isChecked(position));
    }

    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mCheckStates.put((Integer) buttonView.getTag(), isChecked);
    }
}
