package com.artivisi.android.playsms.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.artivisi.android.playsms.R;
import com.artivisi.android.playsms.domain.Message;
import com.artivisi.android.playsms.ui.db.PlaySmsDb;

import java.util.List;

/**
 * Created by opaw on 2/10/15.
 */
public class InboxAdapter extends BaseAdapter{

    private Context context;
    private List<Message> listMessages;
    private PlaySmsDb db;

    public InboxAdapter (Context context){
        this.context = context;
        this.db = new PlaySmsDb(context);
        this.listMessages = db.getAllInbox();
    }



    @Override
    public int getCount() {
        return listMessages.size();
    }

    @Override
    public Message getItem(int position) {
        return listMessages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message message = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.list_inbox, parent, false);
        }

        TextView inboxFrom = (TextView) convertView.findViewById(R.id.txt_inbox_from);
        inboxFrom.setText(message.getSrc());

        TextView inboxMsg = (TextView) convertView.findViewById(R.id.txt_inbox_msg);
        inboxMsg.setText(message.getMsg());

        TextView inboxDate = (TextView) convertView.findViewById(R.id.txt_inbox_date);
        inboxDate.setText(message.getDt());

        return convertView;
    }

    public void updateList(){
        listMessages = db.getAllInbox();
        notifyDataSetChanged();
    }
}
