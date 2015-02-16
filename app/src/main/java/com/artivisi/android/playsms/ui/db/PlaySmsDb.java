package com.artivisi.android.playsms.ui.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.artivisi.android.playsms.domain.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by opaw on 2/12/15.
 */
public class PlaySmsDb extends SQLiteOpenHelper {

    private static final String DB_NAME = "playsms.db";

    private static final int DB_VERSION_NUMBER = 1;
    private static final String DB_TABLE_INBOX = "inbox";
    private static final String DB_TABLE_SENT = "sent";
    private static final String DB_COLUMN_SMSLOG = "smslog_id";
    private static final String DB_COLUMN_ID = "id";
    private static final String DB_COLUMN_SRC = "src";
    private static final String DB_COLUMN_DST = "dst";
    private static final String DB_COLUMN_MSG = "msg";
    private static final String DB_COLUMN_DT = "dt";
    private static final String DB_COLUMN_UPDATE = "updt";
    private static final String DB_COLUMN_STATUS = "status";

    private SQLiteDatabase sqliteDBInstance = null;

    private static final String DB_CREATE_TABLE_INBOX_SCRIPT = "create table " + DB_TABLE_INBOX +
            "(" + DB_COLUMN_ID +" integer,"
            + DB_COLUMN_SRC +" text,"
            + DB_COLUMN_DST +" text,"
            + DB_COLUMN_MSG +" text,"
            + DB_COLUMN_DT +" text)";

    private static final String DB_CREATE_TABLE_SENT_SCRIPT = "create table " + DB_TABLE_SENT +
            "(" + DB_COLUMN_SMSLOG +" integer,"
            + DB_COLUMN_SRC +" text,"
            + DB_COLUMN_DST +" text,"
            + DB_COLUMN_MSG +" text,"
            + DB_COLUMN_DT +" text,"
            + DB_COLUMN_UPDATE +" text,"
            + DB_COLUMN_STATUS +" text)";

    public PlaySmsDb(Context context) {
        super(context, DB_NAME, null, DB_VERSION_NUMBER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DB_CREATE_TABLE_INBOX_SCRIPT);
        db.execSQL(DB_CREATE_TABLE_SENT_SCRIPT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertInbox(Message message){
        int id = Integer.parseInt(message.getId());
        sqliteDBInstance = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DB_COLUMN_ID, id);
        contentValues.put(DB_COLUMN_SRC, message.getSrc());
        contentValues.put(DB_COLUMN_DST, message.getDst());
        contentValues.put(DB_COLUMN_MSG, message.getMsg());
        contentValues.put(DB_COLUMN_DT, message.getDt());
        this.sqliteDBInstance.insertWithOnConflict(DB_TABLE_INBOX, null, contentValues,SQLiteDatabase.CONFLICT_REPLACE);
        sqliteDBInstance.close();
    }

    public void updateInbox(Message message){
        sqliteDBInstance = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DB_COLUMN_SRC, message.getSrc());
        contentValues.put(DB_COLUMN_DST, message.getDst());
        contentValues.put(DB_COLUMN_MSG, message.getMsg());
        contentValues.put(DB_COLUMN_DT, message.getDt());
        this.sqliteDBInstance.update(DB_TABLE_INBOX, contentValues, "id=?", new String[]{String.valueOf(message.getId())});
        sqliteDBInstance.close();
    }

    public void deleteInbox(String id){
        sqliteDBInstance = getWritableDatabase();
        sqliteDBInstance.delete(DB_TABLE_INBOX, "id=?", new String[]{id});
        sqliteDBInstance.close();
    }

    public void truncateInbox(){
        sqliteDBInstance = getWritableDatabase();
        sqliteDBInstance.execSQL("delete from " + DB_TABLE_INBOX);
        sqliteDBInstance.close();
    }

    public List<Message> getAllInbox(){
        this.sqliteDBInstance = getWritableDatabase();
        Cursor cursor = this.sqliteDBInstance.query(DB_TABLE_INBOX, new String[]{DB_COLUMN_ID, DB_COLUMN_SRC, DB_COLUMN_DST, DB_COLUMN_MSG, DB_COLUMN_DT}, null, null, null, null, DB_COLUMN_ID + " DESC");
        List<Message> listInbox = new ArrayList<Message>();
        if(cursor.getCount() > 0){
            while (cursor.moveToNext()){
                Message message = new Message();
                message.setId(cursor.getString(cursor.getColumnIndex(DB_COLUMN_ID)));
                message.setSrc(cursor.getString(cursor.getColumnIndex(DB_COLUMN_SRC)));
                message.setDst(cursor.getString(cursor.getColumnIndex(DB_COLUMN_DST)));
                message.setMsg(cursor.getString(cursor.getColumnIndex(DB_COLUMN_MSG)));
                message.setDt(cursor.getString(cursor.getColumnIndex(DB_COLUMN_DT)));
                listInbox.add(message);
            }

            this.sqliteDBInstance.close();
            return listInbox;
        } else {
            this.sqliteDBInstance.close();
            return new ArrayList<Message>();
        }
    }

