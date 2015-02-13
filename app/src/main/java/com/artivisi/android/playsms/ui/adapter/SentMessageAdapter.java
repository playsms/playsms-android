package com.artivisi.android.playsms.ui.adapter;

import android.content.Context;
import android.graphics.Typeface;
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
 * Created by opaw on 2/9/15.
 */
public class SentMessageAdapter extends BaseAdapter {

    private Context context;
    private List<Message> listMessages;
    private PlaySmsDb db;

    public SentMessageAdapter(Context context){
        this.context = context;
        this.db = new PlaySmsDb(context);
        this.listMessages = db.getAllSent();
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
            convertView = LayoutInflater.from(context).inflate(R.layout.list_sent_message, parent, false);
        }

        Typeface ralewayRegular = Typeface.createFromAsset(context.getAssets(), "fonts/Raleway-Heavy.ttf");

        TextView sentTo = (TextView) convertView.findViewById(R.id.txt_sent_to);
        sentTo.setText(message.getDst());
//        sentTo.setTypeface(ralewayRegular);

        TextView sentMsg = (TextView) convertView.findViewById(R.id.txt_sent_msg);
        sentMsg.setText(message.getMsg());
//        sentMsg.setTypeface(ralewayRegular);

        TextView sentDate = (TextView) convertView.findViewById(R.id.txt_msg_date);
        sentDate.setText(message.getDt());

        return convertView;
    }

    public void updateList(){
        listMessages = db.getAllSent();
        notifyDataSetChanged();
    }
}
