package com.artivisi.android.playsms.ui.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.artivisi.android.playsms.R;
import com.artivisi.android.playsms.domain.Contact;
import com.artivisi.android.playsms.ui.db.PlaySmsDb;

import java.util.List;

/**
 * Created by opaw on 3/4/15.
 */
public class ContactAdapter extends BaseAdapter {
    private Context context;
    private List<Contact> listContact;
    private PlaySmsDb db;

    public ContactAdapter (Context context){
        this.context = context;
        this.db = new PlaySmsDb(context);
        this.listContact = db.getAllContact();
    }

    @Override
    public int getCount() {
        return listContact.size();
    }

    @Override
    public Contact getItem(int position) {
        return listContact.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Contact contact = getItem(position);

        Typeface robotoLight = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf");

        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.list_contact, parent, false);
        }

        TextView name = (TextView) convertView.findViewById(R.id.txt_name);
        name.setTypeface(robotoLight);
        name.setText(contact.getpDesc());

        TextView number = (TextView) convertView.findViewById(R.id.txt_number);
        number.setTypeface(robotoLight);
        number.setText(contact.getpNum());

        CheckBox checked = (CheckBox) convertView.findViewById(R.id.checkbox_contact);
        checked.setClickable(false);

        if (contact.isSelected()) {
            checked.setChecked(true);
        } else {
            checked.setChecked(false);
        }

        return convertView;
    }

    public void updateList(){
        listContact = db.getAllContact();
        notifyDataSetChanged();
    }
}