    public String getLastInbox(){
        sqliteDBInstance = getWritableDatabase();
        Cursor cursor = this.sqliteDBInstance.query(DB_TABLE_INBOX, new String[]{DB_COLUMN_ID}, null, null, null, null, null);
        String id = null;
        int counter = 0;
        if(cursor.getCount() > 0){
            while (cursor.moveToNext()){
                int currentId = Integer.parseInt(cursor.getString(cursor.getColumnIndex(DB_COLUMN_ID)));
                if(currentId > counter){
                    counter = currentId;
                    id = cursor.getString(cursor.getColumnIndex(DB_COLUMN_ID));
                }
            }
            sqliteDBInstance.close();
            return id;
        } else {
            sqliteDBInstance.close();
            return null;
        }
    }

    public void insertSent(Message message){
        int smslog = Integer.parseInt(message.getSmslogId());
        sqliteDBInstance = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DB_COLUMN_SMSLOG, smslog);
        contentValues.put(DB_COLUMN_SRC, message.getSrc());
        contentValues.put(DB_COLUMN_DST, message.getDst());
        contentValues.put(DB_COLUMN_MSG, message.getMsg());
        contentValues.put(DB_COLUMN_DT, message.getDt());
        contentValues.put(DB_COLUMN_UPDATE, message.getUpdate());
        contentValues.put(DB_COLUMN_STATUS, message.getStatus());
        this.sqliteDBInstance.insertWithOnConflict(DB_TABLE_SENT, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        sqliteDBInstance.close();
    }

    public void updateSent(Message message){
        sqliteDBInstance = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DB_COLUMN_SRC, message.getSrc());
        contentValues.put(DB_COLUMN_DST, message.getDst());
        contentValues.put(DB_COLUMN_MSG, message.getMsg());
        contentValues.put(DB_COLUMN_DT, message.getDt());
        contentValues.put(DB_COLUMN_UPDATE, message.getUpdate());
        contentValues.put(DB_COLUMN_STATUS, message.getStatus());
        this.sqliteDBInstance.update(DB_TABLE_SENT, contentValues, "smslog_id=?", new String[]{String.valueOf(message.getSmslogId())});
        sqliteDBInstance.close();
    }

    public void deleteSent(String id){
        sqliteDBInstance = getWritableDatabase();
        this.sqliteDBInstance.delete(DB_TABLE_SENT, "smslog_id=?", new String[]{String.valueOf(id)});
        sqliteDBInstance.close();
    }

    public void truncateSent(){
        sqliteDBInstance = getWritableDatabase();
        sqliteDBInstance.execSQL("delete from " + DB_TABLE_SENT);
        sqliteDBInstance.close();
    }

    public List<Message> getAllSent(){
        sqliteDBInstance = getWritableDatabase();
        Cursor cursor = this.sqliteDBInstance.query(DB_TABLE_SENT, new String[]{DB_COLUMN_SMSLOG, DB_COLUMN_SRC, DB_COLUMN_DST, DB_COLUMN_MSG, DB_COLUMN_DT, DB_COLUMN_UPDATE, DB_COLUMN_STATUS}, null, null, null, null, DB_COLUMN_SMSLOG + " DESC");
        List<Message> listInbox = new ArrayList<Message>();
        if(cursor.getCount() > 0){
            while (cursor.moveToNext()){
                Message message = new Message();
                message.setSmslogId(cursor.getString(cursor.getColumnIndex(DB_COLUMN_SMSLOG)));
                message.setSrc(cursor.getString(cursor.getColumnIndex(DB_COLUMN_SRC)));
                message.setDst(cursor.getString(cursor.getColumnIndex(DB_COLUMN_DST)));
                message.setMsg(cursor.getString(cursor.getColumnIndex(DB_COLUMN_MSG)));
                message.setDt(cursor.getString(cursor.getColumnIndex(DB_COLUMN_DT)));
                message.setUpdate(cursor.getString(cursor.getColumnIndex(DB_COLUMN_UPDATE)));
                message.setStatus(cursor.getString(cursor.getColumnIndex(DB_COLUMN_STATUS)));
                listInbox.add(message);
            }

            sqliteDBInstance.close();
            return  listInbox;
        } else {
            sqliteDBInstance.close();
            return new ArrayList<Message>();
        }
    }

    public String getLastSent(){
        sqliteDBInstance = getWritableDatabase();
        Cursor cursor = this.sqliteDBInstance.query(DB_TABLE_SENT, new String[]{DB_COLUMN_SMSLOG}, null, null, null, null, null);
        String smslogId = null;
        int counter = 0;
        if(cursor.getCount() > 0){
            while (cursor.moveToNext()){
                int currentId = Integer.parseInt(cursor.getString(cursor.getColumnIndex(DB_COLUMN_SMSLOG)));
                if(currentId > counter){
                    counter = currentId;
                    smslogId = cursor.getString(cursor.getColumnIndex(DB_COLUMN_SMSLOG));
                }
            }
            sqliteDBInstance.close();
            return smslogId;
        } else {
            sqliteDBInstance.close();
            return null;
        }
    }
}
