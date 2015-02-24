package com.artivisi.android.playsms.ui.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import com.artivisi.android.playsms.R;
import com.artivisi.android.playsms.domain.Contact;

import java.util.List;

/**
 * Created by opaw on 2/9/15.
 */
public class ContactsAdapter extends BaseAdapter {

    public final String  TAG="ContactsAdapter";
    private Context context;
    private List<Contact> list;
//    private PlaySmsDb db;

    public ContactsAdapter(Context context, List<Contact> list){
        this.context = context;
        this.list = list;
    }

    public void setdata(List<Contact> list) {
        this.list=list;

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Contact getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Contact contact = getItem(position);

//        Typeface robotoMedium = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Medium.ttf");
//        Typeface robotoRegular = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf");
        Typeface robotoLight = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf");

        if(convertView == null){
            Log.d(TAG, "ContactsAdapter LayoutInflater");
            convertView = LayoutInflater.from(context).inflate(R.layout.list_contact, parent, false);
        }

//        TextView tvemail = (TextView) convertView.findViewById(R.id.txt_contact_email);
//        tvemail.setText(contact.getEmail());
//        tvemail.setVisibility(View.GONE);
//        tvemail.setTypeface(robotoLight);

        TextView tvname = (TextView) convertView.findViewById(R.id.txt_contact_name);
        tvname.setText(contact.getP_desc());
        //tvname.setTypeface(robotoLight);

        TextView tvphone = (TextView) convertView.findViewById(R.id.txt_contact_phone);
        tvphone.setText(contact.getP_num());
        //tvphone.setTypeface(robotoLight);

        CheckBox checked = (CheckBox) convertView.findViewById(R.id.selectedcheckbox);
        checked.setClickable(false);
        //checked.setVisibility(View.VISIBLE);
        if (contact.isSelected()) {
            //checked.setVisibility(View.VISIBLE);
            checked.setChecked(true          );
        } else {
            checked.setChecked(false          );
            //checked.setVisibility(View.GONE);
        }
        return convertView;
    }

    public void updateList(){
        //listMessages = db.getAllSent();
        notifyDataSetChanged();
    }
}
