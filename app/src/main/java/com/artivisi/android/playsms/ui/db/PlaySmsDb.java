package com.artivisi.android.playsms.ui.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.artivisi.android.playsms.domain.Contact;
import com.artivisi.android.playsms.domain.Message;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by opaw on 2/12/15.
 */
public class PlaySmsDb extends SQLiteOpenHelper {

    private static final String DB_NAME = "playsms.db";

    private static final int DB_VERSION_NUMBER = 1;
    private static final String DB_TABLE_INBOX = "inbox";
    private static final String DB_TABLE_SENT = "sent";
    private static final String DB_TABLE_USER = "user";
    private static final String DB_TABLE_CONTACT = "contact";
    private static final String DB_TABLE_SERVER = "server";
    private static final String DB_COLUMN_SMSLOG = "smslog_id";
    private static final String DB_COLUMN_ID = "id";
    private static final String DB_COLUMN_SRC = "src";
    private static final String DB_COLUMN_DST = "dst";
    private static final String DB_COLUMN_MSG = "msg";
    private static final String DB_COLUMN_MSG_DELETED = "deleted";
    private static final String DB_COLUMN_DT = "dt";
    private static final String DB_COLUMN_UPDATE = "updt";
    private static final String DB_COLUMN_STATUS = "status";
    private static final String DB_COLUMN_READ = "read";
    private static final String DB_COLUMN_USERNAME = "username";
    private static final String DB_COLUMN_UID = "uid";
    private static final String DB_COLUMN_USER_STATUS = "status";
    private static final String DB_COLUMN_NAME = "name";
    private static final String DB_COLUMN_EMAIL = "email";
    private static final String DB_COLUMN_CREDIT = "credit";
    private static final String DB_COLUMN_PID = "pid";
    private static final String DB_COLUMN_P_DESC = "p_desc";
    private static final String DB_COLUMN_P_NUM = "p_num";
    private static final String DB_COLUMN_URL = "url";

    private SQLiteDatabase sqliteDBInstance = null;

    private static final String DB_CREATE_TABLE_INBOX_SCRIPT = "create table if not exists " + DB_TABLE_INBOX
            + "(" + DB_COLUMN_ID + " integer,"
            + DB_COLUMN_SRC + " text,"
            + DB_COLUMN_DST + " text,"
            + DB_COLUMN_MSG + " text,"
            + DB_COLUMN_DT + " text,"
            + DB_COLUMN_MSG_DELETED + " integer,"
            + DB_COLUMN_READ + " int)";

    private static final String DB_CREATE_TABLE_SENT_SCRIPT = "create table if not exists " + DB_TABLE_SENT
            + "(" + DB_COLUMN_SMSLOG + " integer,"
            + DB_COLUMN_SRC + " text,"
            + DB_COLUMN_DST + " text,"
            + DB_COLUMN_MSG + " text,"
            + DB_COLUMN_DT + " text,"
            + DB_COLUMN_UPDATE + " text,"
            + DB_COLUMN_STATUS + " text,"
            + DB_COLUMN_MSG_DELETED + " integer)";

    private static final String DB_CREATE_TABLE_USER_SCRIPT = "crete table if not exists " + DB_TABLE_USER
            + "(" + DB_COLUMN_USERNAME + " text,"
            + DB_COLUMN_UID + " text,"
            + DB_COLUMN_USER_STATUS + " text,"
            + DB_COLUMN_NAME + " text,"
            + DB_COLUMN_EMAIL + " text,"
            + DB_COLUMN_CREDIT + " text)";

    private static final String DB_CREATE_TABLE_CONTACT_SCRIPT = "create table if not exists " + DB_TABLE_CONTACT
            + "(" + DB_COLUMN_PID + " text,"
            + DB_COLUMN_P_DESC + " text,"
            + DB_COLUMN_P_NUM + " text,"
            + DB_COLUMN_EMAIL + " text)";

    private static final String DB_CREATE_TABLE_SERVER_SCRIPT = "create table if not exists " + DB_TABLE_SERVER
            + "(" + DB_COLUMN_URL + " text)";

    public PlaySmsDb(Context context) {
        super(context, DB_NAME, null, DB_VERSION_NUMBER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DB_CREATE_TABLE_INBOX_SCRIPT);
        db.execSQL(DB_CREATE_TABLE_SENT_SCRIPT);
        db.execSQL(DB_CREATE_TABLE_CONTACT_SCRIPT);
        db.execSQL(DB_CREATE_TABLE_SERVER_SCRIPT);
//        db.execSQL(DB_CREATE_TABLE_USER_SCRIPT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
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
        contentValues.put(DB_COLUMN_READ, 1);
        this.sqliteDBInstance.insertWithOnConflict(DB_TABLE_INBOX, null, contentValues,SQLiteDatabase.CONFLICT_REPLACE);
        sqliteDBInstance.close();
    }

