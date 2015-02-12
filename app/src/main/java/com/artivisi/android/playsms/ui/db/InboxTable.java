package com.artivisi.android.playsms.ui.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.artivisi.android.playsms.domain.Message;

/**
 * Created by opaw on 2/12/15.
 */
public class InboxTable extends SQLiteOpenHelper {

    private static final String DB_NAME = "playsms.db";
    private static final String dateFormat = "yyyy-MM-dd hh:mm:ss";

    private static final int DB_VERSION_NUMBER = 1;
    private static final String DB_TABLE_NAME = "inbox";
    private static final String DB_COLUMN_NAME_1 = "id";
    private static final String DB_COLUMN_NAME_2 = "src";
    private static final String DB_COLUMN_NAME_3 = "dst";
    private static final String DB_COLUMN_NAME_4 = "msg";
    private static final String DB_COLUMN_NAME_5 = "dt";

    private SQLiteDatabase sqliteDBInstance = null;

    private static final String DB_CREATE_SCRIPT = "create table " + DB_TABLE_NAME +
            "(" + DB_COLUMN_NAME_1 +" text PRIMARY KEY,"
            + DB_COLUMN_NAME_2 +" text,"
            + DB_COLUMN_NAME_3 +" text,"
            + DB_COLUMN_NAME_4 +" text,"
            + DB_COLUMN_NAME_5 +" text";

    public InboxTable(Context context) {
        super(context, DB_NAME, null, DB_VERSION_NUMBER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DB_CREATE_SCRIPT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(Message message){
        sqliteDBInstance = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DB_COLUMN_NAME_1, message.getSmslogId());
        contentValues.put(DB_COLUMN_NAME_2, message.getSrc());
        contentValues.put(DB_COLUMN_NAME_3, message.getDst());
        contentValues.put(DB_COLUMN_NAME_4, message.getMsg());
        contentValues.put(DB_COLUMN_NAME_5, message.getDt());
        this.sqliteDBInstance.insertWithOnConflict(DB_TABLE_NAME, null, contentValues,SQLiteDatabase.CONFLICT_REPLACE);
        sqliteDBInstance.close();
    }

    public void update(Message message){
        sqliteDBInstance = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DB_COLUMN_NAME_2, message.getSrc());
        contentValues.put(DB_COLUMN_NAME_3, message.getDst());
        contentValues.put(DB_COLUMN_NAME_4, message.getMsg());
        contentValues.put(DB_COLUMN_NAME_5, message.getDt());
        this.sqliteDBInstance.update(DB_TABLE_NAME, contentValues, "id=?", new String[]{String.valueOf(message.getId())});
        sqliteDBInstance.close();
    }

    public void delete(String id){
        sqliteDBInstance = getWritableDatabase();
        this.sqliteDBInstance.delete(DB_TABLE_NAME, "id=?", new String[]{String.valueOf(id)});
    }

    public void truncate(){
        this.sqliteDBInstance.execSQL("delete from " + DB_TABLE_NAME);
    }
}