    public void insertNewInbox(Message message){
        sqliteDBInstance = getWritableDatabase();
        int id = Integer.parseInt(message.getId());
        ContentValues contentValues = new ContentValues();
        contentValues.put(DB_COLUMN_ID, id);
        contentValues.put(DB_COLUMN_SRC, message.getSrc());
        contentValues.put(DB_COLUMN_DST, message.getDst());
        contentValues.put(DB_COLUMN_MSG, message.getMsg());
        contentValues.put(DB_COLUMN_DT, message.getDt());
        contentValues.put(DB_COLUMN_MSG_DELETED, 0);
        contentValues.put(DB_COLUMN_READ, 0);
        this.sqliteDBInstance.insertWithOnConflict(DB_TABLE_INBOX, null, contentValues,SQLiteDatabase.CONFLICT_REPLACE);
        sqliteDBInstance.close();
    }

    public void deleteInboxLocally(String id){
        sqliteDBInstance = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DB_COLUMN_MSG_DELETED, 1);
        this.sqliteDBInstance.update(DB_TABLE_INBOX, contentValues, "id=?", new String[]{id});
        sqliteDBInstance.close();
    }

    public void readInbox(){
        sqliteDBInstance = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DB_COLUMN_READ, 1);
        this.sqliteDBInstance.update(DB_TABLE_INBOX, contentValues, null, null);
        sqliteDBInstance.close();
    }

    public void updateInbox(Message message){
        sqliteDBInstance = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DB_COLUMN_SRC, message.getSrc());
        contentValues.put(DB_COLUMN_DST, message.getDst());
        contentValues.put(DB_COLUMN_MSG, message.getMsg());
        contentValues.put(DB_COLUMN_DT, message.getDt());
        contentValues.put(DB_COLUMN_READ, 1);
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
        Cursor cursor = this.sqliteDBInstance.query(DB_TABLE_INBOX, new String[]{DB_COLUMN_ID, DB_COLUMN_SRC, DB_COLUMN_DST, DB_COLUMN_MSG, DB_COLUMN_DT, DB_COLUMN_MSG_DELETED, DB_COLUMN_READ}, null, null, null, null, DB_COLUMN_ID + " DESC");
        List<Message> listInbox = new ArrayList<Message>();
        if(cursor.getCount() > 0){
            while (cursor.moveToNext()){
                if(cursor.getInt(cursor.getColumnIndex(DB_COLUMN_MSG_DELETED)) == 0){
                    Message message = new Message();
                    message.setId(cursor.getString(cursor.getColumnIndex(DB_COLUMN_ID)));
                    message.setSrc(cursor.getString(cursor.getColumnIndex(DB_COLUMN_SRC)));
                    message.setDst(cursor.getString(cursor.getColumnIndex(DB_COLUMN_DST)));
                    message.setMsg(cursor.getString(cursor.getColumnIndex(DB_COLUMN_MSG)));
                    message.setDt(timeParser(cursor.getString(cursor.getColumnIndex(DB_COLUMN_DT))));
                    Boolean read = (cursor.getInt(cursor.getColumnIndex(DB_COLUMN_READ)) == 1 ? true : false);
                    message.setRead(read);
                    listInbox.add(message);
                }
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
        sqliteDBInstance = getWritableDatabase();
        int smslog = Integer.parseInt(message.getSmslogId());
        ContentValues contentValues = new ContentValues();
        contentValues.put(DB_COLUMN_SMSLOG, smslog);
        contentValues.put(DB_COLUMN_SRC, message.getSrc());
        contentValues.put(DB_COLUMN_DST, message.getDst());
        contentValues.put(DB_COLUMN_MSG, message.getMsg());
        contentValues.put(DB_COLUMN_DT, message.getDt());
        contentValues.put(DB_COLUMN_UPDATE, message.getUpdate());
        contentValues.put(DB_COLUMN_STATUS, message.getStatus());
        contentValues.put(DB_COLUMN_MSG_DELETED, 0);
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

    public void deleteSentLocally(String id){
        sqliteDBInstance = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DB_COLUMN_MSG_DELETED, 1);
        this.sqliteDBInstance.update(DB_TABLE_SENT, contentValues, "smslog_id=?", new String[]{id});
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
        Cursor cursor = this.sqliteDBInstance.query(DB_TABLE_SENT, new String[]{DB_COLUMN_SMSLOG, DB_COLUMN_SRC, DB_COLUMN_DST, DB_COLUMN_MSG, DB_COLUMN_DT, DB_COLUMN_UPDATE, DB_COLUMN_STATUS, DB_COLUMN_MSG_DELETED}, null, null, null, null, DB_COLUMN_SMSLOG + " DESC");
        List<Message> listInbox = new ArrayList<Message>();
        if(cursor.getCount() > 0){
            while (cursor.moveToNext()){
                Log.i("deleted : ", cursor.getString(cursor.getColumnIndex(DB_COLUMN_MSG_DELETED)));
                if(cursor.getInt(cursor.getColumnIndex(DB_COLUMN_MSG_DELETED)) == 0) {
                    Message message = new Message();
                    Log.i("ga delet : ", cursor.getString(cursor.getColumnIndex(DB_COLUMN_MSG)));
                    message.setSmslogId(cursor.getString(cursor.getColumnIndex(DB_COLUMN_SMSLOG)));
                    message.setSrc(cursor.getString(cursor.getColumnIndex(DB_COLUMN_SRC)));
                    message.setDst(cursor.getString(cursor.getColumnIndex(DB_COLUMN_DST)));
                    message.setMsg(cursor.getString(cursor.getColumnIndex(DB_COLUMN_MSG)));
                    message.setDt(timeParser(cursor.getString(cursor.getColumnIndex(DB_COLUMN_DT))));
                    message.setUpdate(cursor.getString(cursor.getColumnIndex(DB_COLUMN_UPDATE)));
                    message.setStatus(cursor.getString(cursor.getColumnIndex(DB_COLUMN_STATUS)));
                    listInbox.add(message);
                } else {
                    Log.i("ga delet : ", cursor.getString(cursor.getColumnIndex(DB_COLUMN_MSG)));
                }
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

    public void insertContact(Contact contact){
        sqliteDBInstance = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DB_COLUMN_PID, contact.getPid());
        contentValues.put(DB_COLUMN_P_DESC, contact.getpDesc());
        contentValues.put(DB_COLUMN_P_NUM, contact.getpNum());
        contentValues.put(DB_COLUMN_EMAIL, contact.getEmail());
        this.sqliteDBInstance.insertWithOnConflict(DB_TABLE_CONTACT, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        sqliteDBInstance.close();
    }

    public void updateContact(Contact contact){
        sqliteDBInstance = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DB_COLUMN_PID, contact.getPid());
        contentValues.put(DB_COLUMN_P_DESC, contact.getpDesc());
        contentValues.put(DB_COLUMN_P_NUM, contact.getpNum());
        contentValues.put(DB_COLUMN_EMAIL, contact.getEmail());
        this.sqliteDBInstance.update(DB_TABLE_CONTACT, contentValues, DB_COLUMN_PID + "=?", new String[]{contact.getPid()});
        sqliteDBInstance.close();
    }

    public void deleteContact(String id){
        sqliteDBInstance = getWritableDatabase();
        this.sqliteDBInstance.delete(DB_TABLE_CONTACT, DB_COLUMN_PID + "=?", new String[]{id});
        sqliteDBInstance.close();
    }

    public void truncateContact(){
        sqliteDBInstance = getWritableDatabase();
        sqliteDBInstance.execSQL("delete from " + DB_TABLE_CONTACT);
        sqliteDBInstance.close();
    }

    public List<Contact> getAllContact(){
        sqliteDBInstance = getWritableDatabase();
        Cursor cursor = this.sqliteDBInstance.query(DB_TABLE_CONTACT, new String[]{DB_COLUMN_PID, DB_COLUMN_P_DESC, DB_COLUMN_P_NUM, DB_COLUMN_EMAIL}, null, null, null, null, null);
        List<Contact> listContact = new ArrayList<Contact>();
        if(cursor.getCount() > 0){
            while (cursor.moveToNext()){
                Contact contact = new Contact();
                contact.setPid(cursor.getString(cursor.getColumnIndex(DB_COLUMN_PID)));
                contact.setpDesc(cursor.getString(cursor.getColumnIndex(DB_COLUMN_P_DESC)));
                contact.setpNum(cursor.getString(cursor.getColumnIndex(DB_COLUMN_P_NUM)));
                contact.setEmail(cursor.getString(cursor.getColumnIndex(DB_COLUMN_EMAIL)));
                listContact.add(contact);
            }

            sqliteDBInstance.close();
            return  listContact;
        } else {
            sqliteDBInstance.close();
            return new ArrayList<Contact>();
        }
    }

    public String timeParser(String date){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date formattedDate;
        try {
            formattedDate = formatter.parse(date);
            formatter.setTimeZone(TimeZone.getDefault());
            return formatter.format(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return date;
        }
    }

    public void insertServer(String server) {
        if(getLastServer().equals("")){
            sqliteDBInstance = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(DB_COLUMN_URL, server);
            sqliteDBInstance.insertWithOnConflict(DB_TABLE_SERVER, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
            sqliteDBInstance.close();
        } else {
            sqliteDBInstance = getWritableDatabase();
            sqliteDBInstance.execSQL("delete from " + DB_TABLE_SERVER);
            ContentValues contentValues = new ContentValues();
            contentValues.put(DB_COLUMN_URL, server);
            sqliteDBInstance.insertWithOnConflict(DB_TABLE_SERVER, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
            sqliteDBInstance.close();
        }
    }

    public String getLastServer(){
        sqliteDBInstance = getWritableDatabase();
        String server = "";
        try{
            Cursor cursor = this.sqliteDBInstance.query(DB_TABLE_SERVER, new String[]{DB_COLUMN_URL}, null, null, null, null, null, "1");
            if(cursor.getCount() > 0){
                while (cursor.moveToNext()){
                    server = cursor.getString(cursor.getColumnIndex(DB_COLUMN_URL));
                }
                sqliteDBInstance.close();
                return server;
            } else {
                sqliteDBInstance.close();
                return server;
            }
        } catch (Exception ex){
            sqliteDBInstance.execSQL(DB_CREATE_TABLE_SERVER_SCRIPT);
            return server;
        }
    }
}
